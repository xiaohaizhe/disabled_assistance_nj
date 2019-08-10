package com.hd.home_disabled.service;

import com.hd.home_disabled.entity.DingUser;
import com.hd.home_disabled.entity.User;
import com.hd.home_disabled.repository.DingUserRepository;
import com.hd.home_disabled.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @ClassName DingUserService
 * @Description TODO
 * @Author pyt
 * @Date 2019/8/10 17:18
 * @Version
 */
@Service
public class DingUserService {
    @Autowired
    private DingUserRepository dingUserRepository;
    @Autowired
    private UserRepository userRepository;

    public void save(DingUser user){
        Optional<DingUser> dingUserOptional =dingUserRepository.findById(user.getUserId());
        if (!dingUserOptional.isPresent()){
            dingUserRepository.save(user);
        }
    }

    public Long getUserId(String dingUserId){
        Long userId = 0L;
        Optional<DingUser> dingUserOptional =dingUserRepository.findById(dingUserId);
        if (!dingUserOptional.isPresent()){
            DingUser dingUser = dingUserOptional.get();
            if (dingUser.getMobile()!=null){
                Optional<User> userOptional =userRepository.findByContactNumber(dingUser.getMobile());
                if (userOptional.isPresent()){
                    userId = userOptional.get().getId();
                }
            }
        }
        return userId;
    }
}
