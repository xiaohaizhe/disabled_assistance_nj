package com.hd.home_disabled.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.Admin;
import com.hd.home_disabled.entity.Organization;
import com.hd.home_disabled.entity.Project;
import com.hd.home_disabled.entity.User;
import com.hd.home_disabled.entity.dictionary.ProjectType;
import com.hd.home_disabled.entity.statistic.ProjectUser;
import com.hd.home_disabled.entity.statistic.ProjectUserDetail;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.model.statistic.ProjectStatistic;
import com.hd.home_disabled.repository.*;
import com.hd.home_disabled.utils.ExcelUtils;
import com.hd.home_disabled.utils.PageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final ProjectUserRepository projectUserRepository;
    private final ProjectUserDetailRepository projectUserDetailRepository;

    public ProjectService(ProjectRepository projectRepository, ProjectTypeRepository projectTypeRepository, OrganizationRepository organizationRepository, AdminRepository adminRepository, ProjectUserRepository projectUserRepository, ProjectUserDetailRepository projectUserDetailRepository) {
        this.projectRepository = projectRepository;
        this.projectTypeRepository = projectTypeRepository;
        this.organizationRepository = organizationRepository;
        this.adminRepository = adminRepository;
        this.projectUserRepository = projectUserRepository;
        this.projectUserDetailRepository = projectUserDetailRepository;
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

    //Project-->ProjectStatistic
    private ProjectStatistic getStatistic(Project project){
        ProjectStatistic projectStatistic = new ProjectStatistic();
        projectStatistic.setId(project.getId());
        projectStatistic.setName(project.getName());
        projectStatistic.setPersonCountSum(project.getPersonCountSum());
        projectStatistic.setPersonTimeSum(project.getPersonTimeSum());
        projectStatistic.setTotalTimeSum(project.getTotalTimeSum());
        projectStatistic.setAverageTime(project.getAverageTime());
        return projectStatistic;
    }

    //机构下服务项目运行统计
    public JSONObject getProjectStatistic(Integer organizationId){
        List<Project>  projectList = projectRepository.findByOrganizationAndStatus(organizationId,1);
        List<ProjectStatistic> projectStatisticList = new ArrayList<>();
        for (Project project : projectList){
            projectStatisticList.add(getStatistic(project));
        }
        return RESCODE.SUCCESS.getJSONRES(projectStatisticList);
    }

    /**
     * 查询机构下服务项目数据分析
     * 数据内容1:
     * 项目运行数据统计（总服务人数，总服务人次，总服务时长，平均服务时长）
     * @param id 项目id
     * @return 结果
     */
    public JSONObject getProjectAnalysis1(Integer id){
        Optional<Project> projectOptional = projectRepository.findByIdAndStatus(id,1);
        if (projectOptional.isPresent()){
            Project project = projectOptional.get();
            ProjectStatistic projectStatistic = getStatistic(project);
            return RESCODE.SUCCESS.getJSONRES(projectStatistic);
        }
        return RESCODE.PROJECT_ID_NOT_EXIST.getJSONRES();
    }

    /**
     * 残疾人参加项目时间汇总
     * @param projectId 项目id
     * @param userId    残疾人id
     * @return  结果
     */
    private JSONArray getTimeSummary(Integer projectId,Long userId){
        JSONArray array = new JSONArray();
        List<ProjectUserDetail> projectUserDetailList = projectUserDetailRepository.findByProjectAndUser(projectId,userId);
        for (ProjectUserDetail projectUserDetail:projectUserDetailList){
            JSONObject object = new JSONObject();
            object.put("start",projectUserDetail.getStart());
            object.put("end",projectUserDetail.getEnd());
            array.add(object);
        }
        return array;
    }

    private JSONObject getProjectUser(ProjectUser projectUser){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",projectUser.getId());
        if (projectUser.getProject()!=null && projectUser.getProject().getName()!=null)
            jsonObject.put("projectName",projectUser.getProject().getName());
        else jsonObject.put("projectName","");
        if (projectUser.getUser()!=null){
            User user = projectUser.getUser();
            if (user.getId()!=null){
                jsonObject.put("userId",user.getId());
            }else jsonObject.put("userId",0);
            if (user.getName()!=null){
                jsonObject.put("userName",user.getName());
            }else jsonObject.put("userName","");
            if (user.getIdNumber()!=null)
                jsonObject.put("userIdNumber",user.getIdNumber());
            else jsonObject.put("userIdNumber","");
            if (user.getDisabilityCertificateNumber()!=null)
                jsonObject.put("userDisabilityCertificateNumber",user.getDisabilityCertificateNumber());
            else jsonObject.put("userDisabilityCertificateNumber","");
            if (user.getTypeOfDisability()!=null && user.getTypeOfDisability().getName()!=null)
                jsonObject.put("userTypeOfDisability",user.getTypeOfDisability().getName());
            else jsonObject.put("userTypeOfDisability","");
            if (user.getDisabilityDegree()!=null && user.getDisabilityDegree().getType()!=null)
                jsonObject.put("userDisabilityDegree",user.getDisabilityDegree().getType());
            else jsonObject.put("userDisabilityDegree","");
        }else {
            jsonObject.put("userId",0);
            jsonObject.put("userName","");
            jsonObject.put("userIdNumber","");
            jsonObject.put("userDisabilityCertificateNumber","");
            jsonObject.put("userTypeOfDisability","");
            jsonObject.put("userDisabilityDegree","");
        }
        if (projectUser.getStart()!=null)
            jsonObject.put("start",sdf.format(projectUser.getStart()));
        else jsonObject.put("start","");
        if (projectUser.getEnd()!=null)
            jsonObject.put("end",sdf.format(projectUser.getEnd()));
        else jsonObject.put("end","");
        if (projectUser.getLengthOfService()!=null)
            jsonObject.put("lengthOfService",projectUser.getLengthOfService());
        else jsonObject.put("lengthOfService",0);
        if (projectUser.getServicesNum()!=null)
            jsonObject.put("serviceSum",projectUser.getServicesNum());
        else jsonObject.put("serviceSum",0);
        if (projectUser.getTotalLengthOfService()!=null)
            jsonObject.put("totalLengthOfService",projectUser.getTotalLengthOfService());
        else jsonObject.put("totalLengthOfService",0);
        //每次服务时间汇总
        if (projectUser.getProject()!=null && projectUser.getProject().getId()!=null &&
                projectUser.getUser()!=null && projectUser.getUser().getId()!=null)
            jsonObject.put("timeSummary",getTimeSummary(projectUser.getProject().getId(),projectUser.getUser().getId()));
        else jsonObject.put("timeSummary","");
        if (projectUser.getUser()!=null && projectUser.getUser().getSubsidies()!=null)
            jsonObject.put("subsidies",projectUser.getUser().getSubsidies());
        else jsonObject.put("subsidies",0);
        if (projectUser.getAdmin()!=null && projectUser.getAdmin().getName()!=null)
            jsonObject.put("adminName",projectUser.getAdmin().getName());
        else jsonObject.put("adminName","");
        if (projectUser.getCreateTime()!=null)
            jsonObject.put("createTime",sdf.format(projectUser.getCreateTime()));
        else jsonObject.put("createTime","");
        if (projectUser.getModifyTime()!=null)
            jsonObject.put("lastModifyTime",sdf.format(projectUser.getModifyTime()));
        else jsonObject.put("lastModifyTime","");
        return jsonObject;
    }

    /**
     * 查询机构下服务项目数据分析
     * 数据内容2:
     * 参与项目残疾人分页
     * @param id 项目id
     * @param page  页码
     * @param number    每页显示数量
     * @param sorts     排序条件
     * @return  结果
     */
    public JSONObject getProjectAnalysis2(Integer id,Integer page,Integer number,String sorts){
        Optional<Project> projectOptional = projectRepository.findByIdAndStatus(id,1);
        List<JSONObject> projectUserList = new ArrayList<>();
        if (projectOptional.isPresent()){
            Pageable pageable = PageUtils.getPage(page, number, sorts);
            Page<ProjectUser> projectUserPage = projectUserRepository.findByProject(id, pageable);
            List<ProjectUser> projectUsers = projectUserPage.getContent();
            for (ProjectUser projectUser:projectUsers){
                JSONObject object = getProjectUser(projectUser);
                projectUserList.add(object);
            }
            return RESCODE.SUCCESS.getJSONRES(projectUserList);
        }
        return RESCODE.PROJECT_ID_NOT_EXIST.getJSONRES(projectUserList);
    }

    private List<JSONArray> getProjeidctUserListByProjectId(Integer id){
        List<JSONArray> jsonArray = new ArrayList<>();
        List<ProjectUser> projectUsers = projectUserRepository.findByProject(id);
        for (ProjectUser projectUser:projectUsers){
            JSONObject object = getProjectUser(projectUser);
            JSONArray array = new JSONArray();

            JSONObject object2 = new JSONObject();
            object2.put("userName",object.get("userName"));
            array.add(object2);

            JSONObject object1 = new JSONObject();
            object1.put("userIdNumber",object.get("userIdNumber"));
            array.add(object1);

            JSONObject object3 = new JSONObject();
            object3.put("userDisabilityCertificateNumber",object.get("userDisabilityCertificateNumber"));
            array.add(object3);

            JSONObject object4 = new JSONObject();
            object4.put("userTypeOfDisability",object.get("userTypeOfDisability"));
            array.add(object4);

            JSONObject object5 = new JSONObject();
            object5.put("userDisabilityDegree",object.get("userDisabilityDegree"));
            array.add(object5);

            JSONObject object6 = new JSONObject();
            object6.put("start",object.get("start"));
            array.add(object6);

            JSONObject object7 = new JSONObject();
            object7.put("end",object.get("end"));
            array.add(object7);

            JSONObject object8 = new JSONObject();
            object8.put("lengthOfService",object.get("lengthOfService"));
            array.add(object8);

            JSONObject object9 = new JSONObject();
            object9.put("serviceSum",object.get("serviceSum"));
            array.add(object9);

            JSONObject object10 = new JSONObject();
            object10.put("totalLengthOfService",object.get("totalLengthOfService"));
            array.add(object10);

            JSONObject object11 = new JSONObject();
            object11.put("timeSummary",object.get("timeSummary"));
            array.add(object11);

            JSONObject object12 = new JSONObject();
            object12.put("subsidies",object.get("subsidies"));
            array.add(object12);

            JSONObject object13 = new JSONObject();
            object13.put("adminName",object.get("adminName"));
            array.add(object13);

            JSONObject object14 = new JSONObject();
            object14.put("createTime",object.get("createTime"));
            array.add(object14);

            JSONObject object15 = new JSONObject();
            object15.put("lastModifyTime",object.get("lastModifyTime"));
            array.add(object15);

            jsonArray.add(array);
        }
        return jsonArray;
    }

    /**
     * 机构下服务项目数据分析
     * 数据内容2:
     * 参与项目残疾人数据导出
     * @param id 项目id
     */
    public void getProjectAnalysis3(Integer id,HttpServletRequest request, HttpServletResponse response){
        String[] columnNames = new String[]{"残疾人姓名", "身份证号", "残疾证号", "残疾类别", "残疾等级",
                "本次刷卡签到时间", "本次刷卡离开时间", "本次服务时长", "总服务次数","总服务时长",
                "每次服务时间汇总","补贴单价与价格","提交人", "提交时间", "更新时间"};
        Optional<Project> projectOptional = projectRepository.findByIdAndStatus(id,1);
        if (projectOptional.isPresent()){
            String fileName = "userList_" + projectOptional.get().getName() + "_" + sdf.format(new Date()) + ".xls";
            ExcelUtils.exportExcel(fileName, columnNames, getProjeidctUserListByProjectId(id), request, response);
        }



        /*
        List<JSONObject> projectUserList = new ArrayList<>();
        if (projectOptional.isPresent()){
            List<ProjectUser> projectUsers = projectUserRepository.findByProject(id);
            for (ProjectUser projectUser:projectUsers){
                JSONObject object = getProjectUser(projectUser);
                projectUserList.add(object);
            }
            return RESCODE.SUCCESS.getJSONRES(projectUserList);
        }*/
    }
}
