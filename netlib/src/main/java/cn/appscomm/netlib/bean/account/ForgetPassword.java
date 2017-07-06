package cn.appscomm.netlib.bean.account;

import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.constant.NetLibConstant;

/**
 * Created by wy_ln on 2016/5/31.
 */
public class ForgetPassword extends BasePostBean {

    public static final int Forget_Type_Email=111;
    public static final int Forget_Type_Phone=112;

    /**
     * seq : 11111111
     * versionNo : 2.0
     * clientType : web
     * extendParams : {}
     * accountId : 287397795@qq.com
     */

    private String accountId;
    private int accountType;
    private String customerCode = NetLibConstant.DEFAULT_CUSTOMER_CODE;

    public ForgetPassword(String accountId){
        this(accountId, Forget_Type_Email);
    }
    public ForgetPassword(String accountId,int accountType){
        this.accountId = accountId;
        this.accountType = accountType;
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
}
