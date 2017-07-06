package cn.appscomm.l38t.fragment.datachart;

import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.UI.showView.datachart.MoodView;
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
import rx.Subscriber;


/**
 * Created by weiliu on 2016/7/25.
 */
public class MoodShowFragment extends BaseShowFragment {

    private MoodView moodView;
    private Subscriber subscriber;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void initView(View view) {
        super.initView(view);
        baseFragBottomView.setRlSleepVisibility(false);
        baseFragBottomView.setCircleSmallViewIcon(R.mipmap.mood_3);
        baseFragBottomView.setTvDescript(GlobalApp.getAppContext().getString(R.string.draw_mood_subtitle));
        moodView = new MoodView(activity);
        moodView.setSelectPointListener(new MoodView.SelectPointListener() {
            @Override
            public void onPointSelected(int culValue) {
                setMoodIconType(culValue);
                setMoodViewDrawColor(culValue);
                baseFragBottomView.setValue(getMoodString(culValue));
                baseFragBottomView.setCircleSmallVieCurVal(MoodFatigueData.MOOD_MAX - culValue + 1);
            }
        });
        baseFragBottomView.addToTopView(moodView);
    }

    public void setSelected() {
        if (baseFragBottomView != null) {
            baseFragBottomView.setDoOn(6);
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

    @Override
    public void onResume() {
        super.onResume();
        if (isSelected) {
            setSelected();
        }
    }

    private void showViews(LinkedHashMap<Integer, MoodFatigueData> values) {
        int value = MoodView.CALM;
        for (Integer index : values.keySet()) {
            value = values.get(index).getMoodStatus();
            break;
        }
        baseFragBottomView.setValue(getMoodString(value));
        setMoodIconType(value);
        baseFragBottomView.setCircleSmallViewGoalVal(MoodFatigueData.MOOD_MAX + 1);
        setMoodViewDrawColor(value);
        baseFragBottomView.setCircleSmallVieCurVal(MoodFatigueData.MOOD_MAX - value + 1);
        moodView.setDatas(values);
    }

    private void setMoodViewDrawColor(int value) {
        switch (value) {
            case MoodView.CALM:
                baseFragBottomView.setCircleSmallViewDrawColor(getResources().getColor(R.color.mood_calm));
                break;
            case MoodView.MODERATE:
                baseFragBottomView.setCircleSmallViewDrawColor(getResources().getColor(R.color.mood_moderate));
                break;
            case MoodView.DEPRESSED:
                baseFragBottomView.setCircleSmallViewDrawColor(getResources().getColor(R.color.mood_depressed));
                break;
        }
    }

    private void setMoodIconType(int value) {
        switch (value) {
            case MoodView.CALM:
                baseFragBottomView.setCircleSmallViewIcon(R.mipmap.mood_3);
                break;
            case MoodView.MODERATE:
                baseFragBottomView.setCircleSmallViewIcon(R.mipmap.mood_4);
                break;
            case MoodView.DEPRESSED:
                baseFragBottomView.setCircleSmallViewIcon(R.mipmap.mood_2);
                break;
        }
    }

    private String getMoodString(Integer value) {
        String valueString = getResources().getString(R.string.mood_calm);
        switch (value) {
            case MoodView.CALM:
                valueString = getResources().getString(R.string.mood_calm);
                break;
            case MoodView.MODERATE:
                valueString = getResources().getString(R.string.mood_moderate);
                break;
            case MoodView.DEPRESSED:
                valueString = getResources().getString(R.string.mood_depressed);
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
                int moodStatus = details.get(i).getMoodStatus();
                if (moodStatus >= MoodFatigueData.MOOD_MIN && moodStatus <= MoodFatigueData.MOOD_MAX) {
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
