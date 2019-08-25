package com.hd.home_disabled.controller;

import com.hd.home_disabled.timer.TimeTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TimeTask timeTask;

    @RequestMapping(value = "/test")
    public String test() throws NoSuchFieldException, IOException, ClassNotFoundException {
        timeTask.getAttendanceRecord();
        return "suc";
    }
}
