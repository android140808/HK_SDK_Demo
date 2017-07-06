package cn.appscomm.netlib.bean.leaderboard;

import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.constant.NetLibConstant;


/**
 * Created by wy_ln on 2016/5/26.
 */
public class QueryJoin extends BasePostBean {


    private String accountId;
    private String customerCode;
    private String type;

    public QueryJoin(String accountId) {
        this.accountId = accountId;
        this.customerCode = NetLibConstant.DEFAULT_CUSTOMER_CODE;
        this.type = "0";//增加改参数可以模糊查询
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
