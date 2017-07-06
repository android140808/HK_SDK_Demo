package no.nordicsemi.android.ota.dfu;

import android.content.Context;

import java.util.LinkedList;

import cn.appscomm.l38t.utils.LogUtil;
import cn.appscomm.l38t.utils.ParseUtil;
import no.nordicsemi.android.ota.interfer.IAboutUpgrade;
import no.nordicsemi.android.ota.model.UpgradeInfo;
import no.nordicsemi.android.ota.service.DFUUpdateService;

public class UpgradeUtils {
    private static final String TAG = "UpgradeUtils";

    public static final int UPDATE_SUCCESS = 0;
    public static final int UPDATE_ING = 1;
    public static final int UPDATE_FAILD = 2;

    private final byte SEND_PACKAGE_COUNT = 0x0a;                                                   // 一次发送的包数

    LinkedList<OrderInfo> sendOrders = new LinkedList<>();                                          // 所有要升级的命令
    private int max = -1;                                                                           // 总包数,用于回调给调用者,计算进度

    private Context context;
    private IAboutUpgrade iAboutUpgrade;
    private DFUUpdateService dfuUpdateService;

    private static UpgradeUtils upgradeUtils = new UpgradeUtils();                                  // 单例

    private UpgradeUtils() {
    }

    class OrderInfo {
        /**
         * 内容
         */
        public byte[] content;
        /**
         * 是否1531特征通道
         */
        public boolean is1531Flag;
        /**
         * 备注
         */
        public String note;

        public OrderInfo(byte[] content, boolean is1531Flag, String note) {
            this.content = content;
            this.is1531Flag = is1531Flag;
            this.note = note;
        }
    }

    /**
     * 获取实例
     *
     * @return context和mBluetoothLeService都不为null的情况下, 才返回实例
     */
    public static UpgradeUtils getInstance() {
        return upgradeUtils;
    }

    /**
     * 开始升级
     *
     * @return true:可以升级 false:传入的参数有误，不可以升级
     */
    public boolean startUpgrade(Context context, IAboutUpgrade iAboutUpgrade, DFUUpdateService dfuUpdateService) {
        if (context != null && dfuUpdateService != null) {
            this.context = context;
            this.iAboutUpgrade = iAboutUpgrade;
            this.dfuUpdateService = dfuUpdateService;
            return sendDatasToDevice();
        } else {
            return false;
        }
    }

    public int proOrders(UpgradeInfo[] upgradeInfos) {
        sendOrders.clear();
        byte binTotalLength[] = new byte[8];
        int total = 0;
        for (UpgradeInfo upgradeInfo : upgradeInfos) {
            total += ParseUtil.bytesToLong(upgradeInfo.binTotalLength, 0, 3);                       // 计算总长度
            if (upgradeInfo.orderCode == (byte) 0x40) {                                             // 如果存在字库,则先取出字库地址
                System.arraycopy(upgradeInfo.binTotalLength, 4, binTotalLength, 4, 4);              // 5-8字节 字库地址 (字库地址取binContents的前四位)
            }
        }
        for (int i = 0; i < 4; i++) {
            binTotalLength[i] = (byte) ((total >> (8 * i)) & 0xFF);                                 // 1-4字节 bin大小(所有待升级的bin总大小)
        }
        for (int i = 0; i < upgradeInfos.length; i++) {
            UpgradeInfo upgradeInfo = upgradeInfos[i];
            baseOrders(binTotalLength, upgradeInfo.binLength, upgradeInfo.crcCheck, upgradeInfo.binContents, upgradeInfo.orderCode, i == 0 ? true : false, i == upgradeInfos.length - 1 ? true : false);
        }
        max = sendOrders.size();
        return max;
    }

    /**
     * Nordic或Freescale命令整理
     *
     * @param binTotalLength Bin的总长度
     * @param binLength      Nordic或Freescale Bin的长度
     * @param crcCheck       Nordic或Freescale CRC校验
     * @param binContents    Nordic或Freescale Bin的内容
     * @param orderCode      0x04:整理的是Nordic 0x08:整理的是Freescale  0x10:触摸 0x40:Picture
     * @param isStartFlag    开始标志
     * @param isEndFlag      结束标志
     */
    private void baseOrders(byte[] binTotalLength, byte[] binLength, byte[] crcCheck, byte[] binContents, byte orderCode, boolean isStartFlag, boolean isEndFlag) {
        if (isStartFlag) {
            // 09
            sendOrders.addLast(new OrderInfo(new byte[]{0x09}, true, ""));

            // 总bin文件大小
            sendOrders.addLast(new OrderInfo(binTotalLength, false, "BIN_TOTAL_LENGTH"));
        }

        // 01 08
        sendOrders.addLast(new OrderInfo(new byte[]{0x01, orderCode}, true, ""));

        // bin文件大小
        sendOrders.addLast(new OrderInfo(binLength, false, "BIN_LENGTH"));

        // 02 00
        sendOrders.addLast(new OrderInfo(new byte[]{0x02, 0x00}, true, ""));

        // CRC校验值
        sendOrders.addLast(new OrderInfo(crcCheck, false, "CRC"));

        // 02 01
        sendOrders.addLast(new OrderInfo(new byte[]{0x02, 0x01}, true, ""));

        // 08 发送包数 00
        sendOrders.addLast(new OrderInfo(new byte[]{0x08, SEND_PACKAGE_COUNT, 0x00}, true, ""));

        // 03
        sendOrders.addLast(new OrderInfo(new byte[]{0x03}, true, ""));

        // bin内容
        if (binContents != null) {
            int binContentLen = binContents.length;
            int onePackageLen = SEND_PACKAGE_COUNT * 20;
            int totalPackageCount = binContentLen % onePackageLen == 0 ? binContentLen / onePackageLen : (binContentLen / onePackageLen) + 1;
            LogUtil.i(TAG, "内容总长度为:" + binContentLen + "   每包发送的最大长度为:" + onePackageLen + "   总包数为:" + totalPackageCount);
            byte[] tempBytes = null;
            int tempLen = 0;
            for (int i = 0; i < totalPackageCount; i++) {
                tempLen = ((i + 1) == totalPackageCount) ? binContentLen - (onePackageLen * i) : onePackageLen;
                tempBytes = new byte[tempLen];
                System.arraycopy(binContents, onePackageLen * i, tempBytes, 0, tempLen);
                sendOrders.addLast(new OrderInfo(tempBytes, false, "BIN_CONTENT"));
            }
        }

        // 04
        sendOrders.addLast(new OrderInfo(new byte[]{0x04}, true, "ASK_FOR_CHECK_CRC"));

        // 05
        /**
         if (!(upgradeMode == 3 && !isNordicFlag)) { // 当升级模式为全部升级，并且目前是整理Freescale的，则不需要重启命令
         sendOrders.addLast(new OrderInfo(new byte[]{0x05}, true, ""));
         }*/
        if (isEndFlag) {
            sendOrders.addLast(new OrderInfo(new byte[]{0x05}, true, ""));
        }
    }

    /**
     * 解析数据
     *
     * @param bytes 回调时传入null,设备返回时传入具体数据
     * @return 0:升级成功 1:正在升级 2:升级错误
     */
    public int parseRevDatas(byte[] bytes) {
        if (sendOrders.size() > 0) {                                                                // 集合里还有命令才需要解析
            String note = sendOrders.getFirst().note;
            byte[] content = sendOrders.getFirst().content;
            // 回调返回
            if (bytes == null) {
                if (note.equals("BIN_TOTAL_LENGTH") || note.equals("BIN_LENGTH") || note.equals("BIN_CONTENT") || note.endsWith("ASK_FOR_CHECK_CRC")) { // 需要等设备返回的命令
                    // LogUtil.i(TAG, "不需要继续发送，等设备返回数据");
                } else if (content.length == 1 && content[0] == 0x05) { // 05
                    sendOrders.clear();
                    if (iAboutUpgrade != null && max > 0) {
                        iAboutUpgrade.curUpgradeProgress(max - sendOrders.size());
                    }
                    LogUtil.i(TAG, "升级完毕...!!!");
                    return UPDATE_SUCCESS;
                } else {                                                                            // 其他命令
                    LogUtil.i(TAG, "继续发送...");
                    sendOrders.removeFirst();
                    if (!sendDatasToDevice()) {
                        return UPDATE_FAILD;
                    }
                }
            }
            // 设备返回数据
            else {
                LogUtil.i(TAG, "<<<<<<<<<<接收:" + ParseUtil.byteArrayToHexString(bytes));
                // (10 01 01) && (10 02 01) && (10 03 01) && (10 04 01)
                if (bytes.length == 3 && bytes[0] == 0x10 && bytes[2] == 0x01) {
                    if ((bytes[1] == 0x01 && note.equals("BIN_LENGTH"))
                            || (bytes[1] == 0x09 && note.equals("BIN_TOTAL_LENGTH"))
                            // || (bytes[1] == 0x02 && note.equals("02 01"))
                            || (bytes[1] == 0x03) || (bytes[1] == 0x04 && note.equals("ASK_FOR_CHECK_CRC"))) {
                        LogUtil.e(TAG, "sendorders is1 " + (sendOrders == null ? true : false));
                        sendOrders.removeFirst();
                        if (!sendDatasToDevice()) {
                            return UPDATE_FAILD;
                        }
                    }
                }
                // 11 xx xx xx xx
                else if (bytes[0] == 0x11) {
                    sendOrders.removeFirst();
                    if (!sendDatasToDevice()) {
                        return UPDATE_FAILD;
                    }
                } else {
                    LogUtil.i(TAG, "存在错误，错误结果为:" + ParseUtil.byteArrayToHexString(bytes) + " 上次发送的命令是:" + ParseUtil.byteArrayToHexString(sendOrders.getFirst().content));
                    return UPDATE_FAILD;
                }
            }
            return UPDATE_ING;
        } else {
            return UPDATE_FAILD;
        }
    }

    /**
     * 发送数据到设备,顺便把进度回调给apk
     */
    private boolean sendDatasToDevice() {
        if (sendOrders != null && sendOrders.size() > 0) {
            byte[] content = sendOrders.getFirst().content;
            LogUtil.i(TAG, ">>>>>>>>>>发送(" + sendOrders.size() + "):" + ParseUtil.byteArrayToHexString(content));
            dfuUpdateService.sendDataToPedometer(content, sendOrders.getFirst().is1531Flag);
            if (iAboutUpgrade != null && max > 0) {
                iAboutUpgrade.curUpgradeProgress(max - sendOrders.size());
            }
            return true;
        }
        return false;
    }
}
