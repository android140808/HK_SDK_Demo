package cn.appscomm.l38t.UI.popwin;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.utils.UnitHelper;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;


public class NewHeightPop extends PopupWindow {
    private String TAG = getClass().getSimpleName();

    private Context context;
    private View mMenuView;
    private WheelView wheelHeightIntView;
    private WheelView wheelHeightDecView;
    private Button btn_save = null;
    private Button btn_Cancel = null;

    private OnWheelScrollListener wheelScrollListener;

    public int getCurrentItemInt() {
        return wheelHeightIntView.getCurrentItem();
    }

    public int getCurrentItemDes() {
        return wheelHeightDecView.getCurrentItem();
    }

    public void setCurrentItem(int currentIntValItem, int currentDecValItem, int currentUnitItem) {
        SetUnit(currentUnitItem);
        if (currentIntValItem > 0) {
            wheelHeightIntView.setCurrentItem(currentIntValItem);
        }
        if (currentDecValItem > 0) {
            wheelHeightDecView.setCurrentItem(currentDecValItem);
        }
    }

    private void SetUnit(final int AUnit) {
        // 0 ,ft 1 ,cm
        ArrayWheelAdapter<String> HeightIntAdapter;
        ArrayWheelAdapter<String> HeightDecAdapter;
        if (AUnit == 0) {
            HeightIntAdapter = new ArrayWheelAdapter<String>(context, UnitHelper.getInstance().getArrHeightFt_Int());
            HeightDecAdapter = new ArrayWheelAdapter<String>(context, UnitHelper.getInstance().getArrHeightFt_Dec());

        } else {
            HeightIntAdapter = new ArrayWheelAdapter<String>(context, UnitHelper.getInstance().getArrHeight_Int());
            HeightDecAdapter = new ArrayWheelAdapter<String>(context, UnitHelper.getInstance().getArrHeight_Dec());
        }

        HeightIntAdapter.setItemResource(R.layout.wheel_text_item);
        HeightIntAdapter.setItemTextResource(R.id.text);
        wheelHeightIntView.setViewAdapter(HeightIntAdapter);
        wheelHeightIntView.invalidate();

        HeightDecAdapter.setItemResource(R.layout.wheel_text_item);
        HeightDecAdapter.setItemTextResource(R.id.text);
        wheelHeightDecView.setViewAdapter(HeightDecAdapter);
        wheelHeightDecView.invalidate();
        if (AUnit == 0) {
            wheelHeightIntView.setCurrentItem(3);//
            wheelHeightDecView.setCurrentItem(7);
            wheelHeightDecView.setVisibility(View.VISIBLE);
        } else {
            wheelHeightDecView.setVisibility(View.GONE);//不精确到小数点
            wheelHeightIntView.setCurrentItem(80);//默认选择第87个,170
            wheelHeightDecView.setCurrentItem(0);
        }
    }

    public NewHeightPop(Context context, int currentIntValItem, int currentDecValItem, int currentUnitItem, int visibleItems, OnClickListener btnClickListener) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.wheel_height_view, null);
        wheelScrollListener = new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
            }
        };
        wheelHeightIntView = (WheelView) mMenuView.findViewById(R.id.Height_Int);
        wheelHeightDecView = (WheelView) mMenuView.findViewById(R.id.Height_Dec);
        btn_save = (Button) mMenuView.findViewById(R.id.HeightWheelSave);
        btn_Cancel = (Button) mMenuView.findViewById(R.id.HeightWheelCancel);

        btn_save.setOnClickListener(btnClickListener);
        btn_Cancel.setOnClickListener(btnClickListener);
        wheelHeightIntView.setVisibleItems(visibleItems);
        wheelHeightIntView.addScrollingListener(wheelScrollListener);
        wheelHeightDecView.setVisibleItems(visibleItems);
        wheelHeightDecView.addScrollingListener(wheelScrollListener);
        SetUnit(currentUnitItem);
        if (currentIntValItem >= 0) {
            wheelHeightIntView.setCurrentItem(currentIntValItem);
        }
        if (currentDecValItem >= 0) {
            wheelHeightDecView.setCurrentItem(currentDecValItem);
        }
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
}

