package com.cyou.cma.clockscreen.activity;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.cyou.cma.clockscreen.password.SecureAccess;
import com.cyou.cma.clockscreen.service.AppLockService;
import com.cyou.cma.clockscreen.util.Util;

public class BaseAppLockActivity extends BaseActivity implements SecureAccess {
	private boolean autoFocus = false;// 是否自动失去焦点
	private String mPackageName = "";
	private int openApp = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// setContentView(R.layout.activity_app_locker);
		mPackageName = getIntent().getStringExtra("packageName");
		openApp = getIntent().getIntExtra("openApp", 0);
		// Resources res = getResources();
		// DisplayMetrics dm = res.getDisplayMetrics();
		// ImageUtil.getSystemWallpaperCore(this, dm.widthPixels,
		// dm.heightPixels);
		// Log.d("haha", "haha mPackageName onCreate-->" + mPackageName +
		// " taskId " + getTaskId());
		overridePendingTransition(0, 0);
		mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		// LockApplication.addApplockActivity(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			autoFocus = true;
			gotoLauncher();
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
			autoFocus = true;
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		mPackageName = intent.getStringExtra("packageName");
		openApp = intent.getIntExtra("openApp", 0);
		autoFocus = false;
		// Log.d("haha", "haha mPackageName onNewIntent-->" + mPackageName);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		if (hasFocus) {
		} else {
			if (!autoFocus) {
				Intent intent = new Intent(BaseAppLockActivity.this, getClass());
				intent.putExtra("packageName", mPackageName);
				startActivity(intent);
			}
		}
		super.onWindowFocusChanged(hasFocus);
	}

	public void gotoLauncher() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		this.startActivity(intent);
		finish();
	}

	// @Override
	// public void unlockCallback() {
	// Util.startAppByPackageName(mPackageName, getApplicationContext());
	// }
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// LockApplication.removeApplockActivity(this);
	}

	private ActivityManager mActivityManager;

	@Override
	public void onSecureSuccess() {
		this.autoFocus = true;
		AppLockService.mLastLockTime = SystemClock.elapsedRealtime();

		finish();
		if (openApp == 0) {
			List<RunningTaskInfo> runningTaskInfos = this.mActivityManager
					.getRunningTasks(2);
			String firstPackageName = runningTaskInfos.get(0).topActivity
					.getPackageName();
			String secondPackageName = "";
			try {
				secondPackageName = runningTaskInfos.get(1).topActivity
						.getPackageName();
			} catch (Exception e) {
//				if (firstPackageName.equals(getPackageName())) {
					Util.startAppByPackageName(mPackageName,
							getApplicationContext());
//				}
			}

			if (firstPackageName.equals(getPackageName())) {// 如果第一个还是锁屏
				if (!secondPackageName.equals(mPackageName)) {
					Util.startAppByPackageName(mPackageName,
							getApplicationContext());
				}
			} else {
				if (!firstPackageName.equals(mPackageName)) {
					Util.startAppByPackageName(mPackageName,
							getApplicationContext());
				}
			}
			// if
			// (!mPackageName.equals(this.mActivityManager.getRunningTasks(1).get(0).topActivity
			// .getPackageName()))
			// Util.startAppByPackageName(mPackageName,
			// getApplicationContext());
		}
		AppLockService.mHashtable.put(mPackageName, true);
	}
}
