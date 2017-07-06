package cn.appscomm.uploaddata.database;

import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SleepDataDB extends DataSupport {
    private int sleep_id;            // 睡眠ID
    private int sleep_type;            // 睡眠类型：0x00：睡着， 0x01：浅睡， 0x02：醒着，0x03：准备入睡，0x10（16）：进入睡眠模式；0x11（17）| 0x12 (18)：退出睡眠模式
    private String startTime;
    private String endTime;
    private int userId;
    private String accountId;
    private String deviceId;
    private long time_stamp;
    private long local_time_stamp;
    private int isUpdate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SleepDataDB)) return false;

        SleepDataDB that = (SleepDataDB) o;

        if (sleep_id != that.sleep_id) return false;
        if (sleep_type != that.sleep_type) return false;
        if (userId != that.userId) return false;
        if (time_stamp != that.time_stamp) return false;
        if (local_time_stamp != that.local_time_stamp) return false;
        if (isUpdate != that.isUpdate) return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null)
            return false;
        if (endTime != null ? !endTime.equals(that.endTime) : that.endTime != null) return false;
        return deviceId != null ? deviceId.equals(that.deviceId) : that.deviceId == null;
    }

    @Override
    public int hashCode() {
        int result = sleep_id;
        result = 31 * result + sleep_type;
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + userId;
        result = 31 * result + (deviceId != null ? deviceId.hashCode() : 0);
        result = 31 * result + (int) (time_stamp ^ (time_stamp >>> 32));
        result = 31 * result + (int) (local_time_stamp ^ (local_time_stamp >>> 32));
        result = 31 * result + isUpdate;
        return result;
    }

    public int getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(int isUpdate) {
        this.isUpdate = isUpdate;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getSleep_id() {
        return sleep_id;
    }

    public void setSleep_id(int sleep_id) {
        this.sleep_id = sleep_id;
    }

    public int getSleep_type() {
        return sleep_type;
    }

    public void setSleep_type(int sleep_type) {
        this.sleep_type = sleep_type;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }

    public long getLocal_time_stamp() {
        return local_time_stamp;
    }

    public void setLocal_time_stamp(long local_time_stamp) {
        this.local_time_stamp = local_time_stamp;
    }

    /*@Override
    public String toString()
	{
		// summer: modify to timeStamp2format and delete /1000L
		return "id:" + sleep_id  + " type:" + sleep_type  + " local_time_stamp:"+ local_time_stamp + " 格式化：" + timeStamp2format(local_time_stamp) + "\n"  ;
	}*/

    @Override
    public String toString() {
        return "SleepDataDB{" +
                "sleep_id=" + sleep_id +
                ", sleep_type=" + sleep_type +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", userId=" + userId +
                ", deviceId='" + deviceId + '\'' +
                ", time_stamp=" + time_stamp + timeStamp2format(time_stamp) +
                ", local_time_stamp=" + local_time_stamp + timeStamp2format(local_time_stamp) +
                '}';
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    // 把时间戳（秒），转换为日期格式：2014-0-04-16 13:25
    public static String timeStamp2format(long time_stamp) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String reTime = df.format(time_stamp * 1000L);
        return reTime;
    }

}
