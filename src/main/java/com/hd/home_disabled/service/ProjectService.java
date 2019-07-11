package com.hd.home_disabled.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.Admin;
import com.hd.home_disabled.entity.Organization;
import com.hd.home_disabled.entity.Project;
import com.hd.home_disabled.entity.dictionary.ProjectType;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.repository.AdminRepository;
import com.hd.home_disabled.repository.OrganizationRepository;
import com.hd.home_disabled.repository.ProjectRepository;
import com.hd.home_disabled.repository.ProjectTypeRepository;
import com.hd.home_disabled.utils.ExcelUtils;
import com.hd.home_disabled.utils.PageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName ProjectService
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/5 11:54
 * @Version
 */
@Service
@Transactional
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectTypeRepository projectTypeRepository;
    private final OrganizationRepository organizationRepository;
    private final AdminRepository adminRepository;

    public ProjectService(ProjectRepository projectRepository, ProjectTypeRepository projectTypeRepository, OrganizationRepository organizationRepository, AdminRepository adminRepository) {
        this.projectRepository = projectRepository;
        this.projectTypeRepository = projectTypeRepository;
        this.organizationRepository = organizationRepository;
        this.adminRepository = adminRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    //model-->entity
    Project getEntity(com.hd.home_disabled.model.dto.Project project) {
        Project p = new Project();
        if (project.getId() != null) p.setId(project.getId());
        Organization organization = new Organization();
        organization.setId(project.getOrganizationId());
        p.setOrganization(organization);
        ProjectType projectType = new ProjectType();
        projectType.setId(project.getProjectTypeId());
        p.setProjectType(projectType);
        p.setName(project.getName());
        p.setLeader(project.getLeader());
        p.setDescription(project.getDescription());
        p.setImage(project.getImage());
        p.setStartTime(project.getStartTime());
        p.setEndTime(project.getEndTime());
        Admin admin = new Admin();
        admin.setId(project.getAdminId());
        p.setAdmin(admin);
        return p;
    }

    //entity-->model
    com.hd.home_disabled.model.dto.Project getModel(Project project) {
        com.hd.home_disabled.model.dto.Project p = new com.hd.home_disabled.model.dto.Project();
        p.setId(project.getId());
        p.setAdminName(project.getAdmin().getName());
        p.setOrganizationName(project.getOrganization().getName());
        p.setName(project.getName());
        p.setProjectTypeId(project.getProjectType().getId());
        p.setLeader(project.getLeader());
        p.setDescription(project.getDescription());
        p.setImage(project.getImage());
        p.setStartTime(project.getStartTime());
        p.setEndTime(project.getEndTime());

        p.setAdminName(project.getAdmin().getName());
        p.setCreateTime(project.getCreateTime());
        p.setLastModifyTime(project.getModifyTime());
        return p;
    }

    public JSONObject saveAndFlush(com.hd.home_disabled.model.dto.Project project) {
        Project project1 = getEntity(project);
        if (project1.getOrganization() != null &&
                project1.getOrganization().getId() != null &&
                organizationRepository.findByIdAndStatus(project1.getOrganization().getId(), 1).isPresent()) {
            Organization organization = organizationRepository.getOne(project.getOrganizationId());
            if (project1.getAdmin() != null &&
                    project1.getAdmin().getId() != null &&
                    adminRepository.existsById(project1.getAdmin().getId())) {
                if (project1.getProjectType() != null &&
                        project1.getProjectType().getId() != null &&
                        projectTypeRepository.existsById(project1.getProjectType().getId())) {
                    if (project.getId() == null) {//新建
                        if (organization.getProjectSum() != null) {
                            organization.setProjectSum(organization.getProjectSum() + 1);
                        } else {
                            organization.setProjectSum(1);
                        }
                    }
                    project1.setStatus(1);
                    Project project2 = projectRepository.saveAndFlush(project1);
                    return RESCODE.SUCCESS.getJSONRES(getModel(project2));
                }
                return RESCODE.PROJECTTYPE_NOT_EXIST.getJSONRES();
            }
            return RESCODE.ADMIN_ID_NOT_EXIST.getJSONRES();
        }
        return RESCODE.ORGANIZATION_ID_NOT_EXIST.getJSONRES();
    }


    public JSONObject delete(Integer id) {
        Optional<Project> projectOptional = projectRepository.findByIdAndStatus(id, 1);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            Optional<Organization> organizationOptional = organizationRepository.findByIdAndStatus(project.getOrganization().getId(), 1);
            if (organizationOptional.isPresent()) {
                Organization organization = organizationOptional.get();
                if (organization.getProjectSum() != null && organization.getProjectSum() > 0) {
                    organization.setProjectSum(organization.getProjectSum() - 1);
                    organizationRepository.saveAndFlush(organization);
                }
            }
            project.setStatus(0);
            return RESCODE.SUCCESS.getJSONRES();
        }
        return RESCODE.PROJECT_ID_NOT_EXIST.getJSONRES();
    }

    public JSONObject getById(Integer id) {
        Optional<Project> projectOptional = projectRepository.findByIdAndStatus(id, 1);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            return RESCODE.SUCCESS.getJSONRES(getModel(project));
        }
        return RESCODE.PROJECT_ID_NOT_EXIST.getJSONRES();
    }

    public JSONObject getPageByOrganizationId(Integer organizationId, Integer page, Integer number, String sorts) {
        Pageable pageable = PageUtils.getPage(page, number, sorts);
        Page<Project> projectPage = projectRepository.findByOrganizationAndStatus(organizationId, 1, pageable);
        List<com.hd.home_disabled.model.dto.Project> projectList = new ArrayList<>();
        for (Project project :
                projectPage) {
            projectList.add(getModel(project));
        }
        return RESCODE.SUCCESS.getJSONRES(projectList, projectPage.getTotalPages(), projectPage.getTotalElements());
    }

    //全区服务项目列表
    public List<JSONArray> getListsByDistrict(String district) {
        List<JSONArray> jsonArray = new ArrayList<>();
        List<Organization> organizationList = organizationRepository.findByDistrictAndStatus(district, 1);
        for (Organization organization : organizationList) {
            List<Project> projectList = projectRepository.findByOrganizationAndStatus(organization.getId(),1);
            for (Project project:projectList){
                JSONArray array = new JSONArray();
                JSONObject object = new JSONObject();
                if (project.getProjectType()!=null && project.getProjectType().getName()!=null)
                    object.put("projectType", project.getProjectType().getName());
                else object.put("projectType", "");
                array.add(object);
                JSONObject object1 = new JSONObject();
                object1.put("name", project.getName() == null ? 0 : project.getName());
                array.add(object1);
                JSONObject object2 = new JSONObject();
                if (project.getOrganization()!=null && project.getOrganization().getName()!=null)
                    object2.put("organization", project.getOrganization().getName());
                else object2.put("organization", "");
                array.add(object2);
                JSONObject object3 = new JSONObject();
                object3.put("leader", project.getLeader() == null ? 0 : project.getLeader());
                array.add(object3);
                JSONObject object4 = new JSONObject();
                object4.put("description", project.getDescription() == null ? 0 : project.getDescription());
                array.add(object4);
                JSONObject object5 = new JSONObject();
                object5.put("personCountSum", project.getPersonCountSum() == null ? 0 : project.getPersonCountSum());
                array.add(object5);
                JSONObject object6 = new JSONObject();
                object6.put("personTimeSum", project.getPersonTimeSum() == null ? 0 : project.getPersonTimeSum());
                array.add(object6);
                JSONObject object7 = new JSONObject();
                object7.put("totalTimeSum", project.getTotalTimeSum() == null ? 0 : project.getTotalTimeSum());
                array.add(object7);
                JSONObject object8 = new JSONObject();
                object8.put("averageTime", project.getAverageTime() == null ? 0 : project.getAverageTime());
                array.add(object8);

                JSONObject object25 = new JSONObject();
                if (project.getAdmin() != null && project.getAdmin().getName() != null)
                    object25.put("adminName", project.getAdmin().getName());
                else object25.put("adminName", "");
                array.add(object25);
                JSONObject object26 = new JSONObject();
                object26.put("createTime", project.getCreateTime()==null?"":project.getCreateTime());
                array.add(object26);
                JSONObject object27 = new JSONObject();
                object27.put("lastModifyTime", project.getModifyTime()==null?"":project.getModifyTime());
                array.add(object27);
                jsonArray.add(array);
            }
        }
        return jsonArray;
    }

    //全区服务项目信息导出
    public void exportExcel(String district, HttpServletRequest request, HttpServletResponse response) {
        String[] columnNames = new String[]{"残疾人服务内容大类", "项目名称", "所属机构名称", "项目负责人", "项目简介",
                "总服务人数", "总服务人次", "总服务时长", "平均服务时长", "提交人",
                "提交时间", "更新时间"};
        String fileName = "ProjectList_" + district + "_" + sdf.format(new Date()) + ".xls";
        ExcelUtils.exportExcel(fileName, columnNames, getListsByDistrict(district), request, response);
    }
}
