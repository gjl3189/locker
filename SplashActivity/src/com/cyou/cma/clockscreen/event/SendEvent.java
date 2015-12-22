package com.cyou.cma.clockscreen.event;

import com.cyou.cma.clockscreen.quicklaunch.QuickApplication;

import android.content.pm.ResolveInfo;

public class SendEvent {
	public static final int CONTACT_TYPE = 0;
	public static final int APP_TYPE = 1;
	public static final int FOLDER_TYPE = 2;
	public static final int NONE = -1;
	public int eventType;

	public String extra1;
	public String extra2;
	public ResolveInfo resolveInfo;
	public QuickApplication quickApplication;
}
