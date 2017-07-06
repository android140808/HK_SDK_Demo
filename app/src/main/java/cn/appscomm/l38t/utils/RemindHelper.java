package cn.appscomm.l38t.utils;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/5 17:10
 */
public class RemindHelper {
    // 二进制字符串转换成2进制数组 byte[]: "0111111" -->byte[]再换int类型是：127
    public static byte[] binaryStr2Bytes(String binaryByteString) {
        //假设binaryByte 是01，10，011，00以，分隔的格式的字符串
        String[] binaryStr = binaryByteString.split(",");
        byte[] byteArray = new byte[binaryStr.length];
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = (byte) parse(binaryStr[i]);
        }
        return byteArray;
    }

    public static int parse(String str) {
        //32位 为负数
        if (32 == str.length()) {
            str = "-" + str.substring(1);
            return -(Integer.parseInt(str, 2) + Integer.MAX_VALUE + 1);
        }
        return Integer.parseInt(str, 2);
    }

    // int 类型转byte[] 2进制数组
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

}
