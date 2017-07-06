package cn.appscomm.netlib.bean.friends;

import cn.appscomm.netlib.bean.base.BasePostBean;

/**
 * Created by weiliu on 2016/7/19.
 */
public class PendingFriend extends BasePostBean {

    private int ddId;
    private String userLoginName;

    public PendingFriend(Integer ddId) {
        this.ddId=ddId;
    }

    public PendingFriend(Integer ddId,String userLoginName) {
        this.ddId=ddId;
        this.userLoginName=userLoginName;
    }

    public String getUserLoginName() {
        return userLoginName;
    }

    public void setUserLoginName(String userLoginName) {
        this.userLoginName = userLoginName;
    }

    public int getDdId() {
        return ddId;
    }

    public void setDdId(int ddId) {
        this.ddId = ddId;
    }
}
