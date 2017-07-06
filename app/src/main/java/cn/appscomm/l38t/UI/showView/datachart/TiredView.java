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
import cn.appscomm.l38t.UI.showView.Point;
import cn.appscomm.l38t.utils.viewUtil.DateDrawTool;
import cn.appscomm.netlib.bean.mood.MoodFatigueData;


/**
 * Created by weiliu on 2016/7/26.
 */
public class TiredView extends View {

    public static final int ENERGETIC = 0;
    public static final int MODERATE = 1;
    public static final int SLIGHT = 2;
    public static final int SERIOUS = 3;

    private int self_width, self_height;
    private boolean isFirstDraw = true;
    private LinkedHashMap<Integer, MoodFatigueData> tiredDatas;
    private List<String> timeDatas;
    private List<Point> pointList = new ArrayList<Point>();
    public final static int paddingLeft = 20;
    public final static int paddingRight = 30;
    private final static int pillarWidth = 20;
    private final int paddingTop = dp2px(10);//上面,底部留20dp
    private final int drawLineMarginTop = dp2px(40);
    private final int marginBottom = dp2px(40);
    private int min_val = MoodFatigueData.FATIGUE_MIN, max_val = MoodFatigueData.FATIGUE_MAX + 1;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private SelectPointListener selectPointListener;
    private boolean drawSelectedLine = true;
    private float selectedLineX = 0;
    private static final int TOTAL_M = 1440;

    public TiredView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context);
    }

    public TiredView(Context context, AttributeSet attrs) {
        this(context);
    }

    public TiredView(Context context) {
        super(context);
        this.setFocusable(true);
        initLength();
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
                    if (tiredDatas == null) {
                        tiredDatas = new LinkedHashMap<Integer, MoodFatigueData>();
                    }
                    initTimeDatas();
                    init();
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
            timeDatas.add("0");
            timeDatas.add("6");
            timeDatas.add("12");
            timeDatas.add("18");
            timeDatas.add("24");
        }
    }

    public void init() {
        convertData2Point();
        invalidate();
    }

    private void convertData2Point() {
        float rateX = getRateX();
        pointList.clear();
        //注意：点的x轴是总长度-相对右边的距离算得。
        for (Integer index : tiredDatas.keySet()) {
            MoodFatigueData bean = tiredDatas.get(index);
            if (bean != null && bean.getFatigueStatus() != -1) {
                int x = (int) (rateX * index + paddingLeft * 2);
                int y = (int) getDrawLineHeigh(bean.getFatigueStatus());
                pointList.add(new Point(x, y, bean.getFatigueStatus(), bean));
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float nowX = event.getX();
                if (selectedLineX != nowX) {
                    drawSelectedLine = true;
                    selectedLineX = nowX;
                    invalidate();
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

    private void checkPoint(Canvas canvas, float x) {
        if (selectPointListener != null) {
            int nowX = (int) x;
            for (int i = 0; i < pointList.size(); i++) {
                Point point = pointList.get(i);
                if (nowX >= point.px && nowX <= point.px + pillarWidth) {
                    String date = getDateString(nowX);
                    drawTopValue(canvas, nowX, date, " "+getValueText(point.value)+" ");
                    selectPointListener.onPointSelected(pointList.get(i).value);
                }
            }
        }
    }

    private String getDateString(int nowX) {
        String format = "HH:mm";
        float rateX = getRateX();
        int min = (int) ((nowX - paddingLeft * 2) / rateX);
        Date startDate = DateDrawTool.getCurrentDateStartTimeAddMin(null, min);
        return DateDrawTool.dateToStr(startDate, format);
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
            x = x + getRateX() + 5;
            mPaint.setTextAlign(Paint.Align.LEFT);
        } else {
            x = x - getRateX() - 5;
            mPaint.setTextAlign(Paint.Align.RIGHT);
        }
        canvas.drawText(dateString, x, paddingTop + textHeight, mPaint);
        canvas.drawText(valueString, x, paddingTop + textHeight * 2.5F, mPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBottom(canvas);
        drawYLines(canvas);
        drawValuePoint(canvas);
        drawXLinePoint(canvas);
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
        mPaint.setColor(getResources().getColor(R.color.gray4));
        for (int i = 0; i < max_val; i++) {
            float y = getDrawLineHeigh(i);
            canvas.drawRect(0, y, self_width, y + 2, mPaint);
        }
    }

    private void drawValuePoint(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i <= pointList.size() - 1; i++) {
            mPaint.setColor(getValuePainColor(pointList.get(i).value));
            canvas.drawRect(pointList.get(i).px, pointList.get(i).py, pointList.get(i).px + pillarWidth, self_height - marginBottom, mPaint);
        }
    }

    private int getValuePainColor(int value) {
        int color = getResources().getColor(R.color.tired_energetic);
        switch (value) {
            case ENERGETIC:
                color = getResources().getColor(R.color.tired_energetic);
                break;
            case MODERATE:
                color = getResources().getColor(R.color.tired_moderate);
                break;
            case SLIGHT:
                color = getResources().getColor(R.color.tired_slight);
                break;
            case SERIOUS:
                color = getResources().getColor(R.color.tired_serious);
                break;
        }
        return color;
    }

    private String getValueText(int value) {
        String valueS = "";
        switch (value) {
            case ENERGETIC:
                valueS = getResources().getString(R.string.tired_energetic);
                break;
            case MODERATE:
                valueS = getResources().getString(R.string.tired_moderate);
                break;
            case SLIGHT:
                valueS = getResources().getString(R.string.tired_slight);
                break;
            case SERIOUS:
                valueS = getResources().getString(R.string.tired_serious);
                break;
        }
        return valueS;
    }


    private void drawXLinePoint(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        float textSize = (float) ((marginBottom * 1.5) / 4.0);
        mPaint.setTextSize(textSize);
        initTimeDatas();
        float rateX = (float) (self_width - paddingLeft * 2 - paddingRight) / (timeDatas.size() - 1);
        for (int i = 0; i < timeDatas.size(); i++) {
            float px = rateX * i + paddingLeft * 2;
            canvas.drawRect(px, self_height - marginBottom, px + 4, self_height - (marginBottom * 3 / 4), mPaint);
            canvas.drawText("" + timeDatas.get(i), px, self_height - (marginBottom + 12) / 4, mPaint);
            canvas.drawRect(px, self_height - marginBottom / 4, px + 4, self_height, mPaint);
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


    public void setDatas(LinkedHashMap<Integer, MoodFatigueData> tiredDatas) {
        if (this.tiredDatas == null) {
            this.tiredDatas = new LinkedHashMap<>();
        }
        this.tiredDatas.clear();
        for (Integer key : tiredDatas.keySet()) {
            this.tiredDatas.put(key, tiredDatas.get(key));
        }
        convertData2Point();
        invalidate();
    }

    public void setTimeDatas(List<String> timeDatas) {
        this.timeDatas = timeDatas;
        invalidate();
    }

    private float getRateX() {
        float drawWidth = (float) (self_width - paddingLeft * 2 - paddingRight) / (TOTAL_M);
        return drawWidth;
    }


    private float getDrawLineHeigh(int value) {
        //计算方法：上顶部+y占据画线界面的高度
        float total = (float) ((self_height - paddingTop - marginBottom) * 1.0);
        total = total - drawLineMarginTop;
        float avg = total / (max_val - min_val);
        return avg * value + paddingTop + drawLineMarginTop;
    }

    private int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public interface SelectPointListener {
        void onPointSelected(int culValue);
    }

    public void setSelectPointListener(SelectPointListener selectPointListener) {
        this.selectPointListener = selectPointListener;
    }
}
