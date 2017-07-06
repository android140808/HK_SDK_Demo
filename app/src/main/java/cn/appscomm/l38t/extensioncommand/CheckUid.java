package cn.appscomm.l38t.extensioncommand;

import com.appscomm.bluetooth.protocol.command.base.BaseCommand;
import com.appscomm.bluetooth.protocol.command.base.CommandConstant;
import com.appscomm.bluetooth.utils.BaseUtil;

import cn.appscomm.l38t.config.AccountConfig;

/**
 * Created by Bingo on 16/10/11.
 */

public class CheckUid extends BaseCommand {

    private final String TAG = getClass().getSimpleName();

    public CheckUid(CommandResultCallback resultCallback) {
        super(resultCallback, (byte) 0x95, CommandConstant.ACTION_CHECK);
        byte[] content = new byte[]{0x00};
        byte[] contentLen = BaseUtil.intToByteArray(content.length, 2);
        super.setContentLen(contentLen);
        super.setContent(content);
    }

    @Override
    public int parse80BytesArray(int len, byte[] contents) {
        int ret = CommandConstant.RESULTCODE_ERROR;
        if (len >= 1) {
            ret = CommandConstant.RESULTCODE_SUCCESS;
            if (compareUserID(BaseUtil.bytesToHexString(contents))) {
                //
            }else {
                // 
            }
        }
        return ret;
    }

    @Override
    public void praseResponseContent(byte[] respContent) {
        super.praseResponseContent(respContent);
    }


    /**
     * 对比 user id
     *
     * @param devicerUserID 设备已存在的UID
     * @return
     */
    private boolean compareUserID(String devicerUserID) {
        StringBuilder localUserIDHex = new StringBuilder();
        String[] charArray1 = {
                "00", "00", "00", "00",
                "00", "00", "00", "00",
                "00", "00", "00", "00",
                "00", "00", "00", "00"
        };
        // User ID
        String localUserID = Long.parseLong(AccountConfig.getUserId() + "") + "";

        if (localUserID.length() > 0) {
            char[] charArray = localUserID.toCharArray();
            for (int i = 0; i < charArray.length; i++) {
                charArray1[i] = "3" + charArray[i];
            }
        }
        for (String s : charArray1) {
            localUserIDHex.append(s);
        }

        return (localUserIDHex.toString()).equals(devicerUserID);
    }
}

