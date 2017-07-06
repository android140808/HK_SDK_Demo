package cn.appscomm.l38t.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.utils.BackgroundThread;
import cn.appscomm.netlib.bean.account.UserInfoBean;
import cn.appscomm.netlib.config.NetConfig;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        delayStart();
    }

    private Runnable delayStartRunnable = new Runnable() {
        @Override
        public void run() {
            String accessToken= NetConfig.getAccessToken();
            UserInfoBean localUser=AccountConfig.getUserInfoBean();
            if (!TextUtils.isEmpty(accessToken)&&localUser!=null&&!TextUtils.isEmpty(localUser.getUserName())){
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(SplashActivity.this, WelcomeYQActivity.class);
                startActivity(intent);
            }
            finish();
        }
    };

    private void delayStart() {
        BackgroundThread.postDelayed(delayStartRunnable, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BackgroundThread.removeTask(delayStartRunnable);
        delayStartRunnable = null;
    }

    // 此画面不能按返回键来中断
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
