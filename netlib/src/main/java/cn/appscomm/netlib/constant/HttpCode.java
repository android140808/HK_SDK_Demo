package cn.appscomm.netlib.constant;

import android.content.Context;
import android.util.SparseArray;

import cn.appscomm.netlib.R;

/**
 * Created by Administrator on 2016/8/10.
 */
public class HttpCode {

    public static final int CODE_ERROR = -9999;
    public static final int CODE_ACCESS_TOKEN_EXPIRED = -9525;
    public static final int CODE_ACCESS_TOKEN_INVALID = -9526;
    public static final int CODE_ACCESS_TOKEN_NULL = -9527;
    public static final int CODE_SUCCESS = 0;

    private final String SPLITE_VALUE = ",";
    private static HttpCode httpCode;
    private Context mContext;
    private SparseArray<String> codeMaps;

    public static HttpCode getInstance(Context context) {
//        if (httpCode == null) {
//            synchronized (HttpCode.class) {
//                if (httpCode == null) {
//                    httpCode = new HttpCode(context);
//                }
//            }
//        }
//        return httpCode;
        return new HttpCode(context);
    }

    public HttpCode(Context context) {
        mContext = context;
        String[] stringTips = mContext.getResources().getStringArray(R.array.http_code_tip);
        if (codeMaps != null) {
            codeMaps.clear();
        }
        codeMaps = new SparseArray<String>(stringTips.length);
        for (int i = 0; i < stringTips.length; i++) {
            String tip = stringTips[i];
            if (tip != null && !"".equals(tip)) {
                String[] tips = tip.split(SPLITE_VALUE);
                if (tips != null && tips.length == 2 && tips[0] != null) {
                    try {
                        int indexNum = Integer.parseInt(tips[0].toString().trim());
                        codeMaps.append(indexNum, tips[1]);
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
    }

    public static boolean isSuccess(int statusCode) {
        if (statusCode == CODE_SUCCESS) {
            return true;
        }
        return false;
    }

    public String getCodeString(int code) {
        if (codeMaps == null) {
            return "";
        }
        return codeMaps.get(code, "");
    }


    public void onDestory() {
        if (codeMaps != null) {
            codeMaps.clear();
            codeMaps = null;
        }
        mContext = null;
        httpCode = null;
    }
}
