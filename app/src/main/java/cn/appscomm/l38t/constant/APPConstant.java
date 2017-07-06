package cn.appscomm.l38t.constant;

import android.os.Environment;

import java.io.File;
import java.util.Map;

import cn.appscomm.l38t.app.GlobalApp;
import no.nordicsemi.android.ota.model.FirmwareServerInfo;

/**
 * Created by weiliu on 2016/7/20.
 */
public class APPConstant {

    public static final String deviceType02A = "LF02A";
    public static final String deviceType01A = "LF01A";
    public static final String deviceType38T = "L38T";
    public static final String deviceTypeWT10A = "WT10A";
    public static final String deviceTypeWT10C = "WT10C";
    public static final String deviceTypeYOUNG = "L42A_ODM_Young";
    public static final String deviceTypeWT10_xuelei = "WT10";

    public static final String APP_DIR = deviceType38T + File.separator;
    public static final String SDCardDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;// SD
    public static final File FILE_FIRMWARE = GlobalApp.getAppContext().getFilesDir();
    public static final File FILE_APPLICATION_ZIP = new File(GlobalApp.getAppContext().getFilesDir(), "upgrade.zip");

    public static final String APP_EMAIL = "inhealth.service@inwatch.cn";
    public static final String APP_WEBSIZE = "http://www.appscomm.cn/";

    public static final String Device_OTA_Default = "DfuTarg";//有可能多设备在附近导致问题
    public static final String Device_38T_OTA_Default = "38T#";//有可能多设备在附近导致问题
    public static final String Device_38T_OTA = "38T#" + "%s";
    public static final String Device_LF02A_OTA = "F02A#OTA";
    public static final String Device_LF01A_OTA = "F01A#OTA";

    public static final String QUERY_TIME = "query_time";
    public static final String QUERY_USER = "query_user";
    public static final String QUERY_SETTING = "is_query";
    public static final String QUERY_TIME_BASE = "query_time_base";
    public static final String QUERY_DEVICE_ID = "query_device_id";

    public static final String HARDWARE_VERSION = "hardware_version";

    public static final boolean IS_RELEASE_VERSION = false;                                         // 正式版/测试版
    public static final String APP_VERSION = "2.0.5";                                               // APK版本

    /**
     * OTA
     */
    public static FirmwareServerInfo firmwareServerInfos[];                                         // 服务器固件信息
    public static String nordic_server_filename;                                                    // Nordic固件名称
    public static String nordic_server_version;                                                     // Nordic版本

    public static String firmware_server_url;                                                       // 固件url
    public static int updateModel;                                                                  // 升级模式 1011  高位到低位分别代表:Nordic/图片/Freescale/触摸；1-升级，0-不升级
    public static Map<String ,FirmwareServerInfo> updateInfo;                                       // 升级信息
    public static final boolean isTest = false;
}
