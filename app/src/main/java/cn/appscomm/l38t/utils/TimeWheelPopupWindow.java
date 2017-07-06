package cn.appscomm.l38t.utils;

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
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

/**
 * Created with Eclipse.
 * Author: Tim Liu  email:9925124@qq.com
 * Date: 14-3-19
 * Time: 00:33
 */
public class TimeWheelPopupWindow extends PopupWindow {

	public static final String WHEEL_HOUR="1001";
	public static final String WHEEL_MINUTE="1002";
//	private Button btn_take_photo, btn_pick_photo, btn_cancel;
	private View mMenuView;
	private WheelView wheel_hour;
	private WheelView wheel_minute;
	private Button btn_save = null;
	private Button btn_Cancel = null;
	
	public TimeWheelPopupWindow(Activity context, int visibleItems, int currentHourItem, int currentMinuteItem, OnWheelScrollListener wheelScrollListener, OnClickListener btnClickListener) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.wheel_time_view, null);
		
		
		btn_save = (Button)mMenuView.findViewById(R.id.timeWheelSave);
		btn_Cancel = (Button)mMenuView.findViewById(R.id.timeWheelCancel);
		
		btn_save.setOnClickListener(btnClickListener);
		btn_Cancel.setOnClickListener(btnClickListener);
		
		wheel_hour = (WheelView)mMenuView.findViewById(R.id.wheel_hour);
		wheel_hour.setViewAdapter(new NumericWheelAdapter(context, 0, 23, "%02d"));
		wheel_hour.setTag(WHEEL_HOUR);
//		wheel_hour.setCyclic(true);
		
		wheel_minute = (WheelView)mMenuView.findViewById(R.id.wheel_minute);
		wheel_minute.setViewAdapter(new NumericWheelAdapter(context, 0, 59, "%02d")); // %02d:位数不够两位就高位补0
//		wheel_minute.setCyclic(true);
		wheel_minute.setTag(WHEEL_MINUTE);
        
        wheel_hour.setVisibleItems(visibleItems);
        wheel_minute.setVisibleItems(visibleItems);
        wheel_hour.setCurrentItem(currentHourItem);
        wheel_minute.setCurrentItem(currentMinuteItem);
//		wheelview.setViewAdapter(new NumericWheelAdapter(context, 1, 20));		
		// 监听值的改变
        wheel_hour.addScrollingListener(wheelScrollListener);
        wheel_minute.addScrollingListener(wheelScrollListener);
        

		
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
				int y=(int) event.getY();
				if(event.getAction() == MotionEvent.ACTION_UP){
					if( y < height ){
						dismiss();
					}
				}				
				return true;
			}
		});

	}

    public TimeWheelPopupWindow(Activity context, int visibleItems, int currentHourItem, int currentMinuteItem, OnWheelScrollListener wheelScrollListener, OnClickListener btnClickListener , int time12) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.wheel_time_view, null);


        btn_save = (Button)mMenuView.findViewById(R.id.timeWheelSave);
        btn_Cancel = (Button)mMenuView.findViewById(R.id.timeWheelCancel);

        btn_save.setOnClickListener(btnClickListener);
        btn_Cancel.setOnClickListener(btnClickListener);

        wheel_hour = (WheelView)mMenuView.findViewById(R.id.wheel_hour);
        wheel_hour.setViewAdapter(new NumericWheelAdapter(context, 0, 12, "%02d"));
        wheel_hour.setTag(1);
//		wheel_hour.setCyclic(true);

        wheel_minute = (WheelView)mMenuView.findViewById(R.id.wheel_minute);
        wheel_minute.setViewAdapter(new NumericWheelAdapter(context, 0, 59, "%02d")); // %02d:位数不够两位就高位补0
//		wheel_minute.setCyclic(true);
        wheel_minute.setTag(2);

        wheel_hour.setVisibleItems(visibleItems);
        wheel_minute.setVisibleItems(visibleItems);
        wheel_hour.setCurrentItem(currentHourItem);
        wheel_minute.setCurrentItem(currentMinuteItem);
//		wheelview.setViewAdapter(new NumericWheelAdapter(context, 1, 20));
        // 监听值的改变
        wheel_hour.addScrollingListener(wheelScrollListener);
        wheel_minute.addScrollingListener(wheelScrollListener);



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
                int y=(int) event.getY();
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if( y < height ){
                        dismiss();
                    }
                }
                return true;
            }
        });

    }
	
	// 设置当前item
	public void setWheelHourCurrentItem(int current){
		wheel_hour.setCurrentItem(current);
	}
	// 设置当前item
	public void setWheelMinuteCurrentItem(int current){
		wheel_minute.setCurrentItem(current);
	}

}
