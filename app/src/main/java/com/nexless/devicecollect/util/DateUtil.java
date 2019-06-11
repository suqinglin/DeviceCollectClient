package com.nexless.devicecollect.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @date: 2019/5/20
 * @author: su qinglin
 * @description:
 */
public class DateUtil {

    public static final String FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_HH_MM_SS = "HH:mm:ss";
    public static final String FORMAT_YY_MM_DD_HH_MM = "yy-MM-dd HH:mm";

    public static String parseLongToString(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DEFAULT);
        return sdf.format(new Date(timeStamp));
    }

    public static String parseLongToString(long timeStamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(timeStamp));
    }
}
