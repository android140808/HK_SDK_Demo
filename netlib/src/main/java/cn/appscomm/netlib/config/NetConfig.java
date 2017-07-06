package cn.appscomm.netlib.config;

import android.content.Context;

/**
 * Created by Administrator on 2016/8/31.
 */
public class NetConfig extends BaseLocalConfig {

    private static final String accessToken_string = "accessToken_string";


    public static  String getAccessToken() {
        return getInstance().getString(accessToken_string, "");
    }

    public static void setAccessToken(String accessToken) {
        getInstance().saveString(accessToken_string, accessToken);
    }
}
