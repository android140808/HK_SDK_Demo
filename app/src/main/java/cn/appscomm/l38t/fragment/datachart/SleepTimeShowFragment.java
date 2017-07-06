package cn.appscomm.l38t.fragment.datachart;

import android.content.ContentValues;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.UI.showView.datachart.BaseDataChartView;
import cn.appscomm.l38t.UI.showView.datachart.SleepTimeDayView;
import cn.appscomm.l38t.UI.showView.datachart.SleepTimeView;
import cn.appscomm.l38t.activity.base.BaseActivity;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.fragment.base.BaseDataChartFragment;
import cn.appscomm.l38t.model.database.CountSleepDB;
import cn.appscomm.l38t.model.database.DaySleepDB;
import cn.appscomm.l38t.model.database.UserBindDevice;
import cn.appscomm.l38t.utils.LogUtil;
import cn.appscomm.l38t.utils.SleepDataUtils;
import cn.appscomm.l38t.utils.TimeZoneUtils;
import cn.appscomm.l38t.utils.UnitTool;
import cn.appscomm.l38t.utils.viewUtil.DateDrawTool;
import cn.appscomm.netlib.bean.base.BaseObtainBean;
import cn.appscomm.netlib.bean.base.BasePostBean;
import cn.appscomm.netlib.bean.sleep.CountSleepData;
import cn.appscomm.netlib.bean.sleep.CountSleepDetail;
import cn.appscomm.netlib.bean.sleep.CountSleepObtain;
import cn.appscomm.netlib.bean.sleep.QuerySleep;
import cn.appscomm.netlib.bean.sleep.QuerySleepObtain;
import cn.appscomm.netlib.bean.sleep.SleepData;
import cn.appscomm.netlib.bean.sleep.SleepDetail;
import cn.appscomm.netlib.bean.sleep.UploadSleep;
import cn.appscomm.netlib.constant.HttpCode;
import cn.appscomm.netlib.constant.NetLibConstant;
import cn.appscomm.netlib.retrofit_okhttp.RequestManager;
import cn.appscomm.netlib.retrofit_okhttp.interfaces.HttpResponseListener;
import cn.appscomm.uploaddata.UploadDataHelper;
import cn.appscomm.uploaddata.database.SleepDataDB;

/**
 * Created by Administrator on 2016/8/22.
 */
public class SleepTimeShowFragment extends BaseDataChartFragment {

    private SleepTimeView sleepTimeWeekMonthView;
    private SleepTimeDayView sleepTimeDayView;
    private SparseArray<Integer> awakesList = new SparseArray<>();

    @Override
    public void initView(View view) {
        baseFragShowView.setCircleSmallViewIcon(R.mipmap.bedm_ico);
        baseFragShowView.setTvDescript(GlobalApp.getAppContext().getString(R.string.draw_sleep_subtitle));
        sleepTimeWeekMonthView = new SleepTimeView(getActivity());
        sleepTimeDayView = new SleepTimeDayView(getActivity());
        sleepTimeWeekMonthView.setSelectPointListener(new BaseDataChartView.SelectPointListener() {
            @Override
            public void onPointSelected(BaseDataChartView.DataPoint dataPoint) {
                try {
                    int[] hoursMins = UnitTool.getMinToHourMin(UnitTool.getHalfUpValue(dataPoint.yValue, 0).intValue());
                    baseFragShowView.setValue(hoursMins[0] + " " + getResources().getString(R.string.sleeps_hint_hours) + " " + hoursMins[1] + getResources().getString(R.string.sleeps_hint_mins));
                    if (awakesList.get(dataPoint.xValue) != null) {
                        baseFragShowView.setTvDescript(awakesList.get(dataPoint.xValue) + " " + GlobalApp.getAppContext().getString(R.string.draw_sleep_subtitle));
                    } else {
                        baseFragShowView.setTvDescript(0 + " " + GlobalApp.getAppContext().getString(R.string.draw_sleep_subtitle));
                    }
                    baseFragShowView.setCircleSmallVieCurVal(dataPoint.yValue);
                } catch (Resources.NotFoundException e) {
                }
            }

            @Override
            public void onViewTouchDown() {
            }
        });
        sleepTimeDayView.setSelectPointListener(new SleepTimeDayView.SelectPointListener() {
            @Override
            public void onPointSelected(SleepTimeDayView.TimeDayValue culValue) {

            }

            @Override
            public void onViewTouchDown() {
            }
        });
        //baseFragShowView.setCircleSmallViewDrawColor(sleepTimeDayView);
        baseFragShowView.addToTopView(sleepTimeDayView);
    }

    @Override
    protected void loadDefaultData() {
        dateNow = cal.getTime();
        setTimeType(BaseDataChartView.TIME_DAY);
    }

    @Override
    protected void loadData(Date now) {
        setTvDataValue(now);
        Date startTime = now, endTime = now;
        if (timeType == BaseDataChartView.TIME_DAY) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showViews(dateNow, dateNow, 0, 0, new LinkedHashMap<Integer, SleepTimeDayView.TimeDayValue>());
                }
            });
            startTime = DateDrawTool.getCurrentDateStartTime(now);
            endTime = DateDrawTool.getCurrentDateEndTime(now);
//            if (isConnect()) {
//                requestSleepData(startTime, endTime, BaseDataChartView.TIME_DAY);
//            } else {
//                getSleepDayDataFromLocal(startTime, endTime, BaseDataChartView.TIME_DAY);
//            }
            getSleepDayDataFromLocal(startTime, endTime, BaseDataChartView.TIME_DAY);
        } else {
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
//            if (isConnect()) {
//                requestCountSleepData(now, startTime, endTime, timeType);
//            } else {
//                getCountDataFromLocal(startTime, endTime);
//            }
            getCountDataFromLocal(startTime, endTime);
        }
        LogUtil.i(TAG, "startTime=" + startTime + ",endTime=" + endTime);
    }

    private synchronized void getCountDataFromLocal(final Date finalStartTime, Date finalEndTime) {
        final CountSleepObtain countSleepObtain = new CountSleepObtain();
        long startLong = finalStartTime.getTime();
        long endLong = finalEndTime.getTime();
        ArrayList<CountSleepDetail> list = new ArrayList<>();
                /*if (timeType== BaseDataChartView.TIME_WEEK){
                    for (int i = 0; i < 7; i++) {
                        CountSleepDetail countSleepDetail=new CountSleepDetail();
                        countSleepDetail.setUnitFormat(i+1+"");
                        countSleepDetail.setTotalDuration(SleepDataUtils.getSleepData(TimeZoneUtils.getStartAndEndTime(startTime.getTime() + i * 24 * 3600 * 1000)));
                    }
                    countSleepObtain.setSleeps(list);
                }else{*/
        int size = (int) ((endLong - startLong) / 86400000);
        for (int i = 0; i <= size; i++) {
            CountSleepDetail countSleepDetail = new CountSleepDetail();
            if (timeType == BaseDataChartView.TIME_WEEK) {
                String[] weeks = getActivity().getResources().getStringArray(R.array.week_english);
                countSleepDetail.setUnitFormat(weeks[i]);
            } else {
                countSleepDetail.setUnitFormat(i + 1 + "");
            }
            int[] sleepData = SleepDataUtils.getSleepData(TimeZoneUtils.getStartAndEndTime(finalStartTime.getTime() + (long) i * 24 * 3600 * 1000));
            countSleepDetail.setSleepDuration(sleepData[0]);
            countSleepDetail.setAwakeCount(sleepData[1]);
            list.add(countSleepDetail);
        }
        countSleepObtain.setSleeps(list);
//                }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                praseCountSleepData(finalStartTime, countSleepObtain);
            }
        });
    }

    /**
     * 加载本地数据，不能直接显示出来。需要去非UI线程转一圈，让View被构建出来。无奈不
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param timeDay   没用上
     */
    private void getSleepDayDataFromLocal(final Date startTime, final Date endTime, int timeDay) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(500);
//                String bindDeviceId = UserBindDevice.getBindDeviceId(AccountConfig.getUserId());
//                if (!TextUtils.isEmpty(bindDeviceId)) {
//                    List<SleepDataDB> sleepDataDBs = DataSupport.where("userId=? and startTime>? and endTime<?", AccountConfig.getUserId() + "",
//                            simpleDateFormatLocal.format(startTime), simpleDateFormatLocal.format(endTime)).find(SleepDataDB.class);
                List<SleepDataDB> sleepDataDBResult = SleepDataUtils.getSleepDataDBResult(startTime, endTime);
                if (sleepDataDBResult != null && sleepDataDBResult.size() > 0) {
                    List<UploadSleep.SleepDetails> sleeps = UploadDataHelper.getSleepDetailsData(sleepDataDBResult);
                    final QuerySleepObtain querySleepObtain = new QuerySleepObtain();
                    ArrayList<SleepData> sleepDatas = new ArrayList<>();
                    for (int i = 0; i < sleeps.size(); i++) {
                        UploadSleep.SleepDetails sleepDetails = sleeps.get(i);
                        if (sleepDetails.getSleepDuration() <= 0
                                || sleepDetails.getSleepDuration() >= 480) {
                            sleeps.remove(i);
                            i--;
                            continue;
                        }
                        SleepData sleepData = new SleepData();
//                            sleepData.setDeviceId(bindDeviceId);
                        sleepData.setEndTime(sleepDetails.getEndTime());
                        sleepData.setStartTime(sleepDetails.getStartTime());
                        sleepData.setTimeZone(sleepDetails.getTimeZone());
                        sleepData.setQuality(sleepDetails.getQuality() + "");
                        sleepData.setAwakeCount((int) sleepDetails.getAwakeCount());
                        sleepData.setAwakeDuration((int) sleepDetails.getAwakeDuration());
                        sleepData.setDeepDuration((int) sleepDetails.getDeepDuration());
                        sleepData.setLightDuration((int) sleepDetails.getLightDuration());
                        sleepData.setSleepDuration((int) sleepDetails.getSleepDuration());
                        sleepData.setTotalDuration((int) sleepDetails.getTotalDuration());
                        ArrayList<SleepDetail> details = new ArrayList<>();
                        for (int j = 0; j < sleepDetails.getDetails().size(); j++) {
                            SleepDetail sleepDetail = new SleepDetail();
                            sleepDetail.setStartTime(sleeps.get(i).getDetails().get(j).getStartTime());
                            sleepDetail.setStatus(sleeps.get(i).getDetails().get(j).getStatus());
                            details.add(sleepDetail);
                        }
                        sleepData.setDetails(details);
                        sleepDatas.add(sleepData);
                    }
                    querySleepObtain.setSleeps(sleepDatas);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            praseSleepObtain(querySleepObtain);
                        }
                    });
                }
//                } else {
//                    ((BaseActivity) getActivity()).showToast(getString(R.string.no_bind_device));
//                }
            }
        }).start();
    }

    @Override
    public void setSelected() {
        if (baseFragShowView != null) {
            baseFragShowView.setDoOn(3);
        }
    }

    @Override
    public int getCurrentTimeType() {
        return timeType;
    }

    @Override
    public void setTimeType(int time_type) {
        this.timeType = time_type;
        if (timeType == BaseDataChartView.TIME_DAY) {
            baseFragShowView.setRlSleepVisibility(true);
            loadData(dateNow);
        } else {
            baseFragShowView.setRlSleepVisibility(false);
            sleepTimeWeekMonthView.setTimeType(time_type);
            loadData(dateNow);
        }
    }

    @Override
    protected void clickDatePre() {
        if (timeType == BaseDataChartView.TIME_DAY) {
            Date date = DateDrawTool.getCurrentDatePreDate(dateNow);
            cal.setTime(dateNow);
            if (date.getTime() > DateDrawTool.getNowDate().getTime()) {
                ((BaseActivity) getActivity()).showToast(getString(R.string.no_more_data));
            } else {
                dateNow = date;
                loadData(dateNow);
            }
        } else {
            super.clickDatePre();
        }
    }

    @Override
    protected void clickDateNext() {
        if (timeType == BaseDataChartView.TIME_DAY) {
            Date date = DateDrawTool.getCurrentDateNextDate(dateNow);
            cal.setTime(dateNow);
            if (date.getTime() > DateDrawTool.getNowDate().getTime()) {
                ((BaseActivity) getActivity()).showToast(getString(R.string.no_more_data));
            } else {
                dateNow = date;
                loadData(dateNow);
            }
        } else {
            super.clickDateNext();
        }
    }

    @Override
    public void doHttpResponseSportDataShowViews(Date startDate) {
    }


    public void doHttpResponseSportDataShowViews(Date beginDate, Date endDate, float totalDuration, int awakeCount, LinkedHashMap<Integer, SleepTimeDayView.TimeDayValue> values) {

        showViews(beginDate, endDate, totalDuration, awakeCount, values);
    }

    public void doHttpResponseSportDataShowViews(Date startDate, LinkedHashMap<Integer, Float> values) {
        sleepTimeWeekMonthView.setStartDate(startDate);
        sleepTimeWeekMonthView.setTimeType(timeType);
        showViews(values);
    }

    private void showViews(LinkedHashMap<Integer, Float> values) {
        float value = 0;
        int indexKey = 0;
        for (Integer index : values.keySet()) {
            value = values.get(index);
            indexKey = index;
            break;
        }
        int goal = localUserGoal.getGoals_sleep() * 60;//小时，转成分钟
        int[] hoursMins = UnitTool.getMinToHourMin(UnitTool.getHalfUpValue(value, 0).intValue());
        baseFragShowView.setValue(hoursMins[0] + " " + getResources().getString(R.string.sleeps_hint_hours) + " " + hoursMins[1] + getResources().getString(R.string.sleeps_hint_mins));
        baseFragShowView.setCircleSmallViewGoalVal(goal);
        baseFragShowView.setCircleSmallVieCurVal(value);
        if (awakesList.get(indexKey) != null) {
            baseFragShowView.setTvDescript(awakesList.get(indexKey) + " " + GlobalApp.getAppContext().getString(R.string.draw_sleep_subtitle));
        } else {
            baseFragShowView.setTvDescript(0 + " " + GlobalApp.getAppContext().getString(R.string.draw_sleep_subtitle));
        }
        sleepTimeWeekMonthView.setDatas(values);
        baseFragShowView.setCircleSmallViewDrawColor(sleepTimeWeekMonthView.getCircleColor());
        baseFragShowView.addToTopView(sleepTimeWeekMonthView);
    }

    private void showViews(Date beginDate, Date endDate, float sleepDuration, int awakeCount, LinkedHashMap<Integer, SleepTimeDayView.TimeDayValue> values) {
        try {
            int goal = localUserGoal.getGoals_sleep() * 60;//小时，转成分钟
            int[] hoursMins = UnitTool.getMinToHourMin(UnitTool.getHalfUpValue(sleepDuration, 0).intValue());
            baseFragShowView.setValue(hoursMins[0] + " " + getResources().getString(R.string.sleeps_hint_hours) + " " + hoursMins[1] + getResources().getString(R.string.sleeps_hint_mins));
            baseFragShowView.setCircleSmallViewGoalVal(goal);
            baseFragShowView.setCircleSmallVieCurVal(sleepDuration);
            baseFragShowView.setTvDescript(awakeCount + " " + GlobalApp.getAppContext().getString(R.string.draw_sleep_subtitle));
            sleepTimeDayView.setDate(beginDate, endDate);
            sleepTimeDayView.setValueDatas(values);
            baseFragShowView.addToTopView(sleepTimeDayView);
            baseFragShowView.setTvBeginSleep(DateDrawTool.getCurrentDateShowHourMin(beginDate));
            baseFragShowView.setCircleSmallViewDrawColor(Color.parseColor("#05C7D1"));
            baseFragShowView.setTvEndSleep(DateDrawTool.getCurrentDateShowHourMin(endDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestCountSleepData(final Date now, final Date startTime, final Date endTime, final int timeType) {
        final String deviceId = UserBindDevice.getBindDeviceId(AccountConfig.getUserId());
        CountSleepData countSleepData = new CountSleepData();
        countSleepData.setAccountId(AccountConfig.getUserLoginName());
        countSleepData.setCustomerCode(NetLibConstant.DEFAULT_CUSTOMER_CODE);
        countSleepData.setDeviceId(deviceId);
        countSleepData.setStartTime(simpleDateFormatLocal.format(startTime));
        countSleepData.setEndTime(simpleDateFormatLocal.format(endTime));
        countSleepData.setTimeZone("null");
        countSleepData.setQueryType(timeType);
        //现在全部走先显示缓存然后请求网络
//        if (now.getTime() >= startTime.getTime() && now.getTime() <= endTime.getTime()) {//请求时间内
//            showLocalDbCountSleepData(deviceId, countSleepData.getStartTime(), countSleepData.getEndTime(), countSleepData.getQueryType());
//        } else {//历史数据
//            showLocalDbCountSleepData(deviceId, countSleepData.getStartTime(), countSleepData.getEndTime(), countSleepData.getQueryType());
//        }
        showLocalDbCountSleepData(deviceId, countSleepData.getStartTime(), countSleepData.getEndTime(), countSleepData.getQueryType());
//        httpGetCountSleepData(countSleepData);
    }

    private void httpGetCountSleepData(final CountSleepData countSleepData) {
        ((BaseActivity) getActivity()).showBigLoadingProgress(getString(R.string.loading));
        RequestManager.getInstance().countSleepData(countSleepData, new HttpResponseListener() {
            @Override
            public void onResponseSuccess(int statusCode, BaseObtainBean baseObtainBean) {
                ((BaseActivity) getActivity()).dismissLoadingDialog();
                if (HttpCode.isSuccess(statusCode)) {
                    CountSleepObtain countSleepObtain = (CountSleepObtain) baseObtainBean;
                    saveCountSleepData(countSleepObtain, countSleepData.getDeviceId(), countSleepData.getStartTime(), countSleepData.getEndTime(), timeType);
                } else {
//                    ((BaseActivity) getActivity()).showToast(HttpCode.getInstance(GlobalApp.getAppContext()).getCodeString(statusCode));
                    if (statusCode != 8003) {//不上传数据，云平台会提示账号不存在。
                        if (statusCode == 8008) {
                            ((BaseActivity) getActivity()).showToast(getString(R.string.no_bind_device));
                        } else {
                            ((BaseActivity) getActivity()).showToast(HttpCode.getInstance(GlobalApp.getAppContext()).getCodeString(statusCode));
                        }
                    }
                }
            }

            @Override
            public void onResponseError(int statusCode, BasePostBean postBean, Throwable e) {
                ((BaseActivity) getActivity()).dismissLoadingDialog();
//                ((BaseActivity) getActivity()).showToast(HttpCode.getInstance(GlobalApp.getAppContext()).getCodeString(statusCode));
                if (statusCode != 8003) {//不上传数据，云平台会提示账号不存在。
                    if (statusCode == 8008) {
                        ((BaseActivity) getActivity()).showToast(getString(R.string.no_bind_device));
                    } else {
                        ((BaseActivity) getActivity()).showToast(HttpCode.getInstance(GlobalApp.getAppContext()).getCodeString(statusCode));
                    }
                }
            }
        });
    }

    private void saveCountSleepData(final CountSleepObtain countSleepObtain, final String deviceId, final String startTime, final String endTime, final int timeType) {
        CountSleepDB countSleepDB = new CountSleepDB();
        countSleepDB.setContents(gson.toJson(countSleepObtain));
        countSleepDB.setDeviceId(deviceId);
        countSleepDB.setUserId(AccountConfig.getUserId());
        countSleepDB.setDataType(timeType);
        try {
            long start = simpleDateFormatLocal.parse(startTime).getTime() / 1000;
            long end = simpleDateFormatLocal.parse(endTime).getTime() / 1000;
            countSleepDB.setStartTime(start);
            countSleepDB.setEndTime(end);
            if (DataSupport.where("startTime=? and endTime=? and deviceId =? and dataType=?", start + "", end + "", deviceId, timeType + "").find(CountSleepDB.class).size() > 0) {
                ContentValues values = new ContentValues();
                values.put("contents", gson.toJson(countSleepObtain));
                DataSupport.updateAll(CountSleepDB.class, values, "startTime=? and endTime=? and deviceId =? and dataType=?", start + "", end + "", deviceId, timeType + "");
            } else {
                countSleepDB.save();
            }
            praseCountSleepData(simpleDateFormatLocal.parse(startTime), countSleepObtain);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void showLocalDbCountSleepData(final String deviceId, final String startTime, final String endTime, final int timeType) {
        try {
            long starttime = simpleDateFormatLocal.parse(startTime).getTime() / 1000;
            long endtime = simpleDateFormatLocal.parse(endTime).getTime() / 1000;
            CountSleepObtain countSleepObtain = null;
            CountSleepDB countSleepDB = DataSupport.where("startTime<=? and endTime>=? and deviceId =? and dataType=?", starttime + "", endtime + "", deviceId, timeType + "").findFirst(CountSleepDB.class);
            if (countSleepDB != null) {
                countSleepObtain = gson.fromJson(countSleepDB.getContents(), CountSleepObtain.class);
            }
            praseCountSleepData(simpleDateFormatLocal.parse(startTime), countSleepObtain);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void requestSleepData(final Date startTime, final Date endTime, final int timeType) {
        QuerySleep sleep = new QuerySleep();
        sleep.setStartTime(simpleDateFormatLocal.format(startTime));
        sleep.setEndTime(simpleDateFormatLocal.format(endTime));
        sleep.setAccountId(AccountConfig.getUserLoginName());
        final String deviceId = UserBindDevice.getBindDeviceId(AccountConfig.getUserId());
        sleep.setDeviceId(deviceId);
        showLocalDbSleepData(deviceId, deviceId, sleep.getStartTime(), sleep.getEndTime(), timeType);
//        httpGetSleepData(sleep);
    }



    private void saveDaySleepData(final QuerySleepObtain querySleepObtain, final String deviceId, final String startTime, final String endTime) {
        DaySleepDB daySleepDB = new DaySleepDB();
        daySleepDB.setContents(gson.toJson(querySleepObtain));
        daySleepDB.setDeviceId(deviceId);
        daySleepDB.setUserId(AccountConfig.getUserId());
        daySleepDB.setDataType(BaseDataChartView.TIME_DAY);
        for (int i = 0; i < querySleepObtain.getSleeps().size(); i++) {
            querySleepObtain.getSleeps().get(i).setDeviceId(deviceId);
        }
        try {
            long start = simpleDateFormatLocal.parse(startTime).getTime() / 1000;
            long end = simpleDateFormatLocal.parse(endTime).getTime() / 1000;
            daySleepDB.setStartTime(start);
            daySleepDB.setEndTime(end);
            if (DataSupport.where("startTime=? and endTime=? and deviceId =? and dataType=?", start + "", end + "", deviceId, timeType + "").find(DaySleepDB.class).size() > 0) {
                ContentValues values = new ContentValues();
                values.put("contents", gson.toJson(querySleepObtain));
                DataSupport.updateAll(DaySleepDB.class, values, "startTime=? and endTime=? and deviceId =? and dataType=?", start + "", end + "", deviceId, timeType + "");
            } else {
                daySleepDB.save();
            }
            praseSleepObtain(querySleepObtain);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void showLocalDbSleepData(final String userID, final String deviceId, final String startTime, final String endTime, final int timeType) {
        try {
            long start = simpleDateFormatLocal.parse(startTime).getTime() / 1000;
            long end = simpleDateFormatLocal.parse(endTime).getTime() / 1000;
            QuerySleepObtain querySleepObtain = null;
            DaySleepDB daySleepDB = DataSupport.where("startTime<=? and endTime>=? and deviceId =? and dataType=? and userId=?", start + "", end + "", deviceId, timeType + "", userID).findFirst(DaySleepDB.class);
            if (daySleepDB != null) {
                querySleepObtain = gson.fromJson(daySleepDB.getContents(), QuerySleepObtain.class);
            }
            praseSleepObtain(querySleepObtain);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void praseSleepObtain(QuerySleepObtain sleepObtain) {
        LinkedHashMap<Integer, SleepTimeDayView.TimeDayValue> values = new LinkedHashMap<>();
        int sleepDuration = 0, awakeCount = 0;
        Date beginDate = new Date(), endDate = new Date();
        if (sleepObtain != null) {
            int index = 0;
            try {
                if (sleepObtain.getSleeps() != null && sleepObtain.getSleeps().size() > 0) {
                    beginDate = simpleDateFormatLocal.parse(sleepObtain.getSleeps().get(0).getStartTime());
                    endDate = simpleDateFormatLocal.parse(sleepObtain.getSleeps().get(0).getEndTime());
                }
                if (sleepObtain.getSleeps() != null && sleepObtain.getSleeps().size() > 0) {
                    for (int i = 0; i < sleepObtain.getSleeps().size(); i++) {
                        SleepData sleep = sleepObtain.getSleeps().get(i);
                        if (sleep != null && sleep.getDetails() != null && sleep.getDetails().size() > 0) {
                            Date nowBeginDate = simpleDateFormatLocal.parse(sleep.getStartTime());
                            Date nowEndDate = simpleDateFormatLocal.parse(sleep.getEndTime());
                            if (nowBeginDate.getTime() < beginDate.getTime()) {
                                beginDate = nowBeginDate;
                            }
                            if (nowEndDate.getTime() > endDate.getTime()) {
                                endDate = nowEndDate;
                            }
                            sleepDuration += sleep.getSleepDuration();
                            awakeCount += sleep.getAwakeCount();
                            SleepTimeDayView.TimeDayValue timeDayValue = null;
                            Date lastDate = null, nowDate = null;
                            int lastStatus = -1;
                            for (int j = 0; j < sleep.getDetails().size(); j++) {
                                timeDayValue = new SleepTimeDayView.TimeDayValue();
                                SleepDetail detail = sleep.getDetails().get(j);
//                                saveDaySleepDetail(sleep, detail);
                                nowDate = simpleDateFormatLocal.parse(detail.getStartTime());
                                switch (detail.getStatus()) {
                                    case 16:
                                        lastStatus = detail.getStatus();
                                        lastDate = nowDate;
                                        break;
                                    default:
                                        timeDayValue.value = (nowDate.getTime() - lastDate.getTime()) / 60000;
                                        timeDayValue.startDate = lastDate;
                                        timeDayValue.type = lastStatus;
                                        values.put(index, timeDayValue);
                                        index++;
                                        lastDate = nowDate;
                                        lastStatus = detail.getStatus();
                                        break;
                                }
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        doHttpResponseSportDataShowViews(beginDate, endDate, sleepDuration, awakeCount, values);
    }

    private SleepTimeDayView.TimeDayValue praseSleepDetail(List<SleepDetail> sleepDetails) {
        if (sleepDetails.get(0).getStatus() != 16 || sleepDetails.get(sleepDetails.size() - 1).getStatus() != 17) {
            return null;
        }
        SleepTimeDayView.TimeDayValue timeDayValue = null;
        try {
            Date lastDate = null, nowDate = null;
            for (int j = 0; j < sleepDetails.size(); j++) {
                timeDayValue = new SleepTimeDayView.TimeDayValue();
                SleepDetail detail = sleepDetails.get(j);
                nowDate = simpleDateFormatLocal.parse(detail.getStartTime());
                switch (detail.getStatus()) {
                    case 16:
                        lastDate = nowDate;
                        break;
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        timeDayValue.value = (nowDate.getTime() - lastDate.getTime()) / 60000;
                        timeDayValue.startDate = nowDate;
                        timeDayValue.type = detail.getStatus();
                        break;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeDayValue;
    }


    private void praseCountSleepData(Date startDate, CountSleepObtain countSleepObtain) {
        LinkedHashMap<Integer, Float> values = new LinkedHashMap<>();
        awakesList.clear();
        if (countSleepObtain != null) {
            List<CountSleepDetail> sleepDetailsList = countSleepObtain.getSleeps();
            if (sleepDetailsList != null) {
                String[] weeks = getActivity().getResources().getStringArray(R.array.week_english);
                for (CountSleepDetail sleepDetails : sleepDetailsList) {
                    if (sleepDetails != null && !TextUtils.isEmpty(sleepDetails.getUnitFormat())) {
                        String unitFormat = sleepDetails.getUnitFormat();
                        if (timeType == BaseDataChartView.TIME_WEEK) {
                            for (int i = 0; i <= weeks.length - 1; i++) {
                                if (unitFormat.equals(weeks[i])) {
                                    values.put(i, sleepDetails.getSleepDuration());
                                    awakesList.put(i, UnitTool.getHalfUpValue(sleepDetails.getAwakeCount(), 0).intValue());
                                }
                            }
                        } else if (timeType == BaseDataChartView.TIME_MONTH) {
                            int dateIndex = Integer.parseInt(unitFormat);
                            values.put(dateIndex - 1, sleepDetails.getSleepDuration());
                            awakesList.put(dateIndex - 1, UnitTool.getHalfUpValue(sleepDetails.getAwakeCount(), 0).intValue());
                        }
                    }
                }
            }
        }
        doHttpResponseSportDataShowViews(startDate, values);
    }
}
