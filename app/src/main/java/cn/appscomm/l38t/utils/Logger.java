package cn.appscomm.l38t.utils;


import android.util.Log;


/**
 * Created by Avater.Wang
 * On 2017/5/26  19:59
 * E-mail: 183784079790@163.com
 */
public class Logger {

    public static boolean weatherPrint = true;
    static String className;//类名
    static String methodName;//方法名
    static int lineNumber;//行数

    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    private static String createLog(String log) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(methodName);
        buffer.append("(").append(className).append(":").append(lineNumber).append(")");
        buffer.append(log);
        return buffer.toString();
    }

    public static void d(String tag, String msg) {
        if (!weatherPrint)
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.i(className + "3PLUS", createLog(msg));
    }

    public static void v(String tag, String msg) {
        if (!weatherPrint)
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.v(className + "3PLUS", createLog(msg));
    }

    public static void i(String tag, String msg) {

        if (!weatherPrint)
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.i(className + "3PLUS", createLog(msg));
    }

    public static void w(String tag, String msg) {

        if (!weatherPrint)
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.w(className + "3PLUS", createLog(msg));
    }

    public static void e(String tag, String msg) {

        if (!weatherPrint)
            return;

        getMethodNames(new Throwable().getStackTrace());
        Log.e(className + "3PLUS", createLog(msg));
    }

    public static boolean isLoggable(String tag, int level) {
        //return Log.isLoggable(tag.substring(0, Math.min(23, tag.length())), level);
        return true;
    }
}

