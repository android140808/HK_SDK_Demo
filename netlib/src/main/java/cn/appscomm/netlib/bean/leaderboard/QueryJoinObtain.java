package cn.appscomm.netlib.bean.leaderboard;

import java.util.List;

import cn.appscomm.netlib.bean.base.BaseObtainBean;


/**
 * Created by wy_ln on 2016/5/27.
 */
public class QueryJoinObtain extends BaseObtainBean {

    List<FriendsAccount> accounts ;

    public List<FriendsAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<FriendsAccount> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        StringBuffer account = new StringBuffer();
        for(FriendsAccount f:accounts){
            account.append(f.toString()+"-----");
        }
        return "QueryJoinObtain{" +
                "accounts=" + account +
                '}';
    }
}
