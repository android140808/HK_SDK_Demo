package cn.appscomm.netlib.bean.leaderboard;

import cn.appscomm.netlib.bean.base.BasePostBean;


/**
 * Created by wy_ln on 2016/5/26.
 */
public class QueryLeaderBoardWorld extends BasePostBean {

    private String accountId;
    private String queryDateStart;
    private String queryDateEnd;

    public QueryLeaderBoardWorld(String accountId) {
        this.accountId = accountId;
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

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
