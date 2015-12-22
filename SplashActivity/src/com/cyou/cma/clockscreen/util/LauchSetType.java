package com.cyou.cma.clockscreen.util;

public class LauchSetType {
	public static final int CONTACT_TYPE = 1;
	public static final int APP_TYPE = 2;
	public static final int FOLDER_TYPE = 3;

	public static boolean isVaildType(int type) {
		if (type > FOLDER_TYPE || type < CONTACT_TYPE)
			return false;
		return true;
	}
}
