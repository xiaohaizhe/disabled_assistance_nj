package com.hd.home_disabled.controller;

import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.model.dto.Project;
import com.hd.home_disabled.model.dto.User;
import com.hd.home_disabled.service.ProjectService;
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
 * @ClassName ProjectController
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/5 11:53
 * @Version
 */
@RestController
@RequestMapping("api/project")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @ApiOperation(value = "新增或修改", notes = "修改添加id")
    @RequestMapping(value = "/saveAndFlush", method = RequestMethod.POST)
    public JSONObject add(@RequestBody @Validated Project project, BindingResult br) {
        System.out.println("进入");
        JSONObject result = DealWithBindingResult.dealWithBindingResult(br);
        if (result != null) {
            return result;
        }
        return projectService.saveAndFlush(project);
    }

    @ApiOperation(value = "删除", notes = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public JSONObject delete(Integer id) {
        return projectService.delete(id);
    }

    @ApiOperation(value = "查询单个服务项目信息", notes = "根据id查询")
    @RequestMapping(value = "/getById", method = RequestMethod.GET)
    public JSONObject getById(Integer id) {
        return projectService.getById(id);
    }

    @ApiOperation(value = "查询机构下服务项目分页", notes = "分页")
    @RequestMapping(value = "/getPageByOrganization", method = RequestMethod.GET)
    public JSONObject getPageByOrganizationId(Integer organizationId, Integer page, Integer number,String sorts) {
        return projectService.getPageByOrganizationId(organizationId, page, number,sorts);
    }

    @ApiOperation(value = "区服务项目信息导出", notes = "excel导出")
    @RequestMapping(value = "/export_excel",method = RequestMethod.GET)
    public void exportExcel(String district, HttpServletRequest request, HttpServletResponse response) {
        projectService.exportExcel(district,request,response);
    }

    @ApiOperation(value = "查询机构下服务项目运行统计", notes = "统计数据")
    @RequestMapping(value = "/getProjectStatistic", method = RequestMethod.GET)
    public JSONObject getProjectStatistic(Integer organizationId){
        return projectService.getProjectStatistic(organizationId);
    }

    @ApiOperation(value = "查询机构下服务项目数据分析",
            notes = "项目运行数据统计（总服务人数，总服务人次，总服务时长，平均服务时长）")
    @RequestMapping(value = "/getProjectAnalysis1", method = RequestMethod.GET)
    public JSONObject getProjectAnalysis(Integer id){
        return projectService.getProjectAnalysis1(id);
    }

    @ApiOperation(value = "查询机构下服务项目数据分析",notes = " 参与项目残疾人列表")
    @RequestMapping(value = "/getProjectAnalysis2", method = RequestMethod.GET)
    public JSONObject getProjectAnalysis2(Integer id,Integer page,Integer number,String sorts){
        return projectService.getProjectAnalysis2(id,page,number,sorts);
    }

    @ApiOperation(value = "机构下服务项目参与残疾人数据导出",notes = " excel导出")
    @RequestMapping(value = "/userListExport", method = RequestMethod.GET)
    public void getProjectAnalysis3(Integer id,HttpServletRequest request, HttpServletResponse response){
         projectService.getProjectAnalysis3(id,request,response);
    }
}
