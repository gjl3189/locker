package com.cyou.cma.clockscreen.notification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.cyou.cma.clockscreen.bean.AppNotification;
import com.cyou.cma.clockscreen.util.StringUtils;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.InstalledLayout4Notification;

public class NotificationUtil {
	// public static class NotificationText {
	// public String title;
	// public String contentTile;
	// }

	public static AppNotification getNotificationContent(
			Notification notification, Context context) {

		RemoteViews remoteViews = notification.contentView;
		// views.apply(context, parent);
		AppNotification appNotification = new AppNotification();
		appNotification.mPackageName = notification.contentView.getPackage();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		Date d1 = new Date(notification.when);
		String t1 = format.format(d1);
		appNotification.mTime = t1;
		appNotification.mTimeLong = notification.when;
		if (android.os.Build.VERSION.SDK_INT >= 18) {
			appNotification.mTitle = getNotificationTitle4Kitkat(notification);
			appNotification.mContent = getNotificationContent4Kitkat(notification);
			if (TextUtils.isEmpty(appNotification.mTitle)) {
				View view = remoteViews.apply(context.getApplicationContext(),
						new LinearLayout(context.getApplicationContext()));
				appNotification.mTitle = getNotificationTitle(view);
			}
			if (TextUtils.isEmpty(appNotification.mContent)) {
				View view = remoteViews.apply(context.getApplicationContext(),
						new LinearLayout(context.getApplicationContext()));
				appNotification.mContent = getNotificationContent(view);
			}
		} else {
			View view = remoteViews.apply(context.getApplicationContext(),
					new LinearLayout(context.getApplicationContext()));
			appNotification.mTitle = getNotificationTitle(view);
			appNotification.mContent = getNotificationContent(view);
			if (TextUtils.isEmpty(appNotification.mTitle)) {
				try {
					appNotification.mTitle = getNotificationTitle4Kitkat(notification);
				} catch (Exception e) {
				}
			}
			if (TextUtils.isEmpty(appNotification.mContent)) {
				try {

					appNotification.mContent = getNotificationContent4Kitkat(notification);
				} catch (Exception e) {
				}
			}
		}
		
//		String s = Resources.getSystem().getString(context.getResources().getIdentifier("notification_missedCallTitle", "string", "com.android.phone"));
//		Log.d("hhhhh", s);
		// appNotification.mTitle = notification.tickerText.toString();
		
		if("com.android.phone".equals(appNotification.mPackageName)){
			if(appNotification.mContent.contains(":")){
				return null;
			}
		}
		if (TextUtils.isEmpty(appNotification.mTitle)) {
			if (TextUtils.isEmpty(notification.tickerText)) {
				return null;
			} else {
				appNotification.mTitle = notification.tickerText.toString();
			}
		}
		appNotification.mPendingIntent = notification.contentIntent;

		return appNotification;

	}

	public static ArrayList<PM> getNotificationPackages(Context context) {
		ArrayList<PM> list = new ArrayList<PM>();
		String notifications = Util.getPreferenceString(context,
				InstalledLayout4Notification.NOTIFICATION_EXTRA);

		try {

			if (!StringUtils.isEmpty(notifications)) {

				String[] pms = notifications.split(";");
				for (String s : pms) {
					PM pm = new PM();
					String[] pm1 = s.split(",");
					pm.packageName = pm1[0];
					pm.mainActivityClass = pm1[1];
					list.add(pm);
				}
			}
		} catch (Exception e) {
		}
		return list;
	}

	public static String getNotificationPackagesStr(Context context) {
		String notifications = Util.getPreferenceString(context,
				InstalledLayout4Notification.NOTIFICATION_EXTRA);

		try {

			if (!StringUtils.isEmpty(notifications)) {
				return notifications;
			}
		} catch (Exception e) {
		}
		return "";
	}

	public static class PM {
		public String packageName;
		public String mainActivityClass;
	}

	public static String getNotificationTitle(View view) {

		String str1 = "";
		if (view instanceof TextView) {
			String str2 = ((TextView) view).getText().toString();
			if (TextUtils.isEmpty(str2)) {

			} else {
				// return str2;
				// str1+=str2+";";
				return str2;
			}
		} else {
			if (view instanceof ViewGroup) {
				ViewGroup viewGroup = (ViewGroup) view;
				for (int i = 0; i < viewGroup.getChildCount(); i++) {
					String title = getNotificationTitle(viewGroup.getChildAt(i));
					if (TextUtils.isEmpty(title)) {
					} else {
						return title;
					}
				}
			}
		}

		return str1;

	}

	public static String getNotificationTitle4Kitkat(Notification notification) {
		Bundle extras = notification.extras;
		return extras.getString(Notification.EXTRA_TITLE);
		// Bitmap notificationLargeIcon =
		// ((Bitmap) extras.getParcelable(Notification.EXTRA_LARGE_ICON));
		// CharSequence notificationText =
		// extras.getCharSequence(Notification.EXTRA_TEXT);
		// CharSequence notificationSubText =
		// extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
	}

	public static String getNotificationContent4Kitkat(Notification notification) {
		Bundle extras = notification.extras;
		return extras.getString(Notification.EXTRA_TEXT);
	}

	public static String getNotificationContent(View view) {
		String str1 = "";
		if (view instanceof TextView) {
			String str2 = ((TextView) view).getText().toString();
			if (TextUtils.isEmpty(str2)) {

			} else {
				// return str2;
				// str1+=str2+";";
				return str2;
			}
		} else {
			if (view instanceof ViewGroup) {
				ViewGroup viewGroup = (ViewGroup) view;
				for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
					String title = getNotificationContent(viewGroup
							.getChildAt(i));
					if (TextUtils.isEmpty(title)) {
						// return title;
					} else {
						// str1+=title+";";
						return title;
					}
				}
			}
		}

		return str1;
	}

}
