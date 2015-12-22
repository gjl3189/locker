package com.cyou.cma.clockscreen.bean;

import android.graphics.Bitmap;

public class AppBaseInfo {
	private String packageName;
	private String name;
	private Bitmap icon;

	public AppBaseInfo(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}

}
