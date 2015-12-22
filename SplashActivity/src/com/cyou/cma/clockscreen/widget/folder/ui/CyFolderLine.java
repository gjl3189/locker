package com.cyou.cma.clockscreen.widget.folder.ui;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.widget.folder.CyFolderHelper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CyFolderLine extends LinearLayout{
	public CyFolderLine(Context context) {
		super(context);
	}
	public CyFolderLine(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		CyFolderHelper hp = CyFolderHelper.getInstance();
		if(hp!=null){
			if(getId()==R.id.folder_line_0){
				hp.line_0_Y = t;
//				CyTool.log("line_0: t="+t);
			}else{
				hp.line_1_Y = t;
//				CyTool.log("line_1: t="+t);
			}
		}
	}

}
