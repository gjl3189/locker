/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyou.cma.clockscreen.widget;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.material.LFrameLayout;
import com.cyou.cma.clockscreen.widget.material.MaterialStyle;

/**
 * This widget implements the dynamic action bar tab behavior that can change
 * across different configurations or circumstances.
 */
public class TabPageIndicator extends HorizontalScrollView implements
		PageIndicator {
	/** Title text used when no title is provided by the adapter. */
	private static final CharSequence EMPTY_TITLE = "";

	/**
	 * Interface for a callback when the selected tab has been reselected.
	 */
	public interface OnTabReselectedListener {
		/**
		 * Callback when the selected tab has been reselected.
		 * 
		 * @param position
		 *            Position of the current center item.
		 */
		void onTabReselected(int position);
	}

	public interface HeHeListener {
		void onHehe();
	}

	private Runnable mTabSelector;

	private final OnClickListener mTabClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			TabView tabView = (TabView) view;
			final int oldSelected = mViewPager.getCurrentItem();
			final int newSelected = tabView.getIndex();
			mViewPager.setCurrentItem(newSelected);
			if (oldSelected == newSelected && mTabReselectedListener != null) {
				mTabReselectedListener.onTabReselected(newSelected);
			}
			if (mHeheListener != null) {
				mHeheListener.onHehe();
			}
		}
	};

	private final IcsLinearLayout mTabLayout;

	private ViewPager mViewPager;
	private ViewPager.OnPageChangeListener mListener;

	private int mMaxTabWidth;
	private int mSelectedTabIndex;
	private float mOffset = 0;

	private OnTabReselectedListener mTabReselectedListener;
	private HeHeListener mHeheListener;
	private ArrayList<String> titles = new ArrayList<String>();
	private float mDensity;

	public void addTitle(String title) {
		titles.add(title);
	}

	public TabPageIndicator(Context context) {
		this(context, null);
	}

	private boolean drawunder;
	private boolean mNeedDelayClick = false;

	public TabPageIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		setHorizontalScrollBarEnabled(false);
		paint.setColor(context.getResources().getColor(
				R.color.beautycenter_tab_background_highlight_color));
		mDensity = context.getResources().getDisplayMetrics().density;
		mTabLayout = new IcsLinearLayout(context,
				R.attr.vpiTabPageIndicatorStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.tabpageindicator);

		// final String aspect = "square";
		drawunder = a.getBoolean(R.styleable.tabpageindicator_drawunder, false);
		a.recycle();

		TypedArray aLocker = context.obtainStyledAttributes(attrs,
				R.styleable.LButtonStyle);
		mNeedDelayClick = aLocker.getBoolean(
				R.styleable.LButtonStyle_widget_delayclick, false);
		aLocker.recycle();

		addView(mTabLayout, new ViewGroup.LayoutParams(WRAP_CONTENT,
				MATCH_PARENT));

	}

	public void setOnTabReselectedListener(OnTabReselectedListener listener) {
		mTabReselectedListener = listener;
	}

	public void setOnHeheListener(HeHeListener listener) {
		this.mHeheListener = listener;
	}

	private RectF mRectF = new RectF();
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private int mSelectedTabIndex2 = 0;

	public void invalidateForce(int position) {
		mSelectedTabIndex2 = position;
		invalidate();
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		// if(mViewPager==null) return;
		int height = getHeight();
		final int count = mViewPager.getAdapter().getCount();
		if (mSelectedTabIndex2 == count - 1) {
			if (drawunder) {
				canvas.drawRect(mTabLayout.getChildAt(mSelectedTabIndex2)
						.getLeft(), height - 6 * mDensity, mTabLayout
						.getChildAt(mSelectedTabIndex2).getRight(), height,
						paint);
			}
			return;
		}
		int left = mTabLayout.getChildAt(mSelectedTabIndex2).getLeft();
		float newLeft = left + mOffset
				* mTabLayout.getChildAt(mSelectedTabIndex2).getWidth();
		int right = mTabLayout.getChildAt(mSelectedTabIndex2).getRight();

		float newRight = right + mOffset
				* mTabLayout.getChildAt(mSelectedTabIndex2 + 1).getWidth();
		mRectF.set(newLeft, height - 6 * mDensity, newRight, height);
		if (drawunder)
			canvas.drawRect(mRectF, paint);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
		setFillViewport(lockedExpanded);

		final int childCount = mTabLayout.getChildCount();
		if (childCount > 1
				&& (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
			if (childCount > 2) {
				mMaxTabWidth = (int) (MeasureSpec.getSize(widthMeasureSpec) * 0.6f);
			} else {
				mMaxTabWidth = MeasureSpec.getSize(widthMeasureSpec) / 2;
			}
		} else {
			mMaxTabWidth = -1;
		}

		final int oldWidth = getMeasuredWidth();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int newWidth = getMeasuredWidth();

		if (lockedExpanded && oldWidth != newWidth) {
			// Recenter the tab display if we're at a new (scrollable) size.
			setCurrentItem(mSelectedTabIndex);
		}
	}

	private void animateToTab(final int position) {
		final View tabView = mTabLayout.getChildAt(position);
		if (mTabSelector != null) {
			removeCallbacks(mTabSelector);
		}
		mTabSelector = new Runnable() {
			@Override
			public void run() {
				final int scrollPos = tabView.getLeft()
						- (getWidth() - tabView.getWidth()) / 2;
				smoothScrollTo(scrollPos, 0);
				mTabSelector = null;
			}
		};
		post(mTabSelector);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (mTabSelector != null) {
			// Re-post the selector we saved
			post(mTabSelector);
		}
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mTabSelector != null) {
			removeCallbacks(mTabSelector);
		}
	}

	public void addTab(int index, CharSequence text, int iconResId) {
		final TabView tabView = new TabView(getContext());
		tabView.mIndex = index;
		// tabView.setFocusable(true);
		tabView.setOnClickListener(mTabClickListener);
		tabView.setText(text);

		if (iconResId != 0) {
			tabView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
		}

		mTabLayout.addView(tabView, new LinearLayout.LayoutParams(0,
				MATCH_PARENT, 1));
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		if (mListener != null) {
			mListener.onPageScrollStateChanged(arg0);
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		Util.Logjb("onPageSelected", "onPageSelected onPageScrolled -->" + arg0);
		mSelectedTabIndex2 = arg0;
		mOffset = arg1;
		invalidate();
		if (mListener != null) {
			mListener.onPageScrolled(arg0, arg1, arg2);
		}
	}

	@Override
	public void onPageSelected(int arg0) {
		Util.Logjb("onPageSelected", "onPageSelected position -->" + arg0);
		setCurrentItem(arg0);
		if (mListener != null) {
			mListener.onPageSelected(arg0);
		}
	}

	@Override
	public void setViewPager(ViewPager view) {
		if (mViewPager == view) {
			return;
		}
		if (mViewPager != null) {
			mViewPager.setOnPageChangeListener(null);
		}
		final PagerAdapter adapter = view.getAdapter();
		if (adapter == null) {
			// throw new IllegalStateException(
			// "ViewPager does not have adapter instance.");
			return;
		}
		mViewPager = view;
		view.setOnPageChangeListener(this);
		notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetChanged() {
		mTabLayout.removeAllViews();
		// PagerAdapter adapter = mViewPager.getAdapter();

		// final int count = adapter.getCount();
		final int count = titles.size();
		for (int i = 0; i < count; i++) {
			CharSequence title = titles.get(i);
			if (title == null) {
				title = EMPTY_TITLE;
			}
			int iconResId = 0;
			addTab(i, title, iconResId);
		}
		if (mSelectedTabIndex > count) {
			mSelectedTabIndex = count - 1;
		}
		setCurrentItem(mSelectedTabIndex);
		requestLayout();
	}

	@Override
	public void setViewPager(ViewPager view, int initialPosition) {
		setViewPager(view);
		setCurrentItem(initialPosition);
	}

	@Override
	public void setCurrentItem(int item) {
		if (mViewPager == null) {
			// throw new IllegalStateException("ViewPager has not been bound.");
			return;
		}
		mSelectedTabIndex = item;
		mViewPager.setCurrentItem(item);

		final int tabCount = mTabLayout.getChildCount();
		for (int i = 0; i < tabCount; i++) {
			final View child = mTabLayout.getChildAt(i);
			final boolean isSelected = (i == item);
			child.setSelected(isSelected);
			if (isSelected) {
				animateToTab(item);
			}
		}
	}

	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mListener = listener;
	}

	private class TabView extends LFrameLayout {
		private int mIndex;
		private TextView mTextView;

		// private LAnimView mClickView;

		// OnClickListener listener;

		public TabView(Context context) {
			super(context, null);
			mTextView = new TextView(context, null,
					R.attr.vpiTabPageIndicatorStyle);
			this.setType(MaterialStyle.TYPE_TAB_ITEM);
			this.setColor(0xccefff3e);
			this.setDelayClick(mNeedDelayClick);
			this.addView(mTextView);

		}

		public void setCompoundDrawablesWithIntrinsicBounds(int iconResId,
				int i, int j, int k) {
			mTextView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0,
					0);
		}

		public void setText(CharSequence text) {
			mTextView.setText(text);
		}

		// @Override
		// public void setOnClickListener(OnClickListener listener) {
		// this.listener = listener;
		// }

		@Override
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);

			// Re-measure if we went beyond our maximum size.
			if (mMaxTabWidth > 0 && getMeasuredWidth() > mMaxTabWidth) {
				super.onMeasure(MeasureSpec.makeMeasureSpec(mMaxTabWidth,
						MeasureSpec.EXACTLY), heightMeasureSpec);
			}
		}

		public int getIndex() {
			return mIndex;
		}
	}
}
