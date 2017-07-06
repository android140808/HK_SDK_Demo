package cn.appscomm.l38t.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.telephony.TelephonyManager;
import android.view.View;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.app.GlobalApp;

public class SystemTools {

	public static String getSysMANUFACTURER() {
		String sysinfo = android.os.Build.MANUFACTURER;
		if (sysinfo != null && !sysinfo.equals("")) {
			sysinfo = sysinfo.toLowerCase();
		}
		return sysinfo;
	}


	public static String getSysBRAND() {
		String sysinfo = android.os.Build.BRAND;
		return sysinfo;
	}
	
	public static String getSysMODEL() {
		String sysinfo = android.os.Build.MODEL;
		if (sysinfo != null && !sysinfo.equals("")) {
			sysinfo = sysinfo.toLowerCase();
		}
		return sysinfo;
	}

	public static String getSysRELEASE() {
		String sysinfo = android.os.Build.VERSION.RELEASE;
		if (sysinfo != null && !sysinfo.equals("")) {
			sysinfo = sysinfo.toLowerCase();
		}
		return sysinfo;
	}

	public static String getIMEI(Context context) {
		String imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		return imei;
	}

	/**
	 * 获取电话号码
	 */
	public static String getNativePhoneNumber(Context context) {
		String NativePhoneNumber = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
		return NativePhoneNumber;
	}

	public static String getPhoneInfo(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		StringBuilder sb = new StringBuilder();
		sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
		sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
		sb.append("\nLine1Number = " + tm.getLine1Number());
		sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
		sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
		sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
		sb.append("\nNetworkType = " + tm.getNetworkType());
		sb.append("\nPhoneType = " + tm.getPhoneType());
		sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
		sb.append("\nSimOperator = " + tm.getSimOperator());
		sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
		sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
		sb.append("\nSimState = " + tm.getSimState());
		sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
		sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
		return sb.toString();
	}

	public static int getCurrentVersionCode(Context context) {
		int currentVersionCode = 0;
		try {
			PackageManager pManager = context.getPackageManager();
			PackageInfo info = pManager.getPackageInfo(context.getPackageName(), 0);
			currentVersionCode = info.versionCode;
		} catch (Exception e) {
		}
		return currentVersionCode;
	}

	public static String getCurrentVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager pManager = context.getPackageManager();
			PackageInfo info = pManager.getPackageInfo(context.getPackageName(), 0);
			versionName = info.versionName;
		} catch (Exception e) {
		}
		return versionName;
	}

	public static Bitmap shotView(View view) {
//        view.setDrawingCacheEnabled(true);
//        view.buildDrawingCache();
//        Bitmap b1 = view.getDrawingCache();
		Bitmap b1 = null;
		try {
			int width = view.getWidth();
			int height = view.getHeight();
			view.setBackgroundColor(GlobalApp.getAppContext().getResources().getColor(R.color.white));
			b1 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			view.draw(new Canvas(b1));
		} catch (Exception e) {
		}
		return b1;
	}
}
