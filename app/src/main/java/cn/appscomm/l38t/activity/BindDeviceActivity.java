package cn.appscomm.l38t.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appscomm.bluetooth.config.BluetoothConfig;
import com.appscomm.bluetooth.manage.AppsBluetoothManager;
import com.appscomm.bluetooth.manage.GlobalVarManager;
import com.appscomm.bluetooth.protocol.command.base.BaseCommand;
import com.appscomm.bluetooth.protocol.command.bind.BindEnd;
import com.appscomm.bluetooth.protocol.command.bind.BindStart;
import com.appscomm.bluetooth.protocol.command.device.DateTime;
import com.appscomm.bluetooth.protocol.command.device.DeviceVersion;
import com.appscomm.bluetooth.protocol.command.device.Unit;
import com.appscomm.bluetooth.protocol.command.device.WatchID;
import com.appscomm.bluetooth.protocol.command.setting.GoalsSetting;
import com.appscomm.bluetooth.protocol.command.user.UserInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.activity.base.BaseActivity;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.config.AppConfig;
import cn.appscomm.l38t.config.GoalConfig;
import cn.appscomm.l38t.config.bean.LocalUserGoal;
import cn.appscomm.l38t.constant.APPConstant;
import cn.appscomm.l38t.constant.AppUtil;
import cn.appscomm.l38t.eventbus.GlobalEvent;
import cn.appscomm.l38t.model.database.UserBindDevice;
import cn.appscomm.l38t.service.GetServiceData;
import cn.appscomm.l38t.service.Query5YearData;
import cn.appscomm.l38t.utils.AppLogger;
import cn.appscomm.l38t.utils.ChooseDevices;
import cn.appscomm.l38t.utils.DateUtil;
import cn.appscomm.l38t.utils.UnitTool;
import cn.appscomm.netlib.bean.account.UserInfoBean;
import cn.appscomm.netlib.config.BaseLocalConfig;
import cn.appscomm.push.AppsCommPushService;
import cn.appscomm.uploaddata.SyncDataService;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/3 11:45
 */
public class BindDeviceActivity extends BaseActivity {

    @BindView(R.id.pb)
    ProgressBar pb;
    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.iv_tap_device)
    ImageView ivTapDevice;

    private String mDeviceAddress;
    private String mDeviceName;
    private Handler mHanler;
    private AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_device);
        ButterKnife.bind(this);
        setTitle(getString(R.string.title_setup_new_device));
        init();
        beginSendCommands();
    }

    @Override
    protected void onResume() {
        com.appscomm.bluetooth.manage.AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).cancelDiscovery();
        super.onResume();
    }

    private void init() {
        mDeviceAddress = getIntent().getStringExtra("DeviceAddress");
        mDeviceName = getIntent().getStringExtra("DeviceName");
        mHanler = new Handler();
        mHanler.removeCallbacksAndMessages(null);
        if (ChooseDevices.CHOOSEDEVICES.equalsIgnoreCase(ChooseDevices.FUSION)) {
            tvInfo.setText(R.string.binding_device_tips_10c);
        } else if (ChooseDevices.CHOOSEDEVICES.equalsIgnoreCase(ChooseDevices.YOUNG)) {
            tvInfo.setText(R.string.binding_device_tips_young);
        } else {
            tvInfo.setText(R.string.binding_device_tips);
        }
        if (ChooseDevices.CHOOSEDEVICES.equalsIgnoreCase(ChooseDevices.FUSION)) {
            ivTapDevice.setImageResource(R.drawable.bind_fusion_animo);
        } else if (ChooseDevices.CHOOSEDEVICES.equalsIgnoreCase(ChooseDevices.YOUNG)) {
            ivTapDevice.setImageResource(R.mipmap.pair_young);
        } else {
            ivTapDevice.setImageResource(R.drawable.bind_her_animo);
        }
        if (ChooseDevices.CHOOSEDEVICES.equalsIgnoreCase(ChooseDevices.FUSION) || ChooseDevices.CHOOSEDEVICES.equalsIgnoreCase(ChooseDevices.HER)) {
            animationDrawable = (AnimationDrawable) ivTapDevice.getDrawable();
            animationDrawable.start();
        }
    }


    private void beginSendCommands() {
        // 去掉圆环进度
        pb.setVisibility(View.GONE);
        final ArrayList<BaseCommand> list = new ArrayList<>();
        list.add(bindStart());
        list.add(setDateTime());
        list.add(getWatchID());
        list.add(getDeviceVersion());
        list.add(setUserInfo());
        list.add(unit());
        list.add(getGoal());
        list.add(bindEnd());
        // 延迟100毫发送指令
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommandsNoBind(list);
            }
        }, 100);
    }

    BaseCommand.CommandResultCallback iResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {
            if (baseCommand instanceof BindEnd) {   //绑定结束
                if (!BindDeviceActivity.this.isFinishing() && !BindDeviceActivity.this.isDestroyed()) {//在没有完成本地保存就退出
                    dismissLoadingDialog();
                    GetServiceData.startService();
                    pb.setVisibility(View.GONE);
                    showSuccessViews();
                    successSave();
                    showResult();
                }
            } else if (baseCommand instanceof GoalsSetting) {
                LocalUserGoal userGoal = new LocalUserGoal();
                userGoal.setGoals_step(GlobalVarManager.getInstance().getStepGoalsValue());
                userGoal.setGoals_activeMinutes(GlobalVarManager.getInstance().getActiveTimeGoalsValue());
                userGoal.setGoals_distance(GlobalVarManager.getInstance().getDistanceGoalsValue());
                userGoal.setGoals_calories(GlobalVarManager.getInstance().getCalorieGoalsValue());
                userGoal.setGoals_sleep(GlobalVarManager.getInstance().getSleepGoalsValue());
                GoalConfig.setLocalUserGoal(userGoal);
            } else if (baseCommand instanceof BindStart) {
                showBigLoadingProgress(getString(R.string.loading));
            }
        }

        @Override
        public void onFail(BaseCommand baseCommand) {
            AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).killCommandRunnable();
            AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).clearCommandBlockingDeque();
            showResult();
            doFailed();
        }
    };

    private void showResult() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (animationDrawable != null) {
                    animationDrawable.stop();
                }
                if (ChooseDevices.CHOOSEDEVICES.equalsIgnoreCase(ChooseDevices.FUSION)) {
                    ivTapDevice.setImageResource(R.mipmap.fusion_big);
                } else if (ChooseDevices.CHOOSEDEVICES.equalsIgnoreCase(ChooseDevices.YOUNG)) {
                    ivTapDevice.setImageResource(R.mipmap.young_big);
                } else {
                    ivTapDevice.setImageResource(R.mipmap.her_big);
                }
            }
        });
    }

    private void doFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissLoadingDialog();
                pb.setVisibility(View.GONE);
                showTipDialog(getString(R.string.bing_failed), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialog();
                        startReBind();
                    }
                });
            }
        });
    }

    /**
     * 绑定成功
     */
    private void successSave() {
        BluetoothConfig.setDefaultMac(GlobalApp.getAppContext(), mDeviceAddress);

        UserBindDevice userBindDevice = new UserBindDevice();
        userBindDevice.setUserId(AccountConfig.getUserId());
        userBindDevice.setDeviceAddress(mDeviceAddress);
        userBindDevice.setDeviceId(GlobalVarManager.getInstance().getWatchID());
        userBindDevice.setDeviceVersion(GlobalVarManager.getInstance().getSoftVersion());
        userBindDevice.setBindTime(DateUtil.dateToSec(new Date()));
        userBindDevice.setDeviceName(mDeviceName);
        userBindDevice.setDeviceType(AppUtil.getDeviceType(mDeviceName));
        UserBindDevice.updateOrAddByUserId(userBindDevice);

        AppsCommPushService.startService();
        SyncDataService.startService();
        GlobalEvent globalEvent = new GlobalEvent();
        globalEvent.setCode(GlobalEvent.EVENBUS_SIGNAL_CODE_SYNC_NOW);
        EventBus.getDefault().post(globalEvent);
        returnMainActivity();
        finish();
    }

    private void showSuccessViews() {
        dismissLoadingDialog();
        pb.setVisibility(View.GONE);
        tvInfo.setText(R.string.bing_successful);
    }

    private BindStart bindStart() {
        long uid = Long.parseLong(AccountConfig.getUserId() + "");
        AppLogger.d(TAG, "设置UID=" + uid);
        return new BindStart(iResultCallback, (byte) 0x00, uid);
    }

    private BaseCommand getGoal() {
        return new GoalsSetting(iResultCallback);
    }

    private BaseCommand unit() {
        return new Unit(iResultCallback, (byte) AppConfig.getLocalUnit());
    }

    private BindEnd bindEnd() {
        return new BindEnd(iResultCallback, (byte) 0x01);
    }

    private WatchID getWatchID() {
        return new WatchID(iResultCallback);
    }

    private DeviceVersion getDeviceVersion() {
        return new DeviceVersion(iResultCallback, DeviceVersion.DeviceSoftVersion);
    }

    private DateTime setDateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        AppLogger.d(TAG, "设置设备的时间=" + com.appscomm.bluetooth.utils.DateUtil.dateToSec(calendar.getTime()));
        return new DateTime(iResultCallback, 7, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    private UserInfo setUserInfo() {
        UserInfoBean localUser = AccountConfig.getUserInfoBean();
        int sex = localUser.getSex();
        int age = localUser.getAge();
        float fHeight = localUser.getHeight();
        float fWeight = localUser.getWeight();
        int height = UnitTool.getHalfUpValue(fHeight, 0).intValue();
        int weight = UnitTool.getHalfUpValue(fWeight * 10, 0).intValue();
        return new UserInfo(iResultCallback, 5, sex, age, height, weight);
    }


    public void returnMainActivity() {
        if (!(AccountConfig.getUserLoginName().equalsIgnoreCase(BaseLocalConfig.getInstance().getString(APPConstant.QUERY_USER, ""))
                && GlobalVarManager.getInstance().getWatchID().equalsIgnoreCase(BaseLocalConfig.getInstance().getString(APPConstant.QUERY_DEVICE_ID, "")))) {
            //这个分支有一个不相等
            BaseLocalConfig.getInstance().saveLong(APPConstant.QUERY_TIME, System.currentTimeMillis());
        }
        //如果相等则不改变更新的时间。继续更新。
        BaseLocalConfig.getInstance().saveString(APPConstant.QUERY_USER, AccountConfig.getUserLoginName());
        BaseLocalConfig.getInstance().saveString(APPConstant.QUERY_DEVICE_ID, GlobalVarManager.getInstance().getWatchID());
        BaseLocalConfig.getInstance().saveBoolean(APPConstant.QUERY_SETTING, true);
        BaseLocalConfig.getInstance().saveLong(APPConstant.QUERY_TIME_BASE, System.currentTimeMillis());
        Query5YearData.startService();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    private void startReBind() {
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).disConnectDevice(false);
        startActivity(DeviceScanActivity.class);
        finish();
    }

    @OnClick(R.id.btn_ok)
    public void onClick() {
        if (pb.getVisibility() != View.VISIBLE) {
            startReBind();
        }
    }

    @Override
    public void onBackPressed() {
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).disConnectDevice(false);
        startActivity(DeviceScanActivity.class);
    }

    @Override
    protected void onDestroy() {
        mHanler.removeCallbacksAndMessages(null);
        mHanler = null;
        super.onDestroy();
    }
}
