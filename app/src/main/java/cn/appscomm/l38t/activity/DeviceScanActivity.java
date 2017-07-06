package cn.appscomm.l38t.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.appscomm.bluetooth.bean.BluetoothScanDevice;
import com.appscomm.bluetooth.interfaces.BluetoothManagerDeviceConnectListener;
import com.appscomm.bluetooth.interfaces.BluetoothManagerScanListener;
import com.appscomm.bluetooth.manage.AppsBluetoothManager;

import butterknife.BindView;
import butterknife.OnClick;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.activity.base.BaseActivity;
import cn.appscomm.l38t.adapter.DeviceListAdapter;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.constant.AppUtil;
import cn.appscomm.l38t.eventbus.GlobalEvent;
import cn.appscomm.l38t.utils.ChooseDevices;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/3 11:48
 */
public class DeviceScanActivity extends BaseActivity {


    //    @BindView(R.id.searching_for)
//    TextView searching_for;
    @BindView(R.id.show_device_type)
    ImageView deviceType;

    @BindView(R.id.lv_devices)
    ListView lvDevices;
    private boolean isClick = false;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private String mAddress;
    private String mDeviceName;
    private DeviceListAdapter devAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);
        setTitle(R.string.title_turn_on_bluetooth);
        init();
        initType();
    }

    private void initType() {
        if (ChooseDevices.CHOOSEDEVICES.equalsIgnoreCase(ChooseDevices.HER)) {
            deviceType.setImageResource(R.mipmap.her_small);
        } else if (ChooseDevices.CHOOSEDEVICES.equalsIgnoreCase(ChooseDevices.YOUNG)) {
            deviceType.setImageResource(R.mipmap.icon_young);
        } else {
            deviceType.setImageResource(R.mipmap.fusion_small);
        }
    }

    private void init() {
//        searching_for.setText(getString(R.string.searching_for) + ChooseDevices.CHOOSEDEVICES);
        devAdapter = new DeviceListAdapter(this);
        lvDevices.setAdapter(devAdapter);
        lvDevices.setOnItemClickListener(itemClickListener);
        mHandler = new Handler();
        mAddress = "";
        mDeviceName = "";
        initAdapter();
    }

    private void initAdapter() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mHandler != null) {
            mHandler.postDelayed(scanDeviceRunnable, 500);
        }
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            devAdapter.setChoosed(position);
            BluetoothDevice bluetoothDevice = devAdapter.getChoosedDev(position);
            if (bluetoothDevice == null || TextUtils.isEmpty(bluetoothDevice.getName())) {
                devAdapter.clearList();
                return;
            }
            isClick = true;
            com.appscomm.bluetooth.manage.AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).cancelDiscovery();
            showBigLoadingProgress(getString(R.string.connecting));
            com.appscomm.bluetooth.manage.AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).connectDevice(bluetoothDevice.getAddress());
        }
    };

    private Runnable scanDeviceRunnable = new Runnable() {
        @Override
        public void run() {
            scanDevice();
        }
    };


    private void scanDevice() {
        if (mBluetoothAdapter == null) {
            showToast(getString(R.string.error_bluetooth_not_supported));
            return;
        }
        if (mBluetoothAdapter.isEnabled()) {
            mAddress = "";
            showBigLoadingProgress(getString(R.string.searching), new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dismissLoadingDialog();
                        com.appscomm.bluetooth.manage.AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).cancelDiscovery();
                    }
                    return false;
                }
            });
            devAdapter.clearList();
            com.appscomm.bluetooth.manage.AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).startDiscovery();
        } else {
            showToast(getString(R.string.turn_on_bluetooth_tips));
        }
    }

    private BluetoothManagerScanListener scanListener = new BluetoothManagerScanListener() {
        @Override
        public synchronized void onDeviceFound(BluetoothScanDevice scanDevice) {
            if (scanDevice != null && scanDevice.getBluetoothDevice() != null) {
                BluetoothDevice bluetoothDevice = scanDevice.getBluetoothDevice();
                if (null != bluetoothDevice.getName() && AppUtil.isCanScanDevice(bluetoothDevice.getName())) {
                    devAdapter.addDevice(devAdapter.new DevData(bluetoothDevice, scanDevice.getRssi()));
                }
            }
        }

        @Override
        public void onDeviceScanEnd() {
            if (!DeviceScanActivity.this.isDestroyed()) {
                dismissLoadingDialog();
                if (devAdapter.getCount() <= 0) {
                    if (!DeviceScanActivity.this.isDestroyed()) {
                        showTipDialog(getString(R.string.no_found_device));
                    }
                }
            }
        }
    };


    private BluetoothManagerDeviceConnectListener connectListener = new BluetoothManagerDeviceConnectListener() {
        @Override
        public void onConnected(BluetoothDevice bluetoothDevice) {
            sendMessage(GlobalEvent.EVENBUS_SIGNAL_CODE_DEVICE_CONNECTED);
        }

        @Override
        public void onConnectFailed() {
            if (isClick) {
                doFail();
            }
        }

        @Override
        public void onEnableToSendComand(final BluetoothDevice device) {
            mAddress = device.getAddress();
            mDeviceName = device.getName();
            sendMessage(GlobalEvent.EVENBUS_SIGNAL_CODE_DEVICE_ENABLE_CMD);
            dismissLoadingDialog();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 不用配对
                    // com.appscomm.bluetooth.manage.AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).createBound(device);
                    openBindDevice();
                }
            });

        }

        @Override
        public void onConnectDeviceTimeOut() {
            doFail();
        }
    };

    private void doFail() {
        dismissLoadingDialog();
        devAdapter.clearList();
        if (!DeviceScanActivity.this.isDestroyed()) {
            showTipDialog(getString(R.string.connect_time_out));
        }
    }


    private void openBindDevice() {
        com.appscomm.bluetooth.manage.AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).cancelDiscovery();
        com.appscomm.bluetooth.manage.AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).clearBluetoothManagerDeviceConnectListeners();
        com.appscomm.bluetooth.manage.AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).removeBluetoothManagerScanListeners(scanListener);
        Intent intent = new Intent(DeviceScanActivity.this, BindDeviceActivity.class);
        intent.putExtra("DeviceAddress", mAddress);
        intent.putExtra("DeviceName", mDeviceName);
        startActivity(intent);
        finish();
    }


    @OnClick(R.id.btn_search)
    public void onClick() {
        if (mBluetoothAdapter == null) {
            showToast(getString(R.string.error_bluetooth_not_supported));
            return;
        }
        scanDevice();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).removeBluetoothManagerScanListeners(scanListener);
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).removeBluetoothManagerDeviceConnectListeners(connectListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isClick = false;
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).addBluetoothManagerScanListeners(scanListener);
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).addBluetoothManagerDeviceConnectListener(connectListener);
    }

    @Override
    public void onBackPressed() {
        isClick = false;
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).cancelDiscovery();
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).clearBluetoothManagerDeviceConnectListeners();
        Intent intent = new Intent(this, DeviceChooseActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isClick = false;
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).cancelDiscovery();
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).clearBluetoothManagerDeviceConnectListeners();
        mBluetoothAdapter = null;
        mHandler.removeCallbacks(scanDeviceRunnable);
        mHandler = null;
    }
}
