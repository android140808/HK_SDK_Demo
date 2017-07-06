package cn.appscomm.l38t.fragment.datachart;

import android.view.View;

import java.util.Date;
import java.util.LinkedHashMap;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.UI.showView.datachart.BaseDataChartView;
import cn.appscomm.l38t.UI.showView.datachart.SportStepView;
import cn.appscomm.l38t.fragment.base.BaseDataChartFragment;
import cn.appscomm.l38t.utils.UnitTool;


/**
 * Created by Administrator on 2016/8/22.
 */
public class SportStepShowFragment extends BaseDataChartFragment {

    private SportStepView sportStepView;

    @Override
    public void initView(View view) {
        baseFragShowView.setRlSleepVisibility(false);
        baseFragShowView.setTvDescript(getString(R.string.draw_step_subtitle));
        baseFragShowView.setCircleSmallViewIcon(R.mipmap.step_ico);
        sportStepView = new SportStepView(getActivity());
        sportStepView.setSelectPointListener(new BaseDataChartView.SelectPointListener() {
            @Override
            public void onPointSelected(BaseDataChartView.DataPoint dataPoint) {
                baseFragShowView.setValue(UnitTool.getHalfUpValue(dataPoint.yValue,0) + "");
                baseFragShowView.setCircleSmallVieCurVal(dataPoint.yValue);
            }
            @Override
            public void onViewTouchDown() {
            }
        });
        baseFragShowView.setCircleSmallViewDrawColor(sportStepView.getCircleColor());
        baseFragShowView.addToTopView(sportStepView);
    }

    private void showViews(LinkedHashMap<Integer, Float> values) {
//        float value = 0;
//        for (Integer index : values.keySet()) {
//            value = values.get(index);
//            break;
//        }
//        int goal = localUserGoal.getGoals_step();
//        baseFragShowView.setValue(UnitTool.getHalfUpValue(value,0) + "");
//        baseFragShowView.setCircleSmallViewGoalVal(goal);
//        baseFragShowView.setCircleSmallVieCurVal(value);
        sportStepView.setDatas(values);
    }

    @Override
    public void doHttpResponseSportDataShowViews(Date startDate) {
        sportStepView.setStartDate(startDate);
        sportStepView.setTimeType(timeType);
        showViews(sportSteps);
    }

    @Override
    public void setSelected() {
        if (baseFragShowView != null) {
            baseFragShowView.setDoOn(0);
        }
    }

    @Override
    public void setTimeType(int time_type) {
        this.timeType = time_type;
        sportStepView.setTimeType(time_type);
        loadData(dateNow);
    }

    @Override
    public int getCurrentTimeType() {
        return sportStepView.getTimeType();
    }


}
