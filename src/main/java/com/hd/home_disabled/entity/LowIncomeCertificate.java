package com.hd.home_disabled.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @ClassName LowIncomeCertificate
 * @Description TODO
 * @Author pyt
 * @Date 2019/8/29 17:50
 * @Version
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class LowIncomeCertificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
    private ApplyForm applyForm;
    private String url;
}
