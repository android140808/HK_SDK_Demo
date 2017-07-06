package cn.appscomm.netlib.bean.sport;

/**
 * Created by Administrator on 2016/9/1.
 */
public class SportDetail {
    private int sportStep;
    private float sportDistance;
    private float sportSpeed;
    private float sportCalorie;
    private float sportDuration;
    private String startTime;
    private String endTime;

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

    public float getSportDistance() {
        return sportDistance;
    }

    public void setSportDistance(float sportDistance) {
        this.sportDistance = sportDistance;
    }

    public float getSportSpeed() {
        return sportSpeed;
    }

    public void setSportSpeed(float sportSpeed) {
        this.sportSpeed = sportSpeed;
    }

    public float getSportCalorie() {
        return sportCalorie;
    }

    public void setSportCalorie(float sportCalorie) {
        this.sportCalorie = sportCalorie;
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


    @Override
    public String toString() {
        return "SportDetail{" +
                "sportStep=" + sportStep +
                ", sportDistance=" + sportDistance +
                ", sportSpeed=" + sportSpeed +
                ", sportCalorie=" + sportCalorie +
                ", sportDuration=" + sportDuration +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
