package com.cyou.cma.clockscreen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.activity.ProVersionActivity;
import com.cyou.cma.clockscreen.core.Intents;
import com.cyou.cma.clockscreen.quicklaunch.DatabaseUtil;
import com.cyou.cma.clockscreen.util.StringUtils;
import com.cyou.cma.clockscreen.util.Util;

public class PackageChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		// Log.e("PackageChangeReceiver", "onReceive==action-->" + action);
		if (TextUtils.isEmpty(action))
			return;
		if (Intent.ACTION_INSTALL_PACKAGE.equals(action)
				|| Intent.ACTION_PACKAGE_ADDED.equals(action)) {
			String packageName = intent.getData().getSchemeSpecificPart();
			if (packageName != null
					&& Constants.LOCKER_PRO_PACKAGENAME.equals(packageName)) {
				Util.putPreferenceBoolean(context,
						Util.SAVE_KEY_IS_PRO_VERSION, true);
				hideProIconOnLauncher(context, Constants.LOCKER_PRO_PACKAGENAME);
				Intent intentPro = new Intent(context, ProVersionActivity.class);
				intentPro.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_SINGLE_TOP);
				context.startActivity(intentPro);

			}
		}
		try {

			if (Intent.ACTION_UNINSTALL_PACKAGE.equals(action)
					|| Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
				String packageName = intent.getData().getSchemeSpecificPart();
				if (!StringUtils.isEmpty(packageName)) {
					DatabaseUtil.deleteApplicationByPackageName(packageName);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void hideProIconOnLauncher(Context mContext, String packageName) {
		Util.startAppByPackageName(packageName, mContext);
		// ComponentName disableComponentName = intentLauncher.getComponent();
		// if (disableComponentName == null)
		// return;
		// packageManager.setComponentEnabledSetting(disableComponentName,
		// PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
		// PackageManager.DONT_KILL_APP);

		Intent intent = new Intent(Intents.ACTION_HIDE_ICON);
		intent.putExtra("packageName", packageName);
		mContext.sendBroadcast(intent);

	}
}
