package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ClassName AccessTokenRepository
 * @Description TODO
 * @Author pyt
 * @Date 2019/8/10 15:42
 * @Version
 */
public interface AccessTokenRepository extends JpaRepository<AccessToken,Integer> {
}
