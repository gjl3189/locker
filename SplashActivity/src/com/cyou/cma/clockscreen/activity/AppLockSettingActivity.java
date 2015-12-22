package com.cyou.cma.clockscreen.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.adjust.sdk.Adjust;
import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.fragment.AppLockChoosenFragment;
import com.cyou.cma.clockscreen.fragment.AppLockSettingFragment;
import com.umeng.analytics.MobclickAgent;

public class AppLockSettingActivity extends TabFragmentActivity implements
		OnClickListener, OnPageChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppLockSettingActivity.this.finish();
			}
		});
		mTabPageIndicator.setViewPager(mViewPager, 1);
		mTabPageIndicator.invalidateForce(1);
	}

	@Override
	protected void setTitleText(TextView title) {
		title.setText(R.string.applock_title);
	}

	@Override
	protected int getTabsNameResource() {
		return R.array.applock_title;
	}
	

	@Override
	protected List<Fragment> getTabsFragments() {
		List<Fragment> fragements = new ArrayList<Fragment>();
		AppLockSettingFragment settingFragment = new AppLockSettingFragment();
		fragements.add(settingFragment);
		AppLockChoosenFragment choosenFragment = new AppLockChoosenFragment();
		fragements.add(choosenFragment);

		return fragements;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		Adjust.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		Adjust.onResume(this);
	}

}
