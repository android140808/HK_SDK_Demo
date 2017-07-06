package cn.appscomm.l38t.model.database;

import android.content.ContentValues;

import com.appscomm.bluetooth.bean.RemindData;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/20.
 */
public class RemindDataModel extends DataSupport {

    public final static int MAX_REMIND_NUM = 6;//最多提醒数量
    public int remind_id;
    public int remind_type;
    public String remind_text;
    public int remind_time_hours;
    public int remind_time_minutes;
    public String remind_week;
    public int remind_set_ok;

    public int getRemind_id() {
        return remind_id;
    }

    public void setRemind_id(int remind_id) {
        this.remind_id = remind_id;
    }

    public int getRemind_type() {
        return remind_type;
    }

    public void setRemind_type(int remind_type) {
        this.remind_type = remind_type;
    }

    public String getRemind_text() {
        return remind_text;
    }

    public void setRemind_text(String remind_text) {
        this.remind_text = remind_text;
    }

    public int getRemind_time_hours() {
        return remind_time_hours;
    }

    public void setRemind_time_hours(int remind_time_hours) {
        this.remind_time_hours = remind_time_hours;
    }

    public int getRemind_time_minutes() {
        return remind_time_minutes;
    }

    public void setRemind_time_minutes(int remind_time_minutes) {
        this.remind_time_minutes = remind_time_minutes;
    }

    public String getRemind_week() {
        return remind_week;
    }

    public void setRemind_week(String remind_week) {
        this.remind_week = remind_week;
    }

    public int getRemind_set_ok() {
        return remind_set_ok;
    }

    public void setRemind_set_ok(int remind_set_ok) {
        this.remind_set_ok = remind_set_ok;
    }

    public final static List<RemindData> findAllRemindDatas() {
        List<RemindDataModel> list = findAll(RemindDataModel.class);
        List<RemindData> remindDataslist = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                RemindData remindData = new RemindData();
                RemindDataModel model = list.get(i);
                remindData.remind_id = model.getRemind_id();
                remindData.remind_type = model.getRemind_type();
                remindData.remind_text = model.getRemind_text();
                remindData.remind_time_hours = model.getRemind_time_hours();
                remindData.remind_time_minutes = model.getRemind_time_minutes();
                remindData.remind_week = model.getRemind_week();
                remindData.remind_set_ok = model.getRemind_set_ok();
                remindDataslist.add(remindData);
            }
        }
        return remindDataslist;
    }

    public final static int getAllCount() {
        return count(RemindDataModel.class);
    }

    public final static RemindDataModel findByIdRemindDataModel(int remind_id) {
        return DataSupport.where("remind_id = ?", remind_id + "").findFirst(RemindDataModel.class);
    }

    public final static RemindDataModel findRemindDataModel(RemindData remindData) {
        int remind_type = remindData.remind_type;
        int hour = remindData.remind_time_hours;
        int min = remindData.remind_time_minutes;
        String week = remindData.remind_week;
        int set = remindData.remind_set_ok;
        return DataSupport.where("remind_type = ? and remind_time_hours = ? and remind_time_minutes = ? and remind_week = ? and remind_set_ok = ? ",
                remind_type + "", hour + "", min + "", week + "", set + "")
                .findFirst(RemindDataModel.class);
    }

    public final static void saveOrUpdate(RemindData remindData) {
        if (remindData != null) {
            RemindDataModel model = findByIdRemindDataModel(remindData.remind_id);
            if (model != null && remindData.remind_id != -1) {
                model.setRemind_text(remindData.remind_text);
                model.setRemind_set_ok(remindData.remind_set_ok);
                model.setRemind_time_hours(remindData.remind_time_hours);
                model.setRemind_time_minutes(remindData.remind_time_minutes);
                model.setRemind_week(remindData.remind_week);
                model.setRemind_type(remindData.remind_type);
                ContentValues contentValues = new ContentValues();
                contentValues.put("remind_text", remindData.remind_text);
                contentValues.put("remind_set_ok", remindData.remind_set_ok);
                contentValues.put("remind_time_hours", remindData.remind_time_hours);
                contentValues.put("remind_time_minutes", remindData.remind_time_minutes);
                contentValues.put("remind_week", remindData.remind_week);
                contentValues.put("remind_type", remindData.remind_type);
                DataSupport.updateAll(RemindDataModel.class, contentValues, "remind_id = ?", model.remind_id + "");
            } else {
                RemindDataModel bean = new RemindDataModel();
                bean.setRemind_text(remindData.remind_text);
                bean.setRemind_set_ok(remindData.remind_set_ok);
                bean.setRemind_time_hours(remindData.remind_time_hours);
                bean.setRemind_time_minutes(remindData.remind_time_minutes);
                bean.setRemind_week(remindData.remind_week);
                bean.setRemind_type(remindData.remind_type);
                int id = DataSupport.max(RemindDataModel.class, "remind_id", Integer.TYPE);
                bean.setRemind_id(id + 1);
                bean.save();
            }
        }
    }


    public final static void deleteAll() {
        deleteAll(RemindDataModel.class);
    }

    public final static void saveAllRemindDataList(List<RemindData> remindDataslist) {
        if (remindDataslist != null && remindDataslist.size() > 0) {
            for (int i = 0; i < remindDataslist.size(); i++) {
                RemindDataModel bean = new RemindDataModel();
                RemindData remindData = remindDataslist.get(i);
                bean.setRemind_text(remindData.remind_text);
                bean.setRemind_set_ok(remindData.remind_set_ok);
                bean.setRemind_time_hours(remindData.remind_time_hours);
                bean.setRemind_time_minutes(remindData.remind_time_minutes);
                bean.setRemind_week(remindData.remind_week);
                bean.setRemind_type(remindData.remind_type);
                bean.setRemind_id(remindData.remind_id);
                bean.save();
            }
        }
    }

    public final static List<RemindDataModel> findAllByRemindTime(int hour, int min) {
        return where("remind_time_hours = ? and remind_time_minutes = ? ", hour + "", min + "").find(RemindDataModel.class);
    }

    /**
     * 如果星期相同，则返回真。。
     *
     * @param weekString 传入的星期字符串
     * @param remindDataModel   需要对比的数据
     * @return
     */
    private static boolean checkWeek(String weekString, RemindDataModel remindDataModel) {
        String remind_week = remindDataModel.getRemind_week();
        for (int j = 0; j < remind_week.length(); j++) {
            String week = remind_week.substring(j, j + 1);
            if (week.equalsIgnoreCase("1") && week.equalsIgnoreCase(weekString.substring(j, j + 1))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 无相同时间，校验通过
     *
     * @param hour
     * @param min
     * @return
     */
    public final static boolean checkTime(String weekString, int hour, int min) {
        List<RemindDataModel> list = findAllByRemindTime(hour, min);
        if (list == null || list.size() <= 0) {
            return true;
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (checkWeek(weekString, list.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 无相同时间，校验通过
     *
     * @param remindData
     * @param hour
     * @param min
     * @return
     */
    public final static boolean checkRemindHaveSameTime(RemindData remindData, String weekString, int hour, int min) {
        List<RemindDataModel> list = findAllByRemindTime(hour, min);
        if (list == null || list.size() <= 0) {
            return true;
        }
        for (RemindDataModel remindDataModel : list) {
            if (remindDataModel.remind_id != remindData.remind_id && checkWeek(weekString, remindDataModel)) {//存在其他提醒与当前提醒时间一致
                return false;
            }
        }
        return true;
    }


    public final static void deleteRemindData(RemindData remindData) {
        if (remindData != null) {
            RemindDataModel bean = new RemindDataModel();
            bean.setRemind_text(remindData.remind_text);
            bean.setRemind_set_ok(remindData.remind_set_ok);
            bean.setRemind_time_hours(remindData.remind_time_hours);
            bean.setRemind_time_minutes(remindData.remind_time_minutes);
            bean.setRemind_week(remindData.remind_week);
            bean.setRemind_type(remindData.remind_type);
            bean.setRemind_id(remindData.remind_id);
            bean.delete();
        }
    }
}
