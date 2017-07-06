package cn.appscomm.push;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;

import com.appscomm.bluetooth.manage.AppsBluetoothManager;
import com.appscomm.bluetooth.protocol.command.base.BaseCommand;
import com.appscomm.bluetooth.protocol.command.push.MsgCountPush;
import com.appscomm.bluetooth.protocol.command.push.PhoneNamePush;
import com.appscomm.bluetooth.protocol.command.push.SmsPush;
import com.appscomm.bluetooth.protocol.command.push.SocialPush;
import com.appscomm.bluetooth.utils.DateUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.utils.BackgroundThread;
import cn.appscomm.l38t.utils.LogUtil;
import cn.appscomm.push.config.PushConfig;
import cn.appscomm.push.constant.PushConstant;


/**
 * Created by weiliu on 2016/7/21.
 */
public class AppsCommPushService extends Service {

    private final String TAG = this.getClass().getSimpleName();
    private boolean mInit = false;
    private boolean mbRegister = false;
    private int reSendCountTimes = 0;
    private PhoneCallListener phoneCallListener;
    private SMSContentObserver smsContentObserver;
    private NotificationBroadcastReceiver notificationBroadcastReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public static void startService() {
        Intent intent = new Intent(GlobalApp.getAppContext(), AppsCommPushService.class);
        GlobalApp.getAppContext().stopService(intent);
        GlobalApp.getAppContext().startService(intent);
    }

    public static void stopService() {
        Intent intent = new Intent(GlobalApp.getAppContext(), AppsCommPushService.class);
        GlobalApp.getAppContext().stopService(intent);
    }


    private void init() {
        if (mInit) return;
        mInit = true;
        PushConfig.setHangUpCall(true);//默认挂断电话必须发
        phoneCallListener = new PhoneCallListener(this);
        phoneCallListener.registerToTelephonyManager();
        phoneCallListener.setPhoneCallHandler(phoneCallHandler);
        smsContentObserver = new SMSContentObserver();
        smsContentObserver.setSmsContentHandler(smsContentHandler);
        smsContentObserver.registerObserver();
        notificationBroadcastReceiver = new NotificationBroadcastReceiver();
        notificationBroadcastReceiver.setHandleNotification(handleNotification);
        registerBroadcast();
    }

    private NotificationBroadcastReceiver.HandleNotification handleNotification = new NotificationBroadcastReceiver.HandleNotification() {
        @Override
        public void handleMail(String name, String type, int count, String content) {
            sendSocialCommands("mail", content, formatToDeviceDate(new Date()), MsgCountPush.EMAIL_TYPE, (byte) count);
        }

        @Override
        public void handleSchedule(int count, String content) {
            sendSocialCommands("Schedule", content, formatToDeviceDate(new Date()), MsgCountPush.SCHEDULE_TYPE, (byte) count);
        }

        @Override
        public void handleSocial(String name, String type, int count, String content) {
            byte msgType = checkTypeWithName(name);
            sendSocialCommands(name, content, formatToDeviceDate(new Date()), msgType, (byte) count);
        }

        @Override
        public void handleLocalChange(Intent intent) {
            Locale locale = getResources().getConfiguration().locale;
            String language = locale.getLanguage();
        }
    };

    private byte checkTypeWithName(String name) {
        byte type = 0x02;
        if (PushConstant.isDeviceHaveScreen) {
            switch (name) {
                case PushConstant.MM_NAME:
                    type = 0x07;
                    break;
                case PushConstant.VIBER_NAME:
                    type = 0x08;
                    break;
                case PushConstant.SNAPCHAT_NAME:
                    type = 0x09;
                    break;
                case PushConstant.WHATSAPP_NAME:
                    type = 0x0a;
                    break;
                case PushConstant.MOBILEQQ_NAME:
                    type = 0x0b;
                    break;
                case PushConstant.FACEBOOK_NAME:
                    type = 0x0c;
                    break;
                case PushConstant.HANGOUTS_NAME:
                    type = 0x0d;
                    break;
                case PushConstant.GMAIL_NAME:
                    type = 0x0e;
                    break;
                case PushConstant.FBMSG_NAME:
                    type = 0x0f;
                    break;
                case PushConstant.INSTAGRAM_NAME:
                    type = 0x10;
                    break;
                case PushConstant.TWITTER_NAME:
                    type = 0x11;
                    break;
                case PushConstant.LINKEDIN_NAME:
                    type = 0x12;
                    break;
                case PushConstant.UBER_NAME:
                    type = 0x13;
                    break;
                case PushConstant.LINE_NAME:
                    type = 0x14;
                    break;
                case PushConstant.SKYPE_POLARIS_NAME:
                case PushConstant.SKYPE_RAIDER_NAME:
                case PushConstant.SKYPE_ROVER_NAME:
                case PushConstant.SKYPE_VERIZON_NAME:
                    type = 0x15;
                    break;
            }
        }


        return type;
    }

    private static String formatToDeviceDate(Date date) {
        String dateString = DateUtil.dateToStr(date, "yyyyMMdd-HHmmss");
        dateString = dateString.replaceAll("-", "T");
        return dateString;
    }

    private SMSContentObserver.SmsContentHandler smsContentHandler = new SMSContentObserver.SmsContentHandler() {
        @Override
        public void handleNewSmsContent(SMSContentObserver.Msginfo msginfo) {
            if (msginfo != null) {
                sendSmsCommands(msginfo.name, msginfo.content, formatToDeviceDate(DateUtil.timeStampToDate(msginfo.recvTimeMs)), MsgCountPush.SMS_MSG_TYPE, msginfo.contentPacketCount);
            }
        }
    };

    private PhoneCallListener.PhoneCallHandler phoneCallHandler = new PhoneCallListener.PhoneCallHandler() {
        @Override
        public void handleMisCall(String name, String incomingNumber) {
            sendPhoneCallCommands(name, incomingNumber, PhoneNamePush.Mis_Call_type, MsgCountPush.MIS_CALL_TYPE, 1000 * 1);
        }

        @Override
        public void handleIncomingCall(String name, String incomingNumber) {
            sendPhoneCallCommands(name, incomingNumber, PhoneNamePush.Incoming_Call_type, MsgCountPush.ICOMING_CALL_TYPE, 0);
        }

        @Override
        public void handleHangUpCall(String incomingNumber) {
            sendPhoneCallCommands("", incomingNumber, PhoneNamePush.HangUp_Call_type, MsgCountPush.HANGUP_CALL_TYPE, 0);
        }
    };

    private void sendSocialCommands(String from, String content, String date, byte countType, int count) {
        SocialPush pushName = null, pushContent = null, pushDate = null;
        ArrayList<BaseCommand> sendList = new ArrayList<>();
        if (PushConstant.isDeviceHaveScreen) {
            try {
                if (!TextUtils.isEmpty(from)) {
                    byte[] bName = from.getBytes("utf-8");
                    pushName = new SocialPush(iResultCallback, SocialPush.NAME_TYPE, bName);
                }
                if (!TextUtils.isEmpty(content)) {
                    byte[] bContent = content.getBytes("utf-8");
                    pushContent = new SocialPush(iResultCallback, SocialPush.CONTENT_TYPE, bContent);
                }
                if (!TextUtils.isEmpty(date)) {
                    byte[] bDate = date.getBytes("utf-8");
                    pushDate = new SocialPush(iResultCallback, SocialPush.DATE_TYPE, bDate);
                }
            } catch (UnsupportedEncodingException e) {
            }
            if (pushName != null) {
                sendList.add(pushName);
            }
            if (pushContent != null) {
                sendList.add(pushContent);
            }
            if (pushDate != null) {
                sendList.add(pushDate);
            }
        }
        MsgCountPush countPush = new MsgCountPush(iResultCallback, countType, (byte) count);
        sendList.add(countPush);
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommands(sendList);
    }

    private void sendSmsCommands(String from, String content, String date, byte countType, int count) {
        SmsPush smsPushName = null, smsPushContent = null, smsPushDate = null;
        try {
            if (!TextUtils.isEmpty(from)) {
                byte[] bName = from.getBytes("utf-8");
                smsPushName = new SmsPush(iResultCallback, SmsPush.SMS_NAME_TYPE, bName);
            }
            if (!TextUtils.isEmpty(content)) {
                byte[] bContent = content.getBytes("utf-8");
                smsPushContent = new SmsPush(iResultCallback, SmsPush.SMS_CONTENT_TYPE, bContent);
            }
            if (!TextUtils.isEmpty(date)) {
                byte[] bDate = date.getBytes("utf-8");
                smsPushDate = new SmsPush(iResultCallback, SmsPush.SMS_DATE_TYPE, bDate);
            }
        } catch (UnsupportedEncodingException e) {
        }
        MsgCountPush countPush = new MsgCountPush(iResultCallback, countType, (byte) count);
        ArrayList<BaseCommand> sendList = new ArrayList<>();
        LogUtil.i(TAG, "smsPushName=" + smsPushName + "smsPushContent=" + smsPushContent + "smsPushDate=" + smsPushDate);
        if (PushConstant.isDeviceHaveScreen) {
            if (smsPushName != null) {
                sendList.add(smsPushName);
            }
            if (smsPushContent != null) {
                sendList.add(smsPushContent);
            }
            if (smsPushDate != null) {
                sendList.add(smsPushDate);
            }
        }
        sendList.add(countPush);
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommands(sendList);
    }

    private void sendPhoneCallCommands(String name, String incomingNumber, byte callType, byte countType, long delayTime) {
        byte[] bName = new byte[0x00];
        if (!TextUtils.isEmpty(incomingNumber)) {
            bName = incomingNumber.getBytes();
            if (!TextUtils.isEmpty(name)) {
                bName = name.getBytes();
            }
        }
        PhoneNamePush phonePush = new PhoneNamePush(iResultCallback, callType, bName);
        MsgCountPush countPush = new MsgCountPush(iResultCallback, countType, (byte) 0x01);
        ArrayList<BaseCommand> sendList = new ArrayList<>();
        if (PushConstant.isDeviceHaveScreen) {
            sendList.add(phonePush);
            sendList.add(countPush);
        } else {
            sendList.add(countPush);
        }
        if (delayTime > 0) {
            final ArrayList<BaseCommand> commands = sendList;
            BackgroundThread.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommands(commands);
                }
            }, delayTime);
        } else {
            AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommands(sendList);
        }
    }

    BaseCommand.CommandResultCallback iResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand leaf) {
            LogUtil.i(TAG, leaf.getClass().getSimpleName() + " Push,iResultCallback = onSuccess");
        }

        @Override
        public void onFail(BaseCommand leaf) {
            if (leaf instanceof MsgCountPush) {
                if (((MsgCountPush) leaf).getMsgType() == MsgCountPush.HANGUP_CALL_TYPE && reSendCountTimes <= 2) {
                    LogUtil.i(TAG, TAG + " Push,挂断电话 重发" + reSendCountTimes);
                    MsgCountPush phoneCountPush = new MsgCountPush(iResultCallback, MsgCountPush.HANGUP_CALL_TYPE, (byte) 0x01);
                    AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommand(phoneCountPush);
                    reSendCountTimes++;
                } else {
                    reSendCountTimes = 0;
                }
            } else {
                AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).killCommandRunnable();
            }
            LogUtil.i(TAG, leaf.getClass().getSimpleName() + " Push,iResultCallback = onFail");
        }
    };

    private void registerBroadcast() {
        if (!mbRegister) {
            IntentFilter intent1 = new IntentFilter();
            intent1.addAction(NotificationObserver.NEW_NOTIFICATION);
            intent1.addAction(NotificationObserver.DEL_NOTIFICATION);
            intent1.addAction(Intent.ACTION_LOCALE_CHANGED);
            registerReceiver(notificationBroadcastReceiver, intent1);
            mbRegister = true;
        }
    }

    private void unregisterBroadcast() {
        unregisterReceiver(notificationBroadcastReceiver);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mbRegister) {
            unregisterBroadcast();
        }
        smsContentObserver.unregisterObserver();
        smsContentObserver = null;
        phoneCallListener.unregisterFromTelephonyManager();
        phoneCallListener = null;
    }
}
