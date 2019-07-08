package com.hd.home_disabled.entity;

import com.hd.home_disabled.entity.dictionary.ProjectType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * @ClassName Project
 * @Description 服务项目
 * @Author pyt
 * @Date 2019/7/4 10:13
 * @Version
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
    private Organization organization;
    private String name;    //服务项目名称
    @OneToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private ProjectType projectType;    //项目类型
    private String leader;      //项目负责人
    private String description; //项目简介
    private String image;       //项目图片地址
    private Date startTime;     //项目开始时间
    private Date endTime;       //项目结束时间

    //数据创建信息
    @OneToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private Admin admin;       //机构信息提交人
    @CreationTimestamp
    @Column(updatable = false)
    private Date createTime;        //服务项目信息创建时间
    @UpdateTimestamp
    @Column
    private Date modifyTime;        //服务项目信息最新修改时间

    private int status;             //服务项目是否有效：0无效，1有效

}
