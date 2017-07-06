package cn.appscomm.netlib.bean.friends;

import java.util.ArrayList;

import cn.appscomm.netlib.bean.base.BaseObtainBean;

/**
 * Created by weiliu on 2016/7/19.
 */
public class PendingFriendObtain extends BaseObtainBean {

    private ArrayList<PendingFriendAccount> friends;

    public ArrayList<PendingFriendAccount> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<PendingFriendAccount> friends) {
        this.friends = friends;
    }
}
