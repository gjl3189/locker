package com.cyou.cma.clockscreen.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class ScrollLayout extends ViewGroup {
	private static final String TAG = "ScrollLayout";
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mCurScreen;
	private int mDefaultScreen = 0;
	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;
	private static final int SNAP_VELOCITY = 600;
	private int mTouchState = TOUCH_STATE_REST;
	private int mTouchSlop;
	private float mLastMotionX;
	private float mLastMotionY;
	private OnViewChangeListener mOnViewChangeListener;
	private int offset = 58;
	private int offset2 = 42;

	public enum Direct {
		LEFT, RIGHT, UP, DOWN, UNKNOW;
	}

	private Direct mDirect = Direct.UNKNOW;

	public ScrollLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public interface ScaleListener {
		public void onScale(float scale);
	}

	public ScaleListener mScaleListener;

	public void setScaleListener(ScaleListener scaleListener) {
		this.mScaleListener = scaleListener;
	}

	private float density;

	public ScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScroller = new Scroller(context);
		mCurScreen = mDefaultScreen;
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		density = getResources().getDisplayMetrics().density;
		offset = (int) (offset * density);
		offset2 = (int) (offset2 * density);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		Log.d("jiangbin--", "onLayout-->" + TabFragmentActivity2.v);
		// if (changed) {
		//
		// if (TabFragmentActivity2.v < 1) {
//		Log.d("jiangbin--", "onLayout");
		int childTop = 0;
		final int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View childView = getChildAt(i);
			if (childView.getVisibility() != View.GONE) {

				final int childHeight = childView.getMeasuredHeight();
				final int childWidth = childView.getMeasuredWidth();
//				Log.d("jiangbin--", childView+"onLayout--> childWidth:" + childWidth
//						+ " childHeight:" + childHeight);
				childView.layout(0, childTop, childWidth,
						childTop + childHeight);
				childTop += childHeight;
			}
		}
		// }
		// }
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {

		super.onScrollChanged(l, t, oldl, oldt);
		// Log.d("hehe", "onScrollChanged left-->" + l + " top-->" + t +
		// " oldl -->" + oldl + " oldt -->" + oldt);
		// getHeight()-42
		if (mScaleListener != null) {
			float minScale = 42.0f / 58.0f;
			float currentScale = 1 - (t * 1.0f) / (getHeight() - offset);
			float scale = Math.max(minScale, currentScale);
			mScaleListener.onScale(scale);
//			Log.d("hehe", "onScrollChanged left-->" + l + " top-->" + t
//					+ " oldl -->" + oldl + " oldt -->" + scale);

		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		Log.d("jiangbin--", "onMeasure");

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only canmCurScreen run at EXACTLY mode!");
		}
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only can run at EXACTLY mode!");
		}

		// The children are given the same width and height as the scrollLayout
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			if(i==1){
				int height =(int) (MeasureSpec.getSize(heightMeasureSpec)-42*density);
				int spec= MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(heightMeasureSpec));
				getChildAt(i).measure(widthMeasureSpec, spec);}
			else{
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);}
//			MeasureSpec.getMode(measureSpec)
			
//			getChildAt(i).me
		}
		
	}

	/**
	 * According to the position of current layout scroll to the destination
	 * page.
	 */
	public void snapToDestination() {

		final int screenWidth = getHeight();
		final int destScreen = (getScrollY() + screenWidth / 2) / screenWidth;
//		Log.d("jiangbin--", "snapToDestination--》" + destScreen);
		snapToScreen(destScreen);
	}

	public void snapToScreen(int whichScreen) {
		// 是否可滑动
//		Log.d("jiangbin--", "snapToScreen");
		// if (!isScroll) {
		// this.setToScreen(whichScreen);
		// return;
		// }

		scrollToScreen(whichScreen);
	}

	public void scrollToScreen(int whichScreen) {
//		Log.d("jiangbin--", "scrollToScreen");
		// get the valid layout page
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollY() != (whichScreen * getHeight())) {
			Log.d("jiangbin--", "!== == " + getScrollY() + "---" + whichScreen
					+ "==" + getHeight());
			int delta = whichScreen * getHeight() - getScrollY();
			if (whichScreen == 1) {
				delta -= offset2;
			}
			mScroller.startScroll(0, getScrollY(), 0, delta,
					Math.abs(delta) * 1);// 持续滚动时间 以毫秒为单位
			mCurScreen = whichScreen;
			invalidate(); // Redraw the layout

			if (mOnViewChangeListener != null) {
				mOnViewChangeListener.OnViewChange(mCurScreen);
			}
		} else {
//			Log.d("jiangbin--", "== == " + getScrollY() + "---" + whichScreen
//					+ "==" + getHeight());
		}
		// invalidate();
	}

	public void setToScreen(int whichScreen) {
//		Log.d("jiangbin--", "setToScreen");
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		mCurScreen = whichScreen;
		scrollTo(whichScreen * getHeight(), 0);

		if (mOnViewChangeListener != null) {
			mOnViewChangeListener.OnViewChange(mCurScreen);
		}
	}

	public int getCurScreen() {
		return mCurScreen;
	}

	@Override
	public void computeScroll() {
//		Log.d("jiangbin--", "computeScroll");
		if (mScroller.computeScrollOffset()) {
//			Log.d("jiangbin--", "computeScroll 2");
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			// postInvalidate();
			invalidate();
		} else {

		}
	}

//	 @Override
//	 public boolean onTouchEvent(MotionEvent event) {
//	 if (mCurScreen == 1) return false;
//	 Log.d("jiangbin--", "onTouchEvent fuck");
//	
//	 // 是否可滑动
//	
//	 if (mVelocityTracker == null) {
//	 mVelocityTracker = VelocityTracker.obtain();
//	 }
//	 mVelocityTracker.addMovement(event);
//	 final int action = event.getAction();
//	 final float x = event.getX();
//	 final float y = event.getY();
//	 switch (action) {
//	 case MotionEvent.ACTION_DOWN:
//	 if (mCurScreen == 1) return false;
//	 // Log.e(TAG, "event down!");
//	 if (!mScroller.isFinished()) {
//	 mScroller.abortAnimation();
//	 }
//	 mLastMotionX = x;
//	
//	 // ---------------New Code----------------------
//	 mLastMotionY = y;
//	 // ---------------------------------------------
//	
//	 break;
//	 case MotionEvent.ACTION_MOVE:
//	 int deltaX = (int) (mLastMotionX - x);
//	
//	 int deltaY = (int) (mLastMotionY - y);
//	 Log.d("jaingbin", "DELTAy " + deltaY);
//	 if (Math.abs(deltaY) < 200 && Math.abs(deltaX) > 10)
//	 break;
//	 mLastMotionY = y;
//	 // -------------------------------------
//	
//	 mLastMotionX = x;
//	 Log.d("8888", "getScrollY-->" + getScrollY() + " detay " + deltaY);
//	 int scrollY = getScrollY() + deltaY;
//	 if (scrollY < 0) {
//	 scrollBy(0, 0);
//	 } else {
//	 scrollBy(0, deltaY);
//	 }
//	 // }
//	 break;
//	 case MotionEvent.ACTION_UP:
//	 final VelocityTracker velocityTracker = mVelocityTracker;
//	 velocityTracker.computeCurrentVelocity(1000);
//	 int velocityY = (int) velocityTracker.getYVelocity();
//	 // velocityY<0 是往上滑
//	 Log.d("hehhe", "velocityY--->" + velocityY);
//	 if (velocityY > SNAP_VELOCITY && mCurScreen > 0) {
//	 snapToScreen(mCurScreen - 1);
//	 } else if (velocityY < -SNAP_VELOCITY
//	 && mCurScreen < getChildCount() - 1) {
//	 // Fling enough to move right
//	 // Log.e(TAG, "snap right");
//	 Log.d("jiangbin--", "snapToDestination 111");
//	 snapToScreen(mCurScreen + 1);
//	 } else {
//	 Log.d("jiangbin--", "snapToDestination 222");
//	 snapToDestination();
//	 }
//	 if (mVelocityTracker != null) {
//	 mVelocityTracker.recycle();
//	 mVelocityTracker = null;
//	 }
//	 // }
//	 mTouchState = TOUCH_STATE_REST;
//	 break;
//	 case MotionEvent.ACTION_CANCEL:
//	 mTouchState = TOUCH_STATE_REST;
//	 break;
//	 }
//	 return true;
//	 }
//	
//	 @Override
//	 public boolean onInterceptTouchEvent(MotionEvent ev) {
//	 final int action = ev.getAction();
//	 if (mCurScreen == 1) return false;
//	 if ((action == MotionEvent.ACTION_MOVE)
//	 && (mTouchState != TOUCH_STATE_REST)) {
//	 return true;
//	 }
//	 final float x = ev.getX();
//	 final float y = ev.getY();
//	 switch (action) {
//	 case MotionEvent.ACTION_DOWN:
//	 mLastMotionX = x;
//	 mLastMotionY = y;
//	 mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
//	 : TOUCH_STATE_SCROLLING;
//	 break;
//	 case MotionEvent.ACTION_MOVE:
//	 Log.d("jiangbin2", "mTouchSlop -->" + mTouchSlop);
//	 final int xDiff = (int) Math.abs(mLastMotionY - y);
//	 if (xDiff > mTouchSlop) {
//	 mTouchState = TOUCH_STATE_SCROLLING;
//	 }
//	 break;
//	
//	 case MotionEvent.ACTION_CANCEL:
//	 case MotionEvent.ACTION_UP:
//	 break;
//	 }
//	 boolean touchable = (mTouchState != TOUCH_STATE_REST);
//	 Log.d("touchable", "touchable-->" + touchable);
//	 return touchable;
//	 }

	/**
	 * 设置屏幕切换监听器
	 * 
	 * @param listener
	 */
	public void SetOnViewChangeListener(OnViewChangeListener listener) {
		mOnViewChangeListener = listener;
	}

	/**
	 * 屏幕切换监听器
	 * 
	 * @author liux
	 */
	public interface OnViewChangeListener {
		public void OnViewChange(int view);
	}
}
