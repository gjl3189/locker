package com.cyou.cma.cengine.wave.util;

import android.graphics.Bitmap;

public class CyWaveLib {

     static {
         System.loadLibrary("CyWave");
     }

    /**
     * @param width the current view width
     * @param height the current view height
     */
     public static native void init(Bitmap bitmap, int factor, int radius, int width, int height);
     
     public static native void init2(int wBmp, int hBmp, int factor, int radius, int width, int height);

//     public static native int onSurfaceCreated();
//     public static native int onSurfaceChanged(int w, int h);
     public static native int onDrawFrame();
//     public static native int setBmp(Bitmap bitmap);
     
     public static native void initiateRippleAtLocation(float x, float y);
     

     public static native void onDestroy();
     
     public static native int getVersionCode();
}
