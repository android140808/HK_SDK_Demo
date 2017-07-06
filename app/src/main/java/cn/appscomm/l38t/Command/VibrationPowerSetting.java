package cn.appscomm.l38t.Command;

import com.appscomm.bluetooth.manage.GlobalVarManager;
import com.appscomm.bluetooth.protocol.command.base.BaseCommand;
import com.appscomm.bluetooth.protocol.command.base.CommandConstant;

import cn.appscomm.l38t.utils.ParseUtil;

/**
 * Created by liucheng on 2017/6/19.
 */

public class VibrationPowerSetting extends BaseCommand {

    /**
     * 查询震动强度
     * 构造函数(0x70)
     *
     * @param iResultCallback
     */
    public VibrationPowerSetting(CommandResultCallback iResultCallback) {
        super(iResultCallback, CommandConstant.COMMANDCODE_VIBRATION, CommandConstant.ACTION_CHECK);
        byte[] contentLen = ParseUtil.intToByteArray(1, 2);
        byte[] content = new byte[]{0x00};
        super.setContentLen(contentLen);
        super.setContent(content);
    }

    /**
     * 设置震动强度
     * 构造函数(0x71)
     *
     * @param iResultCallback
     * @param len             内容长度
     */
    public VibrationPowerSetting(CommandResultCallback iResultCallback, int len, int top) {
        super(iResultCallback, CommandConstant.COMMANDCODE_VIBRATION, CommandConstant.ACTION_SET);
        byte[] contentLen = ParseUtil.intToByteArray(len, 2);
        byte[] content = ParseUtil.intToByteArray(top, 1);
        super.setContentLen(contentLen);
        super.setContent(content);
    }

    /**
     */
    @Override
    public int parse80BytesArray(int len, byte[] contents) {
        int ret = CommandConstant.RESULTCODE_ERROR;
        GlobalVarManager.getInstance().setVibrationPower(0);
        if (len == 1) {
            GlobalVarManager.getInstance().setVibrationPower(contents[0]);
            ret = CommandConstant.RESULTCODE_SUCCESS;
        }
        return ret;
    }

}
