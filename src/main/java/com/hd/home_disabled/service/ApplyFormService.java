package com.hd.home_disabled.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.*;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.repository.*;
import com.hd.home_disabled.utils.ExcelUtils;
import com.hd.home_disabled.utils.PageUtils;
import com.test.disabled.App;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private final LowIncomeCertificateRepository lowIncomeCertificateRepository;
    private final ApplyFormUserRepository applyFormUserRepository;
    private final ApplyFormRepository applyFormRepository;
    private final OrganizationRepository organizationRepository;
    private final AdminRepository adminRepository;
    public ApplyFormService(ApplyFormRepository applyFormRepository, OrganizationRepository organizationRepository, AdminRepository adminRepository, ApplyFormUserRepository applyFormUserRepository, LowIncomeCertificateRepository lowIncomeCertificateRepository) {
        this.applyFormRepository = applyFormRepository;
        this.organizationRepository = organizationRepository;
        this.adminRepository = adminRepository;
        this.applyFormUserRepository = applyFormUserRepository;
        this.lowIncomeCertificateRepository = lowIncomeCertificateRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(ApplyFormService.class);

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static String[] columnNames = new String[]{"审核状态", "机构名称", "登记注册时间", "地址", "负责人",
            "机构面积", "床位数", "机构性质", "庇护性劳动项目", "符合条件的全托人数",
            "符合条件的日托人数", "申请机构全托运营补贴资金总额", "申请机构日托运营补贴资金总额", "上年当地资金投入情况", "申请托养机构运营补贴资金总额合计",
            "托养残疾人名单", "低保或其他低收入证明", "提交人", "提交时间", "更新时间"};

    //model-->entity
    ApplyForm getEntity(com.hd.home_disabled.model.dto.ApplyForm applyForm) {
        logger.info("补贴申请表：model-->entity");
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
//        applyForm1.setLowIncomeCertificate(applyForm.getLowIncomeCertificate());


        applyForm1.setReasonForRegression(applyForm.getReasonForRegression());
        applyForm1.setStatus(applyForm.getStatus());

        Admin admin = new Admin();
        admin.setId(applyForm.getAdminId());
        applyForm1.setAdmin(admin);
        return applyForm1;
    }

    //entity-->model
    com.hd.home_disabled.model.dto.ApplyForm getModel(ApplyForm applyForm) {
        logger.info("补贴申请表：entity-->model");
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

//        applyForm1.setUserList(applyForm.getUserList());

        applyForm1.setSubsidyFundForDayNursery(applyForm.getSubsidyFundForDayNursery());
        applyForm1.setSubsidyFundForBoardingNursery(applyForm.getSubsidyFundForBoardingNursery());
        applyForm1.setLocalInvestmentOfLastYear(applyForm.getLocalInvestmentOfLastYear());
        applyForm1.setTotalSubsidyFunds(applyForm.getTotalSubsidyFunds());
//        applyForm1.setLowIncomeCertificateList(applyForm.getLowIncomeCertificateList());
        applyForm1.setReasonForRegression(applyForm.getReasonForRegression());
        applyForm1.setStatus(applyForm.getStatus());

        applyForm1.setAdminName(applyForm.getOrganization().getName());
        applyForm1.setCreateTime(applyForm.getCreateTime());
        applyForm1.setLastModifyTime(applyForm.getModifyTime());
        return applyForm1;
    }

    JSONArray getApplyFormUserList(List<ApplyFormUser> applyFormUserList){
        JSONArray array = new JSONArray();
        for (ApplyFormUser applyFormUser :applyFormUserList ){
            JSONObject object = new JSONObject();
            object.put("username",applyFormUser.getUsername());
            object.put("disabilityCertificateNumber",applyFormUser.getDisabilityCertificateNumber());
            object.put("address",applyFormUser.getAddress());
            object.put("contactNumber",applyFormUser.getContactNumber());
            object.put("nursingMode",applyFormUser.getNursingMode());
            object.put("subsidies",applyFormUser.getSubsidies());
            object.put("month",applyFormUser.getMonth());
            array.add(object);
        }
        return array;
    }

    JSONArray getLowIncomeCertificateList(List<LowIncomeCertificate> lowIncomeCertificateList){
        JSONArray array = new JSONArray();
        for (LowIncomeCertificate lowIncomeCertificate :lowIncomeCertificateList ){
            JSONObject object = new JSONObject();
            object.put("url",lowIncomeCertificate.getUrl());
            array.add(object);
        }
        return array;
    }



    /**
     * 新建或修改补贴申请
     *
     * @param applyForm 申请表模型
     * @return 结果
     */
    public JSONObject saveAndFlush(com.hd.home_disabled.model.dto.ApplyForm applyForm) {
        logger.info("新建或修改补贴申请");
        logger.info("补贴申请:"+applyForm.toString());
        ApplyForm applyForm1 = getEntity(applyForm);
        if (applyForm1.getOrganization() != null &&
                applyForm1.getOrganization().getId() != null &&
                organizationRepository.findByIdAndStatus(applyForm1.getOrganization().getId(), 1).isPresent()) {
            Organization organization = organizationRepository.getOne(applyForm1.getOrganization().getId());
            logger.info("补贴申请机构方为："+(organization.getName()==null?"":organization.getName()));

            if (applyForm1.getAdmin() != null &&
                    applyForm1.getAdmin().getId() != null &&
                    adminRepository.existsById(applyForm1.getAdmin().getId())) {
                if (applyForm.getId() == null) {
                    logger.info("新建补贴申请");
                    //申请id不存在，表示新建数据
                    if (organization.getApplySum() != null) {
                        logger.info("机构下补贴申请总数加1");
                        organization.setApplySum(organization.getApplySum() + 1);
                    } else {
                        organization.setApplySum(1);
                    }

                    applyForm1.setStatus(1);//待审核
                    ApplyForm applyForm2 = applyFormRepository.saveAndFlush(applyForm1);
                    for (ApplyFormUser user:applyForm.getUserList()){
                        user.setApplyForm(applyForm2);
                        ApplyFormUser applyFormUser = applyFormUserRepository.save(user);
                    }
                    for (LowIncomeCertificate lowIncomeCertificate : applyForm.getLowIncomeCertificateList()){
                        lowIncomeCertificate.setApplyForm(applyForm2);
                        lowIncomeCertificateRepository.save(lowIncomeCertificate);
                    }
                    List<ApplyFormUser> applyFormUsers = applyFormUserRepository.findByApplyForm(applyForm2.getId());
                    List<LowIncomeCertificate> lowIncomeCertificateList = lowIncomeCertificateRepository.findByApplyForm(applyForm2.getId());
                    JSONObject result = new JSONObject();
                    result.put("applyFormUser",getApplyFormUserList(applyFormUsers));
                    result.put("applyForm",getModel(applyForm2));
                    result.put("lowIncomeCertificate",getLowIncomeCertificateList(lowIncomeCertificateList));
                    return RESCODE.SUCCESS.getJSONRES(result);
                }else if (applyForm.getStatus()==1){
                    //id存在且状态为有效，可修改
                    logger.info("修改补贴申请");
                    ApplyForm applyForm2 = applyFormRepository.saveAndFlush(applyForm1);
                    for (ApplyFormUser user:applyForm.getUserList()){
                        user.setApplyForm(applyForm2);
                        Optional<ApplyFormUser> optional =applyFormUserRepository.findByDisabilityCertificateNumberAndApplyForm(
                                user.getDisabilityCertificateNumber(),
                                applyForm2.getId());
                        if (optional.isPresent()){
                            ApplyFormUser applyFormUser = optional.get();
                            user.setId(applyFormUser.getId());
                        }
                        ApplyFormUser applyFormUser = applyFormUserRepository.saveAndFlush(user);
                    }
                    List<LowIncomeCertificate> lowIncomeCertificates = lowIncomeCertificateRepository.findByApplyForm(applyForm2.getId());
                    for (LowIncomeCertificate lowIncomeCertificate:lowIncomeCertificates){
                        lowIncomeCertificateRepository.delete(lowIncomeCertificate);
                    }
                    for (LowIncomeCertificate lowIncomeCertificate : applyForm.getLowIncomeCertificateList()){
                        lowIncomeCertificate.setApplyForm(applyForm2);
                        lowIncomeCertificateRepository.save(lowIncomeCertificate);
                    }
                    List<ApplyFormUser> applyFormUsers = applyFormUserRepository.findByApplyForm(applyForm2.getId());
                    List<LowIncomeCertificate> lowIncomeCertificateList = lowIncomeCertificateRepository.findByApplyForm(applyForm2.getId());
                    JSONObject result = new JSONObject();
                    result.put("applyFormUser",getApplyFormUserList(applyFormUsers));
                    result.put("applyForm",getModel(applyForm2));
                    result.put("lowIncomeCertificate",getLowIncomeCertificateList(lowIncomeCertificateList));
                    return RESCODE.SUCCESS.getJSONRES(result);
                }else{
                    return RESCODE.APPLY_FORM_CANT_MODIFY.getJSONRES();
                }

            }
            return RESCODE.ADMIN_ID_NOT_EXIST.getJSONRES();
        }
        return RESCODE.ORGANIZATION_ID_NOT_EXIST.getJSONRES();
    }

    /**
     * 删除申请表
     *
     * @param id 申请数据id
     * @return 结果
     */
    public JSONObject delete(Integer id) {
        logger.info("进入补贴申请删除");
        Optional<ApplyForm> applyFormOptional = applyFormRepository.findByIdAndStatusGreaterThanEqual(id, 1);
        if (applyFormOptional.isPresent()) {
            ApplyForm applyForm = applyFormOptional.get();
            Optional<Organization> organizationOptional = organizationRepository.findByIdAndStatus(applyForm.getOrganization().getId(), 1);
            if (organizationOptional.isPresent()) {
                Organization organization = organizationOptional.get();
                if (organization.getApplySum() != null && organization.getApplySum() > 0) {
                    logger.info("补贴申请所属机构："+(organization.getName()==null?"":organization.getName())+"补贴申请总数减1");
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
     *
     * @param id 申请表id
     * @return 结果
     */
    public JSONObject getById(Integer id) {
        logger.info("根据id:"+id+"查询补贴申请");
        Optional<ApplyForm> applyFormOptional = applyFormRepository.findByIdAndStatusGreaterThanEqual(id, 1);
        if (applyFormOptional.isPresent()) {
            List<ApplyFormUser> applyFormUserList = applyFormUserRepository.findByApplyForm(id);
            List<LowIncomeCertificate> lowIncomeCertificateList = lowIncomeCertificateRepository.findByApplyForm(id);
            JSONObject result = new JSONObject();
            result.put("applyFormUser",getApplyFormUserList(applyFormUserList));
            result.put("applyForm",getModel(applyFormOptional.get()));
            result.put("lowIncomeCertificate",getLowIncomeCertificateList(lowIncomeCertificateList));
            return RESCODE.SUCCESS.getJSONRES(result);
        }
        logger.info("补贴申请不存在");
        return RESCODE.APPLY_FORM_NOT_EXIST.getJSONRES();
    }
    public ApplyForm getApplyFormById(Integer id) {
        Optional<ApplyForm> applyFormOptional = applyFormRepository.findByIdAndStatusGreaterThanEqual(id, 1);
        return applyFormOptional.get();
    }

    /**
     * 申请表按机构分页查询
     *
     * @param organizationId 所属机构id
     * @param page           页码
     * @param number         每页显示数量
     * @param sorts          排序条件
     * @return 结果
     */
    public JSONObject getPageByOrganizationId(Integer organizationId, Integer page, Integer number, String sorts) {
        logger.info("根据机构id:"+organizationId+"查询补贴申请，结果分页");
        logger.info("按照"+sorts+"进行降序排序");
        logger.info("每页显示："+number+"条数据");
        logger.info("当前为第"+page+"页");
        Pageable pageable = PageUtils.getPage(page, number, sorts);
        Page<ApplyForm> applyFormPage = applyFormRepository.findByOrganizationAndStatus(organizationId, 1, pageable);
        List<com.hd.home_disabled.model.dto.ApplyForm> applyFormList = new ArrayList<>();
        for (ApplyForm applyForm :
                applyFormPage.getContent()) {
            applyFormList.add(getModel(applyForm));
        }
        return RESCODE.SUCCESS.getJSONRES(applyFormList, applyFormPage.getTotalPages(), applyFormPage.getTotalElements());
    }


    public JSONObject changeStatusToApproval(Integer id,Integer adminId){
        Optional<ApplyForm> applyFormOptional=applyFormRepository.findByIdAndStatusGreaterThanEqual(id,1);
        Optional<Admin> adminOptional = adminRepository.findById(adminId);
        if (applyFormOptional.isPresent()){
            if (adminOptional.isPresent()){
                ApplyForm applyForm = applyFormOptional.get();
                applyForm.setAdmin(adminOptional.get());
                applyForm.setStatus(2);
                return RESCODE.SUCCESS.getJSONRES();
            }
            return RESCODE.ADMIN_ID_NOT_EXIST.getJSONRES();
        }
        return RESCODE.APPLY_FORM_NOT_EXIST.getJSONRES();
    }


    public JSONObject changeStatusToRejection(Integer id,Integer adminId,String reason){
        Optional<ApplyForm> applyFormOptional=applyFormRepository.findByIdAndStatusGreaterThanEqual(id,1);
        Optional<Admin> adminOptional = adminRepository.findById(adminId);
        if (applyFormOptional.isPresent()){
            if (adminOptional.isPresent()){
                ApplyForm applyForm = applyFormOptional.get();
                applyForm.setAdmin(adminOptional.get());
                applyForm.setStatus(3);
                applyForm.setReasonForRegression(reason);
                return RESCODE.SUCCESS.getJSONRES();
            }
            return RESCODE.ADMIN_ID_NOT_EXIST.getJSONRES();
        }
        return RESCODE.APPLY_FORM_NOT_EXIST.getJSONRES();
    }

    public JSONObject getPageByDistrict(String district, Integer page, Integer number, String sorts) {
        logger.info("根据区："+district+"查询补贴申请列表，结果分页");
        logger.info("按照"+sorts+"进行降序排序");
        logger.info("每页显示："+number+"条数据");
        logger.info("当前为第"+page+"页");
        Pageable pageable = PageUtils.getPage(page, number, sorts);
        List<Organization> organizationList = organizationRepository.findByDistrictAndStatus(district, 1);
        List<Integer> ids = new ArrayList<>();
        List<com.hd.home_disabled.model.dto.ApplyForm> applyFormList = new ArrayList<>();
        for (Organization organization : organizationList) {
            ids.add(organization.getId());
        }
        Page<ApplyForm> applyFormPage = applyFormRepository.findByOrganizationAndStatus(ids, 1, pageable);
        for (ApplyForm applyForm :
                applyFormPage.getContent()) {
            applyFormList.add(getModel(applyForm));
        }
        return RESCODE.SUCCESS.getJSONRES(applyFormList, applyFormPage.getTotalPages(), applyFormPage.getTotalElements());
    }

    public List<JSONArray> getListsByOrganizationId(Integer organizationId) {
        List<JSONArray> jsonArray = new ArrayList<>();
        List<ApplyForm> applyFormList = applyFormRepository.findByOrganizationAndStatus(organizationId, 1);
        for (ApplyForm applyForm :
                applyFormList) {
            JSONArray array = new JSONArray();
            JSONObject object = new JSONObject();
            object.put("status", applyForm.getStatus() == null ? 0 : applyForm.getStatus());
            array.add(object);
            if (applyForm.getOrganization() != null) {
                JSONObject object1 = new JSONObject();
                if (applyForm.getOrganization().getName() != null)
                    object1.put("organizationName", applyForm.getOrganization().getName());
                else object1.put("organizationName", "");
                array.add(object1);

                JSONObject object2 = new JSONObject();
                if (applyForm.getOrganization().getRegistrationTime() != null)
                    object2.put("registrationTime", applyForm.getOrganization().getRegistrationTime());
                else object2.put("registrationTime", null);
                array.add(object2);

                JSONObject object3 = new JSONObject();
                if (applyForm.getOrganization().getDetailedAddress() != null)
                    object3.put("address", applyForm.getOrganization().getDetailedAddress());
                else object3.put("address", "");
                array.add(object3);

                JSONObject object5 = new JSONObject();
                if (applyForm.getOrganization().getPersonInCharge() != null)
                    object5.put("object5", applyForm.getOrganization().getPersonInCharge());
                else object5.put("object5", "");
                array.add(object5);

                JSONObject object6 = new JSONObject();
                if (applyForm.getOrganization().getArea() != null)
                    object6.put("object6", applyForm.getOrganization().getArea());
                else object6.put("object6", "");
                array.add(object6);

                JSONObject object7 = new JSONObject();
                if (applyForm.getOrganization().getBedNum() != null)
                    object7.put("object7", applyForm.getOrganization().getBedNum());
                else object7.put("object7", "");
                array.add(object7);

                JSONObject object8 = new JSONObject();
                if (applyForm.getOrganization().getNature() != null)
                    object8.put("object8", applyForm.getOrganization().getNature());
                else object8.put("object8", "");
                array.add(object8);

                JSONObject object9 = new JSONObject();
                if (applyForm.getOrganization().getAsylumLaborProjects() != null)
                    object9.put("object9", applyForm.getOrganization().getAsylumLaborProjects());
                else object9.put("object9", "");
                array.add(object9);
            }
            JSONObject object10 = new JSONObject();
            object10.put("object10", applyForm.getNumOfEligibleBoardingNursery() == null ? 0 : applyForm.getNumOfEligibleBoardingNursery());
            array.add(object10);

            JSONObject object11 = new JSONObject();
            object11.put("object11", applyForm.getNumOfEligibleDayNursery() == null ? 0 : applyForm.getNumOfEligibleDayNursery());
            array.add(object11);

            JSONObject object12 = new JSONObject();
            object12.put("object12", applyForm.getSubsidyFundForBoardingNursery() == null ? 0 : applyForm.getSubsidyFundForBoardingNursery());
            array.add(object12);

            JSONObject object13 = new JSONObject();
            object13.put("object13", applyForm.getSubsidyFundForDayNursery() == null ? 0 : applyForm.getSubsidyFundForDayNursery());
            array.add(object13);

            JSONObject object14 = new JSONObject();
            object14.put("object14", applyForm.getLocalInvestmentOfLastYear() == null ? 0 : applyForm.getLocalInvestmentOfLastYear());
            array.add(object14);

            JSONObject object15 = new JSONObject();
            object15.put("object15", applyForm.getTotalSubsidyFunds() == null ? 0 : applyForm.getTotalSubsidyFunds());
            array.add(object15);

            /*JSONObject object16 = new JSONObject();
            object16.put("object16", applyForm.getNursingList() == null ? "" : applyForm.getNursingList());
            array.add(object16);*/

            JSONObject object17 = new JSONObject();
            object17.put("object17", applyForm.getLowIncomeCertificateList() == null ? 0 : applyForm.getLowIncomeCertificateList());
            array.add(object17);

            JSONObject object18 = new JSONObject();
            if (applyForm.getAdmin() != null && applyForm.getAdmin().getName() != null)
                object18.put("adminName", applyForm.getAdmin().getName());
            else object18.put("adminName", "");
            array.add(object18);

            JSONObject object19 = new JSONObject();
            object19.put("createTime", applyForm.getCreateTime() == null ? "" : applyForm.getCreateTime());
            array.add(object19);

            JSONObject object20 = new JSONObject();
            object20.put("lastModifyTime", applyForm.getModifyTime() == null ? "" : applyForm.getModifyTime());
            array.add(object20);

            jsonArray.add(array);
        }
        return jsonArray;
    }

    public List<JSONArray> getListsByDistrict(String distrct) {
        List<JSONArray> jsonArray = new ArrayList<>();
        List<Organization> organizationList = organizationRepository.findByDistrictAndStatus(distrct, 1);
        for (Organization organization : organizationList) {
            List<ApplyForm> applyFormList = applyFormRepository.findByOrganizationAndStatus(organization.getId(), 1);
            for (ApplyForm applyForm :
                    applyFormList) {
                JSONArray array = new JSONArray();
                JSONObject object = new JSONObject();
                object.put("status", applyForm.getStatus() == null ? 0 : applyForm.getStatus());
                array.add(object);
                if (applyForm.getOrganization() != null) {
                    JSONObject object1 = new JSONObject();
                    if (applyForm.getOrganization().getName() != null)
                        object1.put("organizationName", applyForm.getOrganization().getName());
                    else object1.put("organizationName", "");
                    array.add(object1);

                    JSONObject object2 = new JSONObject();
                    if (applyForm.getOrganization().getRegistrationTime() != null)
                        object2.put("registrationTime", applyForm.getOrganization().getRegistrationTime());
                    else object2.put("registrationTime", null);
                    array.add(object2);

                    JSONObject object4 = new JSONObject();
                    if (applyForm.getOrganization().getDetailedAddress() != null)
                        object4.put("object4", applyForm.getOrganization().getDetailedAddress());
                    else object4.put("object4", "");
                    array.add(object4);

                    JSONObject object5 = new JSONObject();
                    if (applyForm.getOrganization().getPersonInCharge() != null)
                        object5.put("object5", applyForm.getOrganization().getPersonInCharge());
                    else object5.put("object5", "");
                    array.add(object5);

                    JSONObject object6 = new JSONObject();
                    if (applyForm.getOrganization().getArea() != null)
                        object6.put("object6", applyForm.getOrganization().getArea());
                    else object6.put("object6", "");
                    array.add(object6);

                    JSONObject object7 = new JSONObject();
                    if (applyForm.getOrganization().getBedNum() != null)
                        object7.put("object7", applyForm.getOrganization().getBedNum());
                    else object7.put("object7", "");
                    array.add(object7);

                    JSONObject object8 = new JSONObject();
                    if (applyForm.getOrganization().getNature() != null)
                        object8.put("object8", applyForm.getOrganization().getNature());
                    else object8.put("object8", "");
                    array.add(object8);

                    JSONObject object9 = new JSONObject();
                    if (applyForm.getOrganization().getAsylumLaborProjects() != null)
                        object9.put("object9", applyForm.getOrganization().getAsylumLaborProjects());
                    else object9.put("object9", "");
                    array.add(object9);
                }
                JSONObject object10 = new JSONObject();
                object10.put("object10", applyForm.getNumOfEligibleBoardingNursery() == null ? 0 : applyForm.getNumOfEligibleBoardingNursery());
                array.add(object10);

                JSONObject object11 = new JSONObject();
                object11.put("object11", applyForm.getNumOfEligibleDayNursery() == null ? 0 : applyForm.getNumOfEligibleDayNursery());
                array.add(object11);

                JSONObject object12 = new JSONObject();
                object12.put("object12", applyForm.getSubsidyFundForBoardingNursery() == null ? 0 : applyForm.getSubsidyFundForBoardingNursery());
                array.add(object12);

                JSONObject object13 = new JSONObject();
                object13.put("object13", applyForm.getSubsidyFundForDayNursery() == null ? 0 : applyForm.getSubsidyFundForDayNursery());
                array.add(object13);

                JSONObject object14 = new JSONObject();
                object14.put("object14", applyForm.getLocalInvestmentOfLastYear() == null ? 0 : applyForm.getLocalInvestmentOfLastYear());
                array.add(object14);

                JSONObject object15 = new JSONObject();
                object15.put("object15", applyForm.getTotalSubsidyFunds() == null ? 0 : applyForm.getTotalSubsidyFunds());
                array.add(object15);

                /*JSONObject object16 = new JSONObject();
                object16.put("object16", applyForm.getNursingList() == null ? "" : applyForm.getNursingList());
                array.add(object16);*/

                JSONObject object17 = new JSONObject();
                object17.put("object17", applyForm.getLowIncomeCertificateList() == null ? 0 : applyForm.getLowIncomeCertificateList());
                array.add(object17);

                JSONObject object18 = new JSONObject();
                if (applyForm.getAdmin() != null && applyForm.getAdmin().getName() != null)
                    object18.put("adminName", applyForm.getAdmin().getName());
                else object18.put("adminName", "");
                array.add(object18);

                JSONObject object19 = new JSONObject();
                object19.put("createTime", applyForm.getCreateTime() == null ? "" : applyForm.getCreateTime());
                array.add(object19);

                JSONObject object20 = new JSONObject();
                object20.put("lastModifyTime", applyForm.getModifyTime() == null ? "" : applyForm.getModifyTime());
                array.add(object20);

                jsonArray.add(array);
            }
        }

        return jsonArray;
    }

    public void exportExcel(Integer organizationId, HttpServletRequest request, HttpServletResponse response) {
        logger.info("机构下全部补贴申请导出");
        Optional<Organization> organizationOptional = organizationRepository.findById(organizationId);
        String fileName = "ApplyFormList";
        if (organizationOptional.isPresent()) {
            String name = organizationOptional.get().getName()==null?"":organizationOptional.get().getName();
            logger.info("机构为："+name);
            fileName += "_" + name;
        }
        fileName += "_" + sdf.format(new Date()) + ".xls";
        ExcelUtils.exportExcel(fileName, columnNames, getListsByOrganizationId(organizationId), request, response);
    }

    public void exportExcel(String district, HttpServletRequest request, HttpServletResponse response) {
        logger.info("区:"+district+"全部补贴申请导出");
        String fileName = "ApplyFormList_" + district + "_" + sdf.format(new Date()) + ".xls";
        ExcelUtils.exportExcel(fileName, columnNames, getListsByDistrict(district), request, response);
    }

    /**
     * 全区残疾人之家托养服务补贴资金统计
     *
     * @param district 区名
     * @return 结果
     */
    public JSONObject getStatistic(String district) {
        logger.info("区:"+district+"残疾人之家托养服务补贴申请数量统计");
        List<Organization> organizationList = organizationRepository.findByDistrictAndStatus(district, 1);
        List<JSONObject> objectList = new ArrayList<>();
        for (Organization organization : organizationList) {
            JSONObject object = new JSONObject();
            object.put("id", organization.getId());
            object.put("name", organization.getName());
            object.put("applySum", organization.getApplySum());
            objectList.add(object);
        }
        return RESCODE.SUCCESS.getJSONRES(objectList);
    }

    public  JSONObject importExcel(MultipartFile file,
                                   HttpServletRequest request){
        return  ExcelUtils.importExcel(file);
    }

    public void exportUserListExcel(Integer applyFormId,
                                    HttpServletRequest request, HttpServletResponse response){
        Optional<ApplyForm> applyFormOptional = applyFormRepository.findById(applyFormId);

        String fileName = "ApplyForm_userList"+"_"+sdf.format(new Date())+".xls";
        if (applyFormOptional.isPresent()){

        }
        String[] columnNames = new String[]{"姓名", "残疾证号", "家庭住址",
                "联系电话", "托养方式(日托/全托)","补贴金额","托养月数"};
        ExcelUtils.exportExcel(fileName,columnNames,getListsByApplyForm(applyFormId),request,response);
    }

    private List<JSONArray> getListsByApplyForm(Integer applyFormId) {
        List<JSONArray> jsonArray = new ArrayList<>();
        Optional<ApplyForm> applyFormOptional = applyFormRepository.findById(applyFormId);
        if (applyFormOptional.isPresent()){
            ApplyForm applyForm = applyFormOptional.get();
            for (ApplyFormUser u:applyForm.getUserList()){
                JSONArray array = new JSONArray();

                JSONObject object = new JSONObject();
                object.put("username", u.getUsername() == null ? 0 : u.getUsername());
                array.add(object);

                JSONObject object1 = new JSONObject();
                object1.put("disabilityCertificateNumber", u.getDisabilityCertificateNumber() == null ? 0 : u.getDisabilityCertificateNumber());
                array.add(object1);

                JSONObject object2 = new JSONObject();
                object2.put("address", u.getAddress() == null ? 0 : u.getAddress());
                array.add(object2);

                JSONObject object3 = new JSONObject();
                object3.put("contactNumber", u.getContactNumber() == null ? 0 : u.getContactNumber());
                array.add(object3);

                JSONObject object4 = new JSONObject();
                object4.put("nursingMode", u.getNursingMode() == null ? 0 : u.getNursingMode());
                array.add(object4);

                JSONObject object5 = new JSONObject();
                object5.put("subsidies", u.getSubsidies() == null ? 0 : u.getSubsidies());
                array.add(object5);

                JSONObject object6 = new JSONObject();
                object6.put("subsidies", u.getMonth() == null ? 0 : u.getMonth());
                array.add(object6);

                jsonArray.add(array);
            }
        }
        return jsonArray;
    }
}
