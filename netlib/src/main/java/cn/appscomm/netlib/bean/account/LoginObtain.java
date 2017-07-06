package cn.appscomm.netlib.bean.account;

import cn.appscomm.netlib.bean.base.BaseObtainBean;
import cn.appscomm.netlib.bean.base.ObtainResMap;

/**
 * Created by wy_ln on 2016/5/27.
 */
public class LoginObtain extends BaseObtainBean {

    /**
     * seq : 123456789
     * code : 0
     * msg :  sucess ^_^
     * resMap : {"userInfo":{"userInfoId":9,"refUserId":6,"userName":null,"nickname":null,"sex":null,"birthday":null,"iconUrl":null,"height":0,"heightUnit":0,"weight":0,"weightUnit":0,"country":null,"province":null,"city":null,"area":null,"isManage":1}}
     * accessToken : 9d1d703672d879a77eb6833691756f31a575344b1258a2754327ec8c1bf3db28
     */
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
