package cn.appscomm.netlib.bean.base;

import cn.appscomm.netlib.bean.account.UserInfoBean;

/**
 * Created by Administrator on 2016/9/1.
 */
public class ObtainResMap {

    private int apiNo;

    private UserInfoBean userInfo;

    private String lastTime;

    public int getApiNo() {
        return apiNo;
    }

    public void setApiNo(int apiNo) {
        this.apiNo = apiNo;
    }

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }
}
