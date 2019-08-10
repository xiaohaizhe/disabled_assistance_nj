package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.DingUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ClassName DingUserRepository
 * @Description TODO
 * @Author pyt
 * @Date 2019/8/10 17:16
 * @Version
 */
public interface DingUserRepository extends JpaRepository<DingUser,String> {
}
