package cn.appscomm.l38t.model.bean;

import java.io.Serializable;

import cn.appscomm.netlib.bean.leaderboard.LeaderBoard;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/6 10:04
 */
public class LeaderBoardFriend extends LeaderBoard implements Serializable{

    private int rank;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
