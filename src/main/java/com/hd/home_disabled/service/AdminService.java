package com.hd.home_disabled.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.Admin;
import com.hd.home_disabled.entity.DingUser;
import com.hd.home_disabled.entity.DingUserAttendanceRecord;
import com.hd.home_disabled.entity.User;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.repository.AdminRepository;
import com.hd.home_disabled.repository.DingUserAttendanceRecordRepository;
import com.hd.home_disabled.repository.DingUserRepository;
import com.hd.home_disabled.repository.UserRepository;
import com.hd.home_disabled.utils.JasyptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserRepository userRepository;
    private final DingUserRepository dingUserRepository;
    private final UserService userService;
    private final DingUserAttendanceRecordRepository dingUserAttendanceRecordRepository;
    private final DingUserService dingUserService;
    private final AdminRepository adminRepository;
    private final JasyptUtil jasyptUtil;

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    public AdminService(AdminRepository adminRepository, JasyptUtil jasyptUtil, DingUserService dingUserService, DingUserAttendanceRecordRepository dingUserAttendanceRecordRepository, UserService userService, DingUserRepository dingUserRepository, UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.jasyptUtil = jasyptUtil;
        this.dingUserService = dingUserService;
        this.dingUserAttendanceRecordRepository = dingUserAttendanceRecordRepository;
        this.userService = userService;
        this.dingUserRepository = dingUserRepository;
        this.userRepository = userRepository;
    }

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
                    List<DingUserAttendanceRecord> recordList = dingUserAttendanceRecordRepository.findByDingUserId(dingUserAttendanceRecord.getDingUserId(),sort);
                    for (int j = 0; j < recordList.size(); j++) {
                        if (recordList.get(j).getId()==id){
                            if (j>0){
                                int z = j-1;
                                while(z>=0){
                                    if (recordList.get(z).getStatus()==1){
                                        String dingUserId = dingUserAttendanceRecord.getDingUserId();
                                        Optional<DingUser> dingUserOptional = dingUserRepository.findById(dingUserId);
                                        if (dingUserOptional.isPresent()){
                                            Optional<User> userOptional = userRepository.findByContactNumber(dingUserOptional.get().getMobile());
                                            if (userOptional.isPresent()){
                                                userService.clockIn(projectId,userOptional.get().getId(),dingUserAttendanceRecord.getUserCheckTime(),
                                                        recordList.get(z).getUserCheckTime(),userOptional.get().getAdmin().getId());
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
}
