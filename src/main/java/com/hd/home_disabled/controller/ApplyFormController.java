package com.hd.home_disabled.controller;

import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.model.dto.ApplyForm;
import com.hd.home_disabled.model.dto.User;
import com.hd.home_disabled.service.ApplyFormService;
import com.hd.home_disabled.utils.DealWithBindingResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName ApplyFormController
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/9 17:16
 * @Version
 */
@RestController
@RequestMapping("api/apply")
public class ApplyFormController {
    private final ApplyFormService applyFormService;

    public ApplyFormController(ApplyFormService applyFormService) {
        this.applyFormService = applyFormService;
    }

    @ApiOperation(value = "新增或修改", notes = "修改添加id")
    @RequestMapping(value = "/saveAndFlush", method = RequestMethod.POST)
    public JSONObject add(@RequestBody @Validated ApplyForm applyForm, BindingResult br) {
        System.out.println("进入");
        JSONObject result = DealWithBindingResult.dealWithBindingResult(br);
        if (result != null) {
            return result;
        }
        return applyFormService.saveAndFlush(applyForm);
    }

    @ApiOperation(value = "删除", notes = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public JSONObject delete(Integer id) {
        return applyFormService.delete(id);
    }

    @ApiOperation(value = "查询单个补贴申请信息", notes = "根据id查询")
    @RequestMapping(value = "/getById", method = RequestMethod.GET)
    public JSONObject getById(Integer id) {
        return applyFormService.getById(id);
    }

    @ApiOperation(value = "查询机构下补贴申请人分页", notes = "分页")
    @RequestMapping(value = "/getPageByOrganization", method = RequestMethod.GET)
    public JSONObject getPageByOrganizationId(Integer organizationId, Integer page, Integer number,String sorts) {
        return applyFormService.getPageByOrganizationId(organizationId, page, number,sorts);
    }

    @ApiOperation(value = "机构下补贴申请导出", notes = "excel导出")
    @RequestMapping(value = "/export_excel1",method = RequestMethod.GET)
    public void exportExcel(Integer organizationId, HttpServletRequest request, HttpServletResponse response) {
        applyFormService.exportExcel(organizationId,request,response);
    }

    @ApiOperation(value = "区补贴申请导出", notes = "excel导出")
    @RequestMapping(value = "/export_excel2",method = RequestMethod.GET)
    public void exportExcel(String district, HttpServletRequest request, HttpServletResponse response) {
        applyFormService.exportExcel(district,request,response);
    }
}
