package com.hd.home_disabled.entity;

import com.hd.home_disabled.entity.dictionary.DisabilityDegree;
import com.hd.home_disabled.entity.dictionary.NursingMode;
import com.hd.home_disabled.entity.dictionary.TypeOfDisability;
import com.hd.home_disabled.entity.statistic.ProjectUser;
import com.hd.home_disabled.entity.statistic.ProjectUserDetail;
import com.hd.home_disabled.utils.validation.disabilityCertificateNumberValidation.DisabilityCertificateNumberValidation;
import com.hd.home_disabled.utils.validation.idNumberValidation.IdNumberValidation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @ClassName User
 * @Description 残疾人
 * @Author pyt
 * @Date 2019/6/25 14:47
 * @Version
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.LAZY)
    @JoinColumn(name = "organizationId")
    private Organization organization;  //残疾人所属机构
    private String name;                //残疾人姓名
    private String idNumber;            //残疾人身份证号码
    private String disabilityCertificateNumber; //残疾证号码
    @OneToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private TypeOfDisability typeOfDisability;  //残疾类别
    @OneToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private DisabilityDegree disabilityDegree;  //残疾程度
    private String block;               //残疾人所属机构街道
    private String address;             //家庭住址
    private String contactNumber;       //联系电话
    @OneToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private NursingMode nursingMode;    //托养方式
    private Integer nursingMonth;       //托养月数
    private Float subsidies;            //补贴金额

    //数据创建信息
    @OneToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private Admin admin;       //机构信息提交人
    @CreationTimestamp
    @Column(updatable = false)
    private Date createTime;        //残疾人信息创建时间
    @UpdateTimestamp
    @Column
    private Date modifyTime;        //残疾人信息最新修改时间

    private int status;             //残疾人信息是否有效：0无效，1有效

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "user")
    private List<ProjectUser> projectUserList;
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "user")
    private List<ProjectUserDetail> projectUserDetailList;
}
