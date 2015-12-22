package com.cyou.cma.clockscreen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.adjust.sdk.Adjust;
import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.fragment.AppLockChoosenFragment;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.widget.material.LImageButton;

/**
 * 引导设置applock
 * 
 * @author jiangbin
 * 
 */
public class GuideApplockActivity extends FragmentActivity {
	// private LinearLayout mFragmentRoot;

	private LImageButton mBackButton;
	private TextView mTitleTextView;
	private Button mNextButton;

	AppLockChoosenFragment appLockFragment = new AppLockChoosenFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SystemUIStatusUtil.onCreate(this, this.getWindow());

		setContentView(R.layout.activity_guide_applock);
		if (SystemUIStatusUtil.isStatusBarTransparency(this)) {
			findViewById(R.id.root).setPadding(0,
					ImageUtil.getStatusBarHeight(this), 0, 0);
		}
		// ((AppLockChoosenFragment)
		// findViewById(R.id.guideapplock_app_fragment))
		// .setIsGuide(true);
		// mFragmentRoot = (LinearLayout) findViewById(R.id.list);
		// FragmentManager fragmentManager = getFragmentManager();
		// FragmentTransaction fragmentTransaction =
		// fragmentManager.beginTransaction();
		mBackButton = (LImageButton) findViewById(R.id.btn_left);
		mTitleTextView = (TextView) findViewById(R.id.tv_title);
		mNextButton = (Button) findViewById(R.id.next_button);
		// edit by jiangbin
		// mTitleTextView.setText(R.string.shuzimima);
		mTitleTextView.setText(R.string.applock_title);
		// end
		mNextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(GuideApplockActivity.this,
						GuideBackupActivity.class));
				finish();
			}
		});
		mBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		FragmentTransaction transaction = this.getSupportFragmentManager()
				.beginTransaction();
		transaction.add(R.id.guideapplock_content, appLockFragment);
		transaction.commit();
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
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (appLockFragment != null) {
			View v = findViewById(R.id.guideapplock_hint_layout);
			appLockFragment.setIsGuide(true, v.getHeight());
		}
	}
}
