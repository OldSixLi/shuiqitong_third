package com.greatchn.common.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class TimeUtils {

    public final static String FULL_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static SimpleDateFormat FULL_DATE_TIME = new SimpleDateFormat(FULL_DATE_TIME_FORMAT);

    public final static String SIMPLE_DATE_TIME_FORMAT = "yyyyMMddHHmmss";
    public final static SimpleDateFormat SIMPLE_DATE_TIME = new SimpleDateFormat(SIMPLE_DATE_TIME_FORMAT);

    public final static String FULL_DATE_FORMAT = "yyyy-MM-dd";
    public final static SimpleDateFormat FULL_DATE = new SimpleDateFormat(FULL_DATE_FORMAT);

    public final static String SIMPLE_DATE_FORMAT = "yyyyMMdd";
    public final static SimpleDateFormat SIMPLE_DATE = new SimpleDateFormat(SIMPLE_DATE_FORMAT);

    public static String getCurrentTime(String format) {
        Calendar calendar = Calendar.getInstance();
        return format(calendar.getTime(), format);
    }

    public static String getCurrentTime(SimpleDateFormat format) {
        Calendar calendar = Calendar.getInstance();
        return format(calendar.getTime(), format);
    }

    public static String format(Date date, String format) {
        return format(date, new SimpleDateFormat(format));
    }

    public static String format(Date date, SimpleDateFormat format) {
        return format.format(date);
    }

    public static Date parse(String date, String format) throws ParseException {
        return parse(date, new SimpleDateFormat(format));
    }

    public static Date parse(String date, SimpleDateFormat format) throws ParseException {
        return format.parse(date);
    }

    public static String convert(String time, String srcFmt, String destFmt) {
        return convert(time, new SimpleDateFormat(srcFmt), new SimpleDateFormat(destFmt));
    }

    public static String convert(String time, SimpleDateFormat srcFmt, SimpleDateFormat destFmt) {
        try {
            return destFmt.format(srcFmt.parse(time));
        } catch (Exception e) {
            return time;
        }
    }

    /**
     * 获取后面日期
     *
     * @param now    当前时间
     * @param format 输入时间格式
     * @param time   时间常量
     * @return
     * @throws ParseException
     * @author 常帅博
     * @date 2017-2-20 上午10:07:03
     */
    public static String getDayAfter(String now, String format, int time, int num) throws ParseException {
        return changeDay(now, format, time, num);
    }

    /**
     * 获取前面日期
     *
     * @param now    当前时间
     * @param format 输入时间格式
     * @param time   时间常量
     * @return
     * @throws ParseException
     * @author 常帅博
     * @date 2017-2-20 上午10:06:53
     */
    public static String getDayBefore(String now, String format, int time, int num) throws ParseException {
        return changeDay(now, format, time, -num);
    }

    /**
     * 修改日期
     *
     * @param now    当前时间
     * @param format 输入时间格式
     * @param time   时间常量
     * @param num    时间偏移量（+/-）
     * @return
     * @throws ParseException
     * @author 常帅博
     * @date 2017-2-20 上午10:06:53
     */
    public static String changeDay(String now, String format, int time, int num) throws ParseException {
        Calendar c = Calendar.getInstance();
        Date date = null;
        date = new SimpleDateFormat(format).parse(now);
        c.setTime(date);
        int day = c.get(time);
        c.set(time, day + num);
        String dayBefore = new SimpleDateFormat(format).format(c.getTime());
        return dayBefore;
    }


}
