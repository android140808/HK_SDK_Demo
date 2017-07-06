package cn.appscomm.netlib.config;

import android.annotation.SuppressLint;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SharePreferenceUtil {

    private static boolean mDebug = false;

    @SuppressLint("NewApi")
	public static void applyToEditor(Editor editor) {
        if (mDebug)
            Log.d("show", "SDK_INT  = " + android.os.Build.VERSION.SDK_INT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }
    
    public static void commitToEditor(Editor editor) {
        if (mDebug)
            Log.d("show", "SDK_INT  = " + android.os.Build.VERSION.SDK_INT);
        
        editor.commit();	       
    }
}
