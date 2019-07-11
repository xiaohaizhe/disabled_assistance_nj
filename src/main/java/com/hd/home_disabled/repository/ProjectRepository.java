package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

import static org.hibernate.jpa.QueryHints.HINT_COMMENT;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    Optional<Project> findByIdAndStatus(Integer id, Integer status);

    @QueryHints(value = {@QueryHint(name = HINT_COMMENT, value = "a query for pageable")})
    @Query("select u from Project u where u.organization.id = ?1 and u.status = ?2")
    Page<Project> findByOrganizationAndStatus(Integer organizationId, Integer status, Pageable page);

    @Query("select u from Project u where u.organization.id = ?1 and u.status = ?2")
    List<Project> findByOrganizationAndStatus(Integer organizationId, Integer status);
}
