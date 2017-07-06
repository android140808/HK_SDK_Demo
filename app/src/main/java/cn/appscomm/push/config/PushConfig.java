package cn.appscomm.push.config;

import cn.appscomm.netlib.config.BaseLocalConfig;

/**
 * Created by weiliu on 2016/7/21.
 */
public class PushConfig extends BaseLocalConfig {

    private static final String MISS_CALL = "miss_call";
    private static final String INCOMING_CALL = "incoming_call";
    private static final String HANG_UP_CALL = "hang_up_call";
    private static final String ENABLE_SMS = "enable_sms";
    private static final String ENABLE_MAIL = "enable_mail";
    private static final String ENABLE_SCHEDULE = "enable_schedule";
    private static final String ANTI_LOST = "anti_lost";
    private static final String ENABLE_MM = "enable_mm";

    public static void setMissCall(boolean flag) {
        getInstance().saveBoolean(MISS_CALL, flag);
    }

    public static boolean getMissCall() {
        return getInstance().getBoolean(MISS_CALL, false);
    }

    public static void setIncomingCall(boolean flag) {
        getInstance().saveBoolean(INCOMING_CALL, flag);
    }

    public static boolean getIncomingCall() {
        return getInstance().getBoolean(INCOMING_CALL, false);
    }

    public static void setHangUpCall(boolean flag) {
        getInstance().saveBoolean(HANG_UP_CALL, flag);
    }

    public static boolean getHangUpCall() {
        return getInstance().getBoolean(HANG_UP_CALL, false);
    }

    public static void setEnableSms(boolean flag) {
        getInstance().saveBoolean(ENABLE_SMS, flag);
    }

    public static boolean getEnableSms() {
        return getInstance().getBoolean(ENABLE_SMS, false);
    }

    public static void setEnableMail(boolean flag) {
        getInstance().saveBoolean(ENABLE_MAIL, flag);
    }

    public static boolean getEnableMail() {
        return getInstance().getBoolean(ENABLE_MAIL, false);
    }

    public static void setEnableSchedule(boolean flag) {
        getInstance().saveBoolean(ENABLE_SCHEDULE, flag);
    }

    public static boolean getEnableSchedule() {
        return getInstance().getBoolean(ENABLE_SCHEDULE, false);
    }

    public static void setEnableMm(boolean flag) {
        getInstance().saveBoolean(ENABLE_MM, flag);
    }

    public static boolean getEnableMm() {
        return getInstance().getBoolean(ENABLE_MM, false);
    }

    public static void setAntiLost(boolean flag) {
        getInstance().saveBoolean(ANTI_LOST, flag);
    }

    public static boolean getAntiLost() {
        return getInstance().getBoolean(ANTI_LOST, false);
    }


}
