package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.DingUserAttendanceRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface DingUserAttendanceRecordRepository extends JpaRepository<DingUserAttendanceRecord,Long> {
    @Query("select cl from DingUserAttendanceRecord cl where cl.dingUserId = ?1")
    List<DingUserAttendanceRecord> findByDingUserId(String dingUserId, Sort sort);

    @Query("select cl from DingUserAttendanceRecord cl where cl.status = ?1 and cl.userCheckTime>?2 and cl.userCheckTime<?3")
    List<DingUserAttendanceRecord> findByStatus(Byte status, Date from, Date to);
}
