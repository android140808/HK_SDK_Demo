package cn.appscomm.netlib.util;

import android.text.TextUtils;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/9 10:38
 */
public class FileUtil {

    public static final String getDownloadFileUrlExetend(String fileUrl, String defaultExt) {
        int lastIndex = fileUrl.lastIndexOf(".");
        String exte = fileUrl.substring(lastIndex, fileUrl.length());
        if (TextUtils.isEmpty(exte)) {
            return "."+defaultExt;
        } else {
            return exte.toLowerCase();
        }
    }
}
