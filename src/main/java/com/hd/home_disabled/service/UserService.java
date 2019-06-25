package com.hd.home_disabled.service;

import com.hd.home_disabled.entity.User;
import com.hd.home_disabled.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author pyt
 * @Date 2019/6/25 15:05
 * @Version
 */
@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);


    public String insert(User user) {
        LOG.info("添加用户:"+user.toString());
        User userr = userRepository.save(user);
        return userr.toString();
    }
}
