package com.cyou.cma.clockscreen;

/**
 * 常量类 命名规范 统一在常量名前加前缀 C_
 * 
 * @author jiangbin
 */
public class Constants {

	public static final String SKY_LOCKER_DEFAULT_THEME = "com.cynad.cma.theme.default";
	public static final String LOCKER_PRO_PACKAGENAME = "com.cynad.cma.prolocker";
	public static final String THEME_PACKAGENAME_PREFIX = "com.cynad.cma.theme.";// 主题前缀
	public static final String THEME_CLAUNCHER_PACKAGENAME_PREFIX = "com.cyou.cma.clauncher.theme";// CLauncher主题前缀
	/** 默认的锁屏没有下载地址但是根据url会把所有的查出来 */
	public static final String C_LOCKER_DEFAULT_URL = "http://justfornotnull";

	// ----传递相关
	public static final String C_EXTRAS_THEME = "lockinfo_key";

	public static final String C_EXTRAS_DOWNLOAD_ID = "downloadid";
	public static final String C_EXTRAS_ONLINE = "online";
	public static final String C_EXTRAS_REGISTER = "register";

	public static final String C_EXTRAS_NEED_PUSH_UP = "push_up";
	/**
	 * 跳转到壁纸设置的包名参数
	 */
	public static final String C_EXTRAS_PACKAGE = "packagename";
	// public static final String C_EXTRAS_

	/**
	 * 按Home键传递的Extras判断是否跳转到桌面还是继续留在锁屏界面
	 */
	public static final String C_EXTRAS_SHOWLOCKSCREEN = "show_lockscreen";

	// ----壁纸相关

	/**
	 * 使用用户自己裁剪的照片做为锁屏壁纸
	 */
	public static final int C_WALLPAPER_GALLERY = 2;

	/**
	 * 使用桌面壁纸做为锁屏壁纸
	 */
	public static final int C_WALLPAPER_SYSTEM = 1;

	/** 使用锁屏主题自带壁纸做为锁屏壁纸 */
	public static final int C_WALLPAPER_THEME = 0;

	/**
	 * 当前锁屏主题所用壁纸的类型
	 */
	public static final String C_SAVE_KEY_IN_USE_WALLPAPER_TYPE = "in_use_wallpaper_type";

	/**
	 * 锁屏主题壁纸所在路径
	 */
	public static final String C_KEY_WALLPAPER_IN_USE_PATH = "";

	/**
	 * 锁屏服务是否开启 0未开启 1开启
	 */
	public static final String C_KEYGUARD_SERVICE_RUN = "com.cynad.cma.locker.settings.KEYGUARD_SERVICE_RUN";

	public static final String APP_LOCK_SERVICE_RUN = "com.cynad.cma.locker.setting.APP_LOCK_RUN";
	/**
	 * 当前使用的锁屏主题的包名
	 */
	public static final String C_THEME_PACKAGE = "com.cynad.cma.locker.settings.THEME_PACKAGE";
	/**
	 * 锁屏服务开启状态
	 */
	public static final int C_SERVICE_ON = 1;
	/**
	 * 锁屏服务关闭状态
	 */
	public static final int C_SERVICE_OFF = 0;

	public static final String AAAAAA = "aaaaaaa";
	public static final String BBBBBB = "bbbbbbb";
	public static final String CCCCCC = "ccccccc";
	public static final String DDDDDD = "ddddddd";

	/**
	 * 锁屏界面消息中心
	 */
	public static final int MSGCENTER_TYPE_TIP = 0;
	public static final int MSGCENTER_TYPE_SMS = 1;
	public static final int MSGCENTER_TYPE_CALL = 2;

	/**
	 * 快速启动
	 */

	public static final String LAUNCHSETID = "launchsetid";

}
