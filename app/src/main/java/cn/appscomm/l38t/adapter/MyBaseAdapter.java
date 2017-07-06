package cn.appscomm.l38t.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * @author weiliu
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter{

    protected List<T> mData;
    protected Context mContext;

    public MyBaseAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData == null ? 0 : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<T> getData() {
        return mData;
    }

    public void setData(List<T> data) {
        this.mData = data;
    }
}
