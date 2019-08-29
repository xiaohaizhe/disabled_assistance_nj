package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.ApplyFormUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @ClassName ApplyFormUserRepository
 * @Description TODO
 * @Author pyt
 * @Date 2019/8/20 11:44
 * @Version
 */
public interface ApplyFormUserRepository extends JpaRepository<ApplyFormUser,Integer> {
    @Query("select u from ApplyFormUser u where u.disabilityCertificateNumber=?1 and u.applyForm.id=?2")
    Optional<ApplyFormUser> findByDisabilityCertificateNumberAndApplyForm(String disabilityCertificateNumber, Integer applyFormId);

    @Query("select u from ApplyFormUser u where u.applyForm.id=?1")
    List<ApplyFormUser> findByApplyForm(Integer applyFormId);

}
