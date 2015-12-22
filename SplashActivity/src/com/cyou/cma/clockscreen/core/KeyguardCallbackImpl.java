package com.cyou.cma.clockscreen.core;

import android.app.PendingIntent;

import com.cyou.cma.clocker.apf.KeyguardCallback;
import com.cyou.cma.clockscreen.activity.LockScreenDialog;
import com.cyou.cma.clockscreen.activity.NotificationActivity;
import com.cyou.cma.clockscreen.notification.KeyguardSetting;
import com.cyou.cma.clockscreen.password.PasswordHelper;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.Util;

public class KeyguardCallbackImpl implements KeyguardCallback {
	public static final int UNLOCK_NORMAL = 0;
	public static final int UNLOCK_PHONE = 1;
	public static final int UNLOCK_MESSAGE = 2;
	public static final int UNLOCK_CAMERA = 3;
	public static final int UNLOCK_LOCKER_SETTING = 4;
	public static final int UNLOCK_APPLICATION = 5;
	public static final int UNLOCK_CALL_CONTACT = 6;
	public static final int UNLOCK_BY_NOTIFICATION = 7;
	public static final int UNLOCK_NOTIFICATION = 8;
	private LockScreenDialog mLockScreenDialog;

	public KeyguardCallbackImpl(LockScreenDialog lockScreenDialog) {
		this.mLockScreenDialog = lockScreenDialog;
	}

	@Override
	public void unlock() {
		mLockScreenDialog.setUnlockType(UNLOCK_NORMAL, -1);

		checkSecurity(UNLOCK_NORMAL, -1);
	}

	@Override
	public void unlock2Camera() {
		mLockScreenDialog.setUnlockType(UNLOCK_CAMERA, -1);
		checkSecurity(UNLOCK_CAMERA, -1);
	}

	@Override
	public void unlock2Message() {
		mLockScreenDialog.setUnlockType(UNLOCK_MESSAGE, -1);
		checkSecurity(UNLOCK_MESSAGE, -1);
	}

	public void unlock2LockerSetting() {
		mLockScreenDialog.setUnlockType(UNLOCK_LOCKER_SETTING, -1);
		checkSecurity(UNLOCK_LOCKER_SETTING, -1);
	}

	public void unlock2Application() {
		mLockScreenDialog.setUnlockType(UNLOCK_APPLICATION, -1);
		checkSecurity(UNLOCK_APPLICATION, -1);
	}

	public void unlock2CallContact() {
		mLockScreenDialog.setUnlockType(UNLOCK_CALL_CONTACT, -1);
		checkSecurity(UNLOCK_CALL_CONTACT, -1);
	}

	public void unlockByNotification(PendingIntent pendingIntent) {
		mLockScreenDialog.setUnlockType(UNLOCK_BY_NOTIFICATION, pendingIntent);
		checkSecurity(UNLOCK_BY_NOTIFICATION, -1);

	}
	public void unlock2NotificationSetting(){
		Util.putPreferenceBoolean(mLockScreenDialog.getContext(), Util.HAS_SHOW_SWIPE_TIP, true);
		mLockScreenDialog.setUnlockType(UNLOCK_NOTIFICATION, -1);
		checkSecurity(UNLOCK_BY_NOTIFICATION, -1);
	}

	private void checkSecurity(int type, int arg) {
		int unlockType = PasswordHelper.getUnlockType(mLockScreenDialog
				.getContext());
		if (unlockType == PasswordHelper.SLIDE_TYPE) {
			mLockScreenDialog.onSecureSuccess();

			mLockScreenDialog.dismiss();

		} else {
			mLockScreenDialog.showPasswordScreen(PasswordHelper.mClazzHashMap
					.get(unlockType));
		}
	}

	@Override
	public void unlock2Phone(int arg0) {
		mLockScreenDialog.setUnlockType(UNLOCK_PHONE, arg0);
		checkSecurity(UNLOCK_PHONE, arg0);
	}

	@Override
	public boolean isStatusBarTransparency() {
		return SystemUIStatusUtil.isStatusBarTransparency(mLockScreenDialog
				.getContext());
	}

	@Override
	public String getPackageName() {
		return mLockScreenDialog.getContext().getPackageName();
	}

	/**
	 * 语义有变化，以前是判断是 否有密码解锁，如果有密码解锁，主题需要模糊壁纸，以备使用 现在 还需要考虑是否开启了 消息通知功能 如果
	 * 开启了消息通知功能也认为是需要模糊的
	 */
	@Override
	public boolean isSecure() {

		if (!Util.DEBUG) {
			boolean passwordable = PasswordHelper
					.getUnlockType(mLockScreenDialog.getContext()) != PasswordHelper.SLIDE_TYPE;
			boolean appNotifictionAble = KeyguardSetting
					.isAccessibilityEnable(mLockScreenDialog.getContext());
			boolean userNOtificationAble = Util.getPreferenceBoolean(
					mLockScreenDialog.getContext(),
					NotificationActivity.NOTIFICATION_CHECK, false);
			boolean notshow=!Util
			.getPreferenceBoolean(mLockScreenDialog.getContext(), Util.HAS_SHOW_SWIPE_TIP, false);
			return passwordable || (userNOtificationAble && appNotifictionAble||notshow);
		} else {
			return true;
		}
	}

}
