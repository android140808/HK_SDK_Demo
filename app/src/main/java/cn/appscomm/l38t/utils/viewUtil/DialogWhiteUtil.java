package cn.appscomm.l38t.utils.viewUtil;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.appscomm.l38t.R;

public class DialogWhiteUtil {

	private  static Dialog createCustomDialog(Context context, View view) {
		Dialog dialog;
		dialog = new Dialog(context, R.style.loading_dialog);
		int width = context.getResources().getDisplayMetrics().widthPixels;
		LayoutParams params = new LinearLayout.LayoutParams(width - (int) ScreenUtil.dip2px(context, 40), LayoutParams.WRAP_CONTENT);
		dialog.setContentView(view, params);
		return dialog;
	}
	
	
	public static Dialog createDialogWhiteNote(Context context,String title,String note,String nText,String pText,View.OnClickListener nClickListener,View.OnClickListener pClickListener){
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_white_note, null);
		final Dialog dialog =createCustomDialog(context, view);
		LinearLayout llLayout=(LinearLayout) view.findViewById(R.id.ll_dialog);
		LayoutParams layoutParams=llLayout.getLayoutParams();
		int width = context.getResources().getDisplayMetrics().widthPixels;
		layoutParams.width=(int) ((width/7)*5);
		llLayout.setLayoutParams(layoutParams);
		TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
		TextView tvNote= (TextView) view.findViewById(R.id.tv_note);
		Button btnCancle = (Button) view.findViewById(R.id.btn_cancle);
		Button btnOk= (Button) view.findViewById(R.id.btn_ok);
		View viewSplit=  view.findViewById(R.id.view_split);
		if (title!=null) {
			tvTitle.setText(title+"");
		}
		if (note!=null) {
			tvNote.setText(note+"");
		}
		if (nText!=null&&!nText.equals("")) {
			btnCancle.setText(nText+"");
		}else{
			btnCancle.setVisibility(View.GONE);
			viewSplit.setVisibility(View.GONE);
			btnOk.setBackgroundResource(R.drawable.dialog_white_note_btn_left_right_click);
		}
		if (pText!=null&&!pText.equals("")) {
			btnOk.setText(pText+"");
		}else{
			btnOk.setVisibility(View.GONE);
			viewSplit.setVisibility(View.GONE);
			btnCancle.setBackgroundResource(R.drawable.dialog_white_note_btn_left_right_click);
		}
		if (nClickListener!=null) {
			btnCancle.setOnClickListener(nClickListener);
		}else {
			btnCancle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.cancel();
				}
			});
		}
		if (pClickListener!=null) {
			btnOk.setOnClickListener(pClickListener);
		}else {
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.cancel();
				}
			});
		}
		return dialog;
	}

	public static Dialog createMiddleDialogWhiteNote(Context context,String title,String note,String nText,String pText,View.OnClickListener nClickListener,View.OnClickListener pClickListener){
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_white_note, null);
		final Dialog dialog =createCustomDialog(context, view);
		LinearLayout llLayout=(LinearLayout) view.findViewById(R.id.ll_dialog);
		LayoutParams layoutParams=llLayout.getLayoutParams();
		int width = context.getResources().getDisplayMetrics().widthPixels;
		layoutParams.width=(int) ((width/7)*5);
		llLayout.setLayoutParams(layoutParams);
		TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
		TextView tvNote= (TextView) view.findViewById(R.id.tv_note);
		Button btnCancle = (Button) view.findViewById(R.id.btn_cancle);
		Button btnOk= (Button) view.findViewById(R.id.btn_ok);
		View viewSplit=  view.findViewById(R.id.view_split);
		tvTitle.setVisibility(View.GONE);
		if (title!=null) {
			tvTitle.setText(title+"");
		}
		if (note!=null) {
			tvNote.setText(note+"");
			tvNote.setGravity(Gravity.CENTER);
		}
		if (nText!=null&&!nText.equals("")) {
			btnCancle.setText(nText+"");
		}else{
			btnCancle.setVisibility(View.GONE);
			viewSplit.setVisibility(View.GONE);
			btnOk.setBackgroundResource(R.drawable.dialog_white_note_btn_left_right_click);
		}
		if (pText!=null&&!pText.equals("")) {
			btnOk.setText(pText+"");
		}else{
			btnOk.setVisibility(View.GONE);
			viewSplit.setVisibility(View.GONE);
			btnCancle.setBackgroundResource(R.drawable.dialog_white_note_btn_left_right_click);
		}
		if (nClickListener!=null) {
			btnCancle.setOnClickListener(nClickListener);
		}else {
			btnCancle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.cancel();
				}
			});
		}
		if (pClickListener!=null) {
			btnOk.setOnClickListener(pClickListener);
		}else {
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.cancel();
				}
			});
		}
		return dialog;
	}

	public static Dialog createDialogCancleAndPositive(Context context,String note,String nText,String pText,View.OnClickListener nClickListener,View.OnClickListener pClickListener){
		return createDialogWhiteNote(context, context.getString(R.string.tips), note, nText, pText, nClickListener, pClickListener);
	}


	public static Dialog createDialogCancleAndPositive(Context context,String title,String note,String pText,View.OnClickListener pClickListener){
		return createDialogWhiteNote(context, title, note, context.getString(R.string.cancle), pText, null, pClickListener);
	}


	public static Dialog createDialogCancleAndPositive(Context context,String title,String note,View.OnClickListener pClickListener){
		return createDialogWhiteNote(context, title, note, context.getString(R.string.cancle), context.getString(R.string.sure), null, pClickListener);
	}

	public static Dialog createDialogCancleAndPositive(Context context,String note,View.OnClickListener pClickListener,View.OnClickListener nClickListener){
		return createDialogWhiteNote(context, context.getString(R.string.tips), note, context.getString(R.string.cancle), context.getString(R.string.sure), nClickListener, pClickListener);
	}

	public static Dialog createDialogCancleAndPositive(Context context,String note,View.OnClickListener pClickListener){
		return createDialogWhiteNote(context, context.getString(R.string.tips), note, context.getString(R.string.cancle), context.getString(R.string.sure), null, pClickListener);
	}
	
	public static Dialog createDialogPositive(Context context,String title,String note,String pText,View.OnClickListener pClickListener){
		return createDialogWhiteNote(context, title, note, null, pText, null, pClickListener);
	}
	
	public static Dialog createDialogPositive(Context context,String title,String note,View.OnClickListener pClickListener){
		return createDialogWhiteNote(context, title, note, null, context.getString(R.string.sure), null, pClickListener);
	}
	
	public static Dialog createDialogPositive(Context context,String note,View.OnClickListener pClickListener){
		return createDialogWhiteNote(context, context.getString(R.string.tips), note, null, context.getString(R.string.sure), null, pClickListener);
	}
	
	public static Dialog createDialogPositive(Context context,String title,String note){
		return createDialogWhiteNote(context,  title, note, null, context.getString(R.string.sure), null,null);
	}

	public static Dialog createDialogPositive(Context context,String note){
		return createDialogWhiteNote(context, context.getString(R.string.tips) , note, null, context.getString(R.string.sure), null,null);
	}

	public static Dialog createMiddleDialogPositive(Context context,String note){
		return createMiddleDialogWhiteNote(context,  context.getString(R.string.tips) , note, null, context.getString(R.string.sure), null,null);
	}
}
