package com.cyou.cma.clockscreen.service;

import android.app.KeyguardManager;
import android.content.Context;

import com.cyou.cma.clockscreen.util.Util;

public class KeyguardHelper {

    private Context mContext;
    private KeyguardManager mKeyguardManager;
    @SuppressWarnings("deprecation")
    private KeyguardManager.KeyguardLock mKeyguardLock;

    public KeyguardHelper(Context context) {
        mContext = context;
    }

    @SuppressWarnings("deprecation")
    public void disableSystemKeyguard() {
        try {
            ensureKeyguardLock();
            if (mKeyguardLock != null) {
                mKeyguardLock.disableKeyguard();
            }
        } catch (Exception e) {
            Util.printException(e);
        }
    }

    @SuppressWarnings("deprecation")
    public void enableSystemKeyguard() {
        try {
            if (mKeyguardLock != null) {
                mKeyguardLock.reenableKeyguard();
            }
        } catch (Exception e) {
            Util.printException(e);
        }
    }

    @SuppressWarnings("deprecation")
    private void ensureKeyguardLock() {
        ensureKeyguardManager();
        if (mKeyguardLock == null) {
            mKeyguardLock = mKeyguardManager.newKeyguardLock(mContext.getPackageName());
        }
    }

    private void ensureKeyguardManager() {
        if (mKeyguardManager == null) {
            mKeyguardManager = (KeyguardManager) mContext
                    .getSystemService(Context.KEYGUARD_SERVICE);
        }
    }
}
