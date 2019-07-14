package com.hd.home_disabled.model.dto;

import com.hd.home_disabled.entity.Organization;
import com.hd.home_disabled.entity.dictionary.ProjectType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @ClassName Project
 * @Description 项目model
 * @Author pyt
 * @Date 2019/7/9 10:32
 * @Version
 */
@Data
public class Project {
    private Integer id;
    @NotNull(message = "所属机构不能为空")
    private Integer organizationId;     //服务项目所属机构
    private String organizationName;    //服务项目所属机构名称
//    @NotNull(message = "项目名称不能为空")
//    @NotBlank(message = "项目名称不能为空")
    private String name;    //服务项目名称
    @NotNull(message = "项目类型不能为空")
    private Integer projectTypeId;    //项目类型
    private String projectType;     //项目类型
//    @NotNull(message = "项目负责人不能为空")
//    @NotBlank(message = "项目负责人不能为空")
    private String leader;      //项目负责人
//    @NotNull(message = "项目简介不能为空")
//    @NotBlank(message = "项目简介不能为空")
    private String description; //项目简介
//    @NotNull(message = "项目图片地址不能为空")
//    @NotBlank(message = "项目图片地址不能为空")
    private String image;       //项目图片地址
    @NotNull(message = "项目开始时间不能为空")
    private Date startTime;     //项目开始时间
    @NotNull(message = "项目结束时间不能为空")
    private Date endTime;       //项目结束时间

    private Integer personCountSum; //服务人数总数
    private Integer personTimeSum;  //服务总人次
    private Integer totalTimeSum;   //服务总时长
    private Float averageTime;      //平均服务时长：总时长/总人次

    //数据创建信息
    @NotNull(message = "信息提交人不能为空")
    private Integer adminId;       //服务项目信息提交人
    private String adminName;       //管理员姓名
    private Date createTime;        //服务项目信息创建时间
    private Date lastModifyTime;        //服务项目信息最新修改时间
}
