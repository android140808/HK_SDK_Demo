package cn.appscomm.netlib.bean.sport;

import java.util.ArrayList;

import cn.appscomm.netlib.bean.base.BaseObtainBean;

/**
 * Created by Administrator on 2016/6/17.
 */
public class UploadSportObtain extends BaseObtainBean {
    private ArrayList<SportDetail> details = new ArrayList<>();

    public ArrayList<SportDetail> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<SportDetail> details) {
        this.details = details;
    }
}
