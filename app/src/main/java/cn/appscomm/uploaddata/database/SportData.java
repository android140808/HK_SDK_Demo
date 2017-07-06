package cn.appscomm.uploaddata.database;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2016/6/17.
 */
public class SportData extends DataSupport {
    private  int sportStep;
    private  float sportDistance;
    private  float  sportSpeed;
    private  float  sportCalorie;
    private  float  sportDuration;
    private  int userId;
    private String accountId;
    private  String deviceId;
    private long time_stamp;
    private long local_time_stamp;
    private String startTime;
    private String endTime;
    private int isUpdate;
    private int isWorkout;

    public int getIsWorkout() {
        return isWorkout;
    }

    public void setIsWorkout(int isWorkout) {
        this.isWorkout = isWorkout;
    }

    public int getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(int isUpdate) {
        this.isUpdate = isUpdate;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


    public int getSportStep() {
        return sportStep;
    }

    public void setSportStep(int sportStep) {
        this.sportStep = sportStep;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public float getSportDuration() {
        return sportDuration;
    }

    public void setSportDuration(float sportDuration) {
        this.sportDuration = sportDuration;
    }

    public float getSportCalorie() {
        return sportCalorie;
    }

    public void setSportCalorie(float sportCalorie) {
        this.sportCalorie = sportCalorie;
    }

    public float getSportSpeed() {
        return sportSpeed;
    }

    public void setSportSpeed(float sportSpeed) {
        this.sportSpeed = sportSpeed;
    }

    public float getSportDistance() {
        return sportDistance;
    }

    public void setSportDistance(float sportDistance) {
        this.sportDistance = sportDistance;
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

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return "SportData{" +
                "sportStep=" + sportStep +
                ", sportDistance=" + sportDistance +
                ", sportSpeed=" + sportSpeed +
                ", sportCalorie=" + sportCalorie +
                ", sportDuration=" + sportDuration +
                ", userId=" + userId +
                ", accountId='" + accountId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", time_stamp=" + time_stamp +
                ", local_time_stamp=" + local_time_stamp +
                ", isUpdate=" + isUpdate +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
