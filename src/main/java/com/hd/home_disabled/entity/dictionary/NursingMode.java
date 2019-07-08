package com.hd.home_disabled.entity.dictionary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @ClassName NursingMode
 * @Description 残疾人托养方式
 * @Author pyt
 * @Date 2019/7/5 9:51
 * @Version
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NursingMode {
    @Id
    private Integer id;
    private String type;
}
