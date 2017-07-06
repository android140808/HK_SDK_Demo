package cn.appscomm.l38t.Command;

import com.appscomm.bluetooth.manage.GlobalVarManager;
import com.appscomm.bluetooth.protocol.command.base.BaseCommand;
import com.appscomm.bluetooth.protocol.command.base.CommandConstant;
import com.appscomm.bluetooth.utils.BaseUtil;

/**
 * Created by liucheng on 2017/6/9.
 */

public class HeartRateLimit extends BaseCommand {
    public static final int LOW_LIMIT = 30;//心率报警可以设置的最低值
    public static final int HIGH_LIMIT = 220;//心率报警可以设置的最高值

    public HeartRateLimit(CommandResultCallback resultCallback) {
        super(resultCallback, CommandConstant.COMMANDCODE_LIMIT_HEART_RATE, CommandConstant.ACTION_CHECK);
        byte[] content = new byte[]{(byte) 0};
        byte[] contentLen = BaseUtil.intToByteArray(content.length, 2);
        super.setContentLen(contentLen);
        super.setContent(content);
    }

    public HeartRateLimit(CommandResultCallback resultCallback, byte high, byte low, boolean enable) {
        super(resultCallback, CommandConstant.COMMANDCODE_LIMIT_HEART_RATE, CommandConstant.ACTION_SET);
        byte[] content = new byte[]{high, low, (byte) (enable ? 0x01 : 0x00)};
        byte[] contentLen = BaseUtil.intToByteArray(content.length, 2);
        super.setContentLen(contentLen);
        super.setContent(content);
    }


    @Override
    public int parse80BytesArray(int len, byte[] bytes) {
        int ret = CommandConstant.RESULTCODE_ERROR;
        if (len == 3) {
            GlobalVarManager.getInstance().setHeartHighLimit(bytes[0] & 0xff);
            GlobalVarManager.getInstance().setHeartLowLimit(bytes[1] & 0xff);
            GlobalVarManager.getInstance().setHeartLimitEnable(bytes[2] == 0 ? false : true);
            ret = CommandConstant.RESULTCODE_SUCCESS;
        }
        return ret;
    }
}
