package com.hd.home_disabled.service;

import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.Admin;
import com.hd.home_disabled.entity.Organization;
import com.hd.home_disabled.entity.dictionary.NatureOfHousingPropertyRight;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.repository.AdminRepository;
import com.hd.home_disabled.repository.NatureOfHousingPropertyRightRepository;
import com.hd.home_disabled.repository.OrganizationRepository;
import com.hd.home_disabled.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName OrganizationService
 * @Description 机构Serivice
 * @Author pyt
 * @Date 2019/7/4 13:52
 * @Version
 */
@Service
@Transactional
public class OrganizationService {
    private final NatureOfHousingPropertyRightRepository natureOfHousingPropertyRightRepository;

    private final OrganizationRepository organizationRepository;

    private final AdminRepository adminRepository;

    public OrganizationService(NatureOfHousingPropertyRightRepository natureOfHousingPropertyRightRepository, OrganizationRepository organizationRepository, AdminRepository adminRepository) {
        this.natureOfHousingPropertyRightRepository = natureOfHousingPropertyRightRepository;
        this.organizationRepository = organizationRepository;
        this.adminRepository = adminRepository;
    }

    //model-->entity
    Organization dealWithData(com.hd.home_disabled.model.dto.Organization organization){
        Organization org = new Organization();
        if (organization.getId()!=null) org.setId(organization.getId());
        org.setName(organization.getName());
        org.setRegistrationTime(organization.getRegistrationTime());
        org.setRegistrationCertificateNumber(organization.getRegistrationCertificateNumber());
        org.setRegistrationDepartment(organization.getRegistrationDepartment());
        org.setNature(organization.getNature());

        NatureOfHousingPropertyRight natureOfHousingPropertyRight = new NatureOfHousingPropertyRight();
        natureOfHousingPropertyRight.setId(organization.getNatureOfHousingPropertyRightId());
        org.setNatureOfHousingPropertyRight(natureOfHousingPropertyRight);
        org.setArea(organization.getArea());
        org.setBedNum(organization.getBedNum());
        org.setAsylumLaborProjects(organization.getAsylumLaborProjects());

        org.setAddressId(organization.getAddressId());
        org.setDetailedAddress(organization.getDetailedAddress());
        org.setProvince(organization.getProvince());
        org.setCity(organization.getCity());
        org.setDistrict(organization.getDistrict());
        org.setBlock(organization.getBlock());

        org.setPersonInCharge(organization.getPersonInCharge());
        org.setContactNumber(organization.getContactNumber());
        org.setEducation(organization.getEducation());
        org.setGender(organization.getGender());
        org.setBirthMonth(organization.getBirthMonth());

        org.setCertification(organization.getCertification());
        org.setOpenBankAccountPermitCertificate(organization.getOpenBankAccountPermitCertificate());
        org.setFacilitiesPictures(organization.getFacilitiesPictures());
        org.setManagementSystem(organization.getManagementSystem());
        org.setStaffList(organization.getStaffList());

        Admin admin = new Admin();
        admin.setId(organization.getAdminId());
        org.setAdmin(admin);

        return org;
    }

    //entity-->model
    com.hd.home_disabled.model.dto.Organization dealWithOrganization(Organization organization){
        com.hd.home_disabled.model.dto.Organization org = new com.hd.home_disabled.model.dto.Organization();
        org.setId(organization.getId());
        org.setAddressId(organization.getAddressId());
        org.setAdminName(organization.getAdmin().getName());
        org.setArea(organization.getArea());
        org.setAsylumLaborProjects(organization.getAsylumLaborProjects());
        org.setAverageTime(organization.getAverageTime());
        org.setBedNum(organization.getBedNum());
        org.setBirthMonth(organization.getBirthMonth());
        org.setBlock(organization.getBlock());
        org.setCertification(organization.getCertification());
        org.setCity(organization.getCity());
        org.setContactNumber(organization.getContactNumber());
        org.setDetailedAddress(organization.getDetailedAddress());
        org.setDistrict(organization.getDistrict());
        org.setFacilitiesPictures(organization.getFacilitiesPictures());
        org.setEducation(organization.getEducation());
        org.setGender(organization.getGender());
        org.setManagementSystem(organization.getManagementSystem());
        org.setName(organization.getName());
        org.setNature(organization.getNature());
        org.setNatureOfHousingPropertyRight(organization.getNatureOfHousingPropertyRight().getName());
        org.setOpenBankAccountPermitCertificate(organization.getOpenBankAccountPermitCertificate());
        org.setPersonCountSum(organization.getPersonCountSum());
        org.setPersonInCharge(organization.getPersonInCharge());
        org.setPersonTimeSum(organization.getPersonTimeSum());
        org.setProjectSum(organization.getProjectSum());
        org.setProvince(organization.getProvince());
        org.setRegistrationCertificateNumber(organization.getRegistrationCertificateNumber());
        org.setRegistrationDepartment(organization.getRegistrationDepartment());
        org.setRegistrationTime(organization.getRegistrationTime());
        org.setStaffList(organization.getStaffList());
        org.setTotalTimeSum(organization.getTotalTimeSum());
        org.setAdminName(organization.getAdmin().getName());
        org.setCreateTime(organization.getCreateTime());
        org.setLastModifyTime(organization.getModifyTime());
        return org;
    }

    /**
     * 添加或修改机构
     * 修改机构--数据中需要id
     * @param organization 机构
     * @return 添加或修改结果
     */
    public JSONObject saveAndFlush(com.hd.home_disabled.model.dto.Organization organization) {
        Organization org = dealWithData(organization);
        if (org.getAdmin() != null &&
                org.getAdmin().getId()!=null &&
                adminRepository.existsById(org.getAdmin().getId())) {
            if (org.getNatureOfHousingPropertyRight() != null &&
                    org.getNatureOfHousingPropertyRight().getId() != null &&
                    natureOfHousingPropertyRightRepository.existsById(org.getNatureOfHousingPropertyRight().getId())){
                org.setStatus(1);
                Organization organization1 = organizationRepository.saveAndFlush(org);
                return RESCODE.SUCCESS.getJSONRES(dealWithOrganization(organization1));
            }
            return RESCODE.NATURE_OF_HOUSING_PROPERTY_RIGHT_ID_NOT_EXIST.getJSONRES();
        }
        return RESCODE.ADMIN_ID_NOT_EXIST.getJSONRES();
    }

    /**
     * 删除机构
     * @param id 机构id
     * @return  结果
     */
    public JSONObject delete(Integer id) {
        Optional<Organization> optional = organizationRepository.findById(id);
        if (optional.isPresent()) {
            optional.get().setStatus(0);
            return RESCODE.SUCCESS.getJSONRES();
        }
        return RESCODE.ORGANIZATION_ID_NOT_EXIST.getJSONRES();
    }

    /**
     * 查询单个机构
     *
     * @param id 机构id
     * @return  结果
     */
    public JSONObject getById(Integer id) {
        Optional<Organization> optional = organizationRepository.findById(id);
        if (optional.isPresent()) {
            com.hd.home_disabled.model.dto.Organization organization = dealWithOrganization(optional.get());
            return RESCODE.SUCCESS.getJSONRES(organization);
        }
        return RESCODE.ORGANIZATION_ID_NOT_EXIST.getJSONRES();
    }

    /**
     * 查询区机构列表
     *
     * @param district 区名
     * @return 结果
     */
    public JSONObject getListByDistrict(String district) {
        List<Organization> organizationList = organizationRepository.findByDistrictAndStatus(district, 1);
        List<com.hd.home_disabled.model.dto.Organization> organizationList1 = new ArrayList<>();
        for (Organization organization:
                organizationList) {
            com.hd.home_disabled.model.dto.Organization org = dealWithOrganization(organization);
            organizationList1.add(org);
        }
        return RESCODE.SUCCESS.getJSONRES(organizationList1);
    }

    /**
     * 机构分页查询
     * @param district 区名
     * @param page  页码
     * @param number    每页显示数量
     * @param sorts 排序参数
     * @return  结果
     */
    public JSONObject getPageByDistrict(String district, Integer page, Integer number, String sorts) {
        Pageable pageable = PageUtils.getPage(page,number,sorts);
        Page<Organization> organizationPage = organizationRepository.findByDistrictAndStatus(district, 1, pageable);
        List<Organization> organizationList = organizationPage.getContent();
        List<com.hd.home_disabled.model.dto.Organization> organizationList1 = new ArrayList<>();
        for (Organization organization:
                organizationList) {
            com.hd.home_disabled.model.dto.Organization org = dealWithOrganization(organization);
            organizationList1.add(org);
        }
        return RESCODE.SUCCESS.getJSONRES(organizationList1, organizationPage.getTotalPages(), organizationPage.getTotalElements());
    }
}
