package cn.appscomm.netlib.bean.leaderboard;

import java.util.List;

import cn.appscomm.netlib.bean.base.BaseObtainBean;


/**
 * Created by wy_ln on 2016/5/27.
 */
public class QueryLeaderBoardObtain extends BaseObtainBean {
     List<LeaderBoard> details;

    public List<LeaderBoard> getDetails() {
        return details;
    }

    public void setDetails(List<LeaderBoard> details) {
        this.details = details;
    }

}
