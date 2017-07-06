package cn.appscomm.l38t.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.appscomm.bluetooth.manage.AppsBluetoothManager;
import com.appscomm.bluetooth.manage.GlobalVarManager;
import com.appscomm.bluetooth.protocol.command.base.BaseCommand;
import com.appscomm.bluetooth.protocol.command.bind.BindStart;
import com.appscomm.bluetooth.protocol.command.device.DeviceVersion;
import com.appscomm.bluetooth.protocol.command.device.UpgradeMode;

import butterknife.BindView;
import butterknife.OnClick;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.activity.base.BaseActivity;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.constant.AppUtil;
import cn.appscomm.l38t.eventbus.GlobalEvent;
import cn.appscomm.l38t.model.database.UserBindDevice;
import cn.appscomm.netlib.bean.base.BaseObtainBean;
import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.bean.device.QueryProductVersion;
import cn.appscomm.netlib.bean.device.QueryProductVersionObtain;
import cn.appscomm.netlib.constant.HttpCode;
import cn.appscomm.netlib.retrofit_okhttp.RequestManager;
import cn.appscomm.netlib.retrofit_okhttp.interfaces.HttpResponseListener;
import cn.appscomm.netlib.util.NetworkUtil;
import no.nordicsemi.android.ota.activity.DfuActivity;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/5 14:00
 */
public class DeviceInfoActivity extends BaseActivity {

    @BindView(R.id.tv_imi)
    TextView tvImi;
    @BindView(R.id.tv_firmware)
    TextView tvFirmware;

    private String localVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        setTitle(getString(R.string.title_device_info));
        init();
    }

    private void init() {
        UserBindDevice userBindDevice = UserBindDevice.getBindDevice(AccountConfig.getUserId());
        if (userBindDevice != null) {
            localVersion = userBindDevice.getDeviceVersion();
            tvImi.setText(userBindDevice.getDeviceId() + "");
            tvFirmware.setText(localVersion + "");
        } else {
            tvImi.setText(getString(R.string.unknown));
            tvFirmware.setText(getString(R.string.unknown));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDeviceVersionInfo();
    }

    private void getDeviceVersionInfo() {
        if (!AppUtil.checkBluetooth(DeviceInfoActivity.this)) {
            return;
        }
        dismissDialog();
        showBigLoadingProgress(getString(R.string.loading));
        DeviceVersion deviceVersion = new DeviceVersion(iResultCallback, DeviceVersion.DeviceSoftVersion);
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommand(deviceVersion);
    }

    private long lastClickTime = 0;

    @OnClick(R.id.ll_update)
    public void onClick() {
        if (System.currentTimeMillis() - lastClickTime < 800) {
            return;
        }
        lastClickTime = System.currentTimeMillis();
        if (!NetworkUtil.isNetworkConnected(GlobalApp.getAppContext())) {
            showToast(getString(R.string.http_network_failed));
            return;
        }
        if (!AppUtil.checkBluetooth(DeviceInfoActivity.this)) {
            return;
        }
        dismissDialog();
        showBigLoadingProgress(getString(R.string.loading));
        BindStart bindStart = new BindStart(iResultCallback);
        com.appscomm.bluetooth.manage.AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommand(bindStart);
    }


    BaseCommand.CommandResultCallback iResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onFail(BaseCommand baseCommand) {
            dismissLoadingDialog();
            if ((baseCommand instanceof DeviceVersion)) {
                showTipDialogCancleAndPositive(getString(R.string.device_version_get_failed), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialog();
                        getDeviceVersionInfo();
                    }
                });
            }
        }

        @Override
        public void onSuccess(BaseCommand baseCommand) {
            dismissLoadingDialog();
            if ((baseCommand instanceof BindStart)) {
                if (GlobalVarManager.getInstance().getUserUID() != AccountConfig.getUserId()) {
                    showToast(getString(R.string.userUID_wrong));
                    return;
                }
                procHttpUpdate();
            } else if (baseCommand instanceof DeviceVersion) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        localVersion = GlobalVarManager.getInstance().getSoftVersion();
                        tvFirmware.setText(localVersion);
                    }
                });
            }
        }
    };

    public void procHttpUpdate() {
        final QueryProductVersion queryProductVersion = new QueryProductVersion();
        UserBindDevice userBindDevice = UserBindDevice.getBindDevice(AccountConfig.getUserId());
        if (userBindDevice != null) {
            queryProductVersion.setProductCode(userBindDevice.getDeviceType());
        }
        RequestManager.getInstance().queryProductVersion(queryProductVersion, new HttpResponseListener() {
            @Override
            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
                QueryProductVersionObtain queryProductVersionObtain = (QueryProductVersionObtain) baseObtainBean;
                if (queryProductVersionObtain.getCrmProductVersion() == null) {
                    showToast(getString(R.string.last_updated));
                    return;
                }
                String version = queryProductVersionObtain.getCrmProductVersion().getDeviceVersion();
                if (Float.parseFloat(version) > Float.parseFloat(localVersion)) {
                    String fileUrl = queryProductVersionObtain.getCrmProductVersion().getUpdateUrl();
                    if (!TextUtils.isEmpty(fileUrl)) {
                        gotoCmrUpgrade(fileUrl, version);
                    } else {
                        dismissLoadingDialog();
                        showToast(getString(R.string.invalid_update_url));
                    }
                } else {
                    dismissLoadingDialog();
                    showToast(getString(R.string.last_updated));
                }
            }

            @Override
            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {
                dismissLoadingDialog();
                showToast(HttpCode.getInstance(GlobalApp.getAppContext()).getCodeString(statusCode));
            }
        });
    }

    private void gotoCmrUpgrade(final String fileUrl, final String nowVersion) {
        showTipDialogCancleAndPositive(getString(R.string.updateFw_tips, nowVersion), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                setDeviceUpgradeMode(fileUrl, nowVersion);
            }
        });
    }

    private void setDeviceUpgradeMode(final String fileUrl, final String nowVersion) {
        UpgradeMode upgradeMode = new UpgradeMode(new BaseCommand.CommandResultCallback() {
            @Override
            public void onSuccess(BaseCommand leaf) {
                dismissLoadingDialog();
                AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).disConnectDevice(false);
                sendMessage(GlobalEvent.EVENBUS_SIGNAL_CODE_DEVICE_DISCONNECTED);
                UserBindDevice userBindDevice = UserBindDevice.getBindDevice(AccountConfig.getUserId());
                Intent intent = new Intent(DeviceInfoActivity.this, DfuActivity.class);
                intent.putExtra("OTA_NAME", AppUtil.getDeviceOTA_Name(userBindDevice.getDeviceName()));
                intent.putExtra("url", fileUrl);
                //intent.putExtra("url", "http://new.fashioncomm.com/download/F1.3_LF02A.zip");
                intent.putExtra("version", nowVersion);
                startActivity(intent);
            }

            @Override
            public void onFail(BaseCommand leaf) {
                dismissLoadingDialog();
                AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).killCommandRunnable();
                showToast(getString(R.string.set_failed));
            }
        }, UpgradeMode.Upgrade_OTA);
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommand(upgradeMode);
    }
}
