package cn.appscomm.l38t.fragment.datachart;

import android.view.View;

import java.util.Date;
import java.util.LinkedHashMap;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.UI.showView.datachart.BaseDataChartView;
import cn.appscomm.l38t.UI.showView.datachart.CaloriesView;
import cn.appscomm.l38t.fragment.base.BaseDataChartFragment;
import cn.appscomm.l38t.utils.UnitTool;


/**
 * Created by Administrator on 2016/8/22.
 */
public class CaloriesShowFragment extends BaseDataChartFragment {
    private CaloriesView caloriesView;

    @Override
    public void initView(View view) {
        baseFragShowView.setRlSleepVisibility(false);
        baseFragShowView.setTvDescript(getString(R.string.draw_calories_subtitle));
        baseFragShowView.setCircleSmallViewIcon(R.mipmap.calu_ico);
        caloriesView = new CaloriesView(getActivity());
        caloriesView.setSelectPointListener(new BaseDataChartView.SelectPointListener() {
            @Override
            public void onPointSelected(BaseDataChartView.DataPoint dataPoint) {
                baseFragShowView.setValue(dataPoint.yValue + "");
                baseFragShowView.setCircleSmallVieCurVal(dataPoint.yValue);
            }
            @Override
            public void onViewTouchDown() {
            }
        });
        baseFragShowView.setCircleSmallViewDrawColor(caloriesView.getCircleColor());
        baseFragShowView.addToTopView(caloriesView);
    }


    @Override
    public void setSelected() {
        if (baseFragShowView != null) {
            baseFragShowView.setDoOn(1);
        }
    }

    @Override
    public int getCurrentTimeType() {
        return caloriesView.getTimeType();
    }

    @Override
    public void setTimeType(int time_type) {
        this.timeType = time_type;
        caloriesView.setTimeType(time_type);
        loadData(dateNow);
    }

    @Override
    public void doHttpResponseSportDataShowViews(Date startDate) {
        caloriesView.setStartDate(startDate);
        caloriesView.setTimeType(timeType);
        showViews(sportCalories);
    }

    private void showViews(LinkedHashMap<Integer, Float> values) {
//        float firstValue = 0;
        LinkedHashMap<Integer, Float> valuesShow=new LinkedHashMap<>();
        for (Integer index : values.keySet()) {
            valuesShow.put(index, UnitTool.getKaToKKa(values.get(index)).floatValue());
        }
//        for (Integer index : valuesShow.keySet()) {
//            firstValue = valuesShow.get(index);
//            break;
//        }
//        int goal = localUserGoal.getGoals_calories();
//        baseFragShowView.setValue(firstValue + "");
//        baseFragShowView.setCircleSmallViewGoalVal(goal);
//        baseFragShowView.setCircleSmallVieCurVal(firstValue);
        caloriesView.setDatas(valuesShow);
    }

}
