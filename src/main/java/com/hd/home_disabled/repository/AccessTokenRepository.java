package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @ClassName AccessTokenRepository
 * @Description TODO
 * @Author pyt
 * @Date 2019/8/10 15:42
 * @Version
 */
public interface AccessTokenRepository extends JpaRepository<AccessToken,Integer> {
    @Query("select u from AccessToken u where u.accessToken = ?1")
    Optional<AccessToken> findByAccessToken(String accessToken);
}
