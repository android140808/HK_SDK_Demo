package cn.appscomm.l38t.eventbus.base;

/**
 * Created by Administrator on 2016/8/30.
 */
public class EventBusMessage {

    private int id;
    private int code;
    private String message;
    private Object target;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
