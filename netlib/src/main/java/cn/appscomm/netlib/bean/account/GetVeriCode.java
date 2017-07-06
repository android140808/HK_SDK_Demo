package cn.appscomm.netlib.bean.account;

import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.constant.NetLibConstant;


/**
 * Created by wy_ln on 2016/6/1.
 */
public class GetVeriCode extends BasePostBean {
    private String accountId;
    private String customerCode;
    private int accountType;

    public GetVeriCode(String accountId, int accountType) {
        this.accountId = accountId;
        this.customerCode = NetLibConstant.DEFAULT_CUSTOMER_CODE;
        this.accountType = accountType;
        setLanguage(NetLibConstant.DEFAULT_LANGUAGE);
    }

    public GetVeriCode(String accountId, int accountType,int language) {
        this.accountId = accountId;
        this.customerCode = NetLibConstant.DEFAULT_CUSTOMER_CODE;
        this.accountType = accountType;
        setLanguage(language);
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

}
