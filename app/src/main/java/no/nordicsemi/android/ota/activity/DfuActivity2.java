package no.nordicsemi.android.ota.activity;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appscomm.bluetooth.manage.AppsBluetoothManager;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.constant.AppUtil;
import cn.appscomm.l38t.utils.ChooseDevices;
import cn.appscomm.l38t.utils.LogUtil;
import cn.appscomm.l38t.utils.viewUtil.DialogWhiteUtil;
import no.nordicsemi.android.ota.dfu.DFUUpdatingUtil;
import no.nordicsemi.android.ota.interfer.IAboutUpgrade;
import no.nordicsemi.android.ota.service.DfuService;

/**
 * Created by Administrator on 2016/8/22.
 */
public class DfuActivity2 extends Activity implements IAboutUpgrade {

    private static final Class TAG = DfuActivity2.class;
    public static final int SUCCESSFUL = 0;
    public static final int FAILED = 1;

    @BindView(R.id.tv_main_bar_title)
    TextView tvMainBarTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
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
    private static final int REQUEST_ENABLE_BT = 1000;
    private boolean isUpdating = false;
    String dfuName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ota);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        tvMainBarTitle.setText(getString(R.string.title_update));
        ivBack.setVisibility(View.GONE);
        tvEdit.setVisibility(View.GONE);
        if (AppUtil.getShowName().equalsIgnoreCase(ChooseDevices.HER)) {
            ivUpdate.setImageResource(R.mipmap.her_big);
        } else if (AppUtil.getShowName().equalsIgnoreCase(ChooseDevices.YOUNG)) {
            ivUpdate.setImageResource(R.mipmap.young_big);
        } else {
            ivUpdate.setImageResource(R.mipmap.fusion_big);
        }
    }

    private void initData() {
        isUpdating = true;
        dfuName = "";
        progressBar.setMax(100);
        progressBar.setProgress(0);
        ivResult.setImageResource(R.mipmap.ic_device);
        LogUtil.i(TAG, "开始空中升级，先把DFUUpdatingUtil断开...");
        DFUUpdatingUtil.geInstance().endBroadcast();
        boolean isRedoDFU = false;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isRedoDFU = bundle.getBoolean("isRedoDFU");
            dfuName = bundle.getString("dfuName");
        }
        if (checkBluetoothStatus()) {
            if (!isRedoDFU) {
            } else {
                AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).redoRegiresterService();
                DFUUpdatingUtil.geInstance().endBroadcast();
            }
            LogUtil.i(TAG, "发送空中升级命令a");
            DFUUpdatingUtil.geInstance().start(DfuActivity2.this, dfuName);
        } else {
            ((Activity) (GlobalApp.getAppContext())).startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
        }
    }

    public boolean checkBluetoothStatus() {
        BluetoothManager bluetoothManager = (BluetoothManager) GlobalApp.getAppContext().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isEnabled()) { // 手机蓝牙没打开
            return false;
        }
        return true;
    }

    @Override
    public void curUpgradeProgress(int curPackage) {
        progressBar.setProgress(curPackage);
    }

    @Override
    public void curUpgradeMax(int totalPackage) {
        isUpdating = true;
        progressBar.setMax(totalPackage);
    }

    @Override
    public void upgradeResult(boolean result) {
        isUpdating = false;
        LogUtil.e(TAG, "升级结果处理");
        DFUUpdatingUtil.geInstance().endBroadcast();
        ivBack.setVisibility(View.VISIBLE);
        if (result) {
            setResultLayoutStatus(SUCCESSFUL);
            File filesDir = getFilesDir();
            if (filesDir.exists() && filesDir.isDirectory()) {
                File[] files = filesDir.listFiles();
                for (int i = 0; i < files.length; i++) {
                    files[i].deleteOnExit();
                }
            }
        } else {
            setResultLayoutStatus(FAILED);
        }
    }

    @Override
    public void onBackPressed() {
        if (llResult.getVisibility() == View.VISIBLE) {
            finish();
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
        } else {
            initData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DFUUpdatingUtil.geInstance().endBroadcast();
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
//                        ivUpdate.setImageResource(R.mipmap.ic_device_update_ok);
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
        final LocalBroadcastManager manager = LocalBroadcastManager.getInstance(DfuActivity2.this);
        final Intent pauseAction = new Intent(DfuService.BROADCAST_ACTION);
        pauseAction.putExtra(DfuService.EXTRA_ACTION, DfuService.ACTION_ABORT);
        manager.sendBroadcast(pauseAction);
    }

    private void showDialogTip(final String tip, final View.OnClickListener onClickListener) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dimissDialog();
                dialog = DialogWhiteUtil.createDialogCancleAndPositive(DfuActivity2.this, tip, getString(R.string.exit), getString(R.string.sure), new View.OnClickListener() {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isUpdating) {
                onBackPressed();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showExitDialogTip() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dimissDialog();
                dialog = DialogWhiteUtil.createDialogCancleAndPositive(DfuActivity2.this, getString(R.string.exit_update_ota), new View.OnClickListener() {
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
