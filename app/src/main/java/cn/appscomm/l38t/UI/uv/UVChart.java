package cn.appscomm.l38t.UI.uv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import cn.appscomm.l38t.R;

/**
 * Created by zhaozx on 16/10/18.
 * desc:
 */

public class UVChart extends View {
    private Paint paintBG;
    private Paint paintUVValue;     //进度画笔
    private int uvValue = -1;       //紫外线值

    public UVChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UVChart(Context context) {
        super(context);
        init();
    }

    public UVChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        paintBG = new Paint();
        paintBG.setAntiAlias(true);
        paintBG.setColor(getResources().getColor(R.color.uv_bg));

        paintUVValue = new Paint();
        paintUVValue.setAntiAlias(true);
    }

    public void setUvValue(int uvValue) {
        this.uvValue = uvValue;
        invalidate();       //重绘
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int between = 5;        //间隙的宽度
        int round = height / 2;   //两头元的半径

        for (int i = 0; i < 5; i++) {
            //画左侧的圆角
            if (i == 0) {
                canvas.drawCircle(round, round, round, paintBG);
                canvas.drawRect(round, 0, (i + 1) * (width / 5) - between, height, paintBG);
            } else if (i == 4) {   //画右侧圆角
                canvas.drawRect(i * (width / 5), 0, (i + 1) * (width / 5) - between - round, height, paintBG);
                canvas.drawCircle((i + 1) * (width / 5) - between - round, round, round, paintBG);
            } else {
                canvas.drawRect(i * (width / 5), 0, (i + 1) * (width / 5) - between, height, paintBG);
            }
        }
        if (1 <= uvValue && uvValue <= 2) {   //画一格
            paintUVValue.setColor(Color.parseColor("#a4d851"));
            for (int i = 0; i < 5; i++) {
                //画左侧的圆角
                if (i == 0) {
                    canvas.drawCircle(round, round, round, paintUVValue);
                    canvas.drawRect(round, 0, (i + 1) * (width / 5) - between, height, paintUVValue);
                }
            }

        } else if (3 <= uvValue && uvValue <= 5) {//画两格
            paintUVValue.setColor(Color.parseColor("#c8db42"));
            for (int i = 0; i < 5; i++) {
                //画左侧的圆角
                if (i == 0) {
                    canvas.drawCircle(round, round, round, paintUVValue);
                    canvas.drawRect(round, 0, (i + 1) * (width / 5) - between, height, paintUVValue);
                } else if (i < 2) {
                    canvas.drawRect(i * (width / 5), 0, (i + 1) * (width / 5) - between, height, paintUVValue);
                }
            }

        } else if (6 <= uvValue && uvValue <= 7) {//画三格
            paintUVValue.setColor(Color.parseColor("#ff9603"));
            for (int i = 0; i < 5; i++) {
                //画左侧的圆角
                if (i == 0) {
                    canvas.drawCircle(round, round, round, paintUVValue);
                    canvas.drawRect(round, 0, (i + 1) * (width / 5) - between, height, paintUVValue);
                } else if (i < 3) {
                    canvas.drawRect(i * (width / 5), 0, (i + 1) * (width / 5) - between, height, paintUVValue);
                }
            }
        } else if (8 <= uvValue && uvValue <= 10) {//画四格
            paintUVValue.setColor(Color.parseColor("#e61e79"));
            for (int i = 0; i < 5; i++) {
                //画左侧的圆角
                if (i == 0) {
                    canvas.drawCircle(round, round, round, paintUVValue);
                    canvas.drawRect(round, 0, (i + 1) * (width / 5) - between, height, paintUVValue);
                } else if (i == 4) {   //画右侧圆角,不画

                } else {
                    canvas.drawRect(i * (width / 5), 0, (i + 1) * (width / 5) - between, height, paintUVValue);
                }
            }
        } else if (11 <= uvValue) {//画五格
            paintUVValue.setColor(Color.parseColor("#ff2557"));
            for (int i = 0; i < 5; i++) {
                //画左侧的圆角
                if (i == 0) {
                    canvas.drawCircle(round, round, round, paintUVValue);
                    canvas.drawRect(round, 0, (i + 1) * (width / 5) - between, height, paintUVValue);
                } else if (i == 4) {   //画右侧圆角
                    canvas.drawRect(i * (width / 5), 0, (i + 1) * (width / 5) - between - round, height, paintUVValue);
                    canvas.drawCircle((i + 1) * (width / 5) - between - round, round, round, paintUVValue);
                } else {
                    canvas.drawRect(i * (width / 5), 0, (i + 1) * (width / 5) - between, height, paintUVValue);
                }
            }
        }

    }
}
