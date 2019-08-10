package com.hd.home_disabled.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * @ClassName DingUserAttendanceRecord
 * @Description TODO
 * @Author pyt
 * @Date 2019/8/10 19:50
 * @Version
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class DingUserAttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String dingUserId;  //用户id
    private Integer projectId;  //参加项目id
    private Date userCheckTime; //打卡时间
    private Byte status;        //0-打卡开始，1-打卡结束
}
