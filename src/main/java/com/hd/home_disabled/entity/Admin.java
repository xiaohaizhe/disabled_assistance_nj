package com.hd.home_disabled.entity;

import com.hd.home_disabled.entity.dictionary.AdminType;
import lombok.*;

import javax.persistence.*;

/**
 * @ClassName Admin
 * @Description 管理员
 * @Author pyt
 * @Date 2019/7/4 10:17
 * @Version
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;    //管理员姓名
    private String password;    //管理员密码
    @OneToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private AdminType adminType;    //管理员权限

    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    private Organization organization;    //管理员权限
}
