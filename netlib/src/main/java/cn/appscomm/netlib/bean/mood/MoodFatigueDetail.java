package cn.appscomm.netlib.bean.mood;

/**
 * Created by Administrator on 2016/9/1.
 */
public class MoodFatigueDetail {

    private int moodStatus;
    private int fatigueStatus;
    private String startTime;

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
}
