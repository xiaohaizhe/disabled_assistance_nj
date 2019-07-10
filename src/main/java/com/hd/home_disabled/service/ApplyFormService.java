package com.hd.home_disabled.service;

import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.model.RESCODE;
import com.hd.home_disabled.repository.ApplyFormRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName ApplyFormService
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/9 17:17
 * @Version
 */
@Service
@Transactional
public class ApplyFormService {
    private final ApplyFormRepository applyFormRepository;


    public ApplyFormService(ApplyFormRepository applyFormRepository) {
        this.applyFormRepository = applyFormRepository;
    }

    public JSONObject saveAndFlush(){
        return RESCODE.SUCCESS.getJSONRES();
    }
}
