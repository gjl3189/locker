package com.cyou.cma.clockscreen.activity;

import android.os.Bundle;

import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;

public abstract class BaseActivityEx extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SystemUIStatusUtil.onCreate(this, this.getWindow());
		initView(savedInstanceState);
		if (SystemUIStatusUtil.isStatusBarTransparency(mContext)) {
			findViewById(getRootViewRes()).setPadding(0,
					ImageUtil.getStatusBarHeight(mContext), 0, 0);
		}
	}
	/**
	 * setContentView must be written here
	 */
	public abstract void initView(Bundle savedInstanceState);
	
	public abstract int getRootViewRes();
}
