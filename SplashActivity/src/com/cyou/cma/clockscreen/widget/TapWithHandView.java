package com.cyou.cma.clockscreen.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.Util;

public class TapWithHandView extends ImageView {
	private final String TAG = "TapWithHandView";
	private Bitmap mSrcBitmap;
	private Bitmap mTipBitmap;

	private float mSrcWidth;
	private float mSrcHeight;
	private float mCircleCenterX;
	private float mCircleCenterY;
	private float mCircleNormalR;
	private float mSrcPositionX;
	private float mSrcPositionY;
	private int mDesiredWidth;
	private int mDesiredHeight;

	private DrawThread mDrawThread;
	private Thread thread;

	private float REFRE_WIDTH = 1;
	private float REFRE_HEIGHT = 1;

	@SuppressLint("NewApi")
	public TapWithHandView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		REFRE_WIDTH = Util.getScreenWidth(context) / 720f;
		REFRE_HEIGHT = Util.getScreenHeight(context) / 1280f;
		if (getDrawable() != null) {
			mSrcBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
		}
		// mSettingsBitmap = ImageUtil.readBitmapWithDensity(context,
		// R.drawable.icon_defaulttheme_hint_settings);
		mTipBitmap = ImageUtil.readBitmapWithDensity(context,
				R.drawable.icon_tip_tap_hand);

		paintCircle.setAlpha(180);
		paintCircle.setStyle(Paint.Style.STROKE);
		paintCircle.setColor(Color.WHITE);
		paintCircle.setStrokeWidth(2);
		init();
	}

	private void init() {
		if (mSrcBitmap != null) {
			mSrcWidth = mSrcBitmap.getWidth();
			mSrcHeight = mSrcBitmap.getHeight();
		}
		mCircleNormalR = mTipBitmap.getWidth() * 0.3f;

		mDesiredHeight = (int) (getPaddingTop() + getPaddingBottom()
				+ Math.max(mSrcHeight * 0.8f, mCircleNormalR)
				+ mTipBitmap.getHeight() + 1);
		// mDesiredWidth = (int) (mSrcWidth
		// + Math.max(20 * REFRE_WIDTH, mCircleNormalR) + getPaddingLeft() +
		// getPaddingRight());
		mDesiredWidth = (int) Math.max(
				Math.max(mSrcWidth, mTipBitmap.getWidth()),
				(mTipBitmap.getWidth() - 20 * REFRE_WIDTH + mCircleNormalR));

		if (getScaleType() == ScaleType.FIT_END) {
			mSrcPositionX = mDesiredWidth - mSrcWidth;
		} else {
			mSrcPositionX = getPaddingLeft();// + mSrcWidth * 0.2f;
		}
		mSrcPositionY = getPaddingTop();
		mCircleCenterX = getPaddingLeft()
				+ Math.max(mCircleNormalR, 20 * REFRE_WIDTH)
				+ paintCircle.getStrokeWidth();
		mCircleCenterY = mSrcPositionY
				+ Math.max(mSrcHeight * 0.8f, mCircleNormalR)
				+ paintCircle.getStrokeWidth();

	}

	public void startDraw() {
		waitTime = 0;
		mNeedCircleWait = false;
		if (mDrawThread != null)
			mDrawThread.stop();
		mDrawThread = new DrawThread();
		thread = new Thread(mDrawThread);
		thread.start();
	}

	public void stopDraw() {
		if (mDrawThread != null) {
			mDrawThread.stop();
		}
		thread = null;
		mDrawThread = null;
		postInvalidate();
	}

	private class DrawThread implements Runnable {

		private boolean mRun = true;

		@Override
		public void run() {
			while (mRun) {
				try {
					postInvalidate();
				} catch (Exception e) {
					if (Util.DEBUG) {
						Util.printException(e);
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Util.printException(e);
				}
			}
		}

		public void stop() {
			mRun = false;
		}

	}

	private PaintFlagsDrawFilter drawFilter = new PaintFlagsDrawFilter(0,
			Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		if (mCircleCenterX <= 0 || mCircleCenterY <= 0) {
			init();
		}
		canvas.setDrawFilter(drawFilter);
		try {
			drawSrc(canvas);
			drawCircle(canvas);
			drawTip(canvas);
			move();
		} catch (Exception e) {
			if (Util.DEBUG) {
				Util.printException(e);
			}
		}
	}

	private void drawSrc(Canvas canvas) {
		if (ImageUtil.isValidBitmap(mSrcBitmap)) {
			canvas.drawBitmap(mSrcBitmap, mSrcPositionX, mSrcPositionY, null);
		}
	}

	private void drawTip(Canvas canvas) {
		if (ImageUtil.isValidBitmap(mTipBitmap)) {
			canvas.drawBitmap(mTipBitmap, mCircleCenterX - 20 * REFRE_WIDTH,
					mCircleCenterY, null);
		}
	}

	private boolean mNeedCircleWait = false;
	private int waitTime = 0;
	private Paint paintCircle = new Paint();

	private float[] scales = new float[] { 0.5f, 0.75f, 1f };
	private int index = 0;

	private void drawCircle(Canvas canvas) {
		if (mNeedCircleWait)
			return;
		for (int i = 0; i < scales.length; i++) {
			paintCircle.setAlpha(getAlpha(i));
			canvas.drawCircle(mCircleCenterX, mCircleCenterY, scales[i]
					* mCircleNormalR, paintCircle);
		}
	}

	private int getAlpha(int i) {
		int step = Math.abs(i - index % (scales.length * 2));
		switch (step) {
		case 0:
			return 250;
		case 1:
			return 180;
		case 2:
			return 100;
		case 3:
			return 60;
		case 4:
			return 30;
		case 5:
			return 0;
		default:
			return 0;
		}
	}

	private int mStep = 1;

	private void move() {
		if (!mNeedCircleWait) {
			index += mStep;
			if (index % (scales.length * 2) == 0) {
				mStep = -mStep;
				if (index != 0) {
					mNeedCircleWait = true;
					index = 0;
					mStep = 1;
				}

			}
		} else {
			waitTime++;
			if (waitTime == 3) {
				mNeedCircleWait = false;
				waitTime = 0;
			}
		}

	}

	public void onPause() {
		stopDraw();
	}

	public void onResume() {
		startDraw();
	}

	public void cleanUp() {
		stopDraw();
		// if (mSrcBitmap != null && !mSrcBitmap.isRecycled()) {
		// mSrcBitmap.recycle();
		// }
		mSrcBitmap = null;
		if (mTipBitmap != null && !mTipBitmap.isRecycled()) {
			mTipBitmap.recycle();
		}
		mTipBitmap = null;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			width = Math.min(mDesiredWidth, widthSize);
		} else {
			width = mDesiredWidth;
		}

		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			height = Math.min(mDesiredHeight, heightSize);
		} else {
			height = mDesiredHeight;
		}
		setMeasuredDimension(width, height);
	}

}
