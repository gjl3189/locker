package com.cyou.cma.clockscreen.activity;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.password.widget.ApplockPatternScreen;
import com.cyou.cma.clockscreen.util.Blur;
import com.cyou.cma.clockscreen.util.ImageUtil;

public class PatternAppLockActivity extends BaseAppLockActivity {
    ApplockPatternScreen mPatternUnlockScreen;
    LinearLayout mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock_pattern2);
        mRootView = (LinearLayout) findViewById(R.id.root_view);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
       
        LayoutInflater.from(this).inflate(
                R.layout.activity_app_lock_pattern, mRootView);
        mPatternUnlockScreen = (ApplockPatternScreen) findViewById(R.id.patternUnlockScreen);
        mPatternUnlockScreen.setSecureAccess(this);
        try {

            mRootView.setBackgroundDrawable(new BitmapDrawable(Blur.fastblur(this, ImageUtil
                    .getSystemWallpaperCore(this,
                            dm.widthPixels, dm.heightPixels).getBitmap(), 8)));
        } catch (Exception e) {
            mRootView.setBackgroundColor(Color.BLACK);
        }
// mPatternUnlockScreen.setBackgroundDrawable(new BitmapDrawable(ImageUtil.getSystemWallpaperCore(this,
// dm.widthPixels, dm.heightPixels).getBitmap()));
    }
}
