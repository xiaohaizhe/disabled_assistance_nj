package com.hd.home_disabled.entity.dictionary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @ClassName Plan
 * @Description 项目计划（项目完成%基数）
 * @Author pyt
 * @Date 2019/7/4 10:18
 * @Version
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plan {
    @Id
    private Integer id;
    private Integer project_type0;  //日间照料
    private Integer project_type1;  //辅助性就业
    private Integer project_type2;  //康复服务
    private Integer project_type3;  //文体活动
    private Integer project_type4;  //学习培训
    private Integer project_type5;  //志愿服务
    private Integer project_type6;  //其它
}
