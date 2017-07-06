package cn.appscomm.l38t.UI.show;

import android.graphics.Bitmap;

/**
 * 好友排行数据模型
 * Created by Administrator on 2016/6/9.
 */
public class LineRankData {
    //值
    private  int value;
    //是否话在上边
    private  boolean isTop;

    private String iconUrl;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    private Bitmap bitmap ;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
