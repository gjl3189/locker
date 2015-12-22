package com.cyou.cma.clockscreen.widget;

import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RelativeLayout;

public class ViewPagerOnPageChangeListener implements OnPageChangeListener {

    RelativeLayout mViewPagerContainer;

    public ViewPagerOnPageChangeListener(RelativeLayout viewPagerContainer) {
        mViewPagerContainer = viewPagerContainer;
    }

    @Override
    public void onPageSelected(int position) {
        if (mViewPagerContainer != null) {
            mViewPagerContainer.invalidate();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
            int positionOffsetPixels) {
        if (mViewPagerContainer != null) {
            mViewPagerContainer.invalidate();
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        if (mViewPagerContainer != null) {
            mViewPagerContainer.invalidate();
        }
    }
}
