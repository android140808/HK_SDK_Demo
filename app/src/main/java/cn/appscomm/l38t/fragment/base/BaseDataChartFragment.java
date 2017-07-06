package cn.appscomm.l38t.fragment.base;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.UI.showView.BaseFragShowView;
import cn.appscomm.l38t.UI.showView.datachart.BaseDataChartView;
import cn.appscomm.l38t.activity.base.BaseActivity;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.config.GoalConfig;
import cn.appscomm.l38t.config.bean.LocalUserGoal;
import cn.appscomm.l38t.model.database.CountSportDB;
import cn.appscomm.l38t.model.database.UserBindDevice;
import cn.appscomm.l38t.utils.viewUtil.DateDrawTool;
import cn.appscomm.netlib.bean.base.BaseObtainBean;
import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.bean.sport.CountSportData;
import cn.appscomm.netlib.bean.sport.CountSportDetail;
import cn.appscomm.netlib.bean.sport.CountSportObtain;
import cn.appscomm.netlib.constant.HttpCode;
import cn.appscomm.netlib.retrofit_okhttp.RequestManager;
import cn.appscomm.netlib.retrofit_okhttp.interfaces.HttpResponseListener;
import cn.appscomm.uploaddata.database.SportData;


/**
 * Created by weiliu on 2016/7/25.
 */
public abstract class BaseDataChartFragment extends BaseFragment {

    public final String TAG = BaseDataChartFragment.class.getSimpleName();

    protected BaseFragShowView baseFragShowView;
    protected boolean isSelected;
    protected int timeType;
    protected Date dateNow;
    protected Calendar cal = Calendar.getInstance();
    protected Gson gson = new GsonBuilder().create();
    protected static final SimpleDateFormat day_formater = new SimpleDateFormat("yyyy-MM-dd");
    protected static final SimpleDateFormat week_formater = new SimpleDateFormat("MM/dd");
    protected static final SimpleDateFormat month_formater = new SimpleDateFormat("yyyy/MM");
    protected static final SimpleDateFormat simpleDateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected LinkedHashMap<Integer, Float> sportSteps;
    protected LinkedHashMap<Integer, Float> sportDistances;
    protected LinkedHashMap<Integer, Float> sportCalories;
    protected LinkedHashMap<Integer, Float> sportDurations;
    protected LocalUserGoal localUserGoal;

    @Override
    public String getFragmentTAG() {
        return BaseDataChartFragment.class.getSimpleName();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initValuesList();
        cal.setTime(new Date());
        loadDefaultData();
    }

    protected void loadDefaultData() {
        dateNow = cal.getTime();
        setTimeType(BaseDataChartView.TIME_WEEK);
    }

    private void initValuesList() {
        sportSteps = new LinkedHashMap<>();
        sportDistances = new LinkedHashMap<>();
        sportCalories = new LinkedHashMap<>();
        sportDurations = new LinkedHashMap<>();
        localUserGoal = GoalConfig.getLocalUserGoal();
    }

    private void clearValuesList() {
        if (sportSteps != null) {
            sportSteps.clear();
        }
        if (sportDistances != null) {
            sportDistances.clear();
        }
        if (sportCalories != null) {
            sportCalories.clear();
        }
        if (sportDurations != null) {
            sportDurations.clear();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (baseFragShowView == null) {
            baseFragShowView = new BaseFragShowView(activity);
            baseFragShowView.setIvDatePreOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickDatePre();
                }
            });
            baseFragShowView.setIvDateNextOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickDateNext();
                }
            });
        }
        return baseFragShowView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isSelected) {
            setSelected();
        }
    }

    public abstract void setTimeType(int time_type);

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public abstract void initView(final View view);

    protected void loadData(Date now) {
        setTvDataValue(now);
        Date startTime = now, endTime = now;
        switch (timeType) {
            case BaseDataChartView.TIME_WEEK:
                startTime = DateDrawTool.getCurrentMonday(now);
                endTime = DateDrawTool.getCurrentSunday(now);
                break;
            case BaseDataChartView.TIME_MONTH:
                startTime = DateDrawTool.getCurrentDateMonthBeginDate(now);
                endTime = DateDrawTool.getCurrentDateMonthEndDate(now);
                break;
        }
        requestSportData(now, startTime, endTime, timeType);
    }

    public abstract void setSelected();

    public abstract int getCurrentTimeType();

    public abstract void doHttpResponseSportDataShowViews(Date startDate);

    public void praseSportData(Date startDate, CountSportObtain countSportObtain) {
        if (sportSteps == null || sportCalories == null || sportDistances == null || sportDurations == null) {
            initValuesList();
        } else {
            clearValuesList();
        }
        if (countSportObtain != null && countSportObtain.getDetails() != null) {
            for (int i = 0; i < countSportObtain.getDetails().size(); i++) {
                CountSportDetail detail = countSportObtain.getDetails().get(i);
                int timeIndex = detail.getUnitFormat();
                if (timeType == BaseDataChartView.TIME_WEEK) {
                    if (timeIndex == 0) {
                        timeIndex = 7;//周从0开始 所以-1
                    }
                    timeIndex = timeIndex - 1;
                    sportSteps.put(timeIndex, (float) detail.getStep());
                    sportCalories.put(timeIndex, detail.getCalorie());
                    sportDistances.put(timeIndex, detail.getDistance());
                    float duration = ((int) detail.getDuration()) / 60;
                    sportDurations.put(timeIndex, duration);//返回为秒，转换为分钟
                } else {
                    timeIndex = timeIndex - 1;//月从0开始 所以-1
                    sportSteps.put(timeIndex, (float) detail.getStep());
                    sportCalories.put(timeIndex, detail.getCalorie());
                    sportDistances.put(timeIndex, detail.getDistance());
                    float duration = ((int) detail.getDuration()) / 60;
                    sportDurations.put(timeIndex, duration);//返回为秒，转换为分钟
                }
            }
        }
        doHttpResponseSportDataShowViews(startDate);
    }

    protected void clickDatePre() {
        cal.setTime(dateNow);
        switch (timeType) {
            case BaseDataChartView.TIME_WEEK:
                dateNow = DateDrawTool.getCurrentDateLastWeekBeginDate(dateNow);
                break;
            case BaseDataChartView.TIME_MONTH:
                dateNow = DateDrawTool.getCurrentDateLastMonthBeginDate(dateNow);
                break;
        }
        loadData(dateNow);
    }

    protected void clickDateNext() {
        Date date = null;
        cal.setTime(dateNow);
        switch (timeType) {
            case BaseDataChartView.TIME_WEEK:
                date = DateDrawTool.getCurrentDateNextWeekBeginDate(dateNow);
                break;
            case BaseDataChartView.TIME_MONTH:
                date = DateDrawTool.getCurrentDateNextMonthBeginDate(dateNow);
                break;
        }
        if (date.getTime() > DateDrawTool.getNowDate().getTime()) {
            if (activity != null) {
                ((BaseActivity) activity).showToast(getString(R.string.no_more_data));
            }
        } else {
            dateNow = date;
            loadData(dateNow);
        }
    }

    protected void setTvDataValue(Date date) {
        String startTime = "", endTime = "";
        switch (timeType) {
            case BaseDataChartView.TIME_DAY:
                baseFragShowView.setTvDateTime(day_formater.format(date));
                break;
            case BaseDataChartView.TIME_WEEK:
                startTime = week_formater.format(DateDrawTool.getCurrentMonday(date));
                endTime = week_formater.format(DateDrawTool.getCurrentSunday(date));
                baseFragShowView.setTvDateTime(startTime + "-" + endTime);
                break;
            case BaseDataChartView.TIME_MONTH:
                startTime = month_formater.format(date);
                endTime = month_formater.format(date);
                baseFragShowView.setTvDateTime(startTime);
                break;
        }
    }


    public void requestSportData(final Date now, final Date startTime, final Date endTime, final int timeType) {
        CountSportData countSportData = new CountSportData();
        countSportData.setStartTime(simpleDateFormatLocal.format(startTime));
        countSportData.setEndTime(simpleDateFormatLocal.format(endTime));
        final String deviceId = UserBindDevice.getBindDeviceId(AccountConfig.getUserId());
        countSportData.setDeviceId(deviceId);
        countSportData.setAccountId(AccountConfig.getUserLoginName());
        countSportData.setQueryType(timeType);
//        countSportData.setTimeZone(DateUtil.getLocalTimeZoneValue());
        countSportData.setTimeZone("null");
//        if (isConnect()){
//            //现在全部走先显示缓存然后请求网络
//            if (now.getTime() >= startTime.getTime() && now.getTime() <= endTime.getTime()) {//请求时间内
//                showLocalDbSportData(deviceId, countSportData.getStartTime(), countSportData.getEndTime(), countSportData.getQueryType());
//            } else {//历史数据
//                showLocalDbSportData(deviceId, countSportData.getStartTime(), countSportData.getEndTime(), countSportData.getQueryType());
//            }
//            httpGetData(countSportData);
//        }else{//无网络走这边
//            getSportDataFromLocal(deviceId,startTime,endTime,timeType);
//        }
        getSportDataFromLocal(deviceId, startTime, endTime, timeType);
    }

    private void getSportDataFromLocal(String deviceId, Date startTime, Date endTime, int timeType) {
        if (sportSteps == null || sportCalories == null || sportDistances == null || sportDurations == null) {
            initValuesList();
        } else {
            clearValuesList();
        }
        long startLong = startTime.getTime();
        long endLong = endTime.getTime();
        long oneDay = 24 * 3600 * 1000L;
        int size = (int) ((endLong - startLong) / oneDay);
        for (int i = 0; i <= size; i++) {
            getData(deviceId, startLong, i);
        }
        doHttpResponseSportDataShowViews(startTime);
    }

    private void getData(String deviceId, long startLong, int i) {
        long oneDay = 24 * 3600 * 1000L;
        String startFormat = simpleDateFormatLocal.format(new Date(startLong + i * oneDay));//加上一天
        String endFormat = simpleDateFormatLocal.format(new Date(startLong + oneDay * (i + 1) - 1L));//晚上23:59:59:999
        List<SportData> sportDatas = DataSupport.where("startTime>? and endTime<?  and userId=?", startFormat, endFormat, AccountConfig.getUserId() + "").find(SportData.class);
        if (sportDatas != null && sportDatas.size() > 0) {
            float step = 0;
            float cal = 0;
            float dis = 0;
            for (SportData data : sportDatas) {
                step += data.getSportStep();
                cal += data.getSportCalorie();
                dis += data.getSportDistance();
            }
            sportSteps.put(i, step);
            sportCalories.put(i, cal);
            sportDistances.put(i, dis);
        }
    }

    protected boolean isConnect() {
        boolean flag = false;
        ConnectivityManager systemService = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = systemService.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            flag = activeNetworkInfo.isAvailable();
        }
        return flag;
    }


    private void httpGetData(final CountSportData countSportData) {
        if (activity == null) {
            return;
        }
        ((BaseActivity) activity).showBigLoadingProgress(getString(R.string.loading));
        RequestManager.getInstance().countSportData(countSportData, new HttpResponseListener() {
            @Override
            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
                if (activity == null) {
                    return;
                }
                ((BaseActivity) activity).dismissLoadingDialog();
                if (HttpCode.isSuccess(statusCode)) {
                    CountSportObtain countSportObtain = (CountSportObtain) baseObtainBean;
                    saveCountSport(countSportObtain, countSportData.getDeviceId(), countSportData.getStartTime(), countSportData.getEndTime(), timeType);
                } else if (statusCode != 8003) {
                    if (statusCode == 8008) {
                        ((BaseActivity) activity).showToast(getString(R.string.no_bind_device));
                    } else {
                        ((BaseActivity) activity).showToast(HttpCode.getInstance(GlobalApp.getAppContext()).getCodeString(statusCode));
                    }
                }
            }

            @Override
            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {
                if (activity != null) {
                    ((BaseActivity) activity).dismissLoadingDialog();
//                    ((BaseActivity)activity).showToast(HttpCode.getInstance(GlobalApp.getAppContext()).getCodeString(statusCode));
                    if (statusCode != 8003) {
                        if (statusCode == 8008) {
                            ((BaseActivity) activity).showToast(getString(R.string.no_bind_device));
                        } else {
                            ((BaseActivity) activity).showToast(HttpCode.getInstance(GlobalApp.getAppContext()).getCodeString(statusCode));
                        }
                    }
                }
            }
        });
    }

    private void saveCountSport(final CountSportObtain countSportObtain, final String deviceId, final String startTime, final String endTime, final int timeType) {
        CountSportDB countSportDB = new CountSportDB();
        countSportDB.setContents(gson.toJson(countSportObtain));
        countSportDB.setDeviceId(deviceId);
        countSportDB.setUserId(AccountConfig.getUserId());
        countSportDB.setDataType(timeType);
        try {
            long start = simpleDateFormatLocal.parse(startTime).getTime() / 1000;
            long end = simpleDateFormatLocal.parse(endTime).getTime() / 1000;
            countSportDB.setStartTime(start);
            countSportDB.setEndTime(end);
            if (DataSupport.where("startTime=? and endTime=? and deviceId =? and dataType=?", start + "", end + "", deviceId, timeType + "").find(CountSportDB.class).size() > 0) {
                ContentValues values = new ContentValues();
                values.put("contents", gson.toJson(countSportObtain));
                DataSupport.updateAll(CountSportDB.class, values, "startTime=? and endTime=? and deviceId =? and dataType=?", start + "", end + "", deviceId, timeType + "");
            } else {
                countSportDB.save();
            }
            praseSportData(simpleDateFormatLocal.parse(startTime), countSportObtain);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void showLocalDbSportData(final String deviceId, final String startTime, final String endTime, final int timeType) {
        try {
            long start = simpleDateFormatLocal.parse(startTime).getTime() / 1000;
            long end = simpleDateFormatLocal.parse(endTime).getTime() / 1000;
            CountSportObtain countSportObtain = null;
            CountSportDB countSportDB = DataSupport.where("startTime<=? and endTime>=? and deviceId =? and dataType=?", start + "", end + "", deviceId, timeType + "").findFirst(CountSportDB.class);
            if (countSportDB != null) {
                countSportObtain = gson.fromJson(countSportDB.getContents(), CountSportObtain.class);
            }
            praseSportData(simpleDateFormatLocal.parse(startTime), countSportObtain);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
