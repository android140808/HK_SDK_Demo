package cn.appscomm.l38t.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;

import cn.appscomm.l38t.utils.LogUtil;

/**
 * Created by liucheng on 2017/4/17.
 */

public class WelcomeAdapter extends PagerAdapter {
    public ArrayList<View> getData() {
        return data;
    }

    public void setData(ArrayList<View> data) {
        this.data = data;
    }

    private ArrayList<View> data = new ArrayList<>();

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = data.get(position % data.size());
        LogUtil.e("适配器", "加载位置======" + position % data.size());
        ViewParent vp = view.getParent();
        if (vp != null) {
            ViewGroup parent = (ViewGroup) vp;
            parent.removeView(view);
        }
        container.addView(view);
        return view;
    }
}
