package com.cyou.cma.clockscreen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.applock.AppLockHelper;
import com.cyou.cma.clockscreen.event.MailboxEvent;
import com.cyou.cma.clockscreen.event.SecurityCompleteEvent;
import com.cyou.cma.clockscreen.password.PasswordHelper;
import com.cyou.cma.clockscreen.util.CyGuideHelper;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.MailBoxHelper;
import com.cyou.cma.clockscreen.util.SettingsHelper;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.UiHelper;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.material.PreferenceStatus;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

public class SecuritySettingActivity extends BaseActivity implements
		OnClickListener {

	private PreferenceStatus mLockscreenStatus;
	private PreferenceStatus mApplockStatus;
	private PreferenceStatus mMailStatus;
	private TextView mHintTextView;
	private ImageView mLevelImageView;
	private int[] mLevelRes = new int[] { R.drawable.secirotysettings_level_d,
			R.drawable.secirotysettings_level_c,
			R.drawable.secirotysettings_level_b,
			R.drawable.secirotysettings_level_a };
	private boolean mNeedPushUp;

	// BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
	//
	// @Override
	// public void onReceive(Context arg0, Intent arg1) {
	// if (arg1.getAction().equals((Intents.ACTION_MAILBOX))) {
	// mMailStatus.setChecked(MailBoxHelper.hasSavedMailBox(arg0));
	// }
	// }
	//
	// };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SystemUIStatusUtil.onCreate(this, this.getWindow());
		CyGuideHelper.hasShow(this);
		mNeedPushUp = getIntent().getBooleanExtra(
				Constants.C_EXTRAS_NEED_PUSH_UP, false);
		setContentView(R.layout.activity_security_setting);
		initView();
		if (SystemUIStatusUtil.isStatusBarTransparency(mContext)) {
			findViewById(R.id.root).setPadding(0,
					ImageUtil.getStatusBarHeight(mContext), 0, 0);
		}
		// IntentFilter filter = new IntentFilter(Intents.ACTION_MAILBOX);
		// registerReceiver(broadcastReceiver, filter);

		EventBus.getDefault().register(this);
	}

	public void onEventMainThread(MailboxEvent event) {
		mMailStatus.setChecked(MailBoxHelper.hasSavedMailBox(this));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// unregisterReceiver(broadcastReceiver);
		EventBus.getDefault().unregister(this);
		if (mNeedPushUp) {
			EventBus.getDefault().post(new SecurityCompleteEvent());
		}
	}

	private void initView() {
		findViewById(R.id.btn_left).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_title))
				.setText(R.string.security_setting);
		mLevelImageView = (ImageView) findViewById(R.id.secirotysettings_level);
		mHintTextView = (TextView) findViewById(R.id.secirotysettings_hint_level);
		mLockscreenStatus = (PreferenceStatus) findViewById(R.id.secirotysettings_lockscreen);
		mApplockStatus = (PreferenceStatus) findViewById(R.id.secirotysettings_applock);
		mMailStatus = (PreferenceStatus) findViewById(R.id.secirotysettings_mail);
		mLockscreenStatus.setOnClickListener(this);
		mApplockStatus.setOnClickListener(this);
		mMailStatus.setOnClickListener(this);
	}

	private void refresh() {
		boolean lockscreenStatus = PasswordHelper.getUnlockType(mContext) != PasswordHelper.SLIDE_TYPE;
		boolean applockStatus = SettingsHelper.getApplockEnable(mContext);
		boolean mailStatus = MailBoxHelper.hasSavedMailBox(mContext);
		mLockscreenStatus.setChecked(lockscreenStatus);
		mApplockStatus.setChecked(applockStatus);
		mMailStatus.setChecked(mailStatus);
		int level = 0;
		level += lockscreenStatus ? 1 : 0;
		level += applockStatus ? 1 : 0;
		level += mailStatus ? 1 : 0;
		mLevelImageView.setImageResource(mLevelRes[level]);
		if (level == 3) {
			mHintTextView.setVisibility(View.GONE);
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
		refresh();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.secirotysettings_lockscreen:
			int unlockType = PasswordHelper.getUnlockType(mContext);

			try {
				startActivity(new Intent(mContext,
						Class.forName(PasswordHelper.mConfirmClazzs
								.get(unlockType))));
			} catch (ClassNotFoundException e) {
			}
			break;
		case R.id.secirotysettings_applock:
			boolean hasEverSet = AppLockHelper.hasPasswordEverSet(mContext);
			if (hasEverSet) {
				try {
					startActivity(new Intent(
							mContext,
							Class.forName(AppLockHelper.mAppLockerConfirmClazzsEver
									.get(AppLockHelper.getAppLockType(mContext)))));
				} catch (ClassNotFoundException e) {
					Util.printException(e);
				}
			} else {
				// 判断是否启用锁屏密码
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
		case R.id.secirotysettings_mail:
			// TODO 跳转密保邮箱
			// Intent intent = new Intent(SecuritySettingActivity.this,
			// ChooseLockPattern.class);
			// intent.putExtra("type", LockPatternUtils.LOCKSCREEN_TYPE);
			// startActivity(intent);
			UiHelper.showMailboxDialog(mContext);
			break;
		default:
			break;
		}
	}

}
