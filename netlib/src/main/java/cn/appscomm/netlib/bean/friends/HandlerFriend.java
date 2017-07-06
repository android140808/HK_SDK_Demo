package cn.appscomm.netlib.bean.friends;

import cn.appscomm.netlib.bean.base.BasePostBean;

/**
 * Created by weiliu on 2016/7/19.
 */
public class HandlerFriend extends BasePostBean {
    private int memberId;
    private int status;

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
