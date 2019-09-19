package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.DingUserAttendanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hibernate.jpa.QueryHints.HINT_COMMENT;

public interface DingUserAttendanceRecordRepository extends JpaRepository<DingUserAttendanceRecord,Long> {
    @Query("select cl from DingUserAttendanceRecord cl where cl.dingUserId = ?1")
    List<DingUserAttendanceRecord> findByDingUserId(String dingUserId, Sort sort);

    @Query("select cl from DingUserAttendanceRecord cl where cl.dingUserId = ?1 and cl.userCheckTime = ?2")
    Optional<DingUserAttendanceRecord> findByDingUserIdAnAndUserCheckTime(String dingUserId,Date userCheckTime);

    @Query("select cl from DingUserAttendanceRecord cl where cl.status = ?1 and cl.userCheckTime>?2 and cl.userCheckTime<?3")
    List<DingUserAttendanceRecord> findByStatus(Byte status, Date from, Date to);

    @QueryHints(value = {@QueryHint(name = HINT_COMMENT ,value= "a query for pageable")})
    @Query("select cl from DingUserAttendanceRecord cl where cl.dingUserId in ?1 and cl.status = 0 ")
    Page<DingUserAttendanceRecord> findByDingUserIdIn(List<String> dingUserId, Pageable page);
}
