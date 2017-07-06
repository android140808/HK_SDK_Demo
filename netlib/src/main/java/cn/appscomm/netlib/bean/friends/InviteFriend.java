package cn.appscomm.netlib.bean.friends;

import cn.appscomm.netlib.bean.base.BasePostBean;


/**
 * Created by weiliu on 2016/7/26.
 */
public class InviteFriend extends BasePostBean {
    private int ddId;
    private int friendId;

    public int getDdId() {
        return ddId;
    }

    public InviteFriend setDdId(int ddId) {
        this.ddId = ddId;
        return this;
    }

    public int getFriendId() {
        return friendId;
    }

    public InviteFriend setFriendId(int friendId) {
        this.friendId = friendId;
        return this;
    }
}
