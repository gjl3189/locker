package com.cyou.cma.clockscreen.widget;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cynad.cma.theme.sdkmeasuer.DefaultMeasureBase;
import com.cyou.cma.clockscreen.activity.GuidePinActivity;
import com.cyou.cma.clockscreen.activity.SecurityActivity;
import com.cyou.cma.clockscreen.activity.SecuritySettingActivity;
import com.cyou.cma.clockscreen.bean.SecurityBean;
import com.cyou.cma.clockscreen.bean.SecurityJudge;
import com.cyou.cma.clockscreen.bean.SecurityLevel;
import com.cyou.cma.clockscreen.password.PasswordHelper;
import com.cyou.cma.clockscreen.util.CyGuideHelper;
import com.cyou.cma.clockscreen.util.SettingsHelper;
import com.cyou.cma.clockscreen.util.Util;
import com.umeng.common.net.s;

public class MyFrameLayout extends FrameLayout {
	private MyImageView mImageView;
	private TextView mTextView1;
	private TextView mTextView2;
	private TextView mTextView3;
	private TextView mTextView4;
	private ArrayList<TextView> mTextViews = new ArrayList<TextView>();
	// private Rect mImageBounds = new Rect();
	private LinearLayout mLinearLayout;
	private TextView mSecurityTip;
	private SecurityView mSecurityView;
	float scale;
	// private SecurityLevel mSecurityLevel;
	private float mDensity;
	float scale2;
	private long mLastClickTime = 0;
	private DefaultMeasureBase mDefaultMeasureBase;

	public MyFrameLayout(final Context context, AttributeSet attrs) {
		super(context, attrs);
		inflate(context, R.layout.hehe, this);
		int a = sp2px(context, 12);
		mDensity = getResources().getDisplayMetrics().density;
		mDefaultMeasureBase = DefaultMeasureBase.getDefaultMeasureBase();
		scale = mDefaultMeasureBase.getHeightRato(context);
		scale2 = mDefaultMeasureBase.getTargetPixel2BasePixelRato(mDensity)
				* mDefaultMeasureBase.getWidthRato(context);
		mImageView = (MyImageView) findViewById(R.id.myImageView);
		mTextView1 = (TextView) findViewById(R.id.textview1);
		mTextView2 = (TextView) findViewById(R.id.textview2);
		mTextView3 = (TextView) findViewById(R.id.textview3);
		mTextView4 = (TextView) findViewById(R.id.textview4);
		float x = Math.min(12, 12 * scale2);
		// float x = 12*scale2 ;
		mTextView1.setTextSize(x);
		mTextView2.setTextSize(x);
		mTextView3.setTextSize(x);
		mTextView4.setTextSize(x);
		mTextViews.add(mTextView1);
		mTextViews.add(mTextView2);
		mTextViews.add(mTextView3);
		mTextViews.add(mTextView4);
		mSecurityTip = (TextView) findViewById(R.id.security_tip);
		mLinearLayout = (LinearLayout) findViewById(R.id.big);
		mSecurityView = (SecurityView) findViewById(R.id.security_view);
		// final View mask = findViewById(R.id.guide_mask);
		// add by Jack
		mSecurityView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				long currentTime = System.currentTimeMillis();
				if (currentTime - mLastClickTime > 500) {
					mLastClickTime = currentTime;
					Activity at = null;
					if (context != null) {
						at = (Activity) context;
						// edit by jiangbin
						// if (!CyGuideHelper.isShow) {
						// at.startActivity(new Intent(context,
						// SecuritySettingActivity.class));
						// } else {// 引导界面
						if ((PasswordHelper.getUnlockType(getContext()) != PasswordHelper.SLIDE_TYPE)
								|| SettingsHelper
										.getApplockEnable(getContext())) {// 老用户曾经设置过密码
							at.startActivity(new Intent(context,
									SecuritySettingActivity.class));
						} else {
							// 跳到引导界面
							if (!Util.getPreferenceBoolean(getContext(),
									"qiip", false)) {
								at.startActivity(new Intent(context,
										GuidePinActivity.class));
								Util.putPreferenceBoolean(getContext(), "qiip", true);
							}else{
								at.startActivity(new Intent(context,
										SecuritySettingActivity.class));
							}

						}
						// }
						// end edit
					}
					CyGuideHelper.isShow = false;
				}
			}
		});
		// end
	}

	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public void updateSecurityLevel(SecurityJudge securityJudge) {
		mImageView.updateSecurityLevel(securityJudge);
		mSecurityView.updateSecurityJudge(securityJudge);
		// for (SecurityBean securityBean : securityLevel.mOns) {
		//
		// }
		// for (SecurityBean securityBean : securityLevel.mOffs) {
		//
		// }
		mSecurityTip.setText(securityJudge.securityLevel.levelTip);
		for (int i = 0; i < securityJudge.mOns.size(); i++) {
			mTextViews.get(i).setText(securityJudge.mOns.get(i).resId);
		}
		for (int i = securityJudge.mOns.size(); i < securityJudge.mOns.size()
				+ securityJudge.mOffs.size(); i++) {
			mTextViews.get(i)
					.setText(
							securityJudge.mOffs.get(i
									- securityJudge.mOns.size()).resId);
		}

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
//		mImageView.layout(mImageView.getLeft(), (int) (50 * scale),
//				mImageView.getRight(),
//				(int) (50 * scale + mImageView.getHeight()));
		mSecurityTip.layout(mSecurityTip.getLeft(), (int) (736 * scale)
				- mSecurityTip.getHeight(), mSecurityTip.getRight(),
				(int) (736 * scale));
		

		// mLinearLayout.layout(mLinearLayout.getLeft(), (int)(820*scale),
		// mLinearLayout.getRight(),
		// (int)(820*scale+mLinearLayout.getHeight()));
		mLinearLayout.layout(mLinearLayout.getLeft(), getHeight()
				- mLinearLayout.getHeight() - (int) (84 * scale),
				mLinearLayout.getRight(), getHeight() - (int) (84 * scale));
		
		float bottomOfTip = mSecurityTip.getBottom();
		float topOfImageView = mLinearLayout.getTop();
		if(bottomOfTip>topOfImageView){
			mSecurityTip.layout(mSecurityTip.getLeft(), (int) ((736 * scale)-(bottomOfTip-topOfImageView))
					- mSecurityTip.getHeight(), mSecurityTip.getRight(),
					(int) (736 * scale-(bottomOfTip-topOfImageView)));
		}
		
		// mLinearLayout.layout(mLinearLayout.getLeft(),
		// (int)(840*scale)-mLinearLayout.getHeight(), mLinearLayout.getRight(),
		// (int)(840*scale));
		//
		// if (changed) {
		// int imageViewLeft = mImageView.getLeft();
		// int imageViewRight = mImageView.getRight();
		// mLinearLayout.layout(imageViewLeft, mLinearLayout.getTop(),
		// imageViewRight, mLinearLayout.getBottom());
		//
		// int layoutLength = mLinearLayout.getRight() -
		// mLinearLayout.getLeft();
		// int layoutLeft = mLinearLayout.getLeft();
		// mTextView1.layout(layoutLeft, mTextView1.getTop(), layoutLeft
		// + (int) (layoutLength / 4.0f), mTextView1.getBottom());
		// mTextView2.layout(layoutLeft + (int) (layoutLength / 4.0f),
		// mTextView2.getTop(), layoutLeft
		// + (int) (2 * (layoutLength / 4.0f)),
		// mTextView2.getBottom());
		// mTextView3.layout(layoutLeft + (int) (2 * (layoutLength / 4.0f)),
		// mTextView3.getTop(), layoutLeft
		// + (int) (3 * (layoutLength / 4.0f)),
		// mTextView3.getBottom());
		// mTextView4.layout(layoutLeft + (int) (3 * (layoutLength / 4.0f)),
		// mTextView4.getTop(), layoutLeft
		// + (int) (4 * (layoutLength / 4.0f)),
		// mTextView4.getBottom());
		// }

	}
}
