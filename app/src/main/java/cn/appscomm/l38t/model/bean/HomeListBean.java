package cn.appscomm.l38t.model.bean;

/**
 * Created by Administrator on 2016/6/15.
 */
public class HomeListBean {

    public static final int Step=1;
    public static final int Calories=2;
    public static final int Distance=3;
    public static final int Sleep=4;
    public static final int Active=5;

    private int index;
    private  int tarGet;
    private  int current;

    public HomeListBean(int index, int tarGet, int current){
        this.index=index;
        this.tarGet=tarGet;
        this.current=current;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTarGet() {
        return tarGet;
    }

    public void setTarGet(int tarGet) {
        this.tarGet = tarGet;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }
}
