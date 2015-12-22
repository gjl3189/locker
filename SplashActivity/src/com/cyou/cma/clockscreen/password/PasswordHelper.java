package com.cyou.cma.clockscreen.password;

import com.cyou.cma.clockscreen.applock.AppLockHelper;
import com.cyou.cma.clockscreen.util.Util;

import android.content.Context;
import android.util.SparseArray;

public class PasswordHelper {
	public static final String UNLOCK_TYPE = "unlock_type";// 锁屏的方式 slide 图案解锁
															// 数字密码解锁
	public static final int SLIDE_TYPE = 0;// 无密码
	public static final int PATTERN_TYPE = 2;// 图案 模式
	public static final int PASSWORD_TYPE = 1;// pin 码模式

	public static SparseArray<String> mClazzHashMap = new SparseArray<String>();
	/** 锁屏 密码邮箱 **/
	static {
		mClazzHashMap.put(PASSWORD_TYPE,
				"com.cyou.cma.clockscreen.password.LockerPinViewInflater");
		mClazzHashMap.put(PATTERN_TYPE,
				"com.cyou.cma.clockscreen.password.LockerPatternViewInflater");
	}

	/** 点击设置界面 **/
	public static SparseArray<String> mConfirmClazzs = new SparseArray<String>();

	static {
		mConfirmClazzs.put(SLIDE_TYPE,
				"com.cyou.cma.clockscreen.activity.PwdSettingActivity");
		mConfirmClazzs
				.put(PASSWORD_TYPE,
						"com.cyou.cma.clockscreen.activity.ConfirmPinPasswordLockscreen");
		mConfirmClazzs.put(PATTERN_TYPE,
				"com.cyou.cma.clockscreen.activity.ConfirmLockPattern");
	}
	
	public static final String HAS_EVER_SET_PASSWORD="has_ever_set_password";
	public static SparseArray<String> mConfirmClazzsNever = new SparseArray<String>();
	static {
		mConfirmClazzsNever.put(AppLockHelper.NONE, "com.cyou.cma.clockscreen.activity.PwdSettingActivity");
		mConfirmClazzsNever.put(AppLockHelper.PIN_APP_LOCKER, "com.cyou.cma.clockscreen.activity.ConfirmPinPasswordApplock4lLockScreen");
		mConfirmClazzsNever.put(AppLockHelper.PATTERN_APP_LOCKER, "com.cyou.cma.clockscreen.activity.ConfirmAppLockPattern4Lock");
		
	}

	  public static void setPasswordEverSet(boolean set, Context context) {
	        Util.putPreferenceBoolean(context, HAS_EVER_SET_PASSWORD, set);
	    }

	    public static boolean hasPasswordEverSet(Context context) {
	        return Util.getPreferenceBoolean(context, HAS_EVER_SET_PASSWORD, false);
	    }
	public static void setUnlockType(int unlockType, Context context) {
		Util.putPreferenceInt(context, UNLOCK_TYPE, unlockType);
	}

	public static int getUnlockType(Context context) {
		return Util.getPreferenceInt(context, UNLOCK_TYPE, SLIDE_TYPE);
	}

}
