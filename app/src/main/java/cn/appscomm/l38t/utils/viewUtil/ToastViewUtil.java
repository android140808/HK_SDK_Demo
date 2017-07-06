package cn.appscomm.l38t.utils.viewUtil;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.appscomm.l38t.R;

public class ToastViewUtil {

	private static void makeShowToast(Context context,String msg,int drawable,int time){
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.toast_center, null);
		LinearLayout llLayout=(LinearLayout) view.findViewById(R.id.ll_toast);
		LayoutParams layoutParams=llLayout.getLayoutParams();
		int width = context.getResources().getDisplayMetrics().widthPixels;
		layoutParams.width=(int) ((width/10)*9);
		llLayout.setLayoutParams(layoutParams);
		ImageView imageView = (ImageView) view.findViewById(R.id.iv_toast);
		TextView textView = (TextView) view.findViewById(R.id.tv_toast);
		if (drawable>0) {
			imageView.setBackgroundResource(drawable);
		}
		textView.setText(msg+"");
		Toast toast = new Toast(context);
		toast.setGravity(Gravity.TOP,0,0);
		toast.setDuration(time);
		toast.setView(view);
		toast.show();
	}
	
	public static void showLongToast(Context context,String msg){
		makeShowToast(context, msg,0,Toast.LENGTH_SHORT);
	}
	
	public static void showShortToast(Context context,String msg){
		makeShowToast(context, msg,0,Toast.LENGTH_SHORT);
	}
	public static void showTimeToast(Context context,String msg,int time){
		makeShowToast(context, msg,0,time);
	}
	
	public static void showDrawableToast(Context context,String msg,int drawable,int time){
		makeShowToast(context, msg,drawable,time);
	}
	
}
