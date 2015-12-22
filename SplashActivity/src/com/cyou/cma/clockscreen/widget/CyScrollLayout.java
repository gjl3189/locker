package com.cyou.cma.clockscreen.widget;


import com.cyou.cma.clockscreen.util.CyGuideHelper;

import android.content.Context;
import android.util.AttributeSet;

/**
 * add by Jack
 * 2.3没有onLayoutListener这个类，复写方法解决适配问题
 * 仅供tab_activity_main2.xml使用
 */
public class CyScrollLayout extends ScrollLayout {
    public CyScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CyScrollLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	super.onLayout(changed, l, t, r, b);
    	
    	CyGuideHelper.pLeft = l;
		CyGuideHelper.pTop = t;
		CyGuideHelper.setRc();
    }
}