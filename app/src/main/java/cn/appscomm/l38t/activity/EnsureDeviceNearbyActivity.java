package cn.appscomm.l38t.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.activity.base.BaseActivity;
import cn.appscomm.l38t.utils.ChooseDevices;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/3 12:05
 */
public class EnsureDeviceNearbyActivity extends BaseActivity {
    @BindView(R.id.btn_ok)
    Button btnOk;
    @BindView(R.id.show_device)
    ImageView showImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ensure_device_nearby);
        setTitle(getString(R.string.title_turn_on_bluetooth));
        init();
    }

    private void init() {
        if (ChooseDevices.CHOOSEDEVICES.equalsIgnoreCase(ChooseDevices.HER)) {
            showImg.setImageResource(R.mipmap.her_big);
        } else if (ChooseDevices.CHOOSEDEVICES.equalsIgnoreCase(ChooseDevices.YOUNG)) {//YOUNG的图片
            showImg.setImageResource(R.mipmap.young_big);
        } else {
            showImg.setImageResource(R.mipmap.fusion_big);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @OnClick(R.id.btn_ok)
    public void onClick() {
        startActivity(DeviceScanActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(DeviceChooseActivity.class);
        finish();
    }
}
