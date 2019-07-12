package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Integer> {
    List<Organization> findByDistrictAndStatus(String district, Integer status);

    Page<Organization> findByDistrictAndStatus(String district, Integer status, Pageable page);

    Optional<Organization> findByIdAndStatus(Integer id,Integer status);

}
