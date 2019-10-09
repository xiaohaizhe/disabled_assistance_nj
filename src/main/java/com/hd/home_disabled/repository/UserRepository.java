package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.User;
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

/**
 * @ClassName UserRepository
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/5 11:00
 * @Version
 */
public interface UserRepository extends JpaRepository<User, Long> {
    @QueryHints(value = {@QueryHint(name = HINT_COMMENT, value = "a query for pageable")})
    @Query("select u from User u where u.organization.id = ?1 and u.status = ?2")
    Page<User> findByOrganizationAndStatus(Integer organizationId, Integer status, Pageable page);

    Optional<User> findByStatusAndId(Integer status, Long id);

    @Query("select u from User u where u.organization.id = ?1 and u.status = ?2")
    List<User> findByOrganizationAndStatus(Integer organizationId, Integer status);

    @Query("select u from User u where u.idNumber = ?1 and u.status = ?2")
    Optional<User> findByIdNumberAndStatus(String idNumber, Integer status);

    @Query("select u from User u where u.contactNumber = ?1 and u.status = 1")
    Optional<User> findByContactNumber(String contactNumber);

    @Query("SELECT COUNT(1) from User pud where  pud.createTime >= ?1 and pud.createTime<=?2 ")
    Long countValidByStartBetween(Date start, Date end);

    @Query("SELECT COUNT(1) from User pud where pud.status=0 and pud.modifyTime >= ?1 and pud.modifyTime<=?2 ")
    Long countInvalidByStartBetween(Date start, Date end);
}
