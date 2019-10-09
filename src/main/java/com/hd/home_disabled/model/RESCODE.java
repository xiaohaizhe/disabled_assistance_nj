package com.hd.home_disabled.model;

import com.alibaba.fastjson.JSONObject;
import com.hd.home_disabled.utils.Constants;

public enum RESCODE {
    SUCCESS(0, "成功"),
    FAILURE(1,"失败"),
    ORGANIZATION_ID_NOT_EXIST(1,"机构不存在"),
    ADMIN_ID_NOT_EXIST(1,"管理员不存在"),
    ADMIN_ID_EXIST(1,"管理员已存在"),
    NATURE_OF_HOUSING_PROPERTY_RIGHT_ID_NOT_EXIST(1,"房屋产权性质不存在"),
    DISABILITY_DEGREE_NOT_EXIST(1,"残疾程度不存在"),
    TYPE_OF_DISABILITY_NOT_EXIST(1,"残疾类别不存在"),
    NURSING_MODE_NOT_EXIST(1,"托养方式不存在"),
    USER_ID_NOT_EXIST(1,"残疾人不存在"),
    USER_EXIST(1,"用户已存在"),
    PROJECTTYPE_NOT_EXIST(1,"项目类型不存在"),
    PROJECT_ID_NOT_EXIST(1,"服务项目不存在"),
    APPLY_FORM_NOT_EXIST(1,"补贴申请不存在"),
    APPLY_FORM_CANT_MODIFY(1,"补贴申请不可修改"),
    TIME_PARSE_FAILURE(1,"时间格式转换失败"),
    IO_ERROR(1,"文件读取失败"),
    FORMAT_ERROR(1,"格式转换错误"),
    CLOCK_IN_WRONG(1,"打卡时间错误");
    private int code;
    private String msg;


    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    RESCODE(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    /**
     * 最新的返回json
     */
    public JSONObject getJSONRES(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.RESPONSE_CODE_KEY, this.code);
        jsonObject.put(Constants.RESPONSE_MSG_KEY, this.msg);
        return jsonObject;
    }

    public JSONObject getJSONRES(String msg){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.RESPONSE_CODE_KEY, this.code);
        jsonObject.put(Constants.RESPONSE_MSG_KEY, msg);
        return jsonObject;
    }

    public JSONObject getJSONRES(Object entity){
        JSONObject jsonres = getJSONRES();
        jsonres.put(Constants.RESPONSE_DATA_KEY, entity);
        return jsonres;
    }

    public JSONObject getJSONRES(Object entity, Integer pages, Long count){
        JSONObject jsonres = getJSONRES();
        jsonres.put(Constants.RESPONSE_DATA_KEY, entity);
        jsonres.put(Constants.RESPONSE_SIZE_KEY, pages);
        jsonres.put(Constants.RESPONSE_REAL_SIZE_KEY, count);
        return jsonres;
    }
}
