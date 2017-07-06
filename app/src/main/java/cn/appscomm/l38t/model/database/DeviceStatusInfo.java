package cn.appscomm.l38t.model.database;

import android.content.ContentValues;

import com.appscomm.bluetooth.manage.GlobalVarManager;

import org.litepal.crud.DataSupport;

import cn.appscomm.l38t.config.AccountConfig;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/5 09:29
 */
public class DeviceStatusInfo extends DataSupport {

    private int userId;
    private String accountId;
    private String deviceId;

    private int sportSleepMode;
    private int distance;
    private int step;
    private int sleepTime;
    private int sportTime;
    private int calories;
    private int heartRate;
    private int mood;
    private int tired;
    private int uvValue;

    private long lastUpdateTime;

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getSportTime() {
        return sportTime;
    }

    public void setSportTime(int sportTime) {
        this.sportTime = sportTime;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public int getTired() {
        return tired;
    }

    public void setTired(int tired) {
        this.tired = tired;
    }

    public int getSportSleepMode() {
        return sportSleepMode;
    }

    public void setSportSleepMode(int sportSleepMode) {
        this.sportSleepMode = sportSleepMode;
    }

    public static DeviceStatusInfo findByAccountId(String accountId){
        return where("accountId = ?", accountId + "").findFirst(DeviceStatusInfo.class);
    }

    public void setUvValue(int uvValue) {
        this.uvValue = uvValue;
    }

    public int getUvValue() {
        return uvValue;
    }

    @Override
    public String toString() {
        return "DeviceStatusInfo{" +
                "userId=" + userId +
                ", accountId='" + accountId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", sportSleepMode=" + sportSleepMode +
                ", distance=" + distance +
                ", step=" + step +
                ", sleepTime=" + sleepTime +
                ", sportTime=" + sportTime +
                ", calories=" + calories +
                ", heartRate=" + heartRate +
                ", mood=" + mood +
                ", tired=" + tired +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }

    //保存和得到本地设备的状态信息
    public static DeviceStatusInfo saveAndGetDeviceStatusInfoLocal(GlobalVarManager deviceVarManager) {
        DeviceStatusInfo deviceStatusInfo = new DeviceStatusInfo();
        deviceStatusInfo.setUserId(AccountConfig.getUserId());
        deviceStatusInfo.setAccountId(AccountConfig.getUserLoginName());
        deviceStatusInfo.setDeviceId(UserBindDevice.getBindDeviceId(AccountConfig.getUserId()));
        deviceStatusInfo.setSportSleepMode(deviceVarManager.getSportSleepMode());
        deviceStatusInfo.setStep(deviceVarManager.getDeviceDisplayStep());
        deviceStatusInfo.setCalories(deviceVarManager.getDeviceDisplayCalorie());
        deviceStatusInfo.setSleepTime(deviceVarManager.getDeviceDisplaySleep());
        deviceStatusInfo.setDistance(deviceVarManager.getDeviceDisplayDistance());
        deviceStatusInfo.setSportTime(deviceVarManager.getDeviceDisplaySport());
        deviceStatusInfo.setHeartRate(deviceVarManager.getDeviceDisplayHeartRate());
        deviceStatusInfo.setMood(deviceVarManager.getDeviceDisplayMood());
        deviceStatusInfo.setTired(deviceVarManager.getDeviceDisplayTired());
        deviceStatusInfo.setLastUpdateTime(System.currentTimeMillis());
        deviceStatusInfo.setUvValue(deviceVarManager.getUvValue());             //加入紫外线,由于是实时显示一条,所以不用存入数据库
        if (DataSupport.where(" accountId =?", deviceStatusInfo.getAccountId()).find(DeviceStatusInfo.class).size() > 0) {
            ContentValues values = new ContentValues();
            values.put("deviceId", deviceStatusInfo.getDeviceId());
            values.put("sportSleepMode", deviceStatusInfo.getSportSleepMode());
            values.put("distance", deviceStatusInfo.getDistance());
            values.put("step", deviceStatusInfo.getStep());
            values.put("sleepTime", deviceStatusInfo.getSleepTime());
            values.put("sportTime", deviceStatusInfo.getSportTime());
            values.put("calories", deviceStatusInfo.getCalories());
            values.put("heartRate", deviceStatusInfo.getHeartRate());
            values.put("mood", deviceStatusInfo.getMood());
            values.put("tired", deviceStatusInfo.getTired());
            DataSupport.updateAll(DeviceStatusInfo.class, values, " accountId =?", deviceStatusInfo.getAccountId());
        } else {
            deviceStatusInfo.save();
        }
        return deviceStatusInfo;
    }
}
