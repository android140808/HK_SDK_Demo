package cn.appscomm.netlib.bean.token;

import cn.appscomm.netlib.bean.base.BaseObtainBean;

/**
 * Created by Administrator on 2016/8/31.
 */
public class TokenRetrieveObtain extends BaseObtainBean {
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
