package com.hd.home_disabled.controller;

import com.hd.home_disabled.entity.User;
import com.hd.home_disabled.repository.UserRepository;
import com.hd.home_disabled.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName HelloController
 * @Description TODO
 * @Author pyt
 * @Date 2019/6/25 14:11
 * @Version
 */
@RestController
@RequestMapping("/api")
public class HelloController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello() {
        return "Hello!";
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public String insert(@RequestBody User user) {
        return userService.insert(user);
    }



}
