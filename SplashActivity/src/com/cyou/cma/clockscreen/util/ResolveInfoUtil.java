package com.cyou.cma.clockscreen.util;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

public class ResolveInfoUtil {
	public static String getPakcageName(ResolveInfo resolveInfo) {
		return resolveInfo.activityInfo.packageName;
	}

	public static String getAppName(ResolveInfo resolveInfo, PackageManager pm) {
		return (String) resolveInfo.loadLabel(pm);
	}

	public static Drawable getLogo(ResolveInfo resolveInfo, PackageManager pm) {
		return resolveInfo.loadIcon(pm);
	}
}
