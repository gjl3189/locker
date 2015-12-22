package com.cyou.cma.clockscreen.bean;

import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.cyou.cma.clockscreen.LockApplication;

public class InstalledAppBean implements EntityType{
	public ResolveInfo resolveInfo;
	public boolean selected;
	// private boolean oldSelected;
	public Bitmap logo;

	// add by jiangbin 为了方便获取 包名和主activity类名 文件夹
	public String pakcageName;
	public String mainActivityClassName;

	// end by jiangbin
	public Bitmap getLogo() {
		return logo;
	}

	public void setLogo(Drawable logo) {
		this.logo = LockApplication.getInstance().getInstallMaskBitmap(logo);
	}

	public ResolveInfo getResolveInfo() {
		return resolveInfo;
	}

	public void setResolveInfo(ResolveInfo resolveInfo) {
		this.resolveInfo = resolveInfo;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}


}
