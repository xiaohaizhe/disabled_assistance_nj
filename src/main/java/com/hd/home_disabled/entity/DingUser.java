package com.hd.home_disabled.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @ClassName DingUser
 * @Description TODO
 * @Author pyt
 * @Date 2019/8/10 17:04
 * @Version
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class DingUser {
    @Id
    private String userId;
    private String name;
    private String mobile;
    private String departmentId;
}
