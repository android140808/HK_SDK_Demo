package cn.appscomm.netlib.bean.leaderboard;

import cn.appscomm.netlib.constant.NetLibConstant;

/**
 * Created by Administrator on 2016/9/1.
 */
public class FriendsAccount {
    public static final int STATUS_ACCEPT = 1;
    public static final int STATUS_REFUSE = 2;
    int ddId;
    String customerCode = NetLibConstant.DEFAULT_CUSTOMER_CODE;
    String accountId;
    String userName;
    String iconUrl;

    public int getDdId() {
        return ddId;
    }

    public void setDdId(int ddId) {
        this.ddId = ddId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

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

    @Override
    public String toString() {
        return "FriendsAccount{" +
                "ddId=" + ddId +
                ", customerCode='" + customerCode + '\'' +
                ", accountId='" + accountId + '\'' +
                ", userName='" + userName + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                '}';
    }
}
