package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.Date;
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

    @Query("select u from Project u where u.status = ?1")
    List<Project> findByStatus(Integer status);

    @QueryHints(value = {@QueryHint(name = HINT_COMMENT, value = "a query for pageable")})
    @Query("select u from Project u where u.organization.id in ?1 and u.status = ?2")
    Page<Project> findByOrganizationAndStatus(List<Integer> ids, Integer status, Pageable page);

    @QueryHints(value = {@QueryHint(name = HINT_COMMENT, value = "a query for pageable")})
    @Query("select u from Project u where u.organization.id in ?1 and u.status = ?2 and u.createTime>?3 and u.createTime<?4 ")
    Page<Project> findByOrganizationAndStatusAndCreateTimeBetween(List<Integer> ids, Integer status, Date from, Date to, Pageable page);

    @QueryHints(value = {@QueryHint(name = HINT_COMMENT, value = "a query for pageable")})
    @Query("select u from Project u where u.organization.id in ?1 and u.status = ?2 and u.projectType.id = ?3")
    Page<Project> findByOrganizationAndStatusAndProjectType(List<Integer> ids, Integer status, Integer projectTypeId, Pageable page);

    @QueryHints(value = {@QueryHint(name = HINT_COMMENT, value = "a query for pageable")})
    @Query("select u from Project u where u.organization.id in ?1 and u.status = ?2 and u.projectType.id = ?3 and u.createTime>?4 and u.createTime<?5")
    Page<Project> findByOrganizationAndStatusAndProjectTypeAndCreateTimeBetween(List<Integer> ids, Integer status,
                                                                                Integer projectTypeId, Date from, Date to,
                                                                                Pageable page);

    @Query("select u from Project u where u.organization.id in ?1 and u.status = ?2")
    List<Project> findByOrganizationAndStatus(List<Integer> ids, Integer status);
}
