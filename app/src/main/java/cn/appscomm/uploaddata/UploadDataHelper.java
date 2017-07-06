package cn.appscomm.uploaddata;

import com.appscomm.bluetooth.bean.HeartData;
import com.appscomm.bluetooth.bean.SleepData;
import com.appscomm.bluetooth.manage.AppsBluetoothManager;
import com.appscomm.bluetooth.protocol.command.base.BaseCommand;
import com.appscomm.bluetooth.protocol.command.data.GetHeartData;
import com.appscomm.bluetooth.protocol.command.data.GetSleepData;
import com.appscomm.bluetooth.protocol.command.data.GetSportData;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.model.database.UserBindDevice;
import cn.appscomm.l38t.utils.LogUtil;
import cn.appscomm.netlib.bean.sleep.UploadSleep;
import cn.appscomm.uploaddata.database.HeartDataDB;
import cn.appscomm.uploaddata.database.MoodDataDB;
import cn.appscomm.uploaddata.database.SleepDataDB;
import cn.appscomm.uploaddata.database.SportData;

/**
 * Created by Administrator on 2016/8/26.
 */
public class UploadDataHelper {
    private static final String TAG = UploadDataHelper.class.getSimpleName();

    public static void getSportData(BaseCommand.CommandResultCallback resultCallback) {
        long timeStamp = getMaxTimeLocalSport();   //获取本地运动数据最新获取时间
        LogUtil.i(TAG, "getSportData_timeStamp=" + timeStamp);
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommand(new GetSportData(resultCallback, 0, timeStamp, 0));
    }

    public static void getSleepData(BaseCommand.CommandResultCallback resultCallback) {
        long timeStamp = getMaxTimeLocalSleep();
        LogUtil.i(TAG, "getSleepData_timeStamp=" + timeStamp);
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommand(new GetSleepData(resultCallback, 0, timeStamp, 0));
    }

    public static void getHeartData(BaseCommand.CommandResultCallback resultCallback) {
        long timeStamp = getMaxTimeLocalHeart();
        LogUtil.i(TAG, "getHeartData_timeStamp=" + timeStamp);
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommand(new GetHeartData(resultCallback, HeartData.TYPE_HEART_RATE, 0, timeStamp, 0));
    }

    public static void getMoodData(BaseCommand.CommandResultCallback resultCallback) {
        long timeStamp = getMaxTimeLocalMood();
        LogUtil.i(TAG, "getMoodData_timeStamp=" + timeStamp);
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommand(new GetHeartData(resultCallback, HeartData.TYPE_MOOD_TIRED, 0, timeStamp, 0));
    }

    public static GetSportData getSportDataCommand(BaseCommand.CommandResultCallback resultCallback,int counts) {
        long timeStamp = getMaxTimeLocalSport();   //获取本地运动数据最新获取时间
        LogUtil.i(TAG, "getSportData_timeStamp=" + timeStamp);
        return new GetSportData(resultCallback, 0, timeStamp, counts);
    }

    public static GetSleepData getSleepDataCommand(BaseCommand.CommandResultCallback resultCallback, int counts) {
        long timeStamp = getMaxTimeLocalSleep();
        LogUtil.i(TAG, "getSleepData_timeStamp=" + timeStamp);
       return new GetSleepData(resultCallback, 0, timeStamp, counts);
    }

    public static GetHeartData getHeartDataCommand(BaseCommand.CommandResultCallback resultCallback) {
        long timeStamp = getMaxTimeLocalHeart();
        LogUtil.i(TAG, "getHeartData_timeStamp=" + timeStamp);
        return new GetHeartData(resultCallback, HeartData.TYPE_HEART_RATE, 0, timeStamp, 0);
    }

    public static GetHeartData getMoodDataCommand(BaseCommand.CommandResultCallback resultCallback) {
        long timeStamp = getMaxTimeLocalMood();
        LogUtil.i(TAG, "getMoodData_timeStamp=" + timeStamp);
        return new GetHeartData(resultCallback, HeartData.TYPE_MOOD_TIRED, 0, timeStamp, 0);
    }

    private static long getMaxTimeLocalSleep() {
        long maxTime = (int) (System.currentTimeMillis() / 1000);
        try {
            maxTime = DataSupport.where("deviceId = ? and userId=? ", UserBindDevice.getBindDeviceId(AccountConfig.getUserId()), AccountConfig.getUserId() + "").max(SleepDataDB.class, "time_stamp", long.class);
        } catch (Exception e) {
        }
        return maxTime;
    }

    private static long getMaxTimeLocalHeart() {
        long maxTime = System.currentTimeMillis() / 1000;
        try {
            maxTime = DataSupport.where("deviceId = ? and userId=? ", UserBindDevice.getBindDeviceId(AccountConfig.getUserId()), AccountConfig.getUserId() + "").max(HeartDataDB.class, "time_stamp", long.class);
        } catch (Exception e) {
        }
        return maxTime;
    }

    private static long getMaxTimeLocalMood() {
        long maxTime = System.currentTimeMillis() / 1000;
        try {
            maxTime = DataSupport.where("deviceId = ? and userId=? ", UserBindDevice.getBindDeviceId(AccountConfig.getUserId()), AccountConfig.getUserId() + "").max(MoodDataDB.class, "time_stamp", long.class);
        } catch (Exception e) {
        }
        return maxTime;
    }

    private static long getMaxTimeLocalSport() {
        long maxTime = System.currentTimeMillis() / 1000;
        try {
            String deviceId = UserBindDevice.getBindDeviceId(AccountConfig.getUserId());
            String userId = AccountConfig.getUserId() + "";
            maxTime = DataSupport.where("deviceId = ? and userId=? ", deviceId, userId).max(SportData.class, "time_stamp", long.class);
        } catch (Exception e) {
        }
        return maxTime;
    }

    //删除数据
    public static void delLeftLastSportData(List<SportData> sportDataList) {
        if (sportDataList != null && sportDataList.size() > 0) {
            for(SportData sportData : sportDataList){
                LogUtil.i(TAG, "删除的运动数据=" + sportData.toString());
                sportData.delete();
            }
        }
    }

    public static void delLeftLastSleeptData(List<SleepDataDB> sleepDataDBList) {
        if (sleepDataDBList != null && sleepDataDBList.size() > 0) {
            for(SleepDataDB sleepDataDB : sleepDataDBList){
                LogUtil.i(TAG, "删除的睡眠数据=" + sleepDataDB.toString());
                sleepDataDB.delete();
            }
        }
        final List<SleepDataDB> sleepDatas = UploadDataHelper.getSleepDataFromDataBase(0+"");
        LogUtil.i(TAG, "删除后查询的睡眠数据=" + sleepDatas.toString());
        if (sleepDatas != null && sleepDatas.size() > 0) {
            for(SleepDataDB sleepDataDB : sleepDatas){
                LogUtil.i(TAG, "删除后查询的睡眠数据=" + sleepDataDB.toString());
            }
        }
    }

    public static void delLeftLastHeartData(List<HeartDataDB> heartDataDBList) {
        if (heartDataDBList == null || heartDataDBList.size() <= 0) {
            return;
        }
        for (int i = 0; i < heartDataDBList.size() - 1; i++) {
            if (i != heartDataDBList.size() - 1) {
                heartDataDBList.get(i).delete();
            }
        }
    }

    public static void delLeftLastMoodData(List<MoodDataDB> moodDataDBList) {
        if (moodDataDBList == null || moodDataDBList.size() <= 0) {
            return;
        }
        for (int i = 0; i < moodDataDBList.size() - 1; i++) {
            if (i != moodDataDBList.size() - 1) {
                moodDataDBList.get(i).delete();
            }
        }
    }

    /**
     * 本地没有上传但是需要上传的运动数据
     */
    public static List<SportData> getSportDataFromDataBase(long lastSportUpLoadTime) {
        LogUtil.i(TAG, "本地有运动数据 " + lastSportUpLoadTime);
        String userId = AccountConfig.getUserId() + "";
        String deviceId = UserBindDevice.getBindDeviceId(AccountConfig.getUserId()) + "";
        List<SportData> sportDataList = DataSupport.where("local_time_stamp>? and userId=? and deviceId=? ", lastSportUpLoadTime + "", userId, deviceId).order("local_time_stamp asc").find(SportData.class);
        if (sportDataList.size() > 0) {
            //有数据上传
            LogUtil.i(TAG, "本地有运动数据 " + sportDataList.size() + " 条需要上传");
        }
        return sportDataList;
    }
    /**
     * 本地没有上传但是需要上传的睡眠数据
     */
    public static List<SleepDataDB> getSleepDataFromDataBase(String lastSleepUpLoadTime) {
        String userId = AccountConfig.getUserId() + "";
        String deviceId = UserBindDevice.getBindDeviceId(AccountConfig.getUserId()) + "";
        List<SleepDataDB> sleepDataList = new ArrayList<>();
        sleepDataList = DataSupport.where("local_time_stamp>?  and userId=? and deviceId=?", lastSleepUpLoadTime+"", userId, deviceId).order("local_time_stamp asc").find(SleepDataDB.class);
        if (sleepDataList.size() > 0) {
            //有数据上传
            LogUtil.i(TAG, "本地有睡眠数据 " + sleepDataList.size() + " 条需要上传");
        }
        return sleepDataList;
    }

    /**
     * 得到合理的睡眠值:
     * 1、数据的sleep_type以16开始进入睡眠,17(强制睡眠)或18(预设睡眠)退出睡眠。为一次的睡眠数据
     * 2、如果一组睡眠数据包含了当天0点的时间戳,则此次的睡眠数据算今天的
     * @param sleepDataList
     * @return
     */
    public static List<SleepDataDB>  getTrueSleep(List<SleepDataDB> sleepDataList,long zero) {
        //去掉不是今天的数据
        //1、sleep_type == 17(18)时间戳小于当天0点的不是
        Collections.reverse(sleepDataList);     //倒置list
        for(int i = sleepDataList.size()-1;i>0;i--){
            //去掉i之后的   包括i
            if((sleepDataList.get(i).getSleep_type() == 17 || sleepDataList.get(i).getSleep_type() == 18) && sleepDataList.get(i).getTime_stamp() < zero){
                for(int j = sleepDataList.size()-1 ;j>=i;j--){
                    sleepDataList.remove(j);
                }
            }
        }
        for(SleepDataDB s : sleepDataList){
            LogUtil.i(TAG, "MAIN处理后睡眠数据详情7=" + s.toString());
        }
        //去掉不是16开头的
        for(int i = sleepDataList.size()-1;i>0;i--){
            if(sleepDataList.get(i).getSleep_type() == 16){
                break;
            }else{
                sleepDataList.remove(i);
            }
        }
        for(SleepDataDB s : sleepDataList){
            LogUtil.i(TAG, "MAIN处理后睡眠数据详情8=" + s.toString());
        }
        Collections.reverse(sleepDataList);     //倒置list
        //去掉不是17(18)结尾的
        for(int i = sleepDataList.size()-1;i>0;i--){
            if(sleepDataList.get(i).getSleep_type() == 17 || sleepDataList.get(i).getSleep_type() == 18){
                break;
            }else{
                sleepDataList.remove(i);
            }
        }
        for(SleepDataDB s : sleepDataList){
            LogUtil.i(TAG, "MAIN处理后睡眠数据详情9=" + s.toString());
        }
        return sleepDataList;
    }

    public static List<HeartDataDB> getHeartDataFromDataBase(String lastHeartUpLoadTime) {
        String userId = AccountConfig.getUserId() + "";
        String deviceId = UserBindDevice.getBindDeviceId(AccountConfig.getUserId()) + "";
        List<HeartDataDB> heartDataList = DataSupport.where("local_time_stamp>? and userId=? and deviceId=? ", lastHeartUpLoadTime, userId, deviceId).order("local_time_stamp asc").find(HeartDataDB.class);
        if (heartDataList.size() > 0) {
            //有数据上传
            LogUtil.i(TAG, "本地有心率数据 " + heartDataList.size() + " 条需要上传");
        }
        return heartDataList;
    }

    public static List<HeartDataDB> getHeartDataFromDataBaseWithoutDevice(String lastHeartUpLoadTime) {
        String userId = AccountConfig.getUserId() + "";
        List<HeartDataDB> heartDataList = DataSupport.where("local_time_stamp>? and userId=? ", lastHeartUpLoadTime, userId).order("local_time_stamp desc").find(HeartDataDB.class);
        if (heartDataList.size() > 0) {
            //有数据上传
            LogUtil.i(TAG, "本地有心率数据 " + heartDataList.size() + " 条");
        }
        return heartDataList;
    }

    public static List<MoodDataDB> getMoodDataFromDataBase(String lastMoodUpLoadTime) {
        String userId = AccountConfig.getUserId() + "";
        String deviceId = UserBindDevice.getBindDeviceId(AccountConfig.getUserId()) + "";
        List<MoodDataDB> moodDataList = DataSupport.where("local_time_stamp>?  and userId=? and deviceId=? ", lastMoodUpLoadTime, userId, deviceId).order("local_time_stamp asc").find(MoodDataDB.class);
        if (moodDataList.size() > 0) {
            //有数据上传
            LogUtil.i(TAG, "本地有心情疲劳度数据 " + moodDataList.size() + " 条需要上传");
        }
        return moodDataList;
    }


    /**
     * 获取睡眠数据
     * @param sleepList
     * @return
     */
    public static List<UploadSleep.SleepDetails> getSleepDetailsData(List<SleepDataDB> sleepList) {
        List<UploadSleep.SleepDetails> sleeps = new ArrayList<>();
        if (sleepList == null || sleepList.size() <= 0) {
            LogUtil.i(TAG, ">>睡眠数据为空");
            return sleeps;
        }
        //存放多段睡眠数据
        List<List<SleepData>> list = SleepDataHelper.getSleepDataList(sleepList);
        //存放单段睡眠数据
        for (int i = 0; i < list.size(); i++) {
            UploadSleep.SleepDetails sleepDetails=SleepDataHelper.praseSleepDataList(list.get(i));
            if (sleepDetails!=null){
                LogUtil.i(TAG, "上传睡眠数据" + (i) + "," + sleepDetails.toString());
                sleeps.add(sleepDetails);
            }
        }
        return  sleeps;
    }


}
