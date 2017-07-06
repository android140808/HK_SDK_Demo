package cn.appscomm.netlib.app;

import android.content.Context;

/**
 * Created by Administrator on 2016/8/31.
 * 必须要在Application中创建该类的初始化方法
 */
public class NetLibApplicationContext {
    private static NetLibApplicationContext netLibApplicationContext;
    private Context appContext;

    public static NetLibApplicationContext getInstance() {
        if (null == netLibApplicationContext) {
            synchronized (NetLibApplicationContext.class) {
                if (null == netLibApplicationContext) {
                    netLibApplicationContext = new NetLibApplicationContext();
                }
            }
        }
        return netLibApplicationContext;
    }

    public void init(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public Context getAppContext() {
        return appContext;
    }
}
