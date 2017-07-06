package cn.appscomm.l38t.UI.showView.datachart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
import cn.appscomm.netlib.bean.heartRate.HeartBeatData;


/**
 * Created by weiliu on 2016/7/25.
 */
public class HeartBeatView extends View {

    private int self_width, self_height;
    private boolean isFirstDraw = true;
    private LinkedHashMap<Integer, HeartBeatData> heartRateDatas;
    private List<String> heartTimeRateDatas;
    private List<Point> pointList = new ArrayList<Point>();
    public final int paddingLeft = 30;
    public final int paddingRight = 30;
    private final int paddingTop = dp2px(10), paddingBottom = dp2px(15);//上面,底部留20dp
    private final int marginBottom = dp2px(40);
    private int min_val = HeartBeatData.HEART_BEAT_MIN, max_val = HeartBeatData.HEART_BEAT_MAX;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private HeartBeatViewSelectPointListener selectPointListener;
    private boolean drawSelectedLine = true;
    private float selectedLineX = 0;
    private float selectedLineBeforeX = 0;
    private final int TOTAL_M = 1440;

    public HeartBeatView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context);
    }

    public HeartBeatView(Context context, AttributeSet attrs) {
        this(context);
    }

    public HeartBeatView(Context context) {
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
                    if (heartRateDatas == null) {
                        heartRateDatas = new LinkedHashMap<Integer, HeartBeatData>();
                    }
                    initHeartBeatTimeRates();
                    init();
                    isFirstDraw = false;
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        performClick();
    }

    private void initHeartBeatTimeRates() {
        if (heartTimeRateDatas == null) {
            heartTimeRateDatas = new ArrayList<String>();
            heartTimeRateDatas.add("0");
            heartTimeRateDatas.add("6");
            heartTimeRateDatas.add("12");
            heartTimeRateDatas.add("18");
            heartTimeRateDatas.add("24");
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
        for (Integer index : heartRateDatas.keySet()) {
            HeartBeatData bean = heartRateDatas.get(index);
            if (bean != null && bean.getHeartAvg() != -1) {
                int x = (int) (rateX * index + paddingLeft * 2);
                int y = (int) getDrawLineHeigh(bean.getHeartAvg());
                pointList.add(new Point(x, y, bean.getHeartAvg(), bean));
            }
        }
        max_val = HeartBeatData.HEART_BEAT_MAX;
        min_val = HeartBeatData.HEART_BEAT_MIN;
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
                //drawSelectedLine = false;
                //invalidate();
                break;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(event);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBottom(canvas);
        drawYLines(canvas);
        drawValuePoint(canvas);
        drawXLinePoint(canvas);
        if (selectedLineX > 0 && drawSelectedLine) {
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
        mPaint.setTextSize(dp2px(12));
        mPaint.setTextAlign(Paint.Align.RIGHT);
        mPaint.setStyle(Paint.Style.FILL);
        int lineNum = max_val / 20;
        for (int i = 0; i < lineNum; i++) {
            if (i == lineNum - 2) break;
            float y = getDrawLineHeigh(max_val - i * 20);
            mPaint.setColor(getResources().getColor(R.color.gray));
            String text = "" + (max_val - i * 20);
            int textWidth = getDrawTextWidth(mPaint, text);
            canvas.drawText(text, paddingLeft * 2 + textWidth / 2, y, mPaint);
            mPaint.setColor(getResources().getColor(R.color.gray4));
            canvas.drawRect(paddingLeft, y, self_width, y + dp2px(1), mPaint);
        }
    }

    private int getDrawTextWidth(Paint paint, String valueString) {
        Rect textBounds = new Rect();
        paint.getTextBounds(valueString, 0, valueString.length(), textBounds);//get text bounds, that can get the text width and height
        int textWidth = textBounds.right - textBounds.left;
        return textWidth;
    }

    private int getDrawTextHeight(Paint paint, String valueString) {
        Rect textBounds = new Rect();
        paint.getTextBounds(valueString, 0, valueString.length(), textBounds);//get text bounds, that can get the text width and height
        int textHeight = textBounds.bottom - textBounds.top;
        return textHeight;
    }


    private void drawValuePoint(Canvas canvas) {
        mPaint.setColor(getResources().getColor(R.color.red4));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        Point startp = new Point();
        Point endp = new Point();
        for (int i = 0; i < pointList.size() - 1; i++) {
            startp = pointList.get(i);
            endp = pointList.get(i + 1);
            int wt = (startp.px + endp.px) / 2;
            Point ctrlp1 = new Point();
            Point ctrlp2 = new Point();
            ctrlp1.py = startp.py;
            ctrlp1.px = wt;
            ctrlp2.py = endp.py;
            ctrlp2.px = wt;
            Path path = new Path();
            path.moveTo(startp.px, startp.py);
            path.cubicTo(ctrlp1.px, ctrlp1.py, ctrlp2.px, ctrlp2.py, endp.px, endp.py);
            canvas.drawPath(path, mPaint);
        }
    }

    private void drawXLinePoint(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        float textSize = (float) ((marginBottom * 1.5) / 4.0);
        mPaint.setTextSize(textSize);
        initHeartBeatTimeRates();
        float rateX = (float) (self_width - paddingLeft * 2 - paddingRight) / (heartTimeRateDatas.size() - 1);
        for (int i = 0; i < heartTimeRateDatas.size(); i++) {
            float px = rateX * i + paddingLeft * 2;
            canvas.drawRect(px, self_height - marginBottom, px + 4, self_height - (marginBottom * 3 / 4), mPaint);
            canvas.drawText("" + heartTimeRateDatas.get(i), px, self_height - (marginBottom + 12) / 4, mPaint);
            canvas.drawRect(px, self_height - marginBottom / 4, px + 4, self_height, mPaint);
        }
    }

    private void drawSelectedLine(Canvas canvas) {
        // 画横线
        selectedLineBeforeX = selectedLineX;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.gray));
        paint.setStrokeWidth(3);
        canvas.drawLine(selectedLineBeforeX, paddingTop, selectedLineBeforeX, self_height - marginBottom, paint);
        checkPoint(canvas, selectedLineBeforeX);
    }

    private void checkPoint(Canvas canvas, float x) {
        if (selectPointListener != null) {
            int nowX = (int) x;
            for (int i = 0; i < pointList.size(); i++) {
                Point point = pointList.get(i);
                if (nowX >= point.px - 1 && nowX <= point.px + 1) {
                    String date = getDateString(nowX);
                    if (point.value > 0) {
                        drawTopValue(canvas, nowX, date, getValueText(point.value));
                    } else {
                        drawTopValue(canvas, nowX, date, "");
                    }
                    HeartBeatData heartBeatData = (HeartBeatData) point.object;
                    selectPointListener.onPointSelected(heartBeatData);
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


    private String getValueText(int value) {
        String result = " " + value + " " + getResources().getString(R.string.bpm);
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
            x = x + getRateX();
            mPaint.setTextAlign(Paint.Align.LEFT);
        } else {
            x = x - getRateX();
            mPaint.setTextAlign(Paint.Align.RIGHT);
        }
        canvas.drawText(dateString, x, paddingTop + textHeight, mPaint);
        canvas.drawText(valueString, x, paddingTop + textHeight * 2.1F, mPaint);
    }


    public void setHeartRateDatas(LinkedHashMap<Integer, HeartBeatData> heartRateDatas) {
        if (this.heartRateDatas != null) {
            this.heartRateDatas.clear();
        }
        this.heartRateDatas = heartRateDatas;
        convertData2Point();
        invalidate();
    }

    public void setHeartTimeRateDatas(List<String> heartTimeRateDatas) {
        this.heartTimeRateDatas = heartTimeRateDatas;
        invalidate();
    }

    private float getRateX() {
        float drawWidth = (float) (self_width - paddingLeft * 2 - paddingRight) / (TOTAL_M);
        return drawWidth;
    }

    private float getDrawLineHeigh(int height) {
        //计算方法：上顶部+y占据画线界面的高度
        return (float) ((self_height - paddingTop - paddingBottom - marginBottom) * 1.0 / (max_val - min_val) * (max_val - height) + paddingTop);
    }


    private int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public interface HeartBeatViewSelectPointListener {
        void onPointSelected(HeartBeatData curBean);
    }

    public void setSelectPointListener(HeartBeatViewSelectPointListener selectPointListener) {
        this.selectPointListener = selectPointListener;
    }
}
