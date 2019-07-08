package com.hd.home_disabled.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName ApiVersionCondition
 * @Description TODO
 * @Author pyt
 * @Date 2019/6/5 10:13
 * @Version
 */
public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {
    //路径中版本的前缀，使用/v[1-9]的形式
    private final static Pattern VERSION_PREFIX_PATTERN = Pattern.compile("v(\\d+)/");
    //api的版本
    @Setter
    @Getter
    private int apiVersion;

    public ApiVersionCondition(int apiVersion) {
        this.apiVersion = apiVersion;
    }

    /**
     * 将不同的筛选条件合并
     * @param apiVersionCondition 版本条件
     * @return 版本条件
     */
    @Override
    public ApiVersionCondition combine(ApiVersionCondition apiVersionCondition) {
//        return null;
//        采用最后定义优先原则
        return new ApiVersionCondition(apiVersionCondition.getApiVersion());
    }

    /**
     * 根据request查找匹配到的筛选条件
     * @param httpServletRequest Servlet
     * @return 版本条件
     */
    @Nullable
    @Override
    public ApiVersionCondition getMatchingCondition(HttpServletRequest httpServletRequest) {
//        return null;
        Matcher m = VERSION_PREFIX_PATTERN.matcher(httpServletRequest.getRequestURI());
        if(m.find()){
            Integer version = Integer.valueOf(m.group(1));
            if(version >= this.apiVersion){
                return this;
            }
        }
        return null;
    }

    /**
     * 不同筛选条件比较，用于排序
     * @param apiVersionCondition 版本条件
     * @param httpServletRequest   httpServletRequest
     * @return 版本号差值
     */
    @Override
    public int compareTo(ApiVersionCondition apiVersionCondition, HttpServletRequest httpServletRequest) {
//        return 0;
        return apiVersionCondition.getApiVersion() - this.apiVersion;
    }
}
