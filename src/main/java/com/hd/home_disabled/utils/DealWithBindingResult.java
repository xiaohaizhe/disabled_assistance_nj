package com.hd.home_disabled.utils;

import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.model.RESCODE;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName DealWithBindingResult
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/4 16:04
 * @Version
 */
public class DealWithBindingResult {
    public static JSONObject dealWithBindingResult(BindingResult br){
        if (br.hasErrors()) {
            Set<String> errorMsg = new HashSet<>();
            List<FieldError> errors = br.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.add(error.getDefaultMessage());
            }
            return RESCODE.FAILURE.getJSONRES(errorMsg);
        }else {
            return null;
        }
    }
}
