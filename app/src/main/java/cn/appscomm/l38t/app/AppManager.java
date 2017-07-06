package cn.appscomm.l38t.app;

import android.app.Activity;
import android.content.Context;

import com.appscomm.bluetooth.manage.AppsBluetoothManager;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Stack;

import cn.appscomm.l38t.loader.LeaderBoardLoader;
import cn.appscomm.l38t.service.Query5YearData;
import cn.appscomm.push.AppsCommPushService;
import cn.appscomm.uploaddata.SyncDataService;


public class AppManager {
	
	public static Stack<Activity> activityStack = new Stack<Activity>();
	private static AppManager instance;
	
	public AppManager(){}

	public static AppManager getAppManager(){
		if (null == instance) {
			synchronized (AppManager.class) {
				if (null == instance) {
					instance = new AppManager();
				}
			}
		}
		return instance;
	}

	public void addActivity(Activity activity){
		if(activityStack==null){
			activityStack=new Stack<Activity>();
		}
		if (!activityStack.contains(activity)){
			activityStack.add(activity);
		}
	}

	public Activity currentActivity(){
		try {
			if(activityStack==null){
                return null;
            }
			Activity activity=activityStack.lastElement();
			return activity;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void finishActivity(){
		if(activityStack==null){
			return;
		}
		Activity activity=activityStack.lastElement();
		finishActivity(activity);
	}

	public void finishActivity(Activity activity){
		if(activity!=null){
			if(activityStack==null){
				return;
			}
			activityStack.remove(activity);
//			activity.finish();
//			activity=null;
		}
	}

	public void removeActivity(Activity activity){
		if(activity!=null){
			if(activityStack==null){
				return;
			}
			activityStack.remove(activity);
		}
	}

	public void finishActivity(Class<?> cls){
		if(activityStack==null){
			return;
		}
		for (Activity activity : activityStack) {
			if(activity.getClass().equals(cls) ){
				finishActivity(activity);
			}
		}
	}

	public void finishAllActivity(){
		if(activityStack==null){
			return;
		}
		for (int i = 0, size = activityStack.size(); i < size; i++){
            if (null != activityStack.get(i)){
            	activityStack.get(i).finish();
            }
	    }
		activityStack.clear();
	}

	public void AppExit(Context context) {
		try {
			LeaderBoardLoader.getInstance().release();
			AppsBluetoothManager.getInstance(context).killCommandRunnable();
			AppsBluetoothManager.getInstance(context).disConnectDevice(false);
			AppsBluetoothManager.getInstance(context).onDestroy();
			ImageLoader.getInstance().clearDiskCache();//回收图片资源库
			ImageLoader.getInstance().clearMemoryCache();
			//SyncDataService.stopService();
			//AppsCommPushService.stopService();//不退出推送服务
			Query5YearData.stopService();
			finishAllActivity();
            //android.os.Process.killProcess(android.os.Process.myPid());//关闭进程
            //System.exit(0);
		} catch (Exception e) {	}
	}
}