package cn.appscomm.l38t.activity.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appscomm.bluetooth.config.BluetoothConfig;
import com.appscomm.bluetooth.manage.AppsBluetoothManager;
import com.appscomm.bluetooth.manage.GlobalVarManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.activity.LoginActivity;
import cn.appscomm.l38t.activity.TurnOnBluetoothActivity;
import cn.appscomm.l38t.app.AppManager;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.constant.APPConstant;
import cn.appscomm.l38t.constant.AppUtil;
import cn.appscomm.l38t.eventbus.GlobalEvent;
import cn.appscomm.l38t.eventbus.base.EventBusMessage;
import cn.appscomm.l38t.utils.BackgroundThread;
import cn.appscomm.l38t.utils.viewUtil.DialogWhiteUtil;
import cn.appscomm.l38t.utils.viewUtil.LoadingDialogUtil;
import cn.appscomm.l38t.utils.viewUtil.ScreenUtil;
import cn.appscomm.l38t.utils.viewUtil.ToastUtil;
import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.config.BaseLocalConfig;
import cn.appscomm.netlib.config.NetConfig;
import cn.appscomm.netlib.constant.HttpCode;
import cn.appscomm.netlib.retrofit_okhttp.RequestManager;
import cn.appscomm.netlib.retrofit_okhttp.interfaces.HttpResponstTokenListener;

/**
 * Created by Administrator on 2016/1/22.
 */
public class BaseActivity extends FragmentActivity {
    protected final String TAG = this.getClass().getSimpleName();
    protected RelativeLayout rlBar;
    protected TextView tvBarTitle;
    protected RelativeLayout llBarLeft;
    protected LinearLayout llBarRight;
    protected TextView tvBarLeft, tvBarRight;

    private Dialog bigLoadingDialog;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        EventBus.getDefault().register(this);
        initDialog();
    }

    @Override
    public void setContentView(int layoutResID) {
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_base_sub, null, false);
        FrameLayout contentContainer = (FrameLayout) rootView.findViewById(R.id.container);
        View containView = LayoutInflater.from(this).inflate(layoutResID, null);
        contentContainer.addView(containView);
        super.setContentView(rootView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initBarLayout();
        ButterKnife.bind(this);
    }

    private void initBarLayout() {
        rlBar = (RelativeLayout) findViewById(R.id.rl_bar);
        tvBarTitle = (TextView) findViewById(R.id.tv_bar_title);
        llBarLeft = (RelativeLayout) findViewById(R.id.ll_bar_left);
        llBarRight = (LinearLayout) findViewById(R.id.ll_bar_right);
        llBarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.this.onBackPressed();
            }
        });
        int height = ScreenUtil.getScreenHeight(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height / 13);
        rlBar.setLayoutParams(params);
        tvBarLeft = (TextView) findViewById(R.id.tv_bar_left);
        tvBarRight = (TextView) findViewById(R.id.tv_bar_right);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        tvBarTitle.setText(title + "");
    }

    private void initDialog() {
        bigLoadingDialog = LoadingDialogUtil.createBigLoadingDialog(this, getString(R.string.loading));
        bigLoadingDialog.setCancelable(false);
        bigLoadingDialog.setCanceledOnTouchOutside(false);
    }

    public void dismissLoadingDialog() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bigLoadingDialog != null && bigLoadingDialog.isShowing()) {
                        BackgroundThread.removeTask(autoDismissDialogRunnable);
                        bigLoadingDialog.dismiss();
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    private Runnable autoDismissDialogRunnable=new Runnable() {
        @Override
        public void run() {
            dismissLoadingDialog();
        }
    };


    public void showBigLoadingProgress(final String msg) {
        try {
            if (!BaseActivity.this.isFinishing() && bigLoadingDialog != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tvMessage = (TextView) bigLoadingDialog.findViewById(R.id.tv_message);
                        if (tvMessage != null && !TextUtils.isEmpty(msg)) {
                            tvMessage.setText(msg + "");
                        }
                        if (!BaseActivity.this.isFinishing() && !bigLoadingDialog.isShowing()) {
                            bigLoadingDialog.show();
                            BackgroundThread.postDelayed(autoDismissDialogRunnable,45*1000);
                        }
                    }
                });
            }
        } catch (Exception e) {
        }
    }

    public void showBigLoadingProgress(final String msg, final DialogInterface.OnKeyListener keyListener) {
        try {
            if (!BaseActivity.this.isFinishing() && bigLoadingDialog != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tvMessage = (TextView) bigLoadingDialog.findViewById(R.id.tv_message);
                        if (tvMessage != null && !TextUtils.isEmpty(msg)) {
                            tvMessage.setText(msg + "");
                        }
                        bigLoadingDialog.setOnKeyListener(keyListener);
                        if (!BaseActivity.this.isFinishing() && !bigLoadingDialog.isShowing()) {
                            bigLoadingDialog.show();
                            BackgroundThread.postDelayed(autoDismissDialogRunnable,45*1000);
                        }
                    }
                });
            }
        } catch (Exception e) {
        }
    }

    public void showToast(final String tip) {
        ToastUtil.showToast(BaseActivity.this, tip);
    }

    private HttpResponstTokenListener tokenListener=new HttpResponstTokenListener() {
        @Override
        public void onInvalidToken(BasePostBean postBean) {
            showReloginDialog(HttpCode.CODE_ACCESS_TOKEN_INVALID);
        }

        @Override
        public void onExpiredToken(BasePostBean postBean) {
            showReloginDialog(HttpCode.CODE_ACCESS_TOKEN_EXPIRED);
        }

        @Override
        public void onNullToken(BasePostBean postBean) {
            showReloginDialog(HttpCode.CODE_ACCESS_TOKEN_NULL);
        }
    };


    private void showReloginDialog(int code){
        String msg= HttpCode.getInstance(BaseActivity.this).getCodeString(code);
        showTipDialog(msg, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                login_out();
            }
        });
    }

    public void dismissDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        });
    }

    public void showTipDialog(final String tip) {
        if (!BaseActivity.this.isFinishing() && !BaseActivity.this.isDestroyed()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissLoadingDialog();
                    dismissDialog();
                    dialog = DialogWhiteUtil.createDialogPositive(BaseActivity.this, tip);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            });
        }
    }

    public void showNoTipDialog(final String tip) {
        if (!BaseActivity.this.isFinishing() && !BaseActivity.this.isDestroyed()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissLoadingDialog();
                    dismissDialog();
                    dialog = DialogWhiteUtil.createMiddleDialogPositive(BaseActivity.this, tip);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            });
        }
    }

    public void showTipDialog(final String tip, final View.OnClickListener onClickListener) {
        if (!BaseActivity.this.isFinishing() && !BaseActivity.this.isDestroyed()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissLoadingDialog();
                    dismissDialog();
                    dialog = DialogWhiteUtil.createDialogPositive(BaseActivity.this, tip, onClickListener);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            });
        }
    }

    public void showTipDialogCancleAndPositive(final String tip, final View.OnClickListener pClickListener) {
        if (!BaseActivity.this.isFinishing() && !BaseActivity.this.isDestroyed()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissLoadingDialog();
                    dismissDialog();
                    dialog = DialogWhiteUtil.createDialogCancleAndPositive(BaseActivity.this, tip, pClickListener);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            });
        }
    }

    public void showTipDialogCancleAndPositive(final String tip, final View.OnClickListener pClickListener, final View.OnClickListener nClickListener) {
        if (!BaseActivity.this.isFinishing() && !BaseActivity.this.isDestroyed()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissLoadingDialog();
                    dismissDialog();
                    dialog = DialogWhiteUtil.createDialogCancleAndPositive(BaseActivity.this, tip, pClickListener, nClickListener);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            });
        }
    }

    protected void login_out() {
        delInfo();
        BaseLocalConfig.getInstance().saveBoolean(APPConstant.QUERY_SETTING, false);
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).justUnbindDevice(BluetoothConfig.getDefaultMac(BaseActivity.this));
        Intent mIntent = new Intent(this, LoginActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mIntent);
        finish();
    }

    private void delInfo() {
        NetConfig.setAccessToken("");
        // 需求，不需要去掉密码。
        // AccountConfig.setUserLoginPassword("");
        AccountConfig.setFriendsAccountInfo(null);
        BluetoothConfig.setDefaultMac(GlobalApp.getAppContext(), "");
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).disConnectDevice(false);
        sendMessage(GlobalEvent.EVENBUS_SIGNAL_CODE_DEVICE_DISCONNECTED);
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).onDestroy();
        GlobalVarManager.getInstance().setSportSleepMode(0);
        GlobalVarManager.getInstance().onDestory();
    }


    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void doEventBusMessageAsync(EventBusMessage message) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doEventBusMessageMain(EventBusMessage message) {
    }

    public void sendMessage(int code) {
        GlobalEvent globalEvent = new GlobalEvent();
        globalEvent.setCode(code);
        EventBus.getDefault().post(globalEvent);
    }

    public void startActivity(Class<?> activity) {
        Intent it = new Intent(BaseActivity.this, activity);
        startActivity(it);
    }

    public void startActivity(Class<?> activity, Bundle bundle) {
        Intent it = new Intent(BaseActivity.this, activity);
        if (bundle != null) {
            it.putExtras(bundle);
        }
        startActivity(it);
    }

    public boolean checkBindDevice(boolean autoJump) {
        boolean falg = AppUtil.haveBindDevice();
        if (!falg && autoJump) {
            //已经绑定设备
            startActivity(TurnOnBluetoothActivity.class);
        }
        return falg;
    }

    @Override
    protected void onResume() {
        super.onResume();
        RequestManager.getInstance().registerTokenListener(tokenListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RequestManager.getInstance().unregisterTokenListener(tokenListener);
    }

    @Override
    protected void onDestroy() {
        BackgroundThread.removeTask(autoDismissDialogRunnable);
        dismissLoadingDialog();
        EventBus.getDefault().unregister(this);
        AppManager.getAppManager().finishActivity(this);
        super.onDestroy();
    }

    @Override
    public void finish() {
        AppManager.getAppManager().removeActivity(this);
        super.finish();
    }


}
