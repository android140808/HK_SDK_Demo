package cn.appscomm.uploaddata;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.appscomm.bluetooth.bean.HeartData;
import com.appscomm.bluetooth.bean.SleepData;
import com.appscomm.bluetooth.bean.SportsData;
import com.appscomm.bluetooth.interfaces.FindPhoneCallback;
import com.appscomm.bluetooth.interfaces.MusicCallback;
import com.appscomm.bluetooth.manage.AppsBluetoothManager;
import com.appscomm.bluetooth.manage.GlobalDataManager;
import com.appscomm.bluetooth.manage.GlobalVarManager;
import com.appscomm.bluetooth.protocol.command.base.BaseCommand;
import com.appscomm.bluetooth.protocol.command.clear.ClearHeartData;
import com.appscomm.bluetooth.protocol.command.clear.ClearSleepData;
import com.appscomm.bluetooth.protocol.command.clear.ClearSportData;
import com.appscomm.bluetooth.protocol.command.count.SportSleepCount;
import com.appscomm.bluetooth.protocol.command.data.DeviceDisplaySportSleep;
import com.appscomm.bluetooth.protocol.command.data.GetSleepData;
import com.appscomm.bluetooth.protocol.command.data.GetSportData;
import com.appscomm.bluetooth.protocol.command.data.SportSleepMode;
import com.appscomm.bluetooth.protocol.command.device.BatteryPower;
import com.appscomm.bluetooth.protocol.command.device.DateTime;
import com.appscomm.bluetooth.protocol.command.device.Unit;
import com.appscomm.bluetooth.protocol.command.setting.GoalsSetting;
import com.appscomm.bluetooth.utils.DateUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.appscomm.l38t.Command.GetHeartRateCount;
import cn.appscomm.l38t.Command.GetHeartRateData;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.config.AppConfig;
import cn.appscomm.l38t.config.GoalConfig;
import cn.appscomm.l38t.config.bean.LocalUserGoal;
import cn.appscomm.l38t.constant.AppUtil;
import cn.appscomm.l38t.eventbus.GlobalEvent;
import cn.appscomm.l38t.extensioncommand.UVValue;
import cn.appscomm.l38t.model.database.UserBindDevice;
import cn.appscomm.l38t.utils.AppLogger;
import cn.appscomm.l38t.utils.BackgroundThread;
import cn.appscomm.l38t.utils.ChooseDevices;
import cn.appscomm.l38t.utils.FindPhoneUtils;
import cn.appscomm.l38t.utils.MusicUtils;
import cn.appscomm.uploaddata.constant.SyncContant;
import cn.appscomm.uploaddata.database.HeartDataDB;
import cn.appscomm.uploaddata.database.MoodDataDB;
import cn.appscomm.uploaddata.database.SleepDataDB;
import cn.appscomm.uploaddata.database.SportData;


/**
 * 主要功能：
 * <p/>
 * 1.从服务器中获取上传上传成功的数据的时间
 * 然后查询本地数据库，有这时间以后的就上传上去，没有就算
 * 2.查询本地数据库最新的数据时间，有就从设备（手环）获取改时间之后的数据，没有就获取全部上传时间以后的数据
 * 3.该部分添加了 寻找手机的触发，音乐控制的触发。（2017.6.26）
 * <p/>
 * <p/>
 * Created by wy_ln on 2016/6/14.
 */
public class SyncDataService extends Service implements FindPhoneCallback, MusicCallback {
    private final static String TAG = SyncDataService.class.getSimpleName();
    private boolean mInit = false;
    private boolean mbRegister = false;

    public static final String ACTION_DEVICE_SYNC_NOW = "SyncDataService." + SyncContant.APP_NAME + ".ACTION_DEVICE_SYNC_NOW";
    public static final String ACTION_UPLOAD_ALL_DATA_NOW = "SyncDataService." + SyncContant.APP_NAME + ".ACTION_UPLOAD_ALL_DATA_NOW";
    public static final String ACTION_UPLOAD_SPORT_DATA_NOW = "SyncDataService." + SyncContant.APP_NAME + ".ACTION_UPLOAD_SPORT_DATA_NOW";
    public static final String ACTION_UPLOAD_SLEEP_DATA_NOW = "SyncDataService." + SyncContant.APP_NAME + ".ACTION_UPLOAD_SLEEP_DATA_NOW";
    public static final String ACTION_UPLOAD_HEART_DATA_NOW = "SyncDataService." + SyncContant.APP_NAME + ".ACTION_UPLOAD_HEART_DATA_NOW";
    public static final String ACTION_UPLOAD_MOOD_DATA_NOW = "SyncDataService." + SyncContant.APP_NAME + ".ACTION_UPLOAD_MOOD_DATA_NOW";

    private ArrayList<BaseCommand> syncList;

    private static final int stopFindPhone = 1000;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case stopFindPhone:
                    FindPhoneUtils.getInstance(SyncDataService.this).stopVibratorAlarm();
                    break;
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppLogger.d(TAG, "开启数据上传服务");
        init();
    }

    private void init() {
        if (mInit) return;
        mInit = true;
        registerCustomReceiver();
    }

    public static void startService() {
        Intent intent = new Intent(GlobalApp.getAppContext(), SyncDataService.class);
        GlobalApp.getAppContext().stopService(intent);
        GlobalApp.getAppContext().startService(intent);
    }

    public static void stopService() {
        Intent intent = new Intent(GlobalApp.getAppContext(), SyncDataService.class);
        GlobalApp.getAppContext().stopService(intent);
    }

    private void registerCustomReceiver() {
        if (AppsBluetoothManager.getInstance(this).getFindPhoneCallback() == null) {
            AppsBluetoothManager.getInstance(this).setFindPhoneCallback(this);
        }
        if (AppsBluetoothManager.getInstance(this).getMusicCallback() == null) {
            AppsBluetoothManager.getInstance(this).setMusicCallback(this);
        }
        if (!mbRegister) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_DEVICE_SYNC_NOW);//增加监听
            filter.addAction(ACTION_UPLOAD_ALL_DATA_NOW);//增加监听
            filter.addAction(ACTION_UPLOAD_SPORT_DATA_NOW);//增加监听
            filter.addAction(ACTION_UPLOAD_SLEEP_DATA_NOW);//增加监听
            filter.addAction(ACTION_UPLOAD_HEART_DATA_NOW);//增加监听
            filter.addAction(ACTION_UPLOAD_MOOD_DATA_NOW);//增加监听
            registerReceiver(mReceiver, filter);
            mbRegister = true;
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_UPLOAD_ALL_DATA_NOW)) {
                AppLogger.d(TAG, "接到上传所有数据通知");
                UploadDataServer.procGetSportUpLoadLastTime();
                UploadDataServer.procGetSleepUpLoadLastTime();
//                UploadDataServer.procGetHeartUpLoadLastTime();
//                UploadDataServer.procGetMoodUpLoadLastTime();
            } else if (action.equals(ACTION_UPLOAD_SPORT_DATA_NOW)) {
                AppLogger.d(TAG, "接到上传运动数据通知");
                //暂时注释,在主页面显示后再进行上传
                UploadDataServer.procGetSportUpLoadLastTime();
            } else if (action.equals(ACTION_UPLOAD_SLEEP_DATA_NOW)) {
                AppLogger.d(TAG, "接到上传睡眠通知");
                UploadDataServer.procGetSleepUpLoadLastTime();
            } else if (action.equals(ACTION_UPLOAD_HEART_DATA_NOW)) {
                AppLogger.d(TAG, "接到上传心率数据通知");
                UploadDataServer.procGetHeartUpLoadLastTime();
            } else if (action.equals(ACTION_UPLOAD_MOOD_DATA_NOW)) {
                AppLogger.d(TAG, "接到上传心情数据通知");
                UploadDataServer.procGetMoodUpLoadLastTime();
            } else if (action.equals(ACTION_DEVICE_SYNC_NOW)) {
                AppLogger.d(TAG, "接到同步数据通知");
                syncPreCheck();
            }
        }
    };

    /**
     * 每次作通讯操作的时候，检查固件的UID是否于本地存储的是否相等
     */
    private void syncPreCheck() {
//        BindStart bindStart = new BindStart(new BaseCommand.CommandResultCallback() {
//            @Override
//            public void onSuccess(BaseCommand baseCommand) {
//                long deviceUid = GlobalVarManager.getInstance().getUserUID();
//                int localUserId = AccountConfig.getUserId();
//                AppLogger.d(TAG, "UID检查,deviceUidContent=" + BaseUtil.bytesToHexString(baseCommand.getRespContent()));
//                AppLogger.d(TAG, "UID检查,deviceUid=" + deviceUid + ",localUserId=" + localUserId);
//                if (deviceUid != localUserId) {//检查UID不通过
//                    sendMessage(GlobalEvent.EVENBUS_SIGNAL_CODE_DEVICE_BOUND_BY_OTHER);
//                    AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).killCommandRunnable();
//                } else {
//                    beginSync();
//                }
//            }
//
//            @Override
//            public void onFail(BaseCommand baseCommand) {
//                AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).killCommandRunnable();
//                sendMessage(GlobalEvent.EVENBUS_SIGNAL_CODE_SYNC_FAILED);
//            }
//        });
        //AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommand(bindStart);
        beginSync();

    }

    /**
     * 开始同步操作，发送一系列的命令给固件
     */
    private void beginSync() {
        //设置数据都不需要转时区(后期会加入是否需要转时区的判断)
        BaseCommand.setNeedChangeTime(true);
        syncList = new ArrayList<>();
        UVValue uvValue = new UVValue(resultCallback);                      //获取紫外线
        BatteryPower batteryPower = new BatteryPower(resultCallback);
        SportSleepMode sportSleepMode = new SportSleepMode(resultCallback);
        GoalsSetting goalsSetting = new GoalsSetting(resultCallback);
        Unit unit = new Unit(resultCallback, (byte) AppConfig.getLocalUnit());
        GetHeartRateCount getHeartRateCount = new GetHeartRateCount(resultCallback);
        if (AppUtil.getShowName().equalsIgnoreCase(ChooseDevices.HER)) {
            syncList.add(uvValue);
        } else if (AppUtil.getShowName().equalsIgnoreCase(ChooseDevices.YOUNG)) {
            syncList.add(getHeartRateCount);
        }
        syncList.add(batteryPower);                 //获取电量

        syncList.add(sportSleepMode);               //睡眠模式
        syncList.add(goalsSetting);                 //获取设备的目标值
        syncList.add(unit);                          //设置单位

        syncList.add(new SportSleepCount(resultCallback, 1, 0));//获取运动和睡眠条数
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommands(syncList);
    }

    /**
     * 开始同步操作，发送一系列的命令给固件
     */
    private void beginSync2() {
        //设置数据都不需要转时区(后期会加入是否需要转时区的判断)
        BaseCommand.setNeedChangeTime(true);
        ArrayList<BaseCommand> syncList = new ArrayList<>();
        BatteryPower batteryPower = new BatteryPower(resultCallback);
        DateTime setTime = setDateTime();
        //DateTime getTime = new DateTime(resultCallback);
        DeviceDisplaySportSleep deviceDisplaySportSleep = new DeviceDisplaySportSleep(resultCallback);
        SportSleepMode sportSleepMode = new SportSleepMode(resultCallback);
        UVValue uvValue = new UVValue(resultCallback);                      //获取紫外线
//        GetSportData getSportData = UploadDataHelper.getSportDataCommand(resultCallback);
//        GetSleepData getSleepData = UploadDataHelper.getSleepDataCommand(resultCallback);
        ClearSportData clearSportData = new ClearSportData(resultCallback);
        ClearSleepData clearSleepData = new ClearSleepData(resultCallback);
        //GetHeartData getHeartData = UploadDataHelper.getHeartDataCommand(resultCallback);
        //GetHeartData getMoodData = UploadDataHelper.getMoodDataCommand(resultCallback);
        syncList.add(batteryPower);                 //获取电量
        syncList.add(setTime);                      //设置时间 0x04
//        syncList.add(uvValue);                      //获取紫外线
        //syncList.add(getTime);
        syncList.add(deviceDisplaySportSleep);      //设备运动、睡眠数据 0x57
        syncList.add(sportSleepMode);               //运动/睡眠模式 (0x51)
//        syncList.add(getSportData);                 //运动数据 0x54
        syncList.add(clearSportData);               //删除运动数据

//        syncList.add(getSleepData);                 //睡眠数据
        syncList.add(clearSleepData);               //删除睡眠数据

        //syncList.add(getHeartData);                 //心率数据(Her没有心率功能)
        //syncList.add(getMoodData);
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommands(syncList);
    }

    private DateTime setDateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        AppLogger.d(TAG, "设置设备的时间=" + DateUtil.dateToSec(calendar.getTime()));
        DateTime dateTime = new DateTime(resultCallback, 7, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        return dateTime;
    }

    private BaseCommand.CommandResultCallback resultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            if (baseCommand instanceof SportSleepMode) {
                sendMessage(GlobalEvent.EVENBUS_SIGNAL_CODE_SYNC_REFRSH);
            } else if (baseCommand instanceof SportSleepCount) {
                syncList.clear();
                //为了配合睡眠数据和运动数据的上传逻辑，将同步睡眠数据的逻辑放在同步运动数据之前。
                if (GlobalVarManager.getInstance().getHeartRateCount() > 0) {
                    GetHeartRateData getHeartRateData = new GetHeartRateData(resultCallback, 0, GlobalVarManager.getInstance().getHeartRateCount());
                    ClearHeartData clearHearData = new ClearHeartData(resultCallback);

                    syncList.add(getHeartRateData);                 //运动数据
                    syncList.add(clearHearData);               //删除运动数据
                }
                if (GlobalVarManager.getInstance().getSleepCount() > 0) {
                    GetSleepData getSleepData = UploadDataHelper.getSleepDataCommand(resultCallback, (int) GlobalVarManager.getInstance().getSleepCount());
                    ClearSleepData clearSleepData = new ClearSleepData(resultCallback);
                    syncList.add(getSleepData);                 //睡眠数据
                    syncList.add(clearSleepData);               //删除睡眠数据
                }
                if (GlobalVarManager.getInstance().getSportCount() > 0) {
                    GetSportData getSportData = UploadDataHelper.getSportDataCommand(resultCallback, (int) GlobalVarManager.getInstance().getSportCount());
                    ClearSportData clearSportData = new ClearSportData(resultCallback);

                    syncList.add(getSportData);                 //运动数据
                    syncList.add(clearSportData);               //删除运动数据
                }
                DateTime setTime = setDateTime();
                syncList.add(setTime);                          //设置时间
                AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommands(syncList);
            } else if (baseCommand instanceof GetSportData) {
                doGetSportSuccess();
            } else if (baseCommand instanceof GetSleepData) {
                doGetSleepSuccess();
            } else if (baseCommand instanceof GetHeartRateData) {
                doGetHeartSuccess(baseCommand);
            } else if (baseCommand instanceof DateTime) {
                sendMessage(GlobalEvent.EVENBUS_SIGNAL_CODE_SYNC_END);
            } else if (baseCommand instanceof GoalsSetting) {
                LocalUserGoal userGoal = new LocalUserGoal();
                userGoal.setGoals_step(GlobalVarManager.getInstance().getStepGoalsValue());
                userGoal.setGoals_activeMinutes(GlobalVarManager.getInstance().getActiveTimeGoalsValue());
                userGoal.setGoals_distance(GlobalVarManager.getInstance().getDistanceGoalsValue());
                userGoal.setGoals_calories(GlobalVarManager.getInstance().getCalorieGoalsValue());
                userGoal.setGoals_sleep(GlobalVarManager.getInstance().getSleepGoalsValue());
                GoalConfig.setLocalUserGoal(userGoal);
            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).killCommandRunnable();
            sendMessage(GlobalEvent.EVENBUS_SIGNAL_CODE_SYNC_FAILED);
        }
    };


    private synchronized void doGetSportSuccess() {
        AppLogger.d(TAG, "获取设备运动数据成功");
        if (GlobalDataManager.getInstance().getSportsDatas() != null) {
            for (SportsData sportsData : GlobalDataManager.getInstance().getSportsDatas()) {
                SportData sportData = new SportData();
                sportData.setSportStep(sportsData.sport_steps);
                sportData.setSportCalorie(sportsData.sport_cal);
                sportData.setSportDistance(sportsData.sport_energy);
                sportData.setSportDuration(sportsData.sport_timeTotal);//设备是秒
                sportData.setStartTime(DateUtil.dateToSec(DateUtil.timeStampToDate(sportsData.sport_local_time_stamp * 1000)));
                sportData.setEndTime(DateUtil.dateToSec(DateUtil.timeStampToDate(sportsData.sport_local_time_stamp * 1000)));
                sportData.setLocal_time_stamp(sportsData.sport_local_time_stamp);
                sportData.setTime_stamp(sportsData.sport_time_stamp);
                sportData.setUserId(AccountConfig.getUserId());
                sportData.setAccountId(AccountConfig.getUserLoginName());
                sportData.setDeviceId(UserBindDevice.getBindDeviceId(AccountConfig.getUserId()));
                sportData.setIsUpdate(1);//1表示没有上传过;
                sportData.setIsWorkout(1);//1表示普通运动数据;
                AppLogger.d(TAG, "设备运动数据记录=" + sportData.toString());
                if (DataSupport.where("local_time_stamp=? and userId =? and deviceId=?", sportData.getLocal_time_stamp() + "", sportData.getUserId() + "", sportData.getDeviceId()).find(SportData.class).size() > 0) {
                } else {
                    sportData.save();
                }
            }
        }
        sendUploadBroadcast(SyncDataService.ACTION_UPLOAD_ALL_DATA_NOW);//发送广播进行上传
    }

    private synchronized void doGetSleepSuccess() {
        AppLogger.d(TAG, "获取设备睡眠数据成功");
        if (GlobalDataManager.getInstance().getSleepDatas() != null) {
            for (SleepData sleepsData : GlobalDataManager.getInstance().getSleepDatas()) {
                SleepDataDB sleepDataDB = new SleepDataDB();
                sleepDataDB.setTime_stamp(sleepsData.sleep_time_stamp);
                sleepDataDB.setLocal_time_stamp(sleepsData.sleep_local_time_stamp);
                sleepDataDB.setSleep_type(sleepsData.sleep_type);
                sleepDataDB.setStartTime(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepsData.sleep_local_time_stamp * 1000)));
                sleepDataDB.setEndTime(DateUtil.dateToSec(DateUtil.timeStampToDate(sleepsData.sleep_local_time_stamp * 1000)));
                sleepDataDB.setSleep_id(sleepsData.sleep_id);
                sleepDataDB.setUserId(AccountConfig.getUserId());
                sleepDataDB.setAccountId(AccountConfig.getUserLoginName());
                sleepDataDB.setDeviceId(UserBindDevice.getBindDeviceId(AccountConfig.getUserId()));
                sleepDataDB.setIsUpdate(1);
                if (DataSupport.where("startTime=? and userId =? and deviceId=? and sleep_type=?", sleepDataDB.getStartTime() + "", sleepDataDB.getUserId() + "", sleepDataDB.getDeviceId(), sleepDataDB.getSleep_type() + "").find(SleepDataDB.class).size() > 0) {
                } else {
                    sleepDataDB.save();
                }
            }
        }
//        sendUploadBroadcast(SyncDataService.ACTION_UPLOAD_SLEEP_DATA_NOW);//发送广播进行上传
    }

    private void doGetHeartSuccess(BaseCommand command) {
        if (GlobalDataManager.getInstance().getHeartDatas() != null) {
            for (HeartData heartData : GlobalDataManager.getInstance().getHeartDatas()) {
                if (((GetHeartRateData) command).getQueryType() == HeartData.TYPE_HEART_RATE && heartData.type == HeartData.TYPE_HEART_RATE) {
                    int heartRateValue = heartData.heartRate_value;
                    HeartDataDB heartDataDB = new HeartDataDB();
                    heartDataDB.setHeartMin(heartRateValue);
                    heartDataDB.setHeartMax(heartRateValue);
                    heartDataDB.setHeartAvg(heartRateValue);
                    heartDataDB.setTime_stamp(heartData.time_stamp);
                    heartDataDB.setLocal_time_stamp(heartData.local_time_stamp);
                    heartDataDB.setStartTime(DateUtil.dateToSec(DateUtil.timeStampToDate(heartData.local_time_stamp * 1000)));
                    heartDataDB.setEndTime(DateUtil.dateToSec(DateUtil.timeStampToDate(heartData.local_time_stamp * 1000)));
                    heartDataDB.setUserId(AccountConfig.getUserId());
                    heartDataDB.setDeviceId(UserBindDevice.getBindDeviceId(AccountConfig.getUserId()));
                    heartDataDB.setIsUpdate(1);
                    if (DataSupport.where("local_time_stamp=? and userId =? and deviceId=?", heartDataDB.getLocal_time_stamp() + "", heartDataDB.getUserId() + "", heartDataDB.getDeviceId()).find(HeartDataDB.class).size() > 0) {
                    } else {
                        heartDataDB.save();
                    }
                } else if (((GetHeartRateData) command).getQueryType() == HeartData.TYPE_MOOD_TIRED && heartData.type == HeartData.TYPE_MOOD_TIRED) {
                    int moodValue = heartData.mood_value;
                    int tiredValue = heartData.tired_value;
                    MoodDataDB moodDataDB = new MoodDataDB();
                    moodDataDB.setMoodStatus(moodValue);
                    moodDataDB.setFatigueStatus(tiredValue);
                    moodDataDB.setTime_stamp(heartData.time_stamp);
                    moodDataDB.setLocal_time_stamp(heartData.local_time_stamp);
                    moodDataDB.setStartTime(DateUtil.dateToSec(DateUtil.timeStampToDate(heartData.local_time_stamp * 1000)));
                    moodDataDB.setUserId(AccountConfig.getUserId());
                    moodDataDB.setDeviceId(UserBindDevice.getBindDeviceId(AccountConfig.getUserId()));
                    if (DataSupport.where("local_time_stamp=? and userId =? and deviceId=?", moodDataDB.getLocal_time_stamp() + "", moodDataDB.getUserId() + "", moodDataDB.getDeviceId()).find(MoodDataDB.class).size() > 0) {
                    } else {
                        moodDataDB.save();
                    }
                }
            }
        }
        if (((GetHeartRateData) command).getQueryType() == HeartData.TYPE_HEART_RATE) {
            AppLogger.d(TAG, "获取设备心率数据成功");
            sendUploadBroadcast(SyncDataService.ACTION_UPLOAD_HEART_DATA_NOW);//发送广播进行上传
        } else if (((GetHeartRateData) command).getQueryType() == HeartData.TYPE_MOOD_TIRED) {
            AppLogger.d(TAG, "获取设备心情疲劳度数据成功");
            sendUploadBroadcast(SyncDataService.ACTION_UPLOAD_MOOD_DATA_NOW);//发送广播进行上传
            sendMessage(GlobalEvent.EVENBUS_SIGNAL_CODE_SYNC_END);
            //end
        }
    }

    private void sendMessage(int code) {
        GlobalEvent globalEvent = new GlobalEvent();
        globalEvent.setCode(code);
        EventBus.getDefault().post(globalEvent);
    }


    public static void sendUploadBroadcast(final String action) {
        BackgroundThread.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(action);
                GlobalApp.getAppContext().sendBroadcast(intent);
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppLogger.d(TAG, "关闭数据上传服务");
        if (AppsBluetoothManager.getInstance(this).getFindPhoneCallback() != null) {
            AppsBluetoothManager.getInstance(this).setFindPhoneCallback(null);
        }
        if (AppsBluetoothManager.getInstance(this).getMusicCallback() != null) {
            AppsBluetoothManager.getInstance(this).setMusicCallback(null);
        }
        if (mbRegister) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void findAction(int action) {
        switch (action) {
            case FindPhoneCallback.endFind:
                FindPhoneUtils.getInstance(this).stopVibratorAlarm();
                break;
            case FindPhoneCallback.startFind:
                FindPhoneUtils.getInstance(this).startVibratorAlarm();
                timeOutCount();
                break;
        }
    }

    private void timeOutCount() {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FindPhoneUtils.getInstance(SyncDataService.this).stopVibratorAlarm();
            }
        }, 5000);
    }

    @Override
    public void musicAction(int action) {
        MusicUtils.getInstance(this).anaAction(action);
    }
}
