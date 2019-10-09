package com.hd.home_disabled.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName StringUtil
 * @Description TODO
 * @Author pyt
 * @Date 2019/8/21 10:06
 * @Version
 */
public class StringUtil {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Logger logger = LogManager.getLogger(StringUtil.class);


    public static Date getDate(String s) {
        Date date = null;
        try {
            date = sdf.parse(s);
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return date;
    }

    public static String getDateString(Date date) {
        return sdf.format(date);
    }

}
