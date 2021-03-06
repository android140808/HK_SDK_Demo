package cn.appscomm.netlib.bean.mood;

import java.util.List;

import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.constant.NetLibConstant;


public class UploadMood extends BasePostBean {

    private String accountId;
    private String customerCode = NetLibConstant.DEFAULT_CUSTOMER_CODE;
    private int timeZone;
    private String deviceId;
    private String deviceType = NetLibConstant.DEFAULT_DEVICE_TYPE;
    private List<MoodFatigueDetail> details;

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

    public int getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(int timeZone) {
        this.timeZone = timeZone;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public List<MoodFatigueDetail> getDetails() {
        return details;
    }

    public void setDetails(List<MoodFatigueDetail> details) {
        this.details = details;
    }



}
