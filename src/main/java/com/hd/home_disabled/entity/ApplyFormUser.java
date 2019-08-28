package com.hd.home_disabled.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @ClassName ApplyFormUser
 * @Description TODO
 * @Author pyt
 * @Date 2019/8/20 10:13
 * @Version
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyFormUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
    @JoinColumn(name = "applyFormId")
    private ApplyForm applyForm;
    private String username;    //残疾人姓名
    private String disabilityCertificateNumber; //残疾证号
    private String address;     //家庭住址
    private String contactNumber;   //联系电话
    private String nursingMode;     //托养方式
    private Float subsidies;        //补贴金额
    private Integer month;          //托养月数
}
