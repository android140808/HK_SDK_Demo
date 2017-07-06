package cn.appscomm.l38t.Command;

import com.appscomm.bluetooth.manage.GlobalVarManager;
import com.appscomm.bluetooth.protocol.command.base.BaseCommand;
import com.appscomm.bluetooth.protocol.command.base.CommandConstant;
import com.appscomm.bluetooth.utils.BaseUtil;

import cn.appscomm.netlib.bean.friends.InviteFriend;

/**
 * Created by liucheng on 2017/2/16.
 */

public class GetRemindCount extends BaseCommand {
    public GetRemindCount(CommandResultCallback resultCallback) {
        super(resultCallback, CommandConstant.COMMANDCODE_REMIND_COUNT, CommandConstant.ACTION_CHECK);
        byte[] content = new byte[]{(byte) 0};
        byte[] contentLen = BaseUtil.intToByteArray(content.length, 2);
        super.setContentLen(contentLen);
        super.setContent(content);
    }

    @Override
    public int parse80BytesArray(int len, byte[] bytes) {
        int ret=CommandConstant.RESULTCODE_ERROR;
        GlobalVarManager.getInstance().setRemindCount(0);
        if (len==1){
            GlobalVarManager.getInstance().setRemindCount(bytes[0]);
            ret=CommandConstant.RESULTCODE_SUCCESS;
        }
        return ret;
    }
}
