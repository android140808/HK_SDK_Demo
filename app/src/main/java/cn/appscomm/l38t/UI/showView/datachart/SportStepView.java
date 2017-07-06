package cn.appscomm.l38t.UI.showView.datachart;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import cn.appscomm.l38t.R;


/**
 * Created by Administrator on 2016/8/22.
 */
public class SportStepView extends BaseDataChartView {

    public SportStepView(Context context) {
        super(context);
        init();
    }

    public SportStepView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SportStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SportStepView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        circleColor= Color.parseColor("#A1D82A");
        yLineColor= Color.parseColor("#A1D82A");
        setUnit(getContext().getString(R.string.bar_unit_steps));
        setHalfUpNum(0);//保留
    }

}
