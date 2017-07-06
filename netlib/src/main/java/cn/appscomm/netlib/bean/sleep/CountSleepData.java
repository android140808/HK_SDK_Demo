package cn.appscomm.netlib.bean.sleep;

import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.constant.NetLibConstant;


public class CountSleepData extends BasePostBean {

    private  String accountId;
    private  String deviceId;
    private  String  startTime;
    private  String  endTime;
    private  int  queryType;
    private  String customerCode = NetLibConstant.DEFAULT_CUSTOMER_CODE;
    private  String   timeZone;


    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

}


