package com.hd.home_disabled.entity;

import com.hd.home_disabled.entity.dictionary.NatureOfHousingPropertyRight;
import com.hd.home_disabled.entity.dictionary.OperationalNature;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.awt.geom.FlatteningPathIterator;
import java.util.Date;
import java.util.List;

/**
 * @ClassName organization
 * @Description 机构表
 * @Author pyt
 * @Date 2019/7/4 10:10
 * @Version
 */
@Entity
@Data
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;            //机构名称
    private Date registrationTime;  //登记注册时间
    private String registrationCertificateNumber;    //注册证书编号
    private String registrationDepartment;  //注册部门
    private String nature;          //机构性质
    @OneToOne
    private OperationalNature operationalNature;    //运营性质
    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "nature_of_housing_property_right_id", referencedColumnName = "id", nullable = false)
    private NatureOfHousingPropertyRight natureOfHousingPropertyRight;  //房屋产权性质
    private Float area;             //机构面积
    private Integer bedNum;         //床位数
    private String asylumLaborProjects; //庇护性劳动项目

    //机构地址信息
    private String addressId;       //前端留用，cityCode
    private String detailedAddress; //详细地址
    private String province;        //省
    private String city;            //市
    private String district;        //区
    private String block;           //街道

    //机构负责人信息
    private String personInCharge;  //负责人姓名
    private String gender;          //性别
    private String birthMonth;      //出生年月
    private String education;       //文化程度
    private String contactNumber;  //联系电话

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "organization")
    private List<User> userList;
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "organization")
    private List<Project> projectList;
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "organization")
    private List<ApplyForm> applyFormList;

    //图片、文件地址
    private String certification;   //营业执照或登记证书
    private String openBankAccountPermitCertificate;    //银行开户许可
    private String staffList;      //专职工作人员名单
    private String managementSystem;    //管理制度
    private String facilitiesPictures;  //机构设施图片：门头及室内功能区域、无障碍设施

    //机构统计数据
    private Integer applySum;       //机构申请总数

    private Integer projectSum;    //机构服务项目总数
    private Integer personCountSum; //服务人数总数
    private Integer personTimeSum;  //服务总人次
    private Float totalTimeSum;   //服务总时长
    private Float averageTime;      //平均服务时长：总时长/总人次

    //数据创建信息
    @OneToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private Admin admin;       //机构信息提交人
    @CreationTimestamp
    @Column(updatable = false)
    private Date createTime;        //机构信息创建时间
    @UpdateTimestamp
    @Column
    private Date modifyTime;        //机构信息最新修改时间
    private int status;             //机构是否有效：0无效，1有效
}
