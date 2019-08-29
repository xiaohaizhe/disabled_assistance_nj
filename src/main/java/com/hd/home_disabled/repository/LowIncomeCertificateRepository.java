package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.LowIncomeCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @ClassName LowIncomeCertificateRepository
 * @Description TODO
 * @Author pyt
 * @Date 2019/8/29 17:58
 * @Version
 */
public interface LowIncomeCertificateRepository extends JpaRepository<LowIncomeCertificate,Long> {
    @Query("select u from LowIncomeCertificate u where u.url = ?1 and u.applyForm.id = ?2 ")
    Optional<LowIncomeCertificate> findByUrlAndApplyForm(String url,Integer applyFormId);

    @Query("select u from LowIncomeCertificate u where u.applyForm.id = ?1 ")
    List<LowIncomeCertificate> findByApplyForm(Integer applyFormId);
}
