package cn.appscomm.l38t.extensioncommand;

import com.appscomm.bluetooth.protocol.command.base.BaseCommand;
import com.appscomm.bluetooth.protocol.command.base.CommandConstant;
import com.appscomm.bluetooth.utils.BaseUtil;

import cn.appscomm.l38t.config.AccountConfig;

/**
 * Created by Bingo on 16/10/11.
 */

public class WriteUid extends BaseCommand {
    private final String TAG = getClass().getSimpleName();

    public WriteUid(CommandResultCallback resultCallback) {
        super(resultCallback, (byte) 0x95, CommandConstant.ACTION_SET);
        byte[] content = new byte[16];
        byte[] contentLen = BaseUtil.intToByteArray(content.length, 2);
        String userID = Long.parseLong(AccountConfig.getUserId() + "") + "";
        System.arraycopy(userID.getBytes(), 0, content, 0, userID.getBytes().length);

        super.setContentLen(contentLen);
        super.setContent(content);
    }


    @Override
    public int parse80BytesArray(int len, byte[] contents) {
        int ret = CommandConstant.RESULTCODE_ERROR;
        if (true) {
            ret = CommandConstant.RESULTCODE_SUCCESS;
        }
        return ret;
    }

    @Override
    public void praseResponseContent(byte[] respContent) {
        super.praseResponseContent(respContent);
    }
}
