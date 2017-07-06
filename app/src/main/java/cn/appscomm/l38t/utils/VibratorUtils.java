package cn.appscomm.l38t.utils;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

public class VibratorUtils {

	/**
	 * 振动
	 * @param context
	 * @param seconds
	 */
	public static void Vibrate(final Context context, long seconds) { 
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE); 
        vib.vibrate(seconds*1000);
    } 
	
	public static void Vibrate(final Context context) { 
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE); 
        long [] pattern = {100,1000,1000,1000,1000,1000};   // 停止 开启 停止 开启   
        vib.vibrate(pattern,-1);           //重复两次上面的pattern 如果只想震动一次，index设为-1   
    } 
}
