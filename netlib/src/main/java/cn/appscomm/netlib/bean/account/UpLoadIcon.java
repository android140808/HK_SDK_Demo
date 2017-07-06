package cn.appscomm.netlib.bean.account;

import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.constant.NetLibConstant;

/**
 * Created by wy_ln on 2016/5/26.
 */
public class UpLoadIcon extends BasePostBean {

    private int userInfoId;
    private String image;
    private String customerCode = NetLibConstant.DEFAULT_CUSTOMER_CODE;
    private String imageSuffix;


    public UpLoadIcon(int userInfoId) {
        this.userInfoId = userInfoId;
    }

    public Integer getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(Integer userInfoId) {
        this.userInfoId = userInfoId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageSuffix() {
        return imageSuffix;
    }

    public void setImageSuffix(String imageSuffix) {
        this.imageSuffix = imageSuffix;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
}
