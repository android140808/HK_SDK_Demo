package cn.appscomm.l38t.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by liucheng on 2016/10/21.
 */
public class TimeZoneUtils {

    public static long getLocalTimeStamp(long timeStamp){
        long offset = (long) Calendar.getInstance().getTimeZone().getRawOffset();
        long timeStampLocal = timeStamp + offset / 1000L;
        return timeStampLocal;
    }

    public static int getDstOffset() {
        int result = 0;
        TimeZone timeZone = TimeZone.getDefault();
        Locale locale=new Locale("CHINESE","china");
        String displayName = timeZone.getDisplayName(false, TimeZone.SHORT,locale);
        LogUtil.i("timeZone", "时区" + displayName);
        if (displayName.contains("GMT")){
            String gmtHour = displayName.split("GMT")[1].split(":")[0];
            String gmtMin = displayName.split(":")[1];
            Calendar curCalendar = Calendar.getInstance();
            curCalendar.setTime(new Date());
            int curDstOffest = curCalendar.get(Calendar.DST_OFFSET);
            String calOffsetMin = ".0";
            if (Integer.valueOf(gmtMin) == 30) {
                calOffsetMin =".5";
            }else if (Integer.valueOf(gmtMin) == 45){
                calOffsetMin=".75";
            }
            result = (int) ((8-(Float.valueOf(gmtHour+calOffsetMin)+ curDstOffest/3600000))* 3600000);
            LogUtil.i("timezone", "这是时区偏移" + result / 3600000);
        }
        return result;
    }

    public synchronized static String[] getStartAndEndTime(long  time) {
        String[] result = new String[2];
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date(time));
        String[] dayData=format.split(" ");
        result[0]=dayData[0]+" 00:00:00";
        result[1]=dayData[0]+" 23:59:59";
        return result;
    }
}
