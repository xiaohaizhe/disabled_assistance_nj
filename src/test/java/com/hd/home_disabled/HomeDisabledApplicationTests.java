package com.hd.home_disabled;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;

//@RunWith(SpringRunner.class)
@SpringBootTest
public class HomeDisabledApplicationTests {

    @Test
    public void contextLoads() {
        Date end = new Date();
        Date start = new Date();
        start.setMinutes(start.getMinutes()-40);
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        System.out.println(sdf.format(start));
        System.out.println(sdf.format(end));
    }

}
