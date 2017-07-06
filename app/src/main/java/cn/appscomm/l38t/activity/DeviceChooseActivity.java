package cn.appscomm.l38t.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.activity.base.BaseActivity;
import cn.appscomm.l38t.utils.ChooseDevices;

/**
 * Created by Bingo on 2016/10/19.
 */

public class DeviceChooseActivity extends BaseActivity {
    @BindView(R.id.iv_selected_color)
    ImageView ivSelectedColor;
    @BindView(R.id.color_type)
    RelativeLayout colorType;
    @BindView(R.id.iv_selected_fusion)
    ImageView ivSelectedFusion;
    @BindView(R.id.fusion_type)
    RelativeLayout fusionType;
    @BindView(R.id.iv_selected_her)
    ImageView ivSelectedHer;
    @BindView(R.id.her_type)
    RelativeLayout herType;
    @BindView(R.id.iv_selected_young)
    ImageView ivSelectedYoung;
    @BindView(R.id.young_type)
    RelativeLayout youngType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_device);
        ButterKnife.bind(this);
        setTitle(getString(R.string.title_setup_device_type));
    }

    @OnClick({R.id.color_type, R.id.fusion_type, R.id.her_type, R.id.young_type})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.color_type:
                ChooseDevices.CHOOSEDEVICES = ChooseDevices.COLOR;
                selectDevice(ivSelectedColor);
                break;
            case R.id.fusion_type:
                ChooseDevices.CHOOSEDEVICES = ChooseDevices.FUSION;
                selectDevice(ivSelectedFusion);
                break;
            case R.id.her_type:
                ChooseDevices.CHOOSEDEVICES = ChooseDevices.HER;
                selectDevice(ivSelectedHer);
                break;
            case R.id.young_type:
                ChooseDevices.CHOOSEDEVICES = ChooseDevices.YOUNG;
                selectDevice(ivSelectedYoung);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(MainActivity.class);
        finish();
    }

    private void selectDevice(View v) {
        ivSelectedColor.setVisibility(View.GONE);
        ivSelectedFusion.setVisibility(View.GONE);
        ivSelectedHer.setVisibility(View.GONE);
        ivSelectedYoung.setVisibility(View.GONE);
        v.setVisibility(View.VISIBLE);
        startActivity(EnsureDeviceNearbyActivity.class);
        finish();
    }
}
