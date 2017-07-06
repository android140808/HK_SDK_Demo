package no.nordicsemi.android.ota.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import com.appscomm.bluetooth.manage.AppsBluetoothManager;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.utils.LogUtil;
import cn.appscomm.l38t.utils.ParseUtil;

public class DFUUpdateService extends Service {
    private static final Class TAG = DFUUpdateService.class;

    public static final String MSGHEAD = "com.mykronoz.zecircle2.DFUUpdateService.";
    public static final String ACTION_DATA_AVAILABLE = MSGHEAD + "ACTION_DATA_AVAILABLE";
    public static final String ACTION_DATA_WRITER_CALLBACK = MSGHEAD + "ACTION_DATA_WRITER_CALLBACK";
    public static final String ACTION_GATT_CONNECTED = MSGHEAD + "ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = MSGHEAD + "ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = MSGHEAD + "ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_GATT_SERVICES_TIMEOUT = MSGHEAD + "ACTION_GATT_TIMEOUT";

    public static final String EXTRA_DATA = MSGHEAD + "EXTRA_DATA";

    private static final UUID UUID_CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_CHARACTERISTIC_DFU_1531 = UUID.fromString("00001531-1212-EFDE-1523-785FEABCD123");
    private static final UUID UUID_CHARACTERISTIC_DFU_1532 = UUID.fromString("00001532-1212-EFDE-1523-785FEABCD123");
    private static final UUID UUID_CHARACTERISTIC_DFU_1534 = UUID.fromString("00001534-1212-EFDE-1523-785FEABCD123");
    private static final UUID UUID_DFU_SERVICE = UUID.fromString("00001530-1212-EFDE-1523-785FEABCD123");

    private static final int STATE_CONNECTED = 2;       // 连接状态:连接
    private static final int STATE_DISCONNECTED = 0;    // 连接状态:断开

    public static boolean isConnected = false;          // 当前设备是否已经连接
    public static boolean isServiceDisvered = false;    // 是否已经发现了服务
    public static boolean isEnable_time = false;        // 是否启用计时器

    private final int SN_LEN = 20;                      // 序列号的长度
    private final int WRITETIMEOUT = 20;                // 发送数据超时时间:20相当于10秒
    private final int SCANTIMEOUT = 20000;              // 扫描超时时间:20秒
    private int timeoutCount = 0;                       // 计时次数,每计一次为500毫秒
    private static int timeOutCount2 = 0;               // 连续超时的次数
    private Timer timer1 = null;                        // 计时器
    private BluetoothAdapter mBluetoothAdapter = null;  // 蓝牙适配器
    public static boolean SendTimeOut = false;          // 蓝牙工具类连接后，会把该值设为true
    private int connectTimes = 0;                       // 重连次数
    public static int scanStatus = 0;                   // 0:扫描失败 1:扫描成功 2:扫描中
    public static boolean isSend03 = true;              // true:需要发送03 false:不需要发送03

    private BluetoothGatt mBluetoothGatt = null;
    private BluetoothManager mBluetoothManager = null;
    public static BluetoothDevice bluetoothdevice = null;

    public static String mDeviceAddress = "";
    private String deviceName = "";

    public static int enableOtherNotificationCount = 0;                                             // 其他服务开启监听标志
    private Handler mHandler = new Handler() {
    };

    private final IBinder mBinder = new LocalBinder();

    // "GT-N7100"及以后是Note2 的版本
    public class LocalBinder extends Binder {
        public DFUUpdateService getService() {
            return DFUUpdateService.this;
        }
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    /**
     * 发送广播消息
     *
     * @param msgType 消息类型
     * @param bytes   具体数据
     */
    private void broadcastUpdate(String msgType, byte bytes[]) {
        Intent intent = new Intent(msgType);
        if (bytes != null)
            intent.putExtra(EXTRA_DATA, bytes);
        if (msgType == ACTION_DATA_AVAILABLE) {
            LogUtil.i(TAG, TAG.getSimpleName() + "发送广播...");
        }
        sendBroadcast(intent);
    }

    private void broadcastUpdate(String msgType, int value) {
        Intent intent = new Intent(msgType);
        intent.putExtra(EXTRA_DATA, value);
        sendBroadcast(intent);
    }

    /**
     * 添加需要监听广播的消息
     *
     * @return IntentFilter
     */
    public static IntentFilter makeGattUpdateIntentFilter() {
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(ACTION_GATT_CONNECTED);
        intentfilter.addAction(ACTION_GATT_DISCONNECTED);
        intentfilter.addAction(ACTION_GATT_SERVICES_TIMEOUT);
        intentfilter.addAction(ACTION_GATT_SERVICES_DISCOVERED);
        intentfilter.addAction(ACTION_DATA_AVAILABLE);
        intentfilter.addAction(ACTION_DATA_WRITER_CALLBACK);
        intentfilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return intentfilter;
    }

    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, TAG + "服务创建...!!!");
        timeoutCount = 0;
        if (null == timer1) {
            timer1 = new Timer();
            timer1.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!isEnable_time) {
                        timeoutCount = 0;
                        return;
                    }
                    LogUtil.i("service_timeout", "距离上次发送数据，已经用时:" + (float) ((timeoutCount * 500) / 1000f) + "秒...!!!");
                    if (++timeoutCount > WRITETIMEOUT) {
                        isEnable_time = false;
                        timeOutCount2++;
                        timeoutCount = -2;
                        LogUtil.i("service_timeout", "---发送的数据已超时(" + timeoutCount + "秒)，超时次数是:" + timeOutCount2);
                        disconnect();
                        broadcastUpdate(ACTION_GATT_DISCONNECTED, null);
                    }
                }
            }, 0, 500);
        }

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager != null)
            mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, TAG + "服务已销毁...!!!");
        if (null != timer1) {
            timer1.cancel();
            timer1 = null;
        }
    }

    // 蓝牙广播回调
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        /**
         * 蓝牙状态改变 用于设备返回数据到手机
         * @param bluetoothgatt
         * @param bluetoothgattcharacteristic
         */
        public void onCharacteristicChanged(BluetoothGatt bluetoothgatt, BluetoothGattCharacteristic bluetoothgattcharacteristic) {
            if (mBluetoothGatt != null) {
                byte[] bytes = bluetoothgattcharacteristic.getValue();
                // 特征值：1531/1534
                if (UUID_CHARACTERISTIC_DFU_1531.equals(bluetoothgattcharacteristic.getUuid()) ||
                        UUID_CHARACTERISTIC_DFU_1534.equals(bluetoothgattcharacteristic.getUuid())) { // 如果是8002发送完毕，发送ACTION_DATA_AVAILABLE
                    LogUtil.e(TAG, "<<<<<<<<<<获取到设备返回的数据(1531/1534) : " + ParseUtil.byteArrayToHexString(bytes));
                    LogUtil.i(TAG, " ");
                    if ((bytes != null) && (bytes.length > 1)) {
                        timeOutCount2 = 0; //收到数据就复位
                    }
                    timeoutCount = 0;
                    isEnable_time = false;
                    broadcastUpdate(ACTION_DATA_AVAILABLE, bytes);
                }
            }
        }

        /**
         * 读回调
         * @param bluetoothgatt
         * @param bluetoothgattcharacteristic
         * @param i
         */
        public void onCharacteristicRead(BluetoothGatt bluetoothgatt, BluetoothGattCharacteristic bluetoothgattcharacteristic, int i) {
            LogUtil.i(TAG, "==>>onCharacteristicRead(系统返回读回调)");
        }

        /**
         * 写回调
         * @param bluetoothgatt
         * @param bluetoothgattcharacteristic
         * @param i
         */
        public void onCharacteristicWrite(BluetoothGatt bluetoothgatt, BluetoothGattCharacteristic bluetoothgattcharacteristic, int i) {
            LogUtil.i(TAG, "==>>onCharacteristicWrite(系统返回写回调)");
            if (mBluetoothGatt != null) {
                broadcastUpdate(ACTION_DATA_WRITER_CALLBACK, null);
                if (UUID_CHARACTERISTIC_DFU_1532.equals(bluetoothgattcharacteristic.getUuid())) {
                    continueSendBytes();
                } else if (UUID_CHARACTERISTIC_DFU_1531.equals(bluetoothgattcharacteristic.getUuid())) {
                } else if (UUID_CHARACTERISTIC_DFU_1534.equals(bluetoothgattcharacteristic.getUuid())) {
                }
            }
        }

        /**
         * 连接状态回调
         * @param bluetoothgatt
         * @param state
         * @param newState
         */
        @SuppressLint("NewApi")
        public void onConnectionStateChange(BluetoothGatt bluetoothgatt, int state, int newState) {
            // 断开连接
            if (newState == STATE_DISCONNECTED) {
                LogUtil.e(TAG, "xxxxxxxxxxxxx连接状态回调(state=" + state + " newState=" + newState + " 断开连接)");
                scanStatus = 0;
                disconnect();
                broadcastUpdate(ACTION_GATT_DISCONNECTED, null);
            }

            // 已经连接
            else if ((newState == STATE_CONNECTED) && (state == 0)) {
                LogUtil.w(TAG, "==>>1、连接状态回调(state=" + state + " newState=" + newState + " (" + "已连接),准备发现服务...!!!)");
                SendTimeOut = false;
                isConnected = true;
                isServiceDisvered = false;
                timeoutCount = -10;
                connectTimes = 0;
                broadcastUpdate(ACTION_GATT_CONNECTED, null);
                mBluetoothGatt.discoverServices();
            }

            // 设备发送连接请求失败回调(此处断开一直重连)
            else if ((state == 133) && (newState == 2)) {
                LogUtil.e(TAG, "+++++++++++++连接状态回调(state=" + state + " newState=" + newState + " 未连接到设备,准备重新连接)");
                scanStatus = 0;
                connectTimes++;
                LogUtil.e(TAG, "重新连接次数 : " + connectTimes);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        connect(mDeviceAddress, 0);
                    }
                }, 3000);
            }
        }

        // 发现BluetoothGatt服务
        @Override
        public void onServicesDiscovered(BluetoothGatt bluetoothgatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                timeoutCount = -6;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LogUtil.w(TAG, "==>>2、已发现服务(onServicesDiscovered),准备打开1531监听...!!!");
                enableNotification(UUID_DFU_SERVICE, UUID_CHARACTERISTIC_DFU_1531);
            } else {
                LogUtil.e(TAG, "==>>onServicesDiscovered,有异常...!!!");
            }
        }

        public void onDescriptorWrite(BluetoothGatt bluetoothgatt, BluetoothGattDescriptor bluetoothgattdescriptor, int i) {
            if (DFUUpdateService.UUID_CONFIG_DESCRIPTOR.equals(bluetoothgattdescriptor.getUuid())) {
                if (!isServiceDisvered) {
                    LogUtil.w(TAG, "==>>3、已打开1531监听(onDescriptorWrite),准备发送Discovered广播...!!!");
                    bluetoothgatt.readDescriptor(bluetoothgattdescriptor);
                } else {
                    enableOtherNotificationCount--;
                    enableOtherNotificationCount = enableOtherNotificationCount < 0 ? 0 : enableOtherNotificationCount;
                    LogUtil.w(TAG, "==>>其他监听:成功打开一个,还剩下" + enableOtherNotificationCount + "个监听没有打开...!!!");
                }
            } else {
                LogUtil.e(TAG, "==>>onDescriptorWrite,有异常...!!!");
            }
        }

        public void onDescriptorRead(BluetoothGatt bluetoothgatt, BluetoothGattDescriptor bluetoothgattdescriptor, int i) {
            if (DFUUpdateService.UUID_CONFIG_DESCRIPTOR.equals(bluetoothgattdescriptor.getUuid())) {
                isServiceDisvered = true;
                LogUtil.w(TAG, "==>>4、已经连接完毕(onDescriptorRead),发送Discovered广播...!!!");
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, null);
            } else {
                LogUtil.e(TAG, "==>>onDescriptorRead,有异常...!!!");
            }
        }
    };

    // 打开8002/心率监听，让手机可以接收到设备发送过来的数据
    public void enableNotification(UUID service, UUID characteristic) {
        if (mBluetoothGatt != null) {
            try {
                BluetoothGattCharacteristic bluetoothgattcharacteristic = mBluetoothGatt.getService(service).getCharacteristic(characteristic);
                mBluetoothGatt.setCharacteristicNotification(bluetoothgattcharacteristic, true);

                BluetoothGattDescriptor bluetoothgattdescriptor = bluetoothgattcharacteristic.getDescriptor(UUID_CONFIG_DESCRIPTOR);
                bluetoothgattdescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(bluetoothgattdescriptor);
            } catch (Exception e) {
            }
        }
    }

    // 在8002/其他通道发送03到设备
    private void confirmByWriting0x03ToCharacteristic2(UUID service, UUID characteristic) {
        LogUtil.i(TAG, "==>>命令已经发送到设备了，写03到设备结束...");
        if (mBluetoothGatt != null) {
            BluetoothGattCharacteristic bluetoothgattcharacteristic = mBluetoothGatt.getService(service).getCharacteristic(characteristic);
            bluetoothgattcharacteristic.setValue(new byte[]{0x03});
            mBluetoothGatt.writeCharacteristic(bluetoothgattcharacteristic);
        }
    }

    /**
     * 发送数据到设备
     *
     * @param bytes      数据
     * @param is1531Flag true:1531 false:1532
     * @throws InterruptedException
     */
    public void sendDataToPedometer(byte[] bytes, boolean is1531Flag) {
        if (is1531Flag) {
            writeDataToDevice(bytes, true);
        } else {
            sendLargeBytes(bytes);
        }
    }

    /**
     * 写数据到设备
     *
     * @param bytes      具体的数据
     * @param is1531Flag true:1531 ; false:1532
     */
    public void writeDataToDevice(byte bytes[], boolean is1531Flag) {
        if (bytes != null) {
            if (mBluetoothGatt != null) {
                BluetoothGattCharacteristic bluetoothgattcharacteristic = null;
                try {
                    if (is1531Flag) {
                        bluetoothgattcharacteristic = mBluetoothGatt.getService(UUID_DFU_SERVICE).getCharacteristic(UUID_CHARACTERISTIC_DFU_1531);
                        LogUtil.i(TAG, ">>>> 1531 发送的数据是 : " + ParseUtil.byteArrayToHexString(bytes));
                    } else {
                        bluetoothgattcharacteristic = mBluetoothGatt.getService(UUID_DFU_SERVICE).getCharacteristic(UUID_CHARACTERISTIC_DFU_1532);
                        bluetoothgattcharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                        LogUtil.i(TAG, ">>>> 1532 发送的数据是 : " + ParseUtil.byteArrayToHexString(bytes));
                    }
                    bluetoothgattcharacteristic.setValue(bytes);
                    mBluetoothGatt.writeCharacteristic(bluetoothgattcharacteristic);
                } catch (Exception e) {
                }
            }
        }
    }

    public void close() { // 这个调用接口是用来给以前短连接用的，
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        SendTimeOut = false;
        sendBytes = null;
        sendBytesPacketCount = 0;
        timeoutCount = -2;
        isConnected = false;
        isServiceDisvered = false;
        if (null != timer1) {
            timeoutCount = 0;
            timeOutCount2 = 0;
            isEnable_time = false;
        }
        if (mBluetoothGatt != null) {
            try {
                mBluetoothGatt.disconnect();
                mBluetoothGatt.close();
                mBluetoothGatt = null;
            } catch (Exception e) {
            }
        }
    }

    /**
     * 通过mac地址连接设备
     *
     * @param macOrDeviceName
     * @return
     */
    @SuppressLint("NewApi")
    public boolean connect(String macOrDeviceName, int mode) {
        if (TextUtils.isEmpty(macOrDeviceName)) return false;
        boolean flag = true;
        if (mode == 1) {
            deviceName = macOrDeviceName;
            scanLeDevice(true);
        } else if (mode == 0) {
            mDeviceAddress = macOrDeviceName;
            if (mBluetoothAdapter == null) {
                flag = false;
            } else {
                if ((mBluetoothGatt != null)) {
                    try {
                        mBluetoothGatt.disconnect();
                        mBluetoothGatt.close();
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mBluetoothGatt = null;
                }

                bluetoothdevice = mBluetoothAdapter.getRemoteDevice(macOrDeviceName);
                if (bluetoothdevice == null) {
                    flag = false;
                } else {
                    timeoutCount = -8;
                    mBluetoothGatt = bluetoothdevice.connectGatt(this, android.os.Build.VERSION.SDK_INT >= 19 ? false : true, mGattCallback);
                    LogUtil.w(TAG, "-------------连接设备(通过mac地址连接设备,mac : " + mDeviceAddress + "   绑定状态是 : " + bluetoothdevice.getBondState() + ")");
                }
            }
        }
        return flag;
    }

    /**
     * 重新扫描或停止扫描
     *
     * @param enable
     */
    public void scanLeDevice(final boolean enable) {
        if (enable) {
            LogUtil.i(TAG, "准备扫描设备...,目前扫描状态是 : " + scanStatus);
            if (scanStatus != 2) {
                LogUtil.i(TAG, "开始扫描...mBluetoothAdapter : " + (mBluetoothAdapter != null));
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.i(TAG, "超时:目前扫描状态是 : " + scanStatus + "(0:扫描失败 1:扫描成功 2:扫描中)");
                        LogUtil.i(TAG, "deviceName : " + (deviceName != null));
                        if (TextUtils.isEmpty(deviceName)) {
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        }
                    }
                }, SCANTIMEOUT);
                boolean flag = mBluetoothAdapter.startLeScan(mLeScanCallback);
                LogUtil.i(TAG, "首次开启扫描结果:" + flag);
                if (!flag) {
                    for (int i = 0; i < 30; i++) {
                        LogUtil.e(TAG, "开启手机扫描失败,再次开启,当前为第" + i + "次...");
                        flag = mBluetoothAdapter.startLeScan(mLeScanCallback);
                        if (flag) {
                            break;
                        }
                    }
                    LogUtil.e(TAG, "因为开启扫描失败,这里重启蓝牙服务...!!!");
//                    BluetoothUtilNew.getInstance().restartBroadcast();
                    AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).redoRegiresterService();
                }
                LogUtil.w(TAG, "最终扫描结果 : " + flag);
            }
        } else {
            scanStatus = scanStatus == 2 ? 0 : scanStatus;
            LogUtil.i(TAG, "手动:停止扫描,扫描状态是 : " + scanStatus + "(0:扫描失败 1:扫描成功 2:扫描中)");
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    /**
     * 扫描回调，这里会通过devname和watchid拼接出devname进行比对，比对通过则直接连接设备
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @SuppressLint("NewApi")
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            try {
                scanStatus = 2;
                String devname = device.getName();
                LogUtil.i(TAG, "需要升级的设备名是:(" + deviceName + ") 扫描到的设备:" + devname + " mac:" + device.getAddress());
                if (!TextUtils.isEmpty(devname) && deviceName.equals(devname)) {
                    LogUtil.w(TAG, "找到该设备了 设备名称是:" + deviceName + " MAC:" + device.getAddress());
                    scanStatus = 1;
                    mDeviceAddress = device.getAddress();

                    scanLeDevice(false);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            connect(mDeviceAddress, 0);
                        }
                    }, 1000);
                }
            } catch (Exception e) {
            }
        }
    };

    private byte[] sendBytes = null;                // 发送的字节数组
    private int sendBytesPacketCount = 0;           // 发送的包数

    /**
     * 发送多字节包使用该接口
     *
     * @param bytes 数据
     */
    public void sendLargeBytes(byte[] bytes) {
        sendBytes = null;
        sendBytesPacketCount = 0;
        if (bytes != null) {
            sendBytes = bytes;
            if (bytes.length <= 20) {               // 小包直接发走
                writeDataToDevice(bytes, false);
                return;
            }
            sendBytesPacketCount = sendBytes.length / 20 + (sendBytes.length % 20 == 0 ? 0 : 1);
            byte[] firstBytes = new byte[20];
            System.arraycopy(sendBytes, 0, firstBytes, 0, 20);
            LogUtil.i("test_sendLargeBytes", "大字节数组发送第一包:" + ParseUtil.byteArrayToHexString(firstBytes) + " 共" + sendBytesPacketCount + "包数据!!!");
            sendBytesPacketCount--;                 // 发了一包，总数减一
            writeDataToDevice(firstBytes, false);          // 先发第一包
        }
    }

    /**
     * 继续发送数据
     *
     * @return false(已经发送完毕) true(继续发送)
     */
    private boolean continueSendBytes() {
        if (sendBytesPacketCount != 0) {
            byte[] tmpBytes;
            if (sendBytesPacketCount == 1) {        // 是否最后一包，最后一包需检查要发送的字节数
                LogUtil.i("test_sendLargeBytes", "还有最后一包没有发...");
                tmpBytes = new byte[sendBytes.length % 20 == 0 ? 20 : (sendBytes.length % 20)];
            } else {
                LogUtil.i("test_sendLargeBytes", "还有" + sendBytesPacketCount + "包没有发!!!");
                tmpBytes = new byte[20];
            }
            int count = sendBytes.length / 20 + (sendBytes.length % 20 == 0 ? 0 : 1);
            int index = count - sendBytesPacketCount;
            LogUtil.i("test_sendLargeBytes", "index : " + index + "   len : " + tmpBytes.length + "   totallen : " + sendBytes.length);
            System.arraycopy(sendBytes, 20 * index, tmpBytes, 0, tmpBytes.length);
            LogUtil.i("test_sendLargeBytes", "包数据是：" + ParseUtil.byteArrayToHexString(tmpBytes));
            sendBytesPacketCount--;                 // 每发一包，减一包
            writeDataToDevice(tmpBytes, false);
            return true;
        }
        return false;
    }


}

