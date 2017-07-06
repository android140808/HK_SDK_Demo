package cn.appscomm.l38t.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appscomm.bluetooth.config.BluetoothConfig;
import com.appscomm.bluetooth.manage.AppsBluetoothManager;

import butterknife.BindView;
import butterknife.OnClick;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.activity.base.BaseActivity;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.constant.AppUtil;
import cn.appscomm.l38t.model.database.UserBindDevice;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/5 13:53
 */
public class SetUpDeviceActivity extends BaseActivity {

    @BindView(R.id.tv_device_status)
    TextView tvDeviceStatus;
    @BindView(R.id.rl_device_reset)
    RelativeLayout rl_device_reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_device);
        setTitle(getString(R.string.title_device));
        init();
    }

    private void showReset() {
        if (UserBindDevice.getBindDevice(AccountConfig.getUserId())==null){
            rl_device_reset.setVisibility(View.GONE);
        }else {
            rl_device_reset.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        showReset();
        showBind();
    }

    private void showBind() {
        if (AppUtil.haveBindDevice()) {
            tvDeviceStatus.setText(getString(R.string.unbinddevice));
        } else {
            tvDeviceStatus.setText(getString(R.string.binddevice));
        }
    }

    @OnClick({R.id.rl_unbind, R.id.rl_device_info, R.id.rl_device_reset})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_unbind:
                String nowTxt = tvDeviceStatus.getText().toString();
                if (nowTxt != null && nowTxt.equals(getString(R.string.unbinddevice))) {
                    if (AppUtil.haveBindDevice()) {
                        startActivity(UnbindDeviceActivity.class);
                    } else {
                        showToast(getString(R.string.no_bind_device));
                    }
                } else if (nowTxt != null && nowTxt.equals(getString(R.string.binddevice))) {
                    startActivity(TurnOnBluetoothActivity.class);
                    finish();
                }
                break;
            case R.id.rl_device_info:
                if (AppUtil.haveBindDevice()) {
                    startActivity(DeviceInfoActivity.class);
                } else {
                    showToast(getString(R.string.no_bind_device));
                }
                break;
            case R.id.rl_device_reset:
                // 检查设备是否给绑定
//                if (checkBindDevice(true)) {
//                    showTipDialogCancleAndPositive(getString(R.string.reset_device_context), new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dismissDialog();
//                            showBigLoadingProgress(getString(R.string.setting));
//                            AppsBluetoothManager.getInstance(GlobalApp.getAppContext())
//                                    .sendCommand(new RestoreFactory(new BaseCommand.CommandResultCallback() {
//                                        @Override
//                                        public void onSuccess(BaseCommand command) {
//                                            dismissLoadingDialog();
//                                            // 重置设备成功,需要重新绑定
//                                            doUnbind();
//                                        }
//
//                                        @Override
//                                        public void onFail(BaseCommand command) {
//                                            dismissLoadingDialog();
//                                            showToast(getString(R.string.set_failed));
//                                        }
//                                    }));
//                        }
//                    });
//                }
                break;
        }
    }

    /**
     * 解绑
     */
    private void doUnbind() {
        UserBindDevice.deleteByUserId(AccountConfig.getUserId());
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).doUnbindDevice(BluetoothConfig.getDefaultMac(SetUpDeviceActivity.this));
        BluetoothConfig.setDefaultMac(GlobalApp.getAppContext(), "");
        onBackPressed();

        startActivity(new Intent(SetUpDeviceActivity.this, DeviceChooseActivity.class));
        finish();
    }
}
