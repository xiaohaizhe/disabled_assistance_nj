package com.hd.home_disabled.controller;

import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.service.DictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName TypeController
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/4 11:01
 * @Version
 */
@Api("类型")
@RestController
@RequestMapping("/api")
public class TypeController {
    private final DictionaryService dictionaryService;

    public TypeController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @ApiOperation(value = "列表", notes = "类型列表")
    @RequestMapping(value = "/typeList", method = RequestMethod.GET)
    public JSONObject getTypeList(String name){
        return dictionaryService.getList(name);
    }
}
