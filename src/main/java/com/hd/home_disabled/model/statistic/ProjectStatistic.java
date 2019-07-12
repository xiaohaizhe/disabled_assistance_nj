package com.hd.home_disabled.model.statistic;

import lombok.Data;

/**
 * @ClassName ProjectStatistic
 * @Description 项目统计数据
 * @Author pyt
 * @Date 2019/7/12 9:46
 * @Version
 */
@Data
public class ProjectStatistic {
    private Integer id;     //服务项目id
    private String name;    //服务项目名称
    private Integer personCountSum; //服务人数总数
    private Integer personTimeSum;  //服务总人次
    private Integer totalTimeSum;   //服务总时长（分钟）
    private Float averageTime;      //平均服务时长：总时长/总人次
}
