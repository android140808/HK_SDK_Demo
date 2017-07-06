package cn.appscomm.l38t.UI.showView.datachart;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.utils.viewUtil.DateDrawTool;


/**
 * desc:详细数据显示图表数据的基类
 * Created by Administrator on 2016/8/22.
 */
public class BaseDataChartView extends View {

    public static final int TIME_DAY = 1;//天显示
    public static final int TIME_WEEK = 2;//周显示
    public static final int TIME_MONTH = 3;//月显示

    protected int circleColor = Color.parseColor("#a1d82a");
    protected int xValueColor = Color.WHITE;
    protected int yLineColor = Color.parseColor("#a1d82a");

    private int self_width, self_height;
    private boolean isFirstDraw = true;
    private LinkedHashMap<Integer, Float> dataList;
    private List<String> timeDatas;
    private int timeType;
    private Date startDate;//当前选择日期
    private List<DataPoint> valuePointList = new ArrayList<DataPoint>();
    public final static int paddingLeft = 20;
    public final static int paddingRight = 30;
    private final int paddingTop = dp2px(10);//上面,底部留20dp
    private final int marginBottom = dp2px(40);
    private final int drawLineMarginTop = dp2px(40);
    private float maxValue;
    private String unit;
    private int halfUpNum;//保留小数点位数

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private SelectPointListener selectPointListener;
    private boolean drawSelectedLine = true;
    private boolean onTouchFlag = false;
    private float selectedLineX = 0;
    private int selectIndex = 0;


    public BaseDataChartView(Context context) {
        super(context);
        this.setFocusable(true);
        initLength();
    }

    public BaseDataChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseDataChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public BaseDataChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    /**
     * 获取自身宽高
     */
    public void initLength() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                self_width = getWidth();
                self_height = getHeight();
                if (isFirstDraw) {
                    if (dataList == null) {
                        dataList = new LinkedHashMap<Integer, Float>();
                    }
                    if (timeDatas == null) {
                        timeDatas = new ArrayList<String>();
                    }
                    isFirstDraw = false;
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        performClick();
    }

    private void initTimeDatas() {
        if (timeDatas == null) {
            timeDatas = new ArrayList<String>();
        }
        timeDatas.clear();
        if (timeType == TIME_WEEK) {
            String[] weeks = getContext().getResources().getStringArray(R.array.week_desc);
            for (int i = 0; i < weeks.length; i++) {
                timeDatas.add(weeks[i]);
            }
        } else if (timeType == TIME_MONTH) {
            if (startDate == null) {
                startDate = DateDrawTool.getNowDate();
            }
            int dayCount = DateDrawTool.getDaysOfMonth(startDate);
            for (int i = 1; i <= dayCount; i++) {
                timeDatas.add(i + "");
            }
        }
    }


    private void convertData2Point() {
        if (timeDatas == null) {
            return;
        }
        float radio = getCircleRadio();
        float rateX = getXlineRateX(radio);
        valuePointList.clear();
        //注意：点的x轴是总长度-相对右边的距离算得。
        for (int i = 0; i <= timeDatas.size() - 1; i++) {
//            Float value = dataList.get(i);
//            if (value != null && value.floatValue() > 0) {
//                float px = rateX * i + paddingLeft + radio;
//                float py = getDrawValueLineHeigh(dataList.get(i));
//                valuePointList.add(new DataPoint(i, px, py, i, value));
//            }
            Float value = dataList.get(i);
            if (value == null) {
                value = new Float(0);
            }
            float px = rateX * i + paddingLeft + radio;
            float py = getDrawValueLineHeigh(value);
            valuePointList.add(new DataPoint(i, px, py, i, value));
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float nowX = event.getX();
                if (selectedLineX != nowX) {
                    onTouchFlag = true;
                    drawSelectedLine = true;
                    selectedLineX = nowX;
                    invalidate();
                    if (selectPointListener != null) {
                        selectPointListener.onViewTouchDown();
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_MASK:
            case MotionEvent.ACTION_POINTER_DOWN:
                onTouchFlag = false;
                //drawSelectedLine = false;
                //postInvalidate();
                break;
        }
        return true;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);//防止与viewpager左右滑动冲突
        return super.dispatchTouchEvent(event);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBottom(canvas);
        drawYLines(canvas);
        drawXLinePoint(canvas);
        drawValuePoint(canvas);
        if (drawSelectedLine) {
            drawSelectedLine(canvas);
        }
    }


    /**
     * 底部的一块黑色部分
     *
     * @param canvas
     */
    private void drawBottom(Canvas canvas) {
        mPaint.setColor(Color.parseColor("#371F16"));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, self_height - marginBottom, self_width, self_height, mPaint);
    }

    /**
     * 横线
     *
     * @param canvas
     */
    private void drawYLines(Canvas canvas) {
        // 画横线
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(yLineColor);
        int totalNum = 5;//画的总条数
        for (int i = 0; i <= totalNum; i++) {
            if (i == 0) continue;//第一根不画
            float y = getDrawLineHeigh(totalNum, i);
            canvas.drawRect(0, y, self_width, y + 2, mPaint);
        }
    }

    private void drawValuePoint(Canvas canvas) {
        convertData2Point();
        mPaint.setStyle(Paint.Style.FILL);
        float radio = getCircleRadio();
        for (int i = 0; i <= timeDatas.size() - 1; i++) {
            if (i < valuePointList.size()) {
                mPaint.setColor(circleColor);
                float x1 = valuePointList.get(i).px - radio;
                float x2 = x1 + radio * 2;
                canvas.drawRect(x1, valuePointList.get(i).py, x2, self_height - marginBottom, mPaint);
            }
        }
    }

    private void drawXLinePoint(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        initTimeDatas();
        float radio = getCircleRadio();
        float rateX = getXlineRateX(radio);
        for (int i = 0; i < timeDatas.size(); i++) {
            float px = rateX * i + paddingLeft + radio;
            mPaint.setColor(circleColor);
            int py = (self_height - marginBottom) + marginBottom / 2;
            canvas.drawCircle(px, py, radio, mPaint);// 小圆
            mPaint.setColor(xValueColor);
            mPaint.setTextAlign(Paint.Align.CENTER);
            String text = "" + timeDatas.get(i);
            Rect textBounds = new Rect();
            mPaint.setTextSize(getCircleTextSize());
            mPaint.getTextBounds(text, 0, text.length(), textBounds);//get text bounds, that can get the text width and height
            int textHeight = textBounds.bottom - textBounds.top;
            canvas.drawText(text, px, (self_height - marginBottom) + marginBottom / 2 + textHeight / 2, mPaint);
        }
        //canvas.drawRect(paddingLeft, self_height - marginBottom+marginBottom/2, self_width-paddingRight, self_height - marginBottom+marginBottom/2 + 2, mPaint);
    }

    /**
     * draw Bottom Selected Circle
     *
     * @param canvas
     * @param px px
     * @param txt
     */
    private void drawBottomSelectedCircle(Canvas canvas, float px, String txt) {
        mPaint.setColor(Color.WHITE);
        int py = (self_height - marginBottom) + marginBottom / 2;
        canvas.drawCircle(px, py, marginBottom / 2.5F, mPaint);// 小圆
        mPaint.setColor(yLineColor); // 圆内字体颜色
        mPaint.setTextAlign(Paint.Align.CENTER);
        String text = "" + txt;
        // mPaint.setColor(Color.BLACK);
        Rect textBounds = new Rect();
        float textSize = (float) ((marginBottom * 1.5) / 5.0);
        mPaint.setTextSize(textSize);
        mPaint.getTextBounds(text, 0, text.length(), textBounds);//get text bounds, that can get the text width and height
        int textHeight = textBounds.bottom - textBounds.top;
        canvas.drawText(text, px, (self_height - marginBottom) + marginBottom / 2 + textHeight / 2, mPaint);
    }

    private void drawTopValue(Canvas canvas, float px, String dateString, String valueString) {
        mPaint.setColor(Color.BLACK);
        Rect textBounds = new Rect();
        float textSize = (float) ((marginBottom * 1.5) / 5.0);
        mPaint.setTextSize(textSize);
        String maxString = dateString;
        if (maxString.length() < valueString.length()) {
            maxString = valueString;
        }
        mPaint.getTextBounds(maxString, 0, maxString.length(), textBounds);//get text bounds, that can get the text width and height
        int textHeight = textBounds.bottom - textBounds.top;
        float x = px;
        if (px < self_width / 2) {
            x = x + getCircleRadio();
            mPaint.setTextAlign(Paint.Align.LEFT);
        } else {
            x = x - getCircleRadio();
            mPaint.setTextAlign(Paint.Align.RIGHT);
        }
        canvas.drawText(dateString, x, paddingTop + textHeight, mPaint);
        canvas.drawText(valueString, x, paddingTop + textHeight * 2.2F, mPaint);
    }

    private float getXlineRateX(float radio) {
        float width = (self_width - paddingLeft - paddingRight) - radio * 2;
        return width / (timeDatas.size() - 1);
    }

    private float getCircleRadio() {
        float radio = 0;
        switch (timeType) {
            case TIME_DAY:
                break;
            case TIME_WEEK:
                radio = marginBottom / 2.5F;
                break;
            case TIME_MONTH:
                radio = marginBottom / 7.5F;
                break;
        }
        return radio;
    }

    private float getCircleTextSize() {
        float textSize = 0;
        switch (timeType) {
            case TIME_DAY:
                break;
            case TIME_WEEK:
                textSize = (float) ((marginBottom * 1.5) / 5.0);
                break;
            case TIME_MONTH:
                textSize = (float) ((marginBottom * 1.5) / 8.0);
                break;
        }
        return textSize;
    }

    /**
     * 画竖线
     *
     * @param canvas
     */
    private void drawSelectedLine(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        // paint.setColor(getResources().getColor(R.color.gray));
        paint.setColor(yLineColor); // 设置可移动竖线颜色
        paint.setStrokeWidth(3);
        float radio = getCircleRadio();
        for (DataPoint point : valuePointList) {
            if (point.px - radio <= selectedLineX && selectedLineX <= point.px + radio) {
                canvas.drawLine(point.px, paddingTop, point.px, self_height - marginBottom, paint);
                drawBottomSelectedCircle(canvas, point.px, timeDatas.get(point.xValue));
//                if (point.yValue > 0) {
//                    drawTopValue(canvas, point.px, getSelectDataNowString(point.xValue), " " + UnitTool.getHalfUpValue(point.yValue, halfUpNum) + " " + unit);
//                } else {
//                    drawTopValue(canvas, point.px, getSelectDataNowString(point.xValue), "");
//                }
                if (selectPointListener != null) {
                    selectPointListener.onPointSelected(point);
                }
            }
        }
    }

    private String getSelectDataNowString(int xValue) {
        String result = "";
        switch (timeType) {
            case TIME_DAY:

                break;
            case TIME_WEEK:
                if (onTouchFlag) {
                    selectIndex = xValue;
                }
                Date date_w = DateDrawTool.getCurrentDateAfterDate(startDate, xValue);
                result = DateDrawTool.dateToStr(date_w, "MM/dd") + " " + getWeekOfDate(date_w);
                break;
            case TIME_MONTH:
                int index = timeDatas.indexOf(xValue + "") + 1;
                if (onTouchFlag) {
                    selectIndex = xValue;
                }
                Date date_m = DateDrawTool.getCurrentDateAfterDate(startDate, index);
                result = DateDrawTool.dateToStr(date_m, "MM/dd") + " " + getWeekOfDate(date_m);
                break;
        }
        return result;
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public String getWeekOfDate(Date dt) {
        String[] weeks = getContext().getResources().getStringArray(R.array.week_desc);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 2;
        if (w < 0)
            w = 6;
        return weeks[w];
    }


    public void setDatas(LinkedHashMap<Integer, Float> datas) {
        if (this.dataList == null) {
            this.dataList = new LinkedHashMap<Integer, Float>();
        }
        this.dataList.clear();
        for (Integer key : datas.keySet()) {
            dataList.put(key, datas.get(key));
        }
        convertData2Point();
        invalidate();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                selectIndex();
            }
        }, 300);
    }

    public void setTimeDatas(List<String> timeDatas) {
        this.timeDatas = timeDatas;
        invalidate();
    }

    public int getTimeType() {
        return timeType;
    }

    public void setTimeType(int timeType) {
        this.timeType = timeType;
        initTimeDatas();
        switch (timeType) {
            case TIME_WEEK:
                if (startDate != null) {
                    Date startTime = DateDrawTool.getCurrentMonday(startDate);
                    Date endTime = DateDrawTool.getCurrentSunday(startDate);
                    if (new Date().getTime() >= startTime.getTime() && new Date().getTime() <= endTime.getTime()) {
                        selectIndex = DateDrawTool.getWeekIndexOfDate(new Date());
                    }
                }
                break;
            case TIME_MONTH:
                if (startDate != null) {
                    Date startTime = DateDrawTool.getCurrentDateMonthBeginDate(startDate);
                    Date endTime = DateDrawTool.getCurrentDateMonthEndDate(startDate);
                    if (new Date().getTime() >= startTime.getTime() && new Date().getTime() <= endTime.getTime()) {
                        selectIndex = DateDrawTool.getCurrentDateDayOfMonth(new Date()) - 1;
                    }
                }
                break;
        }
        postInvalidate();
    }

    public void setHalfUpNum(int halfUpNum) {
        this.halfUpNum = halfUpNum;
    }

    public void selectIndex() {
        if (valuePointList != null && valuePointList.size() > 0) {
            DataPoint dataPoint = valuePointList.get(0);
            if (dataPoint != null && selectIndex <= dataPoint.timeIndex) {
                selectIndex = 0;
            }
            if (timeType == TIME_WEEK && selectIndex >= valuePointList.size()) {
                selectIndex = 0;
            }
            if (selectIndex == 0) {
                selectedLineX = valuePointList.get(selectIndex).px;
            } else if (selectIndex > 0) {
                selectedLineX = getSelectedIndexPx(selectIndex);
            }
            postInvalidate();
        }
    }

    private float getSelectedIndexPx(int index) {
        if (index >= timeDatas.size() || index < 0) {
            index = 0;
        }
        float radio = getCircleRadio();
        float rateX = getXlineRateX(radio);
        float px = rateX * index + paddingLeft + radio;
        return px;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    private float getDrawValueLineHeigh(float value) {
        //计算方法：上顶部+y占据画线界面的高度
        float total = (float) ((self_height - paddingTop - marginBottom) * 1.0);
        if (value == 0) {
            return total + paddingTop;
        }
        total = total - drawLineMarginTop;
        float avg = total;
        maxValue = getMaxValue();
        if (maxValue > 0) {
            avg = total / maxValue;
        }
        return (total - avg * value) + paddingTop + drawLineMarginTop;
    }

    private float getMaxValue() {
        float temp = 0;
        for (Integer index : dataList.keySet()) {
            temp = dataList.get(index);//先找第一个
            break;
        }
        for (Integer key : dataList.keySet()) {
            float value = dataList.get(key);
            if (value >= temp) {
                temp = value;
            }
        }
        return temp;
    }

    public BaseDataChartView setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    public BaseDataChartView setUnit(String unitString) {
        this.unit = unitString;
        return this;
    }

    private float getMinValue() {
        float temp = 0;
        for (Integer index : dataList.keySet()) {
            temp = dataList.get(index);//先找第一个
            break;
        }
        for (Integer key : dataList.keySet()) {
            float value = dataList.get(key);
            if (value <= temp) {
                temp = value;
            }
        }
        return temp;
    }

    /**
     * 平均Y高度
     *
     * @return
     */
    private float getDrawLineHeigh(int totalNum, int index) {
        //计算方法：上顶部+y占据画线界面的高度
        float total = (float) ((self_height - paddingTop - marginBottom) * 1.0);
        float avg = total / totalNum;
        return avg * index + paddingTop;
    }

    public int getCircleColor() {
        return circleColor;
    }

    public int getyLineColor() {
        return yLineColor;
    }

    private int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public interface SelectPointListener {
        void onPointSelected(DataPoint dataPoint);

        void onViewTouchDown();
    }

    public void setSelectPointListener(SelectPointListener selectPointListener) {
        this.selectPointListener = selectPointListener;
    }


    public class DataPoint {
        public float px, py;
        public int xValue;
        public float yValue;
        public int timeIndex;

        public DataPoint(int timeIndex, float px, float py, int xValue, float yValue) {
            this.timeIndex = timeIndex;
            this.px = px;
            this.py = py;
            this.xValue = xValue;
            this.yValue = yValue;
        }

        @Override
        public String toString() {
            return "px:" + px + ", py:" + py + ",xValue=" + xValue + ",yValue=" + yValue;
        }
    }
}
