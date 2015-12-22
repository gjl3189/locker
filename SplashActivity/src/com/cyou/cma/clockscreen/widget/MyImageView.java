package com.cyou.cma.clockscreen.widget;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.cynad.cma.locker.R;
import com.cynad.cma.theme.sdkmeasuer.DefaultMeasureBase;
import com.cyou.cma.clockscreen.activity.SecuritySettingActivity;
import com.cyou.cma.clockscreen.bean.SecurityBean;
import com.cyou.cma.clockscreen.bean.SecurityJudge;
import com.cyou.cma.clockscreen.bean.SecurityLevel;

public class MyImageView extends View {
	private int width;
	private int height;
	private Drawable mBorderDrawable;
	private Drawable mMaskThreeDrawable;
	private Drawable mMaskZeroDrawable;
	private Drawable mMaskOneDrawable;
	private Drawable mMaskTwoDrawable;
	private SecurityJudge mSecurityJudge;
	private Drawable mBorderDrawable1;
	private Drawable mBorderDrawable2;
	private Drawable mBorderDrawable3;
	private Drawable mBorderDrawable4;
	private RectF mFirstRectF;
	private RectF mSecondRectF;
	private RectF mThirdRectF;
	private RectF mFourthRectF;
	private Drawable mYesDrawable;
	private int which = -1;
	private ArrayList<SecurityBean> mSecurityBeans = new ArrayList<SecurityBean>();
	private int mYesWidth;
	private int mYesHeight;

	private Paint mPaintText;
	private float mDensity;
	private float scale;
	DefaultMeasureBase mDefaultMeasureBase;

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDensity = getResources().getDisplayMetrics().density;
		mDefaultMeasureBase = DefaultMeasureBase.getDefaultMeasureBase(); 
//		scale = mDefaultMeasureBase
//				.getTargetPixel2BasePixelRato(mDensity)
//				* mDefaultMeasureBase.getWidthRato(getContext());
		scale = mDefaultMeasureBase
				.getTargetPixel2BasePixelRato(mDensity)
				* mDefaultMeasureBase.getHeightRato(getContext());
		mBorderDrawable = getResources().getDrawable(R.drawable.border);
		mBorderDrawable1 = getResources().getDrawable(R.drawable.border1);
		mBorderDrawable2 = getResources().getDrawable(R.drawable.border2);
		mBorderDrawable3 = getResources().getDrawable(R.drawable.border3);
		mBorderDrawable4 = getResources().getDrawable(R.drawable.border4);
		mYesDrawable = getResources().getDrawable(R.drawable.yes);
		width = (int) (mBorderDrawable.getIntrinsicWidth()*scale);
		height = (int) (mBorderDrawable.getIntrinsicHeight()*scale);
		mMaskThreeDrawable = getResources().getDrawable(R.drawable.mask3);
		mMaskThreeDrawable.setBounds(new Rect(0, 0, width, height));
		mMaskZeroDrawable = getResources().getDrawable(R.drawable.mask0);
		mMaskZeroDrawable.setBounds(new Rect(0, 0, width, height));
		mMaskOneDrawable = getResources().getDrawable(R.drawable.mask1);
		mMaskOneDrawable.setBounds(new Rect(0, 0, width, height));
		mMaskTwoDrawable = getResources().getDrawable(R.drawable.mask2);
		mMaskTwoDrawable.setBounds(new Rect(0, 0, width, height));
		mBorderDrawable.setBounds(new Rect(0, 0, width, height));
		mBorderDrawable1.setBounds(new Rect(0, 0, width, height));
		mBorderDrawable2.setBounds(new Rect(0, 0, width, height));
		mBorderDrawable3.setBounds(new Rect(0, 0, width, height));
		mBorderDrawable4.setBounds(new Rect(0, 0, width, height));
		mYesWidth = (int) (mYesDrawable.getIntrinsicWidth()*scale);
		mYesHeight = (int) (mYesDrawable.getIntrinsicHeight()*scale);
		mYesDrawable.setBounds(new Rect(0, 0, mYesWidth, mYesHeight));
		mFirstRectF = new RectF();
		mSecondRectF = new RectF();
		mThirdRectF = new RectF();
		mFourthRectF = new RectF();
		mFirstRectF.set(0, 0, (1.f / 4.f) * width, height);
		mSecondRectF.set((1.f / 4.f) * width, 0, (2.f / 4.f) * width, height);
		mThirdRectF.set((2.f / 4.f) * width, 0, (3.f / 4.f) * width, height);
		mFourthRectF.set((3.f / 4.f) * width, 0, width, height);
		mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintText.setColor(Color.WHITE);
		mPaintText.setTextSize(16 * getResources().getDisplayMetrics().density*scale);
		mPaintText.setTextAlign(Align.CENTER);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(width, height);
	}

	public void updateSecurityLevel(SecurityJudge securityJudge) {
		mSecurityJudge = securityJudge;
		mSecurityBeans.clear();
		for (int i = 0; i < securityJudge.mOns.size(); i++) {
			mSecurityBeans.add(securityJudge.mOns.get(i));
		}
		for (int i = securityJudge.mOns.size(); i < securityJudge.mOns.size()
				+ securityJudge.mOffs.size(); i++) {
			mSecurityBeans.add(securityJudge.mOffs.get(i
					- securityJudge.mOns.size()));
		}
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (mFirstRectF.contains(x, y)) {
				which = 0;
			} else if (mSecondRectF.contains(x, y)) {
				which = 1;
			} else if (mThirdRectF.contains(x, y)) {
				which = 2;
			} else {
				which = 3;
			}
			invalidate();
			return true;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			gotoActivity();
			which = -1;
			invalidate();

			break;
		}
		return super.onTouchEvent(event);
	}

	private void gotoActivity() {
		if (which == 3) {
			getContext().startActivity(
					new Intent(getContext(), SecuritySettingActivity.class));
		} else {
			SecurityBean bean = mSecurityBeans.get(which);
			if (bean != null)
				bean.gotoActivity(getContext());
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mSecurityJudge != null) {
			switch (mSecurityJudge.securityLevel) {
			case LEVELA:
				mMaskThreeDrawable.draw(canvas);
				break;
			case LEVELB:
				mMaskOneDrawable.draw(canvas);
				break;
			case LEVELC:
				mMaskZeroDrawable.draw(canvas);
				break;
			case LEVELD:
				// mMaskZeroDrawable.draw(canvas);
				break;
			}
		}
//		if (mSecurityJudge != null) {
//			if (mSecurityJudge.securityLevel == SecurityLevel.LEVELA) {
				canvas.save();
				canvas.translate(width * (3.f / 4)
						+ ((width * (1.f / 4) - mYesWidth) / 2),
						(height - mYesHeight) / 2);
				mYesDrawable.draw(canvas);
				canvas.restore();
//			}
//		}

		mBorderDrawable.draw(canvas);
		switch (which) {
		case 0:
			mBorderDrawable1.draw(canvas);
			break;
		case 1:
			mBorderDrawable2.draw(canvas);
			break;
		case 2:
			mBorderDrawable3.draw(canvas);
			break;
		case 3:
			mBorderDrawable4.draw(canvas);
			break;

		}
		drawOne(canvas);
		drawTwo(canvas);
		drawThree(canvas);
	}

	private void drawThree(Canvas canvas) {

		canvas.drawText("3", width * (5.f / 8), height / 2 + getTextHeight()
				/ 4, mPaintText);

	}

	private void drawTwo(Canvas canvas) {

		canvas.drawText("2", width * (3.f / 8), height / 2 + getTextHeight()
				/ 4, mPaintText);

	}

	private void drawOne(Canvas canvas) {

		canvas.drawText("1", width * (1.f / 8), height / 2 + getTextHeight()
				/ 4, mPaintText);
	}

	public float getTextHeight() {
		FontMetrics fm = mPaintText.getFontMetrics();
		return (float) Math.ceil(fm.descent - fm.top);
	}

	public float getTextWidth(String text) {
		return mPaintText.measureText(text);
	}
}
