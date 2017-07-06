package cn.appscomm.netlib.bean.mood;

import java.util.ArrayList;

import cn.appscomm.netlib.bean.base.BaseObtainBean;


/**
 * Created by weiliu on 2016/8/6.
 */
public class MoodFatigueObtain extends BaseObtainBean {
    private ArrayList<MoodFatigueData> details;

    public ArrayList<MoodFatigueData> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<MoodFatigueData> details) {
        this.details = details;
    }

}
