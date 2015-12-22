package com.cyou.cma.clockscreen.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.widget.Toast;

import com.cynad.cma.locker.R;

public class LockUtil {

	public static int getConversationId(Context mContext) {
		int id = -1;
		Cursor cursor = null;
		try {
			cursor = mContext.getContentResolver().query(
					Uri.parse("content://sms"), new String[] { "thread_id" },
					"read = ?", new String[] { "0" }, null);
			if (cursor != null && cursor.moveToNext()) {
				id = cursor.getInt(cursor.getColumnIndex("thread_id"));
				while (cursor.moveToNext()) {
					if (id != cursor.getInt(cursor.getColumnIndex("thread_id"))) {
						cursor.close();
						return -1;
					}
				}
			}
		} catch (Exception e) {
			Util.printException(e);
			id = -1;
			return id;
		} finally {
			if (cursor != null)
				cursor.close();
		}

		Cursor cursor_mms = null;
		try {
			cursor_mms = mContext.getContentResolver().query(
					Uri.parse("content://mms/inbox"),
					new String[] { "thread_id" }, "read = 0", null, null);
			if (cursor_mms != null) {
				if (id == -1 && cursor_mms.moveToNext()) {
					id = cursor_mms.getInt(cursor_mms
							.getColumnIndex("thread_id"));
				}
				while (cursor_mms.moveToNext()) {
					if (id != cursor_mms.getInt(cursor_mms
							.getColumnIndex("thread_id"))) {
						cursor_mms.close();
						return -1;
					}
				}
			}
		} catch (Exception e) {
			Util.printException(e);
			id = -1;
		} finally {
			if (cursor_mms != null)
				cursor_mms.close();
		}
		return id;
	}

	public static void openCall(Context mContext, int missCallCount) {
		Intent intentCall = new Intent();
		intentCall = new Intent(Intent.ACTION_DIAL);
		if (missCallCount > 0) {// to call records
			// intentCall.setType(Calls.CONTENT_TYPE);
			intentCall = new Intent(Intent.ACTION_CALL_BUTTON);
		}
		intentCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			mContext.startActivity(intentCall);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(mContext, R.string.lock_app_not_exists,
					Toast.LENGTH_SHORT).show();
		} catch (Exception e1) {
			Toast.makeText(mContext, R.string.lock_open_app_faild,
					Toast.LENGTH_SHORT).show();
		}
	}

	public static void openSms(Context mContext) {
		Intent intentSms = new Intent(Intent.ACTION_VIEW);
		intentSms.setType("vnd.android-dir/mms-sms");
		int id = getConversationId(mContext);
		// Log.e(TAG,"getConversationId-->"+id);
		if (id != -1) {
			intentSms.setData(Uri
					.parse("content://mms-sms/conversations/" + id));// conversations
																		// id
		} else {
			intentSms.setAction(Intent.ACTION_MAIN);
		}
		intentSms.addCategory(Intent.CATEGORY_DEFAULT);
		intentSms.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			mContext.startActivity(intentSms);
		} catch (ActivityNotFoundException e) {
			// Toast.makeText(mContext, R.string.lock_app_not_exists,
			// Toast.LENGTH_SHORT).show();
			// 找不到短信就找环聊
			Util.startAppByPackageName("com.google.android.talk", mContext);
		} catch (Exception e1) {
			Toast.makeText(mContext, R.string.lock_open_app_faild,
					Toast.LENGTH_SHORT).show();
		}
	}

	public static void openCamera(Context mContext) {
		Intent intent = new Intent();
		intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			mContext.startActivity(intent);
			return;
		} catch (ActivityNotFoundException e) {
			Util.printException(e);
			Toast.makeText(mContext, R.string.lock_app_not_exists,
					Toast.LENGTH_SHORT).show();
		} catch (Exception e1) {
			e1.printStackTrace();
			Toast.makeText(mContext, R.string.lock_open_app_faild,
					Toast.LENGTH_SHORT).show();
		}

		// case 2
		// Intent intentSms = new Intent(Intent.ACTION_VIEW);
		// intentSms.setAction("android.media.action.IMAGE_CAPTURE");
		// intentSms.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	public static String queryNameByNum(Context mContext, String num) {
		if (num == null)
			return null;
		String name = null;
		String num1 = num;
		if (num.startsWith("+86")) {
			num1 = num.substring(3);
		} else {
			num1 = "+86" + num;
		}
		// Log.e(TAG, "num-->" + num + "  num1-->" + num1);
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(num));
		Cursor cursor = null;
		try {
			cursor = mContext.getContentResolver()
					.query(uri, new String[] { PhoneLookup.DISPLAY_NAME },
							null, null, null);
			if (cursor.getCount() == 0) {
				cursor.close();
				uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
						Uri.encode(num1));
				cursor = mContext.getContentResolver().query(uri,
						new String[] { PhoneLookup.DISPLAY_NAME }, null, null,
						null);
			}
			if (cursor.moveToFirst()) {
				name = cursor.getString(cursor
						.getColumnIndex(PhoneLookup.DISPLAY_NAME));
			}
		} catch (Exception e) {
			Util.printException(e);
		} finally {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
		}
		return name;
	}

	public static void openLockerSetting(Context context) {
		Util.startAppByPackageName(context.getPackageName(), context);
	}

	public static void doCallWithNumber(Context context, String number) {
		// Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+
		// servicePhone));
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
				+ number));
		if (number == null)
			number = "";
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Util.printException(e);
			Toast.makeText(context, R.string.lock_app_not_exists,
					Toast.LENGTH_SHORT).show();
		} catch (Exception e1) {
			e1.printStackTrace();
			Toast.makeText(context, R.string.lock_open_app_faild,
					Toast.LENGTH_SHORT).show();
		}
	}

}
