package cn.appscomm.l38t.eventbus;

import cn.appscomm.l38t.eventbus.base.EventBusMessage;

/**
 * Created by Administrator on 2016/8/31.
 */
public class GlobalEvent extends EventBusMessage {

    public static final int EVENBUS_SIGNAL_CODE_SYNC_NOW = 1000;//立马同步设备信息
    public static final int EVENBUS_SIGNAL_CODE_SYNC_FAILED = 1001;//同步设备信息失败
    public static final int EVENBUS_SIGNAL_CODE_SYNC_REFRSH = 1002;//同步设备信息进行界面刷新
    public static final int EVENBUS_SIGNAL_CODE_SYNC_END = 1003;//同步设备信息结束
    public static final int EVENBUS_SIGNAL_CODE_GOAL_CHANGE_REFRSH = 1004;//目标修改进行界面刷新
    public static final int EVENBUS_SIGNAL_CODE_UNIT_CHANGE_REFRSH = 1005;//单位修改进行界面刷新
    public static final int EVENBUS_SIGNAL_CODE_DEVICE_DISCONNECTED = 1006;//设备断开进行界面刷新

    public static final int EVENBUS_SIGNAL_CODE_DEVICE_BOUND_BY_OTHER = 1007;//设备已经被他人绑定
    public static final int EVENBUS_SIGNAL_CODE_DEVICE_CONNECTED = 1008;//设备连接成功
    public static final int EVENBUS_SIGNAL_CODE_DEVICE_ENABLE_CMD = 1009;//设备可以发送指令
}
