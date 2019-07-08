package com.hd.home_disabled.entity.statistic;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @ClassName UserBlockStatistic
 * @Description 残疾人按街道统计数据
 * @Author pyt
 * @Date 2019/7/4 10:21
 * @Version
 */
@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class UserBlockStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NonNull
    private String block;   //街道
    private Long visualDisability;  //视力残疾数量
    private Long hearingDisability; //听力残疾数量
    private Long speechDisability;  //言语残疾数量
    private Long physicalDisability;    //肢体残疾数量
    private Long intellectualDisability;    //智力残疾数量
    private Long mentalDisability;  //心理残疾数量
    private Long multipleDisability;    //多重残疾数量
    private Long other;                 //其他数量
}
