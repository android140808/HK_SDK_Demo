package cn.appscomm.netlib.bean.heartRate;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/3 16:16
 */
public class HeartDetails {
    private  int heartMin;
    private  int heartMax;
    private int heartAvg;
    private String startTime;
    private String endTime;

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
}
