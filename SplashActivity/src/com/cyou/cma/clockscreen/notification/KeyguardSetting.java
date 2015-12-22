package com.cyou.cma.clockscreen.notification;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils.SimpleStringSplitter;

import com.cyou.cma.clockscreen.activity.NotificationActivity;
import com.cyou.cma.clockscreen.activity.NotifySettingDialogActivity;
import com.cyou.cma.clockscreen.util.Util;

public class KeyguardSetting {

	private static final boolean DEBUG = false;

	private static final String NOTIFY_SERVICE = "com.cynad.cma.locker/com.cyou.cma.clockscreen.notification.KeyguardNotificationService";

	private static final String CHECK_ENABLE = "accessibility_enabled";
	private static final String CHECK_SERVICES_LIST = "enabled_accessibility_services";

	private KeyguardSetting() {

	}

	private static boolean isSystemAccessibilityEnable(Context c) {
		if (Build.VERSION.SDK_INT >= 18) {
			return true;
		}
		int i = 0;
		try {
			i = Settings.Secure.getInt(c.getContentResolver(), CHECK_ENABLE);
		} catch (Settings.SettingNotFoundException e) {
			e.printStackTrace();
		}
		return i == 1;
	}

	/**
	 * 这个是程序本身的
	 * 
	 * @param c
	 * @return
	 */
	private static boolean isKeyguardSystemEnable(Context c) {
		if (Build.VERSION.SDK_INT >= 18) {

			ContentResolver contentResolver = c.getContentResolver();
			String enabledNotificationListeners = Settings.Secure.getString(
					contentResolver, "enabled_notification_listeners");
			String packageName = c.getPackageName();

			// check to see if the enabledNotificationListeners String contains
			// our package name
			if (enabledNotificationListeners == null
					|| !enabledNotificationListeners.contains(packageName)) {
				// in this situation we know that the user has not granted the
				// app the Notification access permission
				return false;
			} else {
				return true;
			}
		} else {
			if (isSystemAccessibilityEnable(c)) {
				SimpleStringSplitter splitter = new SimpleStringSplitter(':');
				String str = Settings.Secure.getString(c.getContentResolver(),
						CHECK_SERVICES_LIST);
				if (str != null) {
					splitter.setString(str);
					while (splitter.hasNext()) {
						if (splitter.next().equalsIgnoreCase(NOTIFY_SERVICE)) {
							return true;
						}
					}
				}
			}
			return false;
		}

	}

	private static void jumpToAccessInter(Context context) {
		Intent intent = null;
		if (VERSION.SDK_INT >= 18) {
			intent = new Intent(
					"android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
		} else {
			intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
		}
		if (intent != null) {
			context.startActivity(intent);
		}
	}

	public static void jumpToAccess(final Context context) {

		jumpToAccessInter(context);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				context.startActivity(new Intent(context, NotifySettingDialogActivity.class));
			}
		}, 500);
	}

	public static boolean isAccessibilityEnable(Context context) {
		boolean isKeyguardSystemEnable = isKeyguardSystemEnable(context);
		boolean isSystemAccessibilityEnable = isSystemAccessibilityEnable(context);
		return isKeyguardSystemEnable && isSystemAccessibilityEnable;
	}

	public static boolean isAllEnable(Context context) {
		boolean allEnable = false;
		boolean appNotifictionAble = KeyguardSetting
				.isAccessibilityEnable(context);
		boolean userNOtificationAble = Util.getPreferenceBoolean(context,
				NotificationActivity.NOTIFICATION_CHECK, false);
		allEnable = appNotifictionAble && userNOtificationAble;
		return allEnable;
	}
}
