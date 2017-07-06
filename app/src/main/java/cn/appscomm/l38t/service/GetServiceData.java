package cn.appscomm.l38t.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.model.database.CountSleepDB;
import cn.appscomm.l38t.model.database.CountSportDB;
import cn.appscomm.l38t.model.database.UserBindDevice;
import cn.appscomm.l38t.utils.viewUtil.DateDrawTool;
import cn.appscomm.netlib.bean.base.BaseObtainBean;
import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.bean.sleep.CountSleepData;
import cn.appscomm.netlib.bean.sleep.CountSleepObtain;
import cn.appscomm.netlib.bean.sport.CountSportData;
import cn.appscomm.netlib.bean.sport.CountSportObtain;
import cn.appscomm.netlib.constant.HttpCode;
import cn.appscomm.netlib.constant.NetLibConstant;
import cn.appscomm.netlib.retrofit_okhttp.RequestManager;
import cn.appscomm.netlib.retrofit_okhttp.interfaces.HttpResponseListener;
import cn.appscomm.netlib.util.DateUtil;

/**
 * Created by liucheng on 2017/2/10.
 */

public class GetServiceData extends Service {
    private static final String TAG = "GetServiceData";
    private final SimpleDateFormat simpleDateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Gson gson = new GsonBuilder().create();


    @Override
    public void onCreate() {
        super.onCreate();
        requestSportData();
        requestCountSleepData();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startService() {
        boolean serviceWork = isServiceWork(GlobalApp.getAppContext(), "cn.appscomm.l38t.service.GetServiceData");
        try {
            if (!serviceWork) {
                Intent intent = new Intent(GlobalApp.getAppContext(), GetServiceData.class);
                GlobalApp.getAppContext().stopService(intent);
                GlobalApp.getAppContext().startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    /**
     * 请求运动数据
     */
    public void requestSportData() {
        Date[] dates = getTime();
        final CountSportData countSportData = new CountSportData();
        countSportData.setStartTime(simpleDateFormatLocal.format(dates[0]));
        countSportData.setEndTime(simpleDateFormatLocal.format(dates[1]));
        final String deviceId = UserBindDevice.getBindDeviceId(AccountConfig.getUserId());
        countSportData.setDeviceId(deviceId);
        countSportData.setAccountId(AccountConfig.getUserLoginName());
        countSportData.setQueryType(2);
        countSportData.setTimeZone(DateUtil.getLocalTimeZoneValue()+"");
        httpGetData(countSportData);
    }

    private Date[] getTime(){
        Date startTime = DateDrawTool.getCurrentDateStartTime(new Date());
        Date endTime = DateDrawTool.getCurrentDateEndTime(new Date());
        Date[] result=new Date[2];
        result[0]=startTime;
        result[1]=endTime;
        return result;
    }

    private void httpGetData(final CountSportData countSportData) {
        //网络请求
        RequestManager.getInstance().countSportData(countSportData, new HttpResponseListener() {
            @Override
            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
                if (HttpCode.isSuccess(statusCode)) {
                    CountSportObtain countSportObtain = (CountSportObtain) baseObtainBean;
                    //将网络上下载的数据保存
                    saveCountSport(countSportObtain, countSportData.getDeviceId(), countSportData.getStartTime(), countSportData.getEndTime(), 2);
                }
            }

            @Override
            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {

            }
        });
    }

    /**
     * 保存网络上的数据
     *
     * @param countSportObtain
     * @param deviceId
     * @param startTime
     * @param endTime
     * @param timeType
     */
    private void saveCountSport(final CountSportObtain countSportObtain, final String deviceId, final String startTime, final String endTime, final int timeType) {
        CountSportDB countSportDB = new CountSportDB();
        countSportDB.setContents(gson.toJson(countSportObtain));
        countSportDB.setDeviceId(deviceId);
        countSportDB.setUserId(AccountConfig.getUserId());
        countSportDB.setDataType(timeType);
        try {
            long start = simpleDateFormatLocal.parse(startTime).getTime() / 1000;
            long end = simpleDateFormatLocal.parse(endTime).getTime() / 1000;
            countSportDB.setStartTime(start);
            countSportDB.setEndTime(end);
            if (DataSupport.where("startTime=? and endTime=? and deviceId =? and dataType=?", start + "", end + "", deviceId, timeType + "").find(CountSportDB.class).size() > 0) {
                ContentValues values = new ContentValues();
                values.put("contents", gson.toJson(countSportObtain));
                DataSupport.updateAll(CountSportDB.class, values, "startTime=? and endTime=? and deviceId =? and dataType=?", start + "", end + "", deviceId, timeType + "");
            } else {
                countSportDB.save();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void requestCountSleepData() {
        Date[] datas = getTime();
        final String deviceId = UserBindDevice.getBindDeviceId(AccountConfig.getUserId());
        final CountSleepData countSleepData = new CountSleepData();
        countSleepData.setAccountId(AccountConfig.getUserLoginName());
        countSleepData.setCustomerCode(NetLibConstant.DEFAULT_CUSTOMER_CODE);
        countSleepData.setDeviceId(deviceId);
        countSleepData.setStartTime(simpleDateFormatLocal.format(datas[0]));
        countSleepData.setEndTime(simpleDateFormatLocal.format(datas[1]));
        countSleepData.setTimeZone(com.appscomm.bluetooth.utils.DateUtil.getLocalTimeZoneValue()+"");
        countSleepData.setQueryType(1);
        httpGetCountSleepData(countSleepData);
    }

    private void httpGetCountSleepData(final CountSleepData countSleepData) {
        RequestManager.getInstance().countSleepData(countSleepData, new HttpResponseListener() {
            @Override
            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
                if (HttpCode.isSuccess(statusCode)) {
                    CountSleepObtain countSleepObtain = (CountSleepObtain) baseObtainBean;
                    saveCountSleepData(countSleepObtain, countSleepData.getDeviceId(), countSleepData.getStartTime(), countSleepData.getEndTime(), 2);
                }
            }

            @Override
            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {
            }
        });
    }

    private void saveCountSleepData(final CountSleepObtain countSleepObtain, final String deviceId, final String startTime, final String endTime, final int timeType) {
        CountSleepDB countSleepDB = new CountSleepDB();
        countSleepDB.setContents(gson.toJson(countSleepObtain));
        countSleepDB.setDeviceId(deviceId);
        countSleepDB.setUserId(AccountConfig.getUserId());
        countSleepDB.setDataType(timeType);
        try {
            long start = simpleDateFormatLocal.parse(startTime).getTime() / 1000;
            long end = simpleDateFormatLocal.parse(endTime).getTime() / 1000;
            countSleepDB.setStartTime(start);
            countSleepDB.setEndTime(end);
            if (DataSupport.where("startTime=? and endTime=? and deviceId =? and dataType=?", start + "", end + "", deviceId, timeType + "").find(CountSleepDB.class).size() > 0) {
                ContentValues values = new ContentValues();
                values.put("contents", gson.toJson(countSleepObtain));
                DataSupport.updateAll(CountSleepDB.class, values, "startTime=? and endTime=? and deviceId =? and dataType=?", start + "", end + "", deviceId, timeType + "");
            } else {
                countSleepDB.save();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
