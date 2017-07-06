package cn.appscomm.push.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Administrator on 2016/8/31.
 */
public class PushUtil {
    public static int UpdateTotalSMS(Context context) {
        Cursor cur = null;
        int SMS_NUM = 0;

        try {
            try {
                cur = context.getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);
                if (null != cur) {
                    SMS_NUM = cur.getCount();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cur != null) {
                    cur.close();
                }
            }
            return SMS_NUM;

        } catch (Exception e) {
            return SMS_NUM;
        }
    }
}
