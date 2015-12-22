package com.cyou.cma.clockscreen.core;

/**
 * {@code Intents} contains constants for intents
 * 
 * @author wind.zhang
 */
public class Intents {
	/**
	 * /** Action To start keyguard themes activity
	 */
	public static final String ACTION_THEMES = "com.cynad.cma.clocker.action.THEMES";

	/**
	 * Action To start keyguard home key setting activity
	 */
	public static final String ACTION_HOMEKEY = "com.cynad.cma.clocker.action.HOMEKEY";

	/**
	 * Broadcast action to tell other keyguard apps to kill their keyguard
	 * service
	 */
	// public static final String ACTION_YIELD =
	// "com.cynad.cma.clocker.action.YIELD";

	/**
	 * Broadcast action sent when a keyguard theme is installed,the broadcast
	 * intent should contains an extra of key {@link #EXTRA_THEME_PACKAGE} with
	 * value set to keyguard theme(just installed)'s package name
	 */
	public static final String ACTION_THEME_INSTALLED = "com.cynad.cma.clocker.action.THEME_INSTALLED";

	/**
	 * Broadcast action to diable a keyguard theme, the broadcast intent should
	 * contains an extra of key {@link #EXTRA_THEME_PACKAGE} with value set to
	 * keyguard theme(to be disabled)'s package name
	 */
	public static final String ACTION_DISABLE = "com.cynad.cma.clocker.action.DISABLE";

	/**
	 * Intent extra key for theme package name
	 */
	public static final String EXTRA_THEME_PACKAGE = "theme_package";

	// public static final String ACTION_CHECKUPDATE =
	// "com.cynad.cma.clocker.action.CHECKUPDATE";

	public static final String ACTION_RESTART_LOCKER_FROM_THEME = "com.cynad.cma.clocker.action.restart.fromtheme";
	public static final String ACTION_RESTART_LOCKER = "com.cynad.cma.clocker.action.restart";
	public static final String ACTION_RECREATE_DIALOG = "com.cynad.cma.clocker.action.recreatedialog";
	public static final String ACTION_HIDE_ICON = "com.cynad.cma.clocker.action.hideicon";
//	public static final String ACTION_MAILBOX = "com.cynad.cma.clocker.action.mailbox";
}
