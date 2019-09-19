package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.DingUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @ClassName DingUserRepository
 * @Description TODO
 * @Author pyt
 * @Date 2019/8/10 17:16
 * @Version
 */
public interface DingUserRepository extends JpaRepository<DingUser,String> {
    @Query("select dd.userId from DingUser dd where dd.mobile in (?1)")
    List<String> findByMobile(List<String> mobiles);
}
