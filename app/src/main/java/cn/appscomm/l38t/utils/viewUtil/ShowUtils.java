package cn.appscomm.l38t.utils.viewUtil;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.UI.showView.datachart.MoodView;
import cn.appscomm.l38t.UI.showView.datachart.TiredView;


/**
 * Created by weiliu on 2016/8/6.
 */
public class ShowUtils {

    public static String getHeartRateLevelString(int value, Context context) {
        String statusStr = "";
        if (value < 45) {
            statusStr = context.getResources().getString(R.string.heart_rate_very_slow);
        } else if (value < 60) {
            statusStr = context.getResources().getString(R.string.heart_rate_slow);
        } else if (value < 80) {
            statusStr = context.getResources().getString(R.string.heart_rate_normal);
        } else if (value < 100) {
            statusStr = context.getResources().getString(R.string.heart_rate_fast);
        } else {
            statusStr = context.getResources().getString(R.string.heart_rate_very_fast);
        }
        return statusStr;
    }

    public static String getHeartRateLevel2String(int value, Context context) {
        String statusStr = "";
        if (value < 76) {
            statusStr = context.getResources().getString(R.string.heart_rate_slow);
        } else if (value >= 76 && value <= 154) {
            statusStr = context.getResources().getString(R.string.heart_rate_normal);
        } else if (value > 154) {
            statusStr = context.getResources().getString(R.string.heart_rate_fast);
        } else {
            statusStr = context.getResources().getString(R.string.heart_rate_fast);
        }
        return statusStr;
    }

    public static int getHeartRateLevel(int value, Context context) {
        int status = 2;
        if (value <= 76) {
            status = 0;
        } else if (value < 60) {
            status = 1;
        } else if (value < 80) {
            status = 2;
        } else if (value < 100) {
            status = 3;
        } else {
            status = 4;
        }
        return status;
    }

    public static int getHeartRateLevel2(int value, Context context) {
        int status = 2;
        if (value < 50) {
            status = -1;//不显示
        } else if (value < 76) {
            status = 0;
        } else if (value < 102) {
            status = 1;
        } else if (value < 128) {
            status = 2;
        } else if (value < 154) {
            status = 3;
        } else {
            status = 4;
        }
        return status;
    }

    public static int getDevicePowerLevelDrawable2(int power) {
        int drawable = R.mipmap.ic_battery_1;
        if (power <= 20) {
            drawable = R.mipmap.ic_battery_1;
        } else if (power <= 40) {
            drawable = R.mipmap.ic_battery_2;
        } else if (power <= 60) {
            drawable = R.mipmap.ic_battery_3;
        } else if (power <= 80) {
            drawable = R.mipmap.ic_battery_4;
        } else if (power <= 99) {
            drawable = R.mipmap.ic_battery_5;
        } else if (power <= 100) {
            drawable = R.mipmap.ic_battery_6;
        }
        return drawable;
    }


    public static String getMoodLevelString(int value, Context context) {
        String statusStr = "";
        switch (value) {
            case MoodView.CALM:
                statusStr = context.getResources().getString(R.string.mood_calm);
                break;
            case MoodView.MODERATE:
                statusStr = context.getResources().getString(R.string.mood_moderate);
                break;
            case MoodView.DEPRESSED:
                statusStr = context.getResources().getString(R.string.mood_depressed);
                break;
            default:
                statusStr = context.getResources().getString(R.string.mood_calm);
                break;
        }
        return statusStr;
    }

    public static int getMoodIconType(int value) {
        int drawable = R.mipmap.mood_3;
        switch (value) {
            case MoodView.CALM:
                drawable = R.mipmap.mood_3;
                break;
            case MoodView.MODERATE:
                drawable = R.mipmap.mood_4;
                break;
            case MoodView.DEPRESSED:
                drawable = R.mipmap.mood_2;
                break;
        }
        return drawable;
    }


    public static String getTiredLevelString(int value, Context context) {
        String valueString = context.getResources().getString(R.string.tired_energetic);
        switch (value) {
            case TiredView.ENERGETIC:
                valueString = context.getResources().getString(R.string.tired_energetic);
                break;
            case TiredView.MODERATE:
                valueString = context.getResources().getString(R.string.tired_moderate);
                break;
            case TiredView.SLIGHT:
                valueString = context.getResources().getString(R.string.tired_slight);
                break;
            case TiredView.SERIOUS:
                valueString = context.getResources().getString(R.string.tired_serious);
                break;
        }
        return valueString;
    }

    public static int getTiredIconType(int value) {
        int drawable = R.mipmap.tired_4;
        switch (value) {
            case TiredView.ENERGETIC:
                drawable = R.mipmap.tired_4;
                break;
            case TiredView.MODERATE:
                drawable = R.mipmap.tired_3;
                break;
            case TiredView.SLIGHT:
                drawable = R.mipmap.tired_2;
                break;
            case TiredView.SERIOUS:
                drawable = R.mipmap.tired_1;
                break;
        }
        return drawable;
    }

    public static void setListViewLayoutHeight(ListView listView){
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount()-1));
        listView.setLayoutParams(params);
    }
}
