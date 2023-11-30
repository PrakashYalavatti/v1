package com.simtech.app1.apputils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonServices {

    public static String getDateTime() {
        Date currentDate = new Date(System.currentTimeMillis());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(currentDate);
    }
}
