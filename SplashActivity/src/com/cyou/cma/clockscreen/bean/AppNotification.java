package com.cyou.cma.clockscreen.bean;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.LockApplication;
import com.cyou.cma.clockscreen.util.SettingsHelper;

public class AppNotification implements EntityType {
	public Bitmap mLogo;
	public String mTitle;
	public String mContent;
	public String mPackageName;
	public String mTime;
	public PendingIntent mPendingIntent;
	public boolean mIsPhone =false;
	
	public  int type =0;
	public  static final int TIP =1;

	public long mTimeLong;

	public void setLogo(Drawable logDrawable, Context context) {
		if (Constants.SKY_LOCKER_DEFAULT_THEME.equals(SettingsHelper
				.getCurrentTheme(context))) {
			this.mLogo = LockApplication.getInstance()
					.getNotificationMaskBitmap(logDrawable);
		} else {
			this.mLogo = LockApplication.getInstance()
					.getNotificationMaskBitmapOther(logDrawable);
		}
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		// return super.equals(o);
		if (o instanceof AppNotification) {
			AppNotification appNotification = (AppNotification) o;
			return appNotification.mPackageName.equals(this.mPackageName);
		}
		return false;
	}
}
