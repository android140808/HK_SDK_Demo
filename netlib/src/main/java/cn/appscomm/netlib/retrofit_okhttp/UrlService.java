package cn.appscomm.netlib.retrofit_okhttp;

import cn.appscomm.netlib.bean.account.AccountEdit;
import cn.appscomm.netlib.bean.account.AccountEditObtain;
import cn.appscomm.netlib.bean.account.AccountQuery;
import cn.appscomm.netlib.bean.account.AccountQueryObtain;
import cn.appscomm.netlib.bean.account.ChangePassword;
import cn.appscomm.netlib.bean.account.ChangePasswordObtain;
import cn.appscomm.netlib.bean.account.ForgetPassword;
import cn.appscomm.netlib.bean.account.ForgetPasswordObtain;
import cn.appscomm.netlib.bean.account.GetVeriCode;
import cn.appscomm.netlib.bean.account.GetVeriCodeObtain;
import cn.appscomm.netlib.bean.account.Login;
import cn.appscomm.netlib.bean.account.LoginObtain;
import cn.appscomm.netlib.bean.account.Register;
import cn.appscomm.netlib.bean.account.RegisterObtain;
import cn.appscomm.netlib.bean.account.UpLoadIcon;
import cn.appscomm.netlib.bean.account.UpLoadIconObtain;
import cn.appscomm.netlib.bean.device.QueryProductVersion;
import cn.appscomm.netlib.bean.device.QueryProductVersionObtain;
import cn.appscomm.netlib.bean.friends.HandlerFriend;
import cn.appscomm.netlib.bean.friends.HandlerFriendObtain;
import cn.appscomm.netlib.bean.friends.InviteFriend;
import cn.appscomm.netlib.bean.friends.InviteFriendObtain;
import cn.appscomm.netlib.bean.friends.LeaderBoardHis;
import cn.appscomm.netlib.bean.friends.LeaderBoardHisObtain;
import cn.appscomm.netlib.bean.friends.PendingFriend;
import cn.appscomm.netlib.bean.friends.PendingFriendObtain;
import cn.appscomm.netlib.bean.friends.UpdateIconUrl;
import cn.appscomm.netlib.bean.friends.UpdateIconUrlObtain;
import cn.appscomm.netlib.bean.heartRate.HeartBeat;
import cn.appscomm.netlib.bean.heartRate.HeartBeatObtain;
import cn.appscomm.netlib.bean.heartRate.QueryHeartLastTime;
import cn.appscomm.netlib.bean.heartRate.QueryHeartLastTimeObtain;
import cn.appscomm.netlib.bean.heartRate.UploadHeart;
import cn.appscomm.netlib.bean.heartRate.UploadHeartObtain;
import cn.appscomm.netlib.bean.leaderboard.CreateDDID;
import cn.appscomm.netlib.bean.leaderboard.CreateDDIDObtain;
import cn.appscomm.netlib.bean.leaderboard.QueryJoin;
import cn.appscomm.netlib.bean.leaderboard.QueryJoinObtain;
import cn.appscomm.netlib.bean.leaderboard.QueryLeaderBoard;
import cn.appscomm.netlib.bean.leaderboard.QueryLeaderBoardObtain;
import cn.appscomm.netlib.bean.leaderboard.QueryLeaderBoardWorld;
import cn.appscomm.netlib.bean.leaderboard.QueryLeaderBoardWorldObtain;
import cn.appscomm.netlib.bean.mood.MoodFatigue;
import cn.appscomm.netlib.bean.mood.MoodFatigueObtain;
import cn.appscomm.netlib.bean.mood.QueryMoodLastTime;
import cn.appscomm.netlib.bean.mood.QueryMoodLastTimeObtain;
import cn.appscomm.netlib.bean.mood.UploadMood;
import cn.appscomm.netlib.bean.mood.UploadMoodObtain;
import cn.appscomm.netlib.bean.sleep.CountSleepData;
import cn.appscomm.netlib.bean.sleep.CountSleepObtain;
import cn.appscomm.netlib.bean.sleep.QuerySleep;
import cn.appscomm.netlib.bean.sleep.QuerySleepLastTime;
import cn.appscomm.netlib.bean.sleep.QuerySleepLastTimeObtain;
import cn.appscomm.netlib.bean.sleep.QuerySleepObtain;
import cn.appscomm.netlib.bean.sleep.UploadSleep;
import cn.appscomm.netlib.bean.sleep.UploadSleepObtain;
import cn.appscomm.netlib.bean.sport.CountSportData;
import cn.appscomm.netlib.bean.sport.CountSportObtain;
import cn.appscomm.netlib.bean.sport.QuerySport;
import cn.appscomm.netlib.bean.sport.QuerySportLastTime;
import cn.appscomm.netlib.bean.sport.QuerySportLastTimeObtain;
import cn.appscomm.netlib.bean.sport.UploadSport;
import cn.appscomm.netlib.bean.sport.UploadSportObtain;
import cn.appscomm.netlib.bean.token.TokenRetrieveObtain;
import cn.appscomm.netlib.bean.token.TokenRetrieve;
import cn.appscomm.netlib.constant.NetLibConstant;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Administrator on 2016/8/31.
 */
public interface UrlService {

    @POST(NetLibConstant.login)
    Observable<LoginObtain> beginTrans(@Body Login login);

    @POST(NetLibConstant.accountEdit)
    Observable<AccountEditObtain> beginTrans(@Body AccountEdit accountEdit);

    @POST(NetLibConstant.url_createDD)
    Observable<CreateDDIDObtain> beginTrans(@Body CreateDDID createDDID);

    @POST(NetLibConstant.url_queryJoin)
    Observable<QueryJoinObtain> beginTrans(@Body QueryJoin queryJoin);

    @POST(NetLibConstant.url_queryLeaderBoard)
    Observable<QueryLeaderBoardObtain> beginTrans(@Body QueryLeaderBoard queryLeaderBoard);

    @POST(NetLibConstant.uploadHeartRecord)
    Observable<UploadHeartObtain> beginTrans(@Body UploadHeart upHeart);

    @POST(NetLibConstant.get_moodFatigue)
    Observable<MoodFatigueObtain> beginTrans(@Body MoodFatigue moodFatigue);

    @POST(NetLibConstant.url_queryUploadMoodTime)
    Observable<QueryMoodLastTimeObtain> beginTrans(@Body QueryMoodLastTime queryMoodLastTime);

    @POST(NetLibConstant.uploadMoodRecord)
    Observable<UploadMoodObtain> beginTrans(@Body UploadMood uploadMood);

    @POST(NetLibConstant.getCountSleep)
    Observable<CountSleepObtain> beginTrans(@Body CountSleepData countSleepData);

//    @POST(NetLibConstant.getSleepData)
//    Observable<QuerySleepObtain> beginTrans(@Body QuerySleep querySleep);

    @POST(NetLibConstant.uploadSleepRecord)
    Observable<UploadSleepObtain> beginTrans(@Body UploadSleep upSleep);

    @POST(NetLibConstant.getCountSport)
    Observable<CountSportObtain> beginTrans(@Body CountSportData countSportData);

    @POST(NetLibConstant.uploadSportData)
    Observable<UploadSportObtain> beginTrans(@Body UploadSport upSport);

    @POST(NetLibConstant.url_querySport)
    Observable<UploadSportObtain> beginTrans(@Body QuerySport querySport);

    @POST(NetLibConstant.getFirmwareVersion)
    Observable<QueryProductVersionObtain> beginTrans(@Body QueryProductVersion queryProductVersion);
}
