package com.cyou.cma.clockscreen.activity;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adjust.sdk.Adjust;
import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.adapter.LocalThemeDetailPagerAdapter;
import com.cyou.cma.clockscreen.adapter.ZoomOutPageTransformer2;
import com.cyou.cma.clockscreen.bean.InstallLocker;
import com.cyou.cma.clockscreen.core.Intents;
import com.cyou.cma.clockscreen.service.KeyguardService;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.SettingsHelper;
import com.cyou.cma.clockscreen.util.StringUtils;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.ToastMaster;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.CustomAlertDialog;
import com.cyou.cma.clockscreen.widget.CyStageClickButton;
import com.cyou.cma.clockscreen.widget.LinePageIndicator;
import com.cyou.cma.clockscreen.widget.ViewPagerOnPageChangeListener;
import com.cyou.cma.clockscreen.widget.material.LButton;
import com.cyou.cma.clockscreen.widget.material.LFrameLayout;
import com.cyou.cma.clockscreen.widget.material.LImageButton;
import com.umeng.analytics.MobclickAgent;

public class LocalThemeDetailActivity extends Activity implements
		OnClickListener {

	private RelativeLayout viewPagerContainer;
	private ViewPager mViewPager;
	private LinePageIndicator mLinePageIndicator;
	// add by Jack: add animation
	public CyStageClickButton mImageButtonCrash;
	// end
	public LImageButton mImageButtonSetting;
	public LImageButton mImageButtonBack;

	private LocalThemeDetailPagerAdapter mPagerAdapter;
	private InstallLocker mInstallLocker;

	private TextView mTextViewLockName;
	private LButton mStateButton;

	private Dialog mDeleteDialog;

	private BroadcastReceiver themeInstallReciever = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_UNINSTALL_PACKAGE.equals(action)
					|| Intent.ACTION_PACKAGE_FULLY_REMOVED.equals(action)) {
				String packageName = intent.getDataString().substring(8);
				boolean isOurLocker = InstallLocker
						.isOurPakcageName(packageName);

				if (isOurLocker) {
					themeUninstallListener(packageName);
				}
			}
		}
	};
	private ContentObserver mObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			String packageName = SettingsHelper
					.getCurrentTheme(LocalThemeDetailActivity.this);
			onUpdateListener(packageName);
		}
	};

	public void themeUninstallListener(String packageName) {
		if (mInstallLocker.packageName.equals(packageName)) {
			finish();
		}
	}

	public void onUpdateListener(String packageName) {
		if (packageName.equals(mInstallLocker.packageName)) {
			showUsing();
		} else {
			showApply();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SystemUIStatusUtil.onCreate(this, this.getWindow());
		setContentView(R.layout.activity_lock_detail);
		MobclickAgent.setDebugMode(Util.DEBUG);
		MobclickAgent.onError(this);
		initInstallLocker();
		findViews();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_UNINSTALL_PACKAGE);
		intentFilter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
		intentFilter.addDataScheme("package");
		registerReceiver(themeInstallReciever, intentFilter);
		getContentResolver().registerContentObserver(
				Settings.System.getUriFor(Constants.C_THEME_PACKAGE), false,
				mObserver);
		if (SystemUIStatusUtil.isStatusBarTransparency(this)) {
			findViewById(R.id.root).setPadding(0,
					ImageUtil.getStatusBarHeight(this), 0, 0);
		}
	}

	private void initInstallLocker() {
		String pakcageName = getIntent().getStringExtra("packageName");
		String currentTheme = SettingsHelper.getCurrentTheme(this);
		boolean using = pakcageName.equals(currentTheme);
		if (pakcageName.equals(Constants.SKY_LOCKER_DEFAULT_THEME)) {
			mInstallLocker = new InstallLocker();
			PackageInfo packageInfo1 = null;
			try {
				packageInfo1 = getPackageManager().getPackageInfo(
						getPackageName(), 0);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			mInstallLocker.firstInstallTime = packageInfo1.firstInstallTime;
			mInstallLocker.lastUpdateTime = packageInfo1.lastUpdateTime;
			mInstallLocker.packageName = Constants.SKY_LOCKER_DEFAULT_THEME;
			mInstallLocker.label = "Default";
			mInstallLocker.versionCode = 1;
			mInstallLocker.versionName = "1.0";
			mInstallLocker.context = this.getApplicationContext();
			mInstallLocker.currentTheme = using;
			long length = new File(packageInfo1.applicationInfo.publicSourceDir)
					.length();
			mInstallLocker.sizeStr = StringUtils.friendly_appsize(length);
		} else {
			try {
				PackageInfo packageInfo = getPackageManager().getPackageInfo(
						pakcageName, 0);
				mInstallLocker = new InstallLocker();

				mInstallLocker.firstInstallTime = packageInfo.firstInstallTime;
				mInstallLocker.lastUpdateTime = packageInfo.lastUpdateTime;
				mInstallLocker.packageName = packageInfo.packageName;
				mInstallLocker.label = getPackageManager().getApplicationLabel(
						packageInfo.applicationInfo).toString();
				mInstallLocker.versionCode = packageInfo.versionCode;
				mInstallLocker.versionName = packageInfo.versionName;
				mInstallLocker.currentTheme = using;
				long length1 = new File(
						packageInfo.applicationInfo.publicSourceDir).length();
				mInstallLocker.sizeStr = StringUtils.friendly_appsize(length1);
				try {
					mInstallLocker.context = createPackageContext(
							mInstallLocker.packageName,
							Context.CONTEXT_IGNORE_SECURITY);
				} catch (NameNotFoundException e) {
					//e.printStackTrace();
				}

			} catch (NameNotFoundException e) {
				//e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		Adjust.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		Adjust.onResume(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Util.Logjb("task id ", "ThemeDetailActivity onNewIntent " + getTaskId());
	}

	private void findViews() {
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mLinePageIndicator = (LinePageIndicator) findViewById(R.id.linePageIndicator);
		viewPagerContainer = (RelativeLayout) findViewById(R.id.pager_layout);

		// 初始化 ViewHolder
		mTextViewLockName = (TextView) findViewById(R.id.textview_lockname);
		mStateButton = (LButton) findViewById(R.id.state_button);
		// add by Jack
		mImageButtonCrash = (CyStageClickButton) findViewById(R.id.imageview_crash);
		// end
		mImageButtonSetting = (LImageButton) findViewById(R.id.imageview_setting);
		mImageButtonBack = (LImageButton) findViewById(R.id.imageview_back);
		mStateButton.setOnClickListener(this);
		mImageButtonCrash.setOnClickListener(this);
		mImageButtonSetting.setOnClickListener(this);
		mImageButtonBack.setOnClickListener(this);
		if (mInstallLocker == null) {
			finish();
			return;
		}
		mTextViewLockName.setText(mInstallLocker.label);
		if (mInstallLocker.currentTheme) {
			showUsing();
		} else {
			showApply();
		}
		initViewPager();
		if (mInstallLocker.packageName
				.equals(Constants.SKY_LOCKER_DEFAULT_THEME)) {
			mImageButtonCrash.setVisibility(View.GONE);
		}
	}

	private void showApply() {
		mStateButton.setText(R.string.state_button_apply);
		mStateButton.setBackgroundResource(R.drawable.statebutton_apply);
		if (!mInstallLocker.packageName
				.equals(Constants.SKY_LOCKER_DEFAULT_THEME)) {
			mImageButtonCrash.setVisibility(View.VISIBLE);
		}
		mImageButtonSetting.setVisibility(View.GONE);
	}

	private void showUsing() {
		mStateButton.setText(R.string.state_button_use);
		mStateButton.setBackgroundResource(R.drawable.statebutton_using);
		mImageButtonCrash.setVisibility(View.GONE);
		mImageButtonSetting.setVisibility(View.VISIBLE);
	}

	private void initViewPager() {
		mPagerAdapter = new LocalThemeDetailPagerAdapter(
				getApplicationContext(), mInstallLocker);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOffscreenPageLimit(3);
		if (Build.VERSION.SDK_INT >= 11) {
			mViewPager.setPageMargin(1);
			mViewPager.setPageTransformer(true, new ZoomOutPageTransformer2());
		} else {
			mViewPager.setPageMargin(getResources().getDimensionPixelSize(
					R.dimen.page_margin));
		}
		mLinePageIndicator.setViewPager(mViewPager, 1);
		ViewPagerOnPageChangeListener mOnPageChangeListener = new ViewPagerOnPageChangeListener(
				viewPagerContainer);
		mLinePageIndicator.setOnPageChangeListener(mOnPageChangeListener);

		viewPagerContainer.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try {
					return mViewPager.dispatchTouchEvent(event);
				} catch (Exception e) {
					return false;
				}

			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	public void uninstallAPK(String packageName) {
		Uri uri = Uri.parse("package:" + packageName);
		Intent intent = new Intent(Intent.ACTION_DELETE, uri);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mViewPager.removeAllViews();
		mPagerAdapter = null;
		// ImageLoader.getInstance().cancelDisplayTask(imageAware)
		getContentResolver().unregisterContentObserver(mObserver);
		unregisterReceiver(themeInstallReciever);
		// add by Jack
		if (mImageButtonCrash != null) {
			mImageButtonCrash.onDestroy();
		}
		System.gc();

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		mViewPager.getLayoutParams().width = (int) (mViewPager.getHeight() * 500 / 888f);

	}

	private void showDeleteDialog() {
		if (mDeleteDialog == null) {
			CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(
					this);
			builder.setTitle(R.string.dialog_title_delete);
			builder.setMessage(R.string.theme_native_dialog_content);
			builder.setPositiveButton(
					getString(R.string.live_wapaper_native_dialog_yes),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (mInstallLocker != null) {
								uninstallAPK(mInstallLocker.packageName);
							}
						}
					});

			builder.setNegativeButton(
					getString(R.string.live_wapaper_native_dialog_no),
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			// add by Jack
			mDeleteDialog = builder.create();
			mDeleteDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					if (mImageButtonCrash != null) {
						mImageButtonCrash.resetStageView();
					}
				}
			});
			// end
		}
		if (!mDeleteDialog.isShowing()) {
			mDeleteDialog.show();
		}

	}

	@Override
	public void onClick(View v) {
		// if(v.equals(o))
		if (v.getTag().toString().equals(mImageButtonBack.getTag().toString())) {
			finish();
		} else if (v.getTag().toString()
				.equals(mImageButtonCrash.getTag().toString())) {
			showDeleteDialog();
		} else if (v.getTag().toString()
				.equals(mImageButtonSetting.getTag().toString())) {
			Intent intent = new Intent(this, ThemeSettingActivity.class);
			startActivity(intent);
		} else if (v.getTag().toString()
				.equals(mStateButton.getTag().toString())) {
			if (mInstallLocker.currentTheme) {
				return;
			} else {
				if (!SettingsHelper.getLockServiceEnable(this)) {
					SettingsHelper.setLockServiceState(this,
							Constants.C_SERVICE_ON);

					startService(new Intent(this, KeyguardService.class));

				}
				SettingsHelper
						.putCurrentTheme(this, mInstallLocker.packageName);
				mInstallLocker.currentTheme = true;
				mStateButton.setText(R.string.state_button_use);
				mStateButton
						.setBackgroundResource(R.drawable.statebutton_using);
				ToastMaster.makeText(this,
						R.string.settings_theme_or_wallpaper_success,
						Toast.LENGTH_SHORT);
				// hideIconOnLauncher(mInstallLocker.packageName);
				if (mInstallLocker.packageName
						.equals(Constants.SKY_LOCKER_DEFAULT_THEME)) {
					return;
				}
				if (Util.isIconShowable(mInstallLocker.packageName)) {
					Intent intent = new Intent(Intents.ACTION_HIDE_ICON);
					intent.putExtra("packageName", mInstallLocker.packageName);
					sendBroadcast(intent);
				}
				// Intent intent = new Intent(Intents.ACTION_RECREATE_DIALOG);
				// sendBroadcast(intent);
			}
		}
	}

	public void hideIconOnLauncher(String packageName) {
		if (packageName.equals(Constants.SKY_LOCKER_DEFAULT_THEME)) {
			return;
		}
		try {
			ComponentName disableComponentName = new ComponentName(packageName,
					"com.cyou.cma.clocker.themedepend.activity.MainActivity");
			getPackageManager().setComponentEnabledSetting(
					disableComponentName,
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
					PackageManager.DONT_KILL_APP);
		} catch (Exception e) {
			//e.printStackTrace();
		}

	}

}
