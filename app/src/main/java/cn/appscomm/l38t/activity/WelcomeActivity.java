package cn.appscomm.l38t.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.activity.base.BaseActivity;

/**
 * Created by Administrator on 2016/8/30.
 */
public class WelcomeActivity extends BaseActivity {


    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_reg)
    Button btnReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {//不是每次都打开首页
            finish();
            return;
        }
        setContentView(R.layout.activity_welcome);
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
    }

    @OnClick({R.id.btn_login, R.id.btn_reg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                startActivity(LoginActivity.class);
                finish();
                break;
            case R.id.btn_reg:
                break;
        }
    }

    private void updateBackground(boolean isLogin) {
        if (isLogin) {
            btnLogin.setBackgroundResource(R.mipmap.login_bg_select);
            btnReg.setBackgroundResource(R.mipmap.reg_bg_unselect);
            btnLogin.setTextColor(getResources().getColor(R.color.white));
            btnReg.setTextColor(getResources().getColor(R.color.header_bg_color));
        } else {
            btnLogin.setBackgroundResource(R.mipmap.login_bg_unselect);
            btnReg.setBackgroundResource(R.mipmap.reg_bg_select);
            btnLogin.setTextColor(getResources().getColor(R.color.header_bg_color));
            btnReg.setTextColor(getResources().getColor(R.color.white));
        }
    }
}
