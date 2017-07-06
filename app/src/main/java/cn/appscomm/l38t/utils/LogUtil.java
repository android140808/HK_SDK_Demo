package cn.appscomm.l38t.utils;

import android.os.Environment;
import android.util.Log;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.appscomm.l38t.constant.APPConstant;

/**
 * Created by duzhe on 16-7-24-0024.
 */
public class LogUtil {
    private static final int DEVELOP = 0;                                                           // 开发阶段
    private static final int DEBUG = 1;                                                             // 内部测试阶段
    private static final int BATE = 2;                                                              // 公开测试
    private static final int RELEASE = 3;                                                           // 正式版
    private static SimpleDateFormat sdf = new SimpleDateFormat("MM月dd HH:mm:ss");

    private static int currentStage = APPConstant.IS_RELEASE_VERSION ? RELEASE : DEVELOP;  // 当前阶段标示

    private static String path;
    private static File file;
    private static FileOutputStream outputStream;

    public static final int TYPE_INFO = 0;
    public static final int TYPE_WRITE = 1;
    public static final int TYPE_ERROR = 2;
    public static final int TYPE_VERBOSE = 3;

    private static List<String> savePackageNames = new ArrayList<>();

    static {
        if (!APPConstant.IS_RELEASE_VERSION && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            path = externalStorageDirectory.getAbsolutePath() + "/ZeCircle2Log/";
            File directory = new File(path);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            file = new File(new File(path), "log.txt");
            try {
                outputStream = new FileOutputStream(file, true);
            } catch (FileNotFoundException e) {
            }
        }
//        savePackageNames.add(SynBluetoothDataNewService.class.getSimpleName());
    }

    public static void i(String tag, String msg, int type) {
        switch (currentStage) {
            case DEVELOP:
                switch (type) {
                    case TYPE_WRITE:
                        Log.w(tag, msg);
                        break;
                    case TYPE_ERROR:
                        Log.e(tag, msg);
                        break;
                    default:
                    case TYPE_INFO:
                        Log.i(tag, msg);
                        break;
                    case TYPE_VERBOSE:
                        Log.v(tag, msg);
                        break;
                }
                if (!savePackageNames.contains(tag) || APPConstant.IS_RELEASE_VERSION)
                    break;
            case BATE:
                // 写日志到sdcard
                String time = sdf.format(System.currentTimeMillis());
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    if (outputStream != null) {
                        String className = tag;
                        String content = "(" + time + ") : " + msg + "\r\n";
//                        writeAndFlush(("*******" + className + "(" + time + ")*******\r\n").getBytes());
                        writeAndFlush(content.getBytes());
                    }
                }
                break;
            case DEBUG:
                // 在应用下面创建目录存放日志
                break;
            case RELEASE:
                // 一般不做日志记录
                break;
        }
    }

    public static void i(String msg) {
        i("LogUtil", msg, 0);
    }

    public static void i(Class clazz, String msg, int type) {
        i(clazz.getSimpleName(), msg, type);
    }

    public static void i(Class clazz, String msg) {
        i(clazz.getSimpleName(), msg, TYPE_INFO);
    }

    public static void i(String TAG, String msg) {
        i(TAG, msg, TYPE_INFO);
    }

    public static void w(Object obj, String msg) {
        if (obj instanceof Class) {
            i(((Class) obj).getSimpleName(), msg, TYPE_WRITE);
        } else if (obj instanceof String) {
            i((String) obj, msg, TYPE_WRITE);
        }
    }

    public static void e(Object obj, String msg) {
        if (obj instanceof Class) {
            i(((Class) obj).getSimpleName(), msg, TYPE_ERROR);
        } else if (obj instanceof String) {
            i((String) obj, msg, TYPE_ERROR);
        }
    }

    public static void v(Class clazz, String msg) {
        i(clazz.getSimpleName(), msg, TYPE_VERBOSE);
    }

    public static void v(String clazz, String msg) {
        i(clazz, msg, TYPE_VERBOSE);
    }

    public static void writeAndFlush(byte[] buf) {
        try {
            outputStream.write(buf);
            outputStream.flush();
        } catch (IOException e) {
        }
    }
}
