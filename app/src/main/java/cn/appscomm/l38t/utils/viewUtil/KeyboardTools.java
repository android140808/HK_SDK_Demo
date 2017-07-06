package cn.appscomm.l38t.utils.viewUtil;


import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyboardTools {

	// 隐藏虚拟键盘
	public static void HideKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

		}
	}

	// 显示虚拟键盘
	public static void ShowKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);

	}

	// 强制显示或者关闭系统键盘
	public static void KeyBoard(final EditText txtSearchKey,final boolean status) {
		InputMethodManager m = (InputMethodManager) txtSearchKey.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (status) {
			m.showSoftInput(txtSearchKey, InputMethodManager.SHOW_FORCED);
		} else {
			m.hideSoftInputFromWindow(txtSearchKey.getWindowToken(), 0);
		}
	}
}
