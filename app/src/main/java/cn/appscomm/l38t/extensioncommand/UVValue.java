package cn.appscomm.l38t.extensioncommand;

import com.appscomm.bluetooth.manage.GlobalVarManager;
import com.appscomm.bluetooth.protocol.command.base.BaseCommand;
import com.appscomm.bluetooth.protocol.command.base.CommandConstant;
import com.appscomm.bluetooth.utils.BaseUtil;

/**
 * Created by zhaozx on 16/10/18.
 * desc:
 */

public class UVValue  extends BaseCommand {

    public UVValue(CommandResultCallback resultCallback) {
        super(resultCallback, CommandConstant.COMMANDCODE_GET_ULTRAVIOLET_RAYS_DATA, CommandConstant.ACTION_CHECK);
        byte[] content = new byte[]{0x01};
        byte[] contentLen = BaseUtil.intToByteArray(content.length, 2);
        super.setContentLen(contentLen);
        super.setContent(content);
    }


    @Override
    public int parse80BytesArray(int len, byte[] contents) {
        int ret = CommandConstant.RESULTCODE_ERROR;
        if (len == 6) {
            int uv = (contents[4]&0xFF);
            GlobalVarManager.getInstance().setUvValue(uv);
            ret = CommandConstant.RESULTCODE_SUCCESS;
        }
        return ret;
    }

    @Override
    public void praseResponseContent(byte[] respContent) {
        super.praseResponseContent(respContent);
    }
}
