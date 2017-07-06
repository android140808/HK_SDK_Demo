package cn.appscomm.push;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.push.config.PushConfig;
import cn.appscomm.push.util.PushUtil;


/**
 * Created by weiliu on 2016/7/21.
 */
public class SMSContentObserver extends ContentObserver {

    private String TAG = this.getClass().getSimpleName();
    private int totalSMSNum = 0;
    private SmsContentHandler smsContentHandler;
    private Context context;

    public interface SmsContentHandler {
        void handleNewSmsContent(Msginfo msginfo);
    }

    public SMSContentObserver() {
        super(new Handler());
        this.context = GlobalApp.getAppContext();
        totalSMSNum = PushUtil.UpdateTotalSMS(context);
    }

    public void setSmsContentHandler(SmsContentHandler smsContentHandler) {
        this.smsContentHandler = smsContentHandler;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        int curTotalSMS = PushUtil.UpdateTotalSMS(context);
        if (curTotalSMS <= totalSMSNum) {
            totalSMSNum = curTotalSMS;
            return;
        } else {
            totalSMSNum = curTotalSMS;
        }

        if (PushConfig.getEnableSms()) {
            //则将新短信放入队列
            Msginfo msginfo = getSmsFromPhone();
            if (msginfo == null) {
                return;
            } else {
                smsContentHandler.handleNewSmsContent(msginfo);
            }
        }
    }

    /**
     * 取消注册数据库监听
     */
    public void unregisterObserver() {
        GlobalApp.getAppContext().getContentResolver().unregisterContentObserver(this);
    }

    /**
     * 注册数据库监听
     */
    public void registerObserver() {
        GlobalApp.getAppContext().getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, this);
    }

    private Uri SMS_INBOX = Uri.parse("content://sms/inbox");

    public Msginfo getSmsFromPhone() {
        try {
            Msginfo msg1 = new Msginfo();
            ContentResolver cr = context.getContentResolver();
            String[] projection = new String[]{"_id", "address", "person", "body", "date"};// "_id",
//            String where = " type = 1 and read = 0 and  date >  " + (System.currentTimeMillis() - 30 * 1000);
            String where = " type = 1 and read = 0";
            Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
            if (null == cur) {
                return null;
            }
            if (cur.moveToNext()) {
                String _id = cur.getString(cur.getColumnIndex("_id"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String number = cur.getString(cur.getColumnIndex("address"));// 手机号
                String name = "1";
                name = PhoneCallListener.getContactNameByNumber(context, number);
                String body = cur.getString(cur.getColumnIndex("body"));
                String time = sdf.format(new Date(cur.getLong((cur.getColumnIndex("date")))));
                msg1._id = _id;
                msg1.name = name;
                msg1.phoneNo = number;
                msg1.content = body;
                msg1.recvTimeMs = cur.getLong((cur.getColumnIndex("date")));
                msg1.contentindex = 1;
                msg1.nameindex = 1;
                if (name == null) {
                    msg1.namePacketCount = 1;
                } else {
                    if (name.getBytes().length > 57)
                        msg1.namePacketCount = 2;
                    else
                        msg1.namePacketCount = 1;
                }
                if (body == null) {
                    msg1.contentPacketCount = 1;
                } else {
                    if (body.getBytes().length > 57)
                        msg1.contentPacketCount = 2;
                    else
                        msg1.contentPacketCount = 1;
                }
                return msg1;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    class Msginfo {
        public String _id;
        public String name;
        public int nameindex = 1;
        public int namePacketCount = 1;
        public String phoneNo;
        public String content;
        public int contentindex = 1;
        public int contentPacketCount = 1;
        public long recvTimeMs;


        // 把时间戳（秒），转换为日期格式：2014-0-04-16 13:25
        private String timeStamp2format(long time_stamp) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String reTime = df.format(time_stamp);
            return reTime;
        }

        @Override
        public String toString() {
            return "name: " + name + ", phoneNo: " + phoneNo + ", content: " + content
                    + ", recvTimeMs; " + recvTimeMs + "(" + timeStamp2format(recvTimeMs) + ")";
        }

    }
}
