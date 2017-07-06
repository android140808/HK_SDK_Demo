package cn.appscomm.netlib.bean.account;

import cn.appscomm.netlib.bean.base.BasePostBean;

/**
 * Created by wy_ln on 2016/5/26.
 */
public class ChangePassword extends BasePostBean {
    /**
     "seq":"123456789",
     "versionNo":"2.1",
     "clientType":"web",
     "extendParams":{},
     "accountId":"287397795@qq.com",
     "oldPassword":"WndYSwd4",
     "newPassword":"654321"
     */
    private String accountId;
    private String oldPassword;
    private String newPassword;


    public ChangePassword(String userInfoId) {
        this.accountId = userInfoId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
