package cn.appscomm.netlib.bean.account;

import cn.appscomm.netlib.bean.base.BasePostBean;


/**
 * Created by wy_ln on 2016/5/26.
 */
public class Login extends BasePostBean {

    /**
     * seq : 123456789
     * versionNo : 2.1
     * clientType : web
     * accountId : 287397795@qq.com
     * password : 12345678
     */

    public Login(String accountId,String password) {
        this.accountId = accountId;
        this.password = password;
    }

    private String accountId;
    private String password;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
