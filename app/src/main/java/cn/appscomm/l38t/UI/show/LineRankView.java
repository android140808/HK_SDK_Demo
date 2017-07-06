package cn.appscomm.l38t.UI.show;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.constant.APPConstant;
import cn.appscomm.l38t.utils.FileUtils;
import cn.appscomm.l38t.utils.UniversalImageLoaderHelper;


/**
 * 排行控件
 * Created by Administrator on 2016/6/9.
 */
public class LineRankView extends View {
    private List<LineRankData> lineRankDatas = new ArrayList<LineRankData>();
    private Paint linePaint;
    private Paint topPaint;
    private Paint buttomPaint;
    private int topColor = Color.parseColor("#EAC029");
    private int buttomColor = Color.parseColor("#29B7C1");
    private int lineColor = Color.parseColor("#B4B4B5");
    private int height;
    private int width;
    private String maxText = "10k";
    private int total;

    public String getMaxText() {
        return maxText;
    }

    public void setMaxText(String maxText) {
        this.maxText = maxText;
        try {
            String valueMax = maxText.replace("k", "");
            total = Integer.parseInt(valueMax) * 1000;
        } catch (NumberFormatException e) {
            total = 0;
        }
        invalidate();
    }

    public List<LineRankData> getLineRankDatas() {
        return lineRankDatas;
    }

    public void setLineRankDatas(List<LineRankData> lineRankDatas) {
        this.lineRankDatas = lineRankDatas;
        invalidate();
    }

    public LineRankView(Context context) {
        this(context, null);
    }

    public LineRankView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineRankView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        topPaint = new Paint();
        topPaint.setAntiAlias(true);
        topPaint.setStyle(Paint.Style.STROKE);
        topPaint.setStrokeWidth(4f);
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        buttomPaint = new Paint();
        buttomPaint.setStyle(Paint.Style.STROKE);
        buttomPaint.setAntiAlias(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        topPaint.setColor(topColor);
        buttomPaint.setColor(buttomColor);
        linePaint.setColor(lineColor);
        height = getMeasuredHeight() - getPaddingBottom() - getPaddingTop();
        width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        //画线
        drawLine(canvas);
        //画线的刻度
        drawCircle(canvas);
    }


    private float getValueX(int nowValue) {
        float startX = height / 5f;
        if (nowValue <= 0) {
            return startX;
        }
        float lineWidth = width - height * 2 / 5f;
        float avg = lineWidth / total;
        float nowX = nowValue * avg;
        return startX + nowX;
    }

    private void drawLine(Canvas canvas) {
        float startX = height / 5f;
        float endX = startX + width - height * 2 / 5f;
        float y = height / 2;
        canvas.drawLine(startX, y, endX, y, linePaint);
        canvas.drawLine(startX, y + height / 20, startX, y - height / 20, linePaint);
        canvas.drawLine(endX, y + height / 20, endX, y - height / 20, linePaint);
        linePaint.setTextSize(20);
        canvas.drawText(maxText, endX + 5, y, linePaint);
    }

    private void drawCircle(Canvas canvas) {
        float y = height / 2;
        float x;
        for (int i = 0; i < lineRankDatas.size(); i++) {
            //从小到大
            x = getValueX(lineRankDatas.get(i).getValue());
            if (lineRankDatas.get(i).isTop()) {
                //画上面
                //小圈
                topPaint.setStrokeWidth(4f);
                canvas.drawCircle(x, y, 15, topPaint);
                //线
                canvas.drawLine(x, y - 15, x, y - 40, topPaint);
                canvas.save();
                Path path = new Path();
                //裁剪区域
                canvas.translate(x - height / 10f, y - 40 - height / 5);
                path.addCircle(height / 10f, height / 10f, height / 10f, Path.Direction.CW);
                canvas.clipPath(path);
//                if (lineRankDatas.get(i).getBitmap() != null) {
//                    Bitmap mBitmap = ImageUtil.zoomBitmap(lineRankDatas.get(i).getBitmap(), (int) (height / 5f), (int) (height / 5f));
//                    canvas.drawBitmap(mBitmap, 0, 0, topPaint);
//                }
                Bitmap bitmap = UniversalImageLoaderHelper.loadImage(lineRankDatas.get(i).getIconUrl(), GlobalApp.getAppContext());
                if (bitmap != null) {
                    Bitmap mBitmap = zoomBitmap(bitmap, (int) (height / 5f), (int) (height / 5f));
                    canvas.drawBitmap(mBitmap, 0, 0, topPaint);
                }
                canvas.restore();
                //大圈
                topPaint.setStrokeWidth(7f);
                canvas.drawCircle(x, y - 40 - height / 10, height / 10f, topPaint);
            } else {
                //画下面
                buttomPaint.setStrokeWidth(4f);
                canvas.drawCircle(x, y, 15, buttomPaint);
                //线
                canvas.drawLine(x, y + 15, x, y + 40, buttomPaint);
                canvas.save();
                Path path = new Path();
                //裁剪区域
                canvas.translate(x - height / 10f, y + 40);
                path.addCircle(height / 10f, height / 10f, height / 10f, Path.Direction.CW);
                canvas.clipPath(path);
//                if (lineRankDatas.get(i).getBitmap() != null) {
//                    Bitmap mBitmap = ImageUtil.zoomBitmap(lineRankDatas.get(i).getBitmap(), (int) (height / 5f), (int) (height / 5f));
//                    canvas.drawBitmap(mBitmap, 0, 0, topPaint);
//                }
                Bitmap bitmap = UniversalImageLoaderHelper.loadImage(lineRankDatas.get(i).getIconUrl(), GlobalApp.getAppContext());
                if (bitmap != null) {
                    Bitmap mBitmap = zoomBitmap(bitmap, (int) (height / 5f), (int) (height / 5f));
                    canvas.drawBitmap(mBitmap, 0, 0, topPaint);
                }
                canvas.restore();
                //大圈
                buttomPaint.setStrokeWidth(7f);
                canvas.drawCircle(x, y + 40 + height / 10, height / 10f, buttomPaint);
            }

        }
    }

    // 放大缩小图片
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidht, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return newbmp;
    }

}
