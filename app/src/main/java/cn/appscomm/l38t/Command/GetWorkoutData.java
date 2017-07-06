package cn.appscomm.l38t.Command;

import com.appscomm.bluetooth.bean.Workout;
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
 * Created by liucheng on 2017/6/12.
 */

public class GetWorkoutData extends BaseIndexCommand {

    private static final String TAG = GetHeartData.class.getSimpleName();
    private int getIndex = 0;

    public GetWorkoutData(CommandResultCallback resultCallback, int index, int resultCount) {
        super(resultCallback, CommandConstant.COMMANDCODE_GET_WORKOUT_DATA, index, resultCount);
        getIndex = index;
    }

    @Override
    public int parse80BytesArray(int len, byte[] contents) {
        super.parse80BytesArray(len, contents);
        int ret = CommandConstant.RESULTCODE_ERROR;
        if (len == 23) {
            int index = (int) BaseUtil.bytesToLong(contents, 0, 1); // 索引值
            long timeStamp = BaseUtil.bytesToLong(contents, 2, 5);  // 时间戳,0时区的
            long localTimeStamp = timeStamp;
            if (isNeedChangeTime) {
                localTimeStamp = DateUtil.getLocalTimeStamp(timeStamp);//做时区处理
            }
            int step = (int) BaseUtil.bytesToLong(contents, 6, 9); // 步数
            int calorie = (int) BaseUtil.bytesToLong(contents, 10, 13); // 卡路里
            int distan = (int) BaseUtil.bytesToLong(contents, 14, 17); // 距离
            int timeDuration = (int) BaseUtil.bytesToLong(contents, 18, 21); // 运动时长
            int heartData = contents[22] & 0xff; // 心率

            BluetoothLogger.d(TAG, "查询返回 :数据(索引号:" + index + " 设备时间撮(" + timeStamp + "),本地时间=" + DateUtil.dateToSec(DateUtil.timeStampToDate(localTimeStamp * 1000))
                    + " 类型:步数:" + step + " 卡路里:" + calorie + " 距离:" + distan + " 运动时长:" + timeDuration + " 心率:" + heartData + ")");

            if (GlobalDataManager.getInstance().getWorkoutDatas() == null || GlobalDataManager.getInstance().getWorkoutDatas().size() == 0 || index == 1) {
                GlobalDataManager.getInstance().setWorkoutDatas(new LinkedList<Workout>());
                GlobalDataManager.getInstance().setIndexsResendCommand(null);
            }
            Workout workout = new Workout();
            workout.id = index;
            workout.time = localTimeStamp;
            workout.step = step;
            workout.calorie = calorie;
            workout.distance = distan;
            workout.timeDuration = timeDuration;
            workout.heartRate = heartData;
            GlobalDataManager.getInstance().getWorkoutDatas().add(workout);
            ret = CommandConstant.RESULTCODE_CONTINUE_RECEIVING;
            // 如果GlobalDataManager.getInstance().getIndexsResendCommand()为null 或 大小为0，则说明是获取全部索引的命令
            if (GlobalDataManager.getInstance().getIndexsResendCommand() == null || GlobalDataManager.getInstance().getIndexsResendCommand().size() == 0) {
                if (index == resultCount) { // 是否到达最后一包 : 在收到最后一包数据的情况下(如果没有收到最后一包，则需要超时判断有没有接收到最后一包了)
                    if (GlobalDataManager.getInstance().getWorkoutDatas().size() != resultCount) { // 获取到的数据和总数量值不相同
                        BluetoothLogger.d(TAG, "存在数据丢失,共" + resultCount + "条数据 接收到的数量是" + GlobalDataManager.getInstance().getWorkoutDatas().size());
                        if ((resultCount - GlobalDataManager.getInstance().getWorkoutDatas().size()) > 5) { // 如果丢失的包超过5条，则需要重新获取一次全部的数据
                            ret = CommandConstant.RESULTCODE_RE_SEND;
                        } else {
                            ArrayList<Integer> indexsAlreadyGet = new ArrayList<Integer>();
                            GlobalDataManager.getInstance().setIndexsResendCommand(new LinkedList<Integer>());
                            for (Workout wo : GlobalDataManager.getInstance().getWorkoutDatas()) {
                                indexsAlreadyGet.add(wo.id);
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
