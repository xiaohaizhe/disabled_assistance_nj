package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ClassName UserRepository
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/5 11:00
 * @Version
 */
public interface UserRepository extends JpaRepository<User, Integer> {
}
