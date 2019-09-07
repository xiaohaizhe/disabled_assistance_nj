package com.hd.home_disabled.entity.dictionary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationalNature {
    @Id
    private Integer id;
    private String nature;  //运营性质
}
