package com.cyou.cma.clockscreen;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.util.Log;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.quicklaunch.DaoMaster;
import com.cyou.cma.clockscreen.quicklaunch.DaoMaster.DevOpenHelper;
import com.cyou.cma.clockscreen.quicklaunch.DaoSession;
import com.cyou.cma.clockscreen.quicklaunch.LaunchSet;
import com.cyou.cma.clockscreen.quicklaunch.LaunchSetDao;
import com.cyou.cma.clockscreen.quicklaunch.QuickApplication;
import com.cyou.cma.clockscreen.quicklaunch.QuickApplicationDao;
import com.cyou.cma.clockscreen.quicklaunch.QuickContact;
import com.cyou.cma.clockscreen.quicklaunch.QuickContactDao;
import com.cyou.cma.clockscreen.quicklaunch.QuickFolder;
import com.cyou.cma.clockscreen.quicklaunch.QuickFolderDao;
import com.cyou.cma.clockscreen.service.KeyguardService;
import com.cyou.cma.clockscreen.sqlite.SqlListenerAppLockSort;
import com.cyou.cma.clockscreen.util.CrashHandler;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.LauchSetType;
import com.cyou.cma.clockscreen.util.ResourceUtil;
import com.cyou.cma.clockscreen.util.SettingsHelper;
import com.cyou.cma.clockscreen.util.Util;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class LockApplication extends Application {

	public static final String TAG = "AppApplication";

	private static LockApplication application;

	private Context mContext;

	/** 图片加载相关 **/
	private DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.loading_thumb)
			.showImageOnFail(R.drawable.loading_failed).cacheInMemory(false)
			.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
			.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).build();
	private ImageLoaderConfiguration applicationConfig;
	private FileNameGenerator mFileNameGenerator;
	private static boolean sStaySetting = false;

	public static SQLiteDatabase db;
	public static DaoMaster daoMaster;
	public static DaoSession daoSession;
	public static LaunchSetDao launchSetDao;
	public static QuickContactDao mQuickContactDao;
	public static QuickApplicationDao mQuickApplicationDao;
	public static QuickFolderDao mQuickFolderDao;
	public static List<LaunchSet> sLaunchSet = new ArrayList<LaunchSet>();
	public static QuickContact mQuickContact;
	public static QuickFolder mQuickFolder;
	public static QuickApplication mQuickApplication;
	public static ArrayList<String> sFolderPackageNames = new ArrayList<String>();



	@Override
	public void onCreate() {

		super.onCreate();
		Util.Logjb("TAG", "application oncreate ");
		mContext = getApplicationContext();
		SEEK_COLOR_POSITION = ResourceUtil.dip2px(this, 10);
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "quicklaunch",
				null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		sFolderPackageNames.add("com.android.calculator2");
		sFolderPackageNames.add("com.android.deskclock");
		sFolderPackageNames.add("com.android.vending");
		sFolderPackageNames.add("com.devuni.flashlight");
		sFolderPackageNames.add("com.cleanmaster.mguard");
		sFolderPackageNames.add("com.dianxinos.optimizer.duplay");
		sFolderPackageNames.add("com.UCMobile.intl");
		sFolderPackageNames.add("com.google.android.apps.translate");
		sFolderPackageNames.add("com.cleanmaster.security");
		sFolderPackageNames.add("com.avast.android.mobilesecurity");
		sFolderPackageNames.add("com.jb.gokeyboard");
		launchSetDao = daoSession.getLaunchSetDao();
		mQuickContactDao = daoSession.getQuickContactDao();
		mQuickApplicationDao = daoSession.getQuickApplicationDao();
		mQuickFolderDao = daoSession.getQuickFolderDao();
		// 初始化数据库
		List<LaunchSet> launchSets = launchSetDao.loadAll();
		if (launchSets.size() == 0) {
			LaunchSet launchSet1 = new LaunchSet();
			LaunchSet launchSet2 = new LaunchSet();
			LaunchSet launchSet3 = new LaunchSet();
			LaunchSet launchSet4 = new LaunchSet();
			LaunchSet launchSet5 = new LaunchSet();
			launchSet1.setTag(1);
			launchSet2.setTag(2);
			launchSet3.setTag(3);
			launchSet4.setTag(4);
			launchSet5.setTag(5);
			launchSetDao.insert(launchSet1);
			launchSetDao.insert(launchSet2);
			launchSetDao.insert(launchSet3);
			launchSetDao.insert(launchSet4);
			launchSetDao.insert(launchSet5);

			SqlListenerAppLockSort sqlListenerLockApp = new SqlListenerAppLockSort(
					this);
			List<String> pakcages = sqlListenerLockApp.getDefaultSortPackages();
		 
			// pakcages.add("com.cyou.cma.clockscreen");
			List<String> hehe = new ArrayList<String>();
			for (String s : pakcages) {
				if (Util.appInstalled(s, this)) {
					hehe.add(s);
					if (hehe.size() == 3)
						break;
				}
			}
			sLaunchSet = launchSetDao.loadAll();
			for (int i = 0; i < hehe.size(); i++) {
				QuickApplication quickApplication = new QuickApplication();
				quickApplication.setPackageName(hehe.get(i));
				String mainActivityClassName = Util
						.getMainActivityClassByPackageName(
								quickApplication.getPackageName(),
								getPackageManager());
				quickApplication
						.setMainActivityClassName(mainActivityClassName);
				quickApplication.setLaunchSetIdOfApplication(sLaunchSet.get(i)
						.getId());
				mQuickApplicationDao.insert(quickApplication);
				LaunchSet launchSet = sLaunchSet.get(i);
				launchSet.setType(LauchSetType.APP_TYPE);
				launchSetDao.update(launchSet);
			}

			QuickFolder quickFolder = new QuickFolder();
			quickFolder.setFolderName("tools");
			quickFolder.setLaunchSetIdOfFolder(sLaunchSet.get(hehe.size())
					.getId());
			LaunchSet launchSet = sLaunchSet.get(hehe.size());
			launchSet.setType(LauchSetType.FOLDER_TYPE);
			launchSetDao.update(launchSet);
			long quickFolderId = mQuickFolderDao.insert(quickFolder);
			int count = 0;
			for (String packageName : sFolderPackageNames) {
				if (Util.appInstalled(packageName, this)) {

					QuickApplication quickApplication = new QuickApplication();
					quickApplication.setPackageName(packageName);
					// String mainActivityClassName =
					// Util.getMainActivityClassByPackageName(quickApplication.getPackageName(),getPackageManager());
					String mainActivityClassName = "";
					quickApplication
							.setMainActivityClassName(mainActivityClassName);
					quickApplication.setFolderIdOfApplication(quickFolderId);
					mQuickApplicationDao.insert(quickApplication);
					count++;
				}
				if (count == 8)
					break;
			}
			quickFolder.setSubCount(count);
			mQuickFolderDao.update(quickFolder);

		}
		if (sLaunchSet.size() == 0)
			sLaunchSet = launchSetDao.loadAll();
		if (!Util.DEBUG) {
			CrashHandler crashHandler = CrashHandler.getInstance();
			crashHandler.init(this);
		}
		if (android.os.Build.VERSION.SDK_INT >= 9 && Util.DEBUG) {
			StrictMode.VmPolicy policy = new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyLog().build();
			StrictMode.setVmPolicy(policy);
		}
		application = this;

		boolean isFirstLaunch = Util.getPreferenceBoolean(
				mContext,
				Util.SAVE_KEY_FIRST_LAUNCH
						+ Util.getCurrenVersionCode(mContext), true);

		if (isFirstLaunch) {// TODO getWallpaper 方法 nullpoint
			// ProviderHelper.getWallpapers(getApplicationContext(),
			// Constants.C_LOCKER_DEFAULT_THEME);
			Util.putPreferenceBoolean(mContext, Util.SAVE_KEY_FIRST_LAUNCH
					+ Util.getCurrenVersionCode(mContext), false);

			if (!SettingsHelper.getLockServiceEnable(this)) {

				SettingsHelper
						.setLockServiceState(this, Constants.C_SERVICE_ON);

			}
			// if (Util.getPreferenceInt(mContext, Util.SAVE_KEY_THEME_INDEX,
			// -1) != -1) {
			// Util.putPreferenceBoolean(mContext,
			// Util.SAVE_KEY_SHOW_UPDATE_HINT, true);
			// }
		}
		initImageLoader(mContext);

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// TODO jiangbin 配置发生变化是否有问题
		// try {
		// getDexClassLoader().initDexResource(getApplicationContext());
		// } catch (Exception e) {
		// }

	}

	@Override
	public void onLowMemory() {
		Util.Logjb(TAG, "onLowMemory called");
		super.onLowMemory();
		if (SettingsHelper.getLockServiceEnable(this)) {
			startService(new Intent(this, KeyguardService.class));
		}
	}

	public void initImageLoader(Context context) {
		mFileNameGenerator = new Md5FileNameGenerator();
		applicationConfig = new ImageLoaderConfiguration.Builder(mContext)
				.threadPriority(Thread.NORM_PRIORITY - 2).threadPoolSize(3)
				.diskCacheFileNameGenerator(mFileNameGenerator)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.defaultDisplayImageOptions(options)
				.memoryCache(new LruMemoryCache(5 * 1024 * 1024))
				.diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100) // release
				.build();

		ImageLoader.getInstance().init(applicationConfig);
	}

	public ImageLoaderConfiguration getImageLoaderConfiguration() {
		return applicationConfig;
	}

	public FileNameGenerator getFileNameGenerator() {
		return mFileNameGenerator;
	}

	public static LockApplication getInstance() {
		return application;
	}

	public static boolean getStaySetting() {
		return sStaySetting;
	}

	private WeakReference<Bitmap> mCommonAppMaskBitmap;
	private WeakReference<Bitmap> mCommonAppTopBitmap;
	private WeakReference<Bitmap> mCommonBelowTopBitmap;
	private WeakReference<Bitmap> mQuickLaunchMaskBitmap;
	private WeakReference<Bitmap> mQuickLaunchFolderMaskBitmap;
	private WeakReference<Bitmap> mNotificationMaskBitmap;
	private WeakReference<Bitmap> mNotificationMaskBitmapOther;
	private final Object mCommonMaskLockBitmap = new Object();
	private final Object mQuickLaunchMaskLockBitmap = new Object();
	private final Object mQuickFolderLaunchMaskLockBitmap = new Object();
	private final Object mNotificationMaskLock = new Object();
	private final Object mNotificationMaskLockOther = new Object();
	
	public Bitmap getmCommonAppMaskBitmap() {
		synchronized (mCommonMaskLockBitmap) {
			if (mCommonAppMaskBitmap == null
					|| mCommonAppMaskBitmap.get() == null
					|| mCommonAppMaskBitmap.get().isRecycled()) {
				// mCommonAppMaskBitmap = new
				// WeakReference<Bitmap>(ImageUtil.readBitmapWithDensity(this,
				// R.drawable.icon_common_mask));// TODO 调试换成ImageUtil.readby
				mCommonAppMaskBitmap = new WeakReference<Bitmap>(
						BitmapFactory.decodeResource(this.getResources(),
								R.drawable.icon_common_mask));

			}
			return mCommonAppMaskBitmap.get();
		}
	}

	public Bitmap getInstallMaskBitmap(Drawable oldDrawable) {

		return getBitmapWithMask(oldDrawable, getmCommonAppMaskBitmap());
	}
	
	public Bitmap getNotificationMaskBitmap(Drawable oldDrawable){
		return getBitmapWithMask(oldDrawable, getNotificationBitmap());
	}
	
	public Bitmap getNotificationMaskBitmapOther(Drawable oldDrawable){
		return getBitmapWithMask(oldDrawable, getNotificationBitmapOther());
	}
	
	private Bitmap getQuickLaunchMaskBitmap() {
		synchronized (mQuickLaunchMaskLockBitmap) {
			if (mQuickLaunchMaskBitmap == null
					|| mQuickLaunchMaskBitmap.get() == null
					|| mQuickLaunchMaskBitmap.get().isRecycled()) {
				mQuickLaunchMaskBitmap = new WeakReference<Bitmap>(
						ImageUtil.readBitmapWithDensity(this,
								R.drawable.icon_quicklaunch_mask));
			}
			return mQuickLaunchMaskBitmap.get();
		}
	}
	
	public Bitmap getNotificationBitmap(){
		synchronized (mNotificationMaskLock) {

				if (mNotificationMaskBitmap == null
						|| mNotificationMaskBitmap.get() == null
						|| mNotificationMaskBitmap.get().isRecycled()) {
					// mCommonAppMaskBitmap = new
					// WeakReference<Bitmap>(ImageUtil.readBitmapWithDensity(this,
					// R.drawable.icon_common_mask));// TODO 调试换成ImageUtil.readby
					mNotificationMaskBitmap = new WeakReference<Bitmap>(
							BitmapFactory.decodeResource(this.getResources(),
									R.drawable.notification_mask));

				}
				return mNotificationMaskBitmap.get();
		
		}
		
	}
	
	public Bitmap getNotificationBitmapOther(){

		synchronized (mNotificationMaskLockOther) {

				if (mNotificationMaskBitmapOther == null
						|| mNotificationMaskBitmapOther.get() == null
						|| mNotificationMaskBitmapOther.get().isRecycled()) {
					// mCommonAppMaskBitmap = new
					// WeakReference<Bitmap>(ImageUtil.readBitmapWithDensity(this,
					// R.drawable.icon_common_mask));// TODO 调试换成ImageUtil.readby
					mNotificationMaskBitmapOther = new WeakReference<Bitmap>(
							BitmapFactory.decodeResource(this.getResources(),
									R.drawable.notification_other_mask));

				}
				return mNotificationMaskBitmapOther.get();
		
		}
		
	
	}

	private Bitmap getQuickLaunchFolderMaskBitmap() {
		synchronized (mQuickFolderLaunchMaskLockBitmap) {
			if (mQuickLaunchFolderMaskBitmap == null
					|| mQuickLaunchFolderMaskBitmap.get() == null
					|| mQuickLaunchFolderMaskBitmap.get().isRecycled()) {
				mQuickLaunchFolderMaskBitmap = new WeakReference<Bitmap>(
						ImageUtil.readBitmapWithDensity(this,
								R.drawable.icon_quicklaunch_mask_folder));
			}
			return mQuickLaunchFolderMaskBitmap.get();
		}
	}

	private Paint mAppIconPaint = new Paint();
	private Paint mXfermodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	public Bitmap getQuickLaunchWithMaskBitmap(Drawable oldDrawable,
			boolean isFolder) {
		return getBitmapWithMask(oldDrawable,
				isFolder ? getQuickLaunchFolderMaskBitmap()
						: getQuickLaunchMaskBitmap());
	}

	public Bitmap getQuickLaunchWithMaskBitmap(Bitmap oldBitmap,
			boolean isFolder) {
		return getBitmapWithMask(oldBitmap,
				isFolder ? getQuickLaunchFolderMaskBitmap()
						: getQuickLaunchMaskBitmap());
	}

	public Bitmap getBitmapWithMask(Drawable oldDrawable, Bitmap maskBitmap) {
		try {
			if (oldDrawable == null || ((BitmapDrawable) oldDrawable) == null)
				return null;
		} catch (Exception e) {
			return null;
		}
		return getBitmapWithMask(((BitmapDrawable) oldDrawable).getBitmap(),
				maskBitmap);
	}

	public Bitmap getBitmapWithMask(Bitmap src, Bitmap maskBitmap) {
		if (src == null)
			return null;
		if (src.getWidth() != maskBitmap.getWidth()
				&& src.getHeight() != maskBitmap.getHeight()) {
			src = Bitmap.createScaledBitmap(src, maskBitmap.getWidth() + 2,
					maskBitmap.getHeight() + 2, true);
		}
		Bitmap result = Bitmap.createBitmap(maskBitmap.getWidth(),
				maskBitmap.getHeight(), Config.ARGB_8888);
		Canvas c = new Canvas(result);
		mXfermodePaint.setXfermode(new PorterDuffXfermode(
				PorterDuff.Mode.DST_IN));
		int xOffset = (maskBitmap.getWidth() - src.getWidth()) / 2;
		int yOffset = (maskBitmap.getHeight() - src.getHeight()) / 2;
		mAppIconPaint.setShader(getLogoBaseColor(src));
		c.drawPaint(mAppIconPaint);
		c.drawBitmap(src, xOffset, yOffset, null);
		// c.drawBitmap(src, new Rect(0, 0, src.getWidth(), src.getHeight()),
		// new Rect(xOffset, yOffset, maskBitmap
		// .getWidth() - xOffset, maskBitmap
		// .getHeight() - yOffset), null);
		if (ImageUtil.isValidBitmap(maskBitmap)) {
			c.drawBitmap(maskBitmap, 0, 0, mXfermodePaint);
		}
		mXfermodePaint.setXfermode(null);
		return result;
	}

	// private Bitmap suiteIconBitmap(Bitmap src) {
	// Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
	// Config.ARGB_8888);
	// Canvas c = new Canvas(result);
	// mXfermodePaint.setXfermode(new PorterDuffXfermode(
	// PorterDuff.Mode.DST_IN));
	// c.drawBitmap(src, 0, 0, null);
	// if (ImageUtil.isValidBitmap(getQuickLaunchMaskBitmap())) {
	// c.drawBitmap(getQuickLaunchMaskBitmap(), new Rect(0, 0,
	// getQuickLaunchMaskBitmap().getWidth(),
	// getQuickLaunchMaskBitmap().getHeight()),
	// new Rect(0, 0, src.getWidth(), src.getHeight()),
	// mXfermodePaint);
	// }
	// mXfermodePaint.setXfermode(null);
	// return result;
	// }

	private Shader getLogoBaseColor(Bitmap icon) {
		int[] color = calculateMatColor(icon);
		Shader shader = new LinearGradient(0, 0, 0, icon.getHeight(),
				new int[] { color[1], color[0] }, null, Shader.TileMode.MIRROR);
		return shader;
	}

	private int SEEK_COLOR_POSITION = 20; // dp
	private static final int DEFAULT_MAT_COLOR = Color.argb(255, 130, 130, 130);
	private static final int MAT_GRADIENT_RANGE = 50; // 0~255

	private int[] calculateMatColor(Bitmap icon) {
		if (icon == null) {
			return new int[] { DEFAULT_MAT_COLOR, DEFAULT_MAT_COLOR };
		}
		int width = icon.getWidth();
		int height = icon.getHeight();
		if (width <= 0 || height <= 0) {
			return new int[] { DEFAULT_MAT_COLOR, DEFAULT_MAT_COLOR };
		}

		int colors[] = new int[3];
		if (width > SEEK_COLOR_POSITION + 1 && height > SEEK_COLOR_POSITION + 1) {
			colors[0] = icon.getPixel(SEEK_COLOR_POSITION, SEEK_COLOR_POSITION);
			// 第二点不取右下角的颜色，使用透明黑色
			// colors[1] = icon.getPixel(width - SEEK_COLOR_POSITION, height -
			// SEEK_COLOR_POSITION);
		} else {
			colors[0] = icon.getPixel(width / 3, height / 3);
			// 第二点不取右下角的颜色，使用透明黑色
			// colors[1] = icon.getPixel(width/3*2, height/3*2);
		}
		// 第二点不取右下角的颜色，使用透明黑色
		colors[1] = Color.argb(0, 100, 100, 100);
		colors[2] = icon.getPixel(width / 2, height / 2);
		return averageColor(colors);
	}

	private int[] averageColor(int[] colors) {
		int red = 0;
		int green = 0;
		int blue = 0;
		int num = colors.length;
		if (colors.length <= 0) {
			return new int[] { DEFAULT_MAT_COLOR, DEFAULT_MAT_COLOR };
		}

		boolean hasNoneTransparentPoint = false;
		for (int i = 0; i < colors.length; i++) {
			if (i == 2 && hasNoneTransparentPoint) {
				// 如果左上、右下两个点有一个不是透明的，则不再取图标中间的点
				num = 2;
				break;
			}

			if (Color.alpha(colors[i]) == 255) {
				hasNoneTransparentPoint = true;
			}
			red += Color.red(colors[i]);
			green += Color.green(colors[i]);
			blue += Color.blue(colors[i]);
		}

		int r = red / num;
		int g = green / num;
		int b = blue / num;
		return new int[] {
				Color.argb(255, r + MAT_GRADIENT_RANGE > 255 ? 255 : r
						+ MAT_GRADIENT_RANGE,
						g + MAT_GRADIENT_RANGE > 255 ? 255 : g
								+ MAT_GRADIENT_RANGE,
						b + MAT_GRADIENT_RANGE > 255 ? 255 : b
								+ MAT_GRADIENT_RANGE), Color.argb(255, r, g, b) };
	}

	// public Bitmap getmCommonAppTopBitmap() {
	// synchronized (mCommonTopLockBitmap) {
	// if (mCommonAppTopBitmap == null
	// || mCommonAppTopBitmap.get() == null
	// || mCommonAppTopBitmap.get().isRecycled()) {
	// mCommonAppTopBitmap = new WeakReference<Bitmap>(
	// BitmapFactory.decodeResource(this.getResources(),
	// R.drawable.icon_common_top));
	// }
	// return mCommonAppTopBitmap.get();
	// }
	// }
	//
	// public Bitmap getmCommonAppBelowBitmap() {
	// synchronized (mCommonBelowLockBitmap) {
	// if (mCommonBelowTopBitmap == null
	// || mCommonBelowTopBitmap.get() == null
	// || mCommonBelowTopBitmap.get().isRecycled()) {
	// mCommonBelowTopBitmap = new WeakReference<Bitmap>(
	// BitmapFactory.decodeResource(this.getResources(),
	// R.drawable.icon_common_below));
	// }
	// return mCommonBelowTopBitmap.get();
	// }
	// }

}
