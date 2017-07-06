package cn.appscomm.l38t.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.activity.base.BaseActivity;
import cn.appscomm.l38t.adapter.WelcomeAdapter;
import cn.appscomm.l38t.utils.LogUtil;

/**
 *
 */

public class WelcomeYQActivity extends BaseActivity {

    @BindView(R.id.welcome_login)
    Button btnLogin;
    @BindView(R.id.welcome_regist)
    Button btnReg;
    @BindView(R.id.welcome_dot_1)
    ImageView dot1;
    @BindView(R.id.welcome_dot_2)
    ImageView dot2;
    @BindView(R.id.welcome_dot_3)
    ImageView dot3;
    @BindView(R.id.welcome_viewpage)
    ViewPager showImg;
    private WelcomeAdapter adapter;
    private static final int CHANGE_PAGE = 10086;
    private volatile static TimerTask timerTask;
    private volatile static Timer timer;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHANGE_PAGE:
                    LogUtil.e(TAG, "收到消息了，当前页是" + showImg.getCurrentItem());
                    showImg.setCurrentItem(showImg.getCurrentItem() + 1);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {//不是每次都打开首页
            finish();
            return;
        }
        setContentView(R.layout.activity_welcome_yingqu);
        rlBar.setVisibility(View.GONE);
        btnReg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    updateBackground(false);
                }
                return false;
            }
        });
        btnLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    updateBackground(true);
                }
                return false;
            }
        });
        adapter = new WelcomeAdapter();
        LayoutInflater mLayoutInflater = getLayoutInflater();
        ArrayList<View> views = new ArrayList<>();
        views.add(mLayoutInflater.inflate(R.layout.welcome_viewpage_page1, null));
        views.add(mLayoutInflater.inflate(R.layout.welcome_viewpage_page2, null));
        views.add(mLayoutInflater.inflate(R.layout.welcome_viewpage_page3, null));
        adapter.setData(views);
        showImg.setAdapter(adapter);
        showImg.setCurrentItem(Integer.MAX_VALUE / 2);
        showImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        cancelTimer();
                        break;
                    case MotionEvent.ACTION_UP:
                        countTime();
                        break;
                }
                return WelcomeYQActivity.super.onTouchEvent(event);
            }
        });
        showImg.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int current = position % 3;
                if (current == 0) {
                    dot1.setImageResource(R.mipmap.welcome_viewpage_choose);
                    dot2.setImageResource(R.mipmap.welcome_viewpage_nochoose);
                    dot3.setImageResource(R.mipmap.welcome_viewpage_nochoose);
                } else if (current == 1) {
                    dot1.setImageResource(R.mipmap.welcome_viewpage_nochoose);
                    dot2.setImageResource(R.mipmap.welcome_viewpage_choose);
                    dot3.setImageResource(R.mipmap.welcome_viewpage_nochoose);
                } else {
                    dot1.setImageResource(R.mipmap.welcome_viewpage_nochoose);
                    dot2.setImageResource(R.mipmap.welcome_viewpage_nochoose);
                    dot3.setImageResource(R.mipmap.welcome_viewpage_choose);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        adapter = null;
        showImg = null;
        super.onDestroy();
    }

    private void setChange() {
        LogUtil.e(TAG, "发送消息了");
        Message message = Message.obtain();
        message.what = CHANGE_PAGE;
        handler.sendMessage(message);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelTimer();
    }

    private void cancelTimer() {
        timerTask.cancel();
        timer.purge();
        timerTask = null;
        timer = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        countTime();
    }

    private void countTime() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                setChange();
            }
        };
        timer.schedule(timerTask, 5000, 5000);
    }

    @OnClick({R.id.welcome_login, R.id.welcome_regist})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.welcome_login:
                startActivity(LoginActivity.class);
                finish();
                break;
            case R.id.welcome_regist:
                break;
        }
    }

    private void updateBackground(boolean isLogin) {
        if (isLogin) {
            btnLogin.setBackgroundResource(R.mipmap.welcome_green);
            btnReg.setBackgroundResource(R.mipmap.welcome_white);
            btnLogin.setTextColor(getResources().getColor(R.color.white));
            btnReg.setTextColor(getResources().getColor(R.color.header_bg_color));
        } else {
            btnLogin.setBackgroundResource(R.mipmap.welcome_white);
            btnReg.setBackgroundResource(R.mipmap.welcome_green);
            btnLogin.setTextColor(getResources().getColor(R.color.header_bg_color));
            btnReg.setTextColor(getResources().getColor(R.color.white));
        }
    }

}
