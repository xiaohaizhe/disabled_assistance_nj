package com.hd.home_disabled.repository;

import com.hd.home_disabled.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
