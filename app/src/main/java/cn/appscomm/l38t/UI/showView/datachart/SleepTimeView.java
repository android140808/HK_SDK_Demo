package cn.appscomm.l38t.UI.showView.datachart;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import cn.appscomm.l38t.R;


/**
 * Created by Administrator on 2016/8/22.
 */
public class SleepTimeView extends BaseDataChartView {
    public SleepTimeView(Context context) {
        super(context);
        init();
    }

    public SleepTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SleepTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SleepTimeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        circleColor = Color.parseColor("#05C7D1");
        yLineColor = Color.parseColor("#DDDDDD");
        setUnit(getContext().getString(R.string.bar_unit_minutes));
        setHalfUpNum(0);//保留
    }
}
