package cn.appscomm.netlib.bean.friends;

import cn.appscomm.netlib.bean.base.BasePostBean;


/**
 * Created by weiliu on 2016/7/19.
 */
public class UpdateIconUrl extends BasePostBean {
    private String accountId;
    private String customerCode;
    private String iconUrl;

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
