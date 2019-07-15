package com.hd.home_disabled.model.dto;

import com.hd.home_disabled.utils.validation.phonevalidation.PhoneValidation;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @ClassName Organization
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/5 15:58
 * @Version
 */
@Data
public class Organization {
    private Integer id;
/*    @NotBlank(message = "机构名称不能为空")
    @NotNull(message = "机构名称不能为空")*/
    private String name;            //机构名称
//    @NotNull(message = "登记注册时间不能为空")
    private Date registrationTime;  //登记注册时间
/*    @NotBlank(message = "注册证书编号不能为空")
    @NotNull(message = "注册证书编号不能为空")*/
    private String registrationCertificateNumber;    //注册证书编号
/*    @NotBlank(message = "注册部门不能为空")
    @NotNull(message = "注册部门不能为空")*/
    private String registrationDepartment;  //注册部门
/*    @NotBlank(message = "机构性质不能为空")
    @NotNull(message = "机构性质不能为空")*/
    private String nature;          //机构性质
    @NotNull(message = "房屋产权性质不能为空")
    private Integer natureOfHousingPropertyRightId;  //房屋产权性质
    private String natureOfHousingPropertyRight;
//    @NotNull(message = "机构面积不能为空")
    private Float area;             //机构面积
//    @NotNull(message = "床位数不能为空")
    private Integer bedNum;         //床位数
/*    @NotBlank(message = "庇护性劳动项目不能为空")
    @NotNull(message = "庇护性劳动项目不能为空")*/
    private String asylumLaborProjects; //庇护性劳动项目

    //机构地址信息
    /*@NotBlank(message = "地址不能为空")
    @NotNull(message = "地址不能为空")*/
    private String addressId;       //前端留用，cityCode
   /* @NotBlank(message = "详细地址不能为空")
    @NotNull(message = "详细地址不能为空")*/
    private String detailedAddress; //详细地址
/*    @NotBlank(message = "省级信息不能为空")
    @NotNull(message = "省级信息不能为空")*/
    private String province;        //省
   /* @NotBlank(message = "市级信息不能为空")
    @NotNull(message = "市级信息不能为空")*/
    private String city;            //市
    @NotBlank(message = "区级信息不能为空")
    @NotNull(message = "区级信息不能为空")
    private String district;        //区
    @NotBlank(message = "街道信息不能为空")
    @NotNull(message = "街道信息不能为空")
    private String block;           //街道

    //机构负责人信息
    /*@NotBlank(message = "负责人姓名不能为空")
    @NotNull(message = "负责人姓名不能为空")*/
    private String personInCharge;  //负责人姓名
   /* @NotBlank(message = "负责人性别不能为空")
    @NotNull(message = "负责人性别不能为空")*/
    private String gender;          //性别
   /* @NotBlank(message = "负责人出生年月不能为空")
    @NotNull(message = "负责人出生年月不能为空")*/
    private String birthMonth;      //出生年月
    /*@NotBlank(message = "负责人文化程度不能为空")
    @NotNull(message = "负责人文化程度不能为空")*/
    private String education;       //文化程度
   /* @NotBlank(message = "负责人联系电话不能为空")
    @NotNull(message = "负责人联系电话不能为空")
    @PhoneValidation(message = "手机号格式错误")*/
    private String contactNumber;  //联系电话

    //图片、文件地址
  /*  @NotNull(message = "营业执照或登记证书不能为空")
    @NotBlank(message = "营业执照或登记证书不能为空")*/
    private String certification;   //营业执照或登记证书
   /* @NotNull(message = "银行开户许可不能为空")
    @NotBlank(message = "银行开户许可不能为空")*/
    private String openBankAccountPermitCertificate;    //银行开户许可
   /* @NotNull(message = "专职工作人员名单不能为空")
    @NotBlank(message = "专职工作人员名单不能为空")*/
    private String staffList;      //专职工作人员名单
   /* @NotNull(message = "管理制度不能为空")
    @NotBlank(message = "管理制度不能为空")*/
    private String managementSystem;    //管理制度
    /*@NotNull(message = "门头及室内功能区域、无障碍设施不能为空")
    @NotBlank(message = "门头及室内功能区域、无障碍设施不能为空")*/
    private String facilitiesPictures;  //机构设施图片：门头及室内功能区域、无障碍设施

    //机构统计数据
    private Integer applySum;       //机构申请总数
    private Integer projectSum;    //机构服务项目总数
    private Integer personCountSum; //服务人数总数
    private Integer personTimeSum;  //服务总人次
    private Float totalTimeSum;   //服务总时长
    private Float averageTime;      //平均服务时长：总时长/总人次

    //数据创建信息
    @NotNull(message = "机构信息提交人不能为空")
    private Integer adminId;       //机构信息提交人
    private String adminName;       //机构信息提交人姓名
    private Date createTime;        //创建时间
    private Date lastModifyTime;        //最新修改时间
}
