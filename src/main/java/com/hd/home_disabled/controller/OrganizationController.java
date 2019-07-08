package com.hd.home_disabled.controller;


import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.model.dto.Organization;
import com.hd.home_disabled.service.OrganizationService;
import com.hd.home_disabled.utils.DealWithBindingResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName OrganizationController
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/4 14:11
 * @Version
 */
@RestController
@RequestMapping("/api/org")
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @ApiOperation(value = "新增或修改", notes = "修改添加id")
    @RequestMapping(value = "/saveAndFlush", method = RequestMethod.POST)
    public JSONObject add(@RequestBody @Validated Organization organization, BindingResult br) {
        JSONObject result = DealWithBindingResult.dealWithBindingResult(br);
        if (result != null) {
            return result;
        }
        return organizationService.saveAndFlush(organization);
    }

    @ApiOperation(value = "删除", notes = "删除机构")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public JSONObject delete(Integer id) {
        return organizationService.delete(id);
    }

    @ApiOperation(value = "查询单个机构", notes = "根据id查询")
    @RequestMapping(value = "/getById", method = RequestMethod.GET)
    public JSONObject getOrgById(Integer id) {
        return organizationService.getById(id);
    }

    @ApiOperation(value = "查询区机构列表", notes = "查询")
    @RequestMapping(value = "/getListByDistrict", method = RequestMethod.GET)
    public JSONObject getOrgList1(String district) {
        return organizationService.getListByDistrict(district);
    }

    @ApiOperation(value = "查询区机构分页", notes = "分页")
    @RequestMapping(value = "/getPageByDistrict", method = RequestMethod.GET)
    public JSONObject getOrgList2(String district, Integer page, Integer number, String sorts) {
        return organizationService.getPageByDistrict(district, page, number,sorts);
    }
}
