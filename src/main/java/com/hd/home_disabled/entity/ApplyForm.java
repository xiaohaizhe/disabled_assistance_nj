package com.hd.home_disabled.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * @ClassName ApplyForm
 * @Description 残疾人托养服务补贴资金申请
 * @Author pyt
 * @Date 2019/7/4 10:11
 * @Version
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyForm {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
    @JoinColumn(name = "organizationId")
    private Organization organization;              //申请所属机构
    private Integer numOfEligibleDayNursery;    //符合条件的日托人数
    private Integer numOfEligibleBoardingNursery;   //符合条件的全托人数
    private Float subsidyFundForDayNursery;     //申请机构日托运营补贴资金总额
    private Float subsidyFundForBoardingNursery;    //申请机构全托运营补贴资金总额
    private Float localInvestmentOfLastYear;        //上年当地资金投入
    private Float totalSubsidyFunds;              //申请托养机构运营补贴资金总额合计

    private String nursingList;                    //托养残疾人名单
    private String lowIncomeCertificate;            //低保或其他低收入证明

    private String reasonForRegression;           //申请退回原因
    private Integer status;                         //申请状态:0-删除,1-有效，2-退回，3-审核完成
    //数据创建信息
    @OneToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private Admin admin;            //申请提交人
    @CreationTimestamp
    @Column(updatable = false)
    private Date createTime;        //申请创建时间
    @UpdateTimestamp
    @Column
    private Date modifyTime;        //申请最新修改时间
}
