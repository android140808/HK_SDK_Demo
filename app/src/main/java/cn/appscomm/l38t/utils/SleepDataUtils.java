package cn.appscomm.l38t.utils;

import android.text.TextUtils;

import com.appscomm.bluetooth.bean.SleepData;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.appscomm.l38t.config.AccountConfig;
import cn.appscomm.netlib.bean.sleep.UploadSleep;
import cn.appscomm.uploaddata.SleepDataHelper;
import cn.appscomm.uploaddata.database.SleepDataDB;

/**
 * 用来对数据的统计处理
 */
public class SleepDataUtils {
    /**
     * 获取睡眠区间和清醒次数
     *
     * @param startAndEndTime
     * @return 数组0为睡眠时间，数组索引1为清醒次数
     */
    public synchronized static int[] getSleepData(String[] startAndEndTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int[] result = new int[2];
        try {
            Date parse = simpleDateFormat.parse(startAndEndTime[0]);
            Date last = simpleDateFormat.parse(startAndEndTime[1]);
            //去除前一天和后一天的数据
            List<SleepDataDB> sleepDataDBs = getSleepDataDBResult(parse, last);
            if (sleepDataDBs != null && sleepDataDBs.size() > 0) {
                List<List<SleepData>> sleepDataList = SleepDataHelper.getSleepDataList(sleepDataDBs);
                for (int i = 0; i < sleepDataList.size(); i++) {
                    UploadSleep.SleepDetails sleepDetails = SleepDataHelper.praseSleepDataList(sleepDataList.get(i));
                    if (sleepDetails != null && (sleepDetails.getSleepDuration() <= 0
                            || sleepDetails.getSleepDuration() >= 480)) {
                        sleepDataList.remove(i);
                        i--;
                        continue;
                    }
                    result[0] += sleepDetails.getSleepDuration();
                    result[1] += sleepDetails.getAwakeCount();
                }
            }
        } catch (Exception e) {
            result[0] = 0;
            result[1] = 0;
            e.printStackTrace();
        }
        return result;
    }


    public synchronized static List<SleepDataDB> getSleepDataDBResult(Date startTime, Date endTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTimePre = simpleDateFormat.format(new Date(startTime.getTime() - 12 * 3600 * 1000));
        String endTimeLast = simpleDateFormat.format(new Date(endTime.getTime() + 12 * 3600 * 1000));
        List<SleepDataDB> sleepDataDBTemp = DataSupport.where("userId=? and startTime>? and endTime<? order by startTime asc",
                AccountConfig.getUserId() + "",
                startTimePre, endTimeLast).find(SleepDataDB.class);
        return getTrueSleep(startTimePre, endTimeLast, sleepDataDBTemp);
    }

    private static synchronized List<SleepDataDB> getTrueSleep(String start, String end, List<SleepDataDB> sleepDataDBTemp) {
        List<SleepDataDB> result = null;
        String pre17 = "", last16 = "";
        int index16 = -1;
        //去除掉前面一天的数据，以17为判断标准，去除掉后一天的数据，以16为判断标准
        if (sleepDataDBTemp != null && sleepDataDBTemp.size() > 0) {
            for (int i = 0; i < sleepDataDBTemp.size(); i++) {
                //获取16和17,18的标志位
                if (sleepDataDBTemp.get(i).getSleep_type() == 16) {
                    last16 = sleepDataDBTemp.get(i).getStartTime();
                    index16 = i;
                } else if (sleepDataDBTemp.get(i).getSleep_type() == 17 || sleepDataDBTemp.get(i).getSleep_type() == 18) {
                    pre17 = sleepDataDBTemp.get(i).getStartTime();
                }
                /**如果17或18的开始时间与start的日期相同，则去除掉该输出之前的数据
                 如果17与end的日期一致，则删除16到17之间的 数据
                 如果16的开始时间与end的日期一致，则去除掉16之后的睡眠数据
                 */
                if (!TextUtils.isEmpty(pre17) && pre17.substring(0, 10).equalsIgnoreCase(start.substring(0, 10))) {
                    sleepDataDBTemp = sleepDataDBTemp.subList(i + 1, sleepDataDBTemp.size());
                    pre17 = "";
                    last16 = "";
                    index16 = -1;
                    i = 0;
                } else if (!TextUtils.isEmpty(pre17) && pre17.substring(0, 10).equalsIgnoreCase(end.substring(0, 10))
                        && index16 != -1) {
                    sleepDataDBTemp = sleepDataDBTemp.subList(0, index16 + 1);
                    break;
                } else if (!TextUtils.isEmpty(last16) && last16.substring(0, 10).equalsIgnoreCase(end.substring(0, 10))) {
                    sleepDataDBTemp = sleepDataDBTemp.subList(0, i + 1);
                    break;
                }
            }
            result = sleepDataDBTemp;
        }
        return result;
    }

}
