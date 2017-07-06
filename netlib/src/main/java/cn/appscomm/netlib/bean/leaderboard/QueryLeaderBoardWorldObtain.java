package cn.appscomm.netlib.bean.leaderboard;

import java.util.List;

import cn.appscomm.netlib.bean.base.BaseObtainBean;


/**
 * Created by wy_ln on 2016/5/26.
 */
public class QueryLeaderBoardWorldObtain extends BaseObtainBean {
    List<LeaderBoardWorld> details;

    public List<LeaderBoardWorld> getDetails() {
        return details;
    }

    public void setDetails(List<LeaderBoardWorld> details) {
        this.details = details;
    }
}
