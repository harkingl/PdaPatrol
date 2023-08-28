package com.pda.patrol.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final String TAG = "DateUtil";

    public static final String FORMAT_YYYYMMDDTHHMMSSTZD = "yyyy-MM-dd'T'HH:mm:ssz";
    public static final String FORMAT_YYYYMMDDHHMM = "yyyy.MM.dd HH:mm";
    public static final String FORMAT_YYYYMMDD = "yyyy.MM.dd";
    public static String convertTimeFormat(String time, String sourceFormat, String targetFormat) {
        try {
            Date date = new SimpleDateFormat(sourceFormat).parse(time);
            return new SimpleDateFormat(targetFormat).format(date);
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }

        return time;
    }
}
