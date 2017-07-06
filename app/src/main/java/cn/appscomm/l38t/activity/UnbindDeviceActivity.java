package cn.appscomm.l38t.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.appscomm.bluetooth.config.BluetoothConfig;
import com.appscomm.bluetooth.manage.AppsBluetoothManager;

import butterknife.BindView;
import butterknife.OnClick;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.activity.base.BaseActivity;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.constant.APPConstant;
import cn.appscomm.l38t.constant.AppUtil;
import cn.appscomm.l38t.model.database.UserBindDevice;
import cn.appscomm.l38t.service.Query5YearData;
import cn.appscomm.l38t.utils.ChooseDevices;
import cn.appscomm.netlib.config.BaseLocalConfig;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/5 14:01
 */
public class UnbindDeviceActivity extends BaseActivity {
    @BindView(R.id.iv_l39)
    ImageView showImg;
    private BluetoothAdapter mBluetoothAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unbind_device);
        setTitle(getString(R.string.unbinddevice));
        init();
    }

    private void init() {
        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager != null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }
        if (AppUtil.getShowName().equalsIgnoreCase(ChooseDevices.HER)) {
            showImg.setImageResource(R.mipmap.her_big);
        } else if (AppUtil.getShowName().equalsIgnoreCase(ChooseDevices.YOUNG)) {
            showImg.setImageResource(R.mipmap.young_big);
        } else {
            showImg.setImageResource(R.mipmap.fusion_big);
        }
    }

    /**
     * 解除绑定操作
     */
    private void doUnbind() {
        UserBindDevice.deleteByUserId(AccountConfig.getUserId());
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext())
                .doUnbindDevice(BluetoothConfig.getDefaultMac(UnbindDeviceActivity.this));
        BluetoothConfig.setDefaultMac(GlobalApp.getAppContext(), "");
        BaseLocalConfig.getInstance().saveBoolean(APPConstant.QUERY_SETTING,false);
        Query5YearData.stopService();
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @OnClick(R.id.btn_unbind)
    public void onClick() {
        showTipDialogCancleAndPositive(getString(R.string.unbinddevice_tips), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                doUnbind();
            }
        });
    }
}
