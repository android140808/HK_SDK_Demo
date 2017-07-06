package cn.appscomm.uploaddata.database;

import org.litepal.crud.DataSupport;

public class MoodDataDB extends DataSupport {
    private int moodStatus;
    private int fatigueStatus;
    private  String  startTime;
    private  int userId;
    private  String deviceId;
    private long time_stamp;
    private long local_time_stamp;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getMoodStatus() {
        return moodStatus;
    }

    public void setMoodStatus(int moodStatus) {
        this.moodStatus = moodStatus;
    }

    public int getFatigueStatus() {
        return fatigueStatus;
    }

    public void setFatigueStatus(int fatigueStatus) {
        this.fatigueStatus = fatigueStatus;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
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

    public void setLocal_time_stamp(long local_time_stamp) {
        this.local_time_stamp = local_time_stamp;
    }
}
