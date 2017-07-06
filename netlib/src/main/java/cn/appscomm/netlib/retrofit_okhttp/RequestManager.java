package cn.appscomm.netlib.retrofit_okhttp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import cn.appscomm.netlib.R;
import cn.appscomm.netlib.app.NetLibApplicationContext;
import cn.appscomm.netlib.bean.account.AccountEdit;
import cn.appscomm.netlib.bean.account.AccountQuery;
import cn.appscomm.netlib.bean.account.ChangePassword;
import cn.appscomm.netlib.bean.account.ForgetPassword;
import cn.appscomm.netlib.bean.account.GetVeriCode;
import cn.appscomm.netlib.bean.account.Login;
import cn.appscomm.netlib.bean.account.Register;
import cn.appscomm.netlib.bean.account.UpLoadIcon;
import cn.appscomm.netlib.bean.base.BaseObtainBean;
import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.bean.device.QueryProductVersion;
import cn.appscomm.netlib.bean.friends.HandlerFriend;
import cn.appscomm.netlib.bean.friends.InviteFriend;
import cn.appscomm.netlib.bean.friends.LeaderBoardHis;
import cn.appscomm.netlib.bean.friends.PendingFriend;
import cn.appscomm.netlib.bean.friends.UpdateIconUrl;
import cn.appscomm.netlib.bean.heartRate.HeartBeat;
import cn.appscomm.netlib.bean.heartRate.QueryHeartLastTime;
import cn.appscomm.netlib.bean.heartRate.UploadHeart;
import cn.appscomm.netlib.bean.leaderboard.CreateDDID;
import cn.appscomm.netlib.bean.leaderboard.QueryJoin;
import cn.appscomm.netlib.bean.leaderboard.QueryLeaderBoard;
import cn.appscomm.netlib.bean.leaderboard.QueryLeaderBoardWorld;
import cn.appscomm.netlib.bean.mood.MoodFatigue;
import cn.appscomm.netlib.bean.mood.QueryMoodLastTime;
import cn.appscomm.netlib.bean.mood.UploadMood;
import cn.appscomm.netlib.bean.sleep.CountSleepData;
import cn.appscomm.netlib.bean.sleep.QuerySleep;
import cn.appscomm.netlib.bean.sleep.QuerySleepLastTime;
import cn.appscomm.netlib.bean.sleep.UploadSleep;
import cn.appscomm.netlib.bean.sport.CountSportData;
import cn.appscomm.netlib.bean.sport.QuerySport;
import cn.appscomm.netlib.bean.sport.QuerySportLastTime;
import cn.appscomm.netlib.bean.sport.UploadSport;
import cn.appscomm.netlib.bean.token.TokenRetrieve;
import cn.appscomm.netlib.constant.HttpCode;
import cn.appscomm.netlib.retrofit_okhttp.interfaces.HttpFileDownloadListener;
import cn.appscomm.netlib.retrofit_okhttp.interfaces.HttpResponseListener;
import cn.appscomm.netlib.retrofit_okhttp.interfaces.HttpResponstTokenListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Administrator on 2016/9/1.
 */
public class RequestManager {

    private final String TAG = RequestManager.class.getSimpleName();
    private static RequestManager requestManager;
    private UrlService urlService;
    private ArrayList<HttpResponstTokenListener> tokenListeners;


    public RequestManager() {
        tokenListeners = new ArrayList<HttpResponstTokenListener>();
        urlService = RetrofitManager.getInstance().getUrlService();
    }

    public static RequestManager getInstance() {
        if (null == requestManager) {
            synchronized (RequestManager.class) {
                if (null == requestManager) {
                    requestManager = new RequestManager();
                }
            }
        }
        return requestManager;
    }

    private Subscriber createBasePostBeanSubscriber(final BasePostBean postBean, final HttpResponseListener listener) {
        return new Subscriber<BaseObtainBean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                listener.onResponseError(HttpCode.CODE_ERROR, postBean, e);
            }

            @Override
            public void onNext(BaseObtainBean obtainBean) {
                if (obtainBean != null) {
                    int code = obtainBean.getCode();
                    switch (code) {
                        case HttpCode.CODE_ACCESS_TOKEN_NULL:
                        case HttpCode.CODE_ACCESS_TOKEN_INVALID:
                        case HttpCode.CODE_ACCESS_TOKEN_EXPIRED:
                            doTokenCallback(postBean, code);
                            break;
                        default:
                            listener.onResponseSuccess(obtainBean.getCode(), obtainBean);
                            break;
                    }
                } else {
                    String msg = NetLibApplicationContext.getInstance().getAppContext().getString(R.string.obtainBean_is_null);
                    listener.onResponseError(HttpCode.CODE_ERROR, postBean, new Exception(msg));
                }
            }
        };
    }

    private Subscriber createTokenSubscriber(final HttpResponseListener listener) {
        return new Subscriber<BaseObtainBean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                listener.onResponseError(HttpCode.CODE_ERROR, null, e);
            }

            @Override
            public void onNext(BaseObtainBean obtainBean) {
                if (obtainBean != null) {
                    int code = obtainBean.getCode();
                    switch (code) {
                        case HttpCode.CODE_ACCESS_TOKEN_NULL:
                        case HttpCode.CODE_ACCESS_TOKEN_INVALID:
                        case HttpCode.CODE_ACCESS_TOKEN_EXPIRED:
                            doTokenCallback(null, code);
                            break;
                        default:
                            listener.onResponseSuccess(obtainBean.getCode(), obtainBean);
                            break;
                    }
                } else {
                    String msg = NetLibApplicationContext.getInstance().getAppContext().getString(R.string.obtainBean_is_null);
                    listener.onResponseError(HttpCode.CODE_ERROR, null, new Exception(msg));
                }
            }
        };
    }

    private void doTokenCallback(final BasePostBean postBean, final int code) {
        if (tokenListeners != null) {
            for (HttpResponstTokenListener listener : tokenListeners) {
                switch (code) {
                    case HttpCode.CODE_ACCESS_TOKEN_NULL:
                        listener.onNullToken(postBean);
                        break;
                    case HttpCode.CODE_ACCESS_TOKEN_INVALID:
                        listener.onInvalidToken(postBean);
                        break;
                    case HttpCode.CODE_ACCESS_TOKEN_EXPIRED:
                        listener.onExpiredToken(postBean);
                        break;
                }
            }
        }
    }


    /**
     * 登录
     *
     * @param login
     * @param listener
     */
    public void login(final Login login, final HttpResponseListener listener) {
        urlService.beginTrans(login).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(createBasePostBeanSubscriber(login, listener));
    }


    /**
     * 账户信息编辑
     *
     * @param accountEdit
     * @param listener
     */
    public void accountEdit(final AccountEdit accountEdit, final HttpResponseListener listener) {
        urlService.beginTrans(accountEdit).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(createBasePostBeanSubscriber(accountEdit, listener));
    }


    /**
     * 创建朋友圈ID
     *
     * @param createDDID
     * @param listener
     */
    public void createDDID(final CreateDDID createDDID, final HttpResponseListener listener) {
        urlService.beginTrans(createDDID).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(createBasePostBeanSubscriber(createDDID, listener));
    }

    /**
     * 查询个人排行
     *
     * @param queryJoin
     * @param listener
     */
    public void queryJoin(final QueryJoin queryJoin, final HttpResponseListener listener) {
        urlService.beginTrans(queryJoin).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(createBasePostBeanSubscriber(queryJoin, listener));
    }


    /**
     * 查询朋友圈排行榜
     *
     * @param queryLeaderBoard
     * @param listener
     */
    public void queryLeaderBoard(final QueryLeaderBoard queryLeaderBoard, final HttpResponseListener listener) {
        urlService.beginTrans(queryLeaderBoard).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(createBasePostBeanSubscriber(queryLeaderBoard, listener));
    }


    /**
     * 上传心率数据
     *
     * @param uploadHeart
     * @param listener
     */
    public void uploadHeart(final UploadHeart uploadHeart, final HttpResponseListener listener) {
        urlService.beginTrans(uploadHeart).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(createBasePostBeanSubscriber(uploadHeart, listener));
    }

    /**
     * 查询心情疲劳度数据
     *
     * @param moodFatigue
     * @param listener
     */
    public void moodFatigue(final MoodFatigue moodFatigue, final HttpResponseListener listener) {
        urlService.beginTrans(moodFatigue).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(createBasePostBeanSubscriber(moodFatigue, listener));
    }

    /**
     * 查询心情疲劳度最后上传时间
     *
     * @param queryMoodLastTime
     * @param listener
     */
    public void queryMoodLastTime(final QueryMoodLastTime queryMoodLastTime, final HttpResponseListener listener) {
        urlService.beginTrans(queryMoodLastTime).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(createBasePostBeanSubscriber(queryMoodLastTime, listener));
    }

    /**
     * 上传心情疲劳度数据
     *
     * @param uploadMood
     * @param listener
     */
    public void uploadMood(final UploadMood uploadMood, final HttpResponseListener listener) {
        urlService.beginTrans(uploadMood).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createBasePostBeanSubscriber(uploadMood, listener));
    }

    /**
     * 查询睡眠统计数据
     *
     * @param countSleepData
     * @param listener
     */
    public void countSleepData(final CountSleepData countSleepData, final HttpResponseListener listener) {
        urlService.beginTrans(countSleepData).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(createBasePostBeanSubscriber(countSleepData, listener));
    }


//    /**
//     * 查询睡眠详细数据
//     *
//     * @param querySleep
//     * @param listener
//     */
//    public void querySleep(final QuerySleep querySleep, final HttpResponseListener listener) {
//        urlService.beginTrans(querySleep).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(createBasePostBeanSubscriber(querySleep, listener));
//    }

    /**
     * 上传睡眠数据
     *
     * @param uploadSleep
     * @param listener
     */
    public void uploadSleep(final UploadSleep uploadSleep, final HttpResponseListener listener) {
        urlService.beginTrans(uploadSleep).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(createBasePostBeanSubscriber(uploadSleep, listener));
    }

    /**
     * 查询运动详细数据
     *
     * @param querySport
     * @param listener
     */
    public void querySport(final QuerySport querySport, final HttpResponseListener listener) {
        urlService.beginTrans(querySport).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(createBasePostBeanSubscriber(querySport, listener));
    }


    /**
     * 查询运动统计数据
     *
     * @param countSportData
     * @param listener
     */
    public void countSportData(final CountSportData countSportData, final HttpResponseListener listener) {
        urlService.beginTrans(countSportData).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(createBasePostBeanSubscriber(countSportData, listener));
    }


    /**
     * 上传运动数据
     *
     * @param uploadSport
     * @param listener
     */
    public void uploadSport(final UploadSport uploadSport, final HttpResponseListener listener) {
        urlService.beginTrans(uploadSport).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(createBasePostBeanSubscriber(uploadSport, listener));
    }

    /**
     * 检查固件版本号
     *
     * @param queryProductVersion
     * @param listener
     */
    public void queryProductVersion(final QueryProductVersion queryProductVersion, final HttpResponseListener listener) {
        urlService.beginTrans(queryProductVersion).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(createBasePostBeanSubscriber(queryProductVersion, listener));
    }


    public void registerTokenListener(HttpResponstTokenListener tokenListener) {
        if (tokenListeners != null && !tokenListeners.contains(tokenListener)) {
            tokenListeners.add(tokenListener);
        }
    }

    public void unregisterTokenListener(HttpResponstTokenListener tokenListener) {
        if (tokenListeners != null && tokenListeners.contains(tokenListener)) {
            tokenListeners.remove(tokenListener);
        }
    }


    /**
     * 下载文件
     *
     * @param fileUrl
     * @param fileDownloadListener
     */
    public void downloadFile(final String fileUrl, final String defaultFileExtension, final HttpFileDownloadListener fileDownloadListener) {
        Request request = new Request.Builder().url(fileUrl).build();
        RetrofitManager.getInstance().getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (fileDownloadListener != null) {
                    fileDownloadListener.onDownloadError(call, fileUrl, e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    if (fileDownloadListener != null) {
                        fileDownloadListener.onDownloadError(call, fileUrl, new Exception("" + response.code()));
                    }
                    return;
                }
                InputStream inputStream = null;
                FileOutputStream outputStream = null;
                File file = null;
                try {
                    MediaType contentType = response.body().contentType();
                    long total = response.body().contentLength();
                    String sdCardPath = NetLibApplicationContext.getInstance().getAppContext().getFilesDir().getAbsolutePath();
                    final String saveFileName = "upgrade.zip";
                    file = new File(sdCardPath, saveFileName);
                    outputStream = new FileOutputStream(file);
                    inputStream = response.body().byteStream();
                    byte[] buf = new byte[1024];
                    int len = 0;
                    long sum = 0;
                    if (fileDownloadListener != null) {
                        fileDownloadListener.onDownloadBegin(call, contentType, total);
                    }
                    while ((len = inputStream.read(buf)) != -1) {
                        outputStream.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        if (fileDownloadListener != null) {
                            fileDownloadListener.onDownloading(call, contentType, total, progress);
                        }
                    }
                    outputStream.flush();
                    if (fileDownloadListener != null) {
                        fileDownloadListener.onDownloadSuccess(fileUrl, file.getAbsolutePath(), contentType);
                    }
                } catch (IOException e) {
                    if (file != null && file.exists()) {
                        file.delete();//下载失败，删除失败文件
                    }
                    if (fileDownloadListener != null) {
                        fileDownloadListener.onDownloadError(call, fileUrl, e);
                    }
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    /**
     * 内存回收
     */
    public void onDestory() {
        urlService = null;
        requestManager = null;
        if (tokenListeners != null) {
            tokenListeners.clear();
            tokenListeners = null;
        }
        HttpCode.getInstance(NetLibApplicationContext.getInstance().getAppContext()).onDestory();
        RetrofitManager.getInstance().onDestory();
    }

}
