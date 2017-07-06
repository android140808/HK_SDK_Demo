package cn.appscomm.uploaddata;

import android.content.ContentValues;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.constant.AppUtil;
import cn.appscomm.l38t.model.database.UserBindDevice;
import cn.appscomm.l38t.utils.AppLogger;
import cn.appscomm.netlib.bean.base.BaseObtainBean;
import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.bean.heartRate.HeartDetails;
import cn.appscomm.netlib.bean.heartRate.UploadHeart;
import cn.appscomm.netlib.bean.mood.MoodFatigueDetail;
import cn.appscomm.netlib.bean.mood.QueryMoodLastTime;
import cn.appscomm.netlib.bean.mood.UploadMood;
import cn.appscomm.netlib.bean.sleep.UploadSleep;
import cn.appscomm.netlib.bean.sport.SportDetail;
import cn.appscomm.netlib.bean.sport.UploadSport;
import cn.appscomm.netlib.constant.HttpCode;
import cn.appscomm.netlib.constant.NetLibConstant;
import cn.appscomm.netlib.retrofit_okhttp.RequestManager;
import cn.appscomm.netlib.retrofit_okhttp.interfaces.HttpResponseListener;
import cn.appscomm.netlib.util.DateUtil;
import cn.appscomm.netlib.util.NetworkUtil;
import cn.appscomm.uploaddata.database.HeartDataDB;
import cn.appscomm.uploaddata.database.MoodDataDB;
import cn.appscomm.uploaddata.database.SleepDataDB;
import cn.appscomm.uploaddata.database.SportData;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/3 16:06
 */
public class UploadDataServer {
    private final static String TAG = UploadDataServer.class.getSimpleName();

//    //获取运动最新更新数据时间
//    public static void procGetSportUpLoadLastTime() {
//        AppLogger.d(TAG, "开始获取运动数据上传时间");
//        if (!NetworkUtil.isNetworkConnected(GlobalApp.getAppContext())) {
//            AppLogger.d(TAG, "无网络，不操作");
//            return;
//        }
//        final QuerySportLastTime querySportLastTime = new QuerySportLastTime();
//        String deviceId = UserBindDevice.getBindDeviceId(AccountConfig.getUserId());
//        String accountId = AccountConfig.getUserLoginName();
//        querySportLastTime.setDeviceId(deviceId);
//        querySportLastTime.setAccountId(accountId);
//        RequestManager.getInstance().querySportLastTime(querySportLastTime, new HttpResponseListener() {
//            @Override
//            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
//                if (HttpCode.isSuccess(baseObtainBean.getCode())) {
//                    AppLogger.d(TAG, "获取上传运动数据上传时间成功");
//                    long lastSportUpLoadTime = Long.parseLong(baseObtainBean.getResMap().getLastTime());
//                    Log.d(TAG, "lastSportUpLoadTime= " + lastSportUpLoadTime + ",运动数据最后上传时间:" + DateUtil.dateToSec(DateUtil.timestampToDate(lastSportUpLoadTime * 1000)));
//                    List<SportData> sportDataList = UploadDataHelper.getSportDataFromDataBase(lastSportUpLoadTime);
//                    upLoadSportData(sportDataList);
//
//                }
//            }
//
//            @Override
//            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {
//
//            }
//        });
//    }

    //获取运动最新更新数据时间
    public static void procGetSportUpLoadLastTime() {
        if (!NetworkUtil.isNetworkConnected(GlobalApp.getAppContext())) {
            AppLogger.d(TAG, "无网络，不操作");
            return;
        }
        List<SportData> sportDatas = DataSupport.where("isUpdate=?", "1").find(SportData.class);
        upLoadSportData(sportDatas);
    }

    /**
     * 上传的数据只有一条,就是需要上传的数据总数
     * @param sportDataList
     */
    public static void upLoadSportData(final List<SportData> sportDataList) {
        if (sportDataList == null || sportDataList.size() <= 0) {
            return;
        }
        SportData sportData = sportDataList.get(0);
        UploadSport upSport = new UploadSport();
        UserBindDevice bindDevice = UserBindDevice.getBindDevice(AccountConfig.getUserId());
        upSport.setAccountId(AccountConfig.getUserLoginName());
        upSport.setCustomerCode(NetLibConstant.DEFAULT_CUSTOMER_CODE);
        upSport.setDeviceType(AppUtil.getDeviceType(bindDevice.getDeviceName()));
        upSport.setTimeZone(DateUtil.getLocalTimeZoneValue());
        upSport.setDeviceId(sportData.getDeviceId());
        List<SportDetail> details = new ArrayList<SportDetail>();
        AppLogger.d(TAG, "开始上传运动数据");
        int totalStep = 0, calories = 0, distance = 0, duration = 0;
        for (int i = 0; i < sportDataList.size(); i++) {
            SportDetail detail = new SportDetail();
            SportData s = sportDataList.get(i);
            detail.setSportCalorie(s.getSportCalorie());
            detail.setSportDistance(s.getSportDistance());
            detail.setSportDuration(s.getSportDuration());
            detail.setSportSpeed(s.getSportSpeed());
            detail.setSportStep(s.getSportStep());
            detail.setStartTime(s.getStartTime());
            detail.setEndTime(s.getEndTime());
            details.add(detail);
        }
        upSport.setDetails(details);
        RequestManager.getInstance().uploadSport(upSport, new HttpResponseListener() {
            @Override
            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
                if (HttpCode.isSuccess(baseObtainBean.getCode())) {
                    AppLogger.d(TAG, "上传运动数据成功");
                    //做数据清理操作
//                    UploadDataHelper.delLeftLastSportData(sportDataList);
                    for (int i = 0; i < sportDataList.size(); i++) {
                        SportData sportData = DataSupport.where("startTime=? and endTime=?", sportDataList.get(i).getStartTime() + "", sportDataList.get(i).getEndTime() + "").findLast(SportData.class);
                        if (sportData != null) {
                            ContentValues values = new ContentValues();
                            values.put("isUpdate", 0);
                            DataSupport.updateAll(SportData.class, values, "startTime=? and endTime=?", sportDataList.get(i).getStartTime() + "", sportDataList.get(i).getEndTime() + "");
                        }
                    }

                }
            }

            @Override
            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {

            }
        });
    }

//
//    //获取睡眠最新更新数据时间
//    public static void procGetSleepUpLoadLastTime() {
//        AppLogger.d(TAG, "开始获取睡眠数据上传时间");
//        if (!NetworkUtil.isNetworkConnected(GlobalApp.getAppContext())) {
//            AppLogger.d(TAG, "无网络，不操作");
//            return;
//        }
//        final QuerySleepLastTime querySleepLastTime = new QuerySleepLastTime();
//        String deviceId = UserBindDevice.getBindDeviceId(AccountConfig.getUserId());
//        String accountId = AccountConfig.getUserLoginName();
//        querySleepLastTime.setDeviceId(deviceId);
//        querySleepLastTime.setAccountId(accountId);
//        RequestManager.getInstance().querySleepLastTime(querySleepLastTime, new HttpResponseListener() {
//            @Override
//            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
//                if (HttpCode.isSuccess(baseObtainBean.getCode())) {
//                    AppLogger.d(TAG, "获取上传睡眠数据上传时间成功");
//                    final String lastSleepUpLoadTime = baseObtainBean.getResMap().getLastTime();
//                    AppLogger.d(TAG, "睡眠数据最后上传时间 lastSportUpLoadTime= " + DateUtil.dateToSec(DateUtil.timestampToDate(Long.parseLong(lastSleepUpLoadTime) * 1000l)));
//                    final List<SleepDataDB> sleepDatas = UploadDataHelper.getSleepDataFromDataBase(lastSleepUpLoadTime);
//                    upLoadSleepData(sleepDatas);
//
//                }
//            }
//
//            @Override
//            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {
//
//            }
//        });
//    }

    //获取睡眠最新更新数据时间
    public static void procGetSleepUpLoadLastTime() {
        if (!NetworkUtil.isNetworkConnected(GlobalApp.getAppContext())) {
            AppLogger.d(TAG, "无网络，不操作");
            return;
        }
        List<SleepDataDB> sleepDatas = DataSupport.where("isUpdate=?", "1").find(SleepDataDB.class);
        upLoadSleepData(sleepDatas);
    }

    public static void upLoadSleepData(final List<SleepDataDB> sleepDataList) {
        if (sleepDataList == null || sleepDataList.size() <= 0) {
            return;
        }
        UploadSleep upSleep = new UploadSleep();
        upSleep.setAccountId(AccountConfig.getUserLoginName());
        upSleep.setCustomerCode(NetLibConstant.DEFAULT_CUSTOMER_CODE);
        upSleep.setTimeZone(DateUtil.getLocalTimeZoneValue());
        List<UploadSleep.SleepDetails> sleeps = UploadDataHelper.getSleepDetailsData(sleepDataList);
        if (sleeps == null && sleeps.size() < 0) {
            return;
        }
        for (int i = 0; i < sleeps.size(); i++) {
            sleeps.get(i).getSleepDuration();
        }
        upSleep.setSleeps(sleeps);
        RequestManager.getInstance().uploadSleep(upSleep, new HttpResponseListener() {
            @Override
            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
                if (HttpCode.isSuccess(baseObtainBean.getCode())) {
                    AppLogger.d(TAG, "上传睡眠数据成功");
                    for (int i = 0; i < sleepDataList.size(); i++) {
                        SleepDataDB sleepDataDB = DataSupport.where("startTime=?",  sleepDataList.get(i).getStartTime() + "").findLast(SleepDataDB.class);
                        if (sleepDataDB != null) {
                            ContentValues values = new ContentValues();
                            values.put("isUpdate", 0);
                            DataSupport.updateAll(SleepDataDB.class, values, "startTime=?",sleepDataList.get(i).getStartTime() + "");
                        }
                    }
//                    UploadDataHelper.delLeftLastSleeptData(sleepDataList);
                }
            }

            @Override
            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {
                AppLogger.d(TAG, "上传睡眠数据失败，原因=" + e.getMessage());
            }
        });
    }

    //获取心率最新更新数据时间
    public static void procGetHeartUpLoadLastTime() {
        AppLogger.d(TAG, "开始获取心率数据上传时间");
        if (!NetworkUtil.isNetworkConnected(GlobalApp.getAppContext())) {
            AppLogger.d(TAG, "无网络，不操作");
            return;
        }
        List<HeartDataDB> heartDataDBs = DataSupport.where("isUpdate=?", "1").find(HeartDataDB.class);
        upLoadHeartData(heartDataDBs);
//        final QueryHeartLastTime queryHeartLastTime = new QueryHeartLastTime();
//        String deviceId = UserBindDevice.getBindDeviceId(AccountConfig.getUserId());
//        String accountId = AccountConfig.getUserLoginName();
//        queryHeartLastTime.setDeviceId(deviceId);
//        queryHeartLastTime.setAccountId(accountId);
//        RequestManager.getInstance().queryHeartLastTime(queryHeartLastTime, new HttpResponseListener() {
//            @Override
//            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
//                if (HttpCode.isSuccess(baseObtainBean.getCode())) {
//                    AppLogger.d(TAG, "获取上传心率数据上传时间成功");
//                    String lastHeartUpLoadTime = baseObtainBean.getResMap().getLastTime();
//                    AppLogger.d(TAG, "心率数据最后上传时间 lastSportUpLoadTime= " + DateUtil.dateToSec(DateUtil.timestampToDate(Long.parseLong(lastHeartUpLoadTime) * 1000)));
//                    List<HeartDataDB> heartDataDBList = UploadDataHelper.getHeartDataFromDataBase(lastHeartUpLoadTime);
//                    upLoadHeartData(heartDataDBList);
//                }
//            }
//
//            @Override
//            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {
//
//            }
//        });

    }

    private static void upLoadHeartData(final List<HeartDataDB> heartDataList) {
        if (heartDataList == null || heartDataList.size() <= 0) {
            return;
        }
        UploadHeart upHeart = new UploadHeart();
        upHeart.setAccountId(AccountConfig.getUserLoginName());
        upHeart.setCustomerCode(NetLibConstant.DEFAULT_CUSTOMER_CODE);
        upHeart.setTimeZone(DateUtil.getLocalTimeZoneValue());
        upHeart.setDeviceType(NetLibConstant.DEFAULT_DEVICE_TYPE);
        upHeart.setDeviceId(UserBindDevice.getBindDeviceId(AccountConfig.getUserId()));
        List<HeartDetails> heartDetails = new ArrayList<HeartDetails>();
        AppLogger.d(TAG, "开始上传心率数据");
        for (int i = 0; i < heartDataList.size(); i++) {
            HeartDetails detail = new HeartDetails();
            HeartDataDB heartDataDB = heartDataList.get(i);
            detail.setHeartAvg(heartDataDB.getHeartAvg());
            detail.setHeartMax(heartDataDB.getHeartMax());
            detail.setHeartMin(heartDataDB.getHeartMin());
            detail.setStartTime(heartDataDB.getStartTime());
            detail.setEndTime(heartDataDB.getEndTime());
            heartDetails.add(detail);
        }
        upHeart.setDetails(heartDetails);
        RequestManager.getInstance().uploadHeart(upHeart, new HttpResponseListener() {
            @Override
            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
                if (HttpCode.isSuccess(baseObtainBean.getCode())) {
                    AppLogger.d(TAG, "上传心率数据成功");
//                    UploadDataHelper.delLeftLastHeartData(heartDataList);
                    for (int i = 0; i < heartDataList.size(); i++) {
                        HeartDataDB heartDataDB = DataSupport.where("startTime=?", heartDataList.get(i).getStartTime() + "").findLast(HeartDataDB.class);
                        if (heartDataDB != null) {
                            ContentValues values = new ContentValues();
                            values.put("isUpdate", 0);
                            DataSupport.updateAll(HeartDataDB.class, values, "startTime=?", heartDataList.get(i).getStartTime() + "");
                        }
                    }
                }
            }

            @Override
            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {

            }
        });
    }

    //获取心情最新更新数据时间
    public static void procGetMoodUpLoadLastTime() {
        AppLogger.d(TAG, "开始获取心情疲劳度数据上传时间");
        if (!NetworkUtil.isNetworkConnected(GlobalApp.getAppContext())) {
            AppLogger.d(TAG, "无网络，不操作");
            return;
        }
        final QueryMoodLastTime queryMoodLastTime = new QueryMoodLastTime();
        String deviceId = UserBindDevice.getBindDeviceId(AccountConfig.getUserId());
        String accountId = AccountConfig.getUserLoginName();
        queryMoodLastTime.setDeviceId(deviceId);
        queryMoodLastTime.setAccountId(accountId);
        RequestManager.getInstance().queryMoodLastTime(queryMoodLastTime, new HttpResponseListener() {
            @Override
            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
                if (HttpCode.isSuccess(baseObtainBean.getCode())) {
                    AppLogger.d(TAG, "获取上传心情疲劳度数据上传时间成功");
                    String lastMoodUpLoadTime = baseObtainBean.getResMap().getLastTime();
                    AppLogger.d(TAG, lastMoodUpLoadTime + "  心情疲劳度数据上传时间 lastMoodUpLoadTime= " + DateUtil.dateToSec(DateUtil.timestampToDate(Long.parseLong(lastMoodUpLoadTime) * 1000)));
                    List<MoodDataDB> moodDataDBs = UploadDataHelper.getMoodDataFromDataBase(lastMoodUpLoadTime);
                    upLoadMoodData(moodDataDBs);
                }
            }

            @Override
            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {

            }
        });
    }


    private static void upLoadMoodData(final List<MoodDataDB> moodDataList) {
        if (moodDataList == null || moodDataList.size() <= 0) {
            return;
        }
        UploadMood upMood = new UploadMood();
        UserBindDevice bindDevice = UserBindDevice.getBindDevice(AccountConfig.getUserId());
        upMood.setAccountId(AccountConfig.getUserLoginName());
        upMood.setCustomerCode(NetLibConstant.DEFAULT_CUSTOMER_CODE);
        upMood.setTimeZone(DateUtil.getLocalTimeZoneValue());
        upMood.setDeviceType(AppUtil.getDeviceType(bindDevice.getDeviceName()));
        upMood.setDeviceId(UserBindDevice.getBindDeviceId(AccountConfig.getUserId()));
        List<MoodFatigueDetail> moodDetails = new ArrayList<MoodFatigueDetail>();
        AppLogger.d(TAG, "开始上传心情疲劳度数据");
        for (int i = 0; i < moodDataList.size(); i++) {
            MoodFatigueDetail detail = new MoodFatigueDetail();
            MoodDataDB moodDataDB = moodDataList.get(i);
            detail.setFatigueStatus(moodDataDB.getFatigueStatus());
            detail.setMoodStatus(moodDataDB.getMoodStatus());
            detail.setStartTime(moodDataDB.getStartTime());
            moodDetails.add(detail);
        }
        upMood.setDetails(moodDetails);
        RequestManager.getInstance().uploadMood(upMood, new HttpResponseListener() {
            @Override
            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
                if (HttpCode.isSuccess(baseObtainBean.getCode())) {
                    AppLogger.d(TAG, "上传心情疲劳度数据成功");
                    UploadDataHelper.delLeftLastMoodData(moodDataList);
                }
            }

            @Override
            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {

            }
        });
    }

}
