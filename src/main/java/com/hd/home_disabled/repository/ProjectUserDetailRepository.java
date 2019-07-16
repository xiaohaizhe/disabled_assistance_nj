package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.statistic.ProjectUserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

/**
 * @ClassName ProjectUserDetailRepository
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/12 10:28
 * @Version
 */
public interface ProjectUserDetailRepository extends JpaRepository<ProjectUserDetail, Integer> {
    @Query("select u from ProjectUserDetail u where u.project.id = ?1 and u.user.id = ?2")
    List<ProjectUserDetail> findByProjectAndUser(Integer projectId, Long userId);

    List<ProjectUserDetail> findByStartAfter(Date start);
}
