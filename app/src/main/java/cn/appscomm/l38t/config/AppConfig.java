package cn.appscomm.l38t.config;

import cn.appscomm.l38t.utils.UnitHelper;
import cn.appscomm.netlib.config.BaseLocalConfig;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/5 09:04
 */
public class AppConfig extends BaseLocalConfig {

    private static final String key_local_unit = "key_local_unit";


    public static int getLocalUnit() {
        return getInstance().getInt(key_local_unit, UnitHelper.UNIT_METRIC);//默认公制
    }

    public static void setLocalUnit(int unit) {
        getInstance().saveInt(key_local_unit, unit);
    }
}
