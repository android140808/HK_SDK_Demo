package cn.appscomm.l38t.fragment.datachart;

import android.view.View;

import java.util.Date;
import java.util.LinkedHashMap;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.UI.showView.datachart.BaseDataChartView;
import cn.appscomm.l38t.UI.showView.datachart.SportTimeView;
import cn.appscomm.l38t.fragment.base.BaseDataChartFragment;
import cn.appscomm.l38t.utils.UnitTool;


/**
 * Created by Administrator on 2016/8/22.
 */
public class SportTimeShowFragment extends BaseDataChartFragment {

    private SportTimeView sportTimeView;

    @Override
    public void initView(View view) {
        baseFragShowView.setRlSleepVisibility(false);
        baseFragShowView.setTvDescript(getString(R.string.draw_activity_subtitle));
        baseFragShowView.setCircleSmallViewIcon(R.mipmap.clock_ico);
        sportTimeView = new SportTimeView(getActivity());
        sportTimeView.setSelectPointListener(new BaseDataChartView.SelectPointListener() {
            @Override
            public void onPointSelected(BaseDataChartView.DataPoint dataPoint) {
                baseFragShowView.setValue(UnitTool.getHalfUpValue(dataPoint.yValue,0) + "");
                baseFragShowView.setCircleSmallVieCurVal(dataPoint.yValue);
            }
            @Override
            public void onViewTouchDown() {
            }
        });
        baseFragShowView.setCircleSmallViewDrawColor(sportTimeView.getCircleColor());
        baseFragShowView.addToTopView(sportTimeView);
    }

    @Override
    public void setSelected() {
        if (baseFragShowView != null) {
            baseFragShowView.setDoOn(4);
        }
    }

    @Override
    public int getCurrentTimeType() {
        return sportTimeView.getTimeType();
    }

    @Override
    public void setTimeType(int time_type) {
        this.timeType = time_type;
        sportTimeView.setTimeType(time_type);
        loadData(dateNow);
    }

    @Override
    public void doHttpResponseSportDataShowViews(Date startDate) {
        sportTimeView.setStartDate(startDate);
        sportTimeView.setTimeType(timeType);
        showViews(sportDurations);
    }


    private void showViews(LinkedHashMap<Integer, Float> values) {
//        float value = 0;
//        for (Integer index : values.keySet()) {
//            value = values.get(index);
//            break;
//        }
//        int goal = localUserGoal.getGoals_activeMinutes();
//        baseFragShowView.setValue(UnitTool.getHalfUpValue(value,0) + "");
//        baseFragShowView.setCircleSmallViewGoalVal(goal);
//        baseFragShowView.setCircleSmallVieCurVal(value);
        sportTimeView.setDatas(values);
    }
}
