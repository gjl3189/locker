package com.cyou.cma.clockscreen.widget;

import static android.view.View.MeasureSpec.EXACTLY;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

/**
 * A special layout when measured in AT_MOST will take up a given percentage of
 * the available space.
 */
public class WeightedLinearLayout extends LinearLayout {
    private float mMajorWeight;
    private float mMinorWeight;

    public WeightedLinearLayout(Context context) {
        super(context);
    }

    public WeightedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mMajorWeight = 0.55f;
        mMinorWeight = 0.9f;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        final int screenWidth = metrics.widthPixels;
        final boolean isPortrait = screenWidth < metrics.heightPixels;

        final float widthWeight = isPortrait ? mMinorWeight : mMajorWeight;
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (screenWidth * widthWeight), EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
