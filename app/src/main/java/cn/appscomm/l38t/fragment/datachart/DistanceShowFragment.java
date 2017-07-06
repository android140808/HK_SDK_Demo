package cn.appscomm.l38t.fragment.datachart;

import android.view.View;

import com.appscomm.bluetooth.protocol.command.device.Unit;

import java.util.Date;
import java.util.LinkedHashMap;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.UI.showView.datachart.BaseDataChartView;
import cn.appscomm.l38t.UI.showView.datachart.DistanceView;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AppConfig;
import cn.appscomm.l38t.fragment.base.BaseDataChartFragment;
import cn.appscomm.l38t.utils.UnitHelper;
import cn.appscomm.l38t.utils.UnitTool;


/**
 * Created by Administrator on 2016/8/22.
 */
public class DistanceShowFragment extends BaseDataChartFragment {

    private DistanceView distanceView;

    @Override
    public void initView(View view) {
        baseFragShowView.setRlSleepVisibility(false);
        baseFragShowView.setCircleSmallViewIcon(R.mipmap.distan_ico);
        distanceView = new DistanceView(getActivity());
        int unit = AppConfig.getLocalUnit();
        if (unit == UnitHelper.UNIT_US) {//英制
            baseFragShowView.setTvDescript(GlobalApp.getAppContext().getString(R.string.draw_distance_miles_subtitle));
            distanceView.setUnit(getString(R.string.bar_unit_distance_miles));
        } else {
            baseFragShowView.setTvDescript(GlobalApp.getAppContext().getString(R.string.draw_distance_km_subtitle));
            distanceView.setUnit(getString(R.string.bar_unit_distance));
        }
        distanceView.setSelectPointListener(new BaseDataChartView.SelectPointListener() {
            @Override
            public void onPointSelected(BaseDataChartView.DataPoint dataPoint) {
                baseFragShowView.setValue(dataPoint.yValue + "");
                baseFragShowView.setCircleSmallVieCurVal(dataPoint.yValue);
            }

            @Override
            public void onViewTouchDown() {
            }
        });
        baseFragShowView.setCircleSmallViewDrawColor(distanceView.getCircleColor());
        baseFragShowView.addToTopView(distanceView);
    }

    @Override
    public void setSelected() {
        if (baseFragShowView != null) {
            baseFragShowView.setDoOn(2);
        }
    }

    @Override
    public int getCurrentTimeType() {
        return distanceView.getTimeType();
    }

    @Override
    public void setTimeType(int time_type) {
        this.timeType = time_type;
        distanceView.setTimeType(time_type);
        loadData(dateNow);
    }

    @Override
    public void doHttpResponseSportDataShowViews(Date startDate) {
        distanceView.setStartDate(startDate);
        distanceView.setTimeType(timeType);
        showViews(sportDistances);
    }


    private void showViews(LinkedHashMap<Integer, Float> values) {
        int unit = AppConfig.getLocalUnit();
//        float firstValue = 0;
        LinkedHashMap<Integer, Float> valuesShow = new LinkedHashMap<>();
        double resule = 0.0;
        for (Integer index : values.keySet()) {
            if (unit == UnitHelper.UNIT_US) {//英制
                resule = values.get(index) * Unit.UNIT_S_EN_NUM;
            } else {
                resule = values.get(index);
            }
            valuesShow.put(index, UnitTool.getMiToKm(resule).floatValue());
        }
//        for (Integer index : valuesShow.keySet()) {
//            firstValue = valuesShow.get(index);
//            break;
//        }
//        int goal = localUserGoal.getGoals_distance();
//        baseFragShowView.setValue(firstValue + "");
//        baseFragShowView.setCircleSmallViewGoalVal(goal);
//        baseFragShowView.setCircleSmallVieCurVal(firstValue);
        distanceView.setDatas(valuesShow);
    }
}
