package cn.appscomm.netlib.bean.leaderboard;

import cn.appscomm.netlib.bean.base.BasePostBean;


/**
 * Created by wy_ln on 2016/5/26.
 */
public class QueryLeaderBoard extends BasePostBean {

    private int ddId;
    private String queryDateStart;
    private String queryDateEnd;


    public QueryLeaderBoard(int ddId) {
        this.ddId = ddId;
    }

    public int getDdId() {
        return ddId;
    }

    public void setDdId(int ddId) {
        this.ddId = ddId;
    }

    public String getQueryDateStart() {
        return queryDateStart;
    }

    public void setQueryDateStart(String queryDateStart) {
        this.queryDateStart = queryDateStart;
    }

    public String getQueryDateEnd() {
        return queryDateEnd;
    }

    public void setQueryDateEnd(String queryDateEnd) {
        this.queryDateEnd = queryDateEnd;
    }
}
