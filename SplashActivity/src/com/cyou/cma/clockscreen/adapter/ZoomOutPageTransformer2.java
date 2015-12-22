package com.cyou.cma.clockscreen.adapter;

import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

import com.cynad.cma.locker.R;

@SuppressLint("NewApi")
public class ZoomOutPageTransformer2 implements PageTransformer {
    private static final float MIN_SCALE = 0.936f;

    // private static final float MIN_ALPHA = 0.5f;

    public ZoomOutPageTransformer2() {
    }

    @Override
    public void transformPage(View view, float position) {
        view.findViewById(R.id.view_foreground).setVisibility(View.VISIBLE);

        // [-1,1]
        // Modify the default slide transition to shrink the page as well
        float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));

        // Scale the page down (between MIN_SCALE and 1)
        // if(!(position<-1))
        view.setScaleX(scaleFactor);
        view.setScaleY(scaleFactor);
        if (Math.abs(position) < 0.1) {
            view.findViewById(R.id.view_foreground).setVisibility(View.GONE);
        }
        if (position == 0) {
            view.setScaleY(1.0f);
        }
        if (Math.abs(position) >= 0.9) {
            view.setScaleX(1.0f);
        }

    }
}
