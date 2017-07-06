package cn.appscomm.l38t.model.bean;

import android.text.TextUtils;

import com.appscomm.bluetooth.bean.DeviceTimeFormat;

import java.io.Serializable;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/6 19:33
 */
public class DeviceTimeFormatBean implements Serializable {

    private int id;
    private String timeFormat;
    private int drawable;
    private String timeFormatShowValue;
    private DeviceTimeFormat deviceTimeFormat;
    private boolean isSelected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getTimeFormatShowValue() {
        return timeFormatShowValue;
    }

    public void setTimeFormatShowValue(String timeFormatShowValue) {
        this.timeFormatShowValue = timeFormatShowValue;
        if (!TextUtils.isEmpty(timeFormatShowValue)){
            deviceTimeFormat=new DeviceTimeFormat();
            deviceTimeFormat.praseTimeFormat(timeFormatShowValue);
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public DeviceTimeFormat getDeviceTimeFormat() {
        return deviceTimeFormat;
    }
}
