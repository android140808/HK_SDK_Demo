package cn.appscomm.l38t.model.database;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2016/6/17.
 */
public class CountSleepDB extends DataSupport {
    private  int userId;
    private  String deviceId;
    private long startTime;
    private  long  endTime;
    private  String  contents;
    private  int  dataType; //0天  1周  2月

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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }
}
