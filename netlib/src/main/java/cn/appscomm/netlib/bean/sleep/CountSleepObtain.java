package cn.appscomm.netlib.bean.sleep;

import java.util.List;

import cn.appscomm.netlib.bean.base.BaseObtainBean;

public class CountSleepObtain extends BaseObtainBean{
    private List<CountSleepDetail> sleeps;

    public List<CountSleepDetail> getSleeps() {
        return sleeps;
    }

    public void setSleeps(List<CountSleepDetail> sleeps) {
        this.sleeps = sleeps;
    }

    @Override
    public String toString() {
        StringBuffer sleep = new StringBuffer();
        if(sleeps!=null && sleeps.size()>0){
            for(CountSleepDetail countSleepDetail : sleeps){
                sleep.append(countSleepDetail.toString());
            }
        }
        return "CountSleepObtain{" +
                "sleeps=" + sleep +
                '}';
    }
}
