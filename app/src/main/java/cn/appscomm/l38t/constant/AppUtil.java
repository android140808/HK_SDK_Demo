package cn.appscomm.l38t.constant;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.activity.base.BaseActivity;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.model.database.UserBindDevice;
import cn.appscomm.l38t.utils.ChooseDevices;
import cn.appscomm.netlib.constant.NetLibConstant;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/3 11:50
 */
public class AppUtil {

    /**
     * 判断用户是否绑定设备
     *
     * @return
     */
    public final static boolean haveBindDevice() {
        return UserBindDevice.haveBindDevice(AccountConfig.getUserId());
    }

    public final static String getShowName() {
//      String name = GlobalApp.getAppContext().getString(R.string.device_name_unbind);
        UserBindDevice userBindDevice = UserBindDevice.getBindDevice(AccountConfig.getUserId());
        String name = "";
        if (userBindDevice != null) {
            if (userBindDevice.getDeviceName().startsWith(UserBindDevice.WT10C)) {
                name = ChooseDevices.FUSION;
            } else if (userBindDevice.getDeviceName().startsWith(UserBindDevice.WT10A)) {
                name = ChooseDevices.HER;
            } else if (userBindDevice.getDeviceName().startsWith(UserBindDevice.YOUNG)) {
                name = ChooseDevices.YOUNG;
            }
        }
        return name;
    }

    public static String getShowNameWithWatchId(String fromName) {
        String name = "";
        if (fromName.startsWith(UserBindDevice.WT10C)) {
            name = fromName.replace(UserBindDevice.WT10C, ChooseDevices.FUSION);
        } else if (fromName.startsWith(UserBindDevice.WT10A)) {
            name = fromName.replace(UserBindDevice.WT10A, ChooseDevices.HER);
        } else if (fromName.startsWith(UserBindDevice.YOUNG)) {
            name = fromName.replace(UserBindDevice.YOUNG, ChooseDevices.YOUNG);
        }
        return name;
    }


    /**
     * 判断扫描到的绑定
     *
     * @return
     */
    public final static boolean isCanScanDevice(String scanName) {
        if (TextUtils.isEmpty(scanName)) {
            return false;
        }
        if (AppUtil.haveBindDevice()) {
            UserBindDevice userBindDevice = UserBindDevice.getBindDevice(AccountConfig.getUserId());
            if (userBindDevice != null) {
                if (userBindDevice.getDeviceName().startsWith(UserBindDevice.WT10C)) {
                    ChooseDevices.CHOOSEDEVICES = ChooseDevices.FUSION;
                } else if (userBindDevice.getDeviceName().startsWith(UserBindDevice.WT10A)) {
                    ChooseDevices.CHOOSEDEVICES = ChooseDevices.HER;
                } else if (userBindDevice.getDeviceName().startsWith(UserBindDevice.YOUNG)) {
                    ChooseDevices.CHOOSEDEVICES = ChooseDevices.YOUNG;
                } else if (userBindDevice.getDeviceName().startsWith(UserBindDevice.COLOR)) {
                    ChooseDevices.CHOOSEDEVICES = ChooseDevices.COLOR;
                }
            }
        }
        if ((ChooseDevices.CHOOSEDEVICES.equals(ChooseDevices.FUSION) && scanName.startsWith(UserBindDevice.WT10C))
                || (ChooseDevices.CHOOSEDEVICES.equals(ChooseDevices.HER) && scanName.startsWith(UserBindDevice.WT10A))
                || (ChooseDevices.CHOOSEDEVICES.equals(ChooseDevices.YOUNG) && scanName.startsWith(UserBindDevice.YOUNG))
                || (ChooseDevices.CHOOSEDEVICES.equals(ChooseDevices.COLOR) && scanName.startsWith(UserBindDevice.COLOR))) {
            return true;
        }
        return false;
    }

    /**
     * 判断扫描到的绑定
     *
     * @return
     */
    public final static String getDeviceType(String deviceName) {
        if (TextUtils.isEmpty(deviceName)) {
            return NetLibConstant.DEFAULT_PRODUCT_CODE;
        }
        if (deviceName.startsWith(UserBindDevice.WT10A)) {
            return APPConstant.deviceTypeWT10A;
        } else if (deviceName.startsWith(UserBindDevice.WT10C)) {
            return APPConstant.deviceTypeWT10C;
        } else if (deviceName.startsWith(UserBindDevice.YOUNG)) {
            return APPConstant.deviceTypeYOUNG;
        }
        return NetLibConstant.DEFAULT_PRODUCT_CODE;
    }

    /**
     * 判断扫描到的绑定
     *
     * @return
     */
    public final static String getDeviceOTA_Name(String deviceName) {
        if (TextUtils.isEmpty(deviceName)) {
            return APPConstant.Device_OTA_Default;
        }
        deviceName = deviceName.replace("#", "");
        if (deviceName.contains(APPConstant.deviceTypeWT10A)) {
            deviceName = deviceName.replace(APPConstant.deviceTypeWT10A, "10#");
        } else if (deviceName.contains(APPConstant.deviceTypeWT10C)) {
            deviceName = deviceName.replace(APPConstant.deviceTypeWT10C, "10#");
        } else if (deviceName.contains(UserBindDevice.YOUNG)) {
            deviceName = deviceName.replace(UserBindDevice.YOUNG + " ", "42#");
        }
        return deviceName;
    }

    /**
     * 判断设置页是否显示时间格式设置
     *
     * @return
     */
    public final static boolean isShowTimeFormat() {
        UserBindDevice userBindDevice = UserBindDevice.getBindDevice(AccountConfig.getUserId());
        if (userBindDevice == null) {
            return false;
        }
        if (userBindDevice.getDeviceName().startsWith(UserBindDevice.L38T)) {
            return true;
        }
        return false;
    }

    /**
     * 判断设备是否具有屏幕
     *
     * @return
     */
    public final static boolean isDeviceHaveScreen() {
        UserBindDevice userBindDevice = UserBindDevice.getBindDevice(AccountConfig.getUserId());
        if (userBindDevice == null) {
            return false;
        }
        if (userBindDevice.getDeviceName().startsWith(UserBindDevice.L38T)) {
            return true;
        }
        return false;
    }


    /**
     * 判断是否显示心率相关
     *
     * @return
     */
    public final static boolean isShowHeartRate() {
        UserBindDevice userBindDevice = UserBindDevice.getBindDevice(AccountConfig.getUserId());
        if (userBindDevice == null) {
            return false;
        }
        if (userBindDevice.getDeviceName().startsWith(UserBindDevice.YOUNG)) {
            return true;
        }
        return false;
    }


    /**
     * 判断是否显示恢复出厂设置
     *
     * @return
     */
    public final static boolean isShowRestoreFactory() {
        UserBindDevice userBindDevice = UserBindDevice.getBindDevice(AccountConfig.getUserId());
        if (userBindDevice == null) {
            return false;
        }
        if (userBindDevice.getDeviceName().startsWith(UserBindDevice.L38T)) {
            return true;
        }
        return false;
    }


    /**
     * 检查蓝牙并进行提示
     *
     * @param activity 必须传入BaseActivity
     * @return
     */
    public static boolean checkBluetooth(BaseActivity activity) {
        if (!AppUtil.haveBindDevice()) {
            activity.showToast(activity.getString(R.string.no_bind_device));
            return false;
        }
        BluetoothManager mBluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = null;
        if (mBluetoothManager != null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }
        if (mBluetoothAdapter == null) {
            activity.showToast(activity.getString(R.string.error_bluetooth_not_supported));
            return false;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            activity.showToast(activity.getString(R.string.turn_on_bluetooth_tips));
            return false;
        }
        return true;
    }


    public final static void sendEmail(Context context, int chooseType) {
        // 必须明确使用mailto前缀来修饰邮件地址,如果使用
        // intent.putExtra(Intent.EXTRA_EMAIL, email)，结果将匹配不到任何应用
        Uri uri = Uri.parse("mailto:" + APPConstant.APP_EMAIL);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        String baseString = context.getString(R.string.send_email_title);
        switch (chooseType) {
            case 0:
                baseString = baseString.replace(context.getString(R.string.send_email_replace), context.getString(R.string.feedback_device));
                break;
            case 1:
                baseString = baseString.replace(context.getString(R.string.send_email_replace), context.getString(R.string.feedback_app));
                break;
            case 2:
                baseString = baseString.replace(context.getString(R.string.send_email_replace), context.getString(R.string.feedback_pair));
                break;
            case 3:
                baseString = baseString.replace(context.getString(R.string.send_email_replace), context.getString(R.string.feedback_other));
                break;
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, baseString); // 主题
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.send_email_content)); // 正文
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.send_email_app_choose)));
    }

}
