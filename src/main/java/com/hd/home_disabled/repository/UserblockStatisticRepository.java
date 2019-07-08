package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.statistic.UserBlockStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @ClassName UserblockStatisticRepository
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/8 11:37
 * @Version
 */
public interface UserblockStatisticRepository extends JpaRepository<UserBlockStatistic, Integer> {
    Optional<UserBlockStatistic> findByBlockLike(String block);
}
