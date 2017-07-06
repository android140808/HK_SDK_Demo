package cn.appscomm.l38t.UI.showView.datachart;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import cn.appscomm.l38t.R;


/**
 * Created by Administrator on 2016/8/22.
 */
public class SportTimeView extends BaseDataChartView {

    public SportTimeView(Context context) {
        super(context);
        init();
    }

    public SportTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SportTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SportTimeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        circleColor = Color.parseColor("#B8D200");
        yLineColor = Color.parseColor("#F4B268");
        setUnit(getContext().getString(R.string.bar_unit_activetime));
        setHalfUpNum(0);//保留
    }
}
