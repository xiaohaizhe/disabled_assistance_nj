package com.hd.home_disabled.model.dto;

import com.hd.home_disabled.entity.ApplyFormUser;
import com.hd.home_disabled.entity.Organization;
import io.swagger.models.auth.In;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @ClassName ApplyForm
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/9 16:05
 * @Version
 */
@Data
public class ApplyForm {
    private Integer id;
    private Integer organizationId;     //申请所属机构
    private String organizationName;    //申请所属机构
    private Date registrationTime;      //机构登记时间
    private String address;             //申请机构地址
    private String personInCharge;      //申请机构负责人
    private Float area;                 //机构面积
    @NotNull(message = "bedNum")
    private Integer bedNum;             //床位数
    @NotEmpty(message = "nature不能为空")
    private String nature;              //机构性质
    @NotEmpty(message = "asylumLaborProjects不能为空")
    private String asylumLaborProjects; //庇护性劳动项目

    @NotNull(message = "numOfEligibleDayNursery符合条件的日托人数不能为空")
    private Integer numOfEligibleDayNursery;    //符合条件的日托人数
    @NotNull(message = "numOfEligibleBoardingNursery符合条件的全托人数不能为空")
    private Integer numOfEligibleBoardingNursery;   //符合条件的全托人数
    @NotNull(message = "subsidyFundForDayNursery申请机构日托运营补贴资金总额不能为空")
    private Float subsidyFundForDayNursery;     //申请机构日托运营补贴资金总额
    @NotNull(message = "subsidyFundForBoardingNursery申请机构全托运营补贴资金总额不能为空")
    private Float subsidyFundForBoardingNursery;    //申请机构全托运营补贴资金总额
    @NotNull(message = "localInvestmentOfLastYear上年当地资金投入情况不能为空")
    private Float localInvestmentOfLastYear;        //上年当地资金投入
    @NotNull(message = "totalSubsidyFunds申请托养机构运营补贴资金总额合计不能为空")
    private Float totalSubsidyFunds;              //申请托养机构运营补贴资金总额合计

    private List<ApplyFormUser> userList;       //补贴名单
/*    @NotBlank(message = "低保或其他低收入证明不能为空")
    @NotNull(message = "低保或其他低收入证明不能为空")*/
    private String lowIncomeCertificate;            //低保或其他低收入证明

    private String reasonForRegression;           //申请退回原因
    private Integer status;                       //申请状态:0-删除,1-有效，2-退回，3-审核完成
    private Integer adminId;                      //申请提交人
    private String adminName;       //机构信息提交人姓名
    private Date createTime;        //创建时间
    private Date lastModifyTime;        //最新修改时间
}
