package cn.appscomm.l38t.app;

import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.widget.Toast;

import com.appscomm.bluetooth.app.BluetoothApplicationContext;
import com.appscomm.bluetooth.config.BluetoothConfig;
import com.appscomm.bluetooth.manage.AppsBluetoothManager;
import com.appscomm.bluetooth.manage.BluetoothConnectCheckService;
import com.facebook.drawee.backends.pipeline.Fresco;

import org.litepal.LitePalApplication;

import cn.appscomm.l38t.BuildConfig;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.constant.AppUtil;
import cn.appscomm.l38t.loader.LeaderBoardLoader;
import cn.appscomm.l38t.service.Query5YearData;
import cn.appscomm.l38t.utils.AppCrashHandler;
import cn.appscomm.l38t.utils.BackgroundThread;
import cn.appscomm.l38t.utils.Env;
import cn.appscomm.l38t.utils.LogUtil;
import cn.appscomm.l38t.utils.UniversalImageLoaderHelper;
import cn.appscomm.netlib.app.NetLibApplicationContext;
import cn.appscomm.netlib.constant.HttpCode;
import cn.appscomm.netlib.retrofit_okhttp.RequestManager;
import cn.appscomm.push.AppsCommPushService;
import cn.appscomm.uploaddata.SyncDataService;


public class GlobalApp extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        NetLibApplicationContext.getInstance().init(this);
        BluetoothApplicationContext.getInstance().init(this);
        LitePalApplication.initialize(this);
        UniversalImageLoaderHelper.init(this);
        appInit();
        Fresco.initialize(this);
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);
    }

    private void appInit() {
        //设置子工程单双核环境
        //单核机和多核机进行分离，避免工作线程抢占系统资源
        if (Env.bIsMultiProc) {
            BackgroundThread.post(new Runnable() {
                public void run() {
                    AsyncInit();
                }
            });
        } else {
            BackgroundThread.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AsyncInit();
                }
            }, 300);
        }
    }

    public static Context getAppContext() {
        return context;
    }

    private void AsyncInit() {
        if (!BuildConfig.DEBUG) {
            initCrashHandler(context);
        }
        startServices();
    }

    /**
     * 初始化服务操作
     */
    private void startServices() {
        BluetoothConfig.setBluetoothConnectCheckTime(this, 1000 * 60 * 5);   //5分钟检查一次
        if (AppUtil.haveBindDevice()) {
            SyncDataService.startService();
            AppsCommPushService.startService();
            BluetoothConnectCheckService.connectCheckDelay(1000 * 30);      //30秒后开启自动检查连接
        }
    }


    /**
     * 初始化crash的控制器
     * TODO(这里用一句话描述这个方法的作用)
     *
     * @Title:initCrashHandler
     * @author yudapei
     */
    public void initCrashHandler(final Context context) {
        // 初始化CrashHandler
        AppCrashHandler.getInstance().init(context);
        // 设置crash的log生成位置
        AppCrashHandler.getInstance().setLogPath(AppCrashHandler.APP_PATH + AppCrashHandler.CRASH_LOG_Path);
        LogUtil.e("application", AppCrashHandler.APP_PATH + AppCrashHandler.CRASH_LOG_Path);
        // 设置成当出现crash的时候不自动退出
        AppCrashHandler.getInstance().setAutomaticExit(false);
        // 设置是否保存crash日志
        AppCrashHandler.getInstance().setCanSaveLog(true);

        // 添加crash后的自定义动作
        AppCrashHandler.getInstance().setCrashListener(new AppCrashHandler.CrashListener() {

            @Override
            public void onCrashAction() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Toast.makeText(context, context.getResources().getString(R.string.app_crash_tip), Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                }).start();
                try {
                    recycle();
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                AppCrashHandler.getInstance().exitApp();
            }
        });
    }


    @Override
    public void onTerminate() {
        recycle();
        super.onTerminate();
    }

    /**
     * 退出回收工作
     */
    private void recycle() {
        LeaderBoardLoader.getInstance().release();
        RequestManager.getInstance().onDestory();
        HttpCode.getInstance(context).onDestory();
        AppsBluetoothManager.getInstance(context).killCommandRunnable();
        AppsBluetoothManager.getInstance(context).onDestroy();
        Query5YearData.stopService();
        //SyncDataService.stopService();
        //AppsCommPushService.stopService();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
