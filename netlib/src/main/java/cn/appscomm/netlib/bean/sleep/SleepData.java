package cn.appscomm.netlib.bean.sleep;

import java.util.List;

/**
 * Created by Administrator on 2016/9/1.
 * 每段睡眠
 */
public class SleepData {

    private String deviceId;
    private String deviceType;
    private int timeZone;
    private String startTime;
    private String endTime;
    private String quality;
    private int sleepDuration;
    private int awakeDuration;
    private int lightDuration;
    private int deepDuration;
    private int totalDuration;
    private int awakeCount;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(int timeZone) {
        this.timeZone = timeZone;
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

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public int getSleepDuration() {
        return sleepDuration;
    }

    public void setSleepDuration(int sleepDuration) {
        this.sleepDuration = sleepDuration;
    }

    public int getAwakeDuration() {
        return awakeDuration;
    }

    public void setAwakeDuration(int awakeDuration) {
        this.awakeDuration = awakeDuration;
    }

    public int getLightDuration() {
        return lightDuration;
    }

    public void setLightDuration(int lightDuration) {
        this.lightDuration = lightDuration;
    }

    public int getDeepDuration() {
        return deepDuration;
    }

    public void setDeepDuration(int deepDuration) {
        this.deepDuration = deepDuration;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public int getAwakeCount() {
        return awakeCount;
    }

    public void setAwakeCount(int awakeCount) {
        this.awakeCount = awakeCount;
    }

    private List<SleepDetail> details;

    public List<SleepDetail> getDetails() {
        return details;
    }

    public void setDetails(List<SleepDetail> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        StringBuffer detail = new StringBuffer();
        if(null != details && details.size()>0){
            for(SleepDetail sleepDetail : details){
                detail.append(sleepDetail.toString());
            }
        }
        return "SleepData{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", timeZone=" + timeZone +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", quality='" + quality + '\'' +
                ", sleepDuration=" + sleepDuration +
                ", awakeDuration=" + awakeDuration +
                ", lightDuration=" + lightDuration +
                ", deepDuration=" + deepDuration +
                ", totalDuration=" + totalDuration +
                ", awakeCount=" + awakeCount +
                ", details=" + detail +
                '}';
    }
}
