package com.cyou.cma.clockscreen;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;

import com.cyou.cma.clockscreen.bean.Group;
import com.cyou.cma.clockscreen.bean.Theme;
import com.cyou.cma.clockscreen.util.Util;

import java.util.Locale;

public class Urls {

	/** 最新锁屏主题地址 */

	public static final String NEW_THEMES_URL_GP_TEST = "http://test.api.c-launcher.com/client/lockscreentheme/latest4gp.do";
	public static final String NEW_THEMES_URL_GP = "http://api.c-launcher.com/client/lockscreentheme/latest4gp.do";
	public static final int DEFAULT_PAGE_SIZE = 8;// 默认每页多少数据

	/** 密码备份 **/
	public static final String PASSWORD_BAK_URL_TEST = "http://test.api.c-launcher.com/client/locker/backupPwd.do";
	public static final String PASSWORD_BAK_URL = "http://api.c-launcher.com/client/locker/backupPwd.do";

	/**
	 * 安全邮箱
	 */
	public static final String MAILBOX_URL = "http://api.c-launcher.com/client/locker/bindEmail.do";
	public static final String MAILBOX_URL_TEST = "http://test.api.c-launcher.com/client/locker/bindEmail.do";

	/**
	 * 最新锁屏主题地址 get 方法参数
	 */
	public static final String PARAM_PAGESIZE = "pageSize";
	public static final String PARAM_THEMEID = "themeId";
	public static final String PARAM_AUDITTIME = "auditTime";
	public static final String PARAM_LANGUAGE = "language";
	public static final String PARAM_COUNTRY = "country";
	public static final String PARAM_VERSION = "version";
	public static final String PARAM_PACKAGE_NAME = "packageName";
	public static final String PARAM_CHANNELID = "channelId";
	public static final String PARAM_MINSDKVERSION = "sdkVersion";

	public static final String PARAM_LIMIT = "limit";

	public static final String PARAM_PASSWORD = "p";
	public static final String PARAM_MAIL = "mail";

	public static final String PARAM_IMEI = "imei";
	public static final String PARAM_ANDROID_ID = "androidId";
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_MAC = "mac";

	public static String getNewThemesUrlGp() {
		return Util.DEBUG ? NEW_THEMES_URL_GP_TEST : NEW_THEMES_URL_GP;
	}

	public static String getPasswordBakUrl() {
		return Util.DEBUG ? PASSWORD_BAK_URL_TEST : PASSWORD_BAK_URL;
	}

	public static String getMailboxUrl() {
		return Util.DEBUG ? MAILBOX_URL_TEST : MAILBOX_URL;
	}

	public static class RequestUtil {
		/**
		 * 获取语言
		 * 
		 * @return
		 */
		public static String getLaunguage() {
			return Locale.getDefault().getLanguage();
		}

		/**
		 * 获取国家地区
		 * 
		 * @return
		 */
		public static String getLocal() {
			return Locale.getDefault().getCountry();
		}

		/**
		 * 获取客户端版本号
		 * 
		 * @return
		 */
		public static String getVersionCode(Context context) {
			int versionName = 1;
			try {
				versionName = context.getPackageManager().getPackageInfo(
						context.getPackageName(), 0).versionCode;
			} catch (NameNotFoundException e) {
				Util.printException(e);
			}
			return String.valueOf(versionName);

		}

		public static String getChannelId(Context context) {
			String channelId = "10000";
			ApplicationInfo appInfo;
			try {
				appInfo = context.getPackageManager().getApplicationInfo(
						context.getPackageName(), PackageManager.GET_META_DATA);
				channelId = "" + appInfo.metaData.getInt("UMENG_CHANNEL");
			} catch (NameNotFoundException e) {

			}
			return channelId;
		}

		public static String getLocalVersion(Group<Theme> themes) {
			StringBuffer sb = new StringBuffer("{");
			int size = themes.size();
			for (int i = 0; i < size; i++) {
				Theme theme = themes.get(i);
				sb.append("\"");
				sb.append(theme.getPackageName());
				sb.append("\":");
				sb.append("" + theme.getVersionCode());
				if (i != size - 1) {
					sb.append(",");
				}
			}
			sb.append("}");
			return sb.toString();
		}

		public static String getOsVersion() {
			return "" + VERSION.SDK_INT;
		}
	}
}
