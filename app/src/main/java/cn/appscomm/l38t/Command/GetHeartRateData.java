package cn.appscomm.l38t.Command;

import com.appscomm.bluetooth.bean.HeartData;
import com.appscomm.bluetooth.manage.GlobalDataManager;
import com.appscomm.bluetooth.protocol.command.base.BaseIndexCommand;
import com.appscomm.bluetooth.protocol.command.base.CommandConstant;
import com.appscomm.bluetooth.protocol.command.data.GetHeartData;
import com.appscomm.bluetooth.utils.BaseUtil;
import com.appscomm.bluetooth.utils.BluetoothLogger;
import com.appscomm.bluetooth.utils.DateUtil;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by liucheng on 2017/6/8.
 */

public class GetHeartRateData extends BaseIndexCommand {
    private static final String TAG = GetHeartData.class.getSimpleName();
    private int getIndex = 0;
    private byte queryType;//查询类型

    public GetHeartRateData(CommandResultCallback resultCallback, int index, int resultCount) {
        super(resultCallback, CommandConstant.COMMANDCODE_GET_HEART_DATA, index, resultCount);
        getIndex = index;
    }

    public byte getQueryType() {
        return HeartData.TYPE_HEART_RATE;
    }

    @Override
    public int parse80BytesArray(int len, byte[] contents) {
        super.parse80BytesArray(len, contents);
        int ret = CommandConstant.RESULTCODE_ERROR;
        if (len == 7) {
            int index = (int) BaseUtil.bytesToLong(contents, 0, 1); // 索引值
            long timeStamp = BaseUtil.bytesToLong(contents, 2, 5);  // 时间戳,0时区的
            long localTimeStamp = timeStamp;
            if (isNeedChangeTime) {
                localTimeStamp = DateUtil.getLocalTimeStamp(timeStamp);//做时区处理
            }
//            byte[] valueBytes = new byte[4];
//            System.arraycopy(contents, 7, valueBytes, 0, valueBytes.length);
            int heartRateValue = 0, moodValue = 0, tiredValue = 0;
            heartRateValue = contents[6] & 0xff;
//            heartRateValue = BaseUtil.byteArrayToInt(valueBytes, 0, false);
//            if (type == HeartData.TYPE_HEART_RATE) {
//            } else if (type == HeartData.TYPE_MOOD_TIRED) {
//                moodValue = BaseUtil.byteArrayToInt(new byte[]{valueBytes[0], valueBytes[1]});
//                tiredValue = BaseUtil.byteArrayToInt(new byte[]{valueBytes[2], valueBytes[3]});
//            }
            BluetoothLogger.d(TAG, "查询返回 :数据(索引号:" + index + " 设备时间撮(" + timeStamp + "),本地时间=" + DateUtil.dateToSec(DateUtil.timeStampToDate(localTimeStamp * 1000))
                    + " 类型:heartRateValue:" + heartRateValue + " tiredValue:" + tiredValue + " moodValue:" + moodValue + ")");

            if (GlobalDataManager.getInstance().getHeartDatas() == null || GlobalDataManager.getInstance().getHeartDatas().size() == 0 || index == 1) {
                GlobalDataManager.getInstance().setHeartDatas(new LinkedList<HeartData>());
                GlobalDataManager.getInstance().setIndexsResendCommand(null);
            }
            HeartData heartData = new HeartData();
            heartData.type = HeartData.TYPE_HEART_RATE;
            heartData.id = index;
            heartData.time_stamp = timeStamp;// 时间撮(秒)
            heartData.local_time_stamp = localTimeStamp;// 时间撮(秒)
            heartData.heartRate_value = heartRateValue;
            heartData.mood_value = moodValue;
            heartData.tired_value = tiredValue;

            GlobalDataManager.getInstance().getHeartDatas().add(heartData);
            ret = CommandConstant.RESULTCODE_CONTINUE_RECEIVING;
            // 如果GlobalDataManager.getInstance().getIndexsResendCommand()为null 或 大小为0，则说明是获取全部索引的命令
            if (GlobalDataManager.getInstance().getIndexsResendCommand() == null || GlobalDataManager.getInstance().getIndexsResendCommand().size() == 0) {
                if (index == resultCount) { // 是否到达最后一包 : 在收到最后一包数据的情况下(如果没有收到最后一包，则需要超时判断有没有接收到最后一包了)
                    if (GlobalDataManager.getInstance().getHeartDatas().size() != resultCount) { // 获取到的数据和总数量值不相同
                        BluetoothLogger.d(TAG, "存在数据丢失,共" + resultCount + "条数据 接收到的数量是" + GlobalDataManager.getInstance().getHeartDatas().size());
                        if ((resultCount - GlobalDataManager.getInstance().getHeartDatas().size()) > 5) { // 如果丢失的包超过5条，则需要重新获取一次全部的数据
                            ret = CommandConstant.RESULTCODE_RE_SEND;
                        } else {
                            ArrayList<Integer> indexsAlreadyGet = new ArrayList<Integer>();
                            GlobalDataManager.getInstance().setIndexsResendCommand(new LinkedList<Integer>());
                            for (HeartData sd : GlobalDataManager.getInstance().getHeartDatas()) {
                                indexsAlreadyGet.add(sd.id);
                            }
                            for (int i = 1; i < resultCount + 1; i++) {
                                if (!indexsAlreadyGet.contains(i)) {
                                    GlobalDataManager.getInstance().getIndexsResendCommand().addLast(i);
                                }
                            }
                            ret = CommandConstant.RESULTCODE_INDEXS_COMMAND;
                        }
                    } else {
                        BluetoothLogger.d(TAG, "获取完所有数据!!!");
                        ret = CommandConstant.RESULTCODE_SUCCESS;
                    }
                } else if (getIndex != 0 && getIndex == index) {
                    BluetoothLogger.d(TAG, "获取完索引号" + getIndex + "的数据!!!");
                    ret = CommandConstant.RESULTCODE_SUCCESS;
                }
            }
            // 接收单独发送索引号的命令解析
            else {
                BluetoothLogger.d(TAG, ":这条是根据索引号获取的,索引号为:" + index);
                GlobalDataManager.getInstance().getIndexsResendCommand().remove(index);
                if (GlobalDataManager.getInstance().getIndexsResendCommand().size() == 0) { // 单独根据索引号发送的命令已全部接收完
                    ret = CommandConstant.RESULTCODE_SUCCESS;
                } else { // 需要继续获取
                    ret = CommandConstant.RESULTCODE_CONTINUE_RECEIVING;
                }
            }
        }
        return ret;
    }
}
