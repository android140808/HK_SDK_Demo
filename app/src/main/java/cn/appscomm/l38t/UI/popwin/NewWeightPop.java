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


public class NewWeightPop extends PopupWindow {

	private Context context;
	private View mMenuView;
	private WheelView wheelWeightIntView;
	private WheelView wheelWeightDecView;
	private Button btn_save = null;
	private Button btn_Cancel = null;
	

	private OnWheelScrollListener wheelScrollListener;


	public int getCurrentItemInt() {
		return wheelWeightIntView.getCurrentItem();
	}

	public int getCurrentItemDes() {
		return wheelWeightDecView.getCurrentItem();
	}
	
	private void SetUnit(final int AUnit) {
		// 0 ,lbs  1 kg
		ArrayWheelAdapter<String> WeightIntAdapter;
		ArrayWheelAdapter<String> WeightDecAdapter;
		if (AUnit == 0) {
			WeightIntAdapter = new ArrayWheelAdapter<String>(context,
					UnitHelper.getInstance().getArrWeightlbs_Int());
			WeightDecAdapter = new ArrayWheelAdapter<String>(context,
					UnitHelper.getInstance().getArrWeight_Dec());

		} else {
			WeightIntAdapter = new ArrayWheelAdapter<String>(context,
					UnitHelper.getInstance().getArrWeight_Int());
			WeightDecAdapter = new ArrayWheelAdapter<String>(context,
					UnitHelper.getInstance().getArrWeight_Dec());
		}

		WeightIntAdapter.setItemResource(R.layout.wheel_text_item);
		WeightIntAdapter.setItemTextResource(R.id.text);
		wheelWeightIntView.setViewAdapter(WeightIntAdapter);
		wheelWeightIntView.invalidate();

		WeightDecAdapter.setItemResource(R.layout.wheel_text_item);
		WeightDecAdapter.setItemTextResource(R.id.text);
		wheelWeightDecView.setViewAdapter(WeightDecAdapter);
		wheelWeightDecView.invalidate();
		if (AUnit == 0) {
			wheelWeightIntView.setCurrentItem(93);//lbs 整数部分 143lbs
			wheelWeightDecView.setCurrentItem(0);
		} else {
			wheelWeightIntView.setCurrentItem(35);//kg 整数部分
			wheelWeightDecView.setCurrentItem(0);
		}
	}
	
	public NewWeightPop(Context context, int currentIntValItem, int currentDecValItem, int currentUnitItem, int visibleItems, OnClickListener btnClickListener)
	{
		super(context);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.wheel_weight_view, null);
		
		wheelScrollListener = new OnWheelScrollListener() {
			
			@Override
			public void onScrollingStarted(WheelView wheel) {
			}
			
			@Override
			public void onScrollingFinished(WheelView wheel) {
			}
		};

		wheelWeightIntView = (WheelView)mMenuView.findViewById(R.id.Weight_Int);
		wheelWeightDecView = (WheelView)mMenuView.findViewById(R.id.Weight_Dec);
		btn_save = (Button)mMenuView.findViewById(R.id.WeightWheelSave);
		btn_Cancel = (Button)mMenuView.findViewById(R.id.WeightWheelCancel);
		
		btn_save.setOnClickListener(btnClickListener);
		btn_Cancel.setOnClickListener(btnClickListener);
        wheelWeightIntView.setVisibleItems(visibleItems);
        wheelWeightIntView.addScrollingListener(wheelScrollListener);
        wheelWeightDecView.setVisibleItems(visibleItems);
        wheelWeightDecView.addScrollingListener(wheelScrollListener);
        ArrayWheelAdapter<String> WeightUnitAdapter = new ArrayWheelAdapter<String>(context,new String[]{"lbs","kg"});
        WeightUnitAdapter.setItemResource(R.layout.wheel_text_item);
        WeightUnitAdapter.setItemTextResource(R.id.text);

        SetUnit(currentUnitItem);
        wheelWeightIntView.setCurrentItem(currentIntValItem);
        wheelWeightDecView.setCurrentItem(currentDecValItem);


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
}
	

