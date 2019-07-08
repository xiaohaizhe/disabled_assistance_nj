package com.hd.home_disabled.entity.dictionary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @ClassName DisabilityDegree
 * @Description 残疾等级
 * @Author pyt
 * @Date 2019/7/5 11:26
 * @Version
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisabilityDegree {
    @Id
    private Integer id;
    private String type;
}
