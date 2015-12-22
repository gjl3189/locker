package com.cyou.cma.clockscreen.widget.folder.ui;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.widget.folder.CyFolderHelper;

public class CyFolderContainer extends LinearLayout{
	public CyFolderContainer(Context context) {
		super(context);
	}

	public CyFolderContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public static final int[] aryId = { R.id.item_0_0, R.id.item_0_1,
			R.id.item_0_2, R.id.item_0_3, R.id.item_1_0, R.id.item_1_1,
			R.id.item_1_2, R.id.item_1_3, };

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (SystemUIStatusUtil
				.isStatusBarTransparency(getContext())) {
			this.setPadding(0,
					ImageUtil.getStatusBarHeight(getContext()),
					0, 0);
		}
		CyFolderHelper hp = CyFolderHelper.getInstance();
		if (hp != null) {
			List<CyFolderItem> lstView = hp.getLstView();
			lstView.clear();
			for (int i = 0; i < aryId.length; i++) {
				CyFolderItem v = (CyFolderItem) findViewById(aryId[i]);
				if (v != null) {
					v.setIndex(i);
					lstView.add(v);
				}
			}
			hp.setCn(this);
		}
	}
}
