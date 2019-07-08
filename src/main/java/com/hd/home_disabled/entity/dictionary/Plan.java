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
    private Integer project_type0;
    private Integer project_type1;
    private Integer project_type2;
    private Integer project_type3;
    private Integer project_type4;
    private Integer project_type5;
    private Integer project_type6;
}
