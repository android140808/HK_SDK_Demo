package cn.appscomm.netlib.retrofit_okhttp.interfaces;

import java.io.File;

import cn.appscomm.netlib.bean.base.BaseObtainBean;
import cn.appscomm.netlib.bean.base.BasePostBean;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/9 09:19
 */
public interface HttpFileDownloadListener {
    void onDownloadSuccess(final String fileUrl, final String fileFullPathName, final MediaType contentType);

    void onDownloadBegin(final Call call,final MediaType contentType, final long total);

    void onDownloading(final Call call,final MediaType contentType, final long total, final int progress);

    void onDownloadError(final Call call, final String fileUrl, Throwable e);
}
