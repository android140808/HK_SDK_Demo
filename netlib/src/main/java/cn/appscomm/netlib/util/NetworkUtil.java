/**
 * @Project feeapp 
 * @Title: NetworkUtil.java
 * @Package com.hgsoft.feeapp.util
 * @Description: TODO((用一句话描述该文件做什么)
 * @author weiliu
 * @date 2014年6月26日 上午10:10:14
 */
package cn.appscomm.netlib.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;

/**
 * @ClassName: NetworkUtil
 * @Description: TODO(用一句话描述该类做什么)
 * @author weiliu
 * @date 2014年6月26日 上午10:10:14
 */
public class NetworkUtil {
	/**
	 * 判断网络是否连接
	 * TODO(这里用一句话描述这个方法的作用)
	 * @Title:isNetworkConnected
	 * @param context
	 * @return 
	 *
	 * @author weiliu
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断wifi是否开启
	 * TODO(这里用一句话描述这个方法的作用)
	 * @Title:isWifiConnected
	 * @param context
	 * @return 
	 *
	 * @author weiliu
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	/**
	 * 判断GPRS是否开启
	 * TODO(这里用一句话描述这个方法的作用)
	 * @Title:isGPRSConnected
	 * @param context
	 * @return 
	 *
	 * @author weiliu
	 */
	public static boolean isGPRSConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				int netSubtype = mNetworkInfo.getSubtype();
				if (netSubtype == TelephonyManager.NETWORK_TYPE_GPRS
						|| netSubtype == TelephonyManager.NETWORK_TYPE_CDMA
						|| netSubtype == TelephonyManager.NETWORK_TYPE_EDGE) {

					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取网络连接的类型
	 * TODO(这里用一句话描述这个方法的作用)
	 * @Title:getConnectedType
	 * @param context
	 * @return one of ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_WIMAX, ConnectivityManager.TYPE_ETHERNET, ConnectivityManager.TYPE_BLUETOOTH, or other types defined by ConnectivityManager
	 *
	 * @author weiliu
	 */
	public static int getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}
	
	/**
	 * 打开网络连接设置
	 * @Title:openNetWorkSetting
	 * @param context 
	 *
	 * @author weiliu
	 */
	public static final void openNetWorkSetting(final Context context) {
		Intent intent = new Intent();
		try {
			intent.setAction(Settings.ACTION_WIFI_SETTINGS);
			context.startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			intent.setAction(Settings.ACTION_SETTINGS);
			try {
				context.startActivity(intent);
			} catch (Exception e) {
			}
		}
	}

}