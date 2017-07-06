package cn.appscomm.l38t.Command;

import com.appscomm.bluetooth.protocol.command.base.BaseCommand;
import com.appscomm.bluetooth.protocol.command.base.CommandConstant;
import com.appscomm.bluetooth.utils.BaseUtil;

/**
 * Created by liucheng on 2017/6/12.
 */

public class DeleteWorkoutData extends BaseCommand {
    public DeleteWorkoutData(CommandResultCallback resultCallback) {
        super(resultCallback, CommandConstant.COMMANDCODE_DELETE_WORKOUT, CommandConstant.ACTION_SET);
        byte[] content = new byte[]{(byte) 0};
        byte[] contentLen = BaseUtil.intToByteArray(content.length, 2);
        super.setContent(content);
        super.setContentLen(contentLen);
    }

    @Override
    public int parse80BytesArray(int len, byte[] contents) {
        return 0;
    }
}
