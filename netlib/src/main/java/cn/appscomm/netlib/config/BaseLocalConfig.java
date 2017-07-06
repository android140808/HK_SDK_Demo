package cn.appscomm.netlib.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cn.appscomm.netlib.app.NetLibApplicationContext;

/**
 * Created by Administrator on 2016/8/31.
 */
public class BaseLocalConfig {
    private final String spName = "baseLocalConfig";
    private static BaseLocalConfig baseLocalConfig;
    private SharedPreferences mshardPreferences = null;

    private void init() {
        if (NetLibApplicationContext.getInstance().getAppContext() == null) {
            throw new NullPointerException("必须要在Application中创建ApplicationContextUtil的init()方法");
        }
        String mSharedPreferenceName = new String(spName + "_preferences");
        mshardPreferences = NetLibApplicationContext.getInstance().getAppContext().getSharedPreferences(mSharedPreferenceName, Context.MODE_PRIVATE);
    }

    public static BaseLocalConfig getInstance() {
        if (null == baseLocalConfig) {
            synchronized (BaseLocalConfig.class) {
                if (null == baseLocalConfig) {
                    baseLocalConfig = new BaseLocalConfig();
                    baseLocalConfig.init();
                }
            }
        }
        return baseLocalConfig;
    }

    public Gson getGson() {
        return new GsonBuilder().create();
    }

    public SharedPreferences getSharedPreference() {
        return mshardPreferences;
    }

    public void saveString(String key, String value) {
        SharedPreferences.Editor mEditor = getSharedPreference().edit();
        mEditor.putString(key, value);
        SharePreferenceUtil.commitToEditor(mEditor);
    }

    public String getString(String key, String defalut) {
        return getSharedPreference().getString(key, defalut);
    }

    public void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor mEditor = getSharedPreference().edit();
        mEditor.putBoolean(key, value);
        SharePreferenceUtil.commitToEditor(mEditor);
    }

    public boolean getBoolean(String key, boolean defalut) {
        return getSharedPreference().getBoolean(key, defalut);
    }

    public void saveInt(String key, int value) {
        SharedPreferences.Editor mEditor = getSharedPreference().edit();
        mEditor.putInt(key, value);
        SharePreferenceUtil.commitToEditor(mEditor);
    }

    public int getInt(String key, int defalut) {
        return getSharedPreference().getInt(key, defalut);
    }

    public void saveLong(String key, long value) {
        SharedPreferences.Editor mEditor = getSharedPreference().edit();
        mEditor.putLong(key, value);
        SharePreferenceUtil.commitToEditor(mEditor);
    }

    public long getLong(String key, long defalut) {
        return getSharedPreference().getLong(key, defalut);
    }

    public void saveFloat(String key, float value) {
        SharedPreferences.Editor mEditor = getSharedPreference().edit();
        mEditor.putFloat(key, value);
        SharePreferenceUtil.commitToEditor(mEditor);
    }

    public float getFloat(String key, float defalut) {
        return getSharedPreference().getFloat(key, defalut);
    }

    public void saveDouble(String key, double value) {
        saveFloat(key, (float) value);
    }

    public double getDouble(String key, double defalut) {
        return (double) getFloat(key, (float) defalut);
    }

}
