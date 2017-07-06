package cn.appscomm.netlib.bean.account;

import cn.appscomm.netlib.bean.base.BasePostBean;

/**
 * Created by Administrator on 2016/9/1.
 */
public class AccountQuery extends BasePostBean{
    private int userId;
    private int userInfoId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(int userInfoId) {
        this.userInfoId = userInfoId;
    }
}
