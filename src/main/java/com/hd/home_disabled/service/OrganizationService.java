package com.hd.home_disabled.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.*;
import com.hd.home_disabled.entity.dictionary.NatureOfHousingPropertyRight;
import com.hd.home_disabled.entity.dictionary.OperationalNature;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.repository.AdminRepository;
import com.hd.home_disabled.repository.NatureOfHousingPropertyRightRepository;
import com.hd.home_disabled.repository.OrganizationRepository;
import com.hd.home_disabled.repository.ProjectUserDetailRepository;
import com.hd.home_disabled.utils.DocumentHandlers;
import com.hd.home_disabled.utils.ExcelUtils;
import com.hd.home_disabled.utils.PageUtils;
import com.hd.home_disabled.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private final ProjectUserDetailRepository projectUserDetailRepository;
    private final NatureOfHousingPropertyRightRepository natureOfHousingPropertyRightRepository;

    private final OrganizationRepository organizationRepository;

    private final AdminRepository adminRepository;

    @Value("${server.port}")
    private String port;
    private final ApplyFormService applyFormService;

    public OrganizationService(NatureOfHousingPropertyRightRepository natureOfHousingPropertyRightRepository, OrganizationRepository organizationRepository, AdminRepository adminRepository, ProjectUserDetailRepository projectUserDetailRepository, ApplyFormService applyFormService) {
        this.natureOfHousingPropertyRightRepository = natureOfHousingPropertyRightRepository;
        this.organizationRepository = organizationRepository;
        this.adminRepository = adminRepository;
        this.projectUserDetailRepository = projectUserDetailRepository;
        this.applyFormService = applyFormService;
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

    //model-->entity
    Organization dealWithData(com.hd.home_disabled.model.dto.Organization organization) throws ParseException {
        logger.info("机构数据:model-->entity");
        Organization org = new Organization();
        if (organization.getId() != null) org.setId(organization.getId());
        org.setName(organization.getName());
        org.setRegistrationTime(sdf.parse(organization.getRegistrationTime()));
        org.setRegistrationCertificateNumber(organization.getRegistrationCertificateNumber());
        org.setRegistrationDepartment(organization.getRegistrationDepartment());
        org.setNature(organization.getNature());

        OperationalNature operationalNature = new OperationalNature();
        operationalNature.setId(organization.getOperationalNatureId());
        org.setOperationalNature(operationalNature);
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
        logger.info("机构数据:entity-->model");
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
        if (organization.getOperationalNature() != null && organization.getOperationalNature().getNature() != null) {
            org.setOperationalNature(organization.getOperationalNature().getNature());
        }
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
        org.setRegistrationTime(sdf.format(organization.getRegistrationTime()));
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
        logger.info("添加或修改机构");
        logger.info(organization.toString());
        Organization org;
        try {
            org = dealWithData(organization);
        } catch (ParseException e) {
            e.printStackTrace();
            return RESCODE.TIME_PARSE_FAILURE.getJSONRES();
        }
        if (org.getAdmin() != null &&
                org.getAdmin().getId() != null) {
            Optional<Admin> adminOptional = adminRepository.findById(org.getAdmin().getId());
            if (adminOptional.isPresent()){
                Admin admin = adminOptional.get();
                if (org.getNatureOfHousingPropertyRight() != null &&
                        org.getNatureOfHousingPropertyRight().getId() != null &&
                        natureOfHousingPropertyRightRepository.existsById(org.getNatureOfHousingPropertyRight().getId())) {
                    org.setStatus(1);
                    Organization organization1 = organizationRepository.saveAndFlush(org);
                    admin.setOrganization(organization1);
                    adminRepository.saveAndFlush(admin);
                    return RESCODE.SUCCESS.getJSONRES(dealWithOrganization(organization1));
                }
                return RESCODE.NATURE_OF_HOUSING_PROPERTY_RIGHT_ID_NOT_EXIST.getJSONRES();
            }
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
        logger.info("删除机构");
        Optional<Organization> optional = organizationRepository.findById(id);
        if (optional.isPresent()) {
            optional.get().setStatus(0);
            logger.info("删除成功");
            return RESCODE.SUCCESS.getJSONRES();
        }
        logger.info("删除失败");
        return RESCODE.ORGANIZATION_ID_NOT_EXIST.getJSONRES();
    }

    /**
     * 查询单个机构
     *
     * @param id 机构id
     * @return 结果
     */
    public JSONObject getById(Integer id) {
        logger.info("查询单个机构");
        Optional<Organization> optional = organizationRepository.findById(id);
        if (optional.isPresent()) {
            com.hd.home_disabled.model.dto.Organization organization = dealWithOrganization(optional.get());
            return RESCODE.SUCCESS.getJSONRES(organization);
        }
        logger.info("机构不存在");
        return RESCODE.ORGANIZATION_ID_NOT_EXIST.getJSONRES();
    }

    /**
     * 查询单个机构
     *
     * @param adminId 机构id
     * @return 结果
     */
    public JSONObject getByAdminId(Integer adminId) {
        logger.info("查询单个机构");
        Optional<Organization> optional = organizationRepository.findByAdmin_IdAndStatus(adminId, 1);
        if (optional.isPresent()) {
            com.hd.home_disabled.model.dto.Organization organization = dealWithOrganization(optional.get());
            return RESCODE.SUCCESS.getJSONRES(organization);
        }
        logger.info("机构不存在");
        return RESCODE.ORGANIZATION_ID_NOT_EXIST.getJSONRES();
    }

    /**
     * 查询区机构列表
     *
     * @param district 区名
     * @return 结果
     */
    public JSONObject getListByDistrict(String district) {
        logger.info("查询区机构");
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
            object26.put("createTime", o.getCreateTime() == null ? "" : o.getCreateTime());
            array.add(object26);
            JSONObject object27 = new JSONObject();
            object27.put("lastModifyTime", o.getModifyTime() == null ? "" : o.getModifyTime());
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
        logger.info("区:" + district + "机构分页");
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

    public void exportExcel(String district, HttpServletRequest request, HttpServletResponse response) {
        String[] columnNames = new String[]{"机构名称", "登记注册时间", "注册证书编号", "注册部门", "机构性质",
                "房屋产权性质", "机构面积", "床位数", "庇护性劳动项目", "地址",
                "负责人", "性别", "出生年月", "文化程度", "营业执照或登记证书",
                "银行开户许可", "门头及室内功能区域、无障碍设施", "专职工作人员名单", "管理制度", "服务项目总数",
                "总服务人数", "总服务人次", "总服务时长", "平均服务时长", "提交人",
                "提交时间", "更新时间"};
        String fileName = "OrganizationList" + "_" + sdf.format(new Date()) + ".xls";
        ExcelUtils.exportExcel(fileName, columnNames, getListsByDistrict(district), request, response);
    }

    private List<JSONObject> getOrganization(List<Organization> organizationList) {
        List<JSONObject> objectList = new ArrayList<>();
        for (Organization organization : organizationList) {
            JSONObject object = new JSONObject();
            object.put("id", organization.getId());
            object.put("name", organization.getName());
            object.put("userNum", organization.getUserList().size());
            objectList.add(object);
        }
        return objectList;
    }

    public JSONObject analysis() {
        JSONObject object = new JSONObject();
        Sort sort1 = new Sort(new Sort.Order(Sort.Direction.DESC, "projectSum"));
        Sort sort2 = new Sort(new Sort.Order(Sort.Direction.DESC, "personCountSum"));
        Sort sort3 = new Sort(new Sort.Order(Sort.Direction.DESC, "personTimeSum"));
        Sort sort4 = new Sort(new Sort.Order(Sort.Direction.DESC, "totalTimeSum"));
        Sort sort5 = new Sort(new Sort.Order(Sort.Direction.DESC, "averageTime"));
        List<Organization> organizationList1 = organizationRepository.findAll(sort1);
        if (organizationList1.size() > 3) organizationList1 = organizationList1.subList(0, 3);
        List<Organization> organizationList2 = organizationRepository.findAll(sort2);
        if (organizationList2.size() > 3) organizationList2 = organizationList2.subList(0, 3);
        List<Organization> organizationList3 = organizationRepository.findAll(sort3);
        if (organizationList3.size() > 3) organizationList3 = organizationList3.subList(0, 3);
        List<Organization> organizationList4 = organizationRepository.findAll(sort4);
        if (organizationList4.size() > 3) organizationList4 = organizationList4.subList(0, 3);
        List<Organization> organizationList5 = organizationRepository.findAll(sort5);
        if (organizationList5.size() > 3) organizationList5 = organizationList5.subList(0, 3);
        object.put("projectSum", getOrganization(organizationList1));
        object.put("personCountSum", getOrganization(organizationList2));
        object.put("personTimeSum", getOrganization(organizationList3));
        object.put("totalTimeSum", getOrganization(organizationList4));
        object.put("averageTime", getOrganization(organizationList5));
        List<Sort.Order> orders = new ArrayList<>();
        String sorts = "projectSum,personCountSum,personTimeSum,totalTimeSum,averageTime";
        for (String sort : sorts.split(",")) {
            orders.add(new Sort.Order(Sort.Direction.DESC, sort));
        }
        Sort sort6 = new Sort(orders);
        List<Organization> organizationList6 = organizationRepository.findAll(sort6);
        if (organizationList6.size() > 3) organizationList6 = organizationList6.subList(0, 3);
        object.put("ranking", getOrganization(organizationList6));
        return RESCODE.SUCCESS.getJSONRES(object);
    }

    public String exportWord(Integer orgId, Integer applyId) throws IOException {
        Optional<Organization> optional = organizationRepository.findById(orgId);
        Organization org = optional.get();
        List<ApplyForm> applyForms = org.getApplyFormList();
        List wlist = new ArrayList();
        ApplyForm applyForm = applyFormService.getApplyFormById(applyId);
        List<ApplyFormUser> list = applyForm.getUserList();

        double sum = 0;
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("userno", i + 1); //
            map.put("username", list.get(i).getUsername()); //结束时间
            map.put("userid", list.get(i).getDisabilityCertificateNumber()); //结束时间
            map.put("useraddr", list.get(i).getAddress()); //结束时间

            map.put("userphone", list.get(i).getContactNumber()); //结束时间
            map.put("usermode", list.get(i).getNursingMode()); //结束时间
            map.put("usermonth", 1); //结束时间
            map.put("usermoney", list.get(i).getSubsidies()); //结束时间
            sum += list.get(i).getSubsidies();

            wlist.add(map);
        }


        Map<String, Object> dataMap = new HashMap<String, Object>();

        dataMap.put("name", org.getName());
        dataMap.put("time", org.getRegistrationTime());
        dataMap.put("city", org.getDetailedAddress());
        dataMap.put("person", org.getPersonInCharge());
        dataMap.put("number", org.getContactNumber());
        dataMap.put("area", org.getArea());
        dataMap.put("bed", org.getBedNum());
        dataMap.put("nature", org.getNature());
        dataMap.put("labor", org.getAsylumLaborProjects());
        dataMap.put("daycare", applyForm.getNumOfEligibleDayNursery());
        dataMap.put("allcare", applyForm.getNumOfEligibleBoardingNursery());
        dataMap.put("daycaremoney", applyForm.getSubsidyFundForDayNursery());
        dataMap.put("allcaremoney", applyForm.getSubsidyFundForBoardingNursery());
        dataMap.put("lastyearmoney", applyForm.getLocalInvestmentOfLastYear());
        dataMap.put("totalmoney", applyForm.getTotalSubsidyFunds());
        dataMap.put("list", wlist);
        dataMap.put("sum", sum);
        InputStream in;
        byte[] picdata = null;
        String img = null;
//        String dic=System.getProperty("user.dir")+"/picture/lowIncomeCertificate";
//        String dic="E://disabled/picture/lowIncomeCertificate";
        String dic = System.getProperty("user.dir");
        List<LowIncomeCertificate> imageList = applyForm.getLowIncomeCertificateList();
        BASE64Encoder encoder = new BASE64Encoder();
        List<String> images = new ArrayList<>();
        for (int i = 0; i < imageList.size(); i++) {
            String url = imageList.get(i).getUrl();
            String[] strings = url.split(port);
            String path = dic + strings[1];
            in = new FileInputStream(path);
            picdata = new byte[in.available()];
            in.read(picdata);
            img = encoder.encode(picdata);
            images.add(img);
        }

//
//        try {
//            for(int i=0;i<applyForms.size();i++)
//            {
//                String url=applyForms.get(i).getLowIncomeCertificate();
//                String[] s=url.split("lowIncomeCertificate");
//                String dir=s[1];
//                String address=dic+"/picture/lowIncomeCertificate"+dir;
//                in=new FileInputStream(address);
//                picdata=new byte[in.available()];
//                in.read(picdata);
//                img=encoder.encode(picdata);
//                images.add(img);
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        dataMap.put("images", images);

        DocumentHandlers documentHandler = new DocumentHandlers();
        return documentHandler.createDoc(dataMap, "new2.ftl", org.getName());
    }

    /**
     * 机构每月服务人数/人次
     *
     * @param month
     * @param type 0-人数，1-人次
     * @return
     */
    public JSONObject numberOfServicePerMonth(Integer organizationId,String month, byte type) {
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
    /*        System.out.println(date.getDate());
            System.out.println(date.getHours());
            System.out.println(date.getMinutes());
            System.out.println(date.getSeconds());*/
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
                result = projectUserDetailRepository.countByOrganizationIdAndStartBetween(organizationId,date,end);
            }else{
                result = projectUserDetailRepository.countDistinctByOrganizationIdAndStartBetween(organizationId,date,end);
            }
            JSONObject object = new JSONObject();
            object.put("result", result);
            return RESCODE.SUCCESS.getJSONRES(object);
        }
    }
}
