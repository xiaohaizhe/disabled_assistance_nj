package com.hd.home_disabled.entity.dictionary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @ClassName NatureOfHousingPropertyRight
 * @Description 房屋产权性质
 * @Author pyt
 * @Date 2019/7/4 9:45
 * @Version
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NatureOfHousingPropertyRight {
    @Id
    private Integer id;
    private String name;
}
