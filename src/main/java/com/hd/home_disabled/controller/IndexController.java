package com.hd.home_disabled.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName IndexController
 * @Description TODO
 * @Author pyt
 * @Date 2019/10/10 10:33
 * @Version
 */
@Controller
@RequestMapping("/")
public class IndexController {
    @RequestMapping("/")
    public String index()  {
        return "login";
    }
}
