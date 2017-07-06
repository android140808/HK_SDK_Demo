package cn.appscomm.l38t.UI.showView.datachart;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import cn.appscomm.l38t.R;


/**
 * Created by Administrator on 2016/8/22.
 */
public class CaloriesView extends BaseDataChartView {
    public CaloriesView(Context context) {
        super(context);
        init();
    }

    public CaloriesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CaloriesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CaloriesView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        circleColor = Color.parseColor("#F5C71D");     // 圆的颜色
        yLineColor = Color.parseColor("#F5C71D");       // 线的颜色
        setUnit(getContext().getString(R.string.bar_unit_calories));
        setHalfUpNum(2);//保留
    }
}
