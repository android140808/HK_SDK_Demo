package cn.appscomm.l38t.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.constant.APPConstant;

/**
 * Created by Administrator on 2016/1/26.
 */
public class ParseUtil {

    private static final String TAG = ParseUtil.class.getSimpleName();
    private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = {"0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"};
    public static final int FIRST_DAY_OF_WEEK = Calendar.MONDAY; // Calendar.SUNDAY;
    public static final int SDF_TYPE_YMDHMS = 1;
    public static final int SDF_TYPE_YMD = 2;
    public static final int SDF_TYPE_YMDH = 3;
    public static final int SDF_TYPE_HM = 4;
    public static final int SDF_TYPE_Y = 5;
    public static final int SDF_TYPE_M = 6;
    public static final int SDF_TYPE_D = 7;
    public static final int SDF_TYPE_Z = 8;
    public static final int SDF_TYPE_MDY_1 = 9;
    public static final int SDF_TYPE_DMY = 10;
    public static final int SDF_TYPE_YMDTHMS = 11;
    public static final int SDF_TYPE_MMM = 12;
    public static final int SDF_TYPE_MMMMYYYY = 13;
    public static final int SDF_TYPE_Y_M_DTHMS = 14;
    public static final int SDF_TYPE_H_M = 15;
    public static final int SDF_TYPE_HMS = 16;
    public static final int SDF_TYPE_EMD = 17;
    public static final int SDF_TYPE_H = 18;
    public static final int SDF_TYPE_MDYHMS = 19;
    public static final int SDF_TYPE_HHM = 20;
    public static final int SDF_TYPE_YMDHMS1 = 21;
    public static final int SDF_TYPE_YMDHMS2 = 22;
    public static final int SDF_TYPE_YMDHM = 23;
    private static SimpleDateFormat sdf_ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdf_ymdh = new SimpleDateFormat("yyyy-MM-dd HH");
    private static SimpleDateFormat sdf_hm = new SimpleDateFormat("HH:mm");
    private static SimpleDateFormat sdf_y = new SimpleDateFormat("yyyy");
    private static SimpleDateFormat sdf_m = new SimpleDateFormat("MM");
    private static SimpleDateFormat sdf_d = new SimpleDateFormat("dd");
    private static SimpleDateFormat sdf_z = new SimpleDateFormat("ZZ");
    private static SimpleDateFormat sdf_emdy = new SimpleDateFormat("EEEE MMM dd'th' yyyy");
    private static SimpleDateFormat sdf_mdy = new SimpleDateFormat("MMM dd'th' yyyy");
    private static SimpleDateFormat sdf_dmy = new SimpleDateFormat("dd MMM yyyy");
    private static SimpleDateFormat sdf_ymd_china = new SimpleDateFormat("yyyy年MM月dd日");
    private static SimpleDateFormat sdf_emd = new SimpleDateFormat("EEEE MMM dd");
    private static SimpleDateFormat sdf_med = new SimpleDateFormat("EEEE dd MMM");
    private static SimpleDateFormat sdf_mdy_1 = new SimpleDateFormat("MM/dd/yy");
    private static SimpleDateFormat sdf_ymdthms = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    private static SimpleDateFormat sdf_mmm = new SimpleDateFormat("MMM");
    private static SimpleDateFormat sdf_mmmmm = new SimpleDateFormat("MMMMM");
    private static SimpleDateFormat sdf_mmmmyyyy = new SimpleDateFormat("MMMM yyyy");
    private static SimpleDateFormat sdf_y_m_dthms = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static SimpleDateFormat sdf_h_m = new SimpleDateFormat("HH 'h' mm 'min'");
    private static SimpleDateFormat sdf_hms = new SimpleDateFormat("HH:mm:ss");
    private static SimpleDateFormat sdf_h = new SimpleDateFormat("HH");
    private static SimpleDateFormat sdf_mdyhms = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
    private static SimpleDateFormat sdf_hhm = new SimpleDateFormat("HH'h'mm");
    private static SimpleDateFormat sdf_ymdhms1 = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    private static SimpleDateFormat sdf_ymdhms2 = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static SimpleDateFormat sdf_ymdhm = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * bytes转换为long，原理如下:
     * sum = (long) (bytes[0] & 0xff) + (long) ((bytes[1] & 0xff) << 8) +
     * (long) ((bytes[2] & 0xff) << 16) + (long) ((bytes[3] & 0xff) << 24) +
     * (long) ((bytes[4] & 0xff) << 32) + (long) ((bytes[5] & 0xff) << 40)……
     *
     * @param bytes 需要转换的bytes
     * @param start 开始索引
     * @param end   结束索引(包含)
     * @return 转换后的long
     */
    public static long bytesToLong(byte[] bytes, int start, int end) {
        if (start > end) {
            return -1;
        }
        long sum = 0;
        for (int i = start, bit = 0; i < end + 1; i++, bit += 8) {
            long temp = (long) (bytes[i] & 0xff) << bit;
            sum += temp;
        }
        return sum;
    }

    /**
     * int转换到byte[]
     *
     * @param integer  需要转换的int
     * @param byteSize byte[]数组的大小
     * @return 转换后的byte[]
     */
    public static byte[] intToByteArray(final int integer, int byteSize) {
        byte[] bytes = new byte[byteSize];
        for (int i = 0; i < byteSize; i++) {
            bytes[i] = (byte) ((integer >> (8 * i)) & 0xFF);
        }
        return bytes;
    }

    /**
     * 二进制转换到十六进制字符串
     *
     * @param bytes 二进制数组
     * @return 十六进制字符串
     */
    public static String byteArrayToHexString(byte[] bytes) {
        String result = "";
        String hex;
        for (int i = 0; i < bytes.length; i++) {
            hex = String.valueOf(hexStr.charAt((bytes[i] & 0xF0) >> 4));            //字节高4位
            hex += String.valueOf(hexStr.charAt(bytes[i] & 0x0F));                  //字节低4位
            result += hex + " ";
        }
        return result;
    }


    /**
     * 二进制字符串转到二进制数组
     *
     * @param binaryByteString 二进制字符串
     * @return 转换后二进制数组
     */
    public static byte[] byteStrToByteArray(String binaryByteString) {
        String[] binaryStr = binaryByteString.split(",");
        byte[] byteArray = new byte[binaryStr.length];
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = (byte) parse(binaryStr[i]);
        }
        return byteArray;
    }

    public static int parse(String str) {
        if (32 == str.length()) {
            str = "-" + str.substring(1);
            return -(Integer.parseInt(str, 2) + Integer.MAX_VALUE + 1);
        }
        return Integer.parseInt(str, 2);
    }

    /**
     * 二进制数组转到二进制字符串
     *
     * @param bArray 二进制数组
     * @return 二进制字符串
     */
    public static String byteArrayToByteStr(byte[] bArray) {
        String outStr = "";
        int pos = 0;
        for (byte b : bArray) {
            pos = (b & 0xF0) >> 4;              //高四位
            outStr += binaryArray[pos];
            pos = b & 0x0F;                     //低四位
            outStr += binaryArray[pos];
        }
        return outStr;
    }

    /**
     * 把时间戳转到设置的格式显示
     *
     * @param timeStamp 时间戳(毫秒/秒)
     * @param type      类型(例如:SDF_TYPE_YMDHMS)
     * @param isEightTZ 是否东8区
     * @return 转换后的时间
     */
    public static String timeStampToString(long timeStamp, int type, boolean isEightTZ) {
        SimpleDateFormat sdf;
        switch (type) {
            case SDF_TYPE_YMD:
                sdf = sdf_ymd;
                break;
            case SDF_TYPE_HM:
                sdf = sdf_hm;
                break;
            case SDF_TYPE_YMDH:
                sdf = sdf_ymdh;
                break;
            case SDF_TYPE_Y:
                sdf = sdf_y;
                break;
            case SDF_TYPE_M:
                sdf = sdf_m;
                break;
            case SDF_TYPE_D:
                sdf = sdf_d;
                break;
            case SDF_TYPE_MDY_1:
                sdf = sdf_mdy_1;
                break;
            case SDF_TYPE_YMDTHMS:
                sdf = sdf_ymdthms;
                break;
            case SDF_TYPE_MMM:
                sdf = sdf_mmm;
                break;
            case SDF_TYPE_MMMMYYYY:
                sdf = sdf_mmmmyyyy;
                break;
            case SDF_TYPE_Y_M_DTHMS:
                sdf = sdf_y_m_dthms;
                break;
            case SDF_TYPE_H_M:
                sdf = sdf_h_m;
                break;
            case SDF_TYPE_HMS:
                sdf = sdf_hms;
                break;
            case SDF_TYPE_H:
                sdf = sdf_h;
                break;
            case SDF_TYPE_MDYHMS:
                sdf = sdf_mdyhms;
                break;
            case SDF_TYPE_HHM:
                sdf = sdf_hhm;
                break;
            case SDF_TYPE_YMDHMS1:
                sdf = sdf_ymdhms1;
                break;
            case SDF_TYPE_YMDHMS2:
                sdf = sdf_ymdhms2;
                break;
            case SDF_TYPE_YMDHM:
                sdf = sdf_ymdhm;
                break;
            case SDF_TYPE_DMY:
                sdf = sdf_dmy;
                break;

            default:
            case SDF_TYPE_YMDHMS:
                sdf = sdf_ymdhms;
                break;
        }
        int len = (timeStamp + "").length();
        sdf.setTimeZone(isEightTZ ? TimeZone.getTimeZone("GMT+8") : TimeZone.getDefault());
        return sdf.format(len > 10 ? timeStamp : (timeStamp * 1000L));
    }

    public static String timeStampToString(long timeStamp, int type) {
        return timeStampToString(timeStamp, type, false);
    }

    /**
     * 获取时间戳
     *
     * @param date 日期
     * @param type 时间类型
     * @return 时间戳
     */
    public static long getTimestamp(String date, int type) {
        long timestamp = 0;
        try {
            SimpleDateFormat sdf = sdf_ymdhms;
            switch (type) {
                case SDF_TYPE_YMD:
                    sdf = sdf_ymd;
                    break;
                case SDF_TYPE_Y_M_DTHMS:
                    sdf = sdf_y_m_dthms;
                    break;
                case SDF_TYPE_YMDTHMS:
                    sdf = sdf_ymdthms;
                    break;
                case SDF_TYPE_YMDHMS:
                    sdf = sdf_ymdhms;
                    break;
            }
            timestamp = sdf.parse(date).getTime();
        } catch (Exception e) {
        }
        return timestamp;
    }

    public static long get8TZTimestamp(String date, int type) {
        long timestamp = 0;
        try {
            SimpleDateFormat sdf = sdf_ymdhms;
            switch (type) {
                case SDF_TYPE_YMD:
                    sdf = sdf_ymd;
                    break;
                case SDF_TYPE_Y_M_DTHMS:
                    sdf = sdf_y_m_dthms;
                    break;
                case SDF_TYPE_YMDTHMS:
                    sdf = sdf_ymdthms;
                    break;
                case SDF_TYPE_YMDHMS:
                    sdf = sdf_ymdhms;
                    break;
            }
            SimpleDateFormat tempSDF = (SimpleDateFormat) sdf.clone();
            tempSDF.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            timestamp = tempSDF.parse(date).getTime();
        } catch (Exception e) {
        }
        return timestamp;
    }

    /**
     * 东8区的时间戳转换为时间+时区,该时区是手环的时区,常用于上传数据
     *
     * @param timestamp 时间戳
     * @param isAddTZ   是否添加时区字段
     * @return 时间+时区
     */
    public static String eightTZTimeStampToStringTZ(long timestamp, boolean isAddTZ) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        int len = (timestamp + "").length();
        timestamp = len > 10 ? timestamp : (timestamp * 1000L);
        String tz = sdf_z.format(timestamp);
        return sdf.format(timestamp) + (isAddTZ ? tz : "");
    }

    /**
     * 时间+时区转换为时间戳
     *
     * @param str
     * @return
     */
    public static long stringTZTotimeStamp(String str) {
        long timestamp = 0L;
        try {
            str = str.substring(0, 19);
            timestamp = sdf_y_m_dthms.parse(str).getTime();
        } catch (Exception e) {
        }
        return timestamp;
    }

    /**
     * 本地时区的时间戳转换为时间+时区
     *
     * @param timestamp 时间戳
     * @param isAddTZ   是否添加时区字段
     * @return 时间+时区
     */
    public static String timeStampToStringTZ(long timestamp, boolean isAddTZ) {
        int len = (timestamp + "").length();
        timestamp = len > 10 ? timestamp : (timestamp * 1000L);
        String tz = sdf_z.format(timestamp);
        return sdf_y_m_dthms.format(timestamp) + (isAddTZ ? tz : "");
    }

    /**
     * crc16算法2
     *
     * @param bytes
     * @return
     */
    public static byte[] crc16(byte[] bytes) {
        byte[] crcBytes = new byte[2];
        int len = bytes.length;
        int crc = 0xFFFF;
        for (int i = 0; i < len; i++) {
            crc = (int) (((short) ((crc >> 8) & 0xFF) | (crc << 8)) & 0xFFFF);
            crc ^= (short) (bytes[i] & 0xFF);
            crc ^= (int) (((short) (crc & 0xFF) >> 4) & 0xFFFF);
            crc ^= (int) (((crc << 8) << 4) & 0xFFFF);
            crc ^= (int) (((crc & 0xff) << 4) << 1 & 0xFFFF);
        }
        crcBytes[0] = (byte) (crc & 0xFF);
        crcBytes[1] = (byte) ((crc >> 8) & 0xFF);
        return crcBytes;
    }

    /**
     * 解压zip
     *
     * @param path 路径
     */
    public static boolean unZip(String path) {
        try {
            String Parent = GlobalApp.getAppContext().getFilesDir().getAbsolutePath();                // 输出路径（文件夹目录）
            File file = new File(path);
//            if (!file.exists()) {
//                file.mkdirs();
//            }
            upZipFile(new File(path), Parent);
//            upZipFile(file, Parent);
            return true;
        } catch (Exception e) {
            LogUtil.i(TAG, "出错了===================" + e.toString());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解压缩含有文件夹的压缩文件
     *
     * @param zipFile    需要解压的文件
     * @param folderPath 解压出来之后放置的地址
     * @throws ZipException
     * @throws IOException
     */
    public static void upZipFile(File zipFile, String folderPath) throws ZipException, IOException {
        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            // 创建目标目录
            desDir.mkdirs();
        }
        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());
            String str = folderPath + File.separator + entry.getName();
            if (entry.isDirectory()) {
                str = new String(str.getBytes("8859_1"), "GB2312");
                File folder = new File(str);
                folder.mkdirs();
            } else {
                InputStream is = zf.getInputStream(entry);
                // 转换编码，避免中文时乱码
                str = new String(str.getBytes("8859_1"), "GB2312");
                File desFile = new File(str);
                LogUtil.i(TAG, "文件名称是" + zipFile.getName() + "解压之后的名称" + desFile.getName());
                if (!desFile.exists()) {
                    // 创建目标文件
                    desFile.createNewFile();
                }
                OutputStream os = new FileOutputStream(desFile);
                byte[] buffer = new byte[1024];
                int realLength;
                while ((realLength = is.read(buffer)) > 0) {
                    os.write(buffer, 0, realLength);
                    os.flush();
                }
                LogUtil.i(TAG, "解压之后的文件大小" + desFile.length() + "解压之后的名称" + desFile.getName());
                is.close();
                os.close();
            }
        }
    }

    // 删除目录
    public static void clearDirectory(File delFile) {
        try {
            File[] files = delFile.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    clearDirectory(file);
                }
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 删除升级文件
    public static void delUpdateFile() {
        try {
            File[] files = APPConstant.FILE_FIRMWARE.listFiles();
            for (File file : files) {
                if (file.getName().contains("application") || file.getName().contains("firmware")) {
                    if (file.isDirectory()) {
                        clearDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 检查固件是否存在本地
    public static boolean checkFirmwareIsExist() {
        if (!TextUtils.isEmpty(APPConstant.nordic_server_version)) {
            File file = new File(APPConstant.FILE_FIRMWARE, "firmware.zip");
            if (file.exists()) {
                ParseUtil.unZip(file.getAbsolutePath());
                file = new File(APPConstant.FILE_FIRMWARE, APPConstant.nordic_server_filename);
                if (file.exists()) {
                    LogUtil.i(TAG, "本地有固件,路径:" + file.getAbsolutePath());
                    ParseUtil.rename(file, APPConstant.FILE_APPLICATION_ZIP);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 文件改名
     *
     * @param oldFile 旧文件
     * @param newFile 新文件
     */
    public static void rename(File oldFile, File newFile) {
        if (newFile.exists()) {
            if (newFile.isDirectory()) {
                clearDirectory(newFile);
            } else {
                newFile.delete();
            }
        }
        oldFile.renameTo(newFile);
    }
}
