package com.hd.home_disabled.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.entity.Admin;
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

    @ApiOperation(value = "添加机构管理员")
    @RequestMapping(value = "/addAdmin", method = RequestMethod.GET)
    public JSONObject addAdmin(String name, String password) {
        return adminService.addAdmin(name, password);
    }

    @ApiOperation(value = "修改机构管理员", notes = "用户名、密码，id必填")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public JSONObject update(@RequestBody Admin admin) {
        return adminService.update(admin);
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public JSONObject delete(Integer adminId) {
        return adminService.delete(adminId);
    }

    @ApiOperation(value = "机构管理员列表")
    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public JSONObject showAdmins(Integer page, Integer number, String sorts) {
        return adminService.showAdmins(page,number,sorts);
    }

/*    @ApiOperation(value = "更新管理员数据")
    @RequestMapping(value = "/updateData", method = RequestMethod.GET)
    public void updateData() {
        adminService.updateData();
    }*/

    @ApiOperation(value = "登陆", notes = "测试管理员密码：测试-5jNKlzcC，测试机构-8icyJpM6")
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public JSONObject login(String name, String password) {
        return adminService.login(name, password);
    }

    @ApiOperation(value = "加密", notes = "管理员密码加密")
    @RequestMapping(value = "/encrypt", method = RequestMethod.GET)
    public String encrypt(String password) {
        return adminService.encrypt(password);
    }

    @ApiOperation(value = "用户选择项目", notes = "管理员为用户选择项目")
    @RequestMapping(value = "/chooseProjectForUser", method = RequestMethod.POST)
    public JSONObject dealWithUserInfo(@RequestBody JSONArray array) {
        return adminService.dealWithUserInfo(array);
    }

    @ApiOperation(value = "每月用户新增/注销数量", notes = "type：0-新增，1-注销")
    @RequestMapping(value = "/numbersChange", method = RequestMethod.POST)
    public JSONObject numbersChange(String month, byte type) {
        return adminService.numbersChange(month, type);
    }

}
