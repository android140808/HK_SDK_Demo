package cn.appscomm.l38t.utils.viewUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2016/8/23.
 */
public class DateDrawTool {

    // 获得当前日期与本周一相差的天数
    private static int getMondayPlus(Date date) {
        Calendar cd = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            cd.setTime(date);
        }
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            return -6;
        } else {
            return 2 - dayOfWeek;
        }
    }

    // 获得当前日期与本周日相差的天数
    private static int getSundayPlus(Date date) {
        Calendar cd = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            cd.setTime(date);
        }
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            return 0;
        }
        return 8 - dayOfWeek;
    }

    // 获得当前周(周一)的日期
    public static Date getCurrentMonday(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            calendar.setTime(date);
        }
        calendar.add(Calendar.DATE, getMondayPlus(date));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    // 获得当前周（周日）的日期
    public static Date getCurrentSunday(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            calendar.setTime(date);
        }
        calendar.add(Calendar.DATE, getSundayPlus(date));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static int getDaysOfMonth(Date date){
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            calendar.setTime(date);
        }
        return calendar.getActualMaximum(Calendar.DATE);
    }


    public static Date getNowDate(){
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        return calendar.getTime();
    }

    public static  Date getCurrentMonthBeginDate(Date date){
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        return c.getTime();
    }

    public static  Date getCurrentDateAfterDate(Date date,int after){
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        c.add(Calendar.DATE, after);
        return c.getTime();
    }

    public static  Date getCurrentDateLastMonthBeginDate(Date date){
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        c.add(Calendar.MONTH, -1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    public static  Date getCurrentDateLastMonthEndDate(Date date){
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.DATE, -1);
        return c.getTime();
    }

    public static  Date getCurrentDateNextMonthBeginDate(Date date){
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        c.add(Calendar.MONTH, 1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    public static  Date getCurrentDateNextMonthEndDate(Date date){
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        c.add(Calendar.MONTH, 2);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.DATE, -1);
        return c.getTime();
    }

    public static Date getCurrentDateLastWeekBeginDate(Date date) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        if (c.get(Calendar.DAY_OF_WEEK) != 1) {
            c.add(Calendar.WEEK_OF_YEAR, -1);
            c.set(Calendar.DAY_OF_WEEK, 2);
        } else {
            c.add(Calendar.WEEK_OF_YEAR, -2);
            c.set(Calendar.DAY_OF_WEEK, 2);
        }
        return c.getTime();
    }

    public static Date getCurrentDateNextWeekBeginDate(Date date) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        System.out.println(c.get(Calendar.DAY_OF_WEEK));
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            c.add(Calendar.WEEK_OF_YEAR,0);
            c.set(Calendar.DAY_OF_WEEK, 2);
        } else {
            c.add(Calendar.WEEK_OF_YEAR, 1);
            c.set(Calendar.DAY_OF_WEEK, 2);
        }
        return c.getTime();
    }


    public static Date getCurrentDateMonthBeginDate(Date date) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    public static Date getCurrentDateMonthEndDate(Date date) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        c.add(Calendar.MONTH, 1);
        c.set(Calendar.DAY_OF_MONTH,1);
        c.add(Calendar.DATE, -1);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    public static Date getCurrentDateNextDate(Date date) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    public static Date getCurrentDatePreDate(Date date) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        c.add(Calendar.DATE, -1);
        return c.getTime();
    }

    public static Date getCurrentDateAddMin(Date date,int min) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        c.add(Calendar.MINUTE, min);
        return c.getTime();
    }

    public static Date getCurrentDateStartTime(Date date) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    public static Date getCurrentDateStartTimeAddMin(Date date,int min) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return getCurrentDateAddMin(c.getTime(),min);
    }

    public static Date getCurrentDateEndTime(Date date) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    public static int[] getCurrentDateHourMin(Date date) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        int hour=c.get(Calendar.HOUR_OF_DAY);
        int min=c.get(Calendar.MINUTE);
        return new int[]{hour,min};
    }

    public static String getCurrentDateShowHourMin(Date date) {
        int[] hourMins=getCurrentDateHourMin(date);
        return String.format("%02d:%02d",hourMins[0],hourMins[1]);
    }

    public static int getWeekIndexOfDate(Date dt) {
        String[] weekDays = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 2;
        if (w < 0)
            w = 6;
        return w;
    }

    public static int getCurrentDateWeekOfYear(Date date) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        return c.get(Calendar.WEEK_OF_YEAR);
    }

    public static int getCurrentDateMonthOfYear(Date date) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        return c.get(Calendar.MONTH);
    }

    public static int getCurrentDateDayOfMonth(Date date) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        if (date != null) {
            c.setTime(date);
        }
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 时间转字符串, 如"yyyy-MM-dd HH:mm:ss"
     *
     * @param datetime
     * @param format
     * @return
     * @Title:dateToStr
     * @author weiliu
     */
    public static String dateToStr(Date datetime, String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(datetime);
    }

    /**
     * 获得当前日历的0点时间
     */
    public static int getTimesMorning(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int) (cal.getTimeInMillis() / 1000);
    }

    /**
     * 得到当前天在本周是第几天
     * 0-日 1-一 2-二 ... 6六
     */
    public static int getDayOfWeek(Calendar cal) {
        cal.setTime(new Date());
        return cal.get(Calendar.DAY_OF_WEEK)-1;
    }
    /**
     * 得到当前天在本月是第几天
     * 0-1号 1-2号 ……
     */
    public static int getDayOfMonth(Calendar cal) {
        cal.setTime(new Date());
        return cal.get(Calendar.DAY_OF_MONTH)-1;
    }
}
