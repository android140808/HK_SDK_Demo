package cn.appscomm.l38t.Command;

import com.appscomm.bluetooth.bean.RemindData;
import com.appscomm.bluetooth.manage.GlobalDataManager;
import com.appscomm.bluetooth.protocol.command.base.BaseIndexCommand;
import com.appscomm.bluetooth.protocol.command.base.CommandConstant;
import com.appscomm.bluetooth.utils.BaseUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import cn.appscomm.l38t.utils.LogUtil;

/**
 * Created by liucheng on 2016/10/11.
 */
public class RemindSettingColor extends BaseIndexCommand {
    private static final String TAG = RemindSettingColor.class.getSimpleName();
    private byte opeartion;
    private int getIndex = 0;


    public RemindSettingColor(CommandResultCallback iResultCallback, int count) {
        super(iResultCallback, CommandConstant.COMMANDCODE_REMIND_SETTING, CommandConstant.ACTION_CHECK);
        byte[] contentLen = BaseUtil.intToByteArray(1, 2);
        byte[] content = new byte[]{0};
        getIndex = count;
        super.setContent(content);
        super.setContentLen(contentLen);
    }

    public RemindSettingColor(CommandResultCallback iResultCallback, int len, byte operation, byte remindType, byte remindHour, byte remindMin, byte remindCycle, byte remindSwitchStatus, byte[] remindContent, byte remindType1, byte remindHour1, byte remindMin1, byte remindCycle1, byte remindSwitchStatus1) {
        super(iResultCallback, CommandConstant.COMMANDCODE_REMIND_SETTING, CommandConstant.ACTION_SET);
        byte[] contentLen = BaseUtil.intToByteArray(len, 2);
        byte[] content = new byte[len];
        this.opeartion = operation;
        if (operation == 3) {
            content[0] = operation;
            LogUtil.i(TAG, "设置 : 准备设置(全部删除)!!!");
        } else {
            content[0] = operation;
            content[1] = remindType;
            content[2] = remindHour;
            content[3] = remindMin;
            content[4] = remindCycle;
            content[5] = remindSwitchStatus;
            switch (operation) {
                case 0:
                    if (remindType == CommandConstant.REMINDTYPE_CUSTOM && remindContent != null) {
                        System.arraycopy(remindContent, 0, content, 6, remindContent.length);
                    }

                    LogUtil.i(TAG, "设置 : 准备设置(新增)!!!");
                    break;
                case 1:
                    content[6] = remindType1;
                    content[7] = remindHour1;
                    content[8] = remindMin1;
                    content[9] = remindCycle1;
                    content[10] = remindSwitchStatus1;
                    if (remindType == CommandConstant.REMINDTYPE_CUSTOM && remindContent != null) {
                        System.arraycopy(remindContent, 0, content, 11, remindContent.length);
                    }

                    LogUtil.i(TAG, "设置 : 准备设置(修改)!!!");
                    break;
                case 2:
                    LogUtil.i(TAG, "设置 : 准备设置(单条删除)!!!");
            }
        }
        super.setContentLen(contentLen);
        super.setContent(content);
    }

    public int parse80BytesArray(int len, byte[] contents) {
        byte ret = CommandConstant.RESULTCODE_ERROR;
        if (len == 2) {
            ret = contents[1];
        }
        if (len >= 6) {
            int index = contents[0] & 255;
            int remindType = contents[1] & 255;
            int remindHour = contents[2] & 255;
            int remindMin = contents[3] & 255;
            byte remindCycle = contents[4];
            int remindSwitchStatus = contents[5] & 255;
            String remindContent = "";
            if (len > 6) {
                try {
                    remindContent = new String(contents, 6, len - 6, "UTF-8");
                } catch (Exception var15) {
                    ;
                }
            }

            if (GlobalDataManager.getInstance().getRemindDatas() == null || GlobalDataManager.getInstance().getRemindDatas().size() == 0 || index == 1) {
                GlobalDataManager.getInstance().setRemindDatas(new LinkedList());
            }

            RemindData remindData = new RemindData();
            remindData.remind_id = index;
            remindData.remind_type = remindType;
            remindData.remind_time_hours = remindHour;
            remindData.remind_time_minutes = remindMin;
            remindData.remind_week = BaseUtil.bytes2BinaryStr(new byte[]{remindCycle}).substring(1, 8);
            remindData.remind_set_ok = remindSwitchStatus;
            LogUtil.i(TAG, "remindContent.len :" + remindContent.length());
            if (remindContent != "") {
                remindData.remind_text = remindContent;
            }

            if (remindHour > 24 || remindHour < 0 || remindMin < 0 || remindMin > 60) {
                LogUtil.i(TAG, "获取提醒数据异常!!");
                return -1;
            }

            GlobalDataManager.getInstance().getRemindDatas().add(remindData);
            ret = CommandConstant.RESULTCODE_CONTINUE_RECEIVING;
            LogUtil.i(TAG, "提醒:索引值(" + index + ") 类型(" + remindType + ") 时(" + remindHour + ") 分(" + remindMin + ") 周期(" + remindData.remind_week + ") 开关(" + remindData.remind_set_ok + ") 自定义内容(" + remindContent + ")");
            if (GlobalDataManager.getInstance().getIndexsResendCommand() != null && GlobalDataManager.getInstance().getIndexsResendCommand().size() != 0) {
                LogUtil.i(TAG, "提醒:这条是根据索引号获取的,索引号为:" + index);
                GlobalDataManager.getInstance().getIndexsResendCommand().remove(index);
                if (GlobalDataManager.getInstance().getIndexsResendCommand().size() == 0) {
                    ret = CommandConstant.RESULTCODE_SUCCESS;
                } else {
                    ret = CommandConstant.RESULTCODE_CONTINUE_RECEIVING;
                }
            } else if (index == this.resultCount) {
                if (GlobalDataManager.getInstance().getRemindDatas().size() != this.resultCount) {
                    LogUtil.i(TAG, "提醒:存在数据丢失,共" + this.resultCount + "条数据 接收到的数量是" + GlobalDataManager.getInstance().getRemindDatas().size());
                    if (this.resultCount - GlobalDataManager.getInstance().getRemindDatas().size() > 5) {
                        ret = CommandConstant.RESULTCODE_RE_SEND;
                    } else {
                        ArrayList indexsAlreadyGet = new ArrayList();
                        GlobalDataManager.getInstance().setIndexsResendCommand(new LinkedList());
                        Iterator i = GlobalDataManager.getInstance().getRemindDatas().iterator();

                        while (i.hasNext()) {
                            RemindData sd = (RemindData) i.next();
                            indexsAlreadyGet.add(Integer.valueOf(sd.remind_id));
                        }

                        for (int var16 = 1; var16 < this.resultCount + 1; ++var16) {
                            if (!indexsAlreadyGet.contains(Integer.valueOf(var16))) {
                                GlobalDataManager.getInstance().getIndexsResendCommand().addLast(Integer.valueOf(var16));
                            }
                        }
                        ret = CommandConstant.RESULTCODE_INDEXS_COMMAND;
                    }
                } else {
                    LogUtil.i(TAG, "获取完所有提醒数据!!!");
                    ret = CommandConstant.RESULTCODE_SUCCESS;
                }
            } else if (this.getIndex != 0 && this.getIndex == index) {
                LogUtil.i(TAG, "获取完索引号" + this.getIndex + "的数据!!!");
                ret = CommandConstant.RESULTCODE_SUCCESS;
            }
        }
        return ret;
    }

    public byte getOpeartion() {
        return this.opeartion;
    }


}
