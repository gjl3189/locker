package com.cyou.cma.clockscreen.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.CallLog;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.LockApplication;
import com.cyou.cma.clockscreen.bean.SecurityBean;
import com.cyou.cma.clockscreen.bean.SecurityJudge;
import com.cyou.cma.clockscreen.bean.SecurityLevel;
import com.cyou.cma.clockscreen.core.Intents;
import com.cyou.cma.clockscreen.password.PasswordHelper;
import com.cyou.cma.clockscreen.quicklaunch.QuickFolder;
import com.cyou.cma.clockscreen.sqlite.ProviderHelper;

public class Util {
	public static final boolean DEBUG = true;
	public static final boolean NOTIFICATION_DEBUG = false;

	private static final long UPDATE_TIME_DEBUG = 10 * 60 * 1000;// 测试版本10分钟检查更新
	public static final long MILLIS_FOR_DAY = 24 * 60 * 60 * 1000;// 一天的毫秒数
	public static final long MILLIS_FOR_DAY_DEBUG = 5 * 60 * 1000;// 一天的毫秒数Debug版本为5秒

	private static final long UPDATE_TIME_THEME_DEBUG = 0 * 60 * 1000;
	private static final long UPDATE_TIME_THEME_RELEASE = 10 * 60 * 1000;

	private static final long OBTAIN_RECOMMEND_DEBUG = 0 * 60 * 60 * 1000;// 一分钟
	private static final long OBTAIN_RECOMMEND_RELEASE = 24 * 60 * 60 * 1000;// 24小时

	public static final String SAVE_KEY_FILE = "save_key_file";// sharepreference文件名
	public static final String SAVE_KEY_RUN = "save_key_run";// 是否开启锁屏服务
	public static final String SAVE_KEY_LAST_UPDATE_TIME = "save_key_last_update_time";// 记录上次更新成功的时间
	public static final String SAVA_KEY_LAST_THEME_UPDATE_TIME = "sava_key_last_theme_update_time";
	public static final String SAVA_KEY_LAST_OBTAIN_RECOMMEND_TIME = "sava_key_last_obtain_recommend_time";
	public static final String SAVA_KEY_COMMONAPP_COLUMN = "save_key_commonapp_column";
	public static final String SAVE_KEY_CURRENT_PWD_CATEGORY = "save_key_current_pwd_category";// 当前密码锁类型
	public static final String SAVE_KEY_IGNORE_SMS_TIME = "ignore_msg_time";// 忽略短信的时间点
	public static final String SAVE_KEY_IGNORE_CALL_TIME = "ignore_call_time";// 忽略来电的时间点
	public static final String SAVE_KEY_NEED_DEFAULT_THEME_TIP = "need_default_theme_tip";// 忽略来电的时间点
	public static final String SAVE_KEY_IS_NEW_SETTINGS = "settings_new";// 设置按钮是否有红点
	public static final String SAVE_KEY_IS_NEW_WALLPAPER = "wallpaper_new";// 设置按钮是否有红点
	public static final String SAVE_KEY_IS_NEW_APPLOCK = "applock_new";// 设置按钮是否有红点
	public static final String SAVE_KEY_IS_PRO_VERSION = "is_pro_version";// 是否为高级版本
	public static final String SAVE_KEY_FIRST_START_TIME = "first_start_time";// 第一次启动的时间
	public static final String SAVE_KEY_IS_SHOWED_GRADE_DIALOG = "is_showed_grade_dialog";// 是否已经弹出过评分框
	public static final String SAVE_KEY_IS_SHOWED_THEME_TIP = "is_showed_theme_tip";// 是否已经显示过主题的提示
	public static final String SAVE_KEY_IS_SHOWED_LOCKSCREEN_SETTING_TIP = "is_showed_lockscreen_setting_tip";// 是否已经显示过锁屏界面设置的提示

	public static final String SAVE_KEY_FIRST_LAUNCH = "first_launch";// 是否是第一次启动
	public static final String SAVE_KEY_SCREEN_WIDTH = "screen_width";// 屏幕的宽
	public static final String SAVE_KEY_SCREEN_HEIGHT = "screen_height";// 屏幕的高
	public static final String SAVE_KEY_OS_TYPE = "os_type";// ROM类型
	public static final int KEY_WALLPAPER_TYPE_GALLERY = 2;// 壁纸使用类型
	public static final int KEY_WALLPAPER_TYPE_SYSTEM = 1;// 壁纸使用类型
	public static final int KEY_WALLPAPER_TYPE_RESTORE = 0;// 壁纸使用类型
	public static final String SAVE_KEY_STATUSBAR_HEIGHT = "statusbar_height";// 状态栏高度

	public static final long SPLASH_DELAY_TIME = 1000;

	public static final long VIBRATE_TIME = 100;

	// public static final String

	public static long getOneDayMillis() {
		if (DEBUG) {
			return MILLIS_FOR_DAY_DEBUG;
		} else {
			return MILLIS_FOR_DAY;
		}
	}

	public static void putPreferenceString(Context context, String key,
			String values) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_KEY_FILE, Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putString(key, values);
		editor.commit();
	}

	public static String getPreferenceString(Context context, String key) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_KEY_FILE, Context.MODE_PRIVATE);
		return preference.getString(key, "");
	}

	public static long getCheckUpdateTime() {
		if (DEBUG) {
			return UPDATE_TIME_DEBUG;
		} else {
			return MILLIS_FOR_DAY;
		}
	}

	public static long getUpdateThemeTime() {
		if (DEBUG) {
			return UPDATE_TIME_THEME_DEBUG;
		} else {
			return UPDATE_TIME_THEME_RELEASE;
		}
	}

	public static long getObtainRecommendTime() {
		if (DEBUG) {
			return OBTAIN_RECOMMEND_DEBUG;
		} else {
			return OBTAIN_RECOMMEND_RELEASE;
		}
	}

	public static void putPreferenceBoolean(Context context, String key,
			boolean values) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_KEY_FILE, Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putBoolean(key, values);
		editor.commit();
	}

	public static boolean getPreferenceBoolean(Context context, String key,
			boolean defaultValues) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_KEY_FILE, Context.MODE_PRIVATE);
		return preference.getBoolean(key, defaultValues);
	}

	public static void putPreferenceLong(Context context, String key,
			long values) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_KEY_FILE, Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putLong(key, values);
		editor.commit();
	}

	public static long getPreferenceLong(Context context, String key,
			long defaultValues) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_KEY_FILE, Context.MODE_PRIVATE);
		return preference.getLong(key, defaultValues);
	}

	public static void putPreferenceInt(Context context, String key, int values) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_KEY_FILE, Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putInt(key, values);
		editor.commit();
	}

	public static int getPreferenceInt(Context context, String key,
			int defaultValues) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_KEY_FILE, Context.MODE_PRIVATE);
		return preference.getInt(key, defaultValues);
	}

	public static void putPreferenceFloat(Context context, String key,
			float values) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_KEY_FILE, Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putFloat(key, values);
		editor.commit();
	}

	public static float getPreferenceFloat(Context context, String key,
			float defaultValues) {
		SharedPreferences preference = context.getSharedPreferences(
				SAVE_KEY_FILE, Context.MODE_PRIVATE);
		return preference.getFloat(key, defaultValues);
	}

	public static String getCurrenVersion(Context context) {
		String versionName = "1.0";
		try {
			versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (Exception e) {
			Util.printException(e);
		}
		return "V" + versionName;
	}

	public static long getLastUptimeMillis(Context context) {
		return getPreferenceLong(context, SAVA_KEY_LAST_THEME_UPDATE_TIME, 0);
	}

	public static void putLastUptimeMillis(Context context,
			long lastUptimeMillis) {
		putPreferenceLong(context, SAVA_KEY_LAST_THEME_UPDATE_TIME,
				lastUptimeMillis);
	}

	public static long getLastObtainRecommend(Context context) {
		return getPreferenceLong(context, SAVA_KEY_LAST_OBTAIN_RECOMMEND_TIME,
				0);
	}

	public static void putLastObtainRecommend(Context context,
			long lastObtainRecommendTime) {
		putPreferenceLong(context, SAVA_KEY_LAST_OBTAIN_RECOMMEND_TIME,
				lastObtainRecommendTime);
	}

	public static String getCurrenVersionCode(Context context) {
		int versionName = 1;
		try {
			versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (Exception e) {
			Util.printException(e);
		}
		return String.valueOf(versionName);
	}

	public static void Logcs(String tag, String msg) {
		if (DEBUG) {
			Log.d("clockercs:" + tag, msg);
		}
	}

	public static void Logjb(String tag, String msg) {
		if (DEBUG) {
			Log.d("clockerjb:" + tag, msg);
		}
	}

	public static void printException(Exception e) {
		if (DEBUG)
			e.printStackTrace();
	}

	public static void printException(Error e) {
		if (DEBUG)
			e.printStackTrace();
	}

	/**
	 * 检查锁屏是否为默认home拦截者
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isDefault(Context context) {
		final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
		filter.addCategory(Intent.CATEGORY_HOME);
		List<IntentFilter> filters = new ArrayList<IntentFilter>();
		filters.add(filter);
		final String myPackageName = context.getPackageName();
		List<ComponentName> activities = new ArrayList<ComponentName>();
		final PackageManager packageManager = context.getPackageManager();
		packageManager.getPreferredActivities(filters, activities,
				myPackageName);
		for (ComponentName activity : activities) {
			if (myPackageName.equals(activity.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	public class Statistics {

		public static final String KEY_SHOW_MISSED_CALL = "ShowMissedCall";
		public static final String KEY_SHOW_MISSED_SMS = "ShowMissedSms";
		public static final String KEY_UNLOCK_VIBRATE = "unlock_vibrate";
		public static final String KEY_UNLOCK_SOUND = "unlock_sound";

		public static final String KEY_SCREEN_TIME = "ScreenTime";
		public static final String KEY_RUNING_SERVICE = "RuningService";
		public static final String KEY_THEME_SWITCH = "ThemeSwitch";
		public static final String KEY_WALLPAPER_SWITCH = "WallpapaerSwitch";
		public static final String KEY_DOWNLOAD_START = "DownloadStart";
		public static final String KEY_DOWNLOAD_COMPLETE = "DownloadComplete";
		public static final String KEY_DOWNLOAD_FAILED = "DownloadFailed";
		public static final String KEY_DOWNLOAD_CANCLEED = "DownloadCanceled";
		public static final String KEY_THEME_APPLIED = "ThemeApplied";
		public static final String KEY_THEME_USING = "ThemeUsing";
		public static final String KEY_START_MAIN_ACTIVITY = "start_main_activity";
		public static final String KEY_UPDATE_START = "UpdateStart";
		public static final String KEY_UPDATE_SUCCESSFUL = "UpdateComplete";

		// /**
		// * 推荐应用入口次数
		// */
		// public static final String KEY_RECOMMEND_ENTER = "RecommendEnter";
		//
		// /**
		// * 推荐应用点击次数
		// */
		// public static final String KEY_RECOMMENDAPPCLICK =
		// "RecommendAppClick";
		//
		// /**
		// * 推荐应用点击开始下载
		// */
		// public static final String KEY_RECOMMENDAPP_DOWNLOAD_START =
		// "RecommendAppDownloadStart";
		//
		// /**
		// * 推荐应用下载完成
		// */
		// public static final String KEY_RECOMMENDAPP_DOWNLOAD_COMPLETE =
		// "RecommendAppDownloadComplete";
		// /**
		// * 打开常用应用
		// */
		// public static final String KEY_OPEN_COMMON_APP = "OpenCommonApp";
		/**
		 * 长按弹出快捷菜单
		 */
		public static final String KEY_OPEN_QUICKLAUNCH = "OpenQuickLaunch";
	}

	/**
	 * Get the screen width
	 * 
	 * @author mapeng_thun
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		return width;
	}

	/**
	 * Get the screen height
	 * 
	 * @author mapeng_thun
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int height = wm.getDefaultDisplay().getHeight();
		return height;
	}

	/**
	 * 获取imei码
	 * 
	 * @param context
	 * @return
	 */
	public static String getImeiCode(Context context) {
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (telephonyManager == null
					|| telephonyManager.getDeviceId() == null
					|| "".equals(telephonyManager.getDeviceId())) {
				return HttpUtil.NULL;
			}
			return telephonyManager.getDeviceId();
		} catch (Exception e) {
			return HttpUtil.NULL;
		}
	}

	public static boolean isIconShowable(String packageName) {
		Intent intent = LockApplication.getInstance().getPackageManager()
				.getLaunchIntentForPackage(packageName);
		return intent != null;
	}

	public static boolean isNewUser(Context context) {
		PackageInfo packageInfo1 = null;
		try {
			packageInfo1 = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Util.Logjb("isNewUser", " " + packageInfo1.firstInstallTime + " last "
				+ packageInfo1.lastUpdateTime);
		return packageInfo1.firstInstallTime == packageInfo1.lastUpdateTime;
	}

	/**
	 * 判断字符是否为空
	 * 
	 * @param content
	 * @return
	 */
	public static boolean contentIsNull(String content) {
		return null == content || content.toString().trim().equals("");
	}

	/**
	 * 判断屏幕是否亮
	 * 
	 * @return
	 */
	public static boolean isScreenOn(Context mContext) {
		boolean screenOn = false;
		PowerManager pm = (PowerManager) mContext
				.getSystemService(Context.POWER_SERVICE);
		if (null != pm) {
			screenOn = pm.isScreenOn();
		}
		return screenOn;
	}

	/**
	 * 获取偏移量的接近值，小数点后一位大于0.7则+1
	 * 
	 * @param offset
	 * @return
	 */
	public static int getOffseByRound(float offset) {
		float offsetTemp = Math.abs(offset);
		if (offsetTemp - (int) offsetTemp > 0.7f) {
			return (int) offset + (offset > 0 ? 1 : -1);
		} else {
			return (int) offset;
		}
	}

	public static void restartLocker(Context context) {
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(
				Intents.ACTION_RESTART_LOCKER), 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 500, pi);
		android.os.Process.killProcess(android.os.Process.myTid());
		System.exit(0);
	}

	/**
	 * 根据包名判断App 是否已经安装
	 * 
	 * @param packageName
	 * @return
	 */
	public static boolean appInstalled(String packageName) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = LockApplication.getInstance().getPackageManager()
					.getPackageInfo(packageName, 0);
		} catch (Exception e) {
			Util.printException(e);
		}
		return packageInfo != null;
	}

	public static boolean appInstalled(String packageName, Context context) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					packageName, 0);
		} catch (Exception e) {
			Util.printException(e);
		}
		return packageInfo != null;
	}

	/**
	 * 根据包名启动app
	 * 
	 * @param packageName
	 * @param context
	 */
	public static void startAppByPackageName(String packageName, Context context) {
		Intent intent = null;
		try {
			intent = LockApplication.getInstance().getPackageManager()
					.getLaunchIntentForPackage(packageName);
			Intent intent1 = new Intent(Intent.ACTION_MAIN);
			intent1.addCategory(Intent.CATEGORY_LAUNCHER);
			intent1.setComponent(intent.getComponent());
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			context.startActivity(intent1);
		} catch (ActivityNotFoundException e1) {
			Toast.makeText(context, R.string.lock_app_not_exists,
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(context, R.string.lock_open_app_faild,
					Toast.LENGTH_SHORT).show();
			Util.printException(e);
		}

	}

	/**
	 * 根据ComponentName启动app
	 * 
	 * @param ComponentName
	 * @param context
	 */
	public static void startAppByPackageName(String packageName,
			String mainActivityName, Context context) {
		if (TextUtils.isEmpty(mainActivityName)) {
			startAppByPackageName(packageName, context);
			return;
		}
		Intent intent = new Intent();
		try {

			// intent = LockApplication.getInstance().getPackageManager()
			// .getLaunchIntentForPackage(packageName);
			intent.setComponent(new ComponentName(packageName, mainActivityName));
			// Intent intent1 = new Intent(Intent.ACTION_MAIN);
			// intent1.addCategory(Intent.CATEGORY_LAUNCHER);
			// intent1.setComponent(intent.getComponent());
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			context.startActivity(intent);
		} catch (ActivityNotFoundException e1) {
			Toast.makeText(context, R.string.lock_app_not_exists,
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(context, R.string.lock_open_app_faild,
					Toast.LENGTH_SHORT).show();
			Util.printException(e);
		}

	}

	/**
	 * 查询手机中安装的所有应用
	 * 
	 * @param context
	 * @return
	 */
	public static List<ResolveInfo> queryInstalledApps(Context context) {
		PackageManager packageManager = context.getPackageManager();
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		return packageManager.queryIntentActivities(mainIntent,
				PackageManager.GET_RESOLVED_FILTER);
	}

	public static boolean checkEmail(String email) {
		String format = "^\\w+(\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+([-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+$";
		// String format =
		// "^(\\d|\\w|\\-|_){3,}@([0-9a-z_\\-]*)(\\.(com|cn|inc|org|cc|edu|de)*){1,2}([a-z]{2})?$";
		if (email.matches(format)) {
			return true;// 邮箱名合法，返回true
		} else {
			return false;// 邮箱名不合法，返回false
		}
	}

	public static Intent getHomeIntent() {
		Intent homeIntent = new Intent(Intent.ACTION_MAIN);
		homeIntent.addCategory(Intent.CATEGORY_HOME);
		homeIntent.addCategory(Intent.CATEGORY_DEFAULT);
		return homeIntent;
	}

	public static List<ResolveInfo> getHomeList(Context context) {
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> list = pm.queryIntentActivities(getHomeIntent(), 0);
		return list;
	}

	public static boolean isProVersion(Context mContext) {
		boolean isProVersion = Util.getPreferenceBoolean(mContext,
				Util.SAVE_KEY_IS_PRO_VERSION, false);
		if (!isProVersion) {
			isProVersion = Util.appInstalled(Constants.LOCKER_PRO_PACKAGENAME);
			if (isProVersion) {
				Util.putPreferenceBoolean(mContext,
						Util.SAVE_KEY_IS_PRO_VERSION, true);
			}
		}
		return isProVersion;
	}

	public static void openAppDetailInGp(Context context, String packageName) {
		Uri gpUri = Uri.parse("https://play.google.com/store/apps/details?id="
				+ packageName);
		Intent intentGP = new Intent(Intent.ACTION_VIEW, gpUri);
		intentGP.setClassName("com.android.vending",
				"com.android.vending.AssetBrowserActivity");
		intentGP.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(intentGP);
		} catch (Exception e) {
			Intent intentWeb = new Intent(Intent.ACTION_VIEW);
			intentWeb.setData(gpUri);
			intentWeb = Intent.createChooser(intentWeb, null);
			try {
				context.startActivity(intentWeb);
			} catch (Exception e1) {
				Toast.makeText(context, R.string.lock_open_app_faild,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public static void doUnlockVibrate(Context context) {
		if (ProviderHelper.getVibrateEnable(context,
				SettingsHelper.getCurrentTheme(context))) {
			try {
				((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE))
						.vibrate(Util.VIBRATE_TIME);
			} catch (Exception e) {
			}
		}
	}

	public static SecurityJudge getSecurityLevel(Context context) {
		boolean isLockScreenOn = PasswordHelper.getUnlockType(context) != PasswordHelper.SLIDE_TYPE;
		boolean isAppLockOn = SettingsHelper.getApplockEnable(context);
		boolean isMailbox = MailBoxHelper.hasSavedMailBox(context);
		ArrayList<SecurityBean> onList = new ArrayList<SecurityBean>();
		ArrayList<SecurityBean> offList = new ArrayList<SecurityBean>();
		if (isAppLockOn) {
			onList.add(new SecurityBean(SecurityBean.APPLOCK_TYPE,
					R.string.applock_bean));
		} else {
			offList.add(new SecurityBean(SecurityBean.APPLOCK_TYPE,
					R.string.applock_bean));
		}
		if (isLockScreenOn) {
			onList.add(new SecurityBean(SecurityBean.PASSWORD_TYPE,
					R.string.lockscreen_bean));
		} else {
			offList.add(new SecurityBean(SecurityBean.PASSWORD_TYPE,
					R.string.lockscreen_bean));
		}
		if (isMailbox) {
			onList.add(new SecurityBean(SecurityBean.MAILBOX_TYPE,
					R.string.mailbox_bean));
		} else {
			offList.add(new SecurityBean(SecurityBean.MAILBOX_TYPE,
					R.string.mailbox_bean));

		}
		int size = onList.size();
		SecurityJudge securityJudge = new SecurityJudge();
		SecurityComparator securityComparator = new SecurityComparator();
		if (offList.size() != 0) {
			// Arrays.sort(offList, securityComparator);
			Collections.sort(offList, securityComparator);
		}
		if (onList.size() != 0) {
			Collections.sort(onList, securityComparator);
		}

		securityJudge.mOffs = offList;
		securityJudge.mOns = onList;
		securityJudge.securityLevel = SecurityLevel
				.valueOf(SecurityLevel.hashMap.get(size));
		return securityJudge;
		// return SecurityLevel.valueOf(SecurityLevel.hashMap.get(size));

	}

	public static String getImei(Context context) {
		return ((TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}

	public static String getAndroidId(Context context) {
		String androidId = "null";
		try {
			androidId = Settings.Secure.getString(context.getContentResolver(),
					Settings.Secure.ANDROID_ID);
		} catch (Exception e) {
		}

		if (androidId == null || "".equals(androidId)) {
			androidId = "null";
		}
		return androidId;
	}

	public static String getLocalMacAddress(Context context) {
		String mac = "null";
		try {
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			mac = info.getMacAddress();
		} catch (Exception e) {
		}

		if (mac == null && "".equals(mac)) {
			mac = "null";
		}
		return mac;
	}

	public static final String FOLDER_NO = "FOLDER_NO";

	// public static int getFolderNo(Context context) {
	// return getPreferenceInt(context, FOLDER_NO, 1);
	// }
	//
	// public static void saveFolderNo(Context context) {
	// int no = getFolderNo(context) + 1;
	// putPreferenceInt(context, FOLDER_NO, no);
	// }
	public static List<Integer> list = new ArrayList<Integer>();
	static {
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
	}

	public static int getFolderNo(Context context) {
		ArrayList<Integer> list2 = new ArrayList<Integer>();
		ArrayList<Integer> list3 = new ArrayList<Integer>();

		List<QuickFolder> quickFolders = LockApplication.mQuickFolderDao
				.loadAll();
		for (QuickFolder quickFolder : quickFolders) {
			if (quickFolder.getFolderName().startsWith("Folder")) {
				if (quickFolder.getFolderName().length() > 6) {
					try {
						String s = quickFolder.getFolderName().substring(6,
								quickFolder.getFolderName().length());
						int no = Integer.parseInt(s);
						if (no > 0 && no < 6) {
							list2.add(no);
						}
					} catch (Exception e) {
					}
				}
			}
		}
		for (Integer inte : list) {
			boolean find = false;
			for (Integer inte2 : list2) {

				if (inte2.intValue() == inte.intValue()) {
					// continue;
					find = true;
				}
				// return inte;
			}
			if (!find) {
				return inte;
			}

		}
		return 1;
	}

	public static String getMainActivityClassByPackageName(String packageName,
			PackageManager packageManager) {
		try {
			Intent it = new Intent(Intent.ACTION_MAIN);
			it.setPackage(packageName);// pkg为包名
			it.addCategory(Intent.CATEGORY_LAUNCHER);
			ComponentName ac = it.resolveActivity(packageManager);//
			// mPackageManager为PackageManager实例
			String classname = ac.getClassName();
			return classname;
		} catch (Exception exception) {
			Log.d("jiangbin", "Exception ----> " + packageName);
			return "";
		}
	}

	public static String getMainActivityClassByPackageName(
			ResolveInfo resolveInfo) {
		String clazzName = resolveInfo.activityInfo.name;
		if (StringUtils.isEmpty(clazzName)) {
			clazzName = "";
		}
		return clazzName;
	}

	public static final String HASHASHAS = "hashashas";
	public static final String HEHEHEHE = "hehehee";
	
	public static final String HAS_SHOW_SWIPE_TIP="HAS_SHOW_SWIPE_TIP";
	
	// 没用的 
	public  static String getMissedCall(Context context) {
        ContentResolver cr = context.getContentResolver();
        int callCount = 0;
        String[] proj = new String[] {
                CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.DATE
        };
        Cursor cursor = null;
        Uri uri = CallLog.Calls.CONTENT_URI;
        try {
            cursor = cr.query(uri, proj, "type=3 and new<>0", null,
                    CallLog.Calls.DEFAULT_SORT_ORDER);
            if (cursor != null)
                callCount = cursor.getCount();
        } catch (Exception e) {
            Util.printException(e);
            cursor = null;
        }
        long missedCallTime = 0;
        StringBuilder sb = new StringBuilder();
        if (cursor != null && callCount > 0) {
            try {
//                int count = 0;
                int dIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
                int nmIndex = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
                int nIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                if (cursor.moveToNext()) {
                    try {
//                        count++;
                        long callTime = cursor.getLong(dIndex);
                        if (callTime > missedCallTime) {
                            missedCallTime = callTime;
                        }
                        String name = cursor.getString(nmIndex);
                        String number;
                        if (!TextUtils.isEmpty(name)) {
                            number = name;
                        } else {
                            number = cursor.getString(nIndex);
                        }
                        if ("-1".equals(number) || TextUtils.isEmpty(number)) {
                            number = context.getString(R.string.state_unknown);
                        }
                        sb.append(number);
                        sb.append(";");
                        sb.append(name);
                        
                    } catch (Exception e) {
                        Util.printException(e);
                    }
                }
            } catch (Exception e) {
                Util.printException(e);
            }
        }
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                Util.printException(e);
            } finally {
                cursor = null;
            }
        }
        return sb.toString();
    }

}
