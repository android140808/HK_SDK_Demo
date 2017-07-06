package cn.appscomm.l38t.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import com.appscomm.bluetooth.interfaces.MusicCallback;
import com.appscomm.bluetooth.manage.AppsBluetoothManager;
import com.appscomm.bluetooth.protocol.command.base.BaseCommand;
import com.appscomm.bluetooth.protocol.command.other.MusicCommand;

/**
 * Created by liucheng on 2017/6/26.
 */

public class MusicUtils {
    private final Context context;
    private static MusicUtils musicUtils;
    private final AudioManager audioManager;
    private int tempVolume = -1;

    private MusicUtils(Context context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
//            MusicControlEx.INSTANCE.init();
        } else {

        }
        registerVolumeReceiver();
        MusicControl.INSTANCE.init(context, audioManager);
    }

    public static MusicUtils getInstance(Context context) {
        if (musicUtils == null) {
            synchronized (FindPhoneUtils.class) {
                if (musicUtils == null) {
                    musicUtils = new MusicUtils(context);
                }
            }
        }
        return musicUtils;
    }

    public void setVolumn(int volumn) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, getVolumn(volumn), AudioManager.FLAG_SHOW_UI);
    }

    /**
     * 由于不同的手机的最大值音量不同，而设备端返回的音量为百分制，故需要转换
     *
     * @param volumn 设备端的音量
     * @return
     */
    private int getVolumn(int volumn) {
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * volumn / 100;
    }

    /**
     * 发送到设备的音量值，即将手机的音量按比例转化为百分制
     *
     * @return 百分制的音量
     */
    public int getVolumnPercent() {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public void sendCommand(BaseCommand.CommandResultCallback commandResultCallback, byte result, byte[] data) {
        MusicCommand musicCommand = new MusicCommand(commandResultCallback, result, data);
        AppsBluetoothManager.getInstance(context).sendCommand(musicCommand);
    }

    public void check() {
        MusicControl.INSTANCE.checkMusicStatus();
    }

    public void play() {
        MusicControl.INSTANCE.playSong();
    }

    public void pause() {
        MusicControl.INSTANCE.pauseSong();
    }

    public void pre() {
        MusicControl.INSTANCE.preSong();
    }

    public void next() {
        MusicControl.INSTANCE.nextSong();
    }

    public void anaAction(int action) {
        //当值大于0时，是音量设置
        if (action >= 0 && action <= 100) {
            setVolumn(action);
        } else if (action == MusicCallback.play) {
            play();
        } else if (action == MusicCallback.pause) {
            pause();
        } else if (action == MusicCallback.pre) {
            pre();
        } else if (action == MusicCallback.next) {
            next();
        } else if (action == MusicCallback.check) {
            check();
        }
    }

    public void sendVolume(boolean isSend) {
        int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (tempVolume != streamVolume || isSend) {
            //由于在使用手机端的音量+-键时，产生了大量的重复音量的值。故做出相同音量则过滤的相同的音量。
            // 且存在有一旦更改非音乐音量也会出现音量改变广播。故需要过滤非音乐音量。
            //但是当设备查询音量大小时，无论是否与上次一致都需要发送音量
            byte sendVolumn = (byte) getVolumnPercent();
            tempVolume = streamVolume;
            //0是播放，1是暂停，2是音量
            sendCommand(null, (byte) 02, new byte[]{sendVolumn});
        }
    }

    private void registerVolumeReceiver() {
        MyVolumeReceiver mVolumeReceiver = new MyVolumeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        context.registerReceiver(mVolumeReceiver, filter);
    }

    /**
     * 处理音量变化时的界面显示
     */
    private class MyVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
                sendVolume(false);
            }
        }
    }

}
