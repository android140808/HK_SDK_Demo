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
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

/**
 * Created with Eclipse.
 * Author: Tim Liu  email:9925124@qq.com
 * Date: 14-3-9
 * Time: 16:46
 */
public class SelectWheelPopupWindow2 extends PopupWindow {


//	private Button btn_take_photo, btn_pick_photo, btn_cancel;
	private View mMenuView;
	private WheelView wheelview;
	private Button btn_save = null;
	private Button btn_Cancel = null;

	public SelectWheelPopupWindow2(Activity context, String[] arr, int visibleItems, int currentItem, OnWheelScrollListener wheelScrollListener, OnDismissListener ls1 , OnClickListener btnClickListener ) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.wheel_view, null);
		
		wheelview = (WheelView)mMenuView.findViewById(R.id.wheelview);

        ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(context, arr);
        ampmAdapter.setItemResource(R.layout.wheel_text_item);
        ampmAdapter.setItemTextResource(R.id.text);
        wheelview.setViewAdapter(ampmAdapter);
        
        wheelview.setVisibleItems(visibleItems);
		wheelview.setCurrentItem(currentItem);
//		wheelview.setViewAdapter(new NumericWheelAdapter(context, 1, 20));
		
		// 监听值的改变
		wheelview.addScrollingListener(wheelScrollListener);
//		hour.set
		
		//设置SelectPicPopupWindow的View
		this.setOnDismissListener(ls1);
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
		
		btn_save = (Button)mMenuView.findViewById(R.id.WheelCancle);
		btn_save.setText(R.string.cancle);
		btn_Cancel = (Button)mMenuView.findViewById(R.id.WheelDone);
		btn_Cancel.setText(R.string.sure);

		btn_save.setOnClickListener(btnClickListener);
		btn_Cancel.setOnClickListener(btnClickListener);
		
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
	public void setWheelCurrentItem(int current){
		wheelview.setCurrentItem(current);
	}

}
