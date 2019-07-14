package com.hd.home_disabled.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.Admin;
import com.hd.home_disabled.entity.ApplyForm;
import com.hd.home_disabled.entity.Organization;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.repository.AdminRepository;
import com.hd.home_disabled.repository.ApplyFormRepository;
import com.hd.home_disabled.repository.OrganizationRepository;
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

    private static final Logger logger = LoggerFactory.getLogger(ApplyFormService.class);

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static String[] columnNames = new String[]{"审核状态", "机构名称", "登记注册时间", "地址", "负责人",
            "机构面积", "床位数", "机构性质", "庇护性劳动项目", "符合条件的全托人数",
            "符合条件的日托人数", "申请机构全托运营补贴资金总额", "申请机构日托运营补贴资金总额", "上年当地资金投入情况", "申请托养机构运营补贴资金总额合计",
            "托养残疾人名单", "低保或其他低收入证明", "提交人", "提交时间", "更新时间"};

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
     *
     * @param id 申请数据id
     * @return 结果
     */
    public JSONObject delete(Integer id) {
        Optional<ApplyForm> applyFormOptional = applyFormRepository.findByIdAndStatus(id, 1);
        if (applyFormOptional.isPresent()) {
            ApplyForm applyForm = applyFormOptional.get();
            Optional<Organization> organizationOptional = organizationRepository.findByIdAndStatus(applyForm.getOrganization().getId(), 1);
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
     *
     * @param id 申请表id
     * @return 结果
     */
    public JSONObject getById(Integer id) {
        Optional<ApplyForm> applyFormOptional = applyFormRepository.findByIdAndStatus(id, 1);
        if (applyFormOptional.isPresent()) {
            return RESCODE.SUCCESS.getJSONRES(getModel(applyFormOptional.get()));
        }
        return RESCODE.APPLY_FORM_NOT_EXIST.getJSONRES();
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
        Pageable pageable = PageUtils.getPage(page, number, sorts);
        Page<ApplyForm> applyFormPage = applyFormRepository.findByOrganizationAndStatus(organizationId, 1, pageable);
        List<com.hd.home_disabled.model.dto.ApplyForm> applyFormList = new ArrayList<>();
        for (ApplyForm applyForm :
                applyFormPage.getContent()) {
            applyFormList.add(getModel(applyForm));
        }
        return RESCODE.SUCCESS.getJSONRES(applyFormList, applyFormPage.getTotalPages(), applyFormPage.getTotalElements());
    }

    public JSONObject getPageByDistrict(String district, Integer page, Integer number, String sorts) {
        logger.info("根据区："+district+"查询补贴申请列表");
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

            JSONObject object16 = new JSONObject();
            object16.put("object16", applyForm.getNursingList() == null ? "" : applyForm.getNursingList());
            array.add(object16);

            JSONObject object17 = new JSONObject();
            object17.put("object17", applyForm.getLowIncomeCertificate() == null ? 0 : applyForm.getLowIncomeCertificate());
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

                JSONObject object16 = new JSONObject();
                object16.put("object16", applyForm.getNursingList() == null ? "" : applyForm.getNursingList());
                array.add(object16);

                JSONObject object17 = new JSONObject();
                object17.put("object17", applyForm.getLowIncomeCertificate() == null ? 0 : applyForm.getLowIncomeCertificate());
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
        Optional<Organization> organizationOptional = organizationRepository.findById(organizationId);
        String fileName = "ApplyFormList";
        if (organizationOptional.isPresent()) {
            fileName += "_" + organizationOptional.get().getName();
        }
        fileName += "_" + sdf.format(new Date()) + ".xls";
        ExcelUtils.exportExcel(fileName, columnNames, getListsByOrganizationId(organizationId), request, response);
    }

    public void exportExcel(String district, HttpServletRequest request, HttpServletResponse response) {
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
}
