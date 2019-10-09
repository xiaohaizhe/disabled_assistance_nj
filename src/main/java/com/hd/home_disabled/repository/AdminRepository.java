package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.Optional;

import static org.hibernate.jpa.QueryHints.HINT_COMMENT;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Optional<Admin> findByName(String name);

    @QueryHints(value = {@QueryHint(name = HINT_COMMENT, value = "a query for pageable")})
    @Query("select u from Admin u where u.adminType.id = 2")
    Page<Admin> findByAdminType(Pageable page);
}
