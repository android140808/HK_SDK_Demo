package cn.appscomm.l38t.config;

import cn.appscomm.netlib.config.BaseLocalConfig;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/3 14:48
 */
public class DeviceSyncConfig extends BaseLocalConfig {

    private static final String key_last_sync_time_long = "key_last_sync_time_long";
    private static final String key_auto_heart_rate_time = "key_auto_heart_rate_time";
    private static final String key_heart_rate_low_limit = "key_heart_rate_low_limit";
    private static final String key_heart_rate_high_limit = "key_heart_rate_high_limit";
    private static final String key_heart_rate_limit_enable = "key_heart_rate_limit_enable";

    private final static String SLEEP_BEDTIME_H_KEY = "SLEEP_BEDTIMEH_KEY";  //睡觉时间 hous //默认23:00
    private final static String SLEEP_BEDTIME_M_KEY = "SLEEP_BEDTIMEM_KEY";  //睡觉时间 min
    private final static String SLEEP_AWAKETIME_H_KEY = "SLEEP_AWAKETIMEH_KEY";  //起床时间 housr //默认07:00
    private final static String SLEEP_AWAKETIME_M_KEY = "SLEEP_AWAKETIMEM_KEY";  //起床时间 housr
    public final static String AUTOSLEEP_SW_ITEM_KEY = "AUTOSLEEP_SW_ITEM_KEY";     //自动睡眠开关


    public static long getLastSyncTime() {
        return getInstance().getLong(key_last_sync_time_long, 0);
    }

    public static void setLastSyncTime(long time) {
        getInstance().saveLong(key_last_sync_time_long, time);
    }

    public static int getAutoHeartRateTime() {
        return getInstance().getInt(key_auto_heart_rate_time, 30);//默认30分钟
    }

    public static void setAutoHeartRateTime(int time) {
        getInstance().saveInt(key_auto_heart_rate_time, time);
    }

    public static int getHeartLowLimit() {
        return getInstance().getInt(key_heart_rate_low_limit, 40);//默认低值为40
    }

    public static void setHeartLowLimit(int lowLimit) {
        getInstance().saveInt(key_heart_rate_low_limit, lowLimit);
    }

    public static boolean getHeartLimitEnable() {
        return getInstance().getBoolean(key_heart_rate_limit_enable, false);//默认低值为关闭
    }

    public static void setHeartLimitEnable(boolean enable) {
        getInstance().saveBoolean(key_heart_rate_limit_enable, enable);
    }

    public static int getHeartHighLimit() {
        return getInstance().getInt(key_heart_rate_high_limit, 180);//默认高值为180
    }

    public static void setHeartHighLimit(int highLimit) {
        getInstance().saveInt(key_heart_rate_high_limit, highLimit);
    }

    public final static void setTime(int bedTimeH, int bedTimeM, int awakTimeH, int awakTimeM) {
        getInstance().saveInt(SLEEP_BEDTIME_H_KEY, bedTimeH);
        getInstance().saveInt(SLEEP_BEDTIME_M_KEY, bedTimeM);
        getInstance().saveInt(SLEEP_AWAKETIME_H_KEY, awakTimeH);
        getInstance().saveInt(SLEEP_AWAKETIME_M_KEY, awakTimeM);
    }

    public final static void setAutoSleepSwitch(boolean flag) {
        getInstance().saveBoolean(AUTOSLEEP_SW_ITEM_KEY, flag);
    }

    public final static boolean getAutoSleepSwitch() {
        return getInstance().getBoolean(AUTOSLEEP_SW_ITEM_KEY, false);
    }

    public final static int getBedTimeH() {
        return getInstance().getInt(SLEEP_BEDTIME_H_KEY, 23);
    }

    public final static int getBedTimeM() {
        return getInstance().getInt(SLEEP_BEDTIME_M_KEY, 0);
    }

    public final static int getAwakTimeH() {
        return getInstance().getInt(SLEEP_AWAKETIME_H_KEY, 7);
    }

    public final static int getAwakTimeM() {
        return getInstance().getInt(SLEEP_AWAKETIME_M_KEY, 0);
    }


    public static boolean isSleepTimeInPreSet(int bedtimes, int awaketimes) {
        int prebedtimes;
        int sleep_bedTimeH = getBedTimeH();
        int sleep_bedTimeM = getBedTimeM();
        boolean isEnableAutoSleep = getAutoSleepSwitch();
        if (!isEnableAutoSleep) return false;
        prebedtimes = sleep_bedTimeH * 60 + sleep_bedTimeM;
        if (!isPreSetTimeCrossDay()) return false;
        if (bedtimes < awaketimes) {
            if (((bedtimes < prebedtimes) && (awaketimes < prebedtimes))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPreSetTimeCrossDay() {

        int sleep_bedTimeH = getBedTimeH();
        int sleep_bedTimeM = getBedTimeM();
        int sleep_awakeTimeH = getAwakTimeH();
        int sleep_awakeTimeM = getAwakTimeM();
        if ((sleep_awakeTimeH * 60 + sleep_awakeTimeM) < (sleep_bedTimeH * 60 + sleep_bedTimeM)) {
            return true;
        } else {
            return false;
        }
    }
}
