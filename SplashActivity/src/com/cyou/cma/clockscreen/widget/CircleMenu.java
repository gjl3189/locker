/*
 * Copyright (C) 2012 Capricorn
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

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.LockApplication;
import com.cyou.cma.clockscreen.quicklaunch.DatabaseUtil;
import com.cyou.cma.clockscreen.quicklaunch.LaunchSet;
import com.cyou.cma.clockscreen.quicklaunch.QuickApplication;
import com.cyou.cma.clockscreen.quicklaunch.QuickContact;
import com.cyou.cma.clockscreen.quicklaunch.QuickFolder;
import com.cyou.cma.clockscreen.quicklaunch.RotateAndTranslateAnimation;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.LauchSetType;
import com.cyou.cma.clockscreen.util.Util;

public class CircleMenu extends ViewGroup implements OnClickListener {
	private final String TAG = "CircleMenu";
	public static int mChildWidth;
	private int mChildHeight;
	private int mMoreHeight;

	public static final float DEFAULT_FROM_DEGREES = -90.0f;

	public static final float DEFAULT_TO_DEGREES = 270.0f;

	private float mFromDegrees = DEFAULT_FROM_DEGREES;

	private float mToDegrees = DEFAULT_TO_DEGREES;

	private static final int ANIM_DURATION = 300;

	private int mRadius;

	private boolean mExpanded = false;
	int mDesiredWidth = 720;

	public CircleMenu(Context context) {
		super(context);
	}

	public CircleMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDesiredWidth = Util.getScreenWidth(context);
		Bitmap bitmap = ImageUtil.readBitmapWithDensity(context,
				R.drawable.icon_quicklaunch_folder);
		mChildWidth = bitmap.getWidth();

	}

	private Rect computeChildFrame(final int centerX, final int centerY,
			final int radius, final float degrees) {

		final double childCenterX = centerX + radius
				* Math.cos(Math.toRadians(degrees));
		final double childCenterY = centerY + radius
				* Math.sin(Math.toRadians(degrees));

		return new Rect((int) (childCenterX - mChildWidth / 2),
				(int) (childCenterY - mChildHeight / 2),
				(int) (childCenterX + mChildWidth / 2),
				(int) (childCenterY + mChildHeight / 2));
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
			height = Math.min(mDesiredWidth, heightSize);
		} else {
			height = mDesiredWidth;
		}
		setMeasuredDimension(width, height);

		mChildHeight = mChildWidth + mMoreHeight;
		// mRadius = (width / 2 - mChildWidth);
		mRadius = Math.min(width / 2 - mChildWidth, mChildWidth * 3);
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			// getChildAt(i)
			// .measure(
			// MeasureSpec.makeMeasureSpec(mChildSize,
			// MeasureSpec.EXACTLY),
			// MeasureSpec.makeMeasureSpec(mChildSize,
			// MeasureSpec.EXACTLY));
			measureChild(getChildAt(i), MeasureSpec.makeMeasureSpec(
					mChildWidth, MeasureSpec.AT_MOST),
					MeasureSpec.makeMeasureSpec((i == count - 1) ? mChildHeight
							- mMoreHeight : mChildHeight, MeasureSpec.AT_MOST));
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		layoutChildView();
	}

	private void layoutChildView() {
		final int centerX = getWidth() / 2;
		final int centerY = getHeight() / 2;
		final int radius = mExpanded ? mRadius : 0;

		final int childCount = getChildCount() - 1;
		final float perDegrees = (mToDegrees - mFromDegrees) / (childCount);

		float degrees = mFromDegrees;
		for (int i = 0; i < childCount; i++) {
			Rect frame = computeChildFrame(centerX, centerY, radius, degrees);
			degrees += perDegrees;
			getChildAt(i).layout(frame.left, frame.top, frame.right,
					frame.bottom);
		}
		if (childCount >= 0 && getChildAt(childCount) != null) {
			getChildAt(childCount).layout(centerX - mChildWidth / 2,
					centerY - mChildWidth / 2, centerX + mChildWidth / 2,
					centerY + mChildWidth / 2);
		}
	}

	/**
	 * refers to {@link LayoutAnimationController#getDelayForView(View view)}
	 */
	private long computeStartOffset(final int childCount,
			final boolean expanded, final int index, final float delayPercent,
			final long duration, Interpolator interpolator) {
		final float delay = delayPercent * duration;
		final long viewDelay = (long) (getTransformedIndex(expanded,
				childCount, index) * delay);
		final float totalDelay = delay * childCount;

		float normalizedDelay = viewDelay / totalDelay;
		normalizedDelay = interpolator.getInterpolation(normalizedDelay);
		return (long) (normalizedDelay * totalDelay);
	}

	private static int getTransformedIndex(final boolean expanded,
			final int count, final int index) {
		if (expanded) {
			return count - 1 - index;
		}
		return index;
	}

	private static Animation createExpandAnimation(float fromXDelta,
			float toXDelta, float fromYDelta, float toYDelta, long startOffset,
			long duration, Interpolator interpolator) {
		Animation animation = new RotateAndTranslateAnimation(0, toXDelta, 0,
				toYDelta, 0, 720);
		animation.setStartOffset(startOffset);
		animation.setDuration(duration);
		animation.setInterpolator(interpolator);
		animation.setFillAfter(true);
		return animation;
	}

	private Animation createShrinkAnimation(float fromXDelta, float toXDelta,
			float fromYDelta, float toYDelta, long startOffset, long duration,
			Interpolator interpolator) {
		AnimationSet animationSet = new AnimationSet(false);
		animationSet.setFillAfter(true);

		final long preDuration = duration / 2;
		// Animation rotateAnimation = new RotateAnimation(0, 360,
		// Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
		// 0.5f);
		// rotateAnimation.setStartOffset(startOffset);
		// rotateAnimation.setDuration(preDuration);
		// rotateAnimation.setInterpolator(new LinearInterpolator());
		// rotateAnimation.setFillAfter(true);
		// animationSet.addAnimation(rotateAnimation);

		Animation translateAnimation = new RotateAndTranslateAnimation(0,
				toXDelta, 0, toYDelta, 360, 720);
		translateAnimation.setStartOffset(startOffset + preDuration);
		translateAnimation.setDuration(duration);// - preDuration
		translateAnimation.setInterpolator(interpolator);
		translateAnimation.setFillAfter(true);

		animationSet.addAnimation(translateAnimation);

		return animationSet;
	}

	private void bindChildAnimation(View child, int index) {
		final int centerX = getWidth() / 2;
		final int centerY = getHeight() / 2;
		final int radius = mExpanded ? 0 : mRadius;

		final int childCount = getChildCount() - 1;
		final float perDegrees = (mToDegrees - mFromDegrees) / (childCount);
		Rect frame = computeChildFrame(centerX, centerY, radius, mFromDegrees
				+ index * perDegrees);

		final int toXDelta = frame.left - child.getLeft();
		final int toYDelta = frame.top - child.getTop();

		Interpolator interpolator = mExpanded ? new AccelerateInterpolator()
				: new OvershootInterpolator(1.5f);
		final long startOffset = computeStartOffset(childCount, mExpanded,
				index, 0.1f, ANIM_DURATION, interpolator);

		Animation animation = mExpanded ? createShrinkAnimation(0, toXDelta, 0,
				toYDelta, startOffset, ANIM_DURATION, interpolator)
				: createExpandAnimation(0, toXDelta, 0, toYDelta, startOffset,
						ANIM_DURATION, interpolator);

		final boolean isLast = getTransformedIndex(mExpanded, childCount, index) == childCount - 1;
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (isLast) {
					postDelayed(new Runnable() {

						@Override
						public void run() {
							onAllAnimationsEnd();
						}
					}, 0);
					if (mCircleMenuListener != null) {
						mCircleMenuListener.onAnimEnd(mExpanded);
					}
				}
			}
		});

		child.setAnimation(animation);
	}

	public boolean isExpanded() {
		return mExpanded;
	}

	public void setDegreesRange(float fromDegrees, float toDegrees) {
		if (mFromDegrees == fromDegrees && mToDegrees == toDegrees) {
			return;
		}
		mFromDegrees = fromDegrees;
		mToDegrees = toDegrees;
		requestLayout();
	}

	public int getChildSize() {
		return mChildWidth;
	}

	/**
	 * switch between expansion and shrinkage
	 * 
	 * @param showAnimation
	 */
	public void switchState(boolean showAnimation) {
		if (showAnimation) {
			final int childCount = getChildCount() - 1;
			for (int i = 0; i < childCount; i++) {
				bindChildAnimation(getChildAt(i), i);
			}
		}
		mExpanded = !mExpanded;
		if (!showAnimation || getChildCount() == 1) {
			requestLayout();
			if (mCircleMenuListener != null) {
				mCircleMenuListener.onAnimEnd(mExpanded);
			}
		}
		invalidate();
	}

	private void onAllAnimationsEnd() {
		final int childCount = getChildCount() - 1;
		for (int i = 0; i < childCount; i++) {
			getChildAt(i).clearAnimation();
		}
		requestLayout();
	}

	private List<LaunchSet> mList = new ArrayList<LaunchSet>();

	public void setAdapterList(List<LaunchSet> list) {
		mList = list;
		int i = 0;
		if (mList != null && mList.size() > 0) {
			PackageManager pm = getContext().getPackageManager();
			for (; i < mList.size(); i++) {
				LaunchAppLayout item = new LaunchAppLayout(getContext());
				if (mMoreHeight == 0) {
					mMoreHeight = item.getTextViewHeight();
				}
				item.setOnClickListener(mItemLickClickListener);
				item.setTag(i);
				switch (mList.get(i).getType()) {
				case LauchSetType.FOLDER_TYPE:
					QuickFolder quickFolder = DatabaseUtil
							.getQuickFolderOnLaunchSet(mList.get(i).getId());
					item.initData(R.drawable.icon_quicklaunch_folder,
							quickFolder.getFolderName());
					break;
				case LauchSetType.CONTACT_TYPE:// TODO
					QuickContact quickContact = DatabaseUtil
							.getQuickContactOnLaunchSet(mList.get(i).getId());
					if (TextUtils.isEmpty(quickContact.getPhotoUri())) {
						item.initData(R.drawable.icon_quicklaunch_contact,
								quickContact.getContactName());
					} else {
						Bitmap bitmap = null;
						try {
							bitmap = MediaStore.Images.Media.getBitmap(
									getContext().getContentResolver(),
									Uri.parse(quickContact.getPhotoUri()));
						} catch (Exception e) {
						}
						if (bitmap == null) {
							item.initData(R.drawable.icon_quicklaunch_contact,
									quickContact.getContactName());
						} else {
							item.initData(
									LockApplication.getInstance()
											.getQuickLaunchWithMaskBitmap(
													bitmap, false),
									quickContact.getContactName());
						}
					}
					break;
				case LauchSetType.APP_TYPE:
					QuickApplication quickApplication = DatabaseUtil
							.getQuickApplicationOnLaunchSet(mList.get(i)
									.getId());
					if (!TextUtils.isEmpty(quickApplication.getPackageName())) {
						try {
							if (!TextUtils.isEmpty(quickApplication
									.getMainActivityClassName())) {
								ActivityInfo activityInfo = pm
										.getActivityInfo(
												new ComponentName(
														quickApplication
																.getPackageName(),
														quickApplication
																.getMainActivityClassName()),
												0);
								item.initData(
										LockApplication.getInstance()
												.getQuickLaunchWithMaskBitmap(
														activityInfo
																.loadIcon(pm),
														false), activityInfo
												.loadLabel(pm).toString());
							} else {
								ApplicationInfo info = pm.getApplicationInfo(
										quickApplication.getPackageName(), 0);
								item.initData(
										LockApplication.getInstance()
												.getQuickLaunchWithMaskBitmap(
														info.loadIcon(pm),
														false),
										info.loadLabel(pm).toString());
							}
						} catch (NameNotFoundException e) {
							Util.printException(e);
							continue;
						}
						// item.setImageBitmap(getAppIcon(pm, mList.get(i)
						// .getPackageName()));
					}
					break;

				default:
					continue;
				}
				this.addView(item, new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
			}
		}
		LaunchAppLayout itemClocker = new LaunchAppLayout(getContext());
		itemClocker.setOnClickListener(mItemLickClickListener);
		itemClocker.setTag(i);
		itemClocker.initData(R.drawable.icon_quicklaunch_logo, "");
		this.addView(itemClocker, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
	}

	OnClickListener mItemLickClickListener = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			if (v.getTag() == null)
				return;
			if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
				Animation animation = disapperItemAnimation(v, true);
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						requestLayout();
						clickItem(v);
					}
				});
			} else {
				clickItem(v);
			}
			invalidate();
		}
	};

	private void clickItem(View v) {
		int index = (Integer) v.getTag();
		if (mCircleMenuListener != null) {
			if (index == mList.size()) {
				mCircleMenuListener.onLockerClick();
			} else {
				float perDegrees = (mToDegrees - mFromDegrees)
						/ (getChildCount() - 1);
				float degrees = perDegrees * index + mFromDegrees;
				Rect rect = computeChildFrame(getWidth() / 2, getHeight() / 2,
						mExpanded ? mRadius : 0, degrees);
				mCircleMenuListener.onItemClick(rect.centerX(), rect.centerY(),
						mList.get(index));
			}
		}
	}

	@Override
	public void onClick(View v) {

	}

	private Animation disapperItemAnimation(View child, boolean isClicked) {
		Animation animation = createItemDisapperAnimation(isClicked);
		child.setAnimation(animation);
		return animation;
	}

	// private void itemDidDisappear() {
	// final int itemCount = getChildCount();
	// for (int i = 0; i < itemCount; i++) {
	// View item = getChildAt(i);
	// item.clearAnimation();
	// }
	// switchState(false);
	// }

	private static Animation createItemDisapperAnimation(boolean isClicked) {
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(new ScaleAnimation(1.0f, isClicked ? 1.3f
				: 0.0f, 1.0f, isClicked ? 1.3f : 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f));
		animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));
		animationSet.setDuration(ANIM_DURATION);
		animationSet.setInterpolator(new DecelerateInterpolator());
		// animationSet.setFillAfter(true);
		return animationSet;
	}

	public void setCircleMenuListener(CircleMenuListener mListener) {
		this.mCircleMenuListener = mListener;
	}

	CircleMenuListener mCircleMenuListener;

	public interface CircleMenuListener {
		public void onAnimEnd(boolean isOpen);

		public void onLockerClick();

		public void onItemClick(int x, int y, LaunchSet obj);
	}

}
