package com.cyou.cma.clockscreen.util;

import android.content.Context;

public class MailBoxHelper {
	public static final String MAILBOX_KEY = "mailbox";

	public static boolean hasSavedMailBox(Context context) {
		// return true;
		// return Util.getPreferenceBoolean(context, MAILBOX_KEY, false);
		String mail = Util.getPreferenceString(context, MAILBOX_KEY);
		return !StringUtils.isEmpty(mail);
	}

	public static String getSavedMailBox(Context context) {
		
		String mail = Util.getPreferenceString(context, MAILBOX_KEY);
		return StringUtils.isEmpty(mail)?"":mail;
	}

	public static void saveMailBox(Context context, String mailbox) {
		Util.putPreferenceString(context, MAILBOX_KEY, mailbox);
	}
}
