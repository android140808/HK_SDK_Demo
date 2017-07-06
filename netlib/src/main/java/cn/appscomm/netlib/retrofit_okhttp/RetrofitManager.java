package cn.appscomm.netlib.retrofit_okhttp;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.appscomm.netlib.app.NetLibApplicationContext;
import cn.appscomm.netlib.bean.account.Login;
import cn.appscomm.netlib.bean.account.LoginObtain;
import cn.appscomm.netlib.bean.account.UserInfoBean;
import cn.appscomm.netlib.bean.base.BaseObtainBean;
import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.bean.base.ObtainResMap;
import cn.appscomm.netlib.config.NetConfig;
import cn.appscomm.netlib.constant.HttpCode;
import cn.appscomm.netlib.constant.NetLibConstant;
import cn.appscomm.netlib.retrofit_okhttp.interfaces.HttpResponseListener;
import cn.appscomm.netlib.util.NetworkUtil;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/8/31.
 */
public class RetrofitManager {
    private static final String TAG = RetrofitManager.class.getSimpleName();
    public static final String BASE_NIUPAI_URL = NetLibConstant.Domain;
    //短缓存有效期为1秒钟
    public static final int CACHE_STALE_SHORT = 1;
    //长缓存有效期为7天
    public static final int CACHE_STALE_LONG = 60 * 60 * 24 * 7;
    public static final String CACHE_CONTROL_AGE = "Cache-Control: public, max-age=";
    //查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    public static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_LONG;
    //查询网络的Cache-Control设置，头部Cache-Control设为max-age=0时则不会使用缓存而请求服务器
    public static final String CACHE_CONTROL_NETWORK = "max-age=0";
    public static long HTTP_CACHE_SIZE = 10 * 1024 * 1024; // 10 MiB
    /*  默认连接超时 15s */
    public static final int DEFAULT_CONNECT_TIMEOUT = 15;

    private OkHttpClient mOkHttpClient;
    private UrlService urlService;
    private static RetrofitManager retrofitManager;
    private Context mContext; // 上下文

    public static RetrofitManager builder() {
        RetrofitManager retrofitManager = new RetrofitManager();
        retrofitManager.init();
        return retrofitManager;
    }

    public static RetrofitManager getInstance() {
        if (null == retrofitManager) {
            synchronized (RetrofitManager.class) {
                if (null == retrofitManager) {
                    retrofitManager = RetrofitManager.builder();
                }
            }
        }
        return retrofitManager;
    }

    private void init() {
        if (NetLibApplicationContext.getInstance().getAppContext() == null) {
            throw new NullPointerException("必须要在Application中创建ApplicationContextUtil的init()方法");
        }
        this.mContext = NetLibApplicationContext.getInstance().getAppContext();
        initOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_NIUPAI_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        urlService = retrofit.create(UrlService.class);
    }

    private void initOkHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (RetrofitManager.class) {
                if (mOkHttpClient == null) {
                    // 指定缓存路径,缓存大小100Mb
                    Cache cache = new Cache(new File(mContext.getApplicationContext().getCacheDir(), "HttpCache"), HTTP_CACHE_SIZE);
                    mOkHttpClient = new OkHttpClient.Builder()
                            .cache(cache)
                            .addInterceptor(mRewriteCacheControlInterceptor)
                            .addNetworkInterceptor(mRewriteCacheControlInterceptor)
                            .addInterceptor(createHttpLoggingInterceptor())
                            .addInterceptor(createAccessTokenInterceptor())
                            .retryOnConnectionFailure(true)
                            .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
    }

    private Interceptor createAccessTokenInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader("accessToken", NetConfig.getAccessToken())
                        .addHeader("accessSeq", String.valueOf(System.currentTimeMillis()))
                        .build();
                return chain.proceed(request);
            }
        };
    }

    private HttpLoggingInterceptor createHttpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    // 云端响应头拦截器，用来配置缓存策略
    private Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetworkUtil.isNetworkConnected(mContext.getApplicationContext())) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }
            Response originalResponse = chain.proceed(request);
            if (NetworkUtil.isNetworkConnected(mContext.getApplicationContext())) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder().header("Cache-Control", cacheControl).removeHeader("Pragma").build();
            } else {
                return originalResponse.newBuilder().header("Cache-Control", CACHE_CONTROL_CACHE).removeHeader("Pragma").build();
            }
        }
    };

    public UrlService getUrlService() {
        return urlService;
    }


    public OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            initOkHttpClient();
        }
        return mOkHttpClient;
    }

    public void onDestory() {
        urlService=null;
        mOkHttpClient = null;
        retrofitManager = null;
        mContext = null;
    }

}
