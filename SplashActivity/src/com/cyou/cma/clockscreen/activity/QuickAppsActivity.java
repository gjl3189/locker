package com.cyou.cma.clockscreen.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.adjust.sdk.Adjust;
import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.adapter.InstalledAppAdapter4QuickLaunch;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.material.LImageButton;

public class QuickAppsActivity extends FragmentActivity {

	private LImageButton mBackButton;
	private TextView mTitleTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SystemUIStatusUtil.onCreate(this, this.getWindow());

		setContentView(R.layout.activity_quick_app);
		if (SystemUIStatusUtil.isStatusBarTransparency(this)) {
			findViewById(R.id.root).setPadding(0,
					ImageUtil.getStatusBarHeight(this), 0, 0);
		}
		mBackButton = (LImageButton) findViewById(R.id.btn_left);
		mTitleTextView = (TextView) findViewById(R.id.tv_title);
		mTitleTextView.setText(R.string.quick_launch_bar);
		// end
		mBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// getSupportFragmentManager().findFragmentById(
		// R.id.quick_app_fragment);
		// FragmentTransaction transaction = this.getSupportFragmentManager()
		// .beginTransaction();
		// transaction.add(R.id.guideapplock_content, appLockFragment);
		// transaction.commit();
		Util.putPreferenceBoolean(this, Util.HASHASHAS, true);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		InstalledAppAdapter4QuickLaunch.sQuickHashMap.clear();
	}
}
