package com.cyou.cma.clockscreen.activity;

import com.adjust.sdk.Adjust;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class BaseFragmentActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        final int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        if (getRequestedOrientation() != orientation) {
            setRequestedOrientation(orientation);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(arg0);
    }

    private boolean mCalled = false;

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (!mCalled) {
            super.setRequestedOrientation(requestedOrientation);
            mCalled = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Adjust.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Adjust.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
// super.onSaveInstanceState(outState);
    }
}
