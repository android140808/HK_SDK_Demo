package cn.appscomm.uploaddata;

import com.appscomm.bluetooth.bean.SleepData;
import com.appscomm.bluetooth.utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.model.database.UserBindDevice;
import cn.appscomm.l38t.utils.UnitTool;
import cn.appscomm.netlib.bean.sleep.UploadSleep;
import cn.appscomm.uploaddata.database.SleepDataDB;


/**
 * Created by Administrator on 2016/8/26.
 */
public class SleepDataHelper {
    static final String TAG = SleepDataHelper.class.getSimpleName();

    /**
     * 获取分组睡眠数据
     *
     * @param sleepList
     * @return
     */
    public synchronized static List<List<SleepData>> getSleepDataList(List<SleepDataDB> sleepList) {
        //存放多段睡眠数据
        List<List<SleepData>> list = new ArrayList<List<SleepData>>();
        //存放单段睡眠数据
        List<SleepData> list1 = null;
        for (SleepDataDB sleepData : sleepList) {
            int id = sleepData.getSleep_id();
            int sleep_type = sleepData.getSleep_type();
            long time_stamp = sleepData.getTime_stamp();
            long sleep_local_time_stamp = sleepData.getLocal_time_stamp();
            //0x00：睡着， 0x01：浅睡， 0x02：醒着，0x03：准备入睡，0x10（16）：进入睡眠模式；0x11（17）：退出睡眠模式
            if (sleep_type == 16) {
                list1 = new ArrayList<SleepData>();
                SleepData mData = new SleepData(id, sleep_type, time_stamp, sleep_local_time_stamp);
                list1.add(mData); //每段睡眠的开始
            } else if (sleep_type == 17 || sleep_type == 18) {
                if (list1 != null) {
                    SleepData mData = new SleepData(id, sleep_type, time_stamp, sleep_local_time_stamp);
                    list1.add(mData); //每段睡眠的结束
                    list.add(list1);
                    list1 = null;
                }
            } else {
                if (list1 != null) {
                    SleepData mData = new SleepData(id, sleep_type, time_stamp, sleep_local_time_stamp);
                    list1.add(mData);
                }
            }
        }
        return list;
    }

    public static UploadSleep.SleepDetails praseSleepDataList(List<SleepData> sleepList) {
        if (sleepList == null || sleepList.size() <= 0) {
            return null;
        }
        boolean startOk = sleepList.get(0).sleep_type == 16;
        boolean endOk = sleepList.get(sleepList.size() - 1).sleep_type == 17 || sleepList.get(sleepList.size() - 1).sleep_type == 18;
        if (!startOk || !endOk) {
            return null;
        }
        List<UploadSleep.SleepTimeState> details = new ArrayList<>();
        String startTime = "", endTime = "";
        float quality = 10;
        long sleepDurationStart = 0;
        float sleepDuration = 0, awakeDuration = 0, lightDuration = 0, deepDuration = 0, totalDuration = 0, awakeCount = 0;
        SleepData cacheSleepData = sleepList.get(0);
        boolean setFlag = false;
        UploadSleep.SleepTimeState timeState = null;
        for (int i = 1; i < sleepList.size(); i++) {
            SleepData sleepData = sleepList.get(i);
            //0x00：睡着， 0x01：浅睡， 0x02：醒着，0x03：准备入睡，0x10（16）：进入睡眠模式；0x11（17）：退出睡眠模式
            if (cacheSleepData != null && cacheSleepData.sleep_type != sleepData.sleep_type) {
                switch (cacheSleepData.sleep_type) {
                    case 16:
                        startTime = DateUtil.dateToSec(new Date(cacheSleepData.sleep_local_time_stamp * 1000));
                        sleepDurationStart = cacheSleepData.sleep_local_time_stamp;
                        sleepDuration = 0;
                        awakeDuration = 0;
                        lightDuration = 0;
                        deepDuration = 0;
                        totalDuration = 0;
                        awakeCount = 0;
                        break;
                    case 3:
                        awakeDuration = sleepData.sleep_local_time_stamp - cacheSleepData.sleep_local_time_stamp + awakeDuration;
                        break;
                    case 1:
                        lightDuration = sleepData.sleep_local_time_stamp - cacheSleepData.sleep_local_time_stamp + lightDuration;
                        break;
                    case 0:
                        deepDuration = sleepData.sleep_local_time_stamp - cacheSleepData.sleep_local_time_stamp + deepDuration;
                        break;
                    case 2:
                        awakeCount++;
                        awakeDuration = sleepData.sleep_local_time_stamp - cacheSleepData.sleep_local_time_stamp + awakeDuration;
                        break;
                }
                setFlag = true;
            } else {
                setFlag = false;
            }
            if (setFlag) {
                timeState = new UploadSleep.SleepTimeState();
                timeState.setStartTime(DateUtil.dateToSec(new Date(cacheSleepData.sleep_local_time_stamp * 1000)));
                timeState.setStatus(cacheSleepData.sleep_type);
                details.add(timeState);
                cacheSleepData = sleepData;
            }
            if (sleepData.sleep_type == 17 || sleepData.sleep_type == 18) {
                endTime = DateUtil.dateToSec(new Date(sleepData.sleep_local_time_stamp * 1000));
                totalDuration = sleepData.sleep_local_time_stamp - sleepDurationStart;
                sleepDuration = deepDuration + lightDuration;
                if (cacheSleepData.sleep_type == 2) {
                    awakeCount--;
                }
                awakeCount++;
                timeState = new UploadSleep.SleepTimeState();
                timeState.setStartTime(DateUtil.dateToSec(new Date(sleepData.sleep_local_time_stamp * 1000)));
                timeState.setStatus(sleepData.sleep_type);
                details.add(timeState);
                break;
            }
        }
        UploadSleep.SleepDetails detail = new UploadSleep.SleepDetails();
        detail.setTimeZone(DateUtil.getLocalTimeZoneValue());
        UserBindDevice userBindDevice = UserBindDevice.getBindDevice(AccountConfig.getUserId());
        if (userBindDevice != null) {
            detail.setDeviceId(userBindDevice.getDeviceId());
            detail.setDeviceType(userBindDevice.getDeviceType());
        }
        detail.setStartTime(startTime);
        detail.setEndTime(endTime);
        detail.setSleepDuration(UnitTool.getHalfUpValue(sleepDuration / 60, 1).floatValue());
        detail.setAwakeDuration(UnitTool.getHalfUpValue(awakeDuration / 60, 1).floatValue());
        detail.setLightDuration(UnitTool.getHalfUpValue(lightDuration / 60, 1).floatValue());
        detail.setDeepDuration(UnitTool.getHalfUpValue(deepDuration / 60, 1).floatValue());
        detail.setAwakeCount(awakeCount);
        detail.setTotalDuration(UnitTool.getHalfUpValue(totalDuration / 60, 1).floatValue());
        detail.setQuality(quality);
        detail.setDetails(details);
        return detail;
    }

}
