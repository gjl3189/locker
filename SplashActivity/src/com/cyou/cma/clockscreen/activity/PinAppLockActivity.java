package com.cyou.cma.clockscreen.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.password.widget.ApplockPinPasswordView;
import com.cyou.cma.clockscreen.util.Blur;
import com.cyou.cma.clockscreen.util.ImageUtil;

public class PinAppLockActivity extends BaseAppLockActivity {
    private ApplockPinPasswordView passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passwordView = (ApplockPinPasswordView) getLayoutInflater().inflate(
                R.layout.password_pin_applock, null);
        setContentView(passwordView);
        passwordView.setSecureAccess(this);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        try {

            passwordView.setBackgroundDrawable(new BitmapDrawable(Blur.fastblur(this, ImageUtil
                    .getSystemWallpaperCore(this,
                            dm.widthPixels, dm.heightPixels).getBitmap(), 8)));
        } catch (Exception e) {
            passwordView.setBackgroundColor(Color.BLACK);
        }
        passwordView.onHide();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        passwordView.onHide();
    }
}
