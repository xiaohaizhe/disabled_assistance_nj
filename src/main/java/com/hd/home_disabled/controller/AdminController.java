package com.hd.home_disabled.controller;

import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.service.AdminService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName AdminController
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/11 14:26
 * @Version
 */
@RestController
@RequestMapping("api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @ApiOperation(value = "登陆", notes = "测试管理员秘密：5jNKlzcC")
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public JSONObject login(String name,String password) {
        return adminService.login(name, password);
    }
}
