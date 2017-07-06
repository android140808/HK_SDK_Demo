package no.nordicsemi.android.ota.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appscomm.bluetooth.bean.BluetoothScanDevice;
import com.appscomm.bluetooth.config.BluetoothConfig;
import com.appscomm.bluetooth.interfaces.BluetoothManagerScanListener;
import com.appscomm.bluetooth.manage.AppsBluetoothManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.utils.viewUtil.DialogWhiteUtil;
import cn.appscomm.netlib.retrofit_okhttp.RequestManager;
import cn.appscomm.netlib.retrofit_okhttp.interfaces.HttpFileDownloadListener;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;
import no.nordicsemi.android.ota.service.DfuService;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/8 18:00
 */
public class DfuActivity extends Activity {

    private final String TAG = this.getClass().getSimpleName();

    public static final int SUCCESSFUL = 0;
    public static final int FAILED = 1;

    @BindView(R.id.tv_main_bar_title)
    TextView tvMainBarTitle;
    @BindView(R.id.iv_back)
    ImageView  ivBack;
    @BindView(R.id.tv_edit)
    TextView tvEdit;
    @BindView(R.id.iv_update)
    ImageView ivUpdate;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.ll_update)
    LinearLayout llUpdate;
    @BindView(R.id.iv_result)
    ImageView ivResult;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.ll_result)
    LinearLayout llResult;
    @BindView(R.id.btn_result)
    Button btnResult;

    private Dialog dialog;
    private String fileUrl;
    private String version;
    private String mOTA_NAME;

    private String deviceMacAddress;
    private int mFileType = DfuService.TYPE_AUTO;
    private String mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ota);
        ButterKnife.bind(this);
        initView();
        init();
    }

    private void initView() {
        tvMainBarTitle.setText(getString(R.string.title_update));
        ivBack.setVisibility(View.GONE);
        tvEdit.setVisibility(View.GONE);
    }

    private void init() {
        fileUrl = getIntent().getStringExtra("url");
        version = getIntent().getStringExtra("version");
        mOTA_NAME = getIntent().getStringExtra("OTA_NAME");
        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
        httpDownloadFile(fileUrl);
    }

    private void httpDownloadFile(String fileUrl) {
        if (isDfuServiceRunning()) {
            stopDfu();
        }
        setDownloadingFileLayoutStatus(true, 0);
        RequestManager.getInstance().downloadFile(fileUrl, "zip", new HttpFileDownloadListener() {
            @Override
            public void onDownloadSuccess(String fileUrl, String fileFullPathName, MediaType contentType) {
                if (contentType.toString().equals(DfuService.MIME_TYPE_ZIP)) {//当前仅仅支持zip包升级
                    Uri uri = new Uri.Builder().encodedPath(fileFullPathName).build();
                    mFilePath = uri.getPath();
                    final String extension = mFileType == DfuService.TYPE_AUTO ? "(?i)ZIP" : "(?i)HEX|BIN"; // (?i) =  case insensitive
                    final boolean status = MimeTypeMap.getFileExtensionFromUrl(fileFullPathName).matches(extension);
                    if (status) {
                        startDfu();
                    } else {
                        setResultLayoutStatus(FAILED);
                    }
                } else {
                    setResultLayoutStatus(FAILED);
                }
            }

            @Override
            public void onDownloadBegin(Call call, MediaType contentType, long total) {
                if (contentType.toString().equals(DfuService.MIME_TYPE_ZIP)) {
                    setBeginDownloadFileLayoutStatus(total);//当前仅仅支持zip包升级
                } else {
                    if (!call.isCanceled()) {
                        call.cancel();
                    }
                    setResultLayoutStatus(FAILED);
                }
            }

            @Override
            public void onDownloading(Call call, MediaType contentType, long total, int progress) {
                setDownloadingFileLayoutStatus(true, 0);
            }

            @Override
            public void onDownloadError(Call call, String fileUrl, Throwable e) {
                if (!call.isCanceled()) {
                    call.cancel();
                }
                setResultLayoutStatus(FAILED);
            }
        });
    }

    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {

        @Override
        public void onDfuCompleted(String deviceAddress) {
            super.onDfuCompleted(deviceAddress);
            setResultLayoutStatus(SUCCESSFUL);
            cancleNotification();
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            super.onDfuAborted(deviceAddress);
            setResultLayoutStatus(FAILED);
            cancleNotification();
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            super.onError(deviceAddress, error, errorType, message);
            setResultLayoutStatus(FAILED);
            cancleNotification();
        }

        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            super.onProgressChanged(deviceAddress, percent, speed, avgSpeed, currentPart, partsTotal);
            setDownloadingFileLayoutStatus(false, percent);
        }

        @Override
        public void onDfuProcessStarted(String deviceAddress) {
            super.onDfuProcessStarted(deviceAddress);
            cancleNotification();
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            super.onDeviceDisconnected(deviceAddress);
            setResultLayoutStatus(FAILED);
            cancleNotification();
        }

    };

    private void startDfu() {
        if (TextUtils.isEmpty(deviceMacAddress)) {
            AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).startDiscovery();
            return;
        }
        final DfuServiceInitiator starter = new DfuServiceInitiator(deviceMacAddress)
                .setDeviceName("DEVICE_OTA")
                .setKeepBond(true);
        // Init packet is required by Bootloader/DFU from SDK 7.0+ if HEX or BIN file is given above.
        // In case of a ZIP file, the init packet (a DAT file) must be included inside the ZIP file.
        if (mFileType == DfuService.TYPE_AUTO) {
            starter.setZip(mFilePath);
        } else {
            starter.setBinOrHex(mFileType, mFilePath).setInitFile(mFilePath);
        }
        starter.start(this, DfuService.class);
    }

    private void setBeginDownloadFileLayoutStatus(final long total) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                llUpdate.setVisibility(View.VISIBLE);
                llResult.setVisibility(View.GONE);
                btnResult.setVisibility(View.GONE);
                progressBar.setMax(100);
            }
        });
    }

    private void setDownloadingFileLayoutStatus(final boolean indeterminate, final int updateProgress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setIndeterminate(indeterminate);
                progressBar.setProgress(updateProgress);
            }
        });
    }


    private void setResultLayoutStatus(final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                llUpdate.setVisibility(View.GONE);
                llResult.setVisibility(View.VISIBLE);
                btnResult.setVisibility(View.VISIBLE);
                switch (status) {
                    case SUCCESSFUL:
                        //ivUpdate.setImageResource(R.mipmap.ic_device_update_ok);
                        ivResult.setImageResource(R.mipmap.update_ok);
                        tvResult.setText(getString(R.string.update_complete));
                        break;
                    case FAILED:
                        //ivUpdate.setImageResource(R.mipmap.ic_device_update_failed);
                        ivResult.setImageResource(R.mipmap.update_fail);
                        tvResult.setText(getString(R.string.update_fail));
                        break;
                }
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.btn_result})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_result:
                onBackPressed();
                break;
        }
    }


    protected void stopDfu() {
        final LocalBroadcastManager manager = LocalBroadcastManager.getInstance(DfuActivity.this);
        final Intent pauseAction = new Intent(DfuService.BROADCAST_ACTION);
        pauseAction.putExtra(DfuService.EXTRA_ACTION, DfuService.ACTION_ABORT);
        manager.sendBroadcast(pauseAction);
    }

    private boolean isDfuServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (DfuService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void cancleNotification() {
        // We have to wait a bit before canceling notification. This is called before DfuService creates the last notification.
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // if this activity is still open and upload process was completed, cancel the notification
//                final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                manager.cancel(DfuService.NOTIFICATION_ID);
//            }
//        }, 200);
    }

    private BluetoothManagerScanListener scanListener = new BluetoothManagerScanListener() {
        @Override
        public void onDeviceFound(BluetoothScanDevice bluetoothScanDevice) {
            if (bluetoothScanDevice != null && bluetoothScanDevice.getBluetoothDevice() != null) {
                BluetoothDevice bluetoothDevice = bluetoothScanDevice.getBluetoothDevice();
                if (!TextUtils.isEmpty(bluetoothDevice.getName()) && bluetoothDevice.getName().equals(mOTA_NAME)) {
                    deviceMacAddress = bluetoothDevice.getAddress();
                    AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).cancelDiscovery();
                    startDfu();
                }
            }
        }

        @Override
        public void onDeviceScanEnd() {
            setDownloadingFileLayoutStatus(false, 0);
            showDialogTip(getString(R.string.update_no_found_device), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dimissDialog();
                    setDownloadingFileLayoutStatus(true, 0);
                    AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).cancelDiscovery();
                    AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).startDiscovery();
                }
            });
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).addBluetoothManagerScanListeners(scanListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).removeBluetoothManagerScanListeners(scanListener);
    }


    @Override
    public void onBackPressed() {
        if (llResult.getVisibility() == View.VISIBLE) {
            finish();
            super.onBackPressed();
        } else {
            showExitDialogTip();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
        cancleNotification();
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).doUnbindDevice(BluetoothConfig.getDefaultMac(DfuActivity.this));
        stopDfu();
    }

    private void showDialogTip(final String tip, final View.OnClickListener onClickListener) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dimissDialog();
                dialog = DialogWhiteUtil.createDialogCancleAndPositive(DfuActivity.this, tip, getString(R.string.exit), getString(R.string.sure), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dimissDialog();
                        onBackPressed();
                    }
                }, onClickListener);
                dialog.show();
            }
        });
    }

    private void showExitDialogTip() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dimissDialog();
                dialog = DialogWhiteUtil.createDialogCancleAndPositive(DfuActivity.this, getString(R.string.exit_update_ota), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dimissDialog();
                        stopDfu();
                        finish();
                    }
                });
                dialog.show();
            }
        });
    }

    private void dimissDialog() {
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
}
