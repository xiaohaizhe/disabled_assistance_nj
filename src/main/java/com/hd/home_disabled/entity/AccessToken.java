package com.hd.home_disabled.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * @ClassName AccessToken
 * @Description TODO
 * @Author pyt
 * @Date 2019/8/10 15:38
 * @Version
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class AccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String accessToken;
    @CreationTimestamp
    private Date created;
}
