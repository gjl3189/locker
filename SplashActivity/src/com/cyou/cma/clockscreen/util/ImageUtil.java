package com.cyou.cma.clockscreen.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;

import com.cyou.cma.clockscreen.bean.CoreBitmapObj;
import com.cyou.cma.clockscreen.sqlite.ProviderHelper;

public class ImageUtil {
	private final static String TAG = "ImageUtil";
	static String a;

	// public static Bitmap getWallpaper(Context context, int defaultId,
	// String packageName, int viewWidth, int viewHeight) {
	// return getWallpaper(context, defaultId, true, packageName, viewWidth,
	// viewHeight);
	// }
	public static CoreBitmapObj getWallpaper(Context context, int defaultId,
			String packageName, int viewWidth, int viewHeight, boolean formTop) {
		return getWallpaper(context, defaultId, packageName, viewWidth,
				viewHeight, formTop, true);
	}

	public static CoreBitmapObj getWallpaper(Context context, int defaultId,
			String packageName, int viewWidth, int viewHeight, boolean formTop,
			boolean userStream) {
		int wallpaperType = ProviderHelper.getWallpaperType(context,
				packageName);
		switch (wallpaperType) {
		case Util.KEY_WALLPAPER_TYPE_RESTORE:
			try {
				return getCoreBitmapWithSize(
						context,
						userStream ? ImageUtil.readBitmapByStream(context,
								defaultId) : ImageUtil.readBitmapByDecode(
								context, defaultId), viewWidth, viewHeight,
						formTop, true);
			} catch (OutOfMemoryError error) {
				error.printStackTrace();
				Util.restartLocker(context);
			}
			break;
		case Util.KEY_WALLPAPER_TYPE_SYSTEM:
			return getSystemWallpaperCore(context, viewWidth, viewHeight);
		case Util.KEY_WALLPAPER_TYPE_GALLERY:
			String path = ProviderHelper.getWallpaperPath(context, packageName);
			File file = new File(path);
			if (file.exists()) {
				try {
					CoreBitmapObj obj = new CoreBitmapObj();
					obj.init(readBitmapWithDensityByPath(context, path), 1, 0,
							0);
					return obj;
				} catch (Exception e) {
					Util.printException(e);
				} catch (OutOfMemoryError e) {
					Util.printException(e);
					Util.restartLocker(context);
				}
			} else {
				ProviderHelper.updateWallpaper(context, packageName,
						Util.KEY_WALLPAPER_TYPE_RESTORE, "");
				// int viewHeight1 = Util.getScreenHeight(context);
				// // - getStatusBarHeight(context);
				// int viewWidth1 = Util.getScreenWidth(context);
				try {
					return getCoreBitmapWithSize(context,
							ImageUtil.readBitmapByStream(context, defaultId),
							viewWidth, viewHeight, formTop, true);
				} catch (OutOfMemoryError error) {
					error.printStackTrace();
					Util.restartLocker(context);
				}
			}
			break;

		default:
			break;
		}
		return null;
	}

	/**
	 * 按照系统Density来获取本地bitmap， 会根据不同密度返回不同大小的bitmap
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitmapWithDensity(Context context, int resId) {
		try {
			Options op = new Options();
			if (getDensityDpi(context) < 240) {
				op.inDensity = 400;
			}
			return BitmapFactory.decodeResource(context.getResources(), resId,
					op);
		} catch (Exception e) {
			return null;
		} catch (Error e) {
			Util.restartLocker(context);
			return null;
		}
	}

	public static int getDensityDpi(Context context) {
		return context.getResources().getDisplayMetrics().densityDpi;
	}

	// public static Bitmap readBitmapWithDensityCustom(Context context, int
	// resId) {
	// Options op = new Options();
	// op.inDensity = 320;
	// op.inTargetDensity = 100;
	// return BitmapFactory.decodeResource(context.getResources(), resId, op);
	// }

	/**
	 * 按照系统Density来获取文件中系统中的bitmap， 会根据不同密度返回不同大小的bitmap
	 * 
	 * @param context
	 * @param path
	 * @return
	 */
	public static Bitmap readBitmapWithDensityByPath(Context context,
			String path) {
		return BitmapFactory.decodeFile(path);
	}

	/**
	 * 以最省内存的方式读取本地资源
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitmapByStream(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = null;
		try {
			is = context.getResources().openRawResource(resId);
			return BitmapFactory.decodeStream(is, null, opt);
		} catch (Exception e) {

		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				Util.printException(e);
			}
		}
		return null;
	}

	public static Bitmap readBitmapByDecode(Context ct, int id) {
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeResource(ct.getResources(), id);
		} catch (OutOfMemoryError e) {
			bmp = null;
			Util.printException(e);
		} catch (Exception e) {
			bmp = null;
			Util.printException(e);
		}
		return bmp;

	}

	public static CoreBitmapObj getSystemWallpaperCore(Context context,
			int viewWidth, int viewHeight) {
		try {
			WallpaperManager wallpaperManager = WallpaperManager
					.getInstance(context);
			Bitmap tempBitmap = ((BitmapDrawable) wallpaperManager
					.getDrawable()).getBitmap();
			return getCoreBitmapWithSize(context, tempBitmap, viewWidth,
					viewHeight, true, false);
		} catch (Exception e) {
			return null;
		}
	}

	public static CoreBitmapObj getCoreBitmapWithSize(Context mContext,
			Bitmap tempBitmap, int viewWidth, int viewHeight, boolean fromTop,
			boolean needRecycleTempBmp) {
		CoreBitmapObj obj = new CoreBitmapObj();
		if (tempBitmap == null)
			return null;
		if (viewWidth == 0 || viewHeight == 0 || tempBitmap.getWidth() == 0
				|| tempBitmap.getHeight() == 0) {
			obj.setBitmap(tempBitmap);
			return obj;
		}

		Bitmap bitmap = null;
		try {
			int x, y, width, height;
			float scale = 1;
			if (tempBitmap.getWidth() / (float) tempBitmap.getHeight() > viewWidth
					/ (float) viewHeight) {// 按照高度裁剪,取全高，截断宽

				y = 0;
				height = tempBitmap.getHeight();
				width = height * viewWidth / viewHeight;
				x = (tempBitmap.getWidth() - width) / 2;
				scale = (float) viewHeight / tempBitmap.getHeight();
			} else {// 按照宽度裁剪，取全宽，截断高
				x = 0;
				width = tempBitmap.getWidth();
				height = width * viewHeight / viewWidth;
				if (fromTop) {
					y = 0;
				} else {
					y = tempBitmap.getHeight() - height;
				}
				scale = viewWidth / tempBitmap.getWidth();
			}
			// Util.Log(TAG, "x-->" + x + " y-->" + y + " width-->" + width +
			// " height-->" + height);
			if (x < 0)
				x = 0;
			if (y < 0)
				y = 0;
			if ((x + width) > tempBitmap.getWidth()
					|| (y + height) > tempBitmap.getHeight()) {
				obj.init(tempBitmap, 1, 0, 0);
				return obj;
			}
			try {

				if (isValidBitmap(tempBitmap)) {
					if (x == 0 && y == 0 && width == tempBitmap.getWidth()
							&& height == tempBitmap.getHeight()) {
						bitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, true);
						obj.init(bitmap, 1, 0, 0);
						return obj;
					} else {
						bitmap = Bitmap.createBitmap(tempBitmap, x, y, width,
								height);
						obj.init(bitmap, scale, x, y);
					}
				} else {
					obj.init(null, 1, 0, 0);
				}
				if (needRecycleTempBmp && (x != 0 || y != 0)) {
					recycleBitmap(tempBitmap);
				}
				return obj;
			} catch (Exception e) {
				Util.printException(e);
				obj.init(tempBitmap, 1, 0, 0);
				return obj;
			}
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
			bitmap = null;
			Util.restartLocker(mContext);
		}
		// obj.init(tempBitmap, 1, 0, 0);
		return obj;
	}

	// public static Bitmap getCoreBitmap(Context mContext, int imageRes) {
	// return getCoreBitmap(mContext, imageRes, true);
	// }

	// public static Bitmap getCoreBitmap(Context mContext, int imageRes,
	// boolean fromTop, int viewWidth, int viewHeight) {
	// // int viewHeight = Util.getScreenHeight(mContext);
	// // // - getStatusBarHeight(mContext);
	// // int viewWidth = Util.getScreenWidth(mContext);
	// return getCoreBitmapWithSize(mContext, imageRes, viewWidth, viewHeight,
	// fromTop);
	// }

	/**
	 * 获取状态栏高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		int height = Util.getPreferenceInt(context,
				Util.SAVE_KEY_STATUSBAR_HEIGHT, 0);
		if (height > 0)
			return height;
		Class<?> c = null;
		Object obj = null;
		java.lang.reflect.Field field = null;
		int x = 0;
		int statusBarHeight = 50;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			Util.printException(e);
		}
		Util.putPreferenceInt(context, Util.SAVE_KEY_STATUSBAR_HEIGHT,
				statusBarHeight);
		return statusBarHeight;
	}

	public static CoreBitmapObj getCoreBitmapWithSize(Context mContext,
			int imageRes, int viewWidth, int viewHeight, boolean fromTop) {
		try {
			Bitmap tempBitmap = ImageUtil
					.readBitmapByStream(mContext, imageRes);
			return getCoreBitmapWithSize(mContext, tempBitmap, viewWidth,
					viewHeight, fromTop, true);
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean isValidBitmap(Bitmap bitmap) {
		if (bitmap == null || bitmap.isRecycled()) {
			return false;
		} else {
			return true;
		}
	}

	public static void recycleBitmap(Bitmap bitmap) {
		try {
			if (bitmap != null) {
				if (!bitmap.isRecycled()) {
					bitmap.recycle();
				}
				bitmap = null;
			}
		} catch (Exception e) {
			Util.printException(e);
		}

	}
}
