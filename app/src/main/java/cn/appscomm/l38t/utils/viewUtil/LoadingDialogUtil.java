package cn.appscomm.l38t.utils.viewUtil;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.appscomm.l38t.R;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/2 11:25
 */
public class LoadingDialogUtil {

    /**
     * 大的加载对话框
     * @param context
     * @param message
     * @return
     */
    public static Dialog createBigLoadingDialog(Context context, String message) {
        Dialog dialog;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.dialog_write_card_loading, null);
        TextView tvMessage = (TextView) linearLayout.findViewById(R.id.tv_message);
        if (tvMessage != null) {
            tvMessage.setText(message);
        }
        dialog = new Dialog(context, R.style.loading_dialog);
        dialog.setContentView(linearLayout);
        return dialog;
    }
}
