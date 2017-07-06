package cn.appscomm.l38t.UI.showView.datachart;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import cn.appscomm.l38t.UI.showView.datachart.BaseDataChartView;

/**
 * Created by Administrator on 2016/8/22.
 */
public class DistanceView extends BaseDataChartView {
    public DistanceView(Context context) {
        super(context);
        init();
    }

    public DistanceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DistanceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DistanceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        circleColor = Color.parseColor("#0F96AC");
        yLineColor = Color.parseColor("#0F96AC");
        setHalfUpNum(2);//保留
    }
}
