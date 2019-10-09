package com.hd.home_disabled.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.Admin;
import com.hd.home_disabled.entity.Organization;
import com.hd.home_disabled.entity.Project;
import com.hd.home_disabled.entity.User;
import com.hd.home_disabled.entity.dictionary.Plan;
import com.hd.home_disabled.entity.dictionary.ProjectType;
import com.hd.home_disabled.entity.statistic.ProjectTypeStatistic;
import com.hd.home_disabled.entity.statistic.ProjectUser;
import com.hd.home_disabled.entity.statistic.ProjectUserDetail;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.model.statistic.ProjectStatistic;
import com.hd.home_disabled.repository.*;
import com.hd.home_disabled.utils.ExcelUtils;
import com.hd.home_disabled.utils.PageUtils;
import com.hd.home_disabled.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private final ProjectTypeStatisticRepository projectTypeStatisticRepository;
    private final TypeOfDisabilityRepository typeOfDisabilityRepository;
    private final PlanRepository planRepository;

    public ProjectService(ProjectRepository projectRepository, ProjectTypeRepository projectTypeRepository, OrganizationRepository organizationRepository, AdminRepository adminRepository, ProjectUserRepository projectUserRepository, ProjectUserDetailRepository projectUserDetailRepository, ProjectTypeStatisticRepository projectTypeStatisticRepository, TypeOfDisabilityRepository typeOfDisabilityRepository, PlanRepository planRepository) {
        this.projectRepository = projectRepository;
        this.projectTypeRepository = projectTypeRepository;
        this.organizationRepository = organizationRepository;
        this.adminRepository = adminRepository;
        this.projectUserRepository = projectUserRepository;
        this.projectUserDetailRepository = projectUserDetailRepository;
        this.projectTypeStatisticRepository = projectTypeStatisticRepository;
        this.typeOfDisabilityRepository = typeOfDisabilityRepository;
        this.planRepository = planRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        p.setProjectType(project.getProjectType().getName());
        p.setLeader(project.getLeader());
        p.setDescription(project.getDescription());
        p.setImage(project.getImage());
        p.setStartTime(project.getStartTime());
        p.setEndTime(project.getEndTime());

        p.setPersonCountSum(project.getPersonCountSum() == null ? 0 : project.getPersonCountSum());
        p.setPersonTimeSum(project.getPersonTimeSum() == null ? 0 : project.getPersonTimeSum());
        p.setTotalTimeSum(project.getTotalTimeSum() == null ? 0 : project.getTotalTimeSum());
        p.setAverageTime(project.getAverageTime() == null ? 0 : project.getAverageTime());

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
            List<Project> projectList = projectRepository.findByOrganizationAndStatus(organization.getId(), 1);
            for (Project project : projectList) {
                JSONArray array = new JSONArray();
                JSONObject object = new JSONObject();
                if (project.getProjectType() != null && project.getProjectType().getName() != null)
                    object.put("projectType", project.getProjectType().getName());
                else object.put("projectType", "");
                array.add(object);
                JSONObject object1 = new JSONObject();
                object1.put("name", project.getName() == null ? 0 : project.getName());
                array.add(object1);
                JSONObject object2 = new JSONObject();
                if (project.getOrganization() != null && project.getOrganization().getName() != null)
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
                object26.put("createTime", project.getCreateTime() == null ? "" : project.getCreateTime());
                array.add(object26);
                JSONObject object27 = new JSONObject();
                object27.put("lastModifyTime", project.getModifyTime() == null ? "" : project.getModifyTime());
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
    private ProjectStatistic getStatistic(Project project) {
        ProjectStatistic projectStatistic = new ProjectStatistic();
        projectStatistic.setId(project.getId());
        projectStatistic.setName(project.getName());
        projectStatistic.setPersonCountSum(project.getPersonCountSum() == null ? 0 : project.getPersonCountSum());
        projectStatistic.setPersonTimeSum(project.getPersonTimeSum() == null ? 0 : project.getPersonTimeSum());
        projectStatistic.setTotalTimeSum(project.getTotalTimeSum() == null ? 0f : project.getTotalTimeSum());
        projectStatistic.setAverageTime(project.getAverageTime() == null ? 0f : project.getAverageTime());
        return projectStatistic;
    }

    //机构下服务项目运行统计
    public JSONObject getProjectStatistic(Integer organizationId) {
        List<Project> projectList = projectRepository.findByOrganizationAndStatus(organizationId, 1);
        List<ProjectStatistic> projectStatisticList = new ArrayList<>();
        for (Project project : projectList) {
            projectStatisticList.add(getStatistic(project));
        }
        return RESCODE.SUCCESS.getJSONRES(projectStatisticList);
    }

    /**
     * 查询机构下服务项目数据分析
     * 数据内容1:
     * 项目运行数据统计（总服务人数，总服务人次，总服务时长，平均服务时长）
     *
     * @param organizationId 机构id
     * @return 结果
     */
    public JSONObject getProjectAnalysis1(Integer organizationId) {
        Optional<Organization> organizationOptional = organizationRepository.findByIdAndStatus(organizationId, 1);
        if (organizationOptional.isPresent()) {
            ProjectStatistic projectStatistic = new ProjectStatistic();
            projectStatistic.setName("统计");
            int personCountSum = 0; //服务人数总数
            int personTimeSum = 0;  //服务总人次
            float totalTimeSum = 0f;   //服务总时长（分钟）
            float averageTime = 0f;        //平均服务时长：总时长/总人次
            List<Project> projectList = projectRepository.findByOrganizationAndStatus(organizationId, 1);
            for (Project project : projectList) {
                if (project.getPersonCountSum() != null) personCountSum += project.getPersonCountSum();
                if (project.getPersonTimeSum() != null) personTimeSum += project.getPersonTimeSum();
                if (project.getTotalTimeSum() != null) totalTimeSum += project.getTotalTimeSum();
            }
            if (personTimeSum != 0)
                averageTime = (float) Math.round((totalTimeSum / personTimeSum) * 100) / 100;
            projectStatistic.setPersonCountSum(personCountSum);
            projectStatistic.setPersonTimeSum(personTimeSum);
            projectStatistic.setTotalTimeSum(totalTimeSum);
            projectStatistic.setAverageTime(averageTime);
            return RESCODE.SUCCESS.getJSONRES(projectStatistic);
        }
        return RESCODE.ORGANIZATION_ID_NOT_EXIST.getJSONRES();
    }

    /**
     * 残疾人参加项目时间汇总
     *
     * @param projectId 项目id
     * @param userId    残疾人id
     * @return 结果
     */
    private JSONArray getTimeSummary(Integer projectId, Long userId) {
        JSONArray array = new JSONArray();
        List<ProjectUserDetail> projectUserDetailList = projectUserDetailRepository.findByProjectAndUser(projectId, userId);
        for (ProjectUserDetail projectUserDetail : projectUserDetailList) {
            JSONObject object = new JSONObject();
            object.put("start", projectUserDetail.getStart());
            object.put("end", projectUserDetail.getEnd());
            array.add(object);
        }
        return array;
    }

    private JSONObject getProjectUser(ProjectUser projectUser) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", projectUser.getId());
        if (projectUser.getProject() != null && projectUser.getProject().getName() != null)
            jsonObject.put("projectName", projectUser.getProject().getName());
        else jsonObject.put("projectName", "");
        if (projectUser.getUser() != null) {
            User user = projectUser.getUser();
            if (user.getId() != null) {
                jsonObject.put("userId", user.getId());
            } else jsonObject.put("userId", 0);
            if (user.getName() != null) {
                jsonObject.put("userName", user.getName());
            } else jsonObject.put("userName", "");
            if (user.getIdNumber() != null)
                jsonObject.put("userIdNumber", user.getIdNumber());
            else jsonObject.put("userIdNumber", "");
            if (user.getDisabilityCertificateNumber() != null)
                jsonObject.put("userDisabilityCertificateNumber", user.getDisabilityCertificateNumber());
            else jsonObject.put("userDisabilityCertificateNumber", "");
            if (user.getTypeOfDisability() != null && user.getTypeOfDisability().getName() != null)
                jsonObject.put("userTypeOfDisability", user.getTypeOfDisability().getName());
            else jsonObject.put("userTypeOfDisability", "");
            if (user.getDisabilityDegree() != null && user.getDisabilityDegree().getType() != null)
                jsonObject.put("userDisabilityDegree", user.getDisabilityDegree().getType());
            else jsonObject.put("userDisabilityDegree", "");
        } else {
            jsonObject.put("userId", 0);
            jsonObject.put("userName", "");
            jsonObject.put("userIdNumber", "");
            jsonObject.put("userDisabilityCertificateNumber", "");
            jsonObject.put("userTypeOfDisability", "");
            jsonObject.put("userDisabilityDegree", "");
        }
        if (projectUser.getStart() != null)
            jsonObject.put("start", sdf1.format(projectUser.getStart()));
        else jsonObject.put("start", "");
        if (projectUser.getEnd() != null)
            jsonObject.put("end", sdf1.format(projectUser.getEnd()));
        else jsonObject.put("end", "");
        if (projectUser.getLengthOfService() != null)
            jsonObject.put("lengthOfService", projectUser.getLengthOfService());
        else jsonObject.put("lengthOfService", 0);
        if (projectUser.getServicesNum() != null)
            jsonObject.put("serviceSum", projectUser.getServicesNum());
        else jsonObject.put("serviceSum", 0);
        if (projectUser.getTotalLengthOfService() != null)
            jsonObject.put("totalLengthOfService", projectUser.getTotalLengthOfService());
        else jsonObject.put("totalLengthOfService", 0);
        //每次服务时间汇总
        if (projectUser.getProject() != null && projectUser.getProject().getId() != null &&
                projectUser.getUser() != null && projectUser.getUser().getId() != null)
            jsonObject.put("timeSummary", getTimeSummary(projectUser.getProject().getId(), projectUser.getUser().getId()));
        else jsonObject.put("timeSummary", "");
        if (projectUser.getUser() != null && projectUser.getUser().getSubsidies() != null)
            jsonObject.put("subsidies", projectUser.getUser().getSubsidies());
        else jsonObject.put("subsidies", 0);
        if (projectUser.getAdmin() != null && projectUser.getAdmin().getName() != null)
            jsonObject.put("adminName", projectUser.getAdmin().getName());
        else jsonObject.put("adminName", "");
        if (projectUser.getCreateTime() != null)
            jsonObject.put("createTime", sdf1.format(projectUser.getCreateTime()));
        else jsonObject.put("createTime", "");
        if (projectUser.getModifyTime() != null)
            jsonObject.put("lastModifyTime", sdf1.format(projectUser.getModifyTime()));
        else jsonObject.put("lastModifyTime", "");
        return jsonObject;
    }

    /**
     * 查询机构下服务项目数据分析
     * 数据内容2:
     * 参与项目残疾人分页
     *
     * @param id     项目id
     * @param page   页码
     * @param number 每页显示数量
     * @param sorts  排序条件
     * @return 结果
     */
    public JSONObject getProjectAnalysis2(Integer id, Integer page, Integer number, String sorts) {
        Optional<Project> projectOptional = projectRepository.findByIdAndStatus(id, 1);
        List<JSONObject> projectUserList = new ArrayList<>();
        if (projectOptional.isPresent()) {
            Pageable pageable = PageUtils.getPage(page, number, sorts);
            Page<ProjectUser> projectUserPage = projectUserRepository.findByProject(id, pageable);
            List<ProjectUser> projectUsers = projectUserPage.getContent();
            for (ProjectUser projectUser : projectUsers) {
                JSONObject object = getProjectUser(projectUser);
                projectUserList.add(object);
            }
            return RESCODE.SUCCESS.getJSONRES(projectUserList);
        }
        return RESCODE.PROJECT_ID_NOT_EXIST.getJSONRES(projectUserList);
    }

    public JSONObject getProjectAnalysis3(Integer organizationId, Integer page, Integer number, String sorts) {
        Optional<Organization> organizationOptional = organizationRepository.findByIdAndStatus(organizationId, 1);
        if (organizationOptional.isPresent()) {
            Pageable pageable = PageUtils.getPage(page, number, sorts);
            Page<Project> projectPage = projectRepository.findByOrganizationAndStatus(organizationId, 1, pageable);
            List<com.hd.home_disabled.model.dto.Project> projectList = new ArrayList<>();
            for (Project project : projectPage.getContent()) {
                projectList.add(getModel(project));
            }
            return RESCODE.SUCCESS.getJSONRES(projectList, projectPage.getTotalPages(), projectPage.getTotalElements());
        }
        return RESCODE.ORGANIZATION_ID_NOT_EXIST.getJSONRES();
    }

    private List<JSONArray> getProjectIdUserListByProjectId(Integer id) {
        List<JSONArray> jsonArray = new ArrayList<>();
        List<ProjectUser> projectUsers = projectUserRepository.findByProject(id);
        for (ProjectUser projectUser : projectUsers) {
            JSONObject object = getProjectUser(projectUser);
            JSONArray array = new JSONArray();

            JSONObject object2 = new JSONObject();
            object2.put("userName", object.get("userName"));
            array.add(object2);

            JSONObject object1 = new JSONObject();
            object1.put("userIdNumber", object.get("userIdNumber"));
            array.add(object1);

            JSONObject object3 = new JSONObject();
            object3.put("userDisabilityCertificateNumber", object.get("userDisabilityCertificateNumber"));
            array.add(object3);

            JSONObject object4 = new JSONObject();
            object4.put("userTypeOfDisability", object.get("userTypeOfDisability"));
            array.add(object4);

            JSONObject object5 = new JSONObject();
            object5.put("userDisabilityDegree", object.get("userDisabilityDegree"));
            array.add(object5);

            JSONObject object6 = new JSONObject();
            object6.put("start", object.get("start"));
            array.add(object6);

            JSONObject object7 = new JSONObject();
            object7.put("end", object.get("end"));
            array.add(object7);

            JSONObject object8 = new JSONObject();
            object8.put("lengthOfService", object.get("lengthOfService"));
            array.add(object8);

            JSONObject object9 = new JSONObject();
            object9.put("serviceSum", object.get("serviceSum"));
            array.add(object9);

            JSONObject object10 = new JSONObject();
            object10.put("totalLengthOfService", object.get("totalLengthOfService"));
            array.add(object10);

            JSONObject object11 = new JSONObject();
            object11.put("timeSummary", object.get("timeSummary"));
            array.add(object11);

            JSONObject object12 = new JSONObject();
            object12.put("subsidies", object.get("subsidies"));
            array.add(object12);

            JSONObject object13 = new JSONObject();
            object13.put("adminName", object.get("adminName"));
            array.add(object13);

            JSONObject object14 = new JSONObject();
            object14.put("createTime", object.get("createTime"));
            array.add(object14);

            JSONObject object15 = new JSONObject();
            object15.put("lastModifyTime", object.get("lastModifyTime"));
            array.add(object15);

            jsonArray.add(array);
        }
        return jsonArray;
    }

    private List<JSONArray> organizationProjectList(Integer organizationId) {
        List<JSONArray> jsonArray = new ArrayList<>();
        List<Project> projectList = projectRepository.findByOrganizationAndStatus(organizationId, 1);
        for (Project project : projectList) {
            JSONArray array = new JSONArray();

            JSONObject object2 = new JSONObject();
            object2.put("userName", project.getProjectType().getName());
            array.add(object2);

            JSONObject object1 = new JSONObject();
            object1.put("userIdNumber", project.getName());
            array.add(object1);

            JSONObject object3 = new JSONObject();
            object3.put("userDisabilityCertificateNumber", project.getLeader());
            array.add(object3);

            JSONObject object4 = new JSONObject();
            object4.put("userTypeOfDisability", project.getDescription());
            array.add(object4);

            JSONObject object5 = new JSONObject();
            object5.put("userDisabilityDegree", project.getImage());
            array.add(object5);

            JSONObject object6 = new JSONObject();
            object6.put("start", project.getStartTime());
            array.add(object6);

            JSONObject object7 = new JSONObject();
            object7.put("end", project.getEndTime());
            array.add(object7);

            jsonArray.add(array);
        }
        return jsonArray;
    }

    /**
     * 机构下服务项目数据分析
     * 数据内容2:
     * 参与项目残疾人数据导出
     *
     * @param id 项目id
     */
    public void getProjectAnalysis3(Integer id, HttpServletRequest request, HttpServletResponse response) {
        String[] columnNames = new String[]{"残疾人姓名", "身份证号", "残疾证号", "残疾类别", "残疾等级",
                "本次刷卡签到时间", "本次刷卡离开时间", "本次服务时长", "总服务次数", "总服务时长",
                "每次服务时间汇总", "补贴单价与价格", "提交人", "提交时间", "更新时间"};
        Optional<Project> projectOptional = projectRepository.findByIdAndStatus(id, 1);
        if (projectOptional.isPresent()) {
            String fileName = "userList_" + projectOptional.get().getName() + "_" + sdf.format(new Date()) + ".xls";
            ExcelUtils.exportExcel(fileName, columnNames, getProjectIdUserListByProjectId(id), request, response);
        }
    }

    /**
     * 机构下全部服务项目导出
     * 项目负责人、项目简介、项目图片、项目开始时间、项目结束时间
     *
     * @param organizationId 机构id
     * @param request        request
     * @param response       response
     */
    public void organizationProjectListExport(Integer organizationId, HttpServletRequest request, HttpServletResponse response) {
        String[] columnNames = new String[]{"残疾人服务内容大类", "创建项目名称", "项目负责人", "项目简介", "项目图片",
                "项目开始时间", "项目结束时间"};
        Optional<Project> projectOptional = projectRepository.findByIdAndStatus(organizationId, 1);
        if (projectOptional.isPresent()) {
            String fileName = "userList_" + projectOptional.get().getName() + "_" + sdf.format(new Date()) + ".xls";
            ExcelUtils.exportExcel(fileName, columnNames, organizationProjectList(organizationId), request, response);
        }
    }


    /**
     * 区服务项目分页
     *
     * @param district 区名
     * @param page     页码
     * @param number   每页显示数量
     * @param sorts    排序调价
     * @return 结果
     */
    public JSONObject getPagesByDistrict(Integer projectTypeId, Integer organizationId, String start, String end, String district, Integer page, Integer number, String sorts) {
        Pageable pageable = PageUtils.getPage(page, number, sorts);
        List<Organization> organizationList = organizationRepository.findByDistrictAndStatus(district, 1);
        List<Integer> ids = new ArrayList<>();
        List<com.hd.home_disabled.model.dto.Project> projectList = new ArrayList<>();
        if (organizationId != null && organizationId != 0) {
            Optional<Organization> organizationOptional = organizationRepository.findById(organizationId);
            if (organizationOptional.isPresent()) {
                ids.add(organizationId);
            }
        } else {
            for (Organization organization : organizationList) {
                ids.add(organization.getId());
            }
        }
        boolean flag = false;
        Date s = null;
        Date e = null;
        if (!StringUtils.isEmpty(start)
                && !StringUtils.isEmpty(start.trim())
                && !StringUtils.isEmpty(end) && !StringUtils.isEmpty(end.trim())) {
            try {
                s = sdf1.parse(start);
                e = sdf1.parse(end);
                flag = true;
            } catch (ParseException ex) {
                logger.error(ex.getMessage());
                flag = false;
            }
        }
        Page<Project> projectPage;
        if (projectTypeId != null && projectTypeId != 0) {
            if (flag) {
                projectPage = projectRepository.findByOrganizationAndStatusAndProjectTypeAndCreateTimeBetween(
                        ids, 1, projectTypeId, s, e, pageable);
            } else {
                projectPage = projectRepository.findByOrganizationAndStatusAndProjectType(ids, 1, projectTypeId, pageable);
            }
        } else {
            if (flag) {
                projectPage = projectRepository.findByOrganizationAndStatusAndCreateTimeBetween(ids, 1, s, e, pageable);
            } else {
                projectPage = projectRepository.findByOrganizationAndStatus(ids, 1, pageable);
            }

        }
        for (Project project : projectPage.getContent()) {
            projectList.add(getModel(project));
        }
        return RESCODE.SUCCESS.getJSONRES(projectList, projectPage.getTotalPages(), projectPage.getTotalElements());
    }

    private List<JSONArray> getProjectList(List<Project> projectList1) {
        List<JSONArray> jsonArray = new ArrayList<>();
        for (Project project : projectList1) {
            JSONArray array = new JSONArray();

            JSONObject object2 = new JSONObject();
            object2.put("projectType", project.getProjectType().getName());
            array.add(object2);

            JSONObject object1 = new JSONObject();
            object1.put("name", project.getName());
            array.add(object1);

            JSONObject object3 = new JSONObject();
            object3.put("organizationName", project.getOrganization().getName());
            array.add(object3);

            JSONObject object4 = new JSONObject();
            object4.put("leader", project.getLeader());
            array.add(object4);

            JSONObject object5 = new JSONObject();
            object5.put("description", project.getDescription());
            array.add(object5);

            JSONObject object6 = new JSONObject();
            object6.put("personCountSum", project.getPersonCountSum() == null ? 0 : project.getPersonCountSum());
            array.add(object6);

            JSONObject object7 = new JSONObject();
            object7.put("personTimeSum", project.getPersonTimeSum() == null ? 0 : project.getPersonTimeSum());
            array.add(object7);

            JSONObject object8 = new JSONObject();
            object8.put("totalTimeSum", project.getTotalTimeSum() == null ? 0 : project.getTotalTimeSum());
            array.add(object8);

            JSONObject object9 = new JSONObject();
            object9.put("averageTime", project.getAverageTime() == null ? 0 : project.getAverageTime());
            array.add(object9);

            JSONObject object10 = new JSONObject();
            object10.put("adminName", project.getAdmin().getName());
            array.add(object10);

            JSONObject object11 = new JSONObject();
            object11.put("createTime", project.getCreateTime());
            array.add(object11);

            jsonArray.add(array);
        }
        return jsonArray;
    }

    public void exportPagesByDistrict(String district, HttpServletRequest request, HttpServletResponse response) {
        String[] columnNames = new String[]{"残疾人服务内容大类", "项目名称", "所属机构名称", "项目负责人", "项目简介",
                "总服务人数", "总服务人次", "总服务时长", "平均服务时长",
                "提交人", "提交时间"};
        List<Organization> organizationList = organizationRepository.findByDistrictAndStatus(district, 1);
        List<Integer> ids = new ArrayList<>();
        for (Organization organization : organizationList) {
            ids.add(organization.getId());
        }
        List<Project> projectList1 = projectRepository.findByOrganizationAndStatus(ids, 1);
        String fileName = "projectList_" + district + "_" + sdf.format(new Date()) + ".xls";
        ExcelUtils.exportExcel(fileName, columnNames, getProjectList(projectList1), request, response);
    }

    /**
     * 领导驾驶舱：全区数据1
     *
     * @param district 区名
     * @return 结果
     */
    public JSONObject overview1(String district) {
        JSONObject object = new JSONObject();
        Integer projectSum = 0;    //机构服务项目总数
        Integer personCountSum = 0; //服务人数总数
        Integer personTimeSum = 0;  //服务总人次
        Float totalTimeSum = 0f;   //服务总时长
        float averageTime = 0f;      //平均服务时长：总时长/总人次
        List<Organization> organizationList = organizationRepository.findByDistrictAndStatus(district, 1);
        for (Organization organization : organizationList) {
/*            if (organization.getProjectSum() != null)
                projectSum += organization.getProjectSum();*/
            if (organization.getPersonCountSum() != null)
                personCountSum += organization.getPersonCountSum();
            if (organization.getPersonTimeSum() != null)
                personTimeSum += organization.getPersonTimeSum();
            if (organization.getTotalTimeSum() != null)
                totalTimeSum += organization.getTotalTimeSum();
            if (personTimeSum != 0)
                averageTime = (float) Math.round(((float) totalTimeSum / personTimeSum) * 100) / 100;
        }
        projectSum = projectRepository.findByStatus(1).size();
        object.put("projectSum", projectSum);
        object.put("personCountSum", personCountSum);
        object.put("personTimeSum", personTimeSum);
        object.put("totalTimeSum", totalTimeSum);
        object.put("averageTime", averageTime);
        return RESCODE.SUCCESS.getJSONRES(object);
    }


    /**
     * 领导驾驶舱：全区数据2，当天数据
     *
     * @return 结果
     */
    public JSONObject overview2() {
        int projectSum;    //当天服务项目总数
        int personCountSum; //当天服务人数总数
        int personTimeSum;  //当天服务总人次
        Float totalTimeSum = 0f;   //当天服务总时长
        float averageTime;      //平均服务时长：总时长/总人次
        Date date = new Date();
        try {
            date = sdf.parse(sdf.format(date));
        } catch (ParseException e) {
            return RESCODE.TIME_PARSE_FAILURE.getJSONRES(e.getMessage());
        }
        List<ProjectUserDetail> projectUserDetailList = projectUserDetailRepository.findByStartAfter(date);
        logger.info("当天打卡详情：");
        Set<Project> projects = new HashSet<>();
        Set<User> users = new HashSet<>();
        personTimeSum = projectUserDetailList.size();
        for (ProjectUserDetail projectUserDetail : projectUserDetailList) {
            logger.info("详情id" + projectUserDetail.getId());
            totalTimeSum += projectUserDetail.getLengthOfService();
            projects.add(projectUserDetail.getProject());
            users.add(projectUserDetail.getUser());
        }
        projectSum = projects.size();
        personCountSum = users.size();
        averageTime = (float) Math.round(((float) totalTimeSum / personTimeSum) * 100) / 100;
        JSONObject object = new JSONObject();
        object.put("projectSum", projectSum);
        object.put("personCountSum", personCountSum);
        object.put("personTimeSum", personTimeSum);
        object.put("totalTimeSum", totalTimeSum);
        object.put("averageTime", averageTime);
        return RESCODE.SUCCESS.getJSONRES(object);
    }

    public JSONObject getData(ProjectTypeStatistic projectTypeStatistic) {
        JSONObject object = new JSONObject();
        object.put("personTimeSum", projectTypeStatistic.getPersonTimeSum() == null ? 0 : projectTypeStatistic.getPersonTimeSum());
        object.put("personCountSum", projectTypeStatistic.getPersonCountSum() == null ? 0 : projectTypeStatistic.getPersonCountSum());
        object.put("totalTimeSum", projectTypeStatistic.getTotalTimeSum() == null ? 0 : projectTypeStatistic.getTotalTimeSum());
        object.put("averageTime", projectTypeStatistic.getAverageTime() == null ? 0 : projectTypeStatistic.getAverageTime());
        return object;
    }

    /**
     * 领导驾驶舱：残疾人服务内容分析
     *
     * @return 结果
     */
    public JSONObject overview3() {
        JSONObject object = new JSONObject();
        List<ProjectType> projectTypeList = projectTypeRepository.findAll();
        JSONObject data = new JSONObject();
        data.put("personTimeSum", 0);
        data.put("personCountSum", 0);
        data.put("totalTimeSum", 0);
        data.put("averageTime", 0);
        for (ProjectType projectType : projectTypeList) {
            object.put(projectType.getName(), data);
        }
        List<ProjectTypeStatistic> projectTypeStatisticList = projectTypeStatisticRepository.findAll();
        for (ProjectTypeStatistic projectTypeStatistic : projectTypeStatisticList) {
            object.put(projectTypeStatistic.getProjectType().getName(), getData(projectTypeStatistic));
        }
        return RESCODE.SUCCESS.getJSONRES(object);
    }

    /**
     * 全区各类残疾人当日最喜爱项目分析
     *
     * @param type 残疾人类型：
     *             1	视力残疾
     *             2	听力残疾
     *             3	言语残疾
     *             4	肢体残疾
     *             5	智力残疾
     *             6	精神残疾
     *             7	多重残疾
     *             8	其他
     * @return 结果
     */
    public JSONObject usersPreferenceAnalysisToday(String type) {
        Date date = new Date();
        try {
            date = sdf.parse(sdf.format(date));
        } catch (ParseException e) {
            return RESCODE.TIME_PARSE_FAILURE.getJSONRES(e.getMessage());
        }
        List<ProjectUserDetail> projectUserDetailList = projectUserDetailRepository.findByStartAfter(date);
        JSONObject object = getResult(projectUserDetailList, type);
        return RESCODE.SUCCESS.getJSONRES(object);
    }

    /**
     * 全区各类残疾人最喜爱项目分析
     *
     * @param type 残疾人类型：
     *             1	视力残疾
     *             2	听力残疾
     *             3	言语残疾
     *             4	肢体残疾
     *             5	智力残疾
     *             6	精神残疾
     *             7	多重残疾
     *             8	其他
     * @return 结果
     */
    public JSONObject usersPreferenceAnalysis(String type) {

        List<ProjectUserDetail> projectUserDetailList = projectUserDetailRepository.findAll();
        //遍历type残疾类型，全部打卡数据
        JSONObject object = getResult(projectUserDetailList, type);
        return RESCODE.SUCCESS.getJSONRES(object);
    }

    private JSONObject getResult(List<ProjectUserDetail> projectUserDetailList, String type) {
        List<ProjectUserDetail> projectUserDetails = new ArrayList<>();
        for (ProjectUserDetail projectUserDetail : projectUserDetailList) {
            if (projectUserDetail.getUser().getTypeOfDisability().getName().equals(type)) {
                projectUserDetails.add(projectUserDetail);
            }
        }
        //遍历打卡数据，获取全部项目，与项目服务时长
        JSONObject object = new JSONObject();
        Float sum = 0F;
        for (ProjectUserDetail projectUserDetail : projectUserDetails) {
            sum += projectUserDetail.getLengthOfService();
            String projectName = projectUserDetail.getProject().getName();
            object.merge(projectName, projectUserDetail.getLengthOfService(), (a, b) -> (Float) a + (Float) b);
        }
        //计算项目时长所占比
        if (sum != 0) {
            for (String key : object.keySet()) {
                Float value = object.getFloatValue(key);
                Float result = (float) Math.round((value / sum) * 100) / 100;
                object.put(key, result);
            }
        } else if (object.keySet().size() > 0) {
            Float result = (float) Math.round((1 / object.keySet().size()) * 100) / 100;
            for (String key : object.keySet()) {
                object.put(key, result);
            }
        }
        return object;
    }

    public void addRecord() throws ParseException {
        ProjectUserDetail projectUserDetail = new ProjectUserDetail();
        Admin admin = new Admin();
        admin.setId(1);
        projectUserDetail.setAdmin(admin);
        Project project = new Project();
        project.setId(2);
        projectUserDetail.setProject(project);
        User user = new User();
        user.setId(1L);
        projectUserDetail.setUser(user);
        Date start = sdf1.parse("2019-07-14 08:00:00");
        Date end = sdf1.parse("2019-07-14 10:40:00");
        projectUserDetail.setStart(start);
        projectUserDetail.setEnd(end);
        Float lengthOfService = (float) Math.round(((end.getTime() - start.getTime()) / 1000f / 60f / 60f) * 100) / 100;
        projectUserDetail.setLengthOfService(lengthOfService);
        projectUserDetailRepository.save(projectUserDetail);
    }


    //残疾人服务项目分析
    //全区残疾人服务项目数比对
    public JSONObject projectAnalysis(String district) {
        int count1 = 0;//日间照料
        int count2 = 0;//辅助性就业
        int count3 = 0;//康复服务
        int count4 = 0;//文体活动
        int count5 = 0;//学习培训
        int count6 = 0;//志愿服务
        int count7 = 0;//其它
        List<Organization> organizationList = organizationRepository.findByDistrictAndStatus(district, 1);
        List<Integer> ids = new ArrayList<>();
        for (Organization organization : organizationList) {
            ids.add(organization.getId());
        }
        List<Project> projectList = projectRepository.findByOrganizationAndStatus(ids, 1);
        for (Project project : projectList) {
            switch (project.getProjectType().getId()) {
                case 1:
                    count1++;
                    break;
                case 2:
                    count2++;
                    break;
                case 3:
                    count3++;
                    break;
                case 4:
                    count4++;
                    break;
                case 5:
                    count5++;
                    break;
                case 6:
                    count6++;
                    break;
                case 7:
                    count7++;
                    break;
                default:
                    break;
            }
        }
        JSONObject object = new JSONObject();
        if (projectList.size() > 0) {
            object.put("日间照料", (float) Math.round(((float) count1 / projectList.size()) * 100) / 100);
            object.put("辅助性就业", (float) Math.round(((float) count2 / projectList.size()) * 100) / 100);
            object.put("康复服务", (float) Math.round(((float) count3 / projectList.size()) * 100) / 100);
            object.put("文体活动", (float) Math.round(((float) count4 / projectList.size()) * 100) / 100);
            object.put("学习培训", (float) Math.round(((float) count5 / projectList.size()) * 100) / 100);
            object.put("志愿服务", (float) Math.round(((float) count6 / projectList.size()) * 100) / 100);
            object.put("其他", (float) Math.round(((float) count7 / projectList.size()) * 100) / 100);

        } else {
            object.put("日间照料", count1);
            object.put("辅助性就业", count2);
            object.put("康复服务", count3);
            object.put("文体活动", count4);
            object.put("学习培训", count5);
            object.put("志愿服务", count6);
            object.put("其他", count7);
        }
        return RESCODE.SUCCESS.getJSONRES(object);
    }

    //残疾人服务项目分析
    //今日残疾人服务项目数比对
    public JSONObject projectAnalysisToday() {
        int count1 = 0;//日间照料
        int count2 = 0;//辅助性就业
        int count3 = 0;//康复服务
        int count4 = 0;//文体活动
        int count5 = 0;//学习培训
        int count6 = 0;//志愿服务
        int count7 = 0;//其它
        Date date = new Date();
        try {
            date = sdf.parse(sdf.format(date));

        } catch (ParseException e) {
            return RESCODE.TIME_PARSE_FAILURE.getJSONRES(e.getMessage());
        }
        Set<Project> projectSet = new HashSet<>();
        List<ProjectUserDetail> projectUserDetailList = projectUserDetailRepository.findByStartAfter(date);
        for (ProjectUserDetail projectUserDetail : projectUserDetailList) {
            projectSet.add(projectUserDetail.getProject());
        }
        for (Project project : projectSet) {
            switch (project.getProjectType().getId()) {
                case 1:
                    count1++;
                    break;
                case 2:
                    count2++;
                    break;
                case 3:
                    count3++;
                    break;
                case 4:
                    count4++;
                    break;
                case 5:
                    count5++;
                    break;
                case 6:
                    count6++;
                    break;
                case 7:
                    count7++;
                    break;
                default:
                    break;
            }
        }
        JSONObject object = new JSONObject();
        if (projectSet.size() > 0) {
            object.put("日间照料", (float) Math.round(((float) count1 / projectSet.size()) * 100) / 100);
            object.put("辅助性就业", (float) Math.round(((float) count2 / projectSet.size()) * 100) / 100);
            object.put("康复服务", (float) Math.round(((float) count3 / projectSet.size()) * 100) / 100);
            object.put("文体活动", (float) Math.round(((float) count4 / projectSet.size()) * 100) / 100);
            object.put("学习培训", (float) Math.round(((float) count5 / projectSet.size()) * 100) / 100);
            object.put("志愿服务", (float) Math.round(((float) count6 / projectSet.size()) * 100) / 100);
            object.put("其他", (float) Math.round(((float) count7 / projectSet.size()) * 100) / 100);

        } else {
            object.put("日间照料", count1);
            object.put("辅助性就业", count2);
            object.put("康复服务", count3);
            object.put("文体活动", count4);
            object.put("学习培训", count5);
            object.put("志愿服务", count6);
            object.put("其他", count7);
        }
        return RESCODE.SUCCESS.getJSONRES(object);
    }

    //残疾人服务项目分析
    // 服务项目完成进度分析
    public JSONObject projectAnalysisData() {
        Date today = new Date();
        try {
            today = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int count1 = 0;//日间照料
        int count11 = 0;//今日日间照料
        int count2 = 0;//辅助性就业
        int count21 = 0;//今日辅助性就业
        int count3 = 0;//康复服务
        int count31 = 0;//今日康复服务
        int count4 = 0;//文体活动
        int count41 = 0;//今日文体活动
        int count5 = 0;//学习培训
        int count51 = 0;//今日学习培训
        int count6 = 0;//志愿服务
        int count61 = 0;//今日志愿服务
        int count7 = 0;//其它
        int count71 = 0;//今日其它
        List<Project> projectList = projectRepository.findAll();
        for (Project project : projectList) {
            switch (project.getProjectType().getId()) {
                case 1:
                    if (project.getCreateTime().getTime() > today.getTime()) count11++;
                    count1++;
                    break;
                case 2:
                    if (project.getCreateTime().getTime() > today.getTime()) count21++;
                    count2++;
                    break;
                case 3:
                    if (project.getCreateTime().getTime() > today.getTime()) count31++;
                    count3++;
                    break;
                case 4:
                    if (project.getCreateTime().getTime() > today.getTime()) count41++;
                    count4++;
                    break;
                case 5:
                    if (project.getCreateTime().getTime() > today.getTime()) count51++;
                    count5++;
                    break;
                case 6:
                    if (project.getCreateTime().getTime() > today.getTime()) count61++;
                    count6++;
                    break;
                case 7:
                    if (project.getCreateTime().getTime() > today.getTime()) count71++;
                    count7++;
                    break;
                default:
                    break;
            }
        }
        JSONArray array = new JSONArray();
        Optional<Plan> planOptional = planRepository.findById(1);
        if (planOptional.isPresent()) {
            Plan plan = planOptional.get();
            JSONObject object = new JSONObject();
            object.put("name", "日间照料");
            object.put("finishedNum", count1);
            object.put("finishedNumToday", count11);
            object.put("percent", plan.getProject_type0() < 1 ? null : (float) Math.round(((float) count1 / plan.getProject_type0()) * 100) / 100);
            array.add(object);

            JSONObject object1 = new JSONObject();
            object1.put("name", "辅助性就业");
            object1.put("finishedNum", count2);
            object1.put("finishedNumToday", count21);
            object1.put("percent", plan.getProject_type0() < 1 ? null : (float) Math.round(((float) count2 / plan.getProject_type0()) * 100) / 100);
            array.add(object1);

            JSONObject object2 = new JSONObject();
            object2.put("name", "康复服务");
            object2.put("finishedNum", count3);
            object2.put("finishedNumToday", count31);
            object2.put("percent", plan.getProject_type0() < 1 ? null : (float) Math.round(((float) count3 / plan.getProject_type0()) * 100) / 100);
            array.add(object2);

            JSONObject object3 = new JSONObject();
            object3.put("name", "文体活动");
            object3.put("finishedNum", count4);
            object3.put("finishedNumToday", count41);
            object3.put("percent", plan.getProject_type0() < 1 ? null : (float) Math.round(((float) count4 / plan.getProject_type0()) * 100) / 100);
            array.add(object3);

            JSONObject object4 = new JSONObject();
            object4.put("name", "学习培训");
            object4.put("finishedNum", count5);
            object4.put("finishedNumToday", count51);
            object4.put("percent", plan.getProject_type0() < 1 ? null : (float) Math.round(((float) count5 / plan.getProject_type0()) * 100) / 100);
            array.add(object4);

            JSONObject object5 = new JSONObject();
            object5.put("name", "志愿服务");
            object5.put("finishedNum", count6);
            object5.put("finishedNumToday", count61);
            object5.put("percent", plan.getProject_type0() < 1 ? null : (float) Math.round(((float) count6 / plan.getProject_type0()) * 100) / 100);
            array.add(object5);

            JSONObject object6 = new JSONObject();
            object6.put("name", "其他");
            object6.put("finishedNum", count7);
            object6.put("finishedNumToday", count71);
            object6.put("percent", plan.getProject_type0() < 1 ? null : (float) Math.round(((float) count7 / plan.getProject_type0()) * 100) / 100);
            array.add(object6);

        } else {
            JSONObject object = new JSONObject();
            object.put("name", "日间照料");
            object.put("finishedNum", count1);
            object.put("finishedNumToday", count11);
            object.put("percent", null);
            array.add(object);

            JSONObject object1 = new JSONObject();
            object1.put("name", "辅助性就业");
            object1.put("finishedNum", count2);
            object1.put("finishedNumToday", count21);
            object1.put("percent", null);
            array.add(object1);

            JSONObject object2 = new JSONObject();
            object2.put("name", "康复服务");
            object2.put("finishedNum", count3);
            object2.put("finishedNumToday", count31);
            object2.put("percent", null);
            array.add(object2);

            JSONObject object3 = new JSONObject();
            object3.put("name", "文体活动");
            object3.put("finishedNum", count4);
            object3.put("finishedNumToday", count41);
            object3.put("percent", null);
            array.add(object3);

            JSONObject object4 = new JSONObject();
            object4.put("name", "学习培训");
            object4.put("finishedNum", count5);
            object4.put("finishedNumToday", count51);
            object4.put("percent", null);
            array.add(object4);

            JSONObject object5 = new JSONObject();
            object5.put("name", "志愿服务");
            object5.put("finishedNum", count6);
            object5.put("finishedNumToday", count61);
            object5.put("percent", null);
            array.add(object5);

            JSONObject object6 = new JSONObject();
            object6.put("name", "其他");
            object6.put("finishedNum", count7);
            object6.put("finishedNumToday", count71);
            object6.put("percent", null);
            array.add(object6);
        }
        return RESCODE.SUCCESS.getJSONRES(array);
    }

    public JSONObject getProjectList(Integer organizationId) {
        List<Project> projectList = projectRepository.findByOrganizationAndStatus(organizationId, 1);
        List<com.hd.home_disabled.model.dto.Project> projectList2 = new ArrayList<>();
        for (Project project :
                projectList) {
            projectList2.add(getModel(project));
        }
        return RESCODE.SUCCESS.getJSONRES(projectList2);
    }

    public void updateClockInData() {
        List<ProjectUserDetail> projectUserDetailList = projectUserDetailRepository.findAll();
        for (ProjectUserDetail o :
                projectUserDetailList) {
            o.setOrganizationId(o.getProject().getOrganization().getId());
        }
    }

    public JSONObject numberOfServicePerMonth(Integer projectId,String month, byte type){
        Date today = new Date();
        Date date = StringUtil.getDate(month);
        if (date.after(today)) {
            //查询时间在当前时间之后
            JSONObject object = new JSONObject();
            object.put("result", 0);
            return RESCODE.SUCCESS.getJSONRES(object);
        } else {
            //查询时间在当前时间之前
            //判断时间是否为月初
            if (date.getDate() != 1 || date.getHours()!=0|| date.getMinutes()!=0||date.getSeconds()!=0) {
                return RESCODE.FAILURE.getJSONRES("请使用月初时间");
            }
            //判断是否为当月
            Date end = null;
            if (date.getMonth() == today.getMonth()) {
                end = new Date();
            } else {
                end = (Date) date.clone();
                end.setMonth(end.getMonth()+1);
            }
            long result = 0;
            if (type==0){
                result = projectUserDetailRepository.countByProjectAndStartBetween(projectId,date,end);
            }else{
                result = projectUserDetailRepository.countDistinctByProjectAndStartBetween(projectId,date,end);
            }
            JSONObject object = new JSONObject();
            object.put("result", result);
            return RESCODE.SUCCESS.getJSONRES(object);
        }
    }
}
