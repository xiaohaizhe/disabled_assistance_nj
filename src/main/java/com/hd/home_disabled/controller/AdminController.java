package com.hd.home_disabled.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.service.AdminService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
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

    @ApiOperation(value = "登陆", notes = "测试管理员密码：测试-5jNKlzcC，测试机构-8icyJpM6")
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public JSONObject login(String name,String password) {
        return adminService.login(name, password);
    }

    @ApiOperation(value = "加密", notes = "管理员密码加密")
    @RequestMapping(value = "/encrypt", method = RequestMethod.GET)
    public String encrypt(String password){
        return adminService.encrypt(password);
    }

    @ApiOperation(value = "用户选择项目", notes = "管理员为用户选择项目")
    @RequestMapping(value = "/chooseProjectForUser", method = RequestMethod.POST)
    public JSONObject dealWithUserInfo(@RequestBody JSONArray array){
        return adminService.dealWithUserInfo(array);
    }

}
