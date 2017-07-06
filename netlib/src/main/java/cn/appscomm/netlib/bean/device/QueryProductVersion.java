package cn.appscomm.netlib.bean.device;

import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.constant.NetLibConstant;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/5 14:42
 */
public class QueryProductVersion extends BasePostBean {

    private String productCode = NetLibConstant.DEFAULT_PRODUCT_CODE;
    private String customerCode = NetLibConstant.DEFAULT_CUSTOMER_CODE;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
}
