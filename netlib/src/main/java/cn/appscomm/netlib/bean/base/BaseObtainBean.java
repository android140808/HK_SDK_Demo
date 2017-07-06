package cn.appscomm.netlib.bean.base;

/**
 * Created by weiliu on 2016/6/7.
 */
public class BaseObtainBean {
    /**
     * seq : 11111111
     * code : 0
     * msg :  sucess ^_^
     * resMap : {}
     */
    private String seq;
    private int code;
    private String msg;
    private ObtainResMap resMap;

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ObtainResMap getResMap() {
        return resMap;
    }

    public void setResMap(ObtainResMap resMap) {
        this.resMap = resMap;
    }
}
