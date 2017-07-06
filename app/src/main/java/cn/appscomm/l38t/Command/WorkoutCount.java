package cn.appscomm.l38t.Command;

import com.appscomm.bluetooth.manage.GlobalVarManager;
import com.appscomm.bluetooth.protocol.command.base.BaseCommand;
import com.appscomm.bluetooth.protocol.command.base.CommandConstant;
import com.appscomm.bluetooth.utils.BaseUtil;

import cn.appscomm.l38t.utils.ParseUtil;

/**
 * Created by liucheng on 2017/6/12.
 */

public class WorkoutCount extends BaseCommand {
    public WorkoutCount(CommandResultCallback resultCallback) {
        super(resultCallback, CommandConstant.COMMANDCODE_GET_WORKOUT_COUNT, CommandConstant.ACTION_CHECK);
        byte[] content = new byte[]{(byte) 0};
        byte[] contentLen = BaseUtil.intToByteArray(content.length, 2);
        super.setContentLen(contentLen);
        super.setContent(content);
    }

    @Override
    public int parse80BytesArray(int len, byte[] bytes) {
        int ret = CommandConstant.RESULTCODE_ERROR;
        GlobalVarManager.getInstance().setWorkoutsCount(0);
        if (len == 2) {
            GlobalVarManager.getInstance().setWorkoutsCount((int) ParseUtil.bytesToLong(bytes, 0, 1));
            ret = CommandConstant.RESULTCODE_SUCCESS;
        }
        return ret;
    }
}
