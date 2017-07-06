package cn.appscomm.netlib.bean.device;

import cn.appscomm.netlib.bean.base.BaseObtainBean;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/5 14:43
 */
public class QueryProductVersionObtain extends BaseObtainBean {
    private CrmProductVersion crmProductVersion;

    public CrmProductVersion getCrmProductVersion() {
        return crmProductVersion;
    }

    public void setCrmProductVersion(CrmProductVersion crmProductVersion) {
        this.crmProductVersion = crmProductVersion;
    }
}
