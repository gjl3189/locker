package com.cyou.cma.clockscreen.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.cyou.cma.clockscreen.bean.InstalledAppBean;
import com.cyou.cma.clockscreen.util.Util;

public class SqlListenerLockApp {
	// private Context context;
	private SQLiteDatabase db;
	private SqlHelperLockApp helper;

	public SqlListenerLockApp(Context c) {
		// this.context = c;
		helper = new SqlHelperLockApp(c);
		try {
			db = helper.getWritableDatabase();
		} catch (IllegalStateException e) {
			Util.printException(e);
		}
	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		db.close();
	}

	public void delete(String tablename, String whereClause, String[] whereArgs) {
		db.delete(tablename, whereClause, whereArgs);
	}

	public void cleanLockedApp() {
		db.delete(DatabaseConstants.TABLE_APPLOCK, null, null);
	}

	public boolean insertLockedApp(List<InstalledAppBean> list) {
		cleanLockedApp();
		if (list == null)
			return false;
		ContentValues values = null;
		boolean lockedApp = false;
		for (InstalledAppBean installedAppBean : list) {
			if (!installedAppBean.isSelected())
				continue;
			if (installedAppBean == null
					|| installedAppBean.getResolveInfo() == null
					|| installedAppBean.getResolveInfo().activityInfo == null
					|| TextUtils
							.isEmpty(installedAppBean.getResolveInfo().activityInfo.packageName)
					|| TextUtils
							.isEmpty(installedAppBean.getResolveInfo().activityInfo.name))
				continue;
			lockedApp = true;
			values = new ContentValues();
			values.put(DatabaseConstants.KEY_APPLOCK_PACKAGE_NAME,
					installedAppBean.getResolveInfo().activityInfo.packageName);
			try {
				db.insert(DatabaseConstants.TABLE_APPLOCK, "", values);
			} catch (Exception e) {
				Util.printException(e);
			}
		}
		return lockedApp;
	}

	public List<String> getLockedApp() {
		List<String> list = new ArrayList<String>();
		Cursor cursor = null;
		try {
			cursor = db.query(DatabaseConstants.TABLE_APPLOCK, null, null,
					null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				int packageIndex = cursor
						.getColumnIndex(DatabaseConstants.KEY_APPLOCK_PACKAGE_NAME);
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
}
