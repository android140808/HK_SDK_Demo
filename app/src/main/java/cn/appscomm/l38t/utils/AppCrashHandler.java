package cn.appscomm.l38t.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.appscomm.l38t.app.GlobalApp;


public class AppCrashHandler implements UncaughtExceptionHandler {

    public static final String CRASH_LOG_Path = "/logs/crash/";
    // 保存奔溃日志开关
    private boolean canSaveLog = false;
    private boolean automaticExit = true;
    public static final String APP_PATH = "sdcard/android/data/" + GlobalApp.getAppContext().getPackageName();
    private String logPath = "";
    private UncaughtExceptionHandler mDefaultHandler;// 系统默认的UncaughtException处理类
    private static AppCrashHandler INSTANCE = new AppCrashHandler();// CrashHandler实例
    private Context mContext;// 程序的Context对象
    private Map<String, String> info = new HashMap<String, String>();// 用来存储设备信息和异常信息
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddHH-mm-ss");// 用于格式化日期,作为日志文件名的一部分

    private CrashListener crashListener;

    public interface CrashListener {
        public void onCrashAction();
    }

    /**
     * 保证只有一个PKE_CrashHandler实例
     */
    private AppCrashHandler() {

    }

    /**
     * 获取PKE_CrashHandler实例 ,单例模式
     */
    public static AppCrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化CrashHandler
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
        Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器
    }

    /**
     * 设置crash日志的文件夹地址
     * TODO(这里用一句话描述这个方法的作用)
     *
     * @param logPath
     * @Title:setLogPath
     * @author yudapei
     */
    public void setLogPath(String logPath) {
        this.logPath = logPath;
        FileUtil.newFolder(logPath);
    }

    /**
     * 当UncaughtException发生时会转入该重写的方法来处理
     */
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果自定义的没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // 退出程序
            if (automaticExit) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                exitApp();
            }
        }
    }

    /**
     * 退出app
     * <p/>
     * TODO(这里用一句话描述这个方法的作用)
     *
     * @Title:exitApp
     * @author yudapei
     */
    public void exitApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex 异常信息 true 如果处理了该异常信息;否则返回false.
     */
    public boolean handleException(Throwable ex) {
        if (ex == null)
            return false;

        if (canSaveLog) {
            // 收集设备参数信息
            collectDeviceInfo(mContext);
            // 保存日志文件
            saveCrashInfo2File(ex);
        }
        // 把action放到调用的地方定义
        if (crashListener != null) {
            crashListener.onCrashAction();
        }

        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param context
     */
    public void collectDeviceInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();// 获得包管理器
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);// 得到该应用的信息，即主Activity
            if (pi != null) {
                String versionName = pi.versionName == null ? "null"
                        : pi.versionName;
                String versionCode = pi.versionCode + "";
                info.put("versionName", versionName);
                info.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        Field[] fields = Build.class.getDeclaredFields();// 反射机制
        for (Field field : fields) {
            try {

                field.setAccessible(true);
                info.put(field.getName(), field.get("").toString());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存设备和crash的信息岛文件
     * TODO(这里用一句话描述这个方法的作用)
     *
     * @param ex
     * @return
     * @Title:saveCrashInfo2File
     * @author yudapei
     */
    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equals("FINGERPRINT") ||
                    key.equals("BOARD") ||
                    key.equals("HOST") ||
                    key.equals("versionCode") ||
                    key.equals("versionName") ||
                    key.equals("MODEL") ||
                    key.equals("CPU_ABI") ||
                    key.equals("IS_DEBUGGABLE") ||
                    key.equals("SERIAL")) {
                sb.append(key + "=" + value + "\r\n");
            }
//			Log.i("test", "key=" + key + ",value=" + value);
        }
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        // 循环着把所有的异常信息写入writer中
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();// 记得关闭
        String result = writer.toString();
        sb.append(result);
        // 保存文件
        long timetamp = System.currentTimeMillis();
        String time = format.format(new Date());
        String fileName = "crash-" + time + "-" + timetamp + ".log";
        FileUtil.writeLog(logPath, "程序出现奔溃:" + fileName + "\n奔溃详细信息:\n" + sb.toString());

        return null;
    }

    public boolean isCanSaveLog() {
        return canSaveLog;
    }

    public void setCanSaveLog(boolean canSaveLog) {
        this.canSaveLog = canSaveLog;
    }

    public boolean isAutomaticExit() {
        return automaticExit;
    }

    public void setAutomaticExit(boolean automaticExit) {
        this.automaticExit = automaticExit;
    }

    public CrashListener getCrashListener() {
        return crashListener;
    }

    public void setCrashListener(CrashListener crashListener) {
        this.crashListener = crashListener;
    }
}


