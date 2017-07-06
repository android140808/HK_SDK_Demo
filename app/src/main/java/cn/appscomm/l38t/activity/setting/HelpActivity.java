package cn.appscomm.l38t.activity.setting;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.appscomm.bluetooth.manage.AppsBluetoothManager;
import com.appscomm.bluetooth.manage.GlobalVarManager;
import com.appscomm.bluetooth.protocol.command.base.BaseCommand;
import com.appscomm.bluetooth.protocol.command.bind.BindStart;
import com.appscomm.bluetooth.protocol.command.device.DeviceVersion;
import com.appscomm.bluetooth.protocol.command.device.UpgradeMode;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.activity.base.BaseActivity;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.config.AppConfig;
import cn.appscomm.l38t.constant.APPConstant;
import cn.appscomm.l38t.constant.AppUtil;
import cn.appscomm.l38t.eventbus.GlobalEvent;
import cn.appscomm.l38t.model.database.UserBindDevice;
import cn.appscomm.l38t.utils.ChooseDevices;
import cn.appscomm.l38t.utils.FileUtil;
import cn.appscomm.l38t.utils.LogUtil;
import cn.appscomm.l38t.utils.ParseUtil;
import cn.appscomm.netlib.bean.base.BaseObtainBean;
import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.bean.device.QueryProductVersion;
import cn.appscomm.netlib.bean.device.QueryProductVersionObtain;
import cn.appscomm.netlib.constant.HttpCode;
import cn.appscomm.netlib.constant.NetLibConstant;
import cn.appscomm.netlib.retrofit_okhttp.RequestManager;
import cn.appscomm.netlib.retrofit_okhttp.interfaces.HttpFileDownloadListener;
import cn.appscomm.netlib.retrofit_okhttp.interfaces.HttpResponseListener;
import cn.appscomm.netlib.util.NetworkUtil;
import no.nordicsemi.android.ota.activity.DfuActivity2;
import no.nordicsemi.android.ota.service.DfuService;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by Bingo on 2016/10/19.
 */

public class HelpActivity extends BaseActivity {
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_firmware_version)
    TextView tvFirmwareVersion;
    @BindView(R.id.tv_watch_id)
    TextView tvWatchId;
    @BindView(R.id.app_version)
    TextView appVersion;
    @BindView(R.id.firmware_version)
    TextView firmwareVersion;
    @BindView(R.id.watch_id)
    TextView watchId;
    private String localVersion;
    private boolean isClick = false;

    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);
        setTitle(getString(R.string.help));
        init();
    }

    private void init() {
        getAppVersion();
        getDeviceVersionInfo();
        // 获取固件信息
        UserBindDevice userBindDevice = UserBindDevice.getBindDevice(AccountConfig.getUserId());
        if (userBindDevice != null) {
            String temp = userBindDevice.getDeviceVersion().replace("N", "");
            localVersion = getVersion(temp);
            tvWatchId.setText(userBindDevice.getDeviceId() + "");
            tvFirmwareVersion.setText(localVersion + "");
        } else {
//            tvWatchId.setText(getString(R.string.unknown));
//            tvFirmwareVersion.setText(getString(R.string.unknown));
            tvWatchId.setText("");
            tvFirmwareVersion.setText("");
        }
    }

    private String getVersion(String temp) {
        temp = temp.replace("N", "");
        int kIndex = temp.indexOf("K");
        if (kIndex != -1) {
            temp = temp.substring(0, kIndex);
        }
        return temp;
    }

    /**
     * 获取app版本
     */
    public void getAppVersion() {
        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
            String versionName = pinfo.versionName;
            tvVersion.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.rl_firmware_up)
    public void onClick() {
        if (System.currentTimeMillis() - lastClickTime < 800) {
            return;
        }
        lastClickTime = System.currentTimeMillis();
        if (!NetworkUtil.isNetworkConnected(GlobalApp.getAppContext())) {
            showToast(getString(R.string.http_network_failed));
            return;
        }
        if (!AppUtil.checkBluetooth(HelpActivity.this)) {
            checkBindDevice(true);
            return;
        }
        dismissDialog();
        showBigLoadingProgress(getString(R.string.loading));
        isClick = true;
        getDeviceVersionInfo();
//        procHttpUpdate();
    }

    BaseCommand.CommandResultCallback iResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onFail(BaseCommand baseCommand) {
            isClick = false;
            dismissLoadingDialog();
            if ((baseCommand instanceof DeviceVersion)) {
                showTipDialogCancleAndPositive(getString(R.string.device_version_get_failed), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDeviceVersionInfo();
                    }
                });
            }
        }

        @Override
        public void onSuccess(BaseCommand baseCommand) {
            if ((baseCommand instanceof BindStart)) {
                dismissLoadingDialog();
                if (GlobalVarManager.getInstance().getUserUID() != AccountConfig.getUserId()) {
                    showToast(getString(R.string.userUID_wrong));
                    return;
                }
                procHttpUpdate();
            } else if (baseCommand instanceof DeviceVersion) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtils.isEmpty(GlobalVarManager.getInstance().getSoftVersion())) {
                            AppConfig.getInstance().saveString(APPConstant.HARDWARE_VERSION, GlobalVarManager.getInstance().getHardwareVersion());
                        } else {
                            localVersion = getVersion(GlobalVarManager.getInstance().getSoftVersion());
                            tvFirmwareVersion.setText(localVersion);
                            if (isClick) {
                                isClick = false;
                                procHttpUpdate();
                            } else {
                                dismissLoadingDialog();
                            }
                        }
                    }
                });
            } else if ((baseCommand instanceof UpgradeMode)) {
                AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).killCommandRunnable();
                showToast(getString(R.string.set_failed));
            }
        }
    };

    /**
     * 获取固件版本信息
     */
    private void getDeviceVersionInfo() {
        if (!AppUtil.checkBluetooth(HelpActivity.this)) {
            checkBindDevice(true);
            return;
        }
        dismissDialog();
        showBigLoadingProgress(getString(R.string.loading));
        DeviceVersion hardwareVersion = new DeviceVersion(iResultCallback, DeviceVersion.DeviceHardwareVersion);
        DeviceVersion deviceVersion = new DeviceVersion(iResultCallback, DeviceVersion.DeviceSoftVersion);
        ArrayList<BaseCommand> commands = new ArrayList<>();
        commands.add(hardwareVersion);
        commands.add(deviceVersion);
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommands(commands);
    }

    /**
     *
     */
    public synchronized void procHttpUpdate() {
        UserBindDevice userBindDevice = UserBindDevice.getBindDevice(AccountConfig.getUserId());
        if (userBindDevice != null) {
            String hardwareVersion = AppConfig.getInstance().getString(APPConstant.HARDWARE_VERSION, "");
            final QueryProductVersion queryProductVersion = new QueryProductVersion();
            // 设置OTA升级参数
            if (!TextUtils.isEmpty(hardwareVersion)) {
                if (hardwareVersion.startsWith("WT10")) {//原有的her
                    queryProductVersion.setProductCode(AppUtil.getDeviceType(userBindDevice.getDeviceName()));
                } else if (hardwareVersion.startsWith("L42A")) {
                    queryProductVersion.setProductCode(AppUtil.getDeviceType(userBindDevice.getDeviceName()));
                } else {//薛磊部门的her
                    queryProductVersion.setProductCode(ChooseDevices.HER_NEW);
                    queryProductVersion.setCustomerCode(NetLibConstant.DEFAULT_CUSTOMER_CODE_HER_NEW);
                }
            }
            RequestManager.getInstance().queryProductVersion(queryProductVersion, new HttpResponseListener() {
                @Override
                public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
                    QueryProductVersionObtain queryProductVersionObtain = (QueryProductVersionObtain) baseObtainBean;
                    if (queryProductVersionObtain.getCrmProductVersion() == null) {
                        dismissLoadingDialog();
                        showToast(getString(R.string.last_updated));
                        return;
                    }
                    String version = queryProductVersionObtain.getCrmProductVersion().getDeviceVersion();
                    if (!TextUtils.isEmpty(version) && version.length() >= 5) {
                        version = version.substring(1, 4);
                    }
                    LogUtil.e(TAG, "version=" + version + "/localVersion=" + localVersion);
//                    if (version.compareTo(localVersion) > 0) {
                    if (true) {
//                        String fileUrl = queryProductVersionObtain.getCrmProductVersion().getUpdateUrl();
                        String fileUrl = "https://new.fashioncomm.com/download/WT10A_Her_F2.0.zip";
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
    }

    private void gotoCmrUpgrade(final String fileUrl, final String nowVersion) {
        showTipDialogCancleAndPositive(getString(R.string.updateFw_tips, nowVersion), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                showBigLoadingProgress(getString(R.string.loading));
                httpDownloadFile(fileUrl);
            }
        });
    }

    private void setDeviceUpgradeMode(final String fileUrl, final String nowVersion) {
        UpgradeMode upgradeMode;
        if (AppUtil.getShowName().equalsIgnoreCase(ChooseDevices.YOUNG)) {
            upgradeMode = new UpgradeMode(getiResultCallback(fileUrl, nowVersion), UpgradeMode.Upgrade_OTA, getAddress());
        } else {
            upgradeMode = new UpgradeMode(getiResultCallback(fileUrl, nowVersion), UpgradeMode.Upgrade_OTA);
        }
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommand(upgradeMode);
    }

    private byte[] getAddress() {
        byte[] address = new byte[15];
        byte[] kl17 = FileUtil.readFile(getFilesDir() + "/kl17.bin");
        if (kl17 != null && kl17.length > 5) {
            System.arraycopy(kl17, 0, address, 0, 5);
        }
        byte[] touch = FileUtil.readFile(getFilesDir() + "/TouchPanel.bin");
        if (touch != null && touch.length > 5) {
            System.arraycopy(touch, 0, address, 5, 5);
        }
        byte[] heartRate = FileUtil.readFile(getFilesDir() + "/Heartrate.bin");
        if (heartRate != null && heartRate.length > 5) {
            System.arraycopy(heartRate, 0, address, 10, 5);
        }
        return address;
    }

    @NonNull
    private BaseCommand.CommandResultCallback getiResultCallback(final String fileUrl, final String nowVersion) {
        return new BaseCommand.CommandResultCallback() {
            @Override
            public void onSuccess(BaseCommand leaf) {
                goToDfu(fileUrl, nowVersion);
            }

            @Override
            public void onFail(BaseCommand leaf) {
                dismissLoadingDialog();
                AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).killCommandRunnable();
                showToast(getString(R.string.set_failed));
            }
        };
    }

    private void goToDfu(String fileUrl, String nowVersion) {
        dismissLoadingDialog();
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).disConnectDevice(false);
        sendMessage(GlobalEvent.EVENBUS_SIGNAL_CODE_DEVICE_DISCONNECTED);
        UserBindDevice userBindDevice = UserBindDevice.getBindDevice(AccountConfig.getUserId());
        Intent intent = new Intent(HelpActivity.this, DfuActivity2.class);
        intent.putExtra("OTA_NAME", AppUtil.getDeviceOTA_Name(userBindDevice.getDeviceName()));
        intent.putExtra("dfuName", AppUtil.getDeviceOTA_Name(userBindDevice.getDeviceName()));
        LogUtil.i(TAG, userBindDevice.getDeviceName() + "----OTA_NAME--" + AppUtil.getDeviceOTA_Name(userBindDevice.getDeviceName()));
        intent.putExtra("url", fileUrl);
        intent.putExtra("version", nowVersion);
        startActivity(intent);
    }

    private int mFileType = DfuService.TYPE_AUTO;
    private String mFilePath;

    private void httpDownloadFile(String fileUrl) {
        RequestManager.getInstance().downloadFile(fileUrl, "zip", new HttpFileDownloadListener() {
            @Override
            public void onDownloadSuccess(final String fileUrl, final String fileFullPathName, MediaType contentType) {
                if (contentType.toString().equals(DfuService.MIME_TYPE_ZIP)) {//当前仅仅支持zip包升级
                    Uri uri = new Uri.Builder().encodedPath(fileFullPathName).build();
                    mFilePath = uri.getPath();
                    final String extension = mFileType == DfuService.TYPE_AUTO ? "(?i)ZIP" : "(?i)HEX|BIN"; // (?i) =  case insensitive
                    final boolean status = MimeTypeMap.getFileExtensionFromUrl(fileFullPathName).matches(extension);
                    if (status) {
                        LogUtil.i(TAG, "下载固件成功(" + fileFullPathName + ") 现在进行解压");
                        if (ParseUtil.unZip(fileFullPathName)) {
                            File[] files = APPConstant.FILE_FIRMWARE.listFiles();
                            for (File file : files) {
                                LogUtil.i(TAG, "file name : " + file.getName());
                            }
                            setDeviceUpgradeMode(fileUrl, "");
                        }
                    }
                }
            }

            @Override
            public void onDownloadBegin(Call call, MediaType contentType, long total) {
                if (contentType.toString().equals(DfuService.MIME_TYPE_ZIP)) {
//                    setBeginDownloadFileLayoutStatus(total);//当前仅仅支持zip包升级
                } else {
                    if (!call.isCanceled()) {
                        call.cancel();
                    }
                }
            }

            @Override
            public void onDownloading(Call call, MediaType contentType, long total, int progress) {
            }

            @Override
            public void onDownloadError(Call call, String fileUrl, Throwable e) {
                if (!call.isCanceled()) {
                    call.cancel();
                }
            }
        });
    }
}
