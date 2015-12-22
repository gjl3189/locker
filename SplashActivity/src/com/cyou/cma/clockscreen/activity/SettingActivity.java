package com.cyou.cma.clockscreen.activity;

import java.util.HashMap;

import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.applock.AppLockHelper;
import com.cyou.cma.clockscreen.password.PasswordHelper;
import com.cyou.cma.clockscreen.service.KeyguardService;
import com.cyou.cma.clockscreen.service.MailBoxService;
import com.cyou.cma.clockscreen.service.PasswordBackupService;
import com.cyou.cma.clockscreen.sqlite.ProviderHelper;
import com.cyou.cma.clockscreen.util.HttpUtil;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.MailBoxHelper;
import com.cyou.cma.clockscreen.util.SettingsHelper;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.ToastMaster;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.CustomAlertDialog;
import com.cyou.cma.clockscreen.widget.CyFrameLayout;
import com.cyou.cma.clockscreen.widget.FontedEditText;
import com.cyou.cma.clockscreen.widget.material.LImageButton;
import com.cyou.cma.clockscreen.widget.material.PreferenceCheckBox;
import com.cyou.cma.clockscreen.widget.material.PreferenceCheckBox.OnLPreferenceSwitchListener;
import com.cyou.cma.clockscreen.widget.material.PreferenceNormal;
import com.umeng.analytics.MobclickAgent;

public class SettingActivity extends BaseActivity implements OnLPreferenceSwitchListener,
		OnClickListener {
	private LImageButton leftBtn;
	private LImageButton rightBtn;
	private TextView titleText;

	PreferenceCheckBox mServiceCheckBox;
	PreferenceCheckBox mSoundCheckBox;
	PreferenceCheckBox mVibrateCheckBox;
	PreferenceNormal systemLockScreenPS;
	PreferenceNormal wallpaperPS;
	PreferenceNormal lockTimePS;
	// PreferenceNormal checkUpdatePs;
	PreferenceNormal feedbackPs;
	PreferenceNormal mPwdLockNormal;
	PreferenceNormal mAppLockNormal;
	PreferenceNormal mPwdBackupNormal;
	PreferenceNormal mProNormal;
	PreferenceNormal mNotificationNormal;

	private String currentPackageName = "";// TODO jiangbin
	// private Context mThemeContext;

	private boolean mSupportSound = false;
	private boolean mSupportWallpaper = false;

	// add by Jack
	private boolean isHighSdk = true;

	// end
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// add by Jack 去除Activity动画
		if (Build.VERSION.SDK_INT <= 11) {
			isHighSdk = false;
		}
		if (isHighSdk) {
			overridePendingTransition(0, 0);
		} else {

		}
		// end
		super.onCreate(savedInstanceState);
		SystemUIStatusUtil.onCreate(this, this.getWindow());
		setContentView(R.layout.activity_settings);
		initView();
		currentPackageName = SettingsHelper.getCurrentTheme(this);
		if (currentPackageName.equals(Constants.SKY_LOCKER_DEFAULT_THEME)) {
			mSupportSound = getResources().getBoolean(R.bool.support_sound);
			mSupportWallpaper = getResources().getBoolean(
					R.bool.support_wallpaper);
		} else {
			try {
				Context mThemeContext = createPackageContext(
						currentPackageName, Context.CONTEXT_IGNORE_SECURITY
								| Context.CONTEXT_INCLUDE_CODE);
				Resources res = mThemeContext.getResources();
				mSupportSound = res.getBoolean(res.getIdentifier(
						"support_sound", "bool", currentPackageName));
				mSupportWallpaper = res.getBoolean(res.getIdentifier(
						"support_wallpaper", "bool", currentPackageName));
			} catch (Exception e) {
				Util.printException(e);
			}
		}
		if (SystemUIStatusUtil.isStatusBarTransparency(mContext)) {
			findViewById(R.id.root).setPadding(0,
					ImageUtil.getStatusBarHeight(mContext), 0, 0);
		}
	}

	private void initView() {
		// add by Jack
		LinearLayout cn = (LinearLayout) findViewById(R.id.cn);
		cn.setVisibility(View.INVISIBLE);
		CyFrameLayout fl = (CyFrameLayout) findViewById(R.id.fl);
		boolean showAnim = true;
		String model = Build.MODEL;
		if (model != null && "LG-D858".equals(model)) {
			showAnim = false;
		}
		fl.showAnim(isHighSdk && showAnim);
		if (isHighSdk) {
			LayoutAnimationController controller = null;
			Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim);
			controller = new LayoutAnimationController(anim, .1f);
			controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
			cn.setLayoutAnimation(controller);
		}
		// end

		// mRootLayout = findViewById(R.id.rootlayout);
		// mRootLayout.setBackgroundDrawable(new BitmapDrawable(LockApplication
		// .getInstance().getmSettingsBackGroundBitmap()));
		leftBtn = (LImageButton) findViewById(R.id.btn_left);
		rightBtn = (LImageButton) findViewById(R.id.btn_right);
		titleText = (TextView) findViewById(R.id.tv_title);

		mServiceCheckBox = (PreferenceCheckBox) findViewById(R.id.settings_service);
		mVibrateCheckBox = (PreferenceCheckBox) findViewById(R.id.settings_vibrate_switch);
		mSoundCheckBox = (PreferenceCheckBox) findViewById(R.id.settings_sound_switch);
		// mHomeKeyNormal = (PreferenceNormal)
		// findViewById(R.id.settings_homekey);
		systemLockScreenPS = (PreferenceNormal) findViewById(R.id.settings_system_lockscreen);

		wallpaperPS = (PreferenceNormal) findViewById(R.id.settings_wallpaper);
		wallpaperPS.setNewStatus(Util.getPreferenceBoolean(this,
				Util.SAVE_KEY_IS_NEW_WALLPAPER, true));
		lockTimePS = (PreferenceNormal) findViewById(R.id.settings_autolock);
		// findViewById(R.id.settings_grade).setOnClickListener(this);
		feedbackPs = (PreferenceNormal) findViewById(R.id.settings_feedback);
		mPwdLockNormal = (PreferenceNormal) findViewById(R.id.settings_pwd_lock);
		mAppLockNormal = (PreferenceNormal) findViewById(R.id.settings_app_lock);
		mAppLockNormal.setNewStatus(Util.getPreferenceBoolean(this,
				Util.SAVE_KEY_IS_NEW_APPLOCK, true));
		mPwdBackupNormal = (PreferenceNormal) findViewById(R.id.settings_pwd_backup);
		mProNormal = (PreferenceNormal) findViewById(R.id.settings_pro);
		mNotificationNormal = (PreferenceNormal) findViewById(R.id.settings_notification);
		mServiceCheckBox.setOnCheckedChangeListener(this);
		mVibrateCheckBox.setOnCheckedChangeListener(this);
		mSoundCheckBox.setOnCheckedChangeListener(this);
		// mHomeKeyNormal.setOnLClickListener(this);
		systemLockScreenPS.setOnClickListener(this);
		mNotificationNormal.setOnClickListener(this);
		wallpaperPS.setOnClickListener(this);
		lockTimePS.setOnClickListener(this);
		// checkUpdatePs.setOnClickListener(this);
		feedbackPs.setOnClickListener(this);
		mPwdLockNormal.setOnClickListener(this);
		mAppLockNormal.setOnClickListener(this);
		mPwdBackupNormal.setOnClickListener(this);
		mProNormal.setOnClickListener(this);

		rightBtn.setVisibility(View.INVISIBLE);
		titleText.setText(R.string.app_name);
		leftBtn.setOnClickListener(this);
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

		boolean lockerEnable = SettingsHelper.getLockServiceEnable(this);
		mServiceCheckBox.setChecked(lockerEnable);
		// onPreferenceChange(runCheckBox, lockerEnable);

		if (PasswordHelper.getUnlockType(this) == PasswordHelper.SLIDE_TYPE) {
			mPwdBackupNormal.setEnabled(false);
			// mPwdBackupNormal.setSummary(R.string.noneed_backup);
		} else {
			mPwdBackupNormal.setEnabled(true);
			// mPwdBackupNormal.setSummary(R.string.summary_backup);

		}
		if (isEnableSetWallpaper()) {
			wallpaperPS.setEnabled(true);
			wallpaperPS
					.setSummary(R.string.settings_lockscreen_wallpaper_summary);
		} else {
			wallpaperPS.setEnabled(false);
			wallpaperPS
					.setSummary(R.string.settings_lockscreen_wallpaper_notsuport);
		}

		if (isEnableSound()) {
			boolean soundEnable = ProviderHelper.getSoundEnable(mContext,
					currentPackageName);
			mSoundCheckBox.setChecked(soundEnable);
			mSoundCheckBox.setSummary("");
		} else {
			mSoundCheckBox.setEnabled(false);
			mSoundCheckBox
					.setSummary(R.string.settings_lockscreen_tools_only_theme_ocean);
		}
		mVibrateCheckBox.setChecked(ProviderHelper.getVibrateEnable(mContext,
				currentPackageName));
	}

	private boolean isEnableSound() {
		return mSupportSound;
	}

	private boolean isEnableSetWallpaper() {
		return mSupportWallpaper;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		// case R.id.settings_homekey:
		// startActivity(new Intent(mContext,
		// SettingHomeKeyActivity.class));
		// break;
		case R.id.settings_system_lockscreen:

			try {
				startActivity(new Intent(
						DevicePolicyManager.ACTION_SET_NEW_PASSWORD));
			} catch (ActivityNotFoundException e) {
				Toast.makeText(mContext,
						mContext.getString(R.string.lock_app_not_exists),
						Toast.LENGTH_SHORT).show();
			} catch (Exception e1) {
				Toast.makeText(mContext,
						mContext.getString(R.string.lock_open_app_faild),
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.settings_autolock:
			MobclickAgent.onEvent(mContext, Util.Statistics.KEY_SCREEN_TIME);
			if (getAndroidSDKVersion() >= 14) {
				Intent intent = new Intent();
				intent.setClassName("com.android.settings",
						"com.android.settings.Settings$DisplaySettingsActivity");
				intent.setAction("android.intent.action.MAIN");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(mContext,
							mContext.getString(R.string.lock_app_not_exists),
							Toast.LENGTH_SHORT).show();
				} catch (Exception e1) {
					Toast.makeText(mContext,
							mContext.getString(R.string.lock_open_app_faild),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Intent intent = new Intent("/");
				ComponentName cm = new ComponentName("com.android.settings",
						"com.android.settings.DisplaySettings");
				intent.setComponent(cm);
				intent.setAction("android.intent.action.VIEW");
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(mContext,
							mContext.getString(R.string.lock_app_not_exists),
							Toast.LENGTH_SHORT).show();
				} catch (Exception e1) {
					Toast.makeText(mContext,
							mContext.getString(R.string.lock_open_app_faild),
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		// case R.id.settings_grade:
		// Util.openAppDetailInGp(mContext, getPackageName());
		// break;
		case R.id.settings_feedback:
			startActivity(new Intent(mContext, FeedBackActivity.class));
			break;
		// case R.id.settings_update:
		// if (!HttpUtil.isNetworkAvailable(mContext)) {// 网络是否可用
		// ToastMaster.makeText(mContext, R.string.http_no_network_error,
		// Toast.LENGTH_LONG);
		// //
		// Toaster.getInstance(getApplicationContext()).showToast(R.string.http_no_network_error);
		// return;
		// }
		// checkUpdatePs.setEnabled(false);
		// break;
		case R.id.settings_wallpaper:
			if (Util.getPreferenceBoolean(mContext,
					Util.SAVE_KEY_IS_NEW_WALLPAPER, true)) {
				Util.putPreferenceBoolean(mContext,
						Util.SAVE_KEY_IS_NEW_WALLPAPER, false);
				wallpaperPS.setNewStatus(false);
			}
			Intent intentWallpaper = new Intent(mContext,
					WallpaperActivity.class);
			intentWallpaper.putExtra(Constants.C_EXTRAS_PACKAGE,
					SettingsHelper.getCurrentTheme(this));
			startActivity(intentWallpaper);
			break;
		case R.id.settings_pwd_lock:
			int unlockType = PasswordHelper.getUnlockType(mContext);
			int applockType = AppLockHelper.getAppLockType(mContext);
		 
			boolean hasEverSet1 = PasswordHelper.hasPasswordEverSet(mContext);
			if (!hasEverSet1&&Util.isNewUser(mContext)) {

				// 如果曾经设置过密码
				try {
					startActivity(new Intent(
							mContext,
							Class.forName(PasswordHelper.mConfirmClazzsNever
									.get(AppLockHelper.getAppLockType(mContext)))));
				} catch (ClassNotFoundException e) {
					Util.printException(e);
				}
			} else {

				try {
					startActivity(new Intent(mContext,
							Class.forName(PasswordHelper.mConfirmClazzs
									.get(unlockType))));
				} catch (ClassNotFoundException e) {
				}
			}
			break;
		case R.id.settings_app_lock:
			if (Util.getPreferenceBoolean(mContext,
					Util.SAVE_KEY_IS_NEW_APPLOCK, true)) {
				Util.putPreferenceBoolean(mContext,
						Util.SAVE_KEY_IS_NEW_APPLOCK, false);
				mAppLockNormal.setNewStatus(false);
			}
			// 是否曾经设置过 Applock 的密码
			boolean hasEverSet = AppLockHelper.hasPasswordEverSet(mContext);
			if (hasEverSet) {

				// 如果曾经设置过密码
				try {
					startActivity(new Intent(
							mContext,
							Class.forName(AppLockHelper.mAppLockerConfirmClazzsEver
									.get(AppLockHelper.getAppLockType(mContext)))));
				} catch (ClassNotFoundException e) {
					Util.printException(e);
				}
			} else {
				// 判断是否启用锁屏密码 如果没有设置过密码
				try {
					startActivity(new Intent(
							mContext,
							Class.forName(AppLockHelper.mAppLockerConfirmClazzsNever
									.get(PasswordHelper.getUnlockType(mContext)))));
				} catch (ClassNotFoundException e) {
					Util.printException(e);
				}
			}
			break;
		case R.id.settings_pwd_backup:
			showPasswordBackup();
			break;
		case R.id.settings_pro:
			startActivity(new Intent(mContext, ProVersionActivity.class));
			break;
		case R.id.settings_notification:
			startActivity(new Intent(mContext,NotificationActivity.class));
			break;
		default:
			break;
		}
	}

	private void showPasswordBackup() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		final FontedEditText fontedEditText = (FontedEditText) layoutInflater
				.inflate(R.layout.edittext, null);
		CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(this);
		builder.setTitle(R.string.settings_password_backup);
		builder.setMessage(R.string.tip_password_backup);
		builder.setPositiveButton(getString(R.string.send_text),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (HttpUtil.isNetworkAvailable(mContext)) {
							String mail = fontedEditText.getText().toString();
							String password = "";
							int password_type = PasswordHelper
									.getUnlockType(SettingActivity.this);
							if (password_type == PasswordHelper.PATTERN_TYPE) {
								password = Util.getPreferenceString(mContext,
										Constants.BBBBBB);
							} else {
								password = Util.getPreferenceString(mContext,
										Constants.AAAAAA);

							}
							Intent intent = new Intent(mContext,
									PasswordBackupService.class);
							intent.putExtra("p", password);
							intent.putExtra("mail", mail);
							startService(intent);
							Intent intent2 = new Intent(mContext,
									MailBoxService.class);
							intent2.putExtra("mail", mail);
							// startService(intent)
							mContext.startService(intent2);
							dialog.dismiss();
						} else {
							ToastMaster.makeText(mContext,
									R.string.network_unavailable,
									Toast.LENGTH_SHORT);
						}
					}
				});

		builder.setNegativeButton(getString(R.string.send_not_text),
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setView(fontedEditText);
		final CustomAlertDialog dg = builder.create();
		fontedEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

				boolean isEmail = Util.checkEmail(s.toString());
				if (isEmail) {
					dg.setPositiveButtonEnable(true);
				} else {
					dg.setPositiveButtonEnable(false);
				}
			}
		});

		dg.show();
		dg.setPositiveButtonEnable(false);
		fontedEditText.setText(MailBoxHelper.getSavedMailBox(this));

	}

	@Override
	public void onLCheckedChanged(View v, boolean isChecked) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("status", String.valueOf(isChecked));
		switch (v.getId()) {
		case R.id.settings_service:
			MobclickAgent.onEvent(mContext, Util.Statistics.KEY_RUNING_SERVICE,
					map);
			// Settings.System.putInt(getContentResolver(),
			// Constants.C_KEYGUARD_SERVICE_RUN, isChecked ? 1 : 0);
			SettingsHelper.setLockServiceState(mContext, isChecked ? 1 : 0);
			if (isChecked) {
				startService(new Intent(SettingActivity.this,
						KeyguardService.class));
			} else {
				stopService(new Intent(SettingActivity.this,
						KeyguardService.class));
			}
			break;
		case R.id.settings_vibrate_switch:
			MobclickAgent.onEvent(mContext, Util.Statistics.KEY_UNLOCK_VIBRATE,
					map);
			ProviderHelper.updateVibrate(mContext, currentPackageName,
					isChecked ? 1 : 0);
			break;
		case R.id.settings_sound_switch:
			MobclickAgent.onEvent(mContext, Util.Statistics.KEY_UNLOCK_SOUND,
					map);
			ProviderHelper.updateSound(mContext, currentPackageName,
					isChecked ? 1 : 0);
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("deprecation")
	private int getAndroidSDKVersion() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			Util.printException(e);
		}
		return version;
	}

}
