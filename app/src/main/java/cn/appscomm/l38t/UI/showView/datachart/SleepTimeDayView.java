package cn.appscomm.l38t.UI.showView.datachart;

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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.utils.viewUtil.DateDrawTool;

/**
 * Created by Administrator on 2016/8/29.
 */
public class SleepTimeDayView extends View {

    public static final int SLEEP_D = 0;
    public static final int SLEEP_L = 1;
    public static final int SLEEP_A = 2;
    public static final int SLEEP_ENTER = 3;

    protected int yLineColor = Color.parseColor("#D7E2ED");
    protected int DEEP_COLOR = Color.parseColor("#68D8D2");
    protected int LIGHT_COLOR = Color.parseColor("#05C7D1");
    protected int AWAKE_COLOR = Color.parseColor("#E60012");

    private int self_width, self_height;
    private boolean isFirstDraw = true;
    private LinkedHashMap<Integer, TimeDayValue> sleepDatas;
    private Date startDate, endDate;
    private List<TimeDayValue> pointList = new ArrayList<TimeDayValue>();
    public final static int paddingLeft = 20;
    public final static int paddingRight = 30;
    private final int paddingTop = dp2px(10);//上面,底部留20dp
    private final int drawLineMarginTop = dp2px(40);
    private final int marginBottom = dp2px(40);

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private SelectPointListener selectPointListener;
    private boolean drawSelectedLine = true;
    private float selectedLineX = 0;

    public SleepTimeDayView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context);
    }

    public SleepTimeDayView(Context context, AttributeSet attrs) {
        this(context);
    }

    public SleepTimeDayView(Context context) {
        super(context);
        this.setFocusable(true);
        initLength();
    }

    /**
     * 获取自身宽高
     */
    private void initLength() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                self_width = getWidth();
                self_height = getHeight();
                if (isFirstDraw) {
                    if (sleepDatas == null) {
                        sleepDatas = new LinkedHashMap<Integer, TimeDayValue>();
                    }
                    init();
                    isFirstDraw = false;
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        performClick();
    }


    private void init() {
        startDate = new Date();
        endDate = new Date();
    }

    private void convertData2Point() {
        pointList.clear();
        //注意：点的x轴是总长度-相对右边的距离算得。
        for (Integer index : sleepDatas.keySet()) {
            TimeDayValue timeDayValue = sleepDatas.get(index);
            if (timeDayValue != null && timeDayValue.value > 0) {
                timeDayValue.x1 = getDrawXWidth(timeDayValue.startDate);
                timeDayValue.y1 = (int) getDrawLineHeigh(5, 3 - timeDayValue.type);
                timeDayValue.x2 = timeDayValue.value * getRateX();
                timeDayValue.y2 = timeDayValue.y1;
                pointList.add(timeDayValue);
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                drawSelectedLine = true;
                selectedLineX = event.getX();
                postInvalidate();
                if (selectPointListener != null) {
                    selectPointListener.onViewTouchDown();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_MASK:
            case MotionEvent.ACTION_POINTER_DOWN:
//                drawSelectedLine = false;
//                postInvalidate();
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


    private void drawBottom(Canvas canvas) {
        mPaint.setColor(Color.parseColor("#371F16"));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, self_height - marginBottom, self_width, self_height, mPaint);
    }

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
        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i <= pointList.size() - 1; i++) {
            mPaint.setColor(getValuePainColor(pointList.get(i).type));
            canvas.drawRect(pointList.get(i).x1, pointList.get(i).y1, pointList.get(i).x1 + pointList.get(i).x2, self_height - marginBottom, mPaint);
        }
    }

    private int getValuePainColor(int value) {
        int color = DEEP_COLOR;
        switch (value) {
            case SLEEP_L:
                color = LIGHT_COLOR;
                break;
            case SLEEP_D:
                color = DEEP_COLOR;
                break;
            case SLEEP_ENTER:
            case SLEEP_A:
                color = AWAKE_COLOR;
                break;
        }
        return color;
    }

    private void drawXLinePoint(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        float textSize = (float) ((marginBottom * 1.5) / 4.0);
        mPaint.setTextSize(textSize);
        String startTimeString = DateDrawTool.getCurrentDateShowHourMin(startDate);
        String endTimeString = DateDrawTool.getCurrentDateShowHourMin(endDate);
        float startPx = getStartX();
        float endPx = getEndX();
        if (startTimeString.equalsIgnoreCase(endTimeString)) {
            float rate = (endPx - startPx) / 4;
            canvas.drawRect(startPx, self_height - marginBottom, startPx + 4, self_height - (marginBottom * 3 / 4), mPaint);
            canvas.drawText("00:00", startPx, self_height - (marginBottom + 12) / 4, mPaint);
            canvas.drawRect(startPx, self_height - marginBottom / 4, startPx + 4, self_height, mPaint);

            canvas.drawRect(startPx + rate, self_height - marginBottom, startPx + rate + 4, self_height - (marginBottom * 3 / 4), mPaint);
            canvas.drawText("06:00", startPx + rate, self_height - (marginBottom + 12) / 4, mPaint);
            canvas.drawRect(startPx + rate, self_height - marginBottom / 4, startPx + rate + 4, self_height, mPaint);

            canvas.drawRect(startPx + rate * 2, self_height - marginBottom, startPx + rate * 2 + 4, self_height - (marginBottom * 3 / 4), mPaint);
            canvas.drawText("12:00", startPx + rate * 2, self_height - (marginBottom + 12) / 4, mPaint);
            canvas.drawRect(startPx + rate * 2, self_height - marginBottom / 4, startPx + rate * 2 + 4, self_height, mPaint);

            canvas.drawRect(startPx + rate * 3, self_height - marginBottom, startPx + rate * 3 + 4, self_height - (marginBottom * 3 / 4), mPaint);
            canvas.drawText("18:00", startPx + rate * 3, self_height - (marginBottom + 12) / 4, mPaint);
            canvas.drawRect(startPx + rate * 3, self_height - marginBottom / 4, startPx + rate * 3 + 4, self_height, mPaint);

            canvas.drawRect(endPx, self_height - marginBottom, endPx + 4, self_height - (marginBottom * 3 / 4), mPaint);
            canvas.drawText("23:59", endPx, self_height - (marginBottom + 12) / 4, mPaint);
            canvas.drawRect(endPx, self_height - marginBottom / 4, endPx + 4, self_height, mPaint);
        } else {
            canvas.drawRect(startPx, self_height - marginBottom, startPx + 4, self_height - (marginBottom * 3 / 4), mPaint);
            canvas.drawText(startTimeString, startPx, self_height - (marginBottom + 12) / 4, mPaint);
            canvas.drawRect(startPx, self_height - marginBottom / 4, startPx + 4, self_height, mPaint);

            canvas.drawRect(endPx, self_height - marginBottom, endPx + 4, self_height - (marginBottom * 3 / 4), mPaint);
            canvas.drawText(endTimeString, endPx, self_height - (marginBottom + 12) / 4, mPaint);
            canvas.drawRect(endPx, self_height - marginBottom / 4, endPx + 4, self_height, mPaint);
        }
    }

    private void drawSelectedLine(Canvas canvas) {
        // 画横线
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.gray));
        paint.setStrokeWidth(3);
        canvas.drawLine(selectedLineX, paddingTop, selectedLineX, self_height - marginBottom, paint);
        checkPoint(canvas, selectedLineX);
    }

    private void checkPoint(Canvas canvas, float x) {
        if (selectPointListener != null) {
            float nowX = x;
            for (int i = 0; i < pointList.size(); i++) {
                TimeDayValue timeDayValue = pointList.get(i);
                if (nowX >= timeDayValue.x1 && nowX <= (timeDayValue.x1 + timeDayValue.x2)) {
                    String date = getDateString(timeDayValue.startDate, timeDayValue.x1, nowX);
                    String type = getValueText(timeDayValue.type);
                    drawTopValue(canvas, nowX, date, type);
                    selectPointListener.onPointSelected(timeDayValue);
                }
            }
        }
    }

    private String getDateString(Date startDate, float beginX, float nowX) {
        String format = "MM/dd HH:mm";
        String result = DateDrawTool.dateToStr(startDate, format);
        float rateX = getRateX();
        int min = (int) ((nowX - beginX) / rateX);
        startDate = DateDrawTool.getCurrentDateAddMin(startDate, min);
        return DateDrawTool.dateToStr(startDate, format);
    }

    private String getValueText(int value) {
        String result = getResources().getString(R.string.awake_sleep);
        switch (value) {
            case SLEEP_L:
                result = getResources().getString(R.string.light_sleep);
                break;
            case SLEEP_D:
                result = getResources().getString(R.string.deep_sleep);
                break;
            case SLEEP_ENTER:
            case SLEEP_A:
                result = getResources().getString(R.string.awake_sleep);
                break;
        }
        return result;
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
            x = x + 10;
            //x = x + getRateX();
            mPaint.setTextAlign(Paint.Align.LEFT);
        } else {
           // x = x - getRateX();
            x = x - 10;
            mPaint.setTextAlign(Paint.Align.RIGHT);
        }
        canvas.drawText(dateString, x, paddingTop + textHeight, mPaint);
        canvas.drawText(valueString, x, paddingTop + textHeight * 3F, mPaint);
    }

    public void setValueDatas(LinkedHashMap<Integer, TimeDayValue> valueDatas) {
        if (this.sleepDatas == null) {
            this.sleepDatas = new LinkedHashMap<>();
        }
        this.sleepDatas.clear();
        for (Integer index : valueDatas.keySet()) {
            this.sleepDatas.put(index, valueDatas.get(index));
        }
        convertData2Point();
        invalidate();
    }

    public void setDate(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        invalidate();
    }

    private float getRateX() {
        float startPx = getStartX();
        float endPx = getEndX();
        long min = (endDate.getTime() - startDate.getTime()) / 60000;
        float drawWidth = (float) (endPx - startPx) / (min);
        return drawWidth;
    }

    private float getStartX() {
        float textSize = (float) ((marginBottom * 1.5) / 4.0);
        mPaint.setTextSize(textSize);
        String startTimeString = DateDrawTool.getCurrentDateShowHourMin(startDate);
        Rect textBounds = new Rect();
        mPaint.getTextBounds(startTimeString, 0, startTimeString.length(), textBounds);//get text bounds, that can get the text width and height
        int textWidth = textBounds.right - textBounds.left;
        float startPx = paddingLeft + textWidth / 2;
        return startPx;
    }

    private float getEndX() {
        float textSize = (float) ((marginBottom * 1.5) / 4.0);
        mPaint.setTextSize(textSize);
        String startTimeString = DateDrawTool.getCurrentDateShowHourMin(endDate);
        Rect textBounds = new Rect();
        mPaint.getTextBounds(startTimeString, 0, startTimeString.length(), textBounds);//get text bounds, that can get the text width and height
        int textWidth = textBounds.right - textBounds.left;
        float endPx = self_width - paddingRight - textWidth / 2;
        return endPx;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    /**
     * 平均Y高度
     *
     * @return
     */
    private float getDrawLineHeigh(int totalNum, int index) {
        //计算方法：上顶部+y占据画线界面的高度
        float total = (float) ((self_height - paddingTop - marginBottom) * 1.0);
        total = total - drawLineMarginTop;
        float avg = total / totalNum;
        return avg * index + paddingTop + drawLineMarginTop;
    }

    /**
     * 平均X高度
     *
     * @return
     */
    private float getDrawXWidth(Date beginDate) {
        long min = (beginDate.getTime() - this.startDate.getTime()) / 60000;
        return getStartX() + min * getRateX();
    }

    private int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public interface SelectPointListener {
        void onPointSelected(TimeDayValue timeDayValue);

        void onViewTouchDown();
    }

    public void setSelectPointListener(SelectPointListener selectPointListener) {
        this.selectPointListener = selectPointListener;
    }

    public static class TimeDayValue {
        public float x1, x2, y1, y2;
        public Date startDate;
        public float value;
        public int type;

        @Override
        public String toString() {
            return "TimeDayValue{" +
                    "x1=" + x1 +
                    ", x2=" + x2 +
                    ", y1=" + y1 +
                    ", y2=" + y2 +
                    ", startDate=" + startDate +
                    ", value=" + value +
                    ", type=" + type +
                    '}';
        }
    }
}
