package com.hd.home_disabled.service;

import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.Admin;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.repository.AdminRepository;
import com.hd.home_disabled.utils.JasyptUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final AdminRepository adminRepository;
    private final JasyptUtil jasyptUtil;

    public AdminService(AdminRepository adminRepository, JasyptUtil jasyptUtil) {
        this.adminRepository = adminRepository;
        this.jasyptUtil = jasyptUtil;
    }

    /**
     * 管理员登陆
     * @param name  用户名
     * @param password  密码
     * @return 登陆成功返回用户信息
     */
    public JSONObject login(String name,String password){
        Optional<Admin> adminOptional =adminRepository.findByName(name);
        if (adminOptional.isPresent()){
            String newPwd = jasyptUtil.decrypt(adminOptional.get().getPassword());
            if (newPwd.equals(password)){
                return RESCODE.SUCCESS.getJSONRES(adminOptional.get());
            }
        }
        return RESCODE.ADMIN_ID_NOT_EXIST.getJSONRES();
    }


}
