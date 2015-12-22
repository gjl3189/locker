package com.cyou.cma.clockscreen.notification;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import com.cyou.cma.clockscreen.event.NotificationEvent;
import com.cyou.cma.clockscreen.event.RemoveNotificationEvent;
import com.cyou.cma.clockscreen.notification.NotificationUtil.PM;
import com.cyou.cma.clockscreen.util.Util;

import de.greenrobot.event.EventBus;

@SuppressLint("NewApi")
public class KeyguardNotificationListener extends NotificationListenerService {

	private static final boolean DEBUG = false;

	@Override
	public void onNotificationPosted(StatusBarNotification notification) {
		if (!KeyguardSetting.isAllEnable(this)) {
			return;
		}
		if (!isInterestEvent(notification)) {
			// ULog.k(DEBUG, "onNotificationPosted not interest");
			return;
		}

		Notification notify = notification.getNotification();
		if (notify == null) {
			// ULog.k(DEBUG, "onNotificationPosted no notify");
			return;
		}
		NotificationEvent event1 = new NotificationEvent();
		if (notification.getPackageName().equals("com.android.phone")) {
			event1.isPhone = true;
		}

		event1.notification = notify;
		EventBus.getDefault().post(event1);

	}

	@Override
	public void onNotificationRemoved(StatusBarNotification notification) {
	
//		event1.notification = notify;
		
		if (notification.getPackageName().equals("com.android.phone")) {
			RemoveNotificationEvent event1 = new RemoveNotificationEvent();
			EventBus.getDefault().post(event1);
		}
		
		// ULog.k(DEBUG, "onNotificationRemoved");
		// ULog.k(DEBUG, notification.toString());
		//
		// if (!isInterestEvent(notification)) {
		// ULog.k(DEBUG, "onNotificationRemoved not interest");
		// return;
		// }
		//
		// String pkgName = notification.getPackageName();
		//
		// Intent intent = new Intent(NotificationReceiver.ACTION_NOTIFICATION);
		// intent.putExtra(KNConst.NOTIFY_MSG_PKGNAME, pkgName);
		// intent.putExtra(KNConst.NOTIFY_MSG_TYPE, KNConst.NOTIFY_TYPE_REMOVE);
		// getBaseContext().sendBroadcast(intent);
	}

	private boolean isInterestEvent(StatusBarNotification event) {
		if (Util.NOTIFICATION_DEBUG) {
			return true;
		}
		if (event == null) {
			return false;
		}

		String pkgName = event.getPackageName().toString();

		if (TextUtils.isEmpty(pkgName)) {
			return false;
		}

		if (("com.android.phone".equals(pkgName))) {
			String getNotificationPackagesStr = NotificationUtil
					.getNotificationPackagesStr(this);
			if (getNotificationPackagesStr
					.contains("com.android.contacts,com.android.contacts.activities.DialtactsActivity")) {
				return true;
			}
			return false;
		} else {

			ArrayList<PM> pms = NotificationUtil.getNotificationPackages(this);
			for (PM pm : pms) {
				if (pm.packageName.equals(pkgName)) {
					return true;
				}
			}

			return false;
		}
	}
	// static class Interest{
	// boolean include;
	// boolean isPhone;
	// }
}
