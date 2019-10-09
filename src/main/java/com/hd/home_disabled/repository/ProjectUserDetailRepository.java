package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.statistic.ProjectUserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    @Query("SELECT COUNT(1) from ProjectUserDetail pud where pud.organizationId=?1 and pud.start >= ?2 and pud.start<=?3 ")
    Long countByOrganizationIdAndStartBetween(Integer organizationId, Date start, Date end);

    @Query("SELECT COUNT(DISTINCT user_id) from ProjectUserDetail pud where pud.organizationId=?1 and  pud.start >= ?2 and pud.start<=?3 ")
    Long countDistinctByOrganizationIdAndStartBetween(Integer organizationId, Date start, Date end);

    @Query("SELECT COUNT(1) from ProjectUserDetail pud where pud.project.id = ?1 and pud.start >= ?2 and pud.start<=?3 ")
    Long countByProjectAndStartBetween(Integer projectId, Date start, Date end);

    @Query("SELECT COUNT(DISTINCT user_id) from ProjectUserDetail pud where pud.project.id = ?1 and  pud.start >= ?2 and pud.start<=?3 ")
    Long countDistinctByProjectAndStartBetween(Integer projectId, Date start, Date end);
}
