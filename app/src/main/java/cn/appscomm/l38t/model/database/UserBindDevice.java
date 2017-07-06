package cn.appscomm.l38t.model.database;

import android.content.ContentValues;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Administrator on 2016/8/12.
 */
public class UserBindDevice extends DataSupport {

    public static final String LF02A = "Lefit HR#";
    public static final String LF01A = "Lefit Basic#";
    public static final String L38T = "AM01";
    public static final String WT10A = "WT10A";
    public static final String WT10C = "WT10C";
    public static final String YOUNG = "L42A";
    public static final String COLOR = "COLOR蓝牙名称";
    public static final String HER = "Her";

    private int id;
    private int userId;
    private String deviceId;
    private String deviceName;
    private String deviceAddress;
    private String bindTime;
    private String deviceVersion;
    private String deviceType;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getBindTime() {
        return bindTime;
    }

    public void setBindTime(String bindTime) {
        this.bindTime = bindTime;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public final static List<UserBindDevice> findByUserId(int userId) {
        return where("userId = ?", userId + "").find(UserBindDevice.class);
    }

    public final static List<UserBindDevice> findAll() {
        return findAll(UserBindDevice.class);
    }

    public final static boolean haveBindDevice(int userId) {
        UserBindDevice device = where("userId = ?", userId + "").findFirst(UserBindDevice.class);
        if (device != null) {
            return true;
        } else {
            return false;
        }
    }

    public final static UserBindDevice getBindDevice(int userId) {
        return where("userId = ?", userId + "").findFirst(UserBindDevice.class);
    }

    public final static List<UserBindDevice> getBindDevice(String deviceAddress) {
        return where("deviceAddress = ?", deviceAddress + "").find(UserBindDevice.class);
    }

    public final static String getBindDeviceId(int userId) {
        UserBindDevice device = where("userId = ?", userId + "").findFirst(UserBindDevice.class);
        if (device != null && null != device.getDeviceId()) {
            return device.getDeviceId();
        } else {
            return "";
        }
    }

    public final static void updateOrAddByUserId(UserBindDevice userBindDevice) {
        if (userBindDevice != null) {
            boolean flag = haveBindDevice(userBindDevice.getUserId());
            if (flag) {
                ContentValues contentValues=new ContentValues();
                contentValues.put("userId",userBindDevice.getUserId());
                contentValues.put("deviceId",userBindDevice.getDeviceId());
                contentValues.put("deviceAddress",userBindDevice.getDeviceAddress());
                contentValues.put("bindTime",userBindDevice.getBindTime());
                contentValues.put("deviceName",userBindDevice.getDeviceName());
                contentValues.put("deviceVersion",userBindDevice.getDeviceVersion());
                contentValues.put("deviceType",userBindDevice.getDeviceType());
                DataSupport.updateAll(RemindDataModel.class,contentValues,"userId = ?",userBindDevice.getUserId() + "");
            } else {
                userBindDevice.save();
            }
        }
    }

    public final static void deleteByUserId(int userId) {
        List<UserBindDevice> list = findByUserId(userId);
        for (UserBindDevice userBindDevice : list) {
            userBindDevice.delete();
        }
    }


}
