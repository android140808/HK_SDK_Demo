package cn.appscomm.netlib.bean.base;

import android.content.Context;
import android.content.pm.PackageManager;

import cn.appscomm.netlib.app.NetLibApplicationContext;
import cn.appscomm.netlib.constant.NetLibConstant;

/**
 * Created by wy_ln on 2016/5/31.
 */
public class BasePostBean {
    private String seq;
    private String versionNo;
    private int language;
    private String clientType;

    public BasePostBean() {
        seq = (int) (System.currentTimeMillis() / 1000) + "";
        versionNo = "1.0.0";
        try {
            Context context = NetLibApplicationContext.getInstance().getAppContext();
            versionNo = ((context.getPackageManager().getPackageInfo(context.getPackageName(), 0)).versionName) + "";
        } catch (PackageManager.NameNotFoundException e) {
        }
        this.language = NetLibConstant.DEFAULT_LANGUAGE;
        this.clientType = NetLibConstant.DEFAULT_CLIENT_TYPE;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }
}
