package cn.appscomm.netlib.bean.leaderboard;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/1.
 */
public class LeaderBoard implements Serializable{

    int ddId;
    int memberId ;
    String userName;
    String iconUrl;
    long dataDate ;
    int sportsStep ;
    float sportsDistance   ;
    float sportsCalorie ;
    float activeTime ;
    long updateTime ;

    public int getDdId() {
        return ddId;
    }

    public void setDdId(int ddId) {
        this.ddId = ddId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
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
}
