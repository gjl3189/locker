package com.cyou.cma.clockscreen.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import com.cyou.cma.clockscreen.bean.InstalledAppBean;
import com.cyou.cma.clockscreen.sqlite.SqlListenerAppLockSort;

public class AppLockComparator implements Comparator<InstalledAppBean> {
	private List<String> mSortList = new ArrayList<String>();

	public AppLockComparator(Context context) {
		initImportantPackageName(context);
		SqlListenerAppLockSort sql = new SqlListenerAppLockSort(context);
		mSortList.addAll(sql.getDefaultSortPackages());
		sql.close();
		// getImportantPackageName(context);
	}

	@Override
	public int compare(InstalledAppBean arg0, InstalledAppBean arg1) {
		if (selecteToInt(arg0) == selecteToInt(arg1)) {// 选中状态一样
			if (mSortList == null || mSortList.size() == 0)
				return 0;
			int position0 = position(arg0);
			int position1 = position(arg1);
			if (position0 == position1) {// 两者都不再排序表中
				return 0;
			} else {
				if (position0 < 0 || position1 < 0) {
					return position0 < 0 ? 1 : -1;
				} else {
					return position0 - position1;
				}
			}
		} else {// 选中状态不同
			return selecteToInt(arg0);
		}
	}

	private int selecteToInt(InstalledAppBean bean) {
		return bean.isSelected() ? -1 : 1;
	}

	// private int containToInt(InstalledAppBean bean) {
	// return mSortList
	// .contains(bean.getResolveInfo().activityInfo.packageName) ? -1
	// : 1;
	// }

	private int position(InstalledAppBean bean) {
		return mSortList
				.indexOf(bean.getResolveInfo().activityInfo.packageName);
	}

	private void initImportantPackageName(Context context) {
		PackageManager pm = context.getPackageManager();
		
		Intent smsIntent = new Intent(Intent.ACTION_VIEW);
		smsIntent.setType("vnd.android-dir/mms-sms");
		smsIntent.setAction(Intent.ACTION_MAIN);
		smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
		List<ResolveInfo> smsList = pm.queryIntentActivities(smsIntent, 0);
		if (smsList != null) {
			for (ResolveInfo resolveInfo : smsList) {
//				Log.e("Conpare", "SMS-->"
//						+ resolveInfo.activityInfo.packageName);
				addPackageName(resolveInfo);
			}
		}
		
		
		// Intent callIntent = new Intent(Intent.ACTION_DIAL);
		// List<ResolveInfo> callList = pm.queryIntentActivities(callIntent, 0);
		// if (callList != null) {
		// for (ResolveInfo resolveInfo : callList) {
		// // Log.e("Conpare", "Call-->"
		// // + resolveInfo.activityInfo.packageName);
		// addPackageName(resolveInfo);
		// }
		// }
		
		Intent galleryIntent = new Intent(Intent.ACTION_VIEW);
		galleryIntent.setType("image/*");
		List<ResolveInfo> galleryList = pm.queryIntentActivities(galleryIntent,
				0);
		if (galleryList != null) {
			for (ResolveInfo resolveInfo : galleryList) {
//				Log.e("Conpare", "Gallery-->"
//						+ resolveInfo.activityInfo.packageName);
				addPackageName(resolveInfo);
			}
		}

		Intent cameraIntent = new Intent(
				"android.media.action.STILL_IMAGE_CAMERA");
		List<ResolveInfo> cameraList = pm.queryIntentActivities(cameraIntent, 0);
		if (cameraList != null) {
			for (ResolveInfo resolveInfo : cameraList) {
				// Log.e("Conpare", "Camera-->"
				// + resolveInfo.activityInfo.packageName);
				addPackageName(resolveInfo);
			}
		}

	}

	private void addPackageName(ResolveInfo resolveInfo) {
		if (resolveInfo.activityInfo == null
				|| TextUtils.isEmpty(resolveInfo.activityInfo.packageName)) {
			return;
		}
		if (resolveInfo.activityInfo.packageName.equals("android")) {
			return;
		} else {
			mSortList.add(resolveInfo.activityInfo.packageName);
		}
	}
}
