package cn.appscomm.l38t.config;

import android.text.TextUtils;

import cn.appscomm.l38t.config.bean.LocalUserGoal;
import cn.appscomm.netlib.config.BaseLocalConfig;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/5 08:57
 */
public class GoalConfig extends BaseLocalConfig {

    private static final String local_user_goal_bean = "local_user_goal_bean";

    public static LocalUserGoal getLocalUserGoal() {
        String result = getInstance().getString(local_user_goal_bean, "");
        LocalUserGoal info = null;
        if (!TextUtils.isEmpty(result)) {
            info = getInstance().getGson().fromJson(result, LocalUserGoal.class);
        }
        if (info==null){
            info = new LocalUserGoal();
            info.setGoals_step(LocalUserGoal.DEFAULT_STEP);
            info.setGoals_sleep(LocalUserGoal.DEFAULT_SLEEP);
            info.setGoals_calories(LocalUserGoal.DEFAULT_CALORIES);
            info.setGoals_distance(LocalUserGoal.DEFAULT_DISTANCE);
            info.setGoals_activeMinutes(LocalUserGoal.DEFAULT_ACTIVE);
        }
        return info;
    }

    public static void setLocalUserGoal(LocalUserGoal userGoal) {
        if (userGoal == null) {
            getInstance().saveString(local_user_goal_bean, "");
            return;
        }
        String result = getInstance().getGson().toJson(userGoal);
        if (TextUtils.isEmpty(result)) {
            getInstance().saveString(local_user_goal_bean, "");
        } else {
            getInstance().saveString(local_user_goal_bean, result);
        }
    }
}
