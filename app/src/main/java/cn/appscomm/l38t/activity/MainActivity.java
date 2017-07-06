package cn.appscomm.l38t.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.appscomm.bluetooth.bean.AvatertSwitch;
import com.appscomm.bluetooth.bean.BluetoothScanDevice;
import com.appscomm.bluetooth.bean.LEDBean;
import com.appscomm.bluetooth.bean.LEDControlSwitch;
import com.appscomm.bluetooth.config.BluetoothConfig;
import com.appscomm.bluetooth.interfaces.BlueToothManagerCommandListener;
import com.appscomm.bluetooth.interfaces.BluetoothManagerDeviceConnectListener;
import com.appscomm.bluetooth.interfaces.BluetoothManagerScanListener;
import com.appscomm.bluetooth.interfaces.HeartRateCallback;
import com.appscomm.bluetooth.interfaces.PressSecondListener;
import com.appscomm.bluetooth.manage.AppsBluetoothManager;
import com.appscomm.bluetooth.manage.GlobalVarManager;
import com.appscomm.bluetooth.protocol.command.base.BaseCommand;
import com.appscomm.bluetooth.protocol.command.device.BatteryPower;
import com.appscomm.bluetooth.protocol.command.device.Unit;
import com.appscomm.bluetooth.protocol.command.setting.SwitchSetting;
import com.appscomm.bluetooth.utils.BaseUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.appscomm.l38t.R;
import cn.appscomm.l38t.UI.progress.TitleProgressView;
import cn.appscomm.l38t.UI.show.LineRankData;
import cn.appscomm.l38t.UI.show.LineRankView;
import cn.appscomm.l38t.UI.uv.UVChart;
import cn.appscomm.l38t.activity.base.BaseActivity;
import cn.appscomm.l38t.activity.setting.HelpActivity;
import cn.appscomm.l38t.app.AppManager;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.config.AppConfig;
import cn.appscomm.l38t.config.DeviceSyncConfig;
import cn.appscomm.l38t.config.GoalConfig;
import cn.appscomm.l38t.config.bean.LocalUserGoal;
import cn.appscomm.l38t.constant.APPConstant;
import cn.appscomm.l38t.constant.AppUtil;
import cn.appscomm.l38t.eventbus.GlobalEvent;
import cn.appscomm.l38t.eventbus.base.EventBusMessage;
import cn.appscomm.l38t.loader.LeaderBoardLoader;
import cn.appscomm.l38t.model.bean.HomeListBean;
import cn.appscomm.l38t.model.database.DeviceStatusInfo;
import cn.appscomm.l38t.model.database.UserBindDevice;
import cn.appscomm.l38t.service.Query5YearData;
import cn.appscomm.l38t.utils.BackgroundThread;
import cn.appscomm.l38t.utils.ChooseDevices;
import cn.appscomm.l38t.utils.CommonUtils;
import cn.appscomm.l38t.utils.ImageUtil;
import cn.appscomm.l38t.utils.LogUtil;
import cn.appscomm.l38t.utils.Logger;
import cn.appscomm.l38t.utils.ShareSdkUtils;
import cn.appscomm.l38t.utils.SleepDataUtils;
import cn.appscomm.l38t.utils.SystemTools;
import cn.appscomm.l38t.utils.TimeZoneUtils;
import cn.appscomm.l38t.utils.UnitHelper;
import cn.appscomm.l38t.utils.UnitTool;
import cn.appscomm.l38t.utils.UniversalImageLoaderHelper;
import cn.appscomm.l38t.utils.viewUtil.MainStatusShowUtil;
import cn.appscomm.l38t.utils.viewUtil.ShowUtils;
import cn.appscomm.netlib.bean.account.UserInfoBean;
import cn.appscomm.netlib.bean.leaderboard.FriendsAccount;
import cn.appscomm.netlib.bean.leaderboard.LeaderBoard;
import cn.appscomm.push.NotificationObserver;
import cn.appscomm.push.constant.PushConstant;
import cn.appscomm.uploaddata.SyncDataService;
import cn.appscomm.uploaddata.database.HeartDataDB;
import cn.appscomm.uploaddata.database.SportData;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

public class MainActivity extends BaseActivity implements HeartRateCallback {

    @BindView(R.id.tv_main_bar_title)
    TextView tvMainBarTitle;
    @BindView(R.id.iv_sync)
    ImageView ivSync;
    @BindView(R.id.cb_sleep)
    CheckBox cbSleep;
    @BindView(R.id.ic_sleep_icon)
    ImageView icSleepIcon;
    @BindView(R.id.connectIcon)
    ImageView connectIcon;
    @BindView(R.id.tv_device_power)
    TextView tvDevicePower;
    @BindView(R.id.iv_device_power)
    ImageView ivDevicePower;
    @BindView(R.id.choose_device)
    ImageView chooseDevice;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    @BindView(R.id.tv_device_status)
    TextView tvDeviceStatus;
    @BindView(R.id.ll_device_power)
    LinearLayout llDevicePower;
    @BindView(R.id.sv_main)
    PullToRefreshScrollView svMain;
    @BindView(R.id.mainStatus)
    LinearLayout mainStatus;
    @BindView(R.id.mainDeviceStatus)
    LinearLayout mainDeviceStatus;
    @BindView(R.id.lineRankViewFriends)
    LineRankView lineRankViewFriends;
    @BindView(R.id.uv_progress_bar)
    UVChart uvChart;            //紫外线View
    @BindView(R.id.uv_display)
    LinearLayout uvDisplay;            //紫外线View
    @BindView(R.id.tv_uv_desc)
    TextView uvDesc;            //紫外线描述
    @BindView(R.id.uvv)
    Button uvv;
    @BindView(R.id.sleep)
    Button sleep;
    @BindView(R.id.sport)
    Button sport;
    @BindView(R.id.led)
    Button led;
    @BindView(R.id.ota)
    Button ota;

    private AnimationDrawable an_sync;

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private boolean isNowSyncFlag = false;
    private boolean sleepModelFlag = false;
    private boolean showToastFlag = false;
    private int queryLeaderBoardCount = 0;

    private static final SimpleDateFormat simpleDateFormatLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //时间格式
    protected Gson gson = new GsonBuilder().create();   //Gson

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        chechPermission();
        initView();
        initRefresh();
        init();
        checkBindDevice(true);
    }

    /**
     * 初始化下拉刷新
     */
    private void initRefresh() {
        svMain.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        svMain.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (!isNowSyncFlag && checkBindDevice(true)) {
                    syncNow();
                }
            }
        });
    }

    private void init() {
        mHandler = new Handler();

        DeviceStatusInfo deviceStatusInfo = DeviceStatusInfo.saveAndGetDeviceStatusInfoLocal(GlobalVarManager.getInstance());
        doSyncRefreshViews(deviceStatusInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (!isNowSyncFlag && checkBindDevice(true)) {
//            syncNow();
//        }
//        protectNotification();
        changeSetting();
        DeviceSyncConfig.setLastSyncTime(0);
        powerControl();
        scroll2Top();
        checkStatus();
        regConnectListener();
        reconnectDevice();
        initView();
        chechPermission();
        DeviceStatusInfo deviceStatusInfo = DeviceStatusInfo.saveAndGetDeviceStatusInfoLocal(GlobalVarManager.getInstance());
        doSyncRefreshViews(deviceStatusInfo);
//        queryLeaderBoard();
        TextListener();
    }


    private void changeSetting() {
        int alwaysFinish = Settings.Global.getInt(getContentResolver(), Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0);
        if (alwaysFinish == 1) {
            Dialog dialog = new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.open_keep_app))
                    .setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton(getString(R.string.title_advanced), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(
                                    Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                            startActivity(intent);
                        }
                    }).create();
            dialog.show();
        }
    }


    private void regConnectListener() {
        if (AppUtil.haveBindDevice()) {
            AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).clearBluetoothManagerDeviceConnectListeners();
            AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).addBluetoothManagerScanListeners(scanListener);
            AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).addBluetoothManagerDeviceConnectListener(deviceConnectListener);
        }
    }

    private Runnable scanDeviceRunnable = new Runnable() {
        @Override
        public void run() {
            scanDevice();
        }
    };

    private void initAdapter() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mHandler != null) {
            mHandler.postDelayed(scanDeviceRunnable, 500);
        }
    }

    private synchronized void scanDevice() {
        if (mBluetoothAdapter == null) {
            showToast(getString(R.string.error_bluetooth_not_supported));
            return;
        }
        if (mBluetoothAdapter.isEnabled()) {
//            showBigLoadingProgress(getString(R.string.searching), new DialogInterface.OnKeyListener() {
//                @Override
//                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                    if (keyCode == KeyEvent.KEYCODE_BACK) {
//                        dismissLoadingDialog();
//                        com.appscomm.bluetooth.manage.AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).cancelDiscovery();
//                    }
//                    return false;
//                }
//            });
            if (!isDeviceConnected() && !TextUtils.isEmpty(BluetoothConfig.getDefaultMac(GlobalApp.getAppContext()))) {
                AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).startDiscovery();
            }
        } else {
            showToast(getString(R.string.turn_on_bluetooth_tips));
        }
    }

    private BluetoothManagerScanListener scanListener = new BluetoothManagerScanListener() {
        @Override
        public void onDeviceFound(BluetoothScanDevice scanDevice) {
            if (scanDevice != null && scanDevice.getBluetoothDevice() != null) {
                BluetoothDevice bluetoothDevice = scanDevice.getBluetoothDevice();
                if (null != bluetoothDevice.getName() && AppUtil.isCanScanDevice(bluetoothDevice.getName())) {
                    UserBindDevice userBindDevice = UserBindDevice.getBindDevice(AccountConfig.getUserId());
                    if (userBindDevice != null && userBindDevice.getDeviceName().equals(bluetoothDevice.getName())) {
                        LogUtil.i(TAG, "搜索到设备,deviceName=" + bluetoothDevice.getName() + "/address:" + bluetoothDevice.getAddress());
                        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).cancelDiscovery();
                        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).connectDevice(bluetoothDevice.getAddress());
                    } else {

                    }
                }
            }
        }

        @Override
        public void onDeviceScanEnd() {
            if (!MainActivity.this.isDestroyed()) {
                dismissLoadingDialog();
            }
        }
    };

    private BluetoothManagerDeviceConnectListener deviceConnectListener = new BluetoothManagerDeviceConnectListener() {
        @Override
        public void onConnected(BluetoothDevice bluetoothDevice) {
            onDeviceConnected();
        }

        @Override
        public void onConnectFailed() {
            onDeviceDisconnected();
        }

        @Override
        public void onConnectDeviceTimeOut() {
            onDeviceDisconnected();
            dismissLoadingDialog();
            isNowSyncFlag = false;
            an_sync.stop();
        }

        @Override
        public void onEnableToSendComand(BluetoothDevice bluetoothDevice) {
            onDeviceEnableCmd();
        }
    };

    private void getDevicePower(final boolean sync) {
        if (!checkBindDevice(false)) {
            return;//没有绑定 不获取电量
        }
        BatteryPower batteryPower = new BatteryPower(new BaseCommand.CommandResultCallback() {
            @Override
            public void onSuccess(BaseCommand baseCommand) {
                showDevicePower(GlobalVarManager.getInstance().getBatteryPower());
                if (sync) {
                    Activity now = AppManager.getAppManager().currentActivity();
                    if (now != null && now instanceof MainActivity) {//当前activity为主页才刷新
                        if (!isNowSyncFlag) {
                            long timeDis = System.currentTimeMillis() - DeviceSyncConfig.getLastSyncTime();
                            if (timeDis >= 1000 * 60 * 30) {
                                dismissLoadingDialog();
                                syncNow();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFail(BaseCommand baseCommand) {
                GlobalVarManager.getInstance().setBatteryPower(0);
                showDevicePower(-1);
            }
        });
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommand(batteryPower);
    }


    private void showDevicePower(final int power) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (power >= 0) {
                    // Her的电量不需要显示
                    tvDevicePower.setText(power + "%");
                    ivDevicePower.setImageResource(ShowUtils.getDevicePowerLevelDrawable2(power));
                } else {
                    tvDevicePower.setText(0 + "%");
                    ivDevicePower.setImageResource(ShowUtils.getDevicePowerLevelDrawable2(0));
                }
            }
        });
    }

    private void reconnectDevice() {
        if (!checkBindDevice(false)) {
            return;
        }
        if (!AppUtil.checkBluetooth(this)) {
            return;
        }
        if (!isDeviceConnected()) {
            GlobalVarManager.getInstance().setBatteryPower(0);
            showDevicePower(-1);
            dismissLoadingDialog();
            showBigLoadingProgress(getString(R.string.connecting));
            showToastFlag = true;
//            AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).onDestroy();
//            AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).redoRegiresterService();
            initAdapter();
        } else {
            if (llDevicePower.getVisibility() != View.VISIBLE) {
                getDevicePower(false);//已经连接上了，没显示电量，获取电量
            }
        }
    }

    private boolean isDeviceConnected() {
        int deviceConnectState = AppsBluetoothManager.getInstance(this).getDeviceConnectState();
        if (deviceConnectState == 1 || deviceConnectState == 3) {
            tvDeviceStatus.setText(getString(R.string.connected));
            connectIcon.setImageResource(R.mipmap.ic_device_connected);
            return true;
        } else {
            GlobalVarManager.getInstance().setBatteryPower(0);
            showDevicePower(-1);
            tvDeviceStatus.setText(getString(R.string.disconnected));
            connectIcon.setImageResource(R.mipmap.ic_device_disconnected);
        }
//        String nowStatus = tvDeviceStatus.getText().toString();
//        String power = tvDevicePower.getText().toString().replace("%", "");
//        if (nowStatus.equals(getString(R.string.connected)) && Integer.valueOf(power) >= 0) {
//            return true;
//        } else {
//
//        }
        return false;
    }

    private void checkStatus() {
        if (!checkBindDevice(false)) {
            //没有绑定，设置为未连接状态
            onDeviceDisconnected();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String showName = AppUtil.getShowName();
                if (!showName.equalsIgnoreCase(tvDeviceName.getText().toString().trim())) {
                    tvDeviceName.setText(showName);
                    tvDeviceName.setMinWidth((int) (tvDeviceStatus.getTextSize() + 0.5f));
                    tvDeviceName.setVisibility(View.VISIBLE);
                }
                if (!TextUtils.isEmpty(showName)) {
                    //调整字体大小
                    tvDeviceStatus.setMinWidth((int) (tvDeviceStatus.getTextSize() + 0.5f));
                    tvDeviceStatus.setVisibility(View.VISIBLE);
                } else {
                    tvDeviceStatus.setVisibility(View.INVISIBLE);
                }
                tvMainBarTitle.setText(showName);
            }
        });
    }

    private void onDeviceConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissLoadingDialog();
                tvDeviceStatus.setText(getString(R.string.connected));
                connectIcon.setImageResource(R.mipmap.ic_device_connected);
            }
        });
    }

    private void onDeviceEnableCmd() {
        getDevicePower(true);
    }

    private void onDeviceDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissLoadingDialog();
                GlobalVarManager.getInstance().setBatteryPower(0);
                showDevicePower(-1);
                tvDeviceStatus.setText(getString(R.string.disconnected));
                connectIcon.setImageResource(R.mipmap.ic_device_disconnected);
                if (showToastFlag) {
                    showToast(getString(R.string.disconnected));
                    showToastFlag = false;
                }
                GlobalVarManager.getInstance().setSportSleepMode(0);
                icSleepIcon.setImageResource(R.mipmap.ic_sleep_off);
                cbSleep.setChecked(false);
                svMain.onRefreshComplete();
                isNowSyncFlag = false;
                an_sync.stop();
            }
        });
    }


    private void initView() {
        AppsBluetoothManager.getInstance(this).setHeartRateCallback(this);
        rlBar.setVisibility(View.GONE);
        tvMainBarTitle.setText(AppUtil.getShowName());
        an_sync = (AnimationDrawable) ivSync.getDrawable();
        an_sync.stop();
        if (AppUtil.getShowName().equalsIgnoreCase(ChooseDevices.HER)) {
            chooseDevice.setImageResource(R.mipmap.her_small);
            uvDisplay.setVisibility(View.VISIBLE);
            mainStatus.setVisibility(View.GONE);
        } else if (AppUtil.getShowName().equalsIgnoreCase(ChooseDevices.YOUNG)) {
            chooseDevice.setImageResource(R.mipmap.icon_young);
            uvDisplay.setVisibility(View.GONE);
            mainStatus.setVisibility(View.VISIBLE);
        } else {
            chooseDevice.setImageResource(R.mipmap.fusion_small);
            uvDisplay.setVisibility(View.GONE);
            mainStatus.setVisibility(View.GONE);
        }
        checkStatus();
        powerControl();
    }


    private void chechPermission() {
        if (AppUtil.haveBindDevice() && !CommonUtils.isServiceWork(this, NotificationObserver.class.getName())) {
            for (int i = 0; i < 3; i++) {
                try {
                    PackageManager pm = getPackageManager();
                    ComponentName componentName = new ComponentName(this, NotificationObserver.class);
                    pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                    break;
                } catch (Exception e) {
                    SystemClock.sleep(300);
                }
            }
        }
        if (AppUtil.getShowName().startsWith(ChooseDevices.YOUNG)) {
            PushConstant.isDeviceHaveScreen = true;
        } else {
            PushConstant.isDeviceHaveScreen = false;
        }
    }

    private void powerControl() {
        if (!checkBindDevice(false)) {
            llDevicePower.setVisibility(View.INVISIBLE);
        } else {
            llDevicePower.setVisibility(View.VISIBLE);
        }
    }

    private void scroll2Top() {
        /*mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((ScrollView) findViewById(R.id.sv_main)).smoothScrollTo(0, 0);//滑动到顶部
            }
        }, 400);*/
    }

    /**
     * 分享
     */
    private void beginShare() {
        BackgroundThread.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = SystemTools.shotView(findViewById(R.id.ll_content));
                        String imageName = "shareImage.jpg";
                        String imagePath = GlobalApp.getAppContext().getFilesDir() + File.separator + imageName;
                        boolean flag = ImageUtil.writeBitmapToLocal(GlobalApp.getAppContext(), bitmap, imageName, true);
                        if (flag) {
                            String unitDistanceString = UnitHelper.getInstance().getArrDistanceUnit()[AppConfig.getLocalUnit()];
                            String content = getString(R.string.share_content_tips, getLocalDistanceString(), unitDistanceString, getLocalCaloriesString());
                            ShareSdkUtils.showShare(GlobalApp.getAppContext(), null, APPConstant.APP_WEBSIZE, getString(R.string.app_big_name), content, imagePath, true, new PlatformActionListener() {
                                @Override
                                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                                    showToast(getString(R.string.ssdk_oks_share_completed));
                                }

                                @Override
                                public void onError(Platform platform, int i, Throwable throwable) {
                                    showToast(getString(R.string.ssdk_oks_share_failed));
                                }

                                @Override
                                public void onCancel(Platform platform, int i) {
                                    showToast(getString(R.string.ssdk_oks_share_canceled));
                                }
                            });
                        } else {
                            showToast(getString(R.string.ssdk_oks_share_failed));
                        }
                    }
                });
            }
        }, 300);
    }

    private String getLocalDistanceString() {
        DeviceStatusInfo deviceStatusInfo = DeviceStatusInfo.findByAccountId(AccountConfig.getUserLoginName());
        if (deviceStatusInfo == null) {
            return "0";
        }
        double result = 0.0;
        int unit = AppConfig.getLocalUnit();
        if (unit == UnitHelper.UNIT_US) {//英制
            result = deviceStatusInfo.getDistance() * Unit.UNIT_S_EN_NUM;
        } else {
            result = deviceStatusInfo.getDistance();
        }
        return UnitTool.getMiToKmShowString(result);
    }

    private String getLocalCaloriesString() {
        DeviceStatusInfo deviceStatusInfo = DeviceStatusInfo.findByAccountId(AccountConfig.getUserLoginName());
        if (deviceStatusInfo == null) {
            return "0";
        }
        return UnitTool.getKaToKKaShowString(deviceStatusInfo.getCalories());
    }

    @Override
    public void doEventBusMessageAsync(EventBusMessage message) {
        super.doEventBusMessageAsync(message);
        switch (message.getCode()) {
            case GlobalEvent.EVENBUS_SIGNAL_CODE_DEVICE_BOUND_BY_OTHER:
                LogUtil.i(TAG, "收到不同用户绑定手环信息");
                dismissLoadingDialog();
                showTipDialog(getString(R.string.userUID_wrong), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialog();
                        UserBindDevice.deleteByUserId(AccountConfig.getUserId());
                        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).doUnbindDevice(BluetoothConfig.getDefaultMac(MainActivity.this));
                        BluetoothConfig.setDefaultMac(GlobalApp.getAppContext(), "");
                        startActivity(TurnOnBluetoothActivity.class);
                    }
                });
                break;
            case GlobalEvent.EVENBUS_SIGNAL_CODE_DEVICE_CONNECTED:
                LogUtil.i(TAG, "收到设备连接成功消息");
                onDeviceConnected();
                break;
            case GlobalEvent.EVENBUS_SIGNAL_CODE_DEVICE_ENABLE_CMD:
                LogUtil.i(TAG, "收到设备可以发送指令消息");
                getDevicePower(false);
                break;
            case GlobalEvent.EVENBUS_SIGNAL_CODE_SYNC_NOW:
                LogUtil.i(TAG, "收到同步消息,准备进行同步");
                syncNow();
                break;
            case GlobalEvent.EVENBUS_SIGNAL_CODE_SYNC_FAILED:
                LogUtil.i(TAG, "收到同步消息,同步失败");
                doSyncEnd(false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        svMain.onRefreshComplete();
                    }
                });
                break;
            case GlobalEvent.EVENBUS_SIGNAL_CODE_DEVICE_DISCONNECTED:
                LogUtil.i(TAG, "收到设备断开连接消息,进行界面刷新");
                showToastFlag = false;
                onDeviceDisconnected();
                svMain.onRefreshComplete();
                break;
            case GlobalEvent.EVENBUS_SIGNAL_CODE_UNIT_CHANGE_REFRSH:
            case GlobalEvent.EVENBUS_SIGNAL_CODE_GOAL_CHANGE_REFRSH:
            case GlobalEvent.EVENBUS_SIGNAL_CODE_SYNC_REFRSH:
                LogUtil.i(TAG, "收到刷新消息,进行同步界面刷新");
                break;
            case GlobalEvent.EVENBUS_SIGNAL_CODE_SYNC_END:
                LogUtil.i(TAG, "收到同步消息,同步结束");
                LogUtil.i(TAG, "收到刷新消息,进行同步界面刷新" + GlobalVarManager.getInstance().toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).cancelDiscovery();
                        DeviceStatusInfo deviceStatusInfo = DeviceStatusInfo.saveAndGetDeviceStatusInfoLocal(GlobalVarManager.getInstance());
                        doSyncRefreshViews(deviceStatusInfo);
                        doSyncEnd(true);
                        svMain.onRefreshComplete();
                    }
                });
                break;
        }
    }


    private void doSyncEnd(final boolean isSuccess) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isNowSyncFlag = false;
                an_sync.stop();
                scroll2Top();
                dismissLoadingDialog();
                if (isSuccess) {
                    // 数据显示后进行处理
                    DeviceSyncConfig.setLastSyncTime(System.currentTimeMillis());
                    showToast(getString(R.string.sync_data_ok));
                } else {
                    if (isOpenBluetooth()) {
                        showToast(getString(R.string.sync_data_fail));
                    } else {
                        showToastFlag = false;
                        onDeviceDisconnected();
                        showToast(getString(R.string.turn_on_bluetooth_tips));
                    }
                }

            }
        });
    }

    //刷新首页显示的数据
    private void doSyncRefreshViews(final DeviceStatusInfo deviceStatusInfo) {
        LogUtil.i(TAG, "刷新页面时显示的数据 = " + deviceStatusInfo.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDevicePower(GlobalVarManager.getInstance().getBatteryPower());
                if (deviceStatusInfo.getSportSleepMode() == 0) {
                    //运动模式
                    icSleepIcon.setImageResource(R.mipmap.ic_sleep_off);
                    cbSleep.setChecked(false);
                } else {
                    icSleepIcon.setImageResource(R.mipmap.ic_sleep);
                    cbSleep.setChecked(true);
                }
                showSportInfo(deviceStatusInfo);        //显示运动数据
                showUVValue(deviceStatusInfo);
                mainStatus.setVisibility(View.VISIBLE);
                if (AppUtil.isShowHeartRate()) {
                    mainStatus.setVisibility(View.VISIBLE);
                    HeartDataDB sleepDataDB = DataSupport.where("userId=?", AccountConfig.getUserId() + "").findLast(HeartDataDB.class);
                    MainStatusShowUtil.showHeartRateResult(mainStatus, sleepDataDB == null ? 0 : sleepDataDB.getHeartAvg());
                    MainStatusShowUtil.showMoodResult(mainStatus, deviceStatusInfo.getMood());
                    MainStatusShowUtil.showTiredResult(mainStatus, deviceStatusInfo.getTired());
                } else {
                    mainStatus.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showUVValue(DeviceStatusInfo deviceStatusInfo) {
        int uvValue = deviceStatusInfo.getUvValue();
        uvChart.setUvValue(uvValue);
        if (1 <= uvValue && uvValue <= 2) {
            uvDesc.setText(getResources().getString(R.string.uv_ray_lel1));

        } else if (3 <= uvValue && uvValue <= 5) {
            uvDesc.setText(getResources().getString(R.string.uv_ray_lel2));

        } else if (6 <= uvValue && uvValue <= 7) {
            uvDesc.setText(getResources().getString(R.string.uv_ray_lel3));

        } else if (8 <= uvValue && uvValue <= 10) {
            uvDesc.setText(getResources().getString(R.string.uv_ray_lel4));

        } else if (11 <= uvValue) {
            uvDesc.setText(getResources().getString(R.string.uv_ray_lel5));
        }
    }

    private int[] searchData() {
        long currentTimeMillis = System.currentTimeMillis();
        String[] startAndEndTime = TimeZoneUtils.getStartAndEndTime(currentTimeMillis);
        int[] result = new int[7];//0:步数,1卡路里,2，睡眠,3距离,4运动
        //运动统计
        List<SportData> sportDatas = DataSupport.where("startTime>? and endTime<? and userId=?", startAndEndTime[0], startAndEndTime[1], AccountConfig.getUserId() + "").find(SportData.class);
        for (SportData d : sportDatas) {
            LogUtil.i(TAG, "详细数据" + d.toString());
        }
        for (int i = 0; i < sportDatas.size(); i++) {
            result[0] += sportDatas.get(i).getSportStep();
            result[1] += sportDatas.get(i).getSportCalorie();
            result[3] += sportDatas.get(i).getSportDistance();
            result[4] += sportDatas.get(i).getSportDuration();
        }
        LogUtil.i(TAG, "步数" + result[0] + ",卡路里" + result[1] + "距离" + result[3]);
        result[2] = SleepDataUtils.getSleepData(startAndEndTime)[0];
        return result;
    }

    //显示数据
    private void showSportInfo(DeviceStatusInfo deviceStatusInfo) {
        TitleProgressView tp_stpes = (TitleProgressView) mainDeviceStatus.findViewById(R.id.tp_stpes);
        TitleProgressView tp_calories = (TitleProgressView) mainDeviceStatus.findViewById(R.id.tp_calories);
        TitleProgressView tp_sleep = (TitleProgressView) mainDeviceStatus.findViewById(R.id.tp_sleep);
        TitleProgressView tp_distance = (TitleProgressView) mainDeviceStatus.findViewById(R.id.tp_distance);
        TitleProgressView tp_sport = (TitleProgressView) mainDeviceStatus.findViewById(R.id.tp_sport);
        int[] searchData = searchData();
        setSportInfo(tp_stpes, searchData);
        setSportInfo(tp_calories, searchData);
        setSportInfo(tp_sleep, searchData);
        setSportInfo(tp_distance, searchData);
        setSportInfo(tp_sport, searchData);
    }

    private void setSportInfo(final TitleProgressView tp, int[] searchData) {
        LocalUserGoal userGoal = GoalConfig.getLocalUserGoal();
        int index = 0, goalValue = 0, currValue = 0;
        switch (tp.getId()) {
            case R.id.tp_stpes:
                index = HomeListBean.Step;
                goalValue = userGoal.getGoals_step();
                currValue = searchData[0];
                if (goalValue == 0) {
                    goalValue = 7000;
                }
                break;
            case R.id.tp_calories:
                index = HomeListBean.Calories;
                goalValue = userGoal.getGoals_calories();
                currValue = searchData[1];
                if (goalValue == 0) {
                    goalValue = 350;
                }
                break;
            case R.id.tp_sleep:
                index = HomeListBean.Sleep;
                goalValue = userGoal.getGoals_sleep();
                currValue = searchData[2];
                if (goalValue == 0) {
                    goalValue = 8;
                }
                break;
            case R.id.tp_distance:
                index = HomeListBean.Distance;
                goalValue = userGoal.getGoals_distance();
                currValue = searchData[3];
                if (goalValue == 0) {
                    goalValue = 5;
                }
                break;
            case R.id.tp_sport:
                index = HomeListBean.Active;
                goalValue = userGoal.getGoals_activeMinutes();
                currValue = searchData[4];
                break;
        }
        final String name = MainStatusShowUtil.getName(index);
        final String goal = MainStatusShowUtil.getGoal(index, goalValue);
        final String curr = MainStatusShowUtil.getCurr(index, currValue);
        final String descript = MainStatusShowUtil.getDescript(index);
        final int icon = MainStatusShowUtil.getIcon(index, goalValue, currValue);
        final int progressId = MainStatusShowUtil.getProgressId(index);
        final int maxValue = MainStatusShowUtil.getMaxValue(index, goalValue);
//        boolean color = MainStatusShowUtil.getColor(index, goalValue, currValue);
        final int finalCurrValue = currValue;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tp.reFreshData(name, goal, curr, descript, icon, progressId, finalCurrValue, maxValue);
            }
        });
    }


    private void syncNow() {
        if (isOpenBluetooth()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    svMain.onRefreshComplete();
                    if (!checkBindDevice(false)) {      //未绑定
                        an_sync.stop();
                        dismissLoadingDialog();
                        showToast(getString(R.string.no_bind_device));
                    } else {
                        if (isDeviceConnected()) {      //连接
                            isNowSyncFlag = true;//开始同步数据
                            Query5YearData.startService();
                            showBigLoadingProgress(getString(R.string.syncting));
                            an_sync.start();
                            //广播进行同步数据
                            SyncDataService.sendUploadBroadcast(SyncDataService.ACTION_DEVICE_SYNC_NOW);
                        } else {                        //未连接
                            AppUtil.checkBluetooth(MainActivity.this);
                            // 按同步，立刻先建立连接，然后在同步.
                            dismissLoadingDialog();
                            reconnectDevice();
                            an_sync.stop();
//                            showToast(getString(R.string.connect_first));
                        }
                    }
                }
            });
        } else {
            onDeviceDisconnected();
            showToast(getString(R.string.turn_on_bluetooth_tips));
        }
    }

    private boolean isOpenBluetooth() {
        boolean isOpen = false;
        //判断是否支持BLE蓝牙
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            //判断安卓版本是否符合要求
            BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                BluetoothAdapter adapter = bluetoothManager.getAdapter();
                //是否打开了蓝牙
                if (adapter != null) {
                    isOpen = adapter.isEnabled();
                }
            }
        }
        return isOpen;
    }


    @OnClick({R.id.iv_menu, R.id.iv_sync, R.id.rl_connectLayout, R.id.cb_sleep,
            R.id.lineRankViewFriends, R.id.tv_add_friend,
            R.id.tp_stpes, R.id.tp_calories, R.id.tp_sleep, R.id.tp_distance, R.id.tp_sport,
            R.id.ll_hear_beat, R.id.ll_mood, R.id.ll_tired})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_menu:
                scroll2Top();
                break;
            case R.id.iv_sync:
                if (!isNowSyncFlag && checkBindDevice(true)) {
                    syncNow();
                }
                break;
            case R.id.rl_connectLayout:
                if (checkBindDevice(true)) {
                    // 重新建立蓝牙连接
                    reconnectDevice();
                }
                break;
            case R.id.cb_sleep:
                if (!checkBindDevice(true)) {
                    return;
                }
                sleepModelFlag = cbSleep.isChecked();
//                showTipDialogCancleAndPositive(getString(R.string.sync_to_device), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
                if (isOpenBluetooth()) {
                    dismissDialog();
//                        if (!AppUtil.checkBluetooth(MainActivity.this)) {
//                            cbSleep.setChecked(!sleepModelFlag);
//                            return;
//                        }
                    if (sleepModelFlag) {
                        synSleepMode(1);
                    } else {
                        synSleepMode(0);
                    }
                } else {
                    showToastFlag = false;
                    onDeviceDisconnected();
                    cbSleep.setChecked(!sleepModelFlag);
                    showToast(getString(R.string.turn_on_bluetooth_tips));
                }
                break;
            case R.id.lineRankViewFriends:
                break;
            case R.id.tv_add_friend:
                break;
        }
        showDataChart(view);
    }

    public void synSleepMode(int sleepMode) {
        showBigLoadingProgress(getString(R.string.syncting));
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).clearCommandBlockingDeque();
        SwitchSetting switchSetting = new SwitchSetting(new BaseCommand.CommandResultCallback() {
            @Override
            public void onSuccess(BaseCommand baseCommand) {
                //切换睡眠模式
                dismissLoadingDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(getString(R.string.successful));
                        if (sleepModelFlag) {
                            GlobalVarManager.getInstance().setSportSleepMode(1);
                            icSleepIcon.setImageResource(R.mipmap.ic_sleep);
                        } else {
                            GlobalVarManager.getInstance().setSportSleepMode(0);
                            icSleepIcon.setImageResource(R.mipmap.ic_sleep_off);
                        }
                    }
                });
            }

            @Override
            public void onFail(BaseCommand baseCommand) {
                dismissLoadingDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(getString(R.string.failed));
                        cbSleep.setChecked(!sleepModelFlag);
                    }
                });
            }
        }, 3, (byte) 1, (byte) 2, (byte) sleepMode);
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommand(switchSetting);
    }

    private void queryLeaderBoard() {
        if (queryLeaderBoardCount >= 1) {
            return;
        }
        FriendsAccount account = AccountConfig.getFriendsAccountInfo();
        if (account != null && account.getDdId() != -1) {
            LeaderBoardLoader.getInstance().queryLeaderBoard(account.getDdId(), leaderBoardLoaderListener);
        } else {
            queryLeaderBoardCount++;
            LeaderBoardLoader.getInstance().leaderBoardQueryJoin(leaderBoardLoaderListener);
        }
    }

    private LeaderBoardLoader.LeaderBoardLoaderListener leaderBoardLoaderListener = new LeaderBoardLoader.LeaderBoardLoaderListener() {
        @Override
        public void onResult(List<LeaderBoard> resultList) {
            queryLeaderBoardCount = 0;
            showRankView(resultList);
        }

        @Override
        public void onFriendsAccountResult(List<FriendsAccount> friendsAccountList) {
            queryLeaderBoardCount = 0;
            FriendsAccount friendsAccount = friendsAccountList.get(0);
            AccountConfig.setFriendsAccountInfo(friendsAccount);
            queryLeaderBoard();
        }
    };

    private List<LeaderBoard> getUserLeaderBoard(List<LeaderBoard> resultList, int total, int preNum, int nexNum) {
        ArrayList arr = new ArrayList();
        if (resultList == null || resultList.size() < 0) {
            return arr;
        }
        if (resultList.size() <= total) {
            return resultList;
        }
        int size = resultList.size();
        int index = 0, begin = 0, end = size - 1;
        LeaderBoard maxLeaderBoard = resultList.get(0);
        for (int i = 0; i < resultList.size(); i++) {
            UserInfoBean localUser = AccountConfig.getUserInfoBean();
            LeaderBoard leaderBoard = resultList.get(i);
            if (localUser != null && leaderBoard.getUserName() != null && leaderBoard.getUserName().equals(localUser.getUserName())) {
                index = i;
                break;
            }
        }
        if (index - preNum <= 0) {
            begin = 0;
        } else {
            begin = index - preNum;
        }
        if (index + nexNum >= size - 1) {
            end = size - 1;
        } else {
            end = index + nexNum;
        }
        if (begin == 0) {
            end = begin + nexNum;
        }
        if (end == (size - 1)) {
            begin = end - preNum;
        }
        for (int i = begin; i <= end; i++) {
            arr.add(resultList.get(i));
        }
        if (!arr.contains(maxLeaderBoard)) {
            arr.add(maxLeaderBoard);
        }
        return arr;
    }

    private void showRankView(List<LeaderBoard> resultList) {
        List<LeaderBoard> filterList = getUserLeaderBoard(resultList, 3, 1, 1);
        if (filterList != null && filterList.size() > 0) {
            List<LineRankData> lineRankDatas = new ArrayList<LineRankData>();
            for (int i = 0; i <= filterList.size() - 1; i++) {
                LineRankData lineRankData = new LineRankData();
                LeaderBoard leaderBoard = filterList.get(i);
                lineRankData.setTop(false);
                UserInfoBean localUser = AccountConfig.getUserInfoBean();
                if (localUser != null && leaderBoard.getUserName() != null && leaderBoard.getUserName().equals(localUser.getUserName())) {
                    lineRankData.setTop(true);
                }
                lineRankData.setValue((leaderBoard.getSportsStep()));
                UniversalImageLoaderHelper.loadImage(leaderBoard.getIconUrl(), new UniversalImageLoaderHelper.ImageLoaderListener() {
                    @Override
                    public void onLoadingComplete() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lineRankViewFriends.invalidate();
                            }
                        });
                    }
                });
                lineRankData.setIconUrl(leaderBoard.getIconUrl());
                lineRankDatas.add(lineRankData);
                if (i == 3) {
                    break;
                }
            }
            lineRankViewFriends.setMaxText(LeaderBoardLoader.getInstance().getMaxStepString() + "");
            lineRankViewFriends.setLineRankDatas(lineRankDatas);
        }
    }

    @Override
    public void onBackPressed() {
        showTipDialogCancleAndPositive(getString(R.string.exit_app), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                AppManager.getAppManager().AppExit(MainActivity.this);
            }
        });
    }

    /**
     * 图形跳转
     *
     * @param view
     */
    private void showDataChart(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.tp_stpes:
                break;
            case R.id.tp_calories:
                break;
            case R.id.tp_distance:
                break;
            case R.id.tp_sleep:
                break;
            case R.id.ll_hear_beat:
                break;
            case R.id.tp_sport:
                break;
            case R.id.ll_mood:
                break;
            case R.id.ll_tired:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        svMain.onRefreshComplete();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @Override
    public void sendHeartRate(final int value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainStatusShowUtil.showHeartRateResult(mainStatus, value);
            }
        });
    }

    private void TextListener() {
        AppsBluetoothManager.getInstance(this).setBluetoothManagerCommandListener(new BlueToothManagerCommandListener() {
            @Override
            public void isWhatBytes(byte[] commands) {
                String action = BaseUtil.bytesToHexString(commands);
                switch (action) {
                    case "6fea810200eb008f"://SOS 报警
                        Logger.d("", "text action == " + action);
                        break;
                    case "6fea810200ec008f"://低电报警
                        Logger.d("", "text action == " + action);
                        break;
                }
            }
        });
        AppsBluetoothManager.getInstance(this).setPressSecondListener(new PressSecondListener() {
            @Override
            public void shortpresseconds(byte[] result) {
                String action = BaseUtil.bytesToHexString(result);
                Logger.d("", "text 2s 短按 命令 == " + action);
            }

            @Override
            public void longpressseconds_2_15s(byte[] result) {
                String action = BaseUtil.bytesToHexString(result);
                Logger.d("", "text 2s-15s 长按 命令 == " + action);
            }

            @Override
            public void longpressseconds_20(byte[] result) {
                String action = BaseUtil.bytesToHexString(result);
                Logger.d("", "text 20s 长按 命令 == " + action);
            }
        });
    }

    private boolean on1 = true;
    private boolean on2 = true;
    private boolean on3 = true;

    @OnClick({R.id.uvv, R.id.sleep, R.id.sport, R.id.led, R.id.ota})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ota:
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.uvv:
                byte state = -1;
                if (on1) {
                    state = AvatertSwitch.SET_ON;
                    on1 = false;
                } else {
                    state = AvatertSwitch.SET_OFF;
                    on1 = true;
                }
                AvatertSwitch a1 = new AvatertSwitch(new BaseCommand.CommandResultCallback() {
                    @Override
                    public void onSuccess(BaseCommand command) {
                        Logger.d("", "text 设置紫外线成功" + BaseUtil.bytesToHexString(command.getContent()));
                    }

                    @Override
                    public void onFail(BaseCommand command) {
                        Logger.d("", "text 设置紫外线失败" + BaseUtil.bytesToHexString(command.getContent()));
                    }
                }, AvatertSwitch.UVV_SWITCH, state);
                AppsBluetoothManager.getInstance(this).sendCommand(a1);
                break;
            case R.id.sleep:
                byte state1 = -1;
                if (on2) {
                    state1 = AvatertSwitch.SET_ON;
                    on2 = false;
                } else {
                    state1 = AvatertSwitch.SET_OFF;
                    on2 = true;
                }
                AvatertSwitch a2 = new AvatertSwitch(new BaseCommand.CommandResultCallback() {
                    @Override
                    public void onSuccess(BaseCommand command) {
                        Logger.d("", "text 设置睡眠模式成功" + BaseUtil.bytesToHexString(command.getContent()));
                    }

                    @Override
                    public void onFail(BaseCommand command) {
                        Logger.d("", "text 设置睡眠模式失败" + BaseUtil.bytesToHexString(command.getContent()));
                    }
                }, AvatertSwitch.SLEEP_SWITCH, state1);
                AppsBluetoothManager.getInstance(this).sendCommand(a2);
                break;
            case R.id.sport:
                byte state2 = -1;
                if (on3) {
                    state2 = AvatertSwitch.SET_ON;
                    on3 = false;
                } else {
                    state2 = AvatertSwitch.SET_OFF;
                    on3 = true;
                }
                AvatertSwitch a3 = new AvatertSwitch(new BaseCommand.CommandResultCallback() {
                    @Override
                    public void onSuccess(BaseCommand command) {
                        Logger.d("", "text 设置运动模式成功" + BaseUtil.bytesToHexString(command.getContent()));
                    }

                    @Override
                    public void onFail(BaseCommand command) {
                        Logger.d("", "text 设置运动模式失败" + BaseUtil.bytesToHexString(command.getContent()));
                    }
                }, AvatertSwitch.SPORT_SWITCH, state2);
                AppsBluetoothManager.getInstance(this).sendCommand(a3);
                break;
            case R.id.led:
                /**
                 *   示例（控制表盘上的 7,8,9,10 点的灯亮，以及控制三色灯为绿色）
                 *   47 80
                 *   100011110000000
                 */
                LEDBean bean = new LEDBean();
                bean.setFirst_8_bytes((byte) 0x47);
                bean.setLast_8_bytes((byte) 0x80);
                byte[] content = bean.getContent();
                if (on4) {
                    on4 = false;
                } else {
                    on4 = true;
                    content = bean.getSetOffCommand();//获取关闭LED灯的命令
                }
                LEDControlSwitch led = new LEDControlSwitch(new BaseCommand.CommandResultCallback() {
                    @Override
                    public void onSuccess(BaseCommand command) {
                        Logger.d("", "text 设置LED模式成功" + BaseUtil.bytesToHexString(command.getContent()));
                    }

                    @Override
                    public void onFail(BaseCommand command) {
                        Logger.d("", "text 设置LED模式失败" + BaseUtil.bytesToHexString(command.getContent()));
                    }
                }, content);
                AppsBluetoothManager.getInstance(this).sendCommand(led);
                break;
        }
    }

    private boolean on4 = true;

}
