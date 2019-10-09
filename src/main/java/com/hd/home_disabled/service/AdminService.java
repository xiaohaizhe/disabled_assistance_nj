package com.hd.home_disabled.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.*;
import com.hd.home_disabled.entity.dictionary.AdminType;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.repository.*;
import com.hd.home_disabled.utils.JasyptUtil;
import com.hd.home_disabled.utils.PageUtils;
import com.hd.home_disabled.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName AdminService
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/11 14:27
 * @Version
 */
@Service
@Transactional
public class AdminService {
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final DingUserRepository dingUserRepository;
    private final UserService userService;
    private final DingUserAttendanceRecordRepository dingUserAttendanceRecordRepository;
    private final DingUserService dingUserService;
    private final AdminRepository adminRepository;
    private final JasyptUtil jasyptUtil;

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    public AdminService(AdminRepository adminRepository, JasyptUtil jasyptUtil, DingUserService dingUserService, DingUserAttendanceRecordRepository dingUserAttendanceRecordRepository, UserService userService, DingUserRepository dingUserRepository, UserRepository userRepository, OrganizationRepository organizationRepository) {
        this.adminRepository = adminRepository;
        this.jasyptUtil = jasyptUtil;
        this.dingUserService = dingUserService;
        this.dingUserAttendanceRecordRepository = dingUserAttendanceRecordRepository;
        this.userService = userService;
        this.dingUserRepository = dingUserRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    public JSONObject addAdmin(String name, String pwd) {
        if (checkName(name)) return RESCODE.ADMIN_ID_EXIST.getJSONRES();
        Admin admin = new Admin();
        admin.setName(name);
        admin.setPassword(jasyptUtil.encrypt(pwd));
        AdminType type = new AdminType();
        type.setId(2);
        admin.setAdminType(type);
        Admin adminNew = adminRepository.save(admin);
        return RESCODE.SUCCESS.getJSONRES(adminNew);
    }

    public JSONObject update(Admin admin) {
        if (admin.getId() != null) {
            Optional<Admin> adminOptional = adminRepository.findById(admin.getId());
            if (adminOptional.isPresent()) {
                Admin adminOld = adminOptional.get();
                if (admin.getName() != null && !admin.getName().equals(adminOld.getName())) {
                    if (checkName(admin.getName())) return RESCODE.ADMIN_ID_EXIST.getJSONRES();
                    adminOld.setName(admin.getName());
                }
                if (admin.getPassword() != null && !StringUtil.isBlank(admin.getPassword()))
                    adminOld.setPassword(jasyptUtil.encrypt(admin.getPassword()));
                adminRepository.saveAndFlush(adminOld);
                return RESCODE.SUCCESS.getJSONRES(adminOld);
            }
        }
        return RESCODE.ADMIN_ID_NOT_EXIST.getJSONRES();
    }

    public JSONObject delete(Integer adminId) {
        Optional<Admin> adminOptional = adminRepository.findById(adminId);
        if (adminOptional.isPresent()) {
            adminRepository.deleteById(adminId);
            return RESCODE.SUCCESS.getJSONRES();
        }
        return RESCODE.ADMIN_ID_NOT_EXIST.getJSONRES();
    }

    public JSONObject getAdmin(Admin admin) {
        JSONObject object = new JSONObject();
        object.put("id", admin.getId());
        object.put("name", admin.getName());
        if (admin.getOrganization()!=null) object.put("organization", admin.getOrganization().getName());
        return object;
    }

    public JSONObject showAdmins(Integer page, Integer number, String sorts) {
        Pageable pageable = PageUtils.getPage(page, number, sorts);
        Page<Admin> adminPage = adminRepository.findByAdminType(pageable);
        JSONArray array = new JSONArray();
        for (Admin admin : adminPage.getContent()) {
            array.add(getAdmin(admin));
        }
        return RESCODE.SUCCESS.getJSONRES(array, adminPage.getTotalPages(), adminPage.getTotalElements());
    }

    boolean checkName(String name) {
        return adminRepository.findByName(name).isPresent();
    }

/*    public void updateData() {
        List<Organization> organizationList =  organizationRepository.findAll();
        for (Organization organization:organizationList) {
            Admin admin =  organization.getAdmin();
            admin.setOrganization(organization);
            adminRepository.saveAndFlush(admin);
        }
    }*/

    /**
     * 管理员登陆
     *
     * @param name     用户名
     * @param password 密码
     * @return 登陆成功返回用户信息
     */
    public JSONObject login(String name, String password) {
        logger.info("====管理员:" + name + ",准备登陆====");
        Optional<Admin> adminOptional = adminRepository.findByName(name);
        if (adminOptional.isPresent()) {
            String newPwd = jasyptUtil.decrypt(adminOptional.get().getPassword());
            if (newPwd.equals(password)) {
                logger.info("====登陆成功====");
                return RESCODE.SUCCESS.getJSONRES(adminOptional.get());
            }
            logger.info("====登陆失败，密码错误====");
            return RESCODE.FAILURE.getJSONRES();
        }
        logger.info("====登陆失败，管理员不存在====");
        return RESCODE.ADMIN_ID_NOT_EXIST.getJSONRES();
    }

    public String encrypt(String password) {
        logger.info("====开始加密管理员密码====");
        return jasyptUtil.encrypt(password);
    }

    public JSONObject dealWithUserInfo(JSONArray array) {
        boolean flag = true;
        JSONArray array1 = new JSONArray();
        System.out.println(array);
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject) array.get(i);
            DingUserAttendanceRecord record = new DingUserAttendanceRecord();
            if (object.getLong("id") != null
                    && object.getInteger("projectId") != null) {
                Long id = object.getLong("id");
                Integer projectId = object.getInteger("projectId");
                logger.info("id:" + id);
                logger.info("projectId:" + projectId);
                if (dingUserService.getById(id) != null) {
                    DingUserAttendanceRecord dingUserAttendanceRecord = dingUserService.getById(id);
                    dingUserAttendanceRecord.setProjectId(projectId);
                    dingUserService.updateDingUserAttendanceRecord(dingUserAttendanceRecord);
                    //检查打卡结束是否录入
                    Sort.Order order = new Sort.Order(Sort.Direction.DESC, "userCheckTime");
                    Sort sort = new Sort(order);
                    List<DingUserAttendanceRecord> recordList = dingUserAttendanceRecordRepository.findByDingUserId(dingUserAttendanceRecord.getDingUserId(), sort);
                    for (int j = 0; j < recordList.size(); j++) {
                        if (recordList.get(j).getId() == id) {
                            if (j > 0) {
                                int z = j - 1;
                                while (z >= 0) {
                                    if (recordList.get(z).getStatus() == 1) {
                                        String dingUserId = dingUserAttendanceRecord.getDingUserId();
                                        Optional<DingUser> dingUserOptional = dingUserRepository.findById(dingUserId);
                                        if (dingUserOptional.isPresent()) {
                                            Optional<User> userOptional = userRepository.findByContactNumber(dingUserOptional.get().getMobile());
                                            if (userOptional.isPresent()) {
                                                userService.clockIn(projectId, userOptional.get().getId(), dingUserAttendanceRecord.getUserCheckTime(),
                                                        recordList.get(z).getUserCheckTime(), userOptional.get().getAdmin().getId());
                                            }
                                        }
                                        break;
                                    }
                                    z--;
                                }
                            }
                            break;
                        }
                    }
                } else {
                    flag = false;
                    array1.add(object);
                }
            } else {
                flag = false;
                array1.add(object);
            }
        }
        if (flag) {
            return RESCODE.SUCCESS.getJSONRES();
        } else {
            return RESCODE.FAILURE.getJSONRES(array1);
        }

    }

    public JSONObject numbersChange(String month, byte type) {
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
            if (date.getDate() != 1 || date.getHours() != 0 || date.getMinutes() != 0 || date.getSeconds() != 0) {
                return RESCODE.FAILURE.getJSONRES("请使用月初时间");
            }
            //判断是否为当月
            Date end = null;
            if (date.getMonth() == today.getMonth()) {
                end = new Date();
            } else {
                end = (Date) date.clone();
                end.setMonth(end.getMonth() + 1);
            }
            long result = 0;
            if (type == 0) {
                result = userRepository.countValidByStartBetween(date, end);
            } else {
                result = userRepository.countInvalidByStartBetween(date, end);
            }
            JSONObject object = new JSONObject();
            object.put("result", result);
            return RESCODE.SUCCESS.getJSONRES(object);
        }
    }
}
