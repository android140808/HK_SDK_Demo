package cn.appscomm.l38t.utils;

import android.content.Context;

import com.appscomm.bluetooth.bean.Workout;
import com.appscomm.bluetooth.manage.AppsBluetoothManager;
import com.appscomm.bluetooth.manage.GlobalDataManager;
import com.appscomm.bluetooth.manage.GlobalVarManager;
import com.appscomm.bluetooth.protocol.command.base.BaseCommand;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.LinkedList;

import cn.appscomm.l38t.Command.DeleteWorkoutData;
import cn.appscomm.l38t.Command.GetWorkoutData;
import cn.appscomm.l38t.Command.WorkoutCount;
import cn.appscomm.l38t.app.GlobalApp;
import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.l38t.interfacepack.SyncInterface;
import cn.appscomm.l38t.model.database.UserBindDevice;
import cn.appscomm.l38t.model.database.WorkoutDB;


/**
 * Created by liucheng on 2017/6/12.
 */

public class WorkoutUtils implements BaseCommand.CommandResultCallback {

    private static volatile WorkoutUtils workoutUtils;
    private SyncInterface syncInterface;

    public static WorkoutUtils getInstance(Context context) {
        if (workoutUtils == null) {
            synchronized (WorkoutUtils.class) {
                workoutUtils = new WorkoutUtils();
            }
        }
        return workoutUtils;
    }

    public ArrayList<WorkoutDB> getData() {
        return (ArrayList<WorkoutDB>) DataSupport.where("userId=? order by time desc", AccountConfig.getUserId() + "").find(WorkoutDB.class);
    }

    public void syncNow(SyncInterface syncInterface) {
        this.syncInterface = syncInterface;
        WorkoutCount workoutCount = new WorkoutCount(this);
        sendCommand(workoutCount);
    }

    private void sendCommand(BaseCommand command) {
        AppsBluetoothManager.getInstance(GlobalApp.getAppContext()).sendCommand(command);
    }

    @Override
    public void onSuccess(BaseCommand command) {
        if (command != null) {
            if (command instanceof WorkoutCount) {
                int workoutsCount = GlobalVarManager.getInstance().getWorkoutsCount();
                if (workoutsCount > 0) {
                    GetWorkoutData getWorkoutData = new GetWorkoutData(this, 0, workoutsCount);
                    sendCommand(getWorkoutData);
                } else {
                    giveBack(true);
                }
            } else if (command instanceof GetWorkoutData) {
                LinkedList<Workout> workoutDatas = GlobalDataManager.getInstance().getWorkoutDatas();
                if (workoutDatas != null && workoutDatas.size() > 0) {
                    getDataSuccess();
                } else {
                    giveBack(false);
                }
            } else if (command instanceof DeleteWorkoutData) {
                giveBack(true);
            }
        }
    }

    @Override
    public void onFail(BaseCommand command) {
        giveBack(false);
    }

    private void getDataSuccess() {
        for (Workout workout : GlobalDataManager.getInstance().getWorkoutDatas()) {
            WorkoutDB workoutDB = new WorkoutDB();
            workoutDB.setTime(workout.time);
            workoutDB.setTimeDuration(workout.timeDuration);
            workoutDB.setStep(workout.step);
            workoutDB.setCalorie(workout.calorie);
            workoutDB.setDistance(workout.distance);
            workoutDB.setHeartRate(workout.heartRate);
            workoutDB.setUserId(AccountConfig.getUserId());
            workoutDB.setDeviceId(UserBindDevice.getBindDeviceId(AccountConfig.getUserId()));
            if (DataSupport.where("time=? and userId =? and deviceId=?", workoutDB.getTime() + "", workoutDB.getUserId() + "", workoutDB.getDeviceId()).find(WorkoutDB.class).size() > 0) {
            } else {
                workoutDB.save();
            }
        }
        DeleteWorkoutData deleteWorkoutData = new DeleteWorkoutData(this);
        sendCommand(deleteWorkoutData);
    }

    private void giveBack(boolean isSuccess) {
        if (syncInterface != null) {
            syncInterface.synOver(isSuccess);
        }
    }
}
