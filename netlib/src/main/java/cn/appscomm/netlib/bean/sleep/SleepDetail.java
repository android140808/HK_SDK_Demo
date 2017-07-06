package cn.appscomm.netlib.bean.sleep;

/**
 * Created by Administrator on 2016/9/1.
 * 每个时刻的睡眠
 */
public class SleepDetail {

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

    @Override
    public String toString() {
        return "SleepDetail{" +
                "startTime='" + startTime + '\'' +
                ", status=" + status +
                '}';
    }
}
