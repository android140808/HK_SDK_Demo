package cn.appscomm.netlib.bean.sleep;

import java.util.List;

import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.constant.NetLibConstant;

public class UploadSleep extends BasePostBean {
    private String accountId;
    private String customerCode = NetLibConstant.DEFAULT_CUSTOMER_CODE;
    private int timeZone;
    private List<SleepDetails> sleeps;

    public static class SleepTimeState {
        private String startTime;
        private int status;

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    public static class SleepDetails {
        private  String deviceId;
        private  String deviceType = NetLibConstant.DEFAULT_DEVICE_TYPE;
        private float sportDuration;
        private String startTime;
        private String endTime;
        private int timeZone;
        private float quality ;
        private float sleepDuration ;
        private float awakeDuration ;
        private float lightDuration ;
        private float deepDuration ;
        private float totalDuration ;
        private float awakeCount ;
        private List<SleepTimeState> details;

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

        public float getSportDuration() {
            return sportDuration;
        }

        public void setSportDuration(float sportDuration) {
            this.sportDuration = sportDuration;
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

        public int getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(int timeZone) {
            this.timeZone = timeZone;
        }

        public float getQuality() {
            return quality;
        }

        public void setQuality(float quality) {
            this.quality = quality;
        }

        public float getSleepDuration() {
            return sleepDuration;
        }

        public void setSleepDuration(float sleepDuration) {
            this.sleepDuration = sleepDuration;
        }

        public float getAwakeDuration() {
            return awakeDuration;
        }

        public void setAwakeDuration(float awakeDuration) {
            this.awakeDuration = awakeDuration;
        }

        public float getLightDuration() {
            return lightDuration;
        }

        public void setLightDuration(float lightDuration) {
            this.lightDuration = lightDuration;
        }

        public float getDeepDuration() {
            return deepDuration;
        }

        public void setDeepDuration(float deepDuration) {
            this.deepDuration = deepDuration;
        }

        public float getTotalDuration() {
            return totalDuration;
        }

        public void setTotalDuration(float totalDuration) {
            this.totalDuration = totalDuration;
        }

        public float getAwakeCount() {
            return awakeCount;
        }

        public void setAwakeCount(float awakeCount) {
            this.awakeCount = awakeCount;
        }

        public List<SleepTimeState> getDetails() {
            return details;
        }

        public void setDetails(List<SleepTimeState> details) {
            this.details = details;
        }

        @Override
        public String toString() {
            return "SleepDetails{" +
                    "deviceId='" + deviceId + '\'' +
                    ", deviceType='" + deviceType + '\'' +
                    ", sportDuration=" + sportDuration +
                    ", startTime='" + startTime + '\'' +
                    ", endTime='" + endTime + '\'' +
                    ", timeZone=" + timeZone +
                    ", quality=" + quality +
                    ", sleepDuration=" + sleepDuration +
                    ", awakeDuration=" + awakeDuration +
                    ", lightDuration=" + lightDuration +
                    ", deepDuration=" + deepDuration +
                    ", totalDuration=" + totalDuration +
                    ", awakeCount=" + awakeCount +
                    ", details=" + details.toString() +
                    '}';
        }
    }


    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }


    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public int getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(int timeZone) {
        this.timeZone = timeZone;
    }

    public List<SleepDetails> getSleeps() {
        return sleeps;
    }

    public void setSleeps(List<SleepDetails> sleeps) {
        this.sleeps = sleeps;
    }
}
