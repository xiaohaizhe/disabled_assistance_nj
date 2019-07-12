package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.statistic.ProjectUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

import static org.hibernate.jpa.QueryHints.HINT_COMMENT;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Integer> {
    @Query("select u from ProjectUser u where u.project.id = ?1")
    List<ProjectUser> findByProject(Integer projectId);

    @QueryHints(value = {@QueryHint(name = HINT_COMMENT, value = "a query for pageable")})
    @Query("select u from ProjectUser u where u.project.id = ?1")
    Page<ProjectUser> findByProject(Integer projectId, Pageable page);
}
