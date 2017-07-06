package cn.appscomm.uploaddata.database;

import org.litepal.crud.DataSupport;

public class HeartDataDB extends DataSupport {
    private int heartMin;
    private int heartMax;
    private int heartAvg;
    private String startTime;
    private String endTime;
    private int userId;
    private String deviceId;
    private long time_stamp;
    private long local_time_stamp;
    private int isUpdate;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getHeartMin() {
        return heartMin;
    }

    public void setHeartMin(int heartMin) {
        this.heartMin = heartMin;
    }

    public int getHeartMax() {
        return heartMax;
    }

    public void setHeartMax(int heartMax) {
        this.heartMax = heartMax;
    }

    public int getHeartAvg() {
        return heartAvg;
    }

    public void setHeartAvg(int heartAvg) {
        this.heartAvg = heartAvg;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }

    public long getLocal_time_stamp() {
        return local_time_stamp;
    }

    public int getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(int isUpdate) {
        this.isUpdate = isUpdate;
    }

    public void setLocal_time_stamp(long local_time_stamp) {
        this.local_time_stamp = local_time_stamp;
    }
}
