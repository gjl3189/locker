package com.cyou.cma.clockscreen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

import com.adjust.sdk.Adjust;

public class BaseActivity extends Activity {
	protected Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		if (getRequestedOrientation() != orientation) {
			setRequestedOrientation(orientation);
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		mContext = this;
	}

	private boolean mCalled = false;

	@Override
	public void setRequestedOrientation(int requestedOrientation) {
		if (!mCalled) {
			super.setRequestedOrientation(requestedOrientation);
			mCalled = true;
		}
	}

	@Override
	protected void onResume() {
	 
		super.onResume();
		Adjust.onResume(this);
	}
	
	@Override
	protected void onPause() {
	 
		super.onPause();
		Adjust.onPause();
	}

}
