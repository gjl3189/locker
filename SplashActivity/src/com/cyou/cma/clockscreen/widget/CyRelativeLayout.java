package com.cyou.cma.clockscreen.widget;

import com.cynad.cma.locker.R;
import com.cynad.cma.theme.sdkmeasuer.DefaultMeasureBase;
import com.cyou.cma.clockscreen.util.CyGuideHelper;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * add by Jack 2.3没有onLayoutListener这个类，复写方法解决适配问题 仅供MyFrameLayout试用
 */
public class CyRelativeLayout extends RelativeLayout {
	SecurityView mSecurityView;
	TextView mSecurityLevel;
	float mDensity;
	DefaultMeasureBase mDefaultMeasureBase;
	float mScale = 1.0f;

	public CyRelativeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CyRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		inflate(context, R.layout.security_view_level, this);
		mSecurityView = (SecurityView) findViewById(R.id.security_view);
		mSecurityLevel = (TextView) findViewById(R.id.security_level);
		mDensity = getResources().getDisplayMetrics().density;
		mDefaultMeasureBase = DefaultMeasureBase.getDefaultMeasureBase();
		mScale = 
				 mDefaultMeasureBase.getWidthRato(getContext());

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		CyGuideHelper.vLeft = l;
		CyGuideHelper.vTop = t;
		CyGuideHelper.setRc();
		mSecurityLevel.layout(mSecurityLevel.getLeft(), (int) (160 * mScale),
				mSecurityLevel.getRight(), (int) (160 * mScale)
						+ mSecurityLevel.getHeight());
	}
}