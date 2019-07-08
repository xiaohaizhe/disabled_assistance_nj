package com.hd.home_disabled.entity.dictionary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @ClassName TypeOfDisability
 * @Description 残疾类别
 * @Author pyt
 * @Date 2019/7/4 10:14
 * @Version
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeOfDisability {
    @Id
    private Integer id;
    private String name;
}
