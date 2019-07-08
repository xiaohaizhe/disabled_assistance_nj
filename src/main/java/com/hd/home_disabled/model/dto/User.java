package com.hd.home_disabled.model.dto;

import com.hd.home_disabled.entity.Admin;
import com.hd.home_disabled.entity.Organization;
import com.hd.home_disabled.entity.dictionary.DisabilityDegree;
import com.hd.home_disabled.entity.dictionary.NursingMode;
import com.hd.home_disabled.entity.dictionary.TypeOfDisability;
import com.hd.home_disabled.utils.validation.disabilityCertificateNumberValidation.DisabilityCertificateNumberValidation;
import com.hd.home_disabled.utils.validation.idNumberValidation.IdNumberValidation;
import com.hd.home_disabled.utils.validation.phonevalidation.PhoneValidation;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @ClassName User
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/8 10:29
 * @Version
 */
@Data
public class User {
    private Long id;
    @NotNull(message = "所属机构不能为空")
    private Integer organizationId;  //残疾人所属机构
    @NotNull(message = "姓名不能为空")
    @NotBlank(message = "姓名不能为空")
    private String name;                //残疾人姓名
    @NotNull(message = "身份证号码不能为空")
    @NotBlank(message = "身份证号码不能为空")
    @IdNumberValidation
    private String idNumber;            //残疾人身份证号码
    @NotNull(message = "残疾证号码不能为空")
    @NotBlank(message = "残疾证号码不能为空")
    @DisabilityCertificateNumberValidation
    private String disabilityCertificateNumber; //残疾证号码
    @NotNull(message = "残疾类别不能为空")
    private Integer typeOfDisabilityId;  //残疾类别
    @NotNull(message = "残疾程度不能为空")
    private Integer disabilityDegreeId;  //残疾程度
    @NotNull(message = "残疾人所属机构街道不能为空")
    @NotBlank(message = "残疾人所属机构街道不能为空")
    private String block;               //残疾人所属机构街道
    @NotNull(message = "家庭住址不能为空")
    @NotBlank(message = "家庭住址不能为空")
    private String address;             //家庭住址
    @NotNull(message = "联系电话不能为空")
    @NotBlank(message = "联系电话不能为空")
    @PhoneValidation
    private String contactNumber;       //联系电话
    @NotNull(message = "托养方式不能为空")
    private Integer nursingModeId;    //托养方式
    @NotNull(message = "托养月数不能为空")
    @Min(1)
    private Integer nursingMonth;       //托养月数
    @NotNull(message = "补贴金额不能为空")
    private Float subsidies;            //补贴金额

    //数据创建信息
    @NotNull(message = "信息提交人不能为空")
    private Integer adminId;       //残疾人信息提交人
    private String adminName;       //管理员姓名
    private Date createTime;        //残疾人信息创建时间
    private Date lastModifyTime;        //残疾人信息最新修改时间
}
