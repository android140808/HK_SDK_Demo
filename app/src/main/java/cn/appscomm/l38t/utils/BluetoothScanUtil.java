package cn.appscomm.l38t.utils;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;

import cn.appscomm.l38t.app.GlobalApp;

/**
 * Created by cui on 2017/4/18.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BluetoothScanUtil {

    private static final Class TAG = BluetoothScanUtil.class;
    private static BluetoothScanUtil bluetoothScanUtil = new BluetoothScanUtil();

    private BluetoothScanResultCallBack bluetoothScanResultCallBack = null;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;

    private BluetoothScanUtil() {
    }

    public static BluetoothScanUtil getInstance() {
        return bluetoothScanUtil;
    }

    public interface BluetoothScanResultCallBack {
        void onLeScan(BluetoothDevice device, int rssi);
    }

    private boolean init() {
        if (mBluetoothManager == null) {
            mBluetoothManager = ((BluetoothManager) GlobalApp.getAppContext().getSystemService(Context.BLUETOOTH_SERVICE));
            if (mBluetoothManager == null) {
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        return this.mBluetoothAdapter != null;
    }

    public boolean startScan(BluetoothScanResultCallBack bluetoothScanResultCallBack) {
        if (Build.VERSION.SDK_INT >= 21) {
            return startEXScan(bluetoothScanResultCallBack);
        } else {
            if (!init()) return false;
            this.bluetoothScanResultCallBack = bluetoothScanResultCallBack;
            return scanLeDevice(true);
        }
    }

    public void stopScan() {
        if (!init()) return;
        this.bluetoothScanResultCallBack = null;
        if (Build.VERSION.SDK_INT >= 21) {
            stopEXScan();
        } else {
            scanLeDevice(false);
        }
    }

    private boolean scanLeDevice(boolean enable) {
        if (enable) {
            if (mScanning)
                return true;
            boolean flag = false;
            for (int i = 0; i < 30; i++) {
                flag = mBluetoothAdapter.startLeScan(mLeScanCallback);
                if (flag) {
                    break;
                }
            }
            mScanning = flag;
        } else {
            if (!mScanning)
                return true;
            if (mBluetoothAdapter != null && mLeScanCallback != null)
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        return mScanning;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (bluetoothScanResultCallBack != null) {
                bluetoothScanResultCallBack.onLeScan(device, rssi);
            }
        }
    };

    public boolean startEXScan(BluetoothScanUtil.BluetoothScanResultCallBack bluetoothScanResultCallBack) {
        if (!init()) return false;
        this.bluetoothScanResultCallBack = bluetoothScanResultCallBack;
        return scanLeEXDevice(true);
    }

    public void stopEXScan() {
        if (!init()) return;
        this.bluetoothScanResultCallBack = null;
        scanLeEXDevice(false);
    }

    private boolean scanLeEXDevice(boolean enable) {
        if (mBluetoothAdapter != null) {
            BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            if (bluetoothLeScanner != null) {
                if (enable) {
                    ScanSettings.Builder builder = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);                               // 推荐使用该配置,扫描速度比较快
                    bluetoothLeScanner.startScan(null, builder.build(), scanCallback);
                    mScanning = true;
                } else {
                    bluetoothLeScanner.stopScan(scanCallback);
                    mScanning = false;
                }
            }
        } else {
            mScanning = false;
        }
        return mScanning;
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (bluetoothScanResultCallBack != null) {
                bluetoothScanResultCallBack.onLeScan(result.getDevice(), result.getRssi());
            }
        }
    };
}
