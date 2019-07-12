package com.hd.home_disabled.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.Admin;
import com.hd.home_disabled.entity.Organization;
import com.hd.home_disabled.entity.dictionary.NatureOfHousingPropertyRight;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.repository.AdminRepository;
import com.hd.home_disabled.repository.NatureOfHousingPropertyRightRepository;
import com.hd.home_disabled.repository.OrganizationRepository;
import com.hd.home_disabled.utils.ExcelUtils;
import com.hd.home_disabled.utils.PageUtils;
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
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    //model-->entity
    Organization dealWithData(com.hd.home_disabled.model.dto.Organization organization) {
        Organization org = new Organization();
        if (organization.getId() != null) org.setId(organization.getId());
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
    com.hd.home_disabled.model.dto.Organization dealWithOrganization(Organization organization) {
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
        org.setApplySum(organization.getApplySum());
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
     *
     * @param organization 机构
     * @return 添加或修改结果
     */
    public JSONObject saveAndFlush(com.hd.home_disabled.model.dto.Organization organization) {
        Organization org = dealWithData(organization);
        if (org.getAdmin() != null &&
                org.getAdmin().getId() != null &&
                adminRepository.existsById(org.getAdmin().getId())) {
            if (org.getNatureOfHousingPropertyRight() != null &&
                    org.getNatureOfHousingPropertyRight().getId() != null &&
                    natureOfHousingPropertyRightRepository.existsById(org.getNatureOfHousingPropertyRight().getId())) {
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
     *
     * @param id 机构id
     * @return 结果
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
     * @return 结果
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
        for (Organization organization :
                organizationList) {
            com.hd.home_disabled.model.dto.Organization org = dealWithOrganization(organization);
            organizationList1.add(org);
        }
        return RESCODE.SUCCESS.getJSONRES(organizationList1);
    }

    public List<JSONArray> getListsByDistrict(String district) {
        List<JSONArray> jsonArray = new ArrayList<>();
        List<Organization> organizationList = organizationRepository.findByDistrictAndStatus(district, 1);
        for (Organization o :
                organizationList) {
            JSONArray array = new JSONArray();
            JSONObject object = new JSONObject();
            object.put("name", o.getName() == null ? 0 : o.getName());
            array.add(object);
            JSONObject object1 = new JSONObject();
            object1.put("registrationTime", o.getRegistrationTime() == null ? "" : o.getRegistrationTime());
            array.add(object1);
            JSONObject object2 = new JSONObject();
            object2.put("registrationCertificateNumber", o.getRegistrationCertificateNumber() == null ? "" : o.getRegistrationCertificateNumber());
            array.add(object2);
            JSONObject object3 = new JSONObject();
            object3.put("registrationDepartment", o.getRegistrationDepartment() == null ? "" : o.getRegistrationDepartment());
            array.add(object3);
            JSONObject object4 = new JSONObject();
            object4.put("nature", o.getNature() == null ? "" : o.getNature());
            array.add(object4);
            JSONObject object5 = new JSONObject();
            if (o.getNatureOfHousingPropertyRight() != null && o.getNatureOfHousingPropertyRight().getName() != null)
                object5.put("natureOfHousingPropertyRight", o.getNatureOfHousingPropertyRight().getName());
            else object5.put("natureOfHousingPropertyRight", "");
            array.add(object5);
            JSONObject object7 = new JSONObject();
            object7.put("area", o.getArea() == null ? 0 : o.getArea());
            array.add(object7);
            JSONObject object8 = new JSONObject();
            object8.put("bedNum", o.getBedNum() == null ? 0 : o.getBedNum());
            array.add(object8);
            JSONObject object9 = new JSONObject();
            object9.put("asylumLaborProjects", o.getAsylumLaborProjects() == null ? "" : o.getAsylumLaborProjects());
            array.add(object9);
            JSONObject object10 = new JSONObject();
            object10.put("detailedAddress", o.getDetailedAddress() == null ? "" : o.getDetailedAddress());
            array.add(object10);
            JSONObject object11 = new JSONObject();
            object11.put("personInCharge", o.getPersonInCharge() == null ? "" : o.getPersonInCharge());
            array.add(object11);
            JSONObject object12 = new JSONObject();
            object12.put("gender", o.getGender() == null ? "" : o.getGender());
            array.add(object12);
            JSONObject object13 = new JSONObject();
            array.add(object13);
            object13.put("birthMonth", o.getBirthMonth() == null ? "" : o.getBirthMonth());
            JSONObject object14 = new JSONObject();
            object14.put("education", o.getEducation() == null ? "" : o.getEducation());
            array.add(object14);
            JSONObject object15 = new JSONObject();
            object15.put("certification", o.getCertification() == null ? "" : o.getCertification());
            array.add(object15);
            JSONObject object16 = new JSONObject();
            object16.put("openBankAccountPermitCertificate", o.getOpenBankAccountPermitCertificate() == null ? "" : o.getOpenBankAccountPermitCertificate());
            array.add(object16);
            JSONObject object17 = new JSONObject();
            object17.put("facilitiesPictures", o.getFacilitiesPictures() == null ? "" : o.getFacilitiesPictures());
            array.add(object17);
            JSONObject object18 = new JSONObject();
            object18.put("staffList", o.getStaffList() == null ? "" : o.getStaffList());
            array.add(object18);
            JSONObject object19 = new JSONObject();
            object19.put("managementSystem", o.getManagementSystem() == null ? "" : o.getManagementSystem());
            array.add(object19);
            JSONObject object20 = new JSONObject();
            object20.put("projectSum", o.getProjectSum() == null ? 0 : o.getProjectSum());
            array.add(object20);
            JSONObject object21 = new JSONObject();
            object21.put("personCountSum", o.getPersonCountSum() == null ? 0 : o.getPersonCountSum());
            array.add(object21);
            JSONObject object22 = new JSONObject();
            object22.put("personTimeSum", o.getPersonTimeSum() == null ? 0 : o.getPersonTimeSum());
            array.add(object22);
            JSONObject object23 = new JSONObject();
            object23.put("totalTimeSum", o.getTotalTimeSum() == null ? 0 : o.getTotalTimeSum());
            array.add(object23);
            JSONObject object24 = new JSONObject();
            object24.put("averageTime", o.getAverageTime() == null ? 0 : o.getAverageTime());
            array.add(object24);
            JSONObject object25 = new JSONObject();
            if (o.getAdmin() != null && o.getAdmin().getName() != null)
                object25.put("adminName", o.getAdmin().getName());
            else object25.put("adminName", "");
            array.add(object25);
            JSONObject object26 = new JSONObject();
            object26.put("createTime", o.getCreateTime()==null?"":o.getCreateTime());
            array.add(object26);
            JSONObject object27 = new JSONObject();
            object27.put("lastModifyTime", o.getModifyTime()==null?"":o.getModifyTime());
            array.add(object27);
            jsonArray.add(array);
        }
        return jsonArray;
    }

    /**
     * 机构分页查询
     *
     * @param district 区名
     * @param page     页码
     * @param number   每页显示数量
     * @param sorts    排序参数
     * @return 结果
     */
    public JSONObject getPageByDistrict(String district, Integer page, Integer number, String sorts) {
        Pageable pageable = PageUtils.getPage(page, number, sorts);
        Page<Organization> organizationPage = organizationRepository.findByDistrictAndStatus(district, 1, pageable);
        List<Organization> organizationList = organizationPage.getContent();
        List<com.hd.home_disabled.model.dto.Organization> organizationList1 = new ArrayList<>();
        for (Organization organization :
                organizationList) {
            com.hd.home_disabled.model.dto.Organization org = dealWithOrganization(organization);
            organizationList1.add(org);
        }
        return RESCODE.SUCCESS.getJSONRES(organizationList1, organizationPage.getTotalPages(), organizationPage.getTotalElements());
    }

    public void exportExcel(String district,HttpServletRequest request, HttpServletResponse response) {
        String[] columnNames = new String[]{"机构名称", "登记注册时间", "注册证书编号", "注册部门", "机构性质",
                "房屋产权性质", "机构面积", "床位数", "庇护性劳动项目", "地址",
                "负责人", "性别", "出生年月", "文化程度", "营业执照或登记证书",
                "银行开户许可", "门头及室内功能区域、无障碍设施", "专职工作人员名单", "管理制度", "服务项目总数",
                "总服务人数", "总服务人次", "总服务时长", "平均服务时长", "提交人",
                "提交时间", "更新时间"};
        String fileName = "OrganizationList"+"_"+sdf.format(new Date())+".xls";
        ExcelUtils.exportExcel(fileName,columnNames, getListsByDistrict(district), request, response);
    }
}
