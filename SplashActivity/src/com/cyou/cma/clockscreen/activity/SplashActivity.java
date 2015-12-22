package com.cyou.cma.clockscreen.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.Contacts;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.fragment.QuickContactsFragment;
import com.cyou.cma.clockscreen.quicklaunch.DatabaseUtil;
import com.cyou.cma.clockscreen.service.AppLockService;
import com.cyou.cma.clockscreen.service.KeyguardService;
import com.cyou.cma.clockscreen.util.SettingsHelper;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.Util;
import com.umeng.analytics.MobclickAgent;

public class SplashActivity extends BaseActivity {
	private ImageView backGroundIV;
	private final int MSG_DELAY = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SystemUIStatusUtil.onCreate(this, this.getWindow());
		setContentView(R.layout.activity_splash);
		if (Util.isNewUser(mContext)
				&& Util.getPreferenceBoolean(this, "isnew", true)) {
			// AppLockHelper.setAppLockType(AppLockHelper.NONE, context)
			SettingsHelper.setApplockEnable(mContext, 0);
			Util.putPreferenceBoolean(this, "isnew", false);

		}

		DatabaseUtil.deleteInvalidContact(this);
		if (SettingsHelper.getLockServiceEnable(this))
			startService(new Intent(SplashActivity.this, KeyguardService.class));
		if (SettingsHelper.getApplockEnable(this)) {
			startService(new Intent(SplashActivity.this, AppLockService.class));
		}
		MobclickAgent.setDebugMode(Util.DEBUG);
		MobclickAgent.onError(this);
		backGroundIV = (ImageView) findViewById(R.id.splash_background);
		try {
			backGroundIV.setImageResource(R.drawable.splash);
			if (handler != null) {
				handler.sendEmptyMessageDelayed(MSG_DELAY,
						Util.SPLASH_DELAY_TIME);
			}
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
			gotoNextActivity();
			return;
		}
		saveKeyOfStatic();
		// 保存第一次启动的时间
		if (Util.getPreferenceLong(mContext, Util.SAVE_KEY_FIRST_START_TIME, 0) == 0) {
			Util.putPreferenceLong(mContext, Util.SAVE_KEY_FIRST_START_TIME,
					System.currentTimeMillis());
		}
	}

	private void saveKeyOfStatic() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		Util.putPreferenceInt(this, Util.SAVE_KEY_SCREEN_WIDTH, dm.widthPixels);
		Util.putPreferenceInt(this, Util.SAVE_KEY_SCREEN_HEIGHT,
				dm.heightPixels);
		// Log.e("SplashActivity", "widthDip-->" + dm.widthPixels
		// + " heightDip-->" + dm.heightPixels);
		// Log.e("SplashActivity", "densityDpi-->" + dm.densityDpi + " xdpi-->"
		// + dm.xdpi + " scale-->" + (dm.widthPixels / dm.xdpi));
		// int column = Util.getPreferenceInt(this,
		// Util.SAVA_KEY_COMMONAPP_COLUMN, 0);
		// if (column == 0) {
		int column = 4;
		if (dm.widthPixels >= 720 && dm.widthPixels / dm.xdpi >= 2.9f) {
			column = 5;
		} else if (dm.widthPixels <= 320) {
			column = 3;
		}
		Util.putPreferenceInt(this, Util.SAVA_KEY_COMMONAPP_COLUMN, column);
		// }
	}

	private void gotoNextActivity() {
		Intent intentSetting = new Intent(mContext, LockMainActivity.class);
		// Intent intentSetting = new Intent(mContext, SecurityActivity.class);
		startActivity(intentSetting);
		SplashActivity.this.finish();
		// Intent intentSetting = new Intent(mContext,
		// CommonUsedAppActivity.class);
		// startActivity(intentSetting);
		// SplashActivity.this.finish();
	}

	// TODO 内存泄露隐患
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_DELAY:
				gotoNextActivity();
				break;
			default:
				break;
			}

		}

	};

	@Override
	public void finish() {

		if (handler != null) {
			handler.removeMessages(MSG_DELAY);
			handler = null;
		}
		super.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (backGroundIV != null) {
			backGroundIV.setImageBitmap(null);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
}
