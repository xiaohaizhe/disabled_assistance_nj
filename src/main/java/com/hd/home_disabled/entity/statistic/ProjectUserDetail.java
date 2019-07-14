package com.hd.home_disabled.entity.statistic;

import com.hd.home_disabled.entity.Admin;
import com.hd.home_disabled.entity.Project;
import com.hd.home_disabled.entity.User;
import com.hd.home_disabled.entity.dictionary.ProjectType;
import com.hd.home_disabled.entity.dictionary.TypeOfDisability;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * @ClassName ProjectUserDetail
 * @Description 残疾人与项目人次关系表,记录每次打卡详情
 * @Author pyt
 * @Date 2019/7/4 10:16
 * @Version
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUserDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
    private Project project;
    @ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
    private User user;
    private Date start;     //打卡开始时间
    private Date end;       //打卡结束时间
    private Float lengthOfService;  //本次服务时长

    //数据创建信息
    @OneToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private Admin admin;       //打卡信息提交人
    @CreationTimestamp
    @Column(updatable = false)
    private Date createTime;        //打卡信息创建时间
    @UpdateTimestamp
    @Column
    private Date modifyTime;        //打卡信息最新修改时间
}
