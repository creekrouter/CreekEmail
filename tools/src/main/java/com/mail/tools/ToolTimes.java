package com.mail.tools;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ToolTimes {


    /**
     * 时间戳格式转换
     */
    static String dayNames[] = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    public static String getMailTimeFormate(long timesamp) {
        String result = "";
        Calendar todayCalendar = Calendar.getInstance();
        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTimeInMillis(timesamp);
        long GMT = 8*60*60*1000;//时差
        long betweenDate = (todayCalendar.getTimeInMillis()+GMT)/(60*60*24*1000)-(timesamp+GMT)/(60*60*24*1000);

        String timeFormat = "M月d日 HH:mm";
        String yearTimeFormat = "yyyy年M月d日 HH:mm";

        timeFormat = "M-dd";
        yearTimeFormat = "yyyy-MM-dd";

        boolean yearTemp = todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR);
        if (yearTemp || betweenDate<7) {
            if (betweenDate<7) {
                switch ((int) betweenDate) {
                    case 0:
                        result = getHourAndMin(timesamp);
                        break;
                    case 1:
                        // result = "昨天 " + getHourAndMin(timesamp);
                        result = "昨天 ";
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        if (isCustomLegal(todayCalendar.get(Calendar.DAY_OF_WEEK),otherCalendar.get(Calendar.DAY_OF_WEEK))){
                            result = dayNames[otherCalendar.get(Calendar.DAY_OF_WEEK) - 1];
                        }else {
                            if (yearTemp){
                                result = getTime(timesamp, timeFormat);
                            }else {
                                result = getYearTime(timesamp, yearTimeFormat);
                            }
                        }
                        break;
                    default:
                        if (yearTemp){
                            result = getTime(timesamp, timeFormat);
                        }else {
                            result = getYearTime(timesamp, yearTimeFormat);
                        }
                        break;
                }
            } else {
                result = getTime(timesamp, timeFormat);
            }
        } else {
            result = getYearTime(timesamp, yearTimeFormat);
        }
        return result;
    }

    private static boolean isCustomLegal(int today,int otherdDay){
        if (otherdDay == Calendar.SUNDAY || otherdDay == Calendar.SATURDAY){
            return false;
        }
        if (today == Calendar.SUNDAY){
            return true;
        }
        if (today > otherdDay){
            return true;
        }
        return false;
    }

    /**
     * 当天的显示时间格式
     *
     * @param time
     * @return
     */
    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    /**
     * 不同一周的显示时间格式
     *
     * @param time
     * @param timeFormat
     * @return
     */
    public static String getTime(long time, String timeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        return format.format(new Date(time));
    }

    /**
     * 不同年的显示时间格式
     *
     * @param time
     * @param yearTimeFormat
     * @return
     */
    public static String getYearTime(long time, String yearTimeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(yearTimeFormat);
        return format.format(new Date(time));
    }

}
