package cn.appscomm.l38t.utils;

import android.util.Log;

import cn.appscomm.l38t.BuildConfig;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/5 17:14
 */
public class AppLogger {

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }
}
