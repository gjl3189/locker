package com.cyou.cma.clockscreen.sqlite;

import java.io.File;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.cyou.cma.clockscreen.bean.WallpaperBean;
import com.cyou.cma.clockscreen.core.Intents;
import com.cyou.cma.clockscreen.util.Util;

/**
 * 查询数据库的帮助类
 * 
 * @author jiangbin
 */
public class ProviderHelper {

    /**
     * 获取用户自定义的壁纸
     * 
     * @return
     */
    public static ArrayList<WallpaperBean> getWallpapers(Context context, String packageName) {

        Cursor cursor = null;
        try {
            ArrayList<WallpaperBean> wallpaperBeans = new ArrayList<WallpaperBean>();
            cursor = context.getContentResolver().query(WallPaperProvider.getWallpaperUri(context), null,
                    null, null, null);
            if (cursor == null)
                return wallpaperBeans;
            int thumbIndex = cursor.getColumnIndex(WallPaperProvider.KEY_THUMBNAIL);
            int wallpaperIndex = cursor.getColumnIndex(WallPaperProvider.KEY_WALLPAPER);
            int timeIndex = cursor.getColumnIndex(WallPaperProvider.KEY_TIME);
            int provideIndex = cursor.getColumnIndex(WallPaperProvider.KEY_PROVIDE);

            while (cursor.moveToNext()) {
                WallpaperBean bean = new WallpaperBean();
                bean.setThumbPath(cursor.getString(thumbIndex));
                bean.setWallpaperPath(cursor.getString(wallpaperIndex));
                bean.setTime(cursor.getLong(timeIndex));
                bean.setIsProvide(cursor.getInt(provideIndex));
                if (bean.getWallpaperPath() == null || bean.getThumbPath() == null
                        || !new File(bean.getThumbPath()).exists()
                        || !new File(bean.getWallpaperPath()).exists()) {
                    Util.Logcs("Sqlistener", "file not exists-->" + bean.getThumbPath());
                    context.getContentResolver().delete(WallPaperProvider.getWallpaperUri(context),
                            WallPaperProvider.KEY_TIME + "=?", new String[] {
                                "" + bean.getTime()
                            });
                    continue;
                }

                String defaultPath = getWallpaperPath(context, packageName);

                bean.setIsDefault(bean.getWallpaperPath().equals(defaultPath) ? 1 : 0);
                wallpaperBeans.add(bean);
            }

            return wallpaperBeans;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 用户选择壁纸后 根据包名和壁纸类型 保存到数据库中
     * 
     * @param context .getContentResolver()
     * @param packageName
     * @param wallpaperType
     * @param id
     */
    public static void updateWallpaper(Context context, String packageName, int wallpaperType,
            String wallpaperPath) {
// if (LockApplication.getDexClassLoader().isSupportBaseThemeSetting(packageName)) {
// TODO
        if (true) {
            ContentValues cv = new ContentValues();
            cv.put(WallPaperProvider.KEY_PACKAGE, packageName);
            cv.put(WallPaperProvider.KEY_WALLPAPER_TYPE, wallpaperType);
            cv.put(WallPaperProvider.KEY_WALLPAPER, wallpaperPath);
            context.getContentResolver().update(WallPaperProvider.getThemeUri(context), cv,
                    WallPaperProvider.KEY_PACKAGE + "=?", new String[] {
                        packageName
                    });
            context.sendBroadcast(new Intent(Intents.ACTION_RECREATE_DIALOG));
        }
    }

    public static void updateVibrate(Context context, String packageName, int state) {
// if (LockApplication.getDexClassLoader().isSupportBaseThemeSetting(packageName)) {
// TODO
        if (true) {
            ContentValues cv = new ContentValues();
            cv.put(WallPaperProvider.KEY_PACKAGE, packageName);
            cv.put(WallPaperProvider.KEY_STATE, state);
            context.getContentResolver().update(WallPaperProvider.getVibrateUri(context), cv,
                    WallPaperProvider.KEY_PACKAGE + "=?", new String[] {
                        packageName
                    });
        }
    }

    public static void updateSound(Context context, String packageName, int state) {
//        if (true) {
            // TODO jiangbin
// if (LockApplication.getDexClassLoader().isSupportBaseThemeSetting(packageName)) {
            ContentValues cv = new ContentValues();
            cv.put(WallPaperProvider.KEY_PACKAGE, packageName);
            cv.put(WallPaperProvider.KEY_STATE, state);
            context.getContentResolver().update(WallPaperProvider.getSoundUri(context), cv,
                    WallPaperProvider.KEY_PACKAGE + "=?", new String[] {
                        packageName
                    });
//        }
    }

    public static boolean getVibrateEnable(Context context, String packageName) {
// LockApplication.getDexClassLoader().isSupportBaseThemeSetting(packageName)
// if (true) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(WallPaperProvider.getVibrateUri(context), null,
                    WallPaperProvider.KEY_PACKAGE + "=?", new String[] {
                        packageName
                    }, null);
            if (c != null && c.getCount() == 1) {
                c.moveToNext();
                int state = c.getInt(c.getColumnIndex(WallPaperProvider.KEY_STATE));
                return state == 1;
            } else {
                return true;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

// }

    public static boolean getSoundEnable(Context context, String packageName) {
// if (LockApplication.getDexClassLoader().isSupportBaseThemeSetting(packageName)) {

// if (true) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(WallPaperProvider.getSoundUri(context), null,
                    WallPaperProvider.KEY_PACKAGE + "=?", new String[] {
                        packageName
                    }, null);
            if (c != null && c.getCount() == 1) {
                c.moveToNext();
                int state = c.getInt(c.getColumnIndex(WallPaperProvider.KEY_STATE));
                return state == 1;
            } else {
                return true;
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

// }

    /**
     * 根据包名获取壁纸的类型
     * 
     * @param context .getContentResolver()
     * @param packageName
     * @return
     */
    public static int getWallpaperType(Context context, String packageName) {
        // if (LockApplication.getDexClassLoader().isSupportBaseThemeSetting(packageName)) {
// if (true)

// {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(WallPaperProvider.getThemeUri(context), null,
                    WallPaperProvider.KEY_PACKAGE + "=?", new String[] {
                        packageName
                    }, null);
            if (c != null && c.getCount() == 1) {
                c.moveToNext();
                int type = c.getInt(c.getColumnIndex(WallPaperProvider.KEY_WALLPAPER_TYPE));
                return type;
            } else {
                return 0;// 默认使用主题自带的
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
// } else {
// return SettingsHelper.getWallpaperType(context);
// }
    }

    /**
     * 根据包名获取壁纸的路径
     * 
     * @param context .getContentResolver()
     * @param packageName
     * @return
     */
    public static String getWallpaperPath(Context context, String packageName) {
// if (LockApplication.getDexClassLoader().isSupportBaseThemeSetting(packageName)) {

// if (true) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(WallPaperProvider.getThemeUri(context), null,
                    WallPaperProvider.KEY_PACKAGE + "=?", new String[] {
                        packageName
                    }, null);
            if (c != null && c.getCount() == 1) {
                c.moveToNext();
                return c.getString(c.getColumnIndex(WallPaperProvider.KEY_WALLPAPER));
            } else {
                return null;// 默认使用主题自带的
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
// } else {
// return SettingsHelper.getWallpaperPath(context);
// }
    }
}
