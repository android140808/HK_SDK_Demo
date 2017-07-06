package cn.appscomm.l38t.loader;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.model.database.QueryLeaderBoardDB;
import cn.appscomm.l38t.utils.LogUtil;
import cn.appscomm.netlib.bean.base.BaseObtainBean;
import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.bean.leaderboard.CreateDDID;
import cn.appscomm.netlib.bean.leaderboard.FriendsAccount;
import cn.appscomm.netlib.bean.leaderboard.LeaderBoard;
import cn.appscomm.netlib.bean.leaderboard.QueryJoin;
import cn.appscomm.netlib.bean.leaderboard.QueryJoinObtain;
import cn.appscomm.netlib.bean.leaderboard.QueryLeaderBoard;
import cn.appscomm.netlib.bean.leaderboard.QueryLeaderBoardObtain;
import cn.appscomm.netlib.constant.HttpCode;
import cn.appscomm.netlib.retrofit_okhttp.RequestManager;
import cn.appscomm.netlib.retrofit_okhttp.interfaces.HttpResponseListener;


/**
 * Created by weiliu on 2016/7/20.
 */
public class LeaderBoardLoader {

    private Gson gson = new GsonBuilder().create();
    private static LeaderBoardLoader loader;
    private List<LeaderBoard> leaderBoardList; // 排行榜所有数据，同时提供给FriendActivity好友列表展示

    public static LeaderBoardLoader getInstance() {
        if (null == loader) {
            synchronized (LeaderBoardLoader.class) {
                if (null == loader) {
                    loader = new LeaderBoardLoader();
                }
            }
        }
        return loader;
    }

    public interface LeaderBoardLoaderListener {
        void onResult(List<LeaderBoard> resultList);

        void onFriendsAccountResult(List<FriendsAccount> friendsAccountList);
    }

    public LeaderBoardLoader() {
        init();
    }

    private void init() {
        leaderBoardList = new ArrayList<>();
    }

    public void queryLeaderBoard(final int ddId, final LeaderBoardLoaderListener callBack) {
        final QueryLeaderBoard queryLeaderBoard = new QueryLeaderBoard(ddId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        final String today = sdf.format(new Date(calendar.getTimeInMillis()));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        final String tomorrow = sdf.format(new Date(calendar.getTimeInMillis()));
        queryLeaderBoard.setQueryDateStart(today);
        queryLeaderBoard.setQueryDateEnd(tomorrow);
        RequestManager.getInstance().queryLeaderBoard(queryLeaderBoard, new HttpResponseListener() {
            @Override
            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
                if (HttpCode.isSuccess(baseObtainBean.getCode())) {
                    QueryLeaderBoardObtain queryLeaderBoardObtain = (QueryLeaderBoardObtain) baseObtainBean;
                    showLeaderBoard(queryLeaderBoardObtain, callBack);
                    saveLeaderBoard(queryLeaderBoard, queryLeaderBoardObtain);
                } else {
                    QueryLeaderBoardObtain queryLeaderBoardObtain = getLocalDbLeaderBoardData(queryLeaderBoard);
                    showLeaderBoard(queryLeaderBoardObtain, callBack);
                }
            }

            @Override
            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {
                QueryLeaderBoardObtain queryLeaderBoardObtain = getLocalDbLeaderBoardData(queryLeaderBoard);
                showLeaderBoard(queryLeaderBoardObtain, callBack);
            }
        });
    }

    private void showLeaderBoard(QueryLeaderBoardObtain queryLeaderBoardObtain, final LeaderBoardLoaderListener callBack) {
        if (leaderBoardList == null) {
            init();
        }
        leaderBoardList.clear();
        if (queryLeaderBoardObtain != null) {
            if (queryLeaderBoardObtain.getDetails().size() > 0) {
                for (int i = 0; i < queryLeaderBoardObtain.getDetails().size(); i++) {
                    LeaderBoard leaderBoard = queryLeaderBoardObtain.getDetails().get(i);
                    leaderBoardList.add(leaderBoard);
                }
                if (callBack != null) {
                    sortList(true);//排序数据
                    callBack.onResult(leaderBoardList);
                }
            }
        }
    }

    private void saveLeaderBoard(QueryLeaderBoard queryLeaderBoard, QueryLeaderBoardObtain queryLeaderBoardObtain) {
        QueryLeaderBoardDB queryLeaderBoardDB = new QueryLeaderBoardDB();
        queryLeaderBoardDB.setQueryDateStart(queryLeaderBoard.getQueryDateStart());
        queryLeaderBoardDB.setQueryDateEnd(queryLeaderBoard.getQueryDateEnd());
        queryLeaderBoardDB.setDdId(queryLeaderBoard.getDdId());
        queryLeaderBoardDB.setContent(gson.toJson(queryLeaderBoardObtain));
        if (DataSupport.where("queryDateStart=? and queryDateEnd =? and ddId=?", queryLeaderBoard.getQueryDateStart() + "", queryLeaderBoard.getQueryDateEnd() + "", queryLeaderBoard.getDdId() + "").find(QueryLeaderBoardDB.class).size() > 0) {
            ContentValues values = new ContentValues();
            values.put("content", gson.toJson(queryLeaderBoardObtain));
            DataSupport.updateAll(QueryLeaderBoardDB.class, values, "queryDateStart=? and queryDateEnd =? and ddId=?", queryLeaderBoard.getQueryDateStart() + "", queryLeaderBoard.getQueryDateEnd() + "", queryLeaderBoard.getDdId() + "");
        } else {
            queryLeaderBoardDB.save();
        }
    }

    private QueryLeaderBoardObtain getLocalDbLeaderBoardData(QueryLeaderBoard queryLeaderBoard) {
        QueryLeaderBoardDB queryLeaderBoardDB = new QueryLeaderBoardDB();
        queryLeaderBoardDB.setQueryDateStart(queryLeaderBoard.getQueryDateStart());
        queryLeaderBoardDB.setQueryDateEnd(queryLeaderBoard.getQueryDateEnd());
        queryLeaderBoardDB.setDdId(queryLeaderBoard.getDdId());
        QueryLeaderBoardObtain queryLeaderBoardObtain = null;
        queryLeaderBoardDB = DataSupport.where("queryDateStart<=? and queryDateEnd >=? and ddId=?", queryLeaderBoard.getQueryDateStart() + "", queryLeaderBoard.getQueryDateEnd() + "", queryLeaderBoard.getDdId() + "").findFirst(QueryLeaderBoardDB.class);
        if (queryLeaderBoardDB != null) {
            queryLeaderBoardObtain = gson.fromJson(queryLeaderBoardDB.getContent(), QueryLeaderBoardObtain.class);
        }
        return queryLeaderBoardObtain;
    }

    private void sortList(final boolean flag) {
        LogUtil.i("Loader", "--------------排序前---------------");
        printResult();
        Collections.sort(leaderBoardList, new Comparator<LeaderBoard>() {
            @Override
            public int compare(LeaderBoard lhs, LeaderBoard rhs) {
                if (flag) {
                    return rhs.getSportsStep() - lhs.getSportsStep();
                } else {
                    return lhs.getSportsStep() - rhs.getSportsStep();
                }
            }
        });
        LogUtil.i("Loader", "--------------排序后---------------");
        printResult();
    }

    public List<LeaderBoard> getLeaderBoardList() {
        List<LeaderBoard> copyList = new ArrayList<LeaderBoard>();//做一份数据拷贝
        sortList(true);//排序数据
        for (int i = 0; i <= leaderBoardList.size() - 1; i++) {
            copyList.add(leaderBoardList.get(i));
        }
        return copyList;
    }

    private void printResult() {
        for (int i = 0; i <= leaderBoardList.size() - 1; i++) {
            LogUtil.i("Loader", leaderBoardList.get(i).getUserName() + "," + leaderBoardList.get(i).getSportsStep());
        }
    }

    public String getMaxStepString() {
        String text = "10" + "k";
        int max = 0;
        for (int i = 0; i <= leaderBoardList.size() - 1; i++) {
            if (leaderBoardList.get(i).getSportsStep() >= max) {
                max = leaderBoardList.get(i).getSportsStep();
            }
        }
        int AVG_MAX_STEP = 1000;
        for (int i = 10; i <= 10 * 100; i = i + 10) {
            if (max <= i * AVG_MAX_STEP) {
                text = i + "k";
                break;
            }
        }
        return text;
    }


    public void leaderBoardQueryJoin(final LeaderBoardLoaderListener callBack) {
        String accountId = AccountConfig.getUserLoginName();
        QueryJoin queryJoin = new QueryJoin(accountId);
        RequestManager.getInstance().queryJoin(queryJoin, new HttpResponseListener() {
            @Override
            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
                if (HttpCode.isSuccess(baseObtainBean.getCode())) {
                    QueryJoinObtain queryJoinObtain = (QueryJoinObtain) baseObtainBean;
                    if (queryJoinObtain.getAccounts().size() <= 0) {
                        createDD(callBack);
                    } else {
                        if (callBack != null) {
                            callBack.onFriendsAccountResult(queryJoinObtain.getAccounts());
                        }
                    }
                }
            }

            @Override
            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {

            }
        });
    }

    public void createDD(final LeaderBoardLoaderListener callBack) {
        String accountId = AccountConfig.getUserLoginName();
        CreateDDID createDDID = new CreateDDID(accountId);
        RequestManager.getInstance().createDDID(createDDID, new HttpResponseListener() {
            @Override
            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
                if (HttpCode.isSuccess(baseObtainBean.getCode())) {
                    if (callBack != null) {
                        leaderBoardQueryJoin(callBack);
                    }
                }
            }

            @Override
            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {
            }
        });
    }


    public void release() {
        if (leaderBoardList != null) {
            leaderBoardList.clear();
            leaderBoardList = null;
        }
    }

}
