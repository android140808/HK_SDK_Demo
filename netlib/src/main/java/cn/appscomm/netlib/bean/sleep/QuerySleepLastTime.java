package cn.appscomm.netlib.bean.sleep;

import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.constant.NetLibConstant;


/**
 * Created by Administrator on 2016/6/20.
 */
public class QuerySleepLastTime extends BasePostBean {

    private  String  accountId;
    private  String  deviceId;
    private  String customerCode = NetLibConstant.DEFAULT_CUSTOMER_CODE;
    private  String timeZone ;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
