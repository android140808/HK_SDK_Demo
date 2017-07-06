package cn.appscomm.l38t.utils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import java.util.List;

/**
 * Created by liucheng on 2017/6/26.
 */

public class FindPhoneUtils {
    private static volatile FindPhoneUtils findPhoneUtils;
    private static Context context;
    private int alarmType = -1;
    private boolean isPlayRingFlag = false;
    private MediaPlayer mediaPlayer;

    private FindPhoneUtils(Context context) {
        this.context = context;
    }

    public static FindPhoneUtils getInstance(Context context) {
        if (findPhoneUtils == null) {
            synchronized (FindPhoneUtils.class) {
                if (findPhoneUtils == null) {
                    findPhoneUtils = new FindPhoneUtils(context);
                }
            }
        }
        return findPhoneUtils;
    }

    public void startVibratorAlarm() {
        try {
            moveTaskToFront();
            Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(1500);
            alarmType = RingtoneManager.TYPE_ALARM;
            playAlarm();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止震动和铃声
     */
    public void stopVibratorAlarm() {
        isPlayRingFlag = false;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void moveTaskToFront() {
        //获取ActivityManager
        ActivityManager mAm = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //获得当前运行的task
        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo rti : taskList) {
            //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
            if (rti.topActivity.getPackageName().equals(context.getPackageName())) {
                mAm.moveTaskToFront(rti.id, 0);
                return;
            }
        }
    }

    private void playAlarm() {
        try {
            if (!isPlayRingFlag && alarmType != -1) { // 如果不在播放铃声，则播放
                Uri uri = RingtoneManager.getActualDefaultRingtoneUri(context, alarmType);
                mediaPlayer = MediaPlayer.create(context, uri);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
                isPlayRingFlag = true;
            }
        } catch (Exception e) {
            if (alarmType == RingtoneManager.TYPE_ALARM) {
                alarmType = RingtoneManager.TYPE_RINGTONE;
                playAlarm();
            } else {
                e.printStackTrace();
            }
        }
    }

}
