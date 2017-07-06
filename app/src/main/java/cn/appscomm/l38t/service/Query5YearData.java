package cn.appscomm.l38t.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.constant.APPConstant;
import cn.appscomm.l38t.model.database.UserBindDevice;
import cn.appscomm.l38t.utils.CommonUtils;
import cn.appscomm.l38t.utils.TimeZoneUtils;
import cn.appscomm.netlib.bean.base.BaseObtainBean;
import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.bean.sleep.QuerySleep;
import cn.appscomm.netlib.bean.sleep.QuerySleepObtain;
import cn.appscomm.netlib.bean.sleep.SleepData;
import cn.appscomm.netlib.bean.sleep.SleepDetail;
import cn.appscomm.netlib.bean.sport.QuerySport;
import cn.appscomm.netlib.bean.sport.SportDetail;
import cn.appscomm.netlib.bean.sport.UploadSportObtain;
import cn.appscomm.netlib.config.BaseLocalConfig;
import cn.appscomm.netlib.retrofit_okhttp.RequestManager;
import cn.appscomm.netlib.retrofit_okhttp.interfaces.HttpResponseListener;
import cn.appscomm.uploaddata.database.SleepDataDB;
import cn.appscomm.uploaddata.database.SportData;

/**
 * Created by liucheng on 2016/10/21.
 */
public class Query5YearData extends Service {
    private String TAG = this.getClass().getName();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String deviceId = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            deviceId = UserBindDevice.getBindDevice(AccountConfig.getUserId()).getDeviceId();
            init();
        } catch (Exception e) {
            e.printStackTrace();
            this.stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public static void startService() {
        boolean isQuery = BaseLocalConfig.getInstance().getBoolean(APPConstant.QUERY_SETTING, false);
        boolean serviceWork = CommonUtils.isServiceWork(GlobalApp.getAppContext(), "cn.appscomm.l38t.service.Query5YearData");
        try {
            if (isQuery && !serviceWork) {
                Intent intent = new Intent(GlobalApp.getAppContext(), Query5YearData.class);
                GlobalApp.getAppContext().stopService(intent);
                GlobalApp.getAppContext().startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopService() {
        Intent intent = new Intent(GlobalApp.getAppContext(), Query5YearData.class);
        GlobalApp.getAppContext().stopService(intent);
    }


    private void init() {
        BaseLocalConfig.getInstance().saveString(APPConstant.QUERY_DEVICE_ID, deviceId);
        queryData();
    }

    private void queryData() {
        boolean isQuery = BaseLocalConfig.getInstance().getBoolean(APPConstant.QUERY_SETTING, false);
        long baseTime = BaseLocalConfig.getInstance().getLong(APPConstant.QUERY_TIME_BASE, System.currentTimeMillis());
        long queryTime = BaseLocalConfig.getInstance().getLong(APPConstant.QUERY_TIME, System.currentTimeMillis());
        if (isQuery && baseTime - queryTime <= 157680000000L && !TextUtils.isEmpty(deviceId)) {//小于5年
            QuerySportData();
        } else {
            BaseLocalConfig.getInstance().saveBoolean(APPConstant.QUERY_SETTING, false);
            this.stopSelf();
        }
    }

    private void QuerySleepData() {
        String user = BaseLocalConfig.getInstance().getString(APPConstant.QUERY_USER, AccountConfig.getUserLoginName());
        QuerySleep querySleep = new QuerySleep();
        querySleep.setDeviceId(deviceId);
        querySleep.setAccountId(user);
        long time = BaseLocalConfig.getInstance().getLong(APPConstant.QUERY_TIME, System.currentTimeMillis());
        String[] startAndEndTime = TimeZoneUtils.getStartAndEndTime(time);
        querySleep.setStartTime(startAndEndTime[0]);
        querySleep.setEndTime(startAndEndTime[1]);
        querySleep.setTimeZone("null");
    }

    private void QuerySportData() {
        String user = BaseLocalConfig.getInstance().getString(APPConstant.QUERY_USER, AccountConfig.getUserLoginName());
        QuerySport querySport = new QuerySport();
        querySport.setDeviceId(deviceId);
        querySport.setAccountId(user);
        long time = BaseLocalConfig.getInstance().getLong(APPConstant.QUERY_TIME, System.currentTimeMillis());
        String[] startAndEndTime = TimeZoneUtils.getStartAndEndTime(time);
        querySport.setStartTime(startAndEndTime[0]);
        querySport.setEndTime(startAndEndTime[1]);
        querySport.setTimeZone("null");
        RequestManager.getInstance().querySport(querySport, new HttpResponseListener() {
            @Override
            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
                if (statusCode == 0 && baseObtainBean != null) {
                    UploadSportObtain baseObtainBean1 = (UploadSportObtain) baseObtainBean;
                    ArrayList<SportDetail> details = baseObtainBean1.getDetails();
                    if (details != null && details.size() > 0) {
                        for (int i = 0; i < details.size(); i++) {
                            SportData sportData = new SportData();
                            sportData.setIsUpdate(0);
                            sportData.setDeviceId(UserBindDevice.getBindDevice(AccountConfig.getUserId()).getDeviceId());
                            sportData.setUserId(AccountConfig.getUserId());
                            sportData.setSportStep(details.get(i).getSportStep());
                            sportData.setSportSpeed(details.get(i).getSportSpeed());
                            sportData.setSportDistance(details.get(i).getSportDistance());
                            sportData.setSportCalorie(details.get(i).getSportCalorie());
                            sportData.setSportDuration(details.get(i).getSportDuration());
                            sportData.setStartTime(details.get(i).getStartTime());
                            sportData.setEndTime(details.get(i).getEndTime());
                            SportData last = DataSupport.where("startTime=? and endTime=? and userId=?", details.get(i).getStartTime() + "", details.get(i).getEndTime() + "", AccountConfig.getUserId() + "").findLast(SportData.class);
                            if (last != null) {
                                ContentValues values = new ContentValues();
                                values.put("isUpdate", 0);
                                values.put("deviceId", UserBindDevice.getBindDevice(AccountConfig.getUserId()).getDeviceId());
                                values.put("userId", AccountConfig.getUserId());
                                values.put("sportStep", details.get(i).getSportStep());
                                values.put("sportDistance", details.get(i).getSportDistance());
                                values.put("sportSpeed", details.get(i).getSportSpeed());
                                values.put("sportCalorie", details.get(i).getSportCalorie());
                                values.put("sportDuration", details.get(i).getSportDuration());
                                values.put("startTime", details.get(i).getStartTime());
                                values.put("endTime", details.get(i).getEndTime());
                                DataSupport.updateAll(SportData.class, values, "startTime=? and endTime=? and userId=?", details.get(i).getStartTime() + "", details.get(i).getEndTime() + "", AccountConfig.getUserId() + "");
                            } else {
                                sportData.save();
                            }
                        }
                    }
                }
                QuerySleepData();
            }

            @Override
            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {
                stopService();
            }
        });
    }

}
