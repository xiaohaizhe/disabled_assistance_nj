package com.hd.home_disabled.service;

import com.hd.home_disabled.entity.statistic.UserBlockStatistic;
import com.hd.home_disabled.repository.UserblockStatisticRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @ClassName UserBlockStatisticService
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/8 11:37
 * @Version
 */
@Service
@Transactional
public class UserBlockStatisticService {
    private final UserblockStatisticRepository userblockStatisticRepository;

    public UserBlockStatisticService(UserblockStatisticRepository userblockStatisticRepository) {
        this.userblockStatisticRepository = userblockStatisticRepository;
    }



}
