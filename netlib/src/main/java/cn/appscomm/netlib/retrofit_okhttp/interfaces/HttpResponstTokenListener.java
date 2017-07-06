package cn.appscomm.netlib.retrofit_okhttp.interfaces;

import cn.appscomm.netlib.bean.base.BasePostBean;

/**
 * Created by Administrator on 2016/9/1.
 */
public interface HttpResponstTokenListener {

    void onInvalidToken(BasePostBean postBean);

    void onExpiredToken(BasePostBean postBean);

    void onNullToken(BasePostBean postBean);
}
