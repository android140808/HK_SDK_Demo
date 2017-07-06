package cn.appscomm.netlib.bean.account;

import java.util.Map;

import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.constant.NetLibConstant;


/**
 * Created by wy_ln on 2016/5/27.
 */
public class Register extends BasePostBean {

    public static final int ACCOUNT_TYPE_EMAIL = 111;
    public static final int ACCOUNT_TYPE_PHONE = 112;
    private String accountId;
    private String customerCode;
    private int accountType;
    private String accountPassword;
    private int verifyCode;
    private Map<String, String> extendParams;

    /**
     * seq : 11111111
     * versionNo : 2.0
     * clientType : web
     * accountId : 2392464349@qq.com
     * customerCode : appscomm
     * accountType : 111
     * accountPassword : 12345678
     * language : 200
     */
    public Register(String accountId, int language, int accountType, String accountPassword) {
        this.accountId = accountId;
        this.customerCode = NetLibConstant.DEFAULT_CUSTOMER_CODE;
        this.accountType = accountType;
        this.accountPassword = accountPassword;
        setLanguage(language);
    }

    public Register(String accountId, int language, int accountType, String accountPassword, int verifyCode) {
        this(accountId, language, accountType, accountPassword);
        this.verifyCode = verifyCode;
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

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    public Map<String, String> getExtendParams() {
        return extendParams;
    }

    public void setExtendParams(Map<String, String> extendParams) {
        this.extendParams = extendParams;
    }

    public int getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(int verifyCode) {
        this.verifyCode = verifyCode;
    }
}
