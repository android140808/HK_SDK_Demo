package cn.appscomm.l38t.UI.edit;

import android.annotation.TargetApi;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Administrator on 2016/8/10.
 */
public class AutoDelEditText extends EditText {
    private boolean autoDelFlag;

    public AutoDelEditText(Context context) {
        super(context);
    }

    public AutoDelEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoDelEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public AutoDelEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setAutoDelFlag(boolean flag){
        autoDelFlag=flag;
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               // Log.d("TAG","beforeTextChanged--------------->start="+start+",count="+count+",after="+after+",s="+s.toString()+",autoDelFlag="+autoDelFlag);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               // Log.d("TAG","onTextChanged--------------->start="+start+",count="+count+",before="+before+",s="+s.toString());
                if (autoDelFlag){
                    if (s!=null&&s.length()==(start+count)) {
                        autoDelFlag=false;
                        String nowTxt=s.toString().substring(start,start+count);
                        AutoDelEditText.this.setText(nowTxt);
                        AutoDelEditText.this.setSelection(nowTxt.length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
               // Log.d("TAG","afterTextChanged--------------->");
            }
        });
    }

}
