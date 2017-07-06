package cn.appscomm.l38t.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.activity.base.BaseActivity;
import cn.appscomm.l38t.constant.AppUtil;

/**
 * Created by liucheng on 2017/4/17.
 */

public class FeedbackActivity extends BaseActivity {
    @BindView(R.id.btn_device)
    TextView device;
    @BindView(R.id.btn_app)
    TextView app;
    @BindView(R.id.btn_pair)
    TextView pair;
    @BindView(R.id.btn_other)
    TextView other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        tvBarTitle.setText(getString(R.string.title_feedback));
    }


    @OnClick({R.id.btn_device, R.id.btn_app, R.id.btn_pair, R.id.btn_other})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_device:
                AppUtil.sendEmail(FeedbackActivity.this, 0);
                break;
            case R.id.btn_app:
                AppUtil.sendEmail(FeedbackActivity.this, 1);
                break;
            case R.id.btn_pair:
                AppUtil.sendEmail(FeedbackActivity.this, 2);
                break;
            case R.id.btn_other:
                AppUtil.sendEmail(FeedbackActivity.this, 3);
                break;
        }
    }
}
