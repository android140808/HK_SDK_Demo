package cn.appscomm.l38t.UI.showView;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.UI.showView.datachart.MoodView;


/**
 * Created by weiliu on 2016/7/25.
 * desc:详细数据的显示基类
 */
public class BaseFragShowView extends FrameLayout {
    private LinearLayout ll_dataChart;
    private RelativeLayout rl_sleep;
    private LinearLayout layout_dataview;
    private RelativeLayout rl_value;
    private CircleSmallView circleSmallView;
    private LinearLayout dotLayout;
    private TextView mTvValue;
    private TextView mTvDescript;
    private TextView mTvDateTime;
    private ImageView mIvDatePre;
    private ImageView mIvDateNext;
    private TextView tv_beginSleep;
    private TextView tv_endSleep;

    public BaseFragShowView(Context context) {
        super(context);
        init(context);
    }

    public BaseFragShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseFragShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseFragShowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_base_fragment_show, this, true);
        ll_dataChart = (LinearLayout) findViewById(R.id.ll_dataChart);
        rl_value= (RelativeLayout) findViewById(R.id.rl_value);
        rl_sleep= (RelativeLayout) findViewById(R.id.rl_sleep);
        layout_dataview = (LinearLayout) findViewById(R.id.circle_dataview);
        dotLayout = (LinearLayout) findViewById(R.id.dotLayout);
        mTvValue = (TextView) findViewById(R.id.tv_Value);
        mTvDescript = (TextView) findViewById(R.id.tv_descript);
        circleSmallView = new CircleSmallView(context, 0, 100);
        layout_dataview.addView(circleSmallView);
        mIvDatePre = (ImageView) findViewById(R.id.iv_datePre);
        mTvDateTime = (TextView) findViewById(R.id.tv_dateTime);
        mIvDateNext = (ImageView) findViewById(R.id.iv_dateNext);
        tv_beginSleep = (TextView) findViewById(R.id.tv_beginSleep);
        tv_endSleep = (TextView) findViewById(R.id.tv_endSleep);
    }

    public void setDoOn(int index) {
        if (dotLayout == null) return;
        for (int i = 0; i <= dotLayout.getChildCount() - 1; i++) {
            if (index == i) {
                ((ImageView) dotLayout.getChildAt(i)).setImageResource(R.mipmap.dot_on);
            } else {
                ((ImageView) dotLayout.getChildAt(i)).setImageResource(R.mipmap.dot_off);
            }
        }
    }

    public void setValue(String value) {
        if (mTvValue != null) {
            mTvValue.setText(value + "");
        }
    }

    public void setTvDateTime(String value) {
        if (mTvDateTime != null) {
            mTvDateTime.setText(value + "");
        }
    }

    public BaseFragShowView setTvBeginSleep(String value) {
        if (tv_beginSleep != null) {
            tv_beginSleep.setText(value + "");
            setRlSleepVisibility(true);
        }
        return this;
    }

    public BaseFragShowView setTvEndSleep(String value) {
        if (tv_endSleep != null) {
            tv_endSleep.setText(value + "");
            setRlSleepVisibility(true);
        }
        return this;
    }

    public BaseFragShowView setRlSleepVisibility(boolean visiable) {
        if (rl_sleep != null) {
            if (visiable) {
                rl_sleep.setVisibility(View.VISIBLE);
            }else {
                rl_sleep.setVisibility(View.GONE);
            }
        }
        return this;
    }

    public void setIvDatePreOnClickListener(OnClickListener listener) {
        if (mIvDatePre != null && listener != null) {
            mIvDatePre.setOnClickListener(listener);
        }
    }

    public void setIvDateNextOnClickListener(OnClickListener listener) {
        if (mIvDateNext != null && listener != null) {
            mIvDateNext.setOnClickListener(listener);
        }
    }


    public void setTvDescript(String value) {
        if (mTvDescript != null) {
            mTvDescript.setText(value + "");
        }
    }

    public void setCircleSmallViewGoalVal(float value) {
        if (circleSmallView != null) {
            circleSmallView.setGoalval(value);
        }
    }

    public void setCircleSmallVieCurVal(float value) {
        if (circleSmallView != null) {
            circleSmallView.setCurval(value);
        }
    }

    public void setCircleSmallViewIcon(int icon) {
        if (circleSmallView != null) {
            circleSmallView.setViewType(MoodView.VIEW_TYPE_CALM);
            circleSmallView.setTitleico(icon);
        }
    }

    public void setCircleSmallViewDrawColor(int color) {
        if (circleSmallView != null) {
            circleSmallView.setDrawcolor(color);
        }
    }

    //加入不同式样的View
    public void addToTopView(View view) {
        if (ll_dataChart != null) {
            ll_dataChart.removeAllViews();
            ll_dataChart.addView(view);
        }
    }

    //加入不同式样的View
    public void addToBottomView(View view) {
        if (rl_value != null) {
            rl_value.removeAllViews();
            rl_value.addView(view);
        }
    }
}
