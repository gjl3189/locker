package com.cyou.cma.clockscreen.widget;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

import com.cynad.cma.locker.R;
import com.cynad.cma.theme.sdkmeasuer.DefaultMeasureBase;
import com.cyou.cma.clockscreen.bean.SecurityJudge;
import com.cyou.cma.clockscreen.event.DrawFinishEvent;
import com.cyou.cma.clockscreen.util.CyGuideHelper;
import com.cyou.cma.clockscreen.util.Util;

import de.greenrobot.event.EventBus;

public class SecurityView extends View {
	private RectF outRingRectF;

	private Paint mCirclePaint;
	private Paint mCenterPaint2;
	private Scroller mUpScroller;
	private Scroller mBackScroller;
	private Scroller mLevelScroller;
	private float mDensity;
	private int mwidth = 276;
	// private int mOuterRadius =
	private int mOuterWidth = 205;
	private float mStrokeWidth = 22;

	private Drawable mDrawableA;
	private Drawable mDrawableB;
	private Drawable mDrawableC;
	private Drawable mDrawableD;
	int drawableWidth;
	private SecurityJudge mSecurityJudge;
	// private Drawable mDrawalbeSecurityLock;
	private int DURATION = 500;
	private boolean stable = false;;
	private Random random = new Random();
	private Drawable[] drawables = new Drawable[4];

	private DefaultMeasureBase mDefaultMeasureBase;

	// private int m
	private boolean is320x480() {
		return Util.getScreenWidth(getContext()) == 320;
	}

	float scale = 1.0f;

	public SecurityView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// if(is320x480()){
		// scale=0.6f;
		// }
		mDensity = context.getResources().getDisplayMetrics().density;
		mDefaultMeasureBase = DefaultMeasureBase.getDefaultMeasureBase();
		// scale= mDefaultMeasureBase.getTargetPixel2BasePixelRato(mDensity) *
		// mDefaultMeasureBase
		// .getWidthRato(getContext());
		scale = mDefaultMeasureBase.getTargetPixel2BasePixelRato(mDensity)
				* mDefaultMeasureBase.getHeightRato(getContext());
		mStrokeWidth *= mDensity * scale;
		mUpScroller = new Scroller(getContext(),
				new AccelerateDecelerateInterpolator());
		mBackScroller = new Scroller(getContext(),
				new AccelerateDecelerateInterpolator());
		mLevelScroller = new Scroller(getContext(),
				new AccelerateDecelerateInterpolator());
		// outRingRectF = new RectF(100, 1s00, mRadius * mDensity, mRadius *
		// mDensity);
		float left = (mwidth - mOuterWidth) / 2 * mDensity * scale;
		float right = left + mOuterWidth * mDensity * scale;
		mwidth = (int) (mwidth * mDensity * scale);
		outRingRectF = new RectF(left, left, right, right);

		// add by Jack
		int len = (int) (mStrokeWidth * 2.1);
		CyGuideHelper.cLeft = (int) (outRingRectF.left) - len;
		CyGuideHelper.cTop = (int) (outRingRectF.top) - len;
		CyGuideHelper.cRight = (int) (outRingRectF.right) + len;
		CyGuideHelper.cBottom = (int) (outRingRectF.bottom) + len;
		CyGuideHelper.setRc();
		// end

		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setColor(Color.argb(77, 255, 255, 255));
		mCirclePaint.setStyle(Style.STROKE);
		mCirclePaint.setStrokeWidth(mStrokeWidth);
		mCirclePaint.setStrokeCap(Cap.ROUND);

		mCenterPaint2 = new Paint();
		mCenterPaint2.setAntiAlias(true);
		mCenterPaint2.setColor(Color.WHITE);
		mCenterPaint2.setStyle(Style.STROKE);
		mCenterPaint2.setStrokeWidth(mStrokeWidth);
		mCenterPaint2.setStrokeCap(Cap.ROUND);
		mDrawableA = getResources().getDrawable(R.drawable.a);
		mDrawableB = getResources().getDrawable(R.drawable.b);
		mDrawableC = getResources().getDrawable(R.drawable.c);
		mDrawableD = getResources().getDrawable(R.drawable.d);
		drawables[0] = mDrawableA;
		drawables[1] = mDrawableB;
		drawables[2] = mDrawableC;
		drawables[3] = mDrawableD;
		// mDrawalbeSecurityLock = getResources().getDrawable(
		// R.drawable.security_lock);
		drawableWidth = (int) (mDrawableA.getIntrinsicWidth() * scale);
		int drawableHeight = (int) (mDrawableA.getIntrinsicHeight() * scale);
		mDrawableA.setBounds(0, 0, drawableWidth, drawableHeight);
		mDrawableB.setBounds(0, 0, drawableWidth, drawableHeight);
		mDrawableC.setBounds(0, 0, drawableWidth, drawableHeight);
		// mDrawalbeSecurityLock.setBounds(0, 0,
		// mDrawalbeSecurityLock.getIntrinsicWidth(),
		// mDrawalbeSecurityLock.getIntrinsicHeight());
		mDrawableD.setBounds(0, 0, drawableWidth, drawableHeight);
		doAnimation();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(mwidth, mwidth);
	}

	private int index = 0;
	private int startDegree = 115;
	private int allDegree = 310;
	boolean is320 = false;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawArc(outRingRectF, startDegree, allDegree, false,
				mCirclePaint);
		canvas.drawArc(outRingRectF, startDegree, mSweepDegree, false,
				mCenterPaint2);
		if (stable) {
			if (this.mSecurityJudge != null) {
				canvas.save();
				canvas.translate((mwidth - drawableWidth) / 2, 122 * mDensity
						* scale);
				switch (mSecurityJudge.securityLevel) {
				case LEVELA:
					mDrawableA.draw(canvas);
					break;

				case LEVELB:
					mDrawableB.draw(canvas);
					break;
				case LEVELC:
					mDrawableC.draw(canvas);
					break;
				case LEVELD:
					mDrawableD.draw(canvas);
					break;
				}

				canvas.restore();
			}
		} else {
			canvas.save();
			canvas.translate((mwidth - drawableWidth) / 2, 122 * mDensity
					* scale);
			drawables[index].draw(canvas);
			index = (++index) % 4;
			canvas.restore();
		}
		// canvas.save();
		// canvas.translate((width - drawableWidth) / 2, 222 * mDensity);
		// mDrawalbeSecurityLock.draw(canvas);
		// canvas.restore();

	}

	// private float sweepAngle;

	public void doAnimation() {
		mUpScroller.startScroll(0, 0, allDegree, 0, DURATION);
	}

	private float mSweepDegree;

	@Override
	public void computeScroll() {
		// Log.d("hahhah", "mSweepDegreee---> computeScroll");
		if (mUpScroller.computeScrollOffset()) {
			if (!mUpScroller.isFinished()) {
				mSweepDegree = mUpScroller.getCurrX();
				// Log.d("hahhah", "mSweepDegreee---> 1");
				invalidate();
			} else {
				// Log.d("hahhah", "mSweepDegreee---> mBackScroller");
				mBackScroller
						.startScroll(allDegree, 0, -allDegree, 0, DURATION);
				invalidate();
			}
		} else if (mBackScroller.computeScrollOffset()) {
			if (!mBackScroller.isFinished()) {
				mSweepDegree = mBackScroller.getCurrX();
				// Log.d("hahhah", "mSweepDegreee---> " + mSweepDegree);
				invalidate();
			} else {
				if (this.mSecurityJudge != null) {
					int dx = 0;
					switch (mSecurityJudge.securityLevel) {
					case LEVELA:
						dx = allDegree;
						break;
					case LEVELB:
						dx = (int) (allDegree * (2.0f / 3));
						break;
					case LEVELC:
						dx = (int) (allDegree * (1.0f / 3));
						break;
					case LEVELD:
						dx = 10;
						break;
					}
					mLevelScroller.startScroll(0, 0, dx, 0, (DURATION));
					invalidate();
				}
			}
		} else if (mLevelScroller.computeScrollOffset()) {
			if (!mLevelScroller.isFinished()) {
				mSweepDegree = mLevelScroller.getCurrX();
				invalidate();
			} else {
				stable = true;
				if (this.mSecurityJudge != null) {

					switch (mSecurityJudge.securityLevel) {
					case LEVELA:
						mSweepDegree = allDegree;
						break;
					case LEVELB:
						mSweepDegree = (int) (allDegree * (2.0f / 3));
						break;
					case LEVELC:
						// mSweepDegree = 90;
						mSweepDegree = (int) (allDegree * (1.0f / 3));
						break;
					case LEVELD:
						mSweepDegree = 10;
						break;
					}
				}
				invalidate();
				if (!CyGuideHelper.isShow) {
					postDelayed(new Runnable() {

						@Override
						public void run() {
							EventBus.getDefault().post(new DrawFinishEvent());

						}
					}, 300);

				}
			}
		}
	}

	public void updateSecurityJudge(SecurityJudge securityJudge) {
		this.mSecurityJudge = securityJudge;
		switch (mSecurityJudge.securityLevel) {
		case LEVELA:
			mSweepDegree = allDegree;
			break;
		case LEVELB:
			mSweepDegree = (int) (allDegree * (2.0f / 3));
			break;
		case LEVELC:
			// mSweepDegree = 90;
			mSweepDegree = (int) (allDegree * (1.0f / 3));
			break;
		case LEVELD:
			mSweepDegree = 10;
			break;

		}
		invalidate();
	}
}
