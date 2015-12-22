package com.cyou.cma.clockscreen.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlHelperLockApp extends SQLiteOpenHelper {
	private final static String dbname = "applock.db";
	private final static int VERSION = 2;

	public SqlHelperLockApp(Context context) {
		super(context, dbname, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE IF NOT EXISTS "
				+ DatabaseConstants.TABLE_APPLOCK + " ("
				+ DatabaseConstants.KEY_APPLOCK_PACKAGE_NAME
				+ " text PRIMARY KEY)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// if (oldVersion == 1 && newVersion == 2) {
		// db.execSQL("DROP TABLE IF EXISTS " +SqlListener.TABLE_BROWSE);
		// onCreate(db);
		// }
	}

}
