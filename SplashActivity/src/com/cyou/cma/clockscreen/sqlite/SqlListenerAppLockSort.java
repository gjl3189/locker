package com.cyou.cma.clockscreen.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cyou.cma.clockscreen.util.Util;

public class SqlListenerAppLockSort {
	private Context context;
	private SQLiteDatabase db;

	public static int COMMON_LIMIT_COUNT = 15;

	public SqlListenerAppLockSort(Context c) {
		this.context = c.getApplicationContext();
		try {
			AssetsDatabaseManager.initManager(context);
			db = AssetsDatabaseManager.getManager().getDatabase(
					DatabaseConstants.DB_APPLOCK_SORT_NAME);
		} catch (IllegalStateException e) {
			Util.printException(e);
		}
	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		// db.close();
		AssetsDatabaseManager.getManager().closeDatabase(
				DatabaseConstants.DB_APPLOCK_SORT_NAME);
	}

	public List<String> getDefaultSortPackages() {
		List<String> list = new ArrayList<String>();
		Cursor cursor = null;
		try {
			cursor = db.query(DatabaseConstants.TABLE_APPLOCK_SORT, null, null,
					null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				int packageIndex = cursor
						.getColumnIndex(DatabaseConstants.KEY_APPLOCKSORT_PACKAGE_NAME);
				while (cursor.moveToNext()) {
					list.add(cursor.getString(packageIndex));
				}
			}
		} catch (Exception e) {
			Util.printException(e);
		} finally {
			try {
				if (cursor != null) {
					cursor.close();
				}
			} catch (Exception e2) {
			}
		}
		return list;

	}

	/**
	 * 删除表信息
	 * 
	 * @param tablename
	 *            表名
	 * @param whereClause
	 *            条件
	 * @param whereArgs
	 *            条件参数
	 * @description
	 */
	public void delete(String tablename, String whereClause, String[] whereArgs) {
		db.delete(tablename, whereClause, whereArgs);
	}

}
