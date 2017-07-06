package cn.appscomm.l38t.utils;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by Administrator on 2016/6/7.
 */
public class HolderViewUtil {


    public static <T extends View> T get(View view, int id) {

        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();

        if (null == viewHolder) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);

        }
        View childView = viewHolder.get(id);
        if (null == childView) {

            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}
