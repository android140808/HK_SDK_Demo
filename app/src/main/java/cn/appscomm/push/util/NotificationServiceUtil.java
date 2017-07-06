package cn.appscomm.push.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.List;

import cn.appscomm.l38t.R;
import cn.appscomm.l38t.utils.LogUtil;

public class NotificationServiceUtil {
    private static final String TAG = "NotificationServiceUtil";

    /**
     * Check if Accessibility Service is enabled.
     *
     * @param mContext
     * @return <code>true</code> if Accessibility Service is ON, otherwise <code>false</code>
     */
    public static boolean isAccessibilitySettingsOn(Context mContext,Class c) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + c.getName();

        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);

                StringBuilder sb = new StringBuilder();
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        return true;
                    } else {
                        sb.append(":");
                        int index = accessabilityService.indexOf("/");
                        if (index != -1) {
                            sb.append(accessabilityService.substring(0, index));
                        } else {
                            sb.append(accessabilityService);
                        }
                        sb.append(":");
                    }
                }
                String logVal = sb.toString();
            }
        } else {
            LogUtil.v(TAG, "***ACCESSIBILIY IS DISABLED***");
        }

        return accessibilityFound;
    }

    public static Intent  getNotificationServiceSettingIntent(){
        Intent intent = null;
        if (Build.VERSION.SDK_INT >= 18) {
            intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        } else {
            intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    public static Intent getAppDetailsIntent(String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return null;
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", pkg, null);
            intent.setData(uri);
        } else {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            String pkgName = (Build.VERSION.SDK_INT == 8) ? "pkg" : "com.android.settings.ApplicationPkgName";
            intent.putExtra(pkgName, pkg);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return intent;
    }

    public static boolean CheckNotifiServiceValid(Context context) {
        final String flat = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null && cn.getPackageName().equals(context.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void openPermissionActivity(final Context context) {
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        BackgroundThread.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, R.string.open_notification_tip, Toast.LENGTH_SHORT).show();
            }
        }, 800);
    }
}
