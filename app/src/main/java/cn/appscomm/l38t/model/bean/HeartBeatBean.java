package cn.appscomm.l38t.model.bean;

import java.io.Serializable;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/28 10:54
 */
public class HeartBeatBean implements Serializable {

    private String time;
    private int value;

    public HeartBeatBean(String time, int value) {
        this.time = time;
        this.value = value;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
