package com.cyou.cma.clockscreen.bean;

import android.content.Context;
import android.content.Intent;

import com.cyou.cma.clockscreen.applock.AppLockHelper;
import com.cyou.cma.clockscreen.password.PasswordHelper;
import com.cyou.cma.clockscreen.util.UiHelper;
import com.cyou.cma.clockscreen.util.Util;

public class SecurityBean {

	public static final int PASSWORD_TYPE = 1;
	public static final int APPLOCK_TYPE = 2;
	public static final int MAILBOX_TYPE = 3;
	public int level;
	public int resId;

	public SecurityBean(int level, int resId) {
		this.level = level;
		this.resId = resId;
	}

	public void gotoActivity(Context mContext) {
		// Context mContext = LockApplication.getInstance();
		switch (level) {
		case PASSWORD_TYPE:
			int unlockType = PasswordHelper.getUnlockType(mContext);
			int applockType = AppLockHelper.getAppLockType(mContext);
		 
			boolean hasEverSet1 = PasswordHelper.hasPasswordEverSet(mContext);
			if (!hasEverSet1&&Util.isNewUser(mContext)) {

				// 如果曾经设置过密码
				try {
					mContext.startActivity(new Intent(
							mContext,
							Class.forName(PasswordHelper.mConfirmClazzsNever
									.get(AppLockHelper.getAppLockType(mContext)))));
				} catch (ClassNotFoundException e) {
					Util.printException(e);
				}
			} else {

				try {
					mContext.startActivity(new Intent(mContext,
							Class.forName(PasswordHelper.mConfirmClazzs
									.get(unlockType))));
				} catch (ClassNotFoundException e) {
				}
			}
			break;
		case APPLOCK_TYPE:
			boolean hasEverSet = AppLockHelper.hasPasswordEverSet(mContext);
			if (hasEverSet) {
				try {
					mContext.startActivity(new Intent(
							mContext,
							Class.forName(AppLockHelper.mAppLockerConfirmClazzsEver
									.get(AppLockHelper.getAppLockType(mContext)))));
				} catch (ClassNotFoundException e) {
					Util.printException(e);
				}
			} else {
				// 判断是否启用锁屏密码
				try {
					mContext.startActivity(new Intent(
							mContext,
							Class.forName(AppLockHelper.mAppLockerConfirmClazzsNever
									.get(PasswordHelper.getUnlockType(mContext)))));
				} catch (ClassNotFoundException e) {
					Util.printException(e);
				}
			}
			break;
		case MAILBOX_TYPE:
			UiHelper.showMailboxDialog(mContext);
			break;
		}
	}
}
