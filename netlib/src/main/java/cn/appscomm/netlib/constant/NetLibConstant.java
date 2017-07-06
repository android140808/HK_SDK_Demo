package cn.appscomm.netlib.constant;

/**
 * Created by Administrator on 2016/8/30.
 */
public class NetLibConstant {
    public static boolean isRelease = false;
    //language
    public static final int lang_chinese = 200;
    public static final int lang_english = 201;
    public static final int lang_french = 202;
    public static final int lang_german = 203;
    public static final int lang_italian = 204;
    public static final int lang_japanese = 205;
    public static final int lang_korean = 206;
    public static final int lang_spanish = 207;

    public static final int DEFAULT_LANGUAGE = lang_chinese;
    //    public static final String DEFAULT_PRODUCT_CODE = "WT10A";
    public static final String DEFAULT_PRODUCT_CODE = "wt10ahk_oem_her";
    public static final String DEFAULT_DEVICE_TYPE = "Young";
    public static final String DEFAULT_CLIENT_TYPE = "android";
    //    public static final String DEFAULT_CUSTOMER_CODE = "appscommodm";
    public static final String DEFAULT_CUSTOMER_CODE = "WT10A_HK";
    public static final String DEFAULT_CUSTOMER_CODE_HER_NEW = "appscommoem2";


    public static final String Domain = isRelease ? "http://new.fashioncomm.com/" : "http://testnew.appscomm.cn:666/";


    public static final String login = "account/login";            //User login
    public static final String accountEdit = "account/edit";    //Account Edit

    public static final String url_createDD = "leaderBoard/createDD";
    public static final String url_queryJoin = "leaderBoard/queryJoin";
    public static final String url_queryLeaderBoard = "leaderBoard/queryLeaderBoard";
    //上传运动数据
    public static final String uploadSportData = "sport/uploadSport";
    //上传睡眠数据
    public static final String uploadSleepRecord = "sleep/uploadSleep";
    //上传心率数据
    public static final String uploadHeartRecord = "heartrate/uploadHeartRate";
    //上传心情数据
    public static final String uploadMoodRecord = "moodFatigue/uploadMoodFatigue";
    //查询最后上传时间
    public static final String url_querySport = "sport/querySport";
    public static final String url_queryUploadMoodTime = "moodFatigue/queryUploadMoodTime";
    //获取统计运动数据
    public static final String getCountSport = "sport/countSport";
    //获取所有睡眠数据
    public static final String getSleepData = "sleep/querySleep";
    //获取统计睡眠数据
    public static final String getCountSleep = "sleep/countSleep";
    //获取所有心率数据
    //获取所有心情数据
    public static final String get_moodFatigue = "moodFatigue/queryMoodFatigue";


    //设备相关
    public static final String getFirmwareVersion = "device/queryProductVersion";
}
