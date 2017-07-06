package cn.appscomm.l38t.UI.showView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import cn.appscomm.l38t.R;


/**
 * Created by glin on 6/30/15.
 */
public class CircleSmallView extends View {

    private float goalval, curval;
    private int titlecolor = 0x1ea84e, bgcolor = 0xa0a09b, drawcolor = 0x5eb749;  //绘制颜色
    private int titleico; //
    private String titleDesc = "goal:", buttomDesc = "STEP TAKEN";
    private Context context;
    private int viewType = 0;
    private int unit = 0;
    private static Paint paint;

    public final static int STEP_VIEW = 0;
    public final static int CALORIES_VIEW = 1;
    public final static int DISTANS_VIEW = 2;
    public final static int ACTIVITY_VIEW = 3;
    public final static int SLEEP_VIEW = 4;
    public final static int HEARTRATE_VIEW = 5;

    private static final int circle_r = 55;
    private static final int ico_r = 35;


    private static final String TAG = "CircleSmallView";

    public int getTitleico() {
        return titleico;
    }

    public int getDrawcolor() {
        return drawcolor;
    }

    /**
     * @param context
     * @param curval  当前值
     * @param goalval 目标值
     */
    public CircleSmallView(Context context, float curval, float goalval) {
        super(context);
        this.context = context;
        this.goalval = goalval;
        this.curval = curval;
        paint = new Paint();
        paint.setAntiAlias(true);
        this.titlecolor = 0xffa1549e;
        this.bgcolor = getResources().getColor(R.color.gray5);
        this.drawcolor = getResources().getColor(R.color.mood_depressed);
        this.titleico = R.mipmap.mood_2;
        this.titleDesc = context.getString(R.string.goal);
        this.buttomDesc = context.getString(R.string.mood);
    }


    public CircleSmallView(Context context) {
        super(context);
        this.context = context;

    }

    public void setViewType(int viewType) {
        this.viewType = viewType;

        invalidate();

    }

    public void setGoalval(float goalval) {


        if (goalval < 0) {

            return;
        }
        this.goalval = goalval;
        invalidate();
    }

    public void setCurval(float curval) {

        if (curval < 0) curval = 0;

        this.curval = curval;
        invalidate();
    }

    public void setDrawcolor(int drawcolor) {
        this.drawcolor = drawcolor;
        invalidate();
    }

    public void setTitleico(int titleico) {
        this.titleico = titleico;
        invalidate();
    }

    public void setTitleDesc(String titleDesc) {
        this.titleDesc = titleDesc;
        invalidate();
    }

    public void setButtomDesc(String buttomDesc) {
        this.buttomDesc = buttomDesc;
        invalidate();
    }

    public CircleSmallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    /**
     * 自定义重绘过程,在重绘制时，这里View
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        int lowLimit = 50;
        int highLimit = 180;
        if (lowLimit <= 0 || highLimit <= 0) {
            lowLimit = 50;
            highLimit = 180;
        }
        int left, top, right, bottom;

        float width;

        paint.setColor(0xffffffff);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, dp2px(circle_r), paint);

        paint.setColor(bgcolor);
        paint.setStyle(Paint.Style.STROKE);
        width = paint.getStrokeWidth();
        paint.setStrokeWidth(dp2px(6));

        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, dp2px(circle_r), paint);
        int x = canvas.getWidth() / 2;
        int y = canvas.getHeight() / 2;
        int r = dp2px(circle_r);

        float angle = 0f;

        RectF recf = new RectF(x - r, y - r, x + r, y + r);
        paint.setColor(drawcolor);

        if (curval >= goalval)
            canvas.drawArc(recf, 0, 360, false, paint);
        else {
            if (viewType == HEARTRATE_VIEW)
                canvas.drawArc(recf, -90, 360 * (curval / (highLimit)), false, paint);      // -lowLimit
            else
                canvas.drawArc(recf, -90, 360 * (curval / goalval), false, paint);
        }

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), titleico);

        left = x;
        top = y;
        int r1 = dp2px(ico_r) / 2;

        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(left - r1, top - r1, left + r1, top + r1), paint);

        super.onDraw(canvas);
    }


    public int dp2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
