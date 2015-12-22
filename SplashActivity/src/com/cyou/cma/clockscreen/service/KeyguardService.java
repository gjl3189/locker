package com.cyou.cma.clockscreen.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.activity.LockScreenDialog;
import com.cyou.cma.clockscreen.core.Intents;
import com.cyou.cma.clockscreen.receiver.KeyguardReceiver;
import com.cyou.cma.clockscreen.receiver.KeyguardReceiver.ScreenListener;
import com.cyou.cma.clockscreen.util.SettingsHelper;
import com.cyou.cma.clockscreen.util.Util;
import com.umeng.analytics.MobclickAgent;

public class KeyguardService extends Service {
	private static final String TAG = "KeyguardService";

	private BroadcastReceiver mKeyguardReceiver;

	private KeyguardHelper sKeyguardHelper;
	private boolean needCallEndLock = false;
	private boolean mNeedAlramEndLock = false;

	@Override
	public void onCreate() {
		// Log.e(TAG, "onCreate");
		MobclickAgent.setDebugMode(Util.DEBUG);
		MobclickAgent.onError(this);
		mKeyguardReceiver = new KeyguardReceiver(new ScreenListener() {

			@Override
			public void onScreenOff() {
				doLockScreen();
			}

			@Override
			public void RecreatLocker() {
				resetLockDialog();
			}

			@Override
			public void onScreenOn() {
				if (mLockscreenDialog != null && mLockscreenDialog.isShowing()) {
					mLockscreenDialog.onResume();
				}
			}

			boolean hasCalledIn = false;

			@Override
			public void onCallIn() {
				if (!hasCalledIn) {
					hasCalledIn = true;
					if (mLockscreenDialog != null
							&& mLockscreenDialog.isShowing()) {
						needCallEndLock = true;
						mLockscreenDialog.hide();
					} else {
						needCallEndLock = false;
					}
				}
			}

			@Override
			public void onCallEnd() {
				hasCalledIn = false;
				if (needCallEndLock) {
					needCallEndLock = false;
					if (mLockscreenDialog != null
							&& mLockscreenDialog.isShowing()) {
						mLockscreenDialog.show();
						 
					}

				}
			}

		});
		sKeyguardHelper = new KeyguardHelper(this);
		sKeyguardHelper.disableSystemKeyguard();

		IntentFilter filter = new IntentFilter();
		// filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		filter.addAction(KeyguardReceiver.ACTION_DEVICE_POLICY_MANAGER_STATE_CHANGED);
		filter.addAction(Intents.ACTION_RECREATE_DIALOG);
		registerReceiver(mKeyguardReceiver, filter);

		IntentFilter callIntentFilter = new IntentFilter();
		callIntentFilter.setPriority(1000);
		callIntentFilter.addAction("android.intent.action.PHONE_STATE");
		callIntentFilter.addAction("android.intent.action.PHONE_STATE_2");
		callIntentFilter.addAction("android.intent.action.PHONE_STATE2");
		callIntentFilter.addAction("android.intent.action.DUAL_PHONE_STATE");
		callIntentFilter.addAction("android.intent.action.PHONE_STATE_EXT");
		callIntentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(mKeyguardReceiver, callIntentFilter);
		if (Build.VERSION.SDK_INT < 18) {
			Notification localNotification = new Notification();
			// localNotification.flags = localNotification.flags | 0x20;
			startForeground(11145, localNotification);

			// Notification notification = new Notification();
			// notification.setLatestEventInfo(this, "notification_title",
			// "notification_message", null);
			// startForeground(0, new Notification());
		}

		Uri uri = Settings.System
				.getUriFor(Settings.System.NEXT_ALARM_FORMATTED);
		Handler handler = new Handler();
		getContentResolver().registerContentObserver(uri, true,
				new AlarmObserver(handler));

	}

	class AlarmObserver extends ContentObserver {
		public AlarmObserver(Handler h) {
			super(h);
		}

		@Override
		public void onChange(boolean selfChange) {
			// Log.e("MyContentObserver",
			// "NEXT_ALARM_FORMATTED-->"
			// + Settings.System.getString(getContentResolver(),
			// Settings.System.NEXT_ALARM_FORMATTED));
			super.onChange(selfChange);
			dismissDialog();

		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Log.e(TAG, "onStartCommand");
		Util.Logjb(TAG, "KeyguardService started with intent: " + intent);
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		// Log.e(TAG, "onDestroy");
		stopForeground(true);
		if (SettingsHelper.getLockServiceEnable(this)) {
			super.onDestroy();
			unregisterReceiver(mKeyguardReceiver);
			startService(new Intent(this, KeyguardService.class));
		} else {
			Util.Logjb(TAG, "KeyguardService destroyed");
			unregisterReceiver(mKeyguardReceiver);
			sKeyguardHelper.enableSystemKeyguard();
			sKeyguardHelper = null;
			super.onDestroy();
		}
	}

	private void doLockScreen() {
		if (mLockscreenDialog != null) {
			if (mLockscreenDialog.isShowing()) {
				// mLockscreenDialog.reset();
				mLockscreenDialog.onPause();
				return;
			} else {
				mLockscreenDialog.cleanUp();
			}
		}
		mLockscreenDialog = new LockScreenDialog(this,
				R.style.LockerDialogStyle);
		showDialog();
	}

	private LockScreenDialog mLockscreenDialog = null;

	public void showDialog() {
		if (mLockscreenDialog != null && !mLockscreenDialog.isShowing()) {
			mLockscreenDialog.show();
		}
	}

	public void dismissDialog() {
		if (mLockscreenDialog != null && mLockscreenDialog.isShowing()) {
			mLockscreenDialog.dismiss();
		}
	}

	private void resetLockDialog() {
		if (mLockscreenDialog != null) {
			mLockscreenDialog.cleanUp();
			mLockscreenDialog = null;
		}
	}

}
