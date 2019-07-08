package com.hd.home_disabled.entity.dictionary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @ClassName ProjectType
 * @Description 残疾人服务内容大类
 * @Author pyt
 * @Date 2019/7/4 10:12
 * @Version
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectType {
    @Id
    private Integer id;
    private String name;
}
