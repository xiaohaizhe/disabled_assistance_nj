package com.hd.home_disabled.controller;

import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.model.dto.ApplyForm;
import com.hd.home_disabled.service.ApplyFormService;
import com.hd.home_disabled.utils.DealWithBindingResult;
import com.hd.home_disabled.utils.ExcelUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public JSONObject add(@RequestBody @Validated ApplyForm applyForm,BindingResult br) {
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

    @ApiOperation(value = "查询机构下补贴申请分页", notes = "分页")
    @RequestMapping(value = "/getPageByOrganization", method = RequestMethod.GET)
    public JSONObject getPageByOrganizationId(Integer organizationId, Integer page, Integer number, String sorts) {
        return applyFormService.getPageByOrganizationId(organizationId, page, number, sorts);
    }

    @ApiOperation(value = "补贴审核", notes = "通过")
    @RequestMapping(value = "/changeStatusToApproval", method = RequestMethod.GET)
    public JSONObject changeStatusToApproval(Integer id, Integer adminId) {
        return applyFormService.changeStatusToApproval(id,adminId);
    }

    @ApiOperation(value = "补贴审核", notes = "拒绝")
    @RequestMapping(value = "/changeStatusToRejection", method = RequestMethod.GET)
    public JSONObject changeStatusToRejection(Integer id, Integer adminId,String reason) {
        return applyFormService.changeStatusToRejection(id,adminId,reason);
    }

    @ApiOperation(value = "机构下补贴申请导出", notes = "excel导出")
    @RequestMapping(value = "/export_excel1", method = RequestMethod.GET)
    public void exportExcel(Integer organizationId, HttpServletRequest request, HttpServletResponse response) {
        applyFormService.exportExcel(organizationId, request, response);
    }

    @ApiOperation(value = "区补贴申请分页", notes = "分页")
    @RequestMapping(value = "/getPageByDistrict", method = RequestMethod.GET)
    public JSONObject getPageByDistrict(String district, Integer page, Integer number, String sorts) {
        return applyFormService.getPageByDistrict(district, page, number, sorts);
    }

    @ApiOperation(value = "区补贴申请导出", notes = "excel导出")
    @RequestMapping(value = "/export_excel2", method = RequestMethod.GET)
    public void exportExcel(String district, HttpServletRequest request, HttpServletResponse response) {
        applyFormService.exportExcel(district, request, response);
    }

    @ApiOperation(value = "区补贴资金统计", notes = "列表")
    @RequestMapping(value = "/getStatistic", method = RequestMethod.GET)
    public JSONObject getStatistic(String district) {
        return applyFormService.getStatistic(district);
    }

    @ApiOperation(value = "托养残疾人名单模板", notes = "模板")
    @RequestMapping(value = "/export_excel", method = RequestMethod.GET)
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        ExcelUtils.exportExcel(request,response);
    }

    @ApiOperation(value = "导入托养残疾人名单", notes = "名单")
    @RequestMapping(value = "/import_excel",method=RequestMethod.POST)
    public JSONObject importExcel( @RequestParam(value = "file", required = false) MultipartFile file,
                                   HttpServletRequest request, HttpServletResponse response){
        return applyFormService.importExcel(file,request);
    }

    @ApiOperation(value = "导出托养残疾人名单", notes = "名单")
    @RequestMapping(value = "/export_userList_excel",method=RequestMethod.GET)
    public void exportUserListExcel( Integer applyFormId,
                                   HttpServletRequest request, HttpServletResponse response){
        applyFormService.exportUserListExcel(applyFormId,request,response);
    }
}
