package com.hd.home_disabled.entity;

import com.hd.home_disabled.entity.dictionary.AdminType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class Admin {
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Setter
    @Getter
    private String name;    //管理员姓名
    @Setter
    private String password;    //管理员密码
    @Setter
    @Getter
    @OneToOne
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private AdminType adminType;    //管理员权限
}
