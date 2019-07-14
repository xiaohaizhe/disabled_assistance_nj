package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.ApplyForm;
import com.hd.home_disabled.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

import static org.hibernate.jpa.QueryHints.HINT_COMMENT;

/**
 * @ClassName ApplyFormRepository
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/9 17:17
 * @Version
 */
public interface ApplyFormRepository extends JpaRepository<ApplyForm,Integer> {
    @QueryHints(value = {@QueryHint(name = HINT_COMMENT ,value= "a query for pageable")})
    @Query("select u from ApplyForm u where u.organization.id = ?1 and u.status = ?2")
    Page<ApplyForm> findByOrganizationAndStatus(Integer organizationId, Integer status, Pageable page);

    Optional<ApplyForm> findByIdAndStatus(Integer id, Integer status);

    @Query("select u from ApplyForm u where u.organization.id = ?1 and u.status = ?2")
    List<ApplyForm> findByOrganizationAndStatus(Integer organizationId, Integer status);

    @Query("select u from ApplyForm u where u.organization.id in ?1 and u.status = ?2")
    List<ApplyForm> findByOrganizationAndStatus(List<Integer> ids, Integer status);

    @QueryHints(value = {@QueryHint(name = HINT_COMMENT ,value= "a query for pageable")})
    @Query("select u from ApplyForm u where u.organization.id in ?1 and u.status = ?2")
    Page<ApplyForm> findByOrganizationAndStatus(List<Integer> ids, Integer status, Pageable page);
}
