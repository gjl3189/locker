package com.cyou.cma.clockscreen.util;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.cyou.cma.clockscreen.Constants;

/**
 * 设置项的帮助类
 * 
 * @author jiangbin
 */
public class SettingsHelper {
    /**
     * 获取当前主题包名
     * 
     * @return
     */
    public static String getCurrentTheme(Context context) {
        String currentTheme = Settings.System.getString(context.getContentResolver(),
                Constants.C_THEME_PACKAGE);
        if (TextUtils.isEmpty(currentTheme)) {
            currentTheme = Constants.SKY_LOCKER_DEFAULT_THEME;
            putCurrentTheme(context, Constants.SKY_LOCKER_DEFAULT_THEME);
        }
        if (!currentTheme.equals(Constants.SKY_LOCKER_DEFAULT_THEME)) {
            boolean installed = Util.appInstalled(currentTheme);
            if (!installed) {
                currentTheme = Constants.SKY_LOCKER_DEFAULT_THEME;
                putCurrentTheme(context, Constants.SKY_LOCKER_DEFAULT_THEME);
            }
        }
        return currentTheme;
    }

    /**
     * 设置当前主题包名
     * 
     * @param context
     */
    public static void putCurrentTheme(Context context, String packageName) {
        Settings.System.putString(context.getContentResolver(),
                Constants.C_THEME_PACKAGE, packageName);
        //解决在非程序界面 卸载主题 
//        Intent intent = new Intent(Intents.ACTION_RECREATE_DIALOG);
//        context.sendBroadcast(intent);
    }

    /**
     * 获取锁屏服务开关状态
     * 
     * @param context
     * @return true开启 false 未开启
     */
    public static boolean getLockServiceEnable(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Constants.C_KEYGUARD_SERVICE_RUN, Constants.C_SERVICE_OFF) == Constants.C_SERVICE_ON;
    }

    /**
     * 设置锁屏服务开关
     * 
     * @param context
     * @return
     */
    public static void setLockServiceState(Context context, int state) {
        Settings.System.putInt(context.getContentResolver(),
                Constants.C_KEYGUARD_SERVICE_RUN, state);
    }

    public static boolean getApplockEnable(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Constants.APP_LOCK_SERVICE_RUN,
                Constants.C_SERVICE_OFF) == Constants.C_SERVICE_ON;
    }

    public static void setApplockEnable(Context context, int state) {
        Settings.System.putInt(context.getContentResolver(),
                Constants.APP_LOCK_SERVICE_RUN, state);
    }


}
