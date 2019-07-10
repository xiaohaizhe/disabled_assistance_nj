package com.hd.home_disabled.service;

import com.alibaba.fastjson.JSON;
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
import com.hd.home_disabled.utils.PageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
                    if (organization.getProjectSum() != null) {
                        organization.setProjectSum(organization.getProjectSum() + 1);
                    } else {
                        organization.setProjectSum(1);
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
        Optional<Project> projectOptional = projectRepository.findByIdAndStatus(id,1);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            Optional<Organization> organizationOptional = organizationRepository.findByIdAndStatus(project.getOrganization().getId(),1);
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

    public JSONObject getById(Integer id){
        Optional<Project> projectOptional = projectRepository.findByIdAndStatus(id,1);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            return RESCODE.SUCCESS.getJSONRES(getModel(project));
        }
        return RESCODE.PROJECT_ID_NOT_EXIST.getJSONRES();
    }

    public JSONObject getPageByOrganizationId(Integer organizationId, Integer page, Integer number,String sorts){
        Pageable pageable = PageUtils.getPage(page, number, sorts);
        Page<Project> projectPage = projectRepository.findByOrganizationAndStatus(organizationId,1,pageable);
        List<com.hd.home_disabled.model.dto.Project> projectList = new ArrayList<>();
        for (Project project :
                projectPage) {
            projectList.add(getModel(project));
        }
        return RESCODE.SUCCESS.getJSONRES(projectList, projectPage.getTotalPages(),projectPage.getTotalElements());
    }
}
