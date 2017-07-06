package cn.appscomm.netlib.retrofit_okhttp.interfaces;

import cn.appscomm.netlib.bean.base.BaseObtainBean;
import cn.appscomm.netlib.bean.base.BasePostBean;

/**
 * Created by Administrator on 2016/9/1.
 */
public interface HttpResponseListener {
    void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean);

    void onResponseError(int statusCode, BasePostBean postBean, Throwable e);
}
