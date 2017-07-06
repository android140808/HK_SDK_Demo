package cn.appscomm.l38t.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.appscomm.l38t.app.GlobalApp;

/**
 * Created by liucheng on 2017/2/23.
 */

public class NotificationProtect extends Service {


    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public NotificationProtect getService() {
            return NotificationProtect.this;
        }
    }


    @Override
    public void onCreate() {
        //每个小时监测一次，如果通知监测没有在运行则启动，若已在运行，则下一个小时再进行监测。
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                boolean work = isWork();
                if (!work) {
                    startNotification();
                }
            }
        },0,1000);
    }

    private boolean isWork() {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) GlobalApp.getAppContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() > 0) {
            for (int i = 0; i < myList.size(); i++) {
                String mName = myList.get(i).service.getClassName().toString();
                if (mName.equals("cn.appscomm.push.NotificationObserver")) {
                    isWork = true;
                    break;
                }
            }
        }
        return isWork;
    }

    private void startNotification() {
        boolean flag;
        do {
            try {
                PackageManager pm = getPackageManager();
                ComponentName componentName = new ComponentName(this, NotificationProtect.class);
                pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                flag = true;
            } catch (Exception e) {
                flag = false;
                SystemClock.sleep(300);
            }
        } while (!flag);
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
