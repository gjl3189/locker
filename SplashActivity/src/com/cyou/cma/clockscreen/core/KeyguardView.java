package com.cyou.cma.clockscreen.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

/**
 * 获取锁屏界面的接口
 * 
 * @author jiangbin
 */
public interface KeyguardView {
    /**
     * 获取锁屏界面
     * 
     * @return
     */
    public View getKeyguardView() throws ClassInitException;

// public void reCreateKeyguardView(Context context) throws ClassInitException;

    public Class<? extends View> getLockClass() throws ClassInitException;

    public void initKeyguardView(Context context) throws ClassInitException;

//    public Bitmap getBlurBitmap(Context context);
//
//    public void initBlurBitmap(Context context);
}
