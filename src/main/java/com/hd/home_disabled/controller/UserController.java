package com.hd.home_disabled.controller;

import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.model.dto.User;
import com.hd.home_disabled.service.UserService;
import com.hd.home_disabled.utils.DealWithBindingResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName UserController
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/5 11:02
 * @Version
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "新增或修改", notes = "修改添加id")
    @RequestMapping(value = "/saveAndFlush", method = RequestMethod.POST)
    public JSONObject add(@RequestBody @Validated User user, BindingResult br) {
        System.out.println("进入");
        JSONObject result = DealWithBindingResult.dealWithBindingResult(br);
        if (result != null) {
            return result;
        }
        return userService.saveAndFlush(user);
    }

    @ApiOperation(value = "删除", notes = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public JSONObject delete(Long id) {
        return userService.delete(id);
    }

    @ApiOperation(value = "查询单个残疾人信息", notes = "根据id查询")
    @RequestMapping(value = "/getById", method = RequestMethod.GET)
    public JSONObject getById(Long id) {
        return userService.getById(id);
    }

    @ApiOperation(value = "查询机构下残疾人分页", notes = "分页")
    @RequestMapping(value = "/getPageByOrganization", method = RequestMethod.GET)
    public JSONObject getPageByOrganizationId(Integer organizationId, Integer page, Integer number,String sorts) {
        return userService.getPageByOrganizationId(organizationId, page, number,sorts);
    }

    @ApiOperation(value = "机构下残疾人信息导出", notes = "excel导出")
    @RequestMapping(value = "/export_excel",method = RequestMethod.GET)
    public void exportExcel(Integer organizationId,HttpServletRequest request, HttpServletResponse response) {
        userService.exportExcel(organizationId,request,response);
    }

    @ApiOperation(value = "全区残疾人统计表", notes = "统计")
    @RequestMapping(value = "/statistic",method = RequestMethod.GET)
    public JSONObject getStatistic() {
        return userService.getStatistic();
    }

    @ApiOperation(value = "全区残疾人统计表导出", notes = "统计")
    @RequestMapping(value = "/statisticExcel",method = RequestMethod.GET)
    public void getStatisticExcel(HttpServletRequest request, HttpServletResponse response) {
         userService.getStatisticExcel(request,response);
    }

    //全区各类残疾人参与服务率分析
    @ApiOperation(value = "全区各类残疾人参与服务率分析", notes = "统计")
    @RequestMapping(value = "/statisticData",method = RequestMethod.GET)
    public JSONObject statisticData() {
        return  userService.statisticData();
    }
}
