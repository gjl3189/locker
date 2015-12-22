package com.cyou.cma.clockscreen.sqlite;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Handler;

//TODO edit jiangbin 
public class WallPaperProvider extends ContentProvider {
    private static final String DB_NAME = "clocker.db";

    /**
     * 壁纸表
     */
    private static final String TABLE_WALLPAPER = "wallpaper";
    /**
     * 主题 壁纸关联表
     */
    private static final String TABLE_THEME_WALLPAPER = "theme_wallpaper";

    private static final String TABLE_VIBRATE = "vibrate";
    private static final String TABLE_SOUND = "sound";

    public final static String KEY_ID = "id";
    public final static String KEY_THUMBNAIL = "thumbnail_path";
    public final static String KEY_WALLPAPER = "wallpaper_path";
    public final static String KEY_DEFAULT = "isdefault";
    public final static String KEY_TIME = "time";
    public final static String KEY_PROVIDE = "isprovide";

    public final static String KEY_PACKAGE = "packageName";
    public final static String KEY_WALLPAPER_TYPE = "wallpaperType";
    public final static String KEY_STATE = "state";
    private static final int DB_VERSION = 2;
    private DatabaseHelper mOpenHelper;
    private SQLiteDatabase mDb;

    /** 全部壁纸 */
    private static final String WALLPAPER_LIST_TYPE = "vnd.android.cursor.dir/wallpaper";

    /** 单个壁纸 */
    private static final String WALLPAPER_TYPE = "vnd.android.cursor.item/wallpaper";

    /** URI matcher used to recognize URIs sent by applications */
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    /**
     * URI matcher constant for the URI of all downloads belonging to the
     * calling UID
     */
    private static final int WALLPAPERS = 1;
    /**
     * URI matcher constant for the URI of an individual download belonging to
     * the calling UID
     */
    // private static final int WALLPAPER_ID = 2;
    private static final int THEME_WALLPAPER = 3;
    private static final int VIBRATE = 2;
    private static final int SOUND = 4;
    public static Uri wallpaper_uri;// = Uri.parse("content://wallpaper/wallpapers");
    public static Uri theme_uri;// = Uri.parse("content://wallpaper/themes");
    public static Uri vibrate_uri;// = Uri.parse("content://wallpaper/vibrate");
    public static Uri sound_uri;// = Uri.parse("content://wallpaper/sound");

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        String packageName = getContext().getPackageName();
        wallpaper_uri = Uri
                .parse("content://" + packageName + "wallpaper/wallpapers");
        theme_uri = Uri.parse("content://" + packageName + "wallpaper/themes");
        vibrate_uri = Uri.parse("content://" + packageName + "wallpaper/vibrate");
        sound_uri = Uri.parse("content://" + packageName + "wallpaper/sound");
        sURIMatcher.addURI(getContext().getPackageName() + "wallpaper", "wallpapers", WALLPAPERS);
        sURIMatcher.addURI(getContext().getPackageName() + "wallpaper", "themes", THEME_WALLPAPER);
        sURIMatcher.addURI(getContext().getPackageName() + "wallpaper", "vibrate", VIBRATE);
        sURIMatcher.addURI(getContext().getPackageName() + "wallpaper", "sound", SOUND);
        
//        sURIMatcher.addURI( "wallpaper", "wallpapers", WALLPAPERS);
//        sURIMatcher.addURI( "wallpaper", "themes", THEME_WALLPAPER);
//        sURIMatcher.addURI( "wallpaper", "vibrate", VIBRATE);
//        sURIMatcher.addURI( "wallpaper", "sound", SOUND);
        return true;
    }

    public static Uri getWallpaperUri(Context context) {
        if (wallpaper_uri == null) {
            String packageName = context.getPackageName();
            wallpaper_uri = Uri
                    .parse("content://" + packageName + "wallpaper/wallpapers");
        }
        return wallpaper_uri;
    }

    public static Uri getSoundUri(Context context) {
        if (sound_uri == null) {
            String packageName = context.getPackageName();
            sound_uri = Uri.parse("content://" + packageName + "wallpaper/sound");
        }
        return sound_uri;
    }

    public static Uri getVibrateUri(Context context) {
        if (vibrate_uri == null) {
            String packageName = context.getPackageName();
            vibrate_uri = Uri.parse("content://" + packageName + "wallpaper/vibrate");
        }
        return vibrate_uri;

    }

    public static Uri getThemeUri(Context context) {
        if (theme_uri == null) {
            String packageName = context.getPackageName();
            theme_uri = Uri.parse("content://" + packageName + "wallpaper/themes");
        }
        return theme_uri;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = mOpenHelper.getReadableDatabase();

            int match = sURIMatcher.match(uri);
            if (match == -1) {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            if (match == THEME_WALLPAPER) {
                cursor = db.query(TABLE_THEME_WALLPAPER, projection, selection, selectionArgs,
                        null, null, sortOrder);
                return cursor;

            } else if (match == VIBRATE) {
                cursor = db.query(TABLE_VIBRATE, projection, selection, selectionArgs, null, null,
                        sortOrder);
                return cursor;
            } else if (match == SOUND) {
                cursor = db.query(TABLE_SOUND, projection, selection, selectionArgs, null, null,
                        sortOrder);
                return cursor;
            }

            else {

                cursor = db.query(TABLE_WALLPAPER, projection, selection, selectionArgs, null,
                        null, sortOrder);

                if (cursor != null) {
                    cursor.setNotificationUri(getContext().getContentResolver(), uri);
                } else {
                }

                return cursor;
            }
        } finally {
        }
    }

    @Override
    public String getType(Uri uri) {
        int match = sURIMatcher.match(uri);
        switch (match) {
            case WALLPAPERS: {
                return WALLPAPER_LIST_TYPE;
            }

            case THEME_WALLPAPER: {
                return WALLPAPER_TYPE;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Cursor c = null;
        try {
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();

            int match = sURIMatcher.match(uri);
            if (match != WALLPAPERS) {
                throw new IllegalArgumentException("Unknown/Invalid URI " + uri);
            }

            c = db.query(TABLE_WALLPAPER, null, KEY_WALLPAPER + "=?", new String[] {
                    String.valueOf(values.get(KEY_WALLPAPER))
            }, null, null, null);
            if (c.getCount() > 0) {
                // db.delete(TABLE_WALLPAPER, KEY_WALLPAPER + "=?",
                // new String[] { String.valueOf(values.get(KEY_WALLPAPER)) });
                return uri;
            }
            long rowID = db.insert(TABLE_WALLPAPER, null, values);
            if (rowID == -1) {
                return null;
            }

            return ContentUris.withAppendedId(wallpaper_uri, rowID);
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = sURIMatcher.match(uri);
        if (match != WALLPAPERS) {
            throw new IllegalArgumentException("Unknown/Invalid URI " + uri);
        }
        int count = 0;
        switch (match) {
            case WALLPAPERS:
                count = db.delete(TABLE_WALLPAPER, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Cannot delete URI: " + uri);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            int match = sURIMatcher.match(uri);
            // if (match != THEME_WALLPAPER) {
            if (match == THEME_WALLPAPER) {
                cursor = db.query(TABLE_THEME_WALLPAPER, null, selection, selectionArgs, null,
                        null, null);
                if (cursor.getCount() == 0) {
                    db.insert(TABLE_THEME_WALLPAPER, null, values);

                } else {
                    db.update(TABLE_THEME_WALLPAPER, values, selection, selectionArgs);
                }
            } else if (match == VIBRATE) {

                cursor = db.query(TABLE_VIBRATE, null, selection, selectionArgs, null, null, null);
                if (cursor.getCount() == 0) {
                    db.insert(TABLE_VIBRATE, null, values);

                } else {
                    db.update(TABLE_VIBRATE, values, selection, selectionArgs);
                }
            } else if (match == SOUND) {

                cursor = db.query(TABLE_SOUND, null, selection, selectionArgs, null, null, null);
                if (cursor.getCount() == 0) {
                    db.insert(TABLE_SOUND, null, values);

                } else {
                    db.update(TABLE_SOUND, values, selection, selectionArgs);
                }

            }

            return 0;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    private final class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(final Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(final SQLiteDatabase db) {
            createWallpaperTable(db);
            mDb = db;
        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, int oldV, final int newV) {
            if (oldV == 1 && newV == 2) {
                upgradFrom1to2(db);
                // importPreviousSetting(db);
                mDb = db;
            }
        }

        private void createWallpaperTable(SQLiteDatabase db) {

            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_WALLPAPER + " (" + KEY_ID
                    + " integer PRIMARY KEY autoincrement, " + KEY_THUMBNAIL + " text, "
                    + KEY_WALLPAPER + " text, " + KEY_PROVIDE + " INTEGER, " + KEY_TIME + " text, "
                    + KEY_DEFAULT + " INTEGER)";
            db.execSQL(sql);
            sql = "CREATE TABLE IF NOT EXISTS  " + TABLE_THEME_WALLPAPER + " (" + KEY_PACKAGE
                    + " text, " + KEY_WALLPAPER_TYPE + " INTEGER, " + KEY_WALLPAPER + " text)";
            db.execSQL(sql);
            // 创建 vibrate 表
            sql = "CREATE TABLE IF NOT EXISTS  " + TABLE_VIBRATE + " (" + KEY_PACKAGE + " text, "
                    + KEY_STATE + " INTEGER)";
            db.execSQL(sql);

            sql = "CREATE TABLE IF NOT EXISTS  " + TABLE_SOUND + " (" + KEY_PACKAGE + " text, "
                    + KEY_STATE + " INTEGER)";
            db.execSQL(sql);
        }
    }

    /**
     * 数据库从版本1升级到2
     */
    private void upgradFrom1to2(final SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS  " + TABLE_THEME_WALLPAPER + " (" + KEY_PACKAGE
                + " text, " + KEY_WALLPAPER_TYPE + " INTEGER, " + KEY_WALLPAPER + " text)";
        db.execSQL(sql);
        // 创建 vibrate 表
        sql = "CREATE TABLE IF NOT EXISTS  " + TABLE_VIBRATE + " (" + KEY_PACKAGE + " text, "
                + KEY_STATE + " INTEGER)";
        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS  " + TABLE_SOUND + " (" + KEY_PACKAGE + " text, "
                + KEY_STATE + " INTEGER)";
        db.execSQL(sql);
    }

}
