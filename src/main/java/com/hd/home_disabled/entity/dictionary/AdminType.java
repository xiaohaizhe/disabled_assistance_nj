package com.hd.home_disabled.entity.dictionary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @ClassName AdminType
 * @Description 管理员类型
 * @Author pyt
 * @Date 2019/7/4 10:16
 * @Version
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminType {
    @Id
    private Integer id;
    private String type;
}
