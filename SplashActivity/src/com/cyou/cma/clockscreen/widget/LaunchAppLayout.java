package com.cyou.cma.clockscreen.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.Util;

public class LaunchAppLayout extends LinearLayout {

	private ImageView mIconImageView;
	private TextView mNameTextView;

	private int mDesiredWidth;

	public LaunchAppLayout(Context context) {
		this(context, null);
	}

	public LaunchAppLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		mDesiredWidth = Util.getScreenWidth(context) * 2 / 3 / 4;
	}

	private void initView() {
		View.inflate(getContext(), R.layout.layout_quicklaunch_item, this);
		mIconImageView = (ImageView) findViewById(R.id.quicklaunch_icon);
		mNameTextView = (TextView) findViewById(R.id.quicklaunch_name);
	}

	public void initData(int iconRes, String name) {
		if (mIconImageView != null) {
			// mIconImageView.setImageResource(iconRes);
			mIconImageView.setImageBitmap(ImageUtil.readBitmapWithDensity(
					getContext(), iconRes));
		}
		if (mNameTextView != null) {
			mNameTextView.setText(name);
		}
	}

	public void initData(Bitmap icon, String name) {
		if (mIconImageView != null) {
			if (icon != null) {
				mIconImageView.setImageBitmap(icon);
			} else {
				mIconImageView
						.setImageResource(R.drawable.icon_quicklaunch_unknown);
			}
		}
		if (mNameTextView != null) {
			mNameTextView.setText(name);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		// int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		// int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		// int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		// int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		//
		// int width;
		// int height;
		//
		// if (widthMode == MeasureSpec.EXACTLY) {
		// width = widthSize;
		// } else if (widthMode == MeasureSpec.AT_MOST) {
		// width = Math.min(mDesiredWidth, widthSize);
		// } else {
		// width = mDesiredWidth;
		// }
		// if (mIconImageView != null) {
		// mIconImageView.getLayoutParams().width = width - 1;
		// mIconImageView.getLayoutParams().height = width - 1;
		// }
		// if (heightMode == MeasureSpec.EXACTLY) {
		// height = heightSize;
		// } else if (heightMode == MeasureSpec.AT_MOST) {
		// height = Math.min(mDesiredHeight, heightSize);
		// } else {
		// height = mDesiredHeight;
		// }
		// setMeasuredDimension(width, width);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public int getTextViewHeight() {
		int desity = getResources().getDisplayMetrics().densityDpi;
		float customScale = 1f;
		if (desity <= 320) {
			if (desity == 320) {
				customScale = 1.5f;
			} else {
				customScale = 2f;
			}
		}
		if (mNameTextView != null) {
			return (int) (mNameTextView.getLineHeight()
					+ mNameTextView.getPaddingTop() * customScale + mNameTextView
					.getPaddingBottom() * customScale);
		}
		return 0;
	}
}
