package cn.appscomm.netlib.bean.sport;

/**
 * Created by Administrator on 2016/9/1.
 */
public class CountSportDetail {

    private  int unitFormat;
    private  int step;
    private  float calorie;
    private  float  distance;
    private  float  duration;

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public float getCalorie() {
        return calorie;
    }

    public void setCalorie(float calorie) {
        this.calorie = calorie;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public int getUnitFormat() {
        return unitFormat;
    }

    public void setUnitFormat(int unitFormat) {
        this.unitFormat = unitFormat;
    }

    @Override
    public String toString() {
        return "CountSportDetail{" +
                "unitFormat=" + unitFormat +
                ", step=" + step +
                ", calorie=" + calorie +
                ", distance=" + distance +
                ", duration=" + duration +
                '}';
    }
}
