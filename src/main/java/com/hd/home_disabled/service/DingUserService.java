package com.hd.home_disabled.service;

import com.hd.home_disabled.entity.DingUser;
import com.hd.home_disabled.entity.DingUserAttendanceRecord;
import com.hd.home_disabled.entity.User;
import com.hd.home_disabled.repository.DingUserAttendanceRecordRepository;
import com.hd.home_disabled.repository.DingUserRepository;
import com.hd.home_disabled.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
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
    @Autowired
    private DingUserAttendanceRecordRepository dingUserAttendanceRecordRepository;

    public void save(DingUser user) {
        Optional<DingUser> dingUserOptional = dingUserRepository.findById(user.getUserId());
        if (!dingUserOptional.isPresent()) {
            dingUserRepository.save(user);
        }
    }

    //根据钉钉用户id找到系统用户id
    public User getUserId(String dingUserId) {
        User user = null;
        Optional<DingUser> dingUserOptional = dingUserRepository.findById(dingUserId);
        if (dingUserOptional.isPresent()) {
            DingUser dingUser = dingUserOptional.get();
            if (dingUser.getMobile() != null) {
                System.out.println(dingUser.getMobile());
                Optional<User> userOptional = userRepository.findByContactNumber(dingUser.getMobile());
                if (userOptional.isPresent()) {
                    user = userOptional.get();
                }
            }
        }
        return user;
    }

    public List<DingUserAttendanceRecord> getDingUserAttendanceRecord() {
        Date from = new Date();
        from.setMinutes(from.getMinutes() - 2000);
        Date to = new Date();
        List<DingUserAttendanceRecord> dingUserAttendanceRecords = dingUserAttendanceRecordRepository.findByStatus((byte) 0, from, to);
        return dingUserAttendanceRecords;
    }

    public void updateDingUserAttendanceRecord(DingUserAttendanceRecord record) {
        dingUserAttendanceRecordRepository.saveAndFlush(record);
    }

    public DingUserAttendanceRecord getById(Long id) {
        Optional<DingUserAttendanceRecord> optional = dingUserAttendanceRecordRepository.findById(id);
        return optional.orElse(null);
    }

}
