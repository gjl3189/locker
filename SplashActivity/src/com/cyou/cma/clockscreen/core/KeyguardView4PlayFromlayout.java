package com.cyou.cma.clockscreen.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;

import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.LayoutInflaterFactory;
import com.cyou.cma.clockscreen.defaulttheme.CLockScreen;
import com.cyou.cma.clockscreen.util.SettingsHelper;
import com.cyou.cma.clockscreen.util.Util;

import dalvik.system.PathClassLoader;

/**
 * 专门为google play市场 锁屏做的获取锁屏View的类
 * 
 * @author jiangbin
 */
public class KeyguardView4PlayFromlayout implements KeyguardView {

    private View mKeyguardView;

    private Bitmap mBitmap;

    @Override
    public void initKeyguardView(Context context) throws ClassInitException {
        if (mKeyguardView == null) {
            try {
                // TODO jiangbin 当前锁屏主题包名需要动态获取
                String currentLockerPackage = SettingsHelper.getCurrentTheme(context);
                if (currentLockerPackage.equals(Constants.SKY_LOCKER_DEFAULT_THEME)) {
                    mKeyguardView = new CLockScreen(context);
                } else {
                    Context themeContext = context.createPackageContext(
                            currentLockerPackage, Context.CONTEXT_IGNORE_SECURITY
                                    | Context.CONTEXT_INCLUDE_CODE);
                    int resId = themeContext.getResources().getIdentifier("lockscreen",
                            "layout", currentLockerPackage);

                    PathClassLoader themePathClassLoader = new PathClassLoader(
                            themeContext.getApplicationInfo().publicSourceDir,
                            themeContext.getApplicationInfo().nativeLibraryDir,
                            getClass().getClassLoader());

                    LayoutInflater layoutInflater = LayoutInflater.from(context);
                    LayoutInflater themeLayoutInflater = layoutInflater.cloneInContext(themeContext);
                    themeLayoutInflater.setFactory(new LayoutInflaterFactory(themePathClassLoader));
                    mKeyguardView = themeLayoutInflater.inflate(resId, null);
                }
            } catch (Exception e) {
                Util.printException(e);
                throw new ClassInitException();
            }
        }
    }

    @Override
    public Class<? extends View> getLockClass() throws ClassInitException {
        throw new ClassInitException();
    }

    @Override
    public View getKeyguardView() throws ClassInitException {
        if (mKeyguardView == null) {
            throw new ClassInitException();
        }
        return mKeyguardView;
    }

//    @Override
//    public void initBlurBitmap(Context context1) {
//        if (mKeyguardView == null) {
//            mBitmap = null;
//        }
//        String currentLockerPackage = SettingsHelper.getCurrentTheme(mKeyguardView.getContext());
//        if (currentLockerPackage.equals("com.cynad.cma.theme.technology")) {
//            mBitmap = new WallpaperBitmapBase().getBitmap(context1,
//                    WallpaperBitmapFactory.getBitmap(context1, R.drawable.technology),
//                    mKeyguardView.getHeight());
//        } else {
//            Resources res = mKeyguardView.getContext().getResources();
//            if (currentLockerPackage.equals(Constants.SKY_LOCKER_DEFAULT_THEME)) {
//                mBitmap = null;
//            }
//            else {
//                Drawable drawable = res.getDrawable(res.getIdentifier("thumbforblur", "drawable",
//                        currentLockerPackage));
//                if (drawable instanceof BitmapDrawable) {
//                    Util.Logjb("KeyguardView4PlayFromlayout", "drawable instanceof bitmapDrawable");
//                    mBitmap = ((BitmapDrawable) drawable).getBitmap();
//                } else {
//                    mBitmap = null;
//                }
//            }
//
//        }
//    }
//
//    @Override
//    public Bitmap getBlurBitmap(Context context1) {
//        if (mBitmap == null) {
//            initBlurBitmap(context1);
//        }
//        return mBitmap;
//    }
}
