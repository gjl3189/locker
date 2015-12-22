package com.cyou.cma.clockscreen.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;

public class SystemUIStatusUtil {
    private final static int OSTYPE_NOT_SUPPORT = -1;
    private final static int OSTYPE_UNKNOW = 0;
    private final static int OSTYPE_SAMSUNG = 1;
    private final static int OSTYPE_SONY = 2;
    private final static int OSTYPE_MEIZU = 3;
    private final static int OSTYPE_KITKAT = 4;
    private static int osType = OSTYPE_UNKNOW;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void onCreate(Context context, Window window) {

        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            // 透明状态栏
            window.addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            // activity.getWindow().addFlags(
            // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            Util.putPreferenceInt(context, Util.SAVE_KEY_OS_TYPE, OSTYPE_KITKAT);
        } else {
            osType = Util.getPreferenceInt(context, Util.SAVE_KEY_OS_TYPE, OSTYPE_UNKNOW);
            switch (getOsType(context)) {
                case OSTYPE_NOT_SUPPORT:
                    // if (Build.VERSION.SDK_INT >=
                    // Build.VERSION_CODES.HONEYCOMB) {
                    // changeSystemUIFlag(activity,
                    // View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                    // }
                    break;
                case OSTYPE_SAMSUNG:
                case OSTYPE_SONY:
                    applyTransparentStatusBar(context, window);
                    break;
                case OSTYPE_MEIZU:
                    applyTransparentMeizu(window);
                    break;
                case OSTYPE_UNKNOW:
                    if (applyTransparentMeizu(window)) {
                        osType = OSTYPE_MEIZU;
                    } else {
                        osType = OSTYPE_NOT_SUPPORT;
                        // if (Build.VERSION.SDK_INT >=
                        // Build.VERSION_CODES.HONEYCOMB) {
                        // changeSystemUIFlag(activity,
                        // View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                        // }
                    }
                    Util.putPreferenceInt(context, Util.SAVE_KEY_OS_TYPE, osType);
                    break;
                default:
                    break;
            }
        }
    }

    public static int getOsType(Context context) {
        if (osType != OSTYPE_UNKNOW)
            return osType;
        String[] libs = context.getPackageManager().getSystemSharedLibraryNames();
        if (libs == null || libs.length == 0)
            return OSTYPE_UNKNOW;
        for (String lib : libs) {
            if (lib.contains("touchwiz")) {
                Util.putPreferenceInt(context, Util.SAVE_KEY_OS_TYPE, OSTYPE_SAMSUNG);
                return OSTYPE_SAMSUNG;
            } else if (lib.contains("com.sonyericsson.navigationbar")) {
                Util.putPreferenceInt(context, Util.SAVE_KEY_OS_TYPE, OSTYPE_SONY);
                return OSTYPE_SONY;
            }
        }
        return OSTYPE_UNKNOW;
    }

    private static int ResolveTransparentStatusBarFlag(Context context) {
        String reflect = null;
        switch (getOsType(context)) {
            case OSTYPE_SAMSUNG:
                reflect = "SYSTEM_UI_FLAG_TRANSPARENT_BACKGROUND";
                break;
            case OSTYPE_SONY:
                reflect = "SYSTEM_UI_FLAG_TRANSPARENT";
                break;
            default:
                break;
        }
        if (reflect == null)
            return 0;
        try {
            Field field = View.class.getField(reflect);
            if (field.getType() == Integer.TYPE) {
                return field.getInt(null);
            }
        } catch (Exception e) {
            Util.printException(e);
        }
        return 0;
    }

    private static boolean applyTransparentMeizu(Window window) {
        WindowManager.LayoutParams localLayoutParams = window
                .getAttributes();
        Class<?> mzLpClass = localLayoutParams.getClass();
        try {
            Field meizuFlagsField = mzLpClass.getField("meizuFlags");
            int meizuFlags = (Integer) meizuFlagsField.get(localLayoutParams);
            meizuFlags |= 0x40;
            meizuFlagsField.set(localLayoutParams, meizuFlags);
        } catch (Exception e) {
            return false;
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.setAttributes(localLayoutParams);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void applyTransparentStatusBar(Context activity, Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        int flag = ResolveTransparentStatusBarFlag(activity);
        if (flag == 0) {
            Util.putPreferenceInt(activity, Util.SAVE_KEY_OS_TYPE, OSTYPE_NOT_SUPPORT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            flag = flag // | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            changeSystemUIFlag(window, flag);
        }
    }

    @SuppressLint("NewApi")
    private static void changeSystemUIFlag(Window window, int flag) {
        if (window != null) {
            View decor = window.getDecorView();
            if (decor != null) {
                decor.setSystemUiVisibility(flag);
            }
        }
    }

    public static boolean isStatusBarTransparency(Context context) {
        return Util.getPreferenceInt(context, Util.SAVE_KEY_OS_TYPE, 0) > 0;
    }
}
