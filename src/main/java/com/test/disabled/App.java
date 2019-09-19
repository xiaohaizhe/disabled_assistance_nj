package com.test.disabled;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
/*        Date from = new Date();
        from.setMinutes(from.getMinutes() - 10);
        Date to = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(from));
        System.out.println(sdf.format(to));*/
        System.out.println( System.getProperty("user.home") );
    }
}
