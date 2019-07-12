package com.hd.home_disabled.entity.statistic;

import com.hd.home_disabled.entity.dictionary.ProjectType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

/**
 * @ClassName ProjectTypeStatistic
 * @Description 服务项目按项目类型统计数据
 * @Author pyt
 * @Date 2019/7/4 10:22
 * @Version
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTypeStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private ProjectType projectType;    //服务项目类型
    private Long person_time_sum;   //总服务人次
    private Long person_count_sum;  //总服务人数
    private Float totalTimeSum; //总服务时长
    private Float averageTime;  //平均服务时长
}
