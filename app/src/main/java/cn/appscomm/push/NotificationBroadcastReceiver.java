package cn.appscomm.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import cn.appscomm.l38t.utils.LogUtil;
import cn.appscomm.push.config.PushConfig;
import cn.appscomm.push.constant.PushConstant;


/**
 * Created by weiliu on 2016/7/21.
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {

    private static String TAG = NotificationBroadcastReceiver.class.getSimpleName();
    private static int mailMsgCount = 0; // 邮件数
    private static int calMsgCount = 0; // 日历提醒数
    private static int socMsgCount = 0; // 社交媒体数
    private static int gmailCount, outlookcount, qqmailCount, androidCalCount, gCalCount, qqLiteCount, qqCount, facebCount, twitterCount, lineCount,
            whatAppCount, weixiCount, skypeVerizonCount, skypePolarisCount, skypeRoverCount, instCount;// 各app的消息数
    private HandleNotification handleNotification;

    @Override
    public void onReceive(Context context, Intent intent) {
        String pkgName = intent.getStringExtra("packageName");
        String sbnContent = intent.getStringExtra("sbnContent");
        if (intent.getAction().equals(NotificationObserver.NEW_NOTIFICATION)) {
            handleNewNotification(pkgName, sbnContent);
        } else if (intent.getAction().equals(NotificationObserver.DEL_NOTIFICATION)) {
            handleDelNotification(pkgName);
        } else if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
            if (handleNotification != null) {
                handleNotification.handleLocalChange(intent);
            }
        }
    }

    public interface HandleNotification {
        void handleMail(String name, String type, int count, String content);

        void handleSchedule(int count, String content);

        void handleSocial(String name, String type, int count, String content);

        void handleLocalChange(Intent intent);
    }

    public void setHandleNotification(HandleNotification handleNotification) {
        this.handleNotification = handleNotification;
    }

    private void handleDelNotification(String pkgName) {
        LogUtil.i(TAG, "handleDelNotification,pkgName=" + pkgName);
        if (pkgName == null)
            return;
        if (pkgName.toLowerCase().contains(PushConstant.GMAIL_PKG)) {
            gmailCount = 0; /* setSendIndex(9); */
        } else if (pkgName.contains(PushConstant.OUTLOOK_PKG)) {
            outlookcount = 0; /* setSendIndex(9); */
        } else if (pkgName.toLowerCase().contains(PushConstant.QQMAIL_PKG)) {
            qqmailCount = 0; /* setSendIndex(9); */
        } else if ((pkgName.toLowerCase().contains(PushConstant.SCHEDULE_PKG)) ||
                (pkgName.toLowerCase().contains(PushConstant.SCHEDULE_BBK_PKG)) ||
                (pkgName.toLowerCase().contains(PushConstant.SCHEDULE_LENOVO_PKG)) ||
                (pkgName.toLowerCase().contains(PushConstant.SCHEDULE_HTC_PKG))) {
            androidCalCount = 0; /* setSendIndex(10); */
        } else if (pkgName.toLowerCase().contains(PushConstant.GMAIL_SCHEDULE_PKG)) {
            gCalCount = 0; /* setSendIndex(10); */
        } else if (pkgName.toLowerCase().contains(PushConstant.QQLIST_PKG)) {
            qqLiteCount = 0; /* setSendIndex(11); */
        } else if (pkgName.toLowerCase().contains(PushConstant.MOBILEQQ_PKG)) {
            qqCount = 0; /* setSendIndex(11); */
        } else if ((pkgName.toLowerCase().contains(PushConstant.FACEBOOK_PKG)) || (pkgName.toLowerCase().contains(PushConstant.FBMSG_PKG))) {
            facebCount = 0; /* setSendIndex(11); */
        } else if (pkgName.toLowerCase().contains(PushConstant.TWITTER_PKG)) {
            twitterCount = 0; /* setSendIndex(11); */
        } else if (pkgName.toLowerCase().contains(PushConstant.WHATSAPP_PKG)) {
            whatAppCount = 0; /* setSendIndex(11); */
        } else if (pkgName.toLowerCase().contains(PushConstant.MM_PKG)) {
            weixiCount = 0; /* setSendIndex(11); */
        } else if (pkgName.toLowerCase().contains(PushConstant.LINE_PKG)) {
            lineCount = 0; /* setSendIndex(11); */
        } else if (pkgName.toLowerCase().contains(PushConstant.SKYPE_VERIZON_PKG)) {
            skypeVerizonCount = 0; /* setSendIndex(11); */
        } else if (pkgName.toLowerCase().contains(PushConstant.SKYPE_POLARIS_PKG)) {
            skypePolarisCount = 0;
        } else if (pkgName.toLowerCase().contains(PushConstant.SKYPE_ROVER_PKG)) {
            skypeRoverCount = 0; /* setSendIndex(11); */
        } else if (pkgName.toLowerCase().contains(PushConstant.INSTAGRAM_PKG)) {
            instCount = 0; /* setSendIndex(11); */
        }
    }

    private void handleNewNotification(String pkgName, String content) {
        LogUtil.i(TAG, "handleNewNotification,pkgName=" + pkgName);
        LogUtil.i(TAG, "handleNewNotification,sbnContent=" + content);
        if (TextUtils.isEmpty(pkgName))
            return;
        String name = "";
        String type = "";
        boolean mail = false;
        boolean schedule = false;
        boolean mm = false;
        if (pkgName.toLowerCase().contains(PushConstant.GMAIL_PKG)) {
            gmailCount++;
            name = PushConstant.GMAIL_NAME;
            type = PushConstant.GMAIL_PKG;
            mail = true;
        } else if (pkgName.contains(PushConstant.OUTLOOK_PKG)) {
            outlookcount++;
            name = PushConstant.OUTLOOK_NAME;
            type = PushConstant.OUTLOOK_PKG;
            mail = true;
        } else if (pkgName.toLowerCase().contains(PushConstant.QQMAIL_PKG)) {
            qqmailCount++;
            mail = true;
            name = PushConstant.QQMAIL_NAME;
            type = PushConstant.QQMAIL_PKG;
        } else if ((pkgName.toLowerCase().contains(PushConstant.SCHEDULE_PKG)) ||
                (pkgName.toLowerCase().contains(PushConstant.SCHEDULE_BBK_PKG)) ||
                (pkgName.toLowerCase().contains(PushConstant.SCHEDULE_LENOVO_PKG)) ||
                (pkgName.toLowerCase().contains(PushConstant.SCHEDULE_HTC_PKG))) {
            androidCalCount++;
            schedule = true;
            name = PushConstant.SCHEDULE_NAME;
            type = PushConstant.SCHEDULE_PKG;
        } else if (pkgName.toLowerCase().contains(PushConstant.GMAIL_SCHEDULE_PKG)) {
            gCalCount++;
            schedule = true;
            name = PushConstant.GMAIL_SCHEDULE_NAME;
            type = PushConstant.GMAIL_SCHEDULE_PKG;
        } else if (pkgName.toLowerCase().contains(PushConstant.QQLIST_PKG)) {
            qqLiteCount++;
            mm = true;
            name = PushConstant.QQLIST_NAME;
            type = PushConstant.QQLIST_PKG;
        } else if (pkgName.toLowerCase().contains(PushConstant.MOBILEQQ_PKG)) {
            qqCount++;
            mm = true;
            name = PushConstant.MOBILEQQ_NAME;
            type = PushConstant.MOBILEQQ_PKG;
        } else if ((pkgName.toLowerCase().contains(PushConstant.FACEBOOK_PKG))) {
            facebCount++;
            mm = true;
            name = PushConstant.FACEBOOK_NAME;
            type = PushConstant.FACEBOOK_PKG;
        } else if ((pkgName.toLowerCase().contains(PushConstant.FBMSG_PKG))) {
            facebCount++;
            mm = true;
            name = PushConstant.FBMSG_NAME;
            type = PushConstant.FBMSG_PKG;

        } else if (pkgName.toLowerCase().contains(PushConstant.TWITTER_PKG)) {
            twitterCount++;
            mm = true;
            name = PushConstant.TWITTER_NAME;
            type = PushConstant.TWITTER_PKG;
        } else if (pkgName.toLowerCase().contains(PushConstant.WHATSAPP_PKG)) {
            whatAppCount++;
            mm = true;
            name = PushConstant.WHATSAPP_NAME;
            type = PushConstant.WHATSAPP_PKG;
        } else if (pkgName.toLowerCase().contains(PushConstant.MM_PKG)) {
            weixiCount++;
            mm = true;
            name = PushConstant.MM_NAME;
            type = PushConstant.MM_PKG;
        } else if (pkgName.toLowerCase().contains(PushConstant.LINE_PKG)) {
            lineCount++;
            mm = true;
            name = PushConstant.LINE_NAME;
            type = PushConstant.LINE_PKG;
        } else if (pkgName.toLowerCase().contains(PushConstant.SKYPE_VERIZON_PKG)) {
            skypeVerizonCount++;
            mm = true;
            name = PushConstant.SKYPE_VERIZON_NAME;
            type = PushConstant.SKYPE_VERIZON_PKG;
        } else if (pkgName.toLowerCase().contains(PushConstant.SKYPE_RAIDER_PKG)) {
            skypeVerizonCount++;
            mm = true;
            name = PushConstant.SKYPE_RAIDER_NAME;
            type = PushConstant.SKYPE_RAIDER_PKG;
        } else if (pkgName.toLowerCase().contains(PushConstant.SKYPE_POLARIS_PKG)) {
            skypePolarisCount++;
            mm = true;
            name = PushConstant.SKYPE_POLARIS_NAME;
            type = PushConstant.SKYPE_POLARIS_PKG;
        } else if (pkgName.toLowerCase().contains(PushConstant.SKYPE_ROVER_PKG)) {
            skypeRoverCount++;
            mm = true;
            name = PushConstant.SKYPE_ROVER_NAME;
            type = PushConstant.SKYPE_ROVER_PKG;
        } else if (pkgName.toLowerCase().contains(PushConstant.INSTAGRAM_PKG)) {
            instCount++;
            mm = true;
            name = PushConstant.INSTAGRAM_NAME;
            type = PushConstant.INSTAGRAM_PKG;
        }
        calMsgCount();
        checkToSendDevice(name, type, mail, schedule, mm, content);
    }

    private void checkToSendDevice(String name, String type, boolean mail, boolean schedule, boolean mm, String content) {
        if (mail && PushConfig.getEnableMail()) {
            handleNotification.handleMail(name, type, mailMsgCount, content);
        }
        if (schedule && PushConfig.getEnableSchedule()) {
            handleNotification.handleSchedule(calMsgCount, content);
        }
        if (mm && PushConfig.getEnableMm()) {
            handleNotification.handleSocial(name, type, socMsgCount, content);
        }
    }

    private void calMsgCount() {
        mailMsgCount = gmailCount + outlookcount + qqmailCount;
        calMsgCount = androidCalCount + gCalCount;
        socMsgCount = qqLiteCount + qqCount + facebCount + twitterCount + lineCount + whatAppCount + weixiCount + skypeVerizonCount + skypePolarisCount + skypeRoverCount + instCount;
    }
}
