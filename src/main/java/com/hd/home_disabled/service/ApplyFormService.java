package com.hd.home_disabled.service;

import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.Admin;
import com.hd.home_disabled.entity.ApplyForm;
import com.hd.home_disabled.entity.Organization;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.repository.AdminRepository;
import com.hd.home_disabled.repository.ApplyFormRepository;
import com.hd.home_disabled.repository.OrganizationRepository;
import com.hd.home_disabled.utils.PageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName ApplyFormService
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/9 17:17
 * @Version
 */
@Service
@Transactional
public class ApplyFormService {
    private final ApplyFormRepository applyFormRepository;
    private final OrganizationRepository organizationRepository;
    private final AdminRepository adminRepository;


    public ApplyFormService(ApplyFormRepository applyFormRepository, OrganizationRepository organizationRepository, AdminRepository adminRepository) {
        this.applyFormRepository = applyFormRepository;
        this.organizationRepository = organizationRepository;
        this.adminRepository = adminRepository;
    }


    //model-->entity
    ApplyForm getEntity(com.hd.home_disabled.model.dto.ApplyForm applyForm) {
        ApplyForm applyForm1 = new ApplyForm();
        if (applyForm.getId() != null) applyForm1.setId(applyForm.getId());
        Organization organization = new Organization();
        organization.setId(applyForm.getOrganizationId());
        applyForm1.setOrganization(organization);

        applyForm1.setNumOfEligibleDayNursery(applyForm.getNumOfEligibleDayNursery());
        applyForm1.setNumOfEligibleBoardingNursery(applyForm.getNumOfEligibleBoardingNursery());

        applyForm1.setSubsidyFundForDayNursery(applyForm.getSubsidyFundForDayNursery());
        applyForm1.setSubsidyFundForBoardingNursery(applyForm.getSubsidyFundForBoardingNursery());
        applyForm1.setLocalInvestmentOfLastYear(applyForm.getLocalInvestmentOfLastYear());
        applyForm1.setTotalSubsidyFunds(applyForm.getTotalSubsidyFunds());
        applyForm1.setNursingList(applyForm.getNursingList());
        applyForm1.setLowIncomeCertificate(applyForm.getLowIncomeCertificate());

        applyForm1.setReasonForRegression(applyForm.getReasonForRegression());
        applyForm1.setStatus(applyForm.getStatus());

        Admin admin = new Admin();
        admin.setId(applyForm.getAdminId());
        applyForm1.setAdmin(admin);
        return applyForm1;
    }

    //entity-->model
    com.hd.home_disabled.model.dto.ApplyForm getModel(ApplyForm applyForm) {
        com.hd.home_disabled.model.dto.ApplyForm applyForm1 = new com.hd.home_disabled.model.dto.ApplyForm();
        applyForm1.setId(applyForm.getId());
        applyForm1.setOrganizationName(applyForm.getOrganization().getName());
        applyForm1.setRegistrationTime(applyForm.getOrganization().getRegistrationTime());
        applyForm1.setAddress(applyForm.getOrganization().getDetailedAddress());
        applyForm1.setPersonInCharge(applyForm.getOrganization().getPersonInCharge());
        applyForm1.setArea(applyForm.getOrganization().getArea());
        applyForm1.setBedNum(applyForm.getOrganization().getBedNum());
        applyForm1.setNature(applyForm.getOrganization().getNature());
        applyForm1.setAsylumLaborProjects(applyForm.getOrganization().getAsylumLaborProjects());

        applyForm1.setSubsidyFundForDayNursery(applyForm.getSubsidyFundForDayNursery());
        applyForm1.setSubsidyFundForBoardingNursery(applyForm.getSubsidyFundForBoardingNursery());
        applyForm1.setLocalInvestmentOfLastYear(applyForm.getLocalInvestmentOfLastYear());
        applyForm1.setTotalSubsidyFunds(applyForm.getTotalSubsidyFunds());
        applyForm1.setNursingList(applyForm.getNursingList());
        applyForm1.setLowIncomeCertificate(applyForm.getLowIncomeCertificate());
        applyForm1.setReasonForRegression(applyForm.getReasonForRegression());
        applyForm1.setStatus(applyForm.getStatus());

        applyForm1.setAdminName(applyForm.getOrganization().getName());
        applyForm1.setCreateTime(applyForm.getCreateTime());
        applyForm1.setLastModifyTime(applyForm.getModifyTime());
        return applyForm1;
    }

    /**
     * 新建或修改补贴申请
     *
     * @param applyForm 申请表模型
     * @return 结果
     */
    public JSONObject saveAndFlush(com.hd.home_disabled.model.dto.ApplyForm applyForm) {
        ApplyForm applyForm1 = getEntity(applyForm);
        if (applyForm1.getOrganization() != null &&
                applyForm1.getOrganization().getId() != null &&
                organizationRepository.findByIdAndStatus(applyForm1.getOrganization().getId(), 1).isPresent()) {
            Organization organization = organizationRepository.getOne(applyForm1.getOrganization().getId());
            if (applyForm1.getAdmin() != null &&
                    applyForm1.getAdmin().getId() != null &&
                    adminRepository.existsById(applyForm1.getAdmin().getId())) {
                if (applyForm.getId() == null) {
                    //申请id不存在，表示新建数据
                    if (organization.getApplySum() != null) {
                        organization.setApplySum(organization.getApplySum() + 1);
                    } else {
                        organization.setApplySum(1);
                    }
                }
                applyForm1.setStatus(1);
                ApplyForm applyForm2 = applyFormRepository.saveAndFlush(applyForm1);
                return RESCODE.SUCCESS.getJSONRES(getModel(applyForm2));
            }
            return RESCODE.ADMIN_ID_NOT_EXIST.getJSONRES();
        }
        return RESCODE.ORGANIZATION_ID_NOT_EXIST.getJSONRES();
    }

    /**
     * 删除申请表
     * @param id 申请数据id
     * @return 结果
     */
    public JSONObject delete(Integer id) {
        Optional<ApplyForm> applyFormOptional = applyFormRepository.findByIdAndStatus(id,1);
        if (applyFormOptional.isPresent()){
            ApplyForm applyForm = applyFormOptional.get();
            Optional<Organization> organizationOptional = organizationRepository.findByIdAndStatus(applyForm.getOrganization().getId(),1);
            if (organizationOptional.isPresent()) {
                Organization organization = organizationOptional.get();
                if (organization.getApplySum() != null && organization.getApplySum() > 0) {
                    organization.setApplySum(organization.getApplySum() - 1);
                    organizationRepository.saveAndFlush(organization);
                }
            }
            applyForm.setStatus(0);
            return RESCODE.SUCCESS.getJSONRES();
        }
        return RESCODE.APPLY_FORM_NOT_EXIST.getJSONRES();
    }

    /**
     * 单个申请表查询
     * @param id 申请表id
     * @return 结果
     */
    public JSONObject getById(Integer id){
        Optional<ApplyForm> applyFormOptional = applyFormRepository.findByIdAndStatus(id,1);
        if (applyFormOptional.isPresent()){
            return RESCODE.SUCCESS.getJSONRES(getModel(applyFormOptional.get()));
        }
        return RESCODE.APPLY_FORM_NOT_EXIST.getJSONRES();
    }

    /**
     * 申请表按机构分页查询
     * @param organizationId 所属机构id
     * @param page  页码
     * @param number    每页显示数量
     * @param sorts 排序条件
     * @return 结果
     */
    public JSONObject getPageByOrganizationId(Integer organizationId, Integer page, Integer number, String sorts){
        Pageable pageable = PageUtils.getPage(page, number, sorts);
        Page<ApplyForm> applyFormPage = applyFormRepository.findByOrganizationAndStatus(organizationId,1,pageable);
        List<com.hd.home_disabled.model.dto.ApplyForm> applyFormList = new ArrayList<>();
        for (ApplyForm applyForm:
                applyFormPage.getContent()) {
            applyFormList.add(getModel(applyForm));
        }
        return RESCODE.SUCCESS.getJSONRES(applyFormList,applyFormPage.getTotalPages(),applyFormPage.getTotalElements());
    }
}
