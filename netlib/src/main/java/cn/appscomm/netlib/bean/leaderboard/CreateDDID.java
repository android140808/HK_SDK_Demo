package cn.appscomm.netlib.bean.leaderboard;

import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.constant.NetLibConstant;


/**
 * Created by wy_ln on 2016/5/26.
 */
public class CreateDDID extends BasePostBean {


    private String accountId;
    private String customerCode;
    private String userName;
    private String deviceType;
    private String iconUrl;

    public CreateDDID(String accountId) {
        this.accountId = accountId;
        this.customerCode = NetLibConstant.DEFAULT_CUSTOMER_CODE;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
