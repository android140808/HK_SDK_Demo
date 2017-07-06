//package cn.appscomm.l38t.utils;
//
//import android.annotation.TargetApi;
//import android.content.ComponentName;
//import android.media.AudioManager;
//import android.media.RemoteController;
//import android.media.session.MediaController;
//import android.media.session.MediaSessionManager;
//import android.os.Build;
//import android.os.SystemClock;
//import android.util.Log;
//import android.view.KeyEvent;
//
//import java.util.List;
//
//import cn.appscomm.presenter.PresenterAppContext;
//import cn.appscomm.presenter.remotecontrol.RemoteControlManager;
//import cn.appscomm.presenter.server.NotificationReceiveService;
//
//import static android.content.Context.AUDIO_SERVICE;
//import static android.content.Context.MEDIA_SESSION_SERVICE;
//
///**
// * 作者：hsh
// * 日期：2017/4/26
// * 说明：音乐控制(仅支持5.0及以上的大部分音乐播放器)
// */
//@TargetApi(Build.VERSION_CODES.LOLLIPOP)
//public enum MusicControlEx {
//    INSTANCE;
//
//    private ComponentName componentName;
//    private MediaSessionManager mediaSessionManager;
//    private RemoteController remoteController;
//    private AudioManager mAudioManager;
//
//    public void init() {
//        componentName = new ComponentName(PresenterAppContext.INSTANCE.getContext(), NotificationReceiveService.class);
//        mediaSessionManager = (MediaSessionManager) PresenterAppContext.INSTANCE.getContext().getSystemService(MEDIA_SESSION_SERVICE);
//
//        mAudioManager = (AudioManager) PresenterAppContext.INSTANCE.getContext().getSystemService(AUDIO_SERVICE);
//    }
//
//    public boolean checkMusicState() {
//        return mAudioManager.isMusicActive();
//    }
//
//    public boolean sendMusicKeyEvent(int keyCode) {
//        Log.i("mytest", "remoteController = " + (remoteController != null));
//        if (remoteController != null) {
//            // send "down" and "up" key events.
//            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
//            boolean down = remoteController.sendMediaKeyEvent(keyEvent);
//            keyEvent = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
//            boolean up = remoteController.sendMediaKeyEvent(keyEvent);
//            return down && up;
//        } else {
//            long eventTime = SystemClock.uptimeMillis();
//            KeyEvent key = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0);
//            dispatchMediaKeyToAudioService(key);
//            dispatchMediaKeyToAudioService(KeyEvent.changeAction(key, KeyEvent.ACTION_UP));
//        }
//        return false;
//    }
//
//    private void dispatchMediaKeyToAudioService(KeyEvent event) {
//        AudioManager audioManager = (AudioManager) PresenterAppContext.INSTANCE.getContext().getSystemService(AUDIO_SERVICE);
//        if (audioManager != null) {
//            try {
//                audioManager.dispatchMediaKeyEvent(event);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * 开始播放
//     */
//    public void playSong() {
//        List<MediaController> controllers = mediaSessionManager.getActiveSessions(componentName);
//        for (MediaController m : controllers) {
//            Log.i("mytest", "播放 包名 : " + m.getPackageName() + " 状态 : " + (m.getPlaybackState() != null));
//            if (m.getPlaybackState() != null) {
//                MediaController.TransportControls transportControls = m.getTransportControls();
//                transportControls.play();
//                RemoteControlManager.INSTANCE.sendPlayPause(true);
//            }
//        }
//    }
//
//    /**
//     * 暂停播放
//     */
//    public void pauseSong() {
//        List<MediaController> controllers = mediaSessionManager.getActiveSessions(componentName);
//        for (MediaController m : controllers) {
//            Log.i("mytest", "暂停 包名 : " + m.getPackageName() + " 状态 : " + (m.getPlaybackState() != null));
//            if (m.getPlaybackState() != null) {
//                MediaController.TransportControls transportControls = m.getTransportControls();
//                transportControls.pause();
//                RemoteControlManager.INSTANCE.sendPlayPause(false);
//            }
//        }
//    }
//
//    /**
//     * 下一曲
//     */
//    public void nextSong() {
//        List<MediaController> controllers = mediaSessionManager.getActiveSessions(componentName);
//        for (MediaController m : controllers) {
//            Log.i("mytest", "下一首 包名 : " + m.getPackageName() + " 状态 : " + (m.getPlaybackState() != null));
//            MediaController.PlaybackInfo ttt = m.getPlaybackInfo();
//
//            if (m.getPlaybackState() != null) {
//                MediaController.TransportControls transportControls = m.getTransportControls();
//                transportControls.skipToNext();
//                RemoteControlManager.INSTANCE.updateSongName();
//            }
//        }
//    }
//
//    /**
//     * 上一曲
//     */
//    public void preSong() {
//        List<MediaController> controllers = mediaSessionManager.getActiveSessions(componentName);
//        for (MediaController m : controllers) {
//            Log.i("mytest", "上一首 包名 : " + m.getPackageName() + " 状态 : " + (m.getPlaybackState() != null));
//            if (m.getPlaybackState() != null) {
//                MediaController.TransportControls transportControls = m.getTransportControls();
//                transportControls.skipToPrevious();
//                RemoteControlManager.INSTANCE.updateSongName();
//            }
//        }
//    }
//}
