package cn.appscomm.netlib.bean.sport;

import java.util.List;

import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.constant.NetLibConstant;

/**
 * 上传运动数据的参数实体
 */
public class UploadSport extends BasePostBean {
    private String accountId;
    private String deviceId;
    private String deviceType = NetLibConstant.DEFAULT_DEVICE_TYPE;
    private String customerCode = NetLibConstant.DEFAULT_CUSTOMER_CODE;
    private int timeZone;
    private List<SportDetail> details;

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

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(int timeZone) {
        this.timeZone = timeZone;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public List<SportDetail> getDetails() {
        return details;
    }

    public void setDetails(List<SportDetail> details) {
        this.details = details;
    }

}
