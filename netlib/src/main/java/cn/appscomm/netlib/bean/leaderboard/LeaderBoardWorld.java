package cn.appscomm.netlib.bean.leaderboard;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/1.
 */
public class LeaderBoardWorld implements Serializable {

    private String accountId;
    private String userName;
    private String iconUrl;
    private long dataDate;
    private int sportsStep;
    private float sportsDistance;
    private float sportsCalorie;
    private float activeTime;
    private long updateTime;
    private int rank;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public long getDataDate() {
        return dataDate;
    }

    public void setDataDate(long dataDate) {
        this.dataDate = dataDate;
    }

    public int getSportsStep() {
        return sportsStep;
    }

    public void setSportsStep(int sportsStep) {
        this.sportsStep = sportsStep;
    }

    public float getSportsDistance() {
        return sportsDistance;
    }

    public void setSportsDistance(float sportsDistance) {
        this.sportsDistance = sportsDistance;
    }

    public float getSportsCalorie() {
        return sportsCalorie;
    }

    public void setSportsCalorie(float sportsCalorie) {
        this.sportsCalorie = sportsCalorie;
    }

    public float getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(float activeTime) {
        this.activeTime = activeTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
