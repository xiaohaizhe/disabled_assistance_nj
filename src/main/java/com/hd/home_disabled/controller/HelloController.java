package com.hd.home_disabled.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName HelloController
 * @Description TODO
 * @Author pyt
 * @Date 2019/6/25 14:11
 * @Version
 */
@RestController
@RequestMapping("/")
public class HelloController {
    @ApiOperation(value = "测试", notes = "测试")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String hello() {
        return "Hello!";
    }
}
