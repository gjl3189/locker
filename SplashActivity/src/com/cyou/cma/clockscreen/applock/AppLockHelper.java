package com.cyou.cma.clockscreen.applock;

import com.cyou.cma.clockscreen.password.PasswordHelper;
import com.cyou.cma.clockscreen.util.Util;

import android.content.Context;
import android.util.SparseArray;

public class AppLockHelper {
	/**从来没有设置过**/
    public final static int NONE = 0;
    /**Pin password**/
    public final static int PIN_APP_LOCKER = 1;
    /**Pattern password**/
    public final static int PATTERN_APP_LOCKER = 2;
//    public final static int PIN_APP_LOCKER_DEFAULT = 3;//

    /**
     * app lock 密码是否设置过
     */
    public final static String APP_LOCKER_PASSWORD_EVER_SET = "app_locker_password_ever_set";

    public final static String APP_LOCKER_TYPE = "app_locker_type";

    /**
     * 锁屏界面的 pattern 密码锁
     */
    public static SparseArray<String> mAppLockerClazzs = new SparseArray<String>();
    static {
        mAppLockerClazzs.put(PIN_APP_LOCKER,
                "com.cyou.cma.clockscreen.applock.StartPinAppLockActivity");
//        mAppLockerClazzs.put(PIN_APP_LOCKER_DEFAULT,
//                "com.cyou.cma.clockscreen.applock.StartPinAppLockActivity");
        mAppLockerClazzs.put(PATTERN_APP_LOCKER,
                "com.cyou.cma.clockscreen.applock.StartPatternAppLockActvity");
    }
    
    
    public static SparseArray<String> mAppLockerConfirmClazzsNever = new SparseArray<String>();
  //如果没有设置过应用锁 就根据 锁屏密码的类型类 跳转相应的界面 
    static {
    	//如果锁屏密码也没有设置过就跳转到 applock 默认的密码界面
        mAppLockerConfirmClazzsNever.put(PasswordHelper.SLIDE_TYPE,
                "com.cyou.cma.clockscreen.activity.ConfirmPinPasswordAppLockDefault");
        
        //如果锁屏密码设置的是 pin码 就跳转到pin 码 
        mAppLockerConfirmClazzsNever.put(PasswordHelper.PASSWORD_TYPE,
                "com.cyou.cma.clockscreen.activity.ConfirmPinPasswordLockscreen4AppLock");
//        mAppLockerConfirmClazzsNever.put(PIN_APP_LOCKER_DEFAULT,
//                "com.cyou.cma.clockscreen.activity.ConfirmPinPasswordLockscreen4AppLock");
        mAppLockerConfirmClazzsNever.put(PasswordHelper.PATTERN_TYPE,
                "com.cyou.cma.clockscreen.activity.ConfirmLockPattern4AppLock");
    }

    public static SparseArray<String> mAppLockerConfirmClazzsEver = new SparseArray<String>();
    //如果设置过 其实就是分为两种情况 一种是pin 一种是Pattern
    static {
        mAppLockerConfirmClazzsEver.put(PIN_APP_LOCKER,
                "com.cyou.cma.clockscreen.activity.ConfirmPinPasswordApplock");
//        mAppLockerConfirmClazzsEver.put(PIN_APP_LOCKER_DEFAULT,
//                "com.cyou.cma.clockscreen.activity.ConfirmPinPasswordAppLockDefault2");
        mAppLockerConfirmClazzsEver.put(PATTERN_APP_LOCKER,
                "com.cyou.cma.clockscreen.activity.ConfirmAppLockPattern");
    }

    public static void setAppLockType(int unlockType, Context context) {
        Util.putPreferenceInt(context, APP_LOCKER_TYPE, unlockType);
    }

    public static int getAppLockType(Context context) {
        int type= Util.getPreferenceInt(context, APP_LOCKER_TYPE, NONE);// Test
        if(type == 3){
        	type=PIN_APP_LOCKER;
        }
        return type;
    }

    public static void setPasswordEverSet(boolean set, Context context) {
        Util.putPreferenceBoolean(context, APP_LOCKER_PASSWORD_EVER_SET, set);
    }

    public static boolean hasPasswordEverSet(Context context) {
        return Util.getPreferenceBoolean(context, APP_LOCKER_PASSWORD_EVER_SET, false);
    }
}
