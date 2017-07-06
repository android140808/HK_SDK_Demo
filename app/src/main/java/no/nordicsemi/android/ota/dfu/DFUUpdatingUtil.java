package no.nordicsemi.android.ota.dfu;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.constant.APPConstant;
import cn.appscomm.l38t.constant.AppUtil;
import cn.appscomm.l38t.model.database.UserBindDevice;
import cn.appscomm.l38t.utils.ChooseDevices;
import cn.appscomm.l38t.utils.LogUtil;
import cn.appscomm.l38t.utils.ParseUtil;
import no.nordicsemi.android.ota.interfer.IAboutUpgrade;
import no.nordicsemi.android.ota.model.UpgradeInfo;
import no.nordicsemi.android.ota.service.DFUUpdateService;

/**
 * Created by Administrator on 2016/8/24.
 */
public class DFUUpdatingUtil {
    private static final Class TAG = DFUUpdatingUtil.class;
    private static DFUUpdatingUtil dfuUpdatingUtil = new DFUUpdatingUtil();

    private Context context = GlobalApp.getAppContext();
    private boolean mIsBind = false;
    private boolean mConnectBlueTooth = false;
    private DFUUpdateService mBluetoothLeService;
    private String mDeviceName;

    private byte[] binContents = null;              // bin文件字节数据
    private byte[] crcCheck = null;                 // crc校验
    private byte[] binLength = null;                // bin文件大小
    private byte[] binTotalLength = null;           // bin文件大小

    private IAboutUpgrade iAboutUpgrade;


    private DFUUpdatingUtil() {
    }

    public static DFUUpdatingUtil geInstance() {
        return dfuUpdatingUtil;
    }

    // 开始升级
    public void start(IAboutUpgrade iAboutUpgrade, String dfuName) {
        this.iAboutUpgrade = iAboutUpgrade;
        if (TextUtils.isEmpty(dfuName)) {
            UserBindDevice userBindDevice = UserBindDevice.getBindDevice(AccountConfig.getUserId());
            mDeviceName = userBindDevice.getDeviceName();
            mDeviceName = mDeviceName.replace("L", "").replace("#", "");
            LogUtil.i(TAG, "原来的名称:" + mDeviceName);
            mDeviceName = AppUtil.getDeviceOTA_Name(mDeviceName);
        } else {
            mDeviceName = dfuName;
        }
        LogUtil.i(TAG, "升级的设备名:" + mDeviceName);
        bindLeService();
    }

    // 绑定蓝牙
    private void bindLeService() {
        LogUtil.i(TAG, "开始绑定升级服务........");
        if (!mIsBind) {
            mIsBind = true;
            Intent gattServiceIntent = new Intent(context, DFUUpdateService.class);
            context.bindService(gattServiceIntent, mServiceConnection, context.BIND_AUTO_CREATE); // 绑定BluetoothLeService服务
            IntentFilter intentFilter = DFUUpdateService.makeGattUpdateIntentFilter();
            context.registerReceiver(mGattUpdateReceiver, intentFilter); // 注册蓝牙状态监听
        }
    }

    // 绑定BluetoothLeService服务情况的回调
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            LogUtil.i(TAG, "绑定升级服务成功...,准备连接...");
            mBluetoothLeService = ((DFUUpdateService.LocalBinder) service).getService();
            mBluetoothLeService.connect(mDeviceName, 1);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogUtil.i(TAG, "BluetoothLeService服务开启失败...");
            mBluetoothLeService = null;
        }
    };

    public void endBroadcast() {
        LogUtil.i(TAG, "DFUUpdateService 服务关闭,mIsBind : " + mIsBind);
        mConnectBlueTooth = false;
        if (mIsBind) {
            context.unbindService(mServiceConnection);
            context.unregisterReceiver(mGattUpdateReceiver);
            if (mBluetoothLeService != null) {
                mBluetoothLeService.scanLeDevice(false);
                mBluetoothLeService.disconnect();
                mBluetoothLeService.close();
            }
            mBluetoothLeService = null;
            mIsBind = false;
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            // 蓝牙已连接
            if (DFUUpdateService.ACTION_GATT_CONNECTED.equals(action)) {
                LogUtil.i(TAG, "蓝牙消息:已连接...");
                mConnectBlueTooth = true;
            }

            // 蓝牙超时
            else if (DFUUpdateService.ACTION_GATT_SERVICES_TIMEOUT.equals(action)) {
                LogUtil.i(TAG, "蓝牙消息:超时...");
                mConnectBlueTooth = false;
                mBluetoothLeService.connect(mDeviceName, 1);
            }

            // 蓝牙断开
            else if (DFUUpdateService.ACTION_GATT_DISCONNECTED.equals(action)) {
                LogUtil.i(TAG, "蓝牙消息:断开...");
                mConnectBlueTooth = false;
                endBroadcast();
                iAboutUpgrade.upgradeResult(false);
            }

            // 发现蓝牙，可以发送数据到设备
            else if (DFUUpdateService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                LogUtil.e(TAG, "发现蓝牙," + (mConnectBlueTooth == true ? "可以发送数据了!!!" : "还没有连接,还不可以发送数据!!!"));
                LogUtil.i(TAG, "开始升级时间:" + ParseUtil.timeStampToString(System.currentTimeMillis(), ParseUtil.SDF_TYPE_YMDHMS));
                mConnectBlueTooth = true;
                DFUUpdateService.SendTimeOut = true; // 设置传输同步数据时，发送超时消息代替断开重连
                startUpgrade();

            }

            // 接收到蓝牙发送过来的数据
            else if (DFUUpdateService.ACTION_DATA_AVAILABLE.equals(action)) {
                mConnectBlueTooth = true;
                byte[] bytes = intent.getByteArrayExtra(DFUUpdateService.EXTRA_DATA);
                parseRevDatas(bytes);
            }

            // 写回调
            else if (DFUUpdateService.ACTION_DATA_WRITER_CALLBACK.equals(action)) {
                parseRevDatas(null);
            }

            // 蓝牙开关状态广播
            else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                LogUtil.e(TAG, "蓝牙状态发生变化!");
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                    LogUtil.i(TAG, "蓝牙断开了...");
                    endBroadcast();
                    iAboutUpgrade.upgradeResult(false);
                }
            }
        }
    };

    /**
     * 解析接收到的数据
     *
     * @param bytes 为null时是回调，不为null时是设备返回数据
     */
    private void parseRevDatas(byte[] bytes) {
        int updateResult = UpgradeUtils.getInstance().parseRevDatas(bytes);
        switch (updateResult) {

            case UpgradeUtils.UPDATE_SUCCESS:   // 升级成功
                endBroadcast();
                LogUtil.i(TAG, "升级结束时间:" + ParseUtil.timeStampToString(System.currentTimeMillis(), ParseUtil.SDF_TYPE_YMDHMS));
                iAboutUpgrade.upgradeResult(true);
                break;

            case UpgradeUtils.UPDATE_FAILD:     // 升级失败
                endBroadcast();
                iAboutUpgrade.upgradeResult(false);
                break;
        }
    }

    /**
     * 开始升级
     */
    private void startUpgrade() {
        LogUtil.i(TAG, "准备升级非语言");
        String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "";
        final String saveFileName = "application.zip";

        String path = "";
        //test-begin-------------------------------------------------------------------------------------
        if (APPConstant.isTest) {
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            path = externalStorageDirectory.getAbsolutePath() + "/wt10c/";
        }
        //test-end
        else {
            path = context.getFilesDir() + "/";
        }

        ArrayList<UpgradeInfo> upgradeInfos = new ArrayList<>();
        boolean flag = false;
        if (ParseUtil.unZip(path + saveFileName)) {
            if (AppUtil.getShowName().equalsIgnoreCase(ChooseDevices.YOUNG)) {
                if (readL42AFile("TouchPanel.bin", null)) {
                    upgradeInfos.add(new UpgradeInfo(binTotalLength, binLength, crcCheck, binContents, (byte) 0x10));
                    flag = true;
                }
                if (readL42AFile("Heartrate.bin", null)) {
                    upgradeInfos.add(new UpgradeInfo(binTotalLength, binLength, crcCheck, binContents, (byte) 0x20));
                    flag = true;
                }
                if (readL42AFile("kl17.bin", null)) {
                    upgradeInfos.add(new UpgradeInfo(binTotalLength, binLength, crcCheck, binContents, (byte) 0x08));
                    flag = true;
                }
                if (readL42AFile("Picture.bin", null)) {
                    upgradeInfos.add(new UpgradeInfo(binTotalLength, binLength, crcCheck, binContents, (byte) 0x40));
                    flag = true;
                }
                if (readL42AFile("application.bin", "application.dat")) {
                    upgradeInfos.add(new UpgradeInfo(binTotalLength, binLength, crcCheck, binContents, (byte) 0x04));
                    flag = true;
                }
            }
        } else {
            if (readFile("application.bin", "application.dat")) {
                upgradeInfos.add(new UpgradeInfo(binTotalLength, binLength, crcCheck, binContents, (byte) 0x04));
                flag = true;
            }
        }
        LogUtil.e(TAG, "flag=" + flag);
        if (flag) {
            int max = UpgradeUtils.getInstance().proOrders(upgradeInfos.toArray(new UpgradeInfo[upgradeInfos.size()]));
            if (iAboutUpgrade != null)
                iAboutUpgrade.curUpgradeMax(max);
            UpgradeUtils.getInstance().startUpgrade(context, iAboutUpgrade, mBluetoothLeService);
        } else {
            endBroadcast();
            iAboutUpgrade.upgradeResult(false);
        }

    }

    /**
     * 读取bin和dat文件
     *
     * @return true:读取成功 false:读取失败
     */
    private boolean readFile(String binName, String datName) {
        LogUtil.i(TAG, "读取文件(bin文件:" + (TextUtils.isEmpty(binName) ? "" : binName) + "  dat文件:" + (TextUtils.isEmpty(datName) ? "" : datName) + ")中,请稍等...!");
        binContents = null;
        crcCheck = null;
        binLength = null;
        binTotalLength = null;
        byte oldBinContents[] = null;
        File file;

        // 获取Nordic或Freescale的bin大小和bin内容
        file = new File(context.getFilesDir(), binName);
        if (file.exists()) {
            try { // 读取Nordic的bin的内容
                binContents = new byte[(int) file.length()];
                FileInputStream fis = new FileInputStream(file);
                fis.read(binContents);
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int fileLen = (int) file.length(); // 读取Nordic的bin大小
            LogUtil.i(TAG, "fileLen=" + fileLen);
            oldBinContents = binContents;
            binLength = new byte[12];
            for (int i = 0; i < 4; i++) {
                binLength[8 + i] = (byte) ((fileLen >> (8 * i)) & 0xFF);
            }
            binTotalLength = new byte[8];
            for (int i = 0; i < 4; i++) {
                binTotalLength[i] = (byte) ((fileLen >> (8 * i)) & 0xFF);                           // 1-4字节 bin大小
                binTotalLength[4 + i] = oldBinContents[i];                                          // 5-8字节 字库地址 (字库地址取binContents的前四位)
            }
        } else {
            LogUtil.i(TAG, "读取BIN文件(" + datName + ")，文件不存在");
            return false;
        }

        // 获取Nordic或Freescale的CRC校验
        if (datName != null) {
            file = new File(context.getFilesDir(), datName);
            if (file.exists()) { // 如果Nordic的dat文件存在,并且现在是升级Nordic，则直接读取dat里的CRC校验
                try { // 读取Nordic的crc校验
                    FileInputStream fis = new FileInputStream(file);
                    crcCheck = new byte[(int) file.length()];
                    fis.read(crcCheck);
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                LogUtil.i(TAG, "读取ZIP文件(" + datName + ")，文件不存在");
                return false;
            }
        } else { // 人工计算CRC校验值，用于Freescale
            if (binContents != null) {
                crcCheck = new byte[14];
                byte[] crcBytes = ParseUtil.crc16(binContents);
                crcCheck[crcCheck.length - 2] = crcBytes[0];
                crcCheck[crcCheck.length - 1] = crcBytes[1];
            } else {
                crcCheck = null;
            }
        }

        LogUtil.e(TAG, "binTotalLength=" + (binTotalLength == null));
        return binContents != null && binLength != null && crcCheck != null;
    }

    /**
     * 读取bin和dat文件
     *
     * @return true:读取成功 false:读取失败
     */
    private boolean readL42AFile(String binName, String datName) {
        binContents = null;
        crcCheck = null;
        binLength = null;
        binTotalLength = null;
        byte oldBinContents[] = null;
        File file;

        // 获取Nordic或Freescale的bin大小和bin内容
        file = new File(context.getFilesDir(), binName);
        if (file.exists()) {
            try { // 读取Nordic的bin的内容
                binContents = new byte[(int) file.length()];
                FileInputStream fis = new FileInputStream(file);
                fis.read(binContents);
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int fileLen = (int) file.length(); // 读取Nordic的bin大小
            oldBinContents = binContents;
            if (binName.toLowerCase().contains("ko") || binName.toLowerCase().contains("sc") || binName.toLowerCase().contains("tc") || binName.toLowerCase().contains("picture")) {
                binContents = Arrays.copyOfRange(binContents, 4, binContents.length);
                fileLen = fileLen - 4;
            } else if (binName.toLowerCase().contains("kl") || binName.toLowerCase().contains("touchpanel") || binName.toLowerCase().contains("heartrate")) {
                binContents = Arrays.copyOfRange(binContents, 5, binContents.length);
                fileLen = fileLen - 5;
            }
            binLength = new byte[12];
            for (int i = 0; i < 4; i++) {
                binLength[8 + i] = (byte) ((fileLen >> (8 * i)) & 0xFF);
            }
            binTotalLength = new byte[8];
            for (int i = 0; i < 4; i++) {
                binTotalLength[i] = (byte) ((fileLen >> (8 * i)) & 0xFF);   // 1-4字节 bin大小
                binTotalLength[4 + i] = oldBinContents[i];                     // 5-8字节 字库地址 (字库地址取binContents的前四位)
            }
        }

        if (datName != null) {
            file = new File(context.getFilesDir(), datName);
            if (file.exists()) { // 如果Nordic的dat文件存在,并且现在是升级Nordic，则直接读取dat里的CRC校验
                try { // 读取Nordic的crc校验
                    FileInputStream fis = new FileInputStream(file);
                    crcCheck = new byte[(int) file.length()];
                    fis.read(crcCheck);
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else { // 人工计算CRC校验值，用于Freescale
            if (binContents != null) {
                crcCheck = new byte[14];
                byte[] crcBytes = ParseUtil.crc16(binContents);
                crcCheck[crcCheck.length - 2] = crcBytes[0];
                crcCheck[crcCheck.length - 1] = crcBytes[1];
            }
        }
        return binContents != null && binLength != null && crcCheck != null;
    }

}
