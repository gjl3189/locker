package com.cyou.cma.clockscreen.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.fragment.LocalThemeFragment;
import com.cyou.cma.clockscreen.fragment.OnlineThemeFragmentGp;
import com.cyou.cma.clockscreen.fragment.QuickLaunchFragment;
import com.cyou.cma.clockscreen.service.KeyguardService;
import com.cyou.cma.clockscreen.util.CyGuideHelper;
import com.cyou.cma.clockscreen.util.SettingsHelper;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.CustomAlertDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class LockMainActivity extends TabFragmentActivity2 {
	private boolean debug = false;
	private Context mContext;
	private boolean mHasShowEnableServiceDialog = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mLeft.setImageResource(R.drawable.icon_header_logo);
		mLeft.setEnabled(false);

		mRight.setVisibility(View.VISIBLE);
		mRight.setImageResource(Util.getPreferenceBoolean(this,
				Util.SAVE_KEY_IS_NEW_SETTINGS, true) ? R.drawable.icon_header_setting_new
				: R.drawable.icon_header_setting);
		mRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Util.getPreferenceBoolean(LockMainActivity.this,
						Util.SAVE_KEY_IS_NEW_SETTINGS, true)) {
					Util.putPreferenceBoolean(LockMainActivity.this,
							Util.SAVE_KEY_IS_NEW_SETTINGS, false);
					mRight.setImageResource(R.drawable.icon_header_setting);
				}
				Intent intent = new Intent(LockMainActivity.this,
						SettingActivity.class);
				startActivity(intent);
			}
		});
		// Edit by jiangbin 默认显示快速入口
		/* 如果没有网络直接显示本地页面 */
		// if (!HttpUtil.isNetworkAvailable(mContext)) {
		// // mViewPager.setCurrentItem(1);
		// mTabPageIndicatorFake.setViewPager(mViewPager, 1);
		// mTabPageIndicatorFake.invalidateForce(1);
		// mTabPageIndicatorReal.setViewPager(mViewPager, 1);
		// mTabPageIndicatorReal.invalidateForce(1);
		// }
//		mTabPageIndicatorFake.setViewPager(mViewPager, 2);
//		mTabPageIndicatorFake.invalidateForce(2);
//		mTabPageIndicatorReal.setViewPager(mViewPager, 2);
//		mTabPageIndicatorReal.invalidateForce(2);
		// Edit by jiangbin 默认显示快速入口
		MobclickAgent
				.onEvent(mContext, Util.Statistics.KEY_START_MAIN_ACTIVITY);

		Util.getPreferenceLong(mContext, Util.SAVE_KEY_CURRENT_PWD_CATEGORY, 0);

		boolean isShowed = Util.getPreferenceBoolean(mContext,
				Util.SAVE_KEY_IS_SHOWED_GRADE_DIALOG, false);
		if (!isShowed) {
			if (System.currentTimeMillis()
					- Util.getPreferenceLong(mContext,
							Util.SAVE_KEY_FIRST_START_TIME, 0) > Util
						.getOneDayMillis()) {
				showGradeDialog();
			}
		}
	}

	private void showGradeDialog() {
		new CustomAlertDialog.Builder(mContext)
				.setTitle(R.string.dialog_grade_title)
				.setMessage(R.string.dialog_grade_message)
				.setIcon(R.drawable.icon_star)
				.setPositiveButton(R.string.dialog_grade_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								Util.openAppDetailInGp(mContext,
										getPackageName());
							}
						})
				.setNegativeButton(R.string.dialog_grade_cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();
		Util.putPreferenceBoolean(mContext,
				Util.SAVE_KEY_IS_SHOWED_GRADE_DIALOG, true);
	}

	@Override
	protected void setTitleText(TextView title) {
		title.setText(R.string.app_name);
	}

	@Override
	protected int getTabsNameResource() {
		return R.array.locker_mode;
	}

	@Override
	protected List<Fragment> getTabsFragments() {
		List<Fragment> fragements = new ArrayList<Fragment>();
		if (!debug) {
			OnlineThemeFragmentGp onlineThemeFragment = new OnlineThemeFragmentGp();
			fragements.add(onlineThemeFragment);
		} else {
			Fragment fragment = new Fragment();
			fragements.add(fragment);
		}
		LocalThemeFragment localThemeFragment = new LocalThemeFragment();
		fragements.add(localThemeFragment);
		QuickLaunchFragment quickLaunchFragment = new QuickLaunchFragment();
		fragements.add(quickLaunchFragment);

		return fragements;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// add by Jack
		if (myFrameLayout != null) {
			View view = myFrameLayout.findViewById(R.id.guide_mask);
			if (view != null) {
				view.setVisibility(View.GONE);
			}
		}
		// end
		ImageLoader.getInstance().clearMemoryCache();
		System.gc();
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		// add by Jack
		if (CyGuideHelper.isShow) {
			CyGuideHelper.isShow = false;
			CyGuideHelper.hasShow(this);
			return;
		}
		// end
		if (!SettingsHelper.getLockServiceEnable(this)
				&& !mHasShowEnableServiceDialog) {
			mHasShowEnableServiceDialog = true;
			new CustomAlertDialog.Builder(mContext)
					.setTitle(R.string.settings_guide_settings_dialog_title)
					.setMessage(R.string.settings_enable_service)
					.setPositiveButton(
							R.string.settings_enable_service_dialog_ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// startActivity(new Intent(mContext,
									// SettingHomeKeyActivity.class));
									SettingsHelper.setLockServiceState(
											mContext, Constants.C_SERVICE_ON);
									startService(new Intent(mContext,
											KeyguardService.class));
									finish();
								}
							})
					.setNegativeButton(
							R.string.settings_enable_service_dialog_cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).show();

		} else {
			super.onBackPressed();
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
