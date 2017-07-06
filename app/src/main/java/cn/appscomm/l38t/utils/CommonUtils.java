package cn.appscomm.l38t.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/2 10:40
 */
public class CommonUtils {
    public static boolean emailFormat(String email) {
        final String pattern1 ="^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        final Pattern pattern = Pattern.compile(pattern1);
        final Matcher mat = pattern.matcher(email);
        return mat.matches();
    }


    public static Pattern getPhonePattern() {
        // Pattern pattern = Pattern.compile("^(13[0-9]|15[0|1|3|6|7|8|9]|18[8|9])\\d{8}$");
        Pattern pattern = Pattern.compile("\\d{11}");
        return pattern;
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    //截取浮点型数据，小数点一位，不需要四舍五入
    public static String getFormatOneData(String data) {
        String str = "";

        if (data.contains(".")) {
            int start = data.indexOf(".");
            try {
                str = data.substring(0, start + 2);
            } catch (Exception e) {
                str = "0";
            }
        }
        return str;
    }

}
