package cn.appscomm.push;

import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.appscomm.l38t.utils.LogUtil;
import cn.appscomm.push.constant.PushConstant;

/**
 * Created by weiliu on 2016/7/21.
 */
public class NotificationObserver extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();
    public final static String NEW_NOTIFICATION = "com.appscomm.new_notification";
    public final static String DEL_NOTIFICATION = "com.appscomm.del_notification";


    private List<MsgInOutTime> msgList;
    private Timer checkTimer;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        LogUtil.i(TAG, "onNotificationPosted");
        if (sbn == null || TextUtils.isEmpty(sbn.getPackageName())) {
            return;
        }
        boolean needSendNotif = false;
        String sbnContent = "";
        String packageName = sbn.getPackageName();
        try {
            LogUtil.i(TAG, "ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + packageName);
            sbnContent = sbn.getNotification().tickerText.toString();
        } catch (Exception e) {
        }
        String addPackage = packageName;
        for (MsgInOutTime msg1 : msgList) {
            if (msg1.packageName.equals(addPackage)) {
                needSendNotif = true;
                if ((System.currentTimeMillis() - msg1.addTime) > 500) {
                    msg1.addTime = System.currentTimeMillis();
                } else {
                    msg1.addTime = System.currentTimeMillis();
                    return;
                }
            }
        }
        if (!needSendNotif)
            return;
        Intent i = new Intent(NEW_NOTIFICATION);
        i.putExtra("packageName", packageName);
        i.putExtra("sbnContent", sbnContent);
        sendBroadcast(i);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        LogUtil.i(TAG, "onNOtificationRemoved");
        if (sbn == null || TextUtils.isEmpty(sbn.getPackageName())) {
            return;
        }
        String packageName = sbn.getPackageName();
        try {
            LogUtil.i(TAG, "ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + packageName);
        } catch (Exception e) {
        }
        try {
            boolean needSendNotif = false;
            String removePkg = packageName;
            for (MsgInOutTime msg1 : msgList) {
                if (msg1.packageName.equals(removePkg)) {
                    needSendNotif = true;
                    msg1.removTime = System.currentTimeMillis();
                }
            }
//            if (needSendNotif) {
//                Intent i = new Intent(DEL_NOTIFICATION);
//                i.putExtra("packageName", removePkg);
//                sendBroadcast(i);
//            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "NotificationObserver onCreate");
        msgList = new ArrayList<MsgInOutTime>();

        msgList.add(new MsgInOutTime(PushConstant.GMAIL_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.ANDROID_EMAIL_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.MAIL139_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.WPSMAIL_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.NETMAIL_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.SINAMAIL_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.YAHOOMAIL_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.OUTLOOK_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.QQMAIL_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.SCHEDULE_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.GMAIL_SCHEDULE_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.SCHEDULE_HTC_PKG, 0, 0)); // HTC的日历
        msgList.add(new MsgInOutTime(PushConstant.SCHEDULE_LENOVO_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.SCHEDULE_BBK_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.QQLIST_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.MOBILEQQ_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.FACEBOOK_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.FBMSG_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.TWITTER_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.WHATSAPP_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.MM_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.LINE_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.SKYPE_RAIDER_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.SKYPE_VERIZON_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.SKYPE_POLARIS_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.SKYPE_ROVER_PKG, 0, 0));
        msgList.add(new MsgInOutTime(PushConstant.INSTAGRAM_PKG, 0, 0));

        checkTimer = new Timer();
        checkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (MsgInOutTime msg1 : msgList) {
                    if (msg1.removTime > msg1.addTime) {
                        msg1.timeOutCount++;
                        if (msg1.timeOutCount > 2) {
                            Intent intent = new Intent(DEL_NOTIFICATION);
                            intent.putExtra("packageName", msg1.packageName);
                            sendBroadcast(intent);
                            msg1.timeOutCount = 0;
                            msg1.removTime = 0;
                        }
                    } else {
                        msg1.timeOutCount = 0;
                    }
                }
            }
        }, 1000, 500);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        checkTimer.cancel();
        checkTimer = null;
    }

    class MsgInOutTime {
        public MsgInOutTime(String packageName, long addTime, long removTime) {
            super();
            this.packageName = packageName;
            this.addTime = addTime;
            this.removTime = removTime;
        }

        public String packageName = "";
        public long addTime = 0;
        public long removTime = 0;
        public int timeOutCount = 0;

    }
}
