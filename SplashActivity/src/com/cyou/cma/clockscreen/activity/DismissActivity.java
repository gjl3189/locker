package com.cyou.cma.clockscreen.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.Util;
import com.umeng.analytics.MobclickAgent;

public class DismissActivity extends BaseActivity {
	private final String TAG = "DismissActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		Log.e(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		SystemUIStatusUtil.onCreate(this, this.getWindow());
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		MobclickAgent.setDebugMode(Util.DEBUG);
		MobclickAgent.onError(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		finish();
		overridePendingTransition(0, 0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
//		Log.e(TAG, "onPause");
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		Log.e(TAG, "onResume");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

}
