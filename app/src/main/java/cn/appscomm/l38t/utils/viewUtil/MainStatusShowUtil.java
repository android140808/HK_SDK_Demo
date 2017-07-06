package cn.appscomm.l38t.utils.viewUtil;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appscomm.bluetooth.protocol.command.device.Unit;

import java.math.BigDecimal;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AppConfig;
import cn.appscomm.l38t.model.bean.HomeListBean;
import cn.appscomm.l38t.utils.UnitHelper;
import cn.appscomm.l38t.utils.UnitTool;

/**
 * author ：weiliu
 * email ：weiliu@appscomm.cn
 * time : 2016/9/3 17:04
 */
public class MainStatusShowUtil {

    public static void showHeartRateResult(View rootView, int heartRate) {
        int value = heartRate;
        String desc = ShowUtils.getHeartRateLevel2String(value, GlobalApp.getAppContext());
        ((TextView) rootView.findViewById(R.id.tv_heartRateText)).setText(value + "\nBPM");
        ((TextView) rootView.findViewById(R.id.hear_desc)).setText(GlobalApp.getAppContext().getString(R.string.your_current_heart) + "  " + desc);
        LinearLayout ll_heartRate_level = (LinearLayout) rootView.findViewById(R.id.ll_heartRate_level);
        int heartRate_level = ShowUtils.getHeartRateLevel2(value, GlobalApp.getAppContext());
        for (int i = 0; i < ll_heartRate_level.getChildCount(); i++) {
            if (i <= heartRate_level) {
                if (i == 0) {
                    ll_heartRate_level.getChildAt(i).setBackgroundResource(R.mipmap.heart_rate_level_begin);
                } else if (i == ll_heartRate_level.getChildCount() - 1) {
                    ll_heartRate_level.getChildAt(i).setBackgroundResource(R.mipmap.heart_rate_level_end);
                } else {
                    ll_heartRate_level.getChildAt(i).setBackgroundResource(R.mipmap.heart_rate_level_center);
                }
            } else {
                if (i == 0) {
                    ll_heartRate_level.getChildAt(i).setBackgroundResource(R.mipmap.heart_rate_level_begin_gray);
                } else if (i == ll_heartRate_level.getChildCount() - 1) {
                    ll_heartRate_level.getChildAt(i).setBackgroundResource(R.mipmap.heart_rate_level_end_gray);
                } else {
                    ll_heartRate_level.getChildAt(i).setBackgroundResource(R.mipmap.heart_rate_level_center_gray);
                }
            }
        }
    }

    public static void showTiredResult(View rootView, int tiredValue) {
        int value = tiredValue;
        String desc = ShowUtils.getTiredLevelString(value, GlobalApp.getAppContext());
        ((TextView) rootView.findViewById(R.id.tired_desc)).setText(GlobalApp.getAppContext().getString(R.string.your_current_tired) + "  " + desc);
        ((ImageView) rootView.findViewById(R.id.ic_tired)).setImageResource(ShowUtils.getTiredIconType(value));
        LinearLayout ll_tired_level = (LinearLayout) rootView.findViewById(R.id.ll_tired_level);
        for (int i = 0; i < ll_tired_level.getChildCount(); i++) {
            if (i <= value) {
                if (i == 0) {
                    ll_tired_level.getChildAt(i).setBackgroundResource(R.mipmap.mood_level_begin);
                } else if (i == ll_tired_level.getChildCount() - 1) {
                    ll_tired_level.getChildAt(i).setBackgroundResource(R.mipmap.mood_level_end);
                } else {
                    ll_tired_level.getChildAt(i).setBackgroundResource(R.mipmap.mood_level_center);
                }
            } else {
                if (i == 0) {
                    ll_tired_level.getChildAt(i).setBackgroundResource(R.mipmap.mood_level_begin_gray);
                } else if (i == ll_tired_level.getChildCount() - 1) {
                    ll_tired_level.getChildAt(i).setBackgroundResource(R.mipmap.mood_level_end_gray);
                } else {
                    ll_tired_level.getChildAt(i).setBackgroundResource(R.mipmap.mood_level_center_gray);
                }
            }
        }
    }

    public static void showMoodResult(View rootView, int moodValue) {
        int value = moodValue;
        String desc = ShowUtils.getMoodLevelString(value, GlobalApp.getAppContext());
        ((TextView) rootView.findViewById(R.id.mood_desc)).setText(GlobalApp.getAppContext().getString(R.string.your_current_mood) + "  " + desc);
        ((ImageView) rootView.findViewById(R.id.ic_mood)).setImageResource(ShowUtils.getMoodIconType(value));
        LinearLayout ll_mood_level = (LinearLayout) rootView.findViewById(R.id.ll_mood_level);
        int count = ll_mood_level.getChildCount();
        for (int i = 0; i < count; i++) {
            if (i <= value) {
                if (i == 0) {
                    ll_mood_level.getChildAt(i).setBackgroundResource(R.mipmap.mood_level_begin);
                } else if (i == ll_mood_level.getChildCount() - 1) {
                    ll_mood_level.getChildAt(i).setBackgroundResource(R.mipmap.mood_level_end);
                } else {
                    ll_mood_level.getChildAt(i).setBackgroundResource(R.mipmap.mood_level_center);
                }
            } else {
                if (i == 0) {
                    ll_mood_level.getChildAt(i).setBackgroundResource(R.mipmap.mood_level_begin_gray);
                } else if (i == ll_mood_level.getChildCount() - 1) {
                    ll_mood_level.getChildAt(i).setBackgroundResource(R.mipmap.mood_level_end_gray);
                } else {
                    ll_mood_level.getChildAt(i).setBackgroundResource(R.mipmap.mood_level_center_gray);
                }
            }
        }
    }


    public static int getMaxValue(int index, int max) {
        int maxValue = 0;
        switch (index) {
            case HomeListBean.Step://单位为步，不用转换
                maxValue = max;
                break;
            case HomeListBean.Calories://返回为千卡，需要转成卡
                maxValue = max * 1000;
                break;
            case HomeListBean.Sleep://单位为小时，转成分钟
                maxValue = max * 60;
                break;
            case HomeListBean.Active://运动时长,分钟
                maxValue = max;
                break;
            case HomeListBean.Distance://距离,米转成千米，英制的先转成千米后转成米
                double resule = 0.0;
                int unit = AppConfig.getLocalUnit();
                if (unit == UnitHelper.UNIT_US) {//英制
                    resule = max * Unit.UNIT_EN_S_NUM * 1000;
                } else {
                    resule = max * 1000;
                }
                maxValue = (int) resule;
                break;
        }
        return maxValue;
    }


    public static String getName(int index) {
        String name = "";
        switch (index) {
            case HomeListBean.Step:
                name = GlobalApp.getAppContext().getString(R.string.steep_name);
                break;
            case HomeListBean.Calories:
                name = GlobalApp.getAppContext().getString(R.string.caloris_name);
                break;
            case HomeListBean.Sleep:
                name = GlobalApp.getAppContext().getString(R.string.sleep_name);
                break;
            case HomeListBean.Distance:
                name = GlobalApp.getAppContext().getString(R.string.distance_name);
                break;
            case HomeListBean.Active:
                name = GlobalApp.getAppContext().getString(R.string.sport_name);
                break;
        }
        return name;
    }

    public static String getGoal(int index, int max) {
        String goal = "";
        switch (index) {
            case HomeListBean.Step:
                goal = GlobalApp.getAppContext().getString(R.string.goal) + ": " + max + " " + GlobalApp.getAppContext().getString(R.string.unit_steps);
                break;
            case HomeListBean.Calories:
                goal = GlobalApp.getAppContext().getString(R.string.goal) + ": " + max + " " + GlobalApp.getAppContext().getString(R.string.unit_calories);
                break;
            case HomeListBean.Sleep:
                goal = GlobalApp.getAppContext().getString(R.string.goal) + ": " + max + " " + GlobalApp.getAppContext().getString(R.string.unit_sleep);
                break;
            case HomeListBean.Distance:
                int unit = AppConfig.getLocalUnit();
                if (unit == UnitHelper.UNIT_US) {//英制
                    goal = GlobalApp.getAppContext().getString(R.string.goal) + ": " + new BigDecimal(max).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() + " " + GlobalApp.getAppContext().getString(R.string.unit_distance_miles);
                } else {
                    goal = GlobalApp.getAppContext().getString(R.string.goal) + ": " + new BigDecimal(max).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() + " " + GlobalApp.getAppContext().getString(R.string.km);
                }
                break;
            case HomeListBean.Active:
                goal = GlobalApp.getAppContext().getString(R.string.goal) + ": " + max + " " + GlobalApp.getAppContext().getString(R.string.unit_activetime);
                break;
        }
        return goal;
    }

    public static String getCurr(int index, int current) {
        String currentString = current + "";
        switch (index) {
            case HomeListBean.Step://单位为步，不用转换
                break;
            case HomeListBean.Calories://返回为卡，需要转成千卡
                currentString = UnitTool.getKaToKKaShowString(current);
                break;
            case HomeListBean.Sleep://睡眠时间，分钟转成小时
                currentString = UnitTool.getMinToHourShowString(current);
                break;
            case HomeListBean.Active://运动时长,分钟
                break;
            case HomeListBean.Distance://距离
                double resule = 0.0;
                int unit = AppConfig.getLocalUnit();
                if (unit == UnitHelper.UNIT_US) {//英制
                    resule = current * Unit.UNIT_S_EN_NUM;
                } else {
                    resule = current;
                }
                currentString = UnitTool.getMiToKmShowString(resule);
                break;
        }
        return currentString;
    }

    public static String getDescript(int index) {
        String descrip = "";
        switch (index) {
            case HomeListBean.Step:
                descrip = GlobalApp.getAppContext().getString(R.string.steep_descr);
                break;
            case HomeListBean.Calories:
                descrip = GlobalApp.getAppContext().getString(R.string.caloris_descr);
                break;
            case HomeListBean.Sleep:
                descrip = GlobalApp.getAppContext().getString(R.string.sleep_descr);
                break;
            case HomeListBean.Distance:
                int unit = AppConfig.getLocalUnit();
                if (unit == UnitHelper.UNIT_US) {//英制
                    descrip = GlobalApp.getAppContext().getString(R.string.distance_descr_miles);
                } else {
                    descrip = GlobalApp.getAppContext().getString(R.string.km);
                }
                break;
            case HomeListBean.Active:
                descrip = GlobalApp.getAppContext().getString(R.string.sport_descr);
                break;
        }
        return descrip;
    }

    public static int getIcon(int index, int max, int progress) {
        int iconId = R.mipmap.ic_goal_steps;
        boolean isall = false;
        switch (index) {
            case HomeListBean.Step:
                if (progress >= max) {
                    isall = true;
                }
                if (isall) {
                    iconId = R.mipmap.ic_goal_step_w;
                } else {
                    iconId = R.mipmap.ic_goal_steps;
                }
                break;
            case HomeListBean.Calories:
                if (progress >= max * 1000) {
                    isall = true;
                }
                if (isall) {
                    iconId = R.mipmap.ic_goal_calories_w;
                } else {
                    iconId = R.mipmap.ic_goal_calories;
                }
                break;
            case HomeListBean.Sleep:
                if (progress >= max * 60) {
                    isall = true;
                }
                if (isall) {
                    iconId = R.mipmap.ic_goal_sleep_w;
                } else {
                    iconId = R.mipmap.ic_goal_sleep;
                }
                break;
            case HomeListBean.Distance:
                if (progress >= max * 1000) {
                    isall = true;
                }
                if (isall) {
                    iconId = R.mipmap.ic_goal_distance_w;
                } else {
                    iconId = R.mipmap.ic_goal_distance;
                }
                break;
            case HomeListBean.Active:
                if (progress >= max) {
                    isall = true;
                }
                if (isall) {
                    iconId = R.mipmap.ic_goal_sport_w;
                } else {
                    iconId = R.mipmap.ic_goal_sport;
                }
                break;
        }
        return iconId;
    }

    public static int getProgressId(int index) {
        int progressResourceId = R.drawable.progress_steps;
        ;
        switch (index) {
            case HomeListBean.Step:
                progressResourceId = R.drawable.progress_steps;
                break;
            case HomeListBean.Calories:
                progressResourceId = R.drawable.progress_calories;
                break;
            case HomeListBean.Sleep:
                progressResourceId = R.drawable.progress_sleep;
                break;
            case HomeListBean.Distance:
                progressResourceId = R.drawable.progress_distance;
                break;
            case HomeListBean.Active:
                progressResourceId = R.drawable.progress_sport;
                break;
        }
        return progressResourceId;
    }

}
