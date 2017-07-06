package cn.appscomm.netlib.bean.sleep;

/**
 * Created by Administrator on 2016/9/1.
 */
public class CountSleepDetail {
    private String unitFormat;
    private float quality;
    private float sleepDuration;
    private float awakeDuration;
    private float lightDuration;
    private float deepDuration;
    private float totalDuration;
    private float awakeCount;


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

    public String getUnitFormat() {
        return unitFormat;
    }

    public void setUnitFormat(String unitFormat) {
        this.unitFormat = unitFormat;
    }

    @Override
    public String toString() {
        return "CountSleepDetail{" +
                "unitFormat='" + unitFormat + '\'' +
                ", quality=" + quality +
                ", sleepDuration=" + sleepDuration +
                ", awakeDuration=" + awakeDuration +
                ", lightDuration=" + lightDuration +
                ", deepDuration=" + deepDuration +
                ", totalDuration=" + totalDuration +
                ", awakeCount=" + awakeCount +
                '}';
    }
}
