package com.cyou.cma.clockscreen.util;

import java.util.concurrent.Callable;

import android.content.Context;
import android.graphics.Bitmap;

public class BlurCallable implements Callable<Bitmap> {
    private Bitmap mOriginal;
    private Context mContext;

    public BlurCallable(Context context, Bitmap original) {
        this.mOriginal = original;
        this.mContext = context;
    }

    @Override
    public Bitmap call() throws Exception {
        return Blur.fastblur(mContext
                , mOriginal, 8);
    }
}
