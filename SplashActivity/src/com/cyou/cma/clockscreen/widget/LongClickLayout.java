package com.cyou.cma.clockscreen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

public class LongClickLayout extends FrameLayout {

	private int mLastMotionX, mLastMotionY;
	private Runnable mLongPressRunnable;
	private boolean isMoved;
	private boolean isLongPressed = false;

	private static final int TOUCH_SLOP = 20;
	long mLongPressTime = ViewConfiguration.getLongPressTimeout();

	private LockLongClickListener mListener;

	public LongClickLayout(Context context) {
		super(context);
	}

	public LongClickLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mLongPressRunnable = new Runnable() {
			@Override
			public void run() {
				// Log.e("TouchEventFather", "mLongPressRunnable");
				// Toast.makeText(getContext(), "mLongPressRunnable",
				// Toast.LENGTH_LONG).show();
				isLongPressed = true;
				// performLongClick();
				if (mListener != null) {
					mListener.onLongClick();
				}
			}
		};
	}

	public boolean dispatchTouchEvent(MotionEvent event) {
		// Log.e("TouchEventFather",
		// "dispatchTouchEvent --> "
		// + TouchEventUtil.getTouchAction(event.getAction()));
		if (mListener == null) {
			return super.dispatchTouchEvent(event);
		}
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			isMoved = false;
			isLongPressed = false;
			postDelayed(mLongPressRunnable, mLongPressTime);
			break;
		case MotionEvent.ACTION_MOVE:
			if (isMoved)
				break;
			if (Math.abs(mLastMotionX - x) > TOUCH_SLOP
					|| Math.abs(mLastMotionY - y) > TOUCH_SLOP) {
				// 移动超过阈值，则表示移动了
				isMoved = true;
				removeCallbacks(mLongPressRunnable);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			removeCallbacks(mLongPressRunnable);
			break;
		}
		// if (isLongPressed) {
		// return true;
		// } else {
		return super.dispatchTouchEvent(event);
		// }
		// return true;
	}

	public boolean onInterceptTouchEvent(MotionEvent event) {
		// Log.e("TouchEventFather",
		// "onInterceptTouchEvent -->"
		// + TouchEventUtil.getTouchAction(event.getAction()));
		if (isLongPressed && mListener != null) {
			return true;
		} else {
			return super.onInterceptTouchEvent(event);
		}
	}

	// public boolean onTouchEvent(MotionEvent ev) {
	// // Log.e("TouchEventFather",
	// // "onTouchEvent --> "
	// // + TouchEventUtil.getTouchAction(ev.getAction()));
	// if (isLongPressed) {
	// return true;
	// } else {
	// return super.onTouchEvent(ev);
	// }
	// }

	// @Override
	// public boolean performLongClick() {
	// return super.performLongClick();
	// }
	//

	public void setLockLongClickListener(LockLongClickListener listener) {
		this.mListener = listener;
	}

	public interface LockLongClickListener {
		public void onLongClick();
	}

}
