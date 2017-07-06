package cn.appscomm.l38t.UI.showView;

/**
 * Created by Administrator on 2015/11/26.
 */
public class Point {
    public int px, py,value;
    public Object object;

    public Point() {
    }

    public Point(int px, int py, int value, Object target) {
        this.px = px;
        this.py = py;
        this.value=value;
        this.object=target;
    }

    @Override
    public String toString() {
        return "px:" + px + ", py:" + py;
    }
}
