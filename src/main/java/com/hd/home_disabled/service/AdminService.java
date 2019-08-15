package com.hd.home_disabled.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.Admin;
import com.hd.home_disabled.entity.DingUserAttendanceRecord;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.repository.AdminRepository;
import com.hd.home_disabled.utils.JasyptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final DingUserService dingUserService;
    private final AdminRepository adminRepository;
    private final JasyptUtil jasyptUtil;

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    public AdminService(AdminRepository adminRepository, JasyptUtil jasyptUtil, DingUserService dingUserService) {
        this.adminRepository = adminRepository;
        this.jasyptUtil = jasyptUtil;
        this.dingUserService = dingUserService;
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
