package com.cyou.cma.clockscreen.notification;

import java.util.ArrayList;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;

import com.cyou.cma.clockscreen.event.NotificationEvent;
import com.cyou.cma.clockscreen.notification.NotificationUtil.PM;
import com.cyou.cma.clockscreen.util.Util;

import de.greenrobot.event.EventBus;

public class KeyguardNotificationService extends AccessibilityService {

	private static final boolean DEBUG = false;

	private static final int EVENT_NOTIFICATION_TIMEOUT_MILLIS = 80;
	int i =0;
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		if (!KeyguardSetting.isAllEnable(this)) {
			return;
		}
		if (Build.VERSION.SDK_INT >= 18 || event == null) {
			return;
		}
		if (event.getEventType() != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
			return;
		}
		if (!(event.getParcelableData() instanceof Notification)) {
			return;
		}
		// event.getWindowId()

		// if (!isInterestEvent(event)) {
		// return;
		// }
		String pkgName = event.getPackageName().toString();
		// event.get
		// long when = System.currentTimeMillis();
		ArrayList<PM> pms = NotificationUtil.getNotificationPackages(this);
		if (Util.NOTIFICATION_DEBUG) {

			Notification notification = (Notification) event
					.getParcelableData();
			NotificationEvent event1 = new NotificationEvent();
			event1.notification = notification;
			EventBus.getDefault().post(event1);

			return;

		} else {
			if (("com.android.phone".equals(pkgName))) {
				String getNotificationPackagesStr = NotificationUtil
						.getNotificationPackagesStr(this);
				if (getNotificationPackagesStr
						.contains("com.android.contacts,com.android.contacts.activities.DialtactsActivity")) {
					Notification notification = (Notification) event
							.getParcelableData();
					NotificationEvent event1 = new NotificationEvent();
					event1.isPhone= true;
					event1.notification = notification;
					EventBus.getDefault().post(event1);
				}
			} else {
				for (PM pm : pms) {
					if (pm.packageName.equals(pkgName)) {
						Notification notification = (Notification) event
								.getParcelableData();
						NotificationEvent event1 = new NotificationEvent();
						event1.notification = notification;
						EventBus.getDefault().post(event1);

						return;
					}
				}
			}
		}
	}

	@Override
	public void onInterrupt() {
	}

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		if (Build.VERSION.SDK_INT < 14) {
			setServiceInfo(AccessibilityServiceInfo.FEEDBACK_VISUAL);
		}
	}

	private void setServiceInfo(int feedbackType) {
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
		info.feedbackType = feedbackType;
		info.notificationTimeout = EVENT_NOTIFICATION_TIMEOUT_MILLIS;
		setServiceInfo(info);
	}

}
