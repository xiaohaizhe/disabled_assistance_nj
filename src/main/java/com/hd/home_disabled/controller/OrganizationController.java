package com.hd.home_disabled.controller;


import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.model.dto.Organization;
import com.hd.home_disabled.service.OrganizationService;
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
import java.io.*;
import java.net.URLEncoder;

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

    @ApiOperation(value = "查询单个机构", notes = "根据id查询")
    @RequestMapping(value = "/getByAdminId", method = RequestMethod.GET)
    public JSONObject getByAdminId(Integer adminId) {
        return organizationService.getByAdminId(adminId);
    }

    @ApiOperation(value = "查询区机构列表", notes = "查询")
    @RequestMapping(value = "/getListByDistrict", method = RequestMethod.GET)
    public JSONObject getOrgList1(String district) {
        return organizationService.getListByDistrict(district);
    }

    @ApiOperation(value = "查询区机构分页", notes = "分页")
    @RequestMapping(value = "/getPageByDistrict", method = RequestMethod.GET)
    public JSONObject getOrgList2(String district, Integer page, Integer number, String sorts) {
        return organizationService.getPageByDistrict(district, page, number, sorts);
    }

    @ApiOperation(value = "区机构信息导出", notes = "excel导出")
    @RequestMapping(value = "/export_excel", method = RequestMethod.GET)
    public void exportExcel(String district, HttpServletRequest request, HttpServletResponse response) {
        organizationService.exportExcel(district, request, response);
    }

    //残疾人之家分析
    @ApiOperation(value = "残疾人之家分析", notes = "查询")
    @RequestMapping(value = "/analysis", method = RequestMethod.GET)
    public JSONObject analysis() {
        return organizationService.analysis();
    }

    @ApiOperation(value = "区机构信息导出word", notes = "word导出")
    @RequestMapping(value = "/export_word", method = RequestMethod.GET)
    public HttpServletResponse exportWord(Integer orgId, Integer applyId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = organizationService.exportWord(orgId, applyId);
        try {
            // path是指欲下载的文件的路径。
            File file = new File(path);
            String filename = file.getName();
            // 取得文件的后缀名。
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();

            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");

            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return response;

    }

    @ApiOperation(value = "机构每月服务人数/人次", notes = "查询，month-月初，格式yyyy-MM-dd HH:mm:ss;type-0:人数、1:人次")
    @RequestMapping(value = "/numberOfServicePerMonth", method = RequestMethod.GET)
    public JSONObject numberOfServicePerMonth(Integer organizationId,String month, byte type) {
        return organizationService.numberOfServicePerMonth(organizationId,month,type);
    }

}
