package com.cyou.cma.clockscreen.defaulttheme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.util.ImageUtil;

public class BottomView extends View {
	private final String TAG = "BottomView";
	private boolean isInit = false;
	private int mLeftInitX;
	private int mLeftInitY;
	private int mRightInitX;
	private int mRightInitY;
	private int mInitRadius;

	private float mScaleLeft = 0;
	private float mScaleRight = 0;
	private float mScaleSpeed = 0.2f;
	private float mScaleClickMax = 2f;

	private int mAlphaLeft = 128;
	private int mAlphaRight = 128;
	private int mAlphaSpeed = 4;

	private final int STATUS_NORMAL = 0;
	private final int STATUS_LEFT_CLICK = 1;
	private final int STATUS_RIGHT_CLICK = 2;
	private final int STATUS_LEFT_MOVE = 3;
	private final int STATUS_RIGHT_MOVE = 4;
	private final int STATUS_TO_NORMAL_LEFT = 5;
	private final int STATUS_TO_NORMAL_RIGHT = 6;
	private final int STATUS_OPEN_LEFT = 7;
	private final int STATUS_OPEN_RIGHT = 8;
	private int status = STATUS_NORMAL;

	private final int ANIM_TIME = 200 / 10;

	private Paint mCirclePaint = new Paint();
	// private Paint mBottomPaint = new Paint();
	private PaintFlagsDrawFilter drawFilter = new PaintFlagsDrawFilter(0,
			Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

	private int mTopViewWidth;
	private int mTopViewHeight;
	private UnlockListener mUnlockListener;
	private Bitmap mArrowLeftBitmap;
	private Bitmap mArrowRightBitmap;

	public BottomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mCirclePaint.setColor(0xffffffff);
		mCirclePaint.setAlpha(0);
		mAlphaSpeed = (128 + 256) / ANIM_TIME;
		mScaleSpeed = (mScaleClickMax * 2) / (float) ANIM_TIME;
	}

	public void setInitPosition(int leftWidth, int leftHeight) {
		mTopViewWidth = leftWidth;
		mTopViewHeight = leftHeight;

	}

	private void init() {
		this.mLeftInitX = mTopViewWidth / 2;
		this.mLeftInitY = getHeight() - mTopViewHeight / 2;
		this.mRightInitX = getWidth() - mTopViewWidth / 2;
		this.mRightInitY = mLeftInitY;
		this.mInitRadius = (int) (mTopViewWidth / 2 * 1.0f);
		isInit = true;
	}

	long lastTime;

	@Override
	public void draw(Canvas canvas) {
		// super.draw(canvas);
		if (!isInit) {
			init();
		}
		// Log.e(TAG, "onDraw-time-->" + (System.currentTimeMillis() - lastTime)
		// + " status-->" + status);
		// lastTime = System.currentTimeMillis();
		canvas.setDrawFilter(drawFilter);
		switch (status) {
		case STATUS_NORMAL:

			return;
		case STATUS_LEFT_MOVE:
			canvas.drawCircle(mLeftInitX, mLeftInitY, mInitRadius * mScaleLeft,
					mCirclePaint);
			return;
		case STATUS_RIGHT_MOVE:
			canvas.drawCircle(mRightInitX, mRightInitY, mInitRadius
					* mScaleRight, mCirclePaint);
			return;
		case STATUS_LEFT_CLICK:
			if (mScaleLeft >= 1) {
				if (!ImageUtil.isValidBitmap(mArrowLeftBitmap)) {
					mArrowLeftBitmap = ImageUtil.readBitmapWithDensity(
							getContext(),
							R.drawable.icon_defaulttheme_left_arrow);
				}
				if (ImageUtil.isValidBitmap(mArrowLeftBitmap)) {
					canvas.drawBitmap(mArrowLeftBitmap, mLeftInitX
							+ mInitRadius * mScaleLeft, mLeftInitY
							- mArrowLeftBitmap.getHeight() / 2, null);
				}
			}
		case STATUS_OPEN_LEFT:
			canvas.drawCircle(mLeftInitX, mLeftInitY, mInitRadius * mScaleLeft,
					mCirclePaint);
			break;
		case STATUS_TO_NORMAL_LEFT:
			// Log.e(TAG, "mScaleLeft-->" + mScaleLeft + " mAlphaLeft-->"
			// + mAlphaLeft + " bottomAlpha-->" + mBottomAlpha);
			canvas.drawCircle(mLeftInitX, mLeftInitY, mInitRadius * mScaleLeft,
					mCirclePaint);
			// canvas.drawRect(0, 0, mInitRadius * mScaleLeft, mInitRadius *
			// mScaleLeft, mCirclePaint);
			break;
		case STATUS_RIGHT_CLICK:
			if (mScaleRight >= 1) {
				if (!ImageUtil.isValidBitmap(mArrowRightBitmap)) {
					mArrowRightBitmap = ImageUtil.readBitmapWithDensity(
							getContext(),
							R.drawable.icon_defaulttheme_right_arrow);
				}
				if (ImageUtil.isValidBitmap(mArrowRightBitmap)) {
					canvas.drawBitmap(mArrowRightBitmap,
							mRightInitX - mInitRadius * mScaleRight
									- mArrowRightBitmap.getWidth(), mRightInitY
									- mArrowRightBitmap.getHeight() / 2, null);
				}
			}
		case STATUS_OPEN_RIGHT:
			canvas.drawCircle(mRightInitX, mRightInitY, mInitRadius
					* mScaleRight, mCirclePaint);
		case STATUS_TO_NORMAL_RIGHT:
			// Log.e(TAG, "mScaleRight-->" + mScaleRight + " mAlphaRight-->"
			// + mAlphaRight);
			canvas.drawCircle(mRightInitX, mRightInitY, mInitRadius
					* mScaleRight, mCirclePaint);
			break;

		default:
			break;
		}

		move();
		// invalidate();

	}

	private int waitTimes = 0;
	private int waitTotalTimes = 50;
	private boolean needWait = false;

	// private void move(){
	// mScaleLeft+=0.2;
	// invalidate();
	// }

	private boolean needReset = false;

	private void move() {
		if (needReset) {
			reset();
		}
		if (needWait) {
			waitTimes++;
			if (waitTimes >= waitTotalTimes) {
				waitTimes = 0;
				needWait = false;
			}
			invalidate();
			return;
		}
		switch (status) {
		case STATUS_NORMAL:
			return;
		case STATUS_LEFT_MOVE:
			return;

		case STATUS_TO_NORMAL_LEFT:
		case STATUS_LEFT_CLICK:
			mScaleLeft += mScaleSpeed;
			mAlphaLeft += mAlphaSpeed;
			if (mScaleLeft >= mScaleClickMax) {
				needWait = true;
				mScaleLeft = mScaleClickMax;
				mScaleSpeed = -mScaleSpeed;
			} else if (mScaleLeft <= 0) {
				mScaleSpeed = -mScaleSpeed;
				needReset = true;
				invalidate();
				// reset();
				return;
			}
			if (mAlphaLeft >= 255) {
				mAlphaLeft = 255;
				mAlphaSpeed = -mAlphaSpeed;
			} else if (mAlphaLeft <= 0) {
				mAlphaSpeed = -mAlphaSpeed;
				needReset = true;
				invalidate();
				// reset();
				return;
			}
			mCirclePaint.setAlpha(mAlphaLeft);
			break;
		case STATUS_TO_NORMAL_RIGHT:
		case STATUS_RIGHT_CLICK:
			mScaleRight += mScaleSpeed;
			mAlphaRight += mAlphaSpeed;
			if (mScaleRight >= mScaleClickMax) {
				needWait = true;
				mScaleRight = mScaleClickMax;
				mScaleSpeed = -mScaleSpeed;
			} else if (mScaleRight <= 0) {
				mScaleSpeed = -mScaleSpeed;
				needReset = true;
				invalidate();
				// reset();
				return;
			}
			if (mAlphaRight >= 255) {
				mAlphaRight = 255;
				mAlphaSpeed = -mAlphaSpeed;
			} else if (mAlphaRight <= 0) {
				mAlphaSpeed = -mAlphaSpeed;
				needReset = true;
				invalidate();
				// reset();
				return;
			}
			mCirclePaint.setAlpha(mAlphaRight);
			break;
		case STATUS_OPEN_LEFT:
//			mScaleLeft += mScaleSpeed;
			mAlphaLeft += mAlphaSpeed;
			if (mAlphaLeft <= 0) {
				mAlphaLeft = 0;
				mCirclePaint.setAlpha(mAlphaLeft);
				needReset = true;
				invalidate();
				// reset();
				return;
			}
			mCirclePaint.setAlpha(mAlphaLeft);
			break;
		case STATUS_OPEN_RIGHT:
//			mScaleRight += mScaleSpeed;
			mAlphaRight += mAlphaSpeed;
			if (mAlphaRight <= 0) {
				mAlphaRight = 0;
				mCirclePaint.setAlpha(mAlphaRight);
				needReset = true;
				invalidate();
				return;
			}
			mCirclePaint.setAlpha(mAlphaRight);
			break;
		default:
			break;
		}
		invalidate();
	}

	private void reset() {
		// invalidate();
		needReset = false;
		switch (status) {
		case STATUS_OPEN_LEFT:
		case STATUS_OPEN_RIGHT:
			status = STATUS_NORMAL;
			mUnlockListener.onUnlockAnimEnd();
			break;
		case STATUS_LEFT_CLICK:
		case STATUS_RIGHT_CLICK:
			status = STATUS_NORMAL;
			mUnlockListener.onClickAnimEnd();
			break;
		default:
			status = STATUS_NORMAL;
			break;
		}

		invalidate();
	}

	public void unlockAnim(boolean left) {
		status = left ? STATUS_OPEN_LEFT : STATUS_OPEN_RIGHT;
		// mUnlockMaxScale = (getWidth() - mLeftInitX) / (float) mInitRadius;
		resetSpeed(left, 0, 0, ANIM_TIME);
		// mBottomAlphaSpeed = 255 / ANIM_TIME;
		invalidate();
	}

	public void resetAnim(boolean left) {
		status = left ? STATUS_TO_NORMAL_LEFT : STATUS_TO_NORMAL_RIGHT;
		resetSpeed(left, 0, 0, ANIM_TIME);
		invalidate();
	}

	public void doClickAnim(boolean left) {
		status = left ? STATUS_LEFT_CLICK : STATUS_RIGHT_CLICK;
		mScaleRight = 0f;
		mAlphaRight = 0;
		mScaleLeft = 0f;
		mAlphaLeft = 0;
		mAlphaSpeed = Math.abs(mAlphaSpeed);
		mScaleSpeed = Math.abs(mScaleSpeed);
		resetSpeed(left, mScaleClickMax, 255, ANIM_TIME / 2);
		invalidate();
	}

	public void onMoveAnim(boolean left, float scale) {
		status = left ? STATUS_LEFT_MOVE : STATUS_RIGHT_MOVE;
		if (left) {
			mScaleLeft = scale;
			mAlphaLeft = (int) (100 * (scale));
			if (mAlphaLeft > 255) {
				mAlphaLeft = 255;
			}
			mCirclePaint.setAlpha(mAlphaLeft);
		} else {
			mScaleRight = scale;
			mAlphaRight = (int) (100 * (scale));
			if (mAlphaRight > 255) {
				mAlphaRight = 255;
			}
			mCirclePaint.setAlpha(mAlphaRight);
		}
		invalidate();
	}

	private void resetSpeed(boolean left, float targetScale, int targetAlpha,
			float time) {
		if (left) {
			mScaleSpeed = (targetScale - mScaleLeft) / time;
			mAlphaSpeed = (int) ((targetAlpha - mAlphaLeft) / time);
		} else {
			mScaleSpeed = (targetScale - mScaleRight) / time;
			mAlphaSpeed = (int) ((targetAlpha - mAlphaRight) / time);
		}
		if (mAlphaSpeed < 1 && mAlphaSpeed > 0) {
			mAlphaSpeed = 1;
		} else if (mAlphaSpeed > -1 && mAlphaSpeed < 0) {
			mAlphaSpeed = -1;
		}
	}

	public boolean isInit() {
		return isInit;
	}

	public void setUnlockListener(UnlockListener unlockListener) {
		mUnlockListener = unlockListener;
	}

	public interface UnlockListener {
		public void onUnlockAnimEnd();

		public void onClickAnimEnd();
	}

	public void resetDirect() {
		status = STATUS_NORMAL;
		invalidate();
	}

	public void cleanUp() {
		ImageUtil.recycleBitmap(mArrowLeftBitmap);
		ImageUtil.recycleBitmap(mArrowRightBitmap);
	}

}
