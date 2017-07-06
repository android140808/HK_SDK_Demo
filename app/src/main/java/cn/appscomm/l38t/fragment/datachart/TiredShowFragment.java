package cn.appscomm.l38t.fragment.datachart;

import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.UI.showView.datachart.TiredView;
import cn.appscomm.l38t.activity.base.BaseActivity;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.fragment.base.BaseShowFragment;
import cn.appscomm.l38t.model.database.UserBindDevice;
import cn.appscomm.l38t.utils.DateUtil;
import cn.appscomm.l38t.utils.viewUtil.DateDrawTool;
import cn.appscomm.netlib.bean.base.BaseObtainBean;
import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.bean.mood.MoodFatigue;
import cn.appscomm.netlib.bean.mood.MoodFatigueData;
import cn.appscomm.netlib.bean.mood.MoodFatigueObtain;
import cn.appscomm.netlib.constant.HttpCode;
import cn.appscomm.netlib.constant.NetLibConstant;
import cn.appscomm.netlib.retrofit_okhttp.RequestManager;
import cn.appscomm.netlib.retrofit_okhttp.interfaces.HttpResponseListener;


/**
 * Created by weiliu on 2016/7/25.
 */
public class TiredShowFragment extends BaseShowFragment {

    private TiredView tiredView;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void initView(View view) {
        super.initView(view);
        baseFragBottomView.setRlSleepVisibility(false);
        baseFragBottomView.setTvDescript(GlobalApp.getAppContext().getString(R.string.draw_tired_subtitle));
        baseFragBottomView.setCircleSmallViewIcon(R.mipmap.tired_4);
        tiredView = new TiredView(activity);
        tiredView.setSelectPointListener(new TiredView.SelectPointListener() {
            @Override
            public void onPointSelected(int culValue) {
                setTiredIconType(culValue);
                setTiredViewDrawColor(culValue);
                baseFragBottomView.setValue(getTiredString(culValue));
                baseFragBottomView.setCircleSmallVieCurVal(MoodFatigueData.FATIGUE_MAX - culValue + 1);
            }
        });
        baseFragBottomView.addToTopView(tiredView);
    }

    public void setSelected() {
        if (baseFragBottomView != null) {
            baseFragBottomView.setDoOn(7);
        }
    }

    @Override
    protected void loadData(Date date) {
        if (date.getTime() > DateDrawTool.getNowDate().getTime()) {
            if (activity == null) {
                return;
            }
            ((BaseActivity) activity).showToast(getString(R.string.no_more_data));
        } else {
            super.loadData(date);
            queryData(date);
        }
    }

    private void showViews(LinkedHashMap<Integer, MoodFatigueData> values) {
        int value = TiredView.ENERGETIC;
        for (Integer index : values.keySet()) {
            value = values.get(index).getFatigueStatus();
            break;
        }
        baseFragBottomView.setValue(getTiredString(value));
        setTiredIconType(value);
        baseFragBottomView.setCircleSmallViewGoalVal(MoodFatigueData.FATIGUE_MAX + 1);
        setTiredViewDrawColor(value);
        baseFragBottomView.setCircleSmallVieCurVal(MoodFatigueData.FATIGUE_MAX - value + 1);
        tiredView.setDatas(values);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isSelected) {
            setSelected();
        }
    }

    private void setTiredViewDrawColor(int value) {
        switch (value) {
            case TiredView.ENERGETIC:
                baseFragBottomView.setCircleSmallViewDrawColor(getResources().getColor(R.color.tired_energetic));
                break;
            case TiredView.MODERATE:
                baseFragBottomView.setCircleSmallViewDrawColor(getResources().getColor(R.color.tired_moderate));
                break;
            case TiredView.SLIGHT:
                baseFragBottomView.setCircleSmallViewDrawColor(getResources().getColor(R.color.tired_slight));
                break;
            case TiredView.SERIOUS:
                baseFragBottomView.setCircleSmallViewDrawColor(getResources().getColor(R.color.tired_serious));
                break;
        }
    }

    private void setTiredIconType(int value) {
        switch (value) {
            case TiredView.ENERGETIC:
                baseFragBottomView.setCircleSmallViewIcon(R.mipmap.tired_4);
                break;
            case TiredView.MODERATE:
                baseFragBottomView.setCircleSmallViewIcon(R.mipmap.tired_3);
                break;
            case TiredView.SLIGHT:
                baseFragBottomView.setCircleSmallViewIcon(R.mipmap.tired_2);
                break;
            case TiredView.SERIOUS:
                baseFragBottomView.setCircleSmallViewIcon(R.mipmap.tired_1);
                break;
        }
    }

    private String getTiredString(Integer value) {
        String valueString = getResources().getString(R.string.tired_energetic);
        switch (value) {
            case TiredView.ENERGETIC:
                valueString = getResources().getString(R.string.tired_energetic);
                break;
            case TiredView.MODERATE:
                valueString = getResources().getString(R.string.tired_moderate);
                break;
            case TiredView.SLIGHT:
                valueString = getResources().getString(R.string.tired_slight);
                break;
            case TiredView.SERIOUS:
                valueString = getResources().getString(R.string.tired_serious);
                break;
        }
        return valueString;
    }


    private void queryData(Date date) {
        if (activity == null) {
            return;
        }
        ((BaseActivity) activity).showBigLoadingProgress(getString(R.string.loading));
        MoodFatigue moodFatigue = new MoodFatigue();
        moodFatigue.setCustomerCode(NetLibConstant.DEFAULT_CUSTOMER_CODE);
        moodFatigue.setAccountId("" + AccountConfig.getUserLoginName());
        moodFatigue.setDeviceId(UserBindDevice.getBindDeviceId(AccountConfig.getUserId()));
        moodFatigue.setTimeZone(com.appscomm.bluetooth.utils.DateUtil.getLocalTimeZoneValue());
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date start = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.SECOND, -1);
        Date end = calendar.getTime();
        moodFatigue.setStartTime(DateUtil.dateToSec(start));
        moodFatigue.setEndTime(DateUtil.dateToSec(end));
        RequestManager.getInstance().moodFatigue(moodFatigue, new HttpResponseListener() {
            @Override
            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
                if (activity == null) {
                    return;
                }
                ((BaseActivity) activity).dismissLoadingDialog();
                if (HttpCode.isSuccess(statusCode)) {
                    MoodFatigueObtain moodFatigueObtain = (MoodFatigueObtain) baseObtainBean;
                    praseValues(moodFatigueObtain.getDetails());
                } else {
                    if (statusCode!=8003){
                        ((BaseActivity) activity).showToast(HttpCode.getInstance(GlobalApp.getAppContext()).getCodeString(statusCode));
                    }
                }
            }

            @Override
            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {
                if (activity == null) {
                    return;
                }
                ((BaseActivity) activity).dismissLoadingDialog();
                ((BaseActivity) activity).showToast(HttpCode.getInstance(GlobalApp.getAppContext()).getCodeString(statusCode));
            }
        });
    }

    private void praseValues(ArrayList<MoodFatigueData> details) {
        LinkedHashMap<Integer, MoodFatigueData> values = new LinkedHashMap<>();
        if (details != null) {
            for (int i = 0; i < details.size(); i++) {
                int fatigueStatus = details.get(i).getFatigueStatus();
                if (fatigueStatus >= MoodFatigueData.FATIGUE_MIN && fatigueStatus <= MoodFatigueData.FATIGUE_MAX) {
                    int index = getIndexByTime(details.get(i));
                    values.put(index, details.get(i));
                }
            }
        }
        showViews(values);
    }

    private int getIndexByTime(MoodFatigueData moodFatigueData) {
        String endTime = moodFatigueData.getStartTime();
        Date date = DateUtil.strToDate(endTime, "yyyy-MM-dd HH:mm:ss");
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int miute = calendar.get(Calendar.MINUTE);
        return hour * 60 + miute;
    }

}
