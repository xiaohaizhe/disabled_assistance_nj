package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.statistic.ProjectTypeStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProjectTypeStatisticRepository extends JpaRepository<ProjectTypeStatistic, Integer> {
    @Query("select u from ProjectTypeStatistic u where u.projectType.id = ?1")
    Optional<ProjectTypeStatistic> findByProjectType(Integer projectTypeId);
}
