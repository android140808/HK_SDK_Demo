package cn.appscomm.netlib.bean.sleep;


import java.util.List;

import cn.appscomm.netlib.bean.base.BaseObtainBean;

/**
 * Created by Administrator on 2016/6/14.
 */
public class QuerySleepObtain extends BaseObtainBean {

    private List<SleepData> sleeps;

    public List<SleepData> getSleeps() {
        return sleeps;
    }

    public void setSleeps(List<SleepData> sleeps) {
        this.sleeps = sleeps;
    }

    @Override
    public String toString() {
        StringBuffer sleep = new StringBuffer();
        if(sleeps!=null && sleeps.size()>0){
            for(SleepData sleepData : sleeps){
                sleep.append(sleepData.toString());
            }
        }
        return "QuerySleepObtain{" +
                "sleeps=" + sleep +
                '}';
    }
}
