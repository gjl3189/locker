package com.cyou.cma.clockscreen.widget.folder.util;

import com.cyou.cma.clockscreen.bean.AppBaseInfo;

import android.graphics.Bitmap;
import android.view.View.OnClickListener;

public class CyFolder {
	private String name = null;
	private Bitmap bmp = null;
	private OnClickListener cl = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Bitmap getBmp() {
		return bmp;
	}

	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}

	public OnClickListener getCl() {
		return cl;
	}

	public void setCl(OnClickListener cl) {
		this.cl = cl;
	}

	public void setAppBaseInfo(AppBaseInfo info) {
		if (info == null)
			return;
		this.name = info.getName();
		this.bmp = info.getIcon();
	}

}
