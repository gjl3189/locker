package com.cyou.cma.clockscreen.core;

import android.graphics.Bitmap;
import android.view.View;

import com.cyou.cma.clocker.apf.Keyguard;
import com.cyou.cma.clocker.apf.KeyguardCallback;
import com.cyou.cma.clockscreen.util.Util;

/**
 * 为google play 市场的app编写的 回调锁屏view方法的类
 * 比如电量显示 未接来电显示等
 * 
 * @author jiangbin
 */
public class KeyguardImpl4Play implements Keyguard {
    private KeyguardView mKeyguardView;
    private Keyguard mKeyguard;

    public KeyguardImpl4Play(KeyguardView keyguardView) {
        this.mKeyguardView = keyguardView;
        View view = null;
        try {
            view = mKeyguardView.getKeyguardView();
        } catch (ClassInitException e) {
        }
        if ((view != null) && (view instanceof Keyguard)) {
            try {
                mKeyguard = (Keyguard) mKeyguardView.getKeyguardView();
            } catch (ClassInitException e) {
                Util.printException(e);
            }
        }
    }

    @Override
    public void cleanUp() {
        if (mKeyguard != null) {
            mKeyguard.cleanUp();
        }
    }

    @Override
    public void onCityChanged(String arg0, String arg1) {
        if (mKeyguard != null) {
            mKeyguard.onCityChanged(arg0, arg1);
        }
    }

    @Override
    public void onCityInit(String arg0, String arg1, String arg2, int arg3, String arg4) {
        if (mKeyguard != null) {
            mKeyguard.onCityInit(arg0, arg1, arg2, arg3, arg4);
        }
    }

    @Override
    public void onMissedCallChanged(int arg0, long arg1, String arg2) {
        if (mKeyguard != null) {
            mKeyguard.onMissedCallChanged(arg0, arg1, arg2);
        }
    }

    @Override
    public void onPause() {
        if (mKeyguard != null) {
            mKeyguard.onPause();
        }
    }

    @Override
    public void onRefreshBatteryInfo(boolean arg0, int arg1) {
        if (mKeyguard != null) {
            mKeyguard.onRefreshBatteryInfo(arg0, arg1);
        }
    }

    @Override
    public void onResume() {
        if (mKeyguard != null) {
            mKeyguard.onResume();
        }
    }

    @Override
    public void onTimeChanged() {

        if (mKeyguard != null) {
            mKeyguard.onTimeChanged();
        }

    }

    @Override
    public void onUnreadSMSChanged(int arg0, long arg1, String arg2) {
        if (mKeyguard != null) {
            mKeyguard.onUnreadSMSChanged(arg0, arg1, arg2);
        }
    }

    @Override
    public void onUpdateTemperature(int arg0, String arg1, String arg2) {
        if (mKeyguard != null) {
            mKeyguard.onUpdateTemperature(arg0, arg1, arg2);
        }
    }

    @Override
    public void setKeyguardCallback(KeyguardCallback arg0) {
        if (mKeyguard != null) {
            mKeyguard.setKeyguardCallback(arg0);
        }
    }

    @Override
    public void reset() {
        mKeyguard.reset();
    }

    @Override
    public Bitmap getBlurBitmap() {
        try {
            if (mKeyguard != null) {
                return mKeyguard.getBlurBitmap();
            }
        } catch (Exception e) {
        } catch (Throwable e) {
        }
        return null;
    }

}
