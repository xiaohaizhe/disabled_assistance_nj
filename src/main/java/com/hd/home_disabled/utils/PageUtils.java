package com.hd.home_disabled.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName PageUtils
 * @Description TODO
 * @Author pyt
 * @Date 2019/7/8 18:03
 * @Version
 */
public class PageUtils {
    public static Pageable getPage(Integer page, Integer number,String sorts){
        List<Sort.Order> orders = new ArrayList<>();
        if (sorts.equals("")){
            return new PageRequest(page-1,number);
        }else{
            for(String sort : sorts.split(",") ){
                orders.add(new Sort.Order(Sort.Direction.DESC, sort));
            }
            return new PageRequest(page - 1, number, new Sort(orders));
        }
    }
}
