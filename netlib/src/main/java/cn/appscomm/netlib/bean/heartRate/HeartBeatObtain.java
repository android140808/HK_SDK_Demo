package cn.appscomm.netlib.bean.heartRate;

import java.util.ArrayList;

import cn.appscomm.netlib.bean.base.BaseObtainBean;

/**
 * Created by weiliu on 2016/7/25.
 */
public class HeartBeatObtain  extends BaseObtainBean {

    private ArrayList<HeartBeatData> details;

    public ArrayList<HeartBeatData> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<HeartBeatData> details) {
        this.details = details;
    }

}
