package com.cyou.cma.clockscreen.widget;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.LockApplication;
import com.cyou.cma.clockscreen.adapter.InstalledAppAdapter4QuickLaunch;
import com.cyou.cma.clockscreen.bean.InstalledAppBean;
import com.cyou.cma.clockscreen.fragment.QuickLaunchFragment;
import com.cyou.cma.clockscreen.quicklaunch.QuickApplication;
import com.cyou.cma.clockscreen.util.AppLockComparator;
import com.cyou.cma.clockscreen.util.Util;

public class InstalledLayout4QuickLaunch extends LinearLayout {

	private String TAG = "InstalledLayout";
	private Context mContext;
	public GridView mGridView;
	private View mLoadingLayout;

	public InstalledAppAdapter4QuickLaunch mAdapter;
	private List<InstalledAppBean> installList = new ArrayList<InstalledAppBean>();
	private List<QuickApplication> mLockedList = new ArrayList<QuickApplication>();
	private int mColumn = 4;
	private AppLockComparator mAppLockComparator;
	private DataLoadHandler mDataHandler;

	public InstalledLayout4QuickLaunch(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initView();
		installList.clear();
		loadData();
	}

	private void initView() {
		mAdapter = new InstalledAppAdapter4QuickLaunch(mContext, installList);
		mColumn = Util.getPreferenceInt(mContext,
				Util.SAVA_KEY_COMMONAPP_COLUMN, 4);
		View.inflate(mContext, R.layout.layout_installed_app, this);
		mGridView = (GridView) findViewById(R.id.installedapp_gridview);
		mGridView.setNumColumns(mColumn);
		mGridView.setAdapter(mAdapter);
		mLoadingLayout = findViewById(R.id.installedapp_loading_layout);

		mDataHandler = new DataLoadHandler(mAdapter, mLoadingLayout);
	}

	private void loadData() {
		List<ResolveInfo> tempHomeList = Util.getHomeList(mContext);
		if (tempHomeList != null && tempHomeList.size() > 0) {
			mHomeList.clear();
			for (ResolveInfo resolveInfo : tempHomeList) {
				mHomeList.add(resolveInfo.activityInfo.packageName);
			}
		}

		mAppLockComparator = new AppLockComparator(mContext);

		mLoadingLayout.setVisibility(View.VISIBLE);
		new DataLoadThread(installList, mLockedList, mContext, mDataHandler,
				mAppLockComparator, this).start();
	}

	static class DataLoadThread extends Thread {
		private WeakReference<List<InstalledAppBean>> installedReference;
		private WeakReference<List<QuickApplication>> mQuickApplicationRef;
		private WeakReference<Context> mContextReference;
		private WeakReference<DataLoadHandler> mHandlerReference;
		private WeakReference<AppLockComparator> mComparatorReference;
		private WeakReference<InstalledLayout4QuickLaunch> mInstalledLayoutReference;

		public DataLoadThread(List<InstalledAppBean> installedList,
				List<QuickApplication> lockedList, Context context,
				DataLoadHandler dataHandler, AppLockComparator comparator,
				InstalledLayout4QuickLaunch installedLayout4QuickLaunch) {
			installedReference = new WeakReference<List<InstalledAppBean>>(
					installedList);
			mQuickApplicationRef = new WeakReference<List<QuickApplication>>(
					lockedList);
			mContextReference = new WeakReference<Context>(context);
			mHandlerReference = new WeakReference<DataLoadHandler>(dataHandler);
			mComparatorReference = new WeakReference<AppLockComparator>(
					comparator);
			mInstalledLayoutReference = new WeakReference<InstalledLayout4QuickLaunch>(
					installedLayout4QuickLaunch);
		}

		@Override
		public void run() {

			try {

				/**
				 * 首先获取到数据库中所有的应用程序数据
				 */
				List<QuickApplication> applications = LockApplication.mQuickApplicationDao
						.loadAll();
				mQuickApplicationRef.get().addAll(applications);

				/**
				 * 查询到 手机中安装的所有应用
				 */
				final List<ResolveInfo> allInstalledApplications = Util
						.queryInstalledApps(mContextReference.get());

				InstalledAppBean installedApplication = null;
				PackageManager packageManager = mContextReference.get()
						.getPackageManager();
				for (ResolveInfo resolveInfo : allInstalledApplications) {
					String packageName = resolveInfo.activityInfo.packageName;

					if (isFilterApp(mContextReference.get(), resolveInfo)) {
						// 排除掉的应用
						continue;
					}
					String mainActivityClassName = Util
							.getMainActivityClassByPackageName(resolveInfo);
					if (mainActivityClassName == null) {
						continue;
					}
					installedApplication = new InstalledAppBean();
					installedApplication.setResolveInfo(resolveInfo);
					installedApplication.setLogo(resolveInfo
							.loadIcon(packageManager));
					installedApplication.pakcageName = packageName;
					if (LockApplication.sFolderPackageNames
							.contains(packageName)) {
						installedApplication.mainActivityClassName = "";
					} else {
						installedApplication.mainActivityClassName = mainActivityClassName;
					}
					boolean isFolder = mInstalledLayoutReference.get().isFolder;
					if (isFolder) {// 如果是文件夹
						if (LockApplication.mQuickFolder == null) {// 如果是新的文件夹
							if (mQuickApplicationRef.get() != null) {
								boolean find = false;
								for (QuickApplication quickApplication : mQuickApplicationRef
										.get()) {

									if (quickApplication
											.getPackageName()
											.equals(resolveInfo.activityInfo.packageName)
											&& (quickApplication
													.getMainActivityClassName()
													.equals(mainActivityClassName) || LockApplication.sFolderPackageNames
													.contains(quickApplication
															.getPackageName()))) {// 找到了
										installedApplication.setSelected(true);
										find = true;
										break;
									}
								}
								if (find) {
									// 新的文件夹 如果应用在其他的文件夹 或者快速入口中已经 选择
								} else {
									installedReference.get().add(
											installedApplication);
								}

							}
						} else {// 如果不是新的文件夹
							// 如果是新的文件夹
							if (mQuickApplicationRef.get() != null) {
								boolean find = false;
								QuickApplication quickApplication1 = null;
								for (QuickApplication quickApplication : mQuickApplicationRef
										.get()) {
									if (quickApplication
											.getPackageName()
											.equals(resolveInfo.activityInfo.packageName)
											&& (quickApplication
													.getMainActivityClassName()
													.equals(mainActivityClassName) || LockApplication.sFolderPackageNames
													.contains(quickApplication
															.getPackageName()))) {// 找到了
										installedApplication.setSelected(true);
										find = true;
										quickApplication1 = quickApplication;
										break;
									}
								}
								if (find) {
									// 不在同一个文件夹的不显示
									if (quickApplication1
											.getFolderIdOfApplication() == null) {
									} else {
										if (!(quickApplication1
												.getFolderIdOfApplication()
												.longValue() == LockApplication.mQuickFolder
												.getId().longValue())) {// 其他文件夹的不加进来
										} else {
											installedReference.get().add(
													installedApplication);
										}
									}
								} else {
									installedReference.get().add(
											installedApplication);
								}

							}

						}
					} else {// 如果是应用程序
						if (LockApplication.mQuickApplication == null) {// 如果是新的应用程序页
							// 如果是新的文件夹
							if (mQuickApplicationRef.get() != null) {
								boolean find = false;
								for (QuickApplication quickApplication : mQuickApplicationRef
										.get()) {
									if (quickApplication
											.getPackageName()
											.equals(resolveInfo.activityInfo.packageName)
											&& quickApplication
													.getMainActivityClassName()
													.equals(mainActivityClassName)) {// 找到了
										installedApplication.setSelected(true);
										find = true;
										break;
									}
								}
								if (find) {

								} else {
									installedReference.get().add(
											installedApplication);
								}

							}

						} else {// 如果不是新的应用程序
							if (mQuickApplicationRef.get() != null) {
								boolean find = false;
								QuickApplication quickApplication1 = null;
								for (QuickApplication quickApplication : mQuickApplicationRef
										.get()) {
									if (quickApplication
											.getPackageName()
											.equals(resolveInfo.activityInfo.packageName)
											&& quickApplication
													.getMainActivityClassName()
													.equals(mainActivityClassName)) {// 找到了
										installedApplication.setSelected(true);
										quickApplication1 = quickApplication;
										find = true;
										break;
									}
								}
								if (find) {
									// 不在同一个文件夹的不显示
									try {

										if (!(quickApplication1
												.getLaunchSetIdOfApplication()
												.longValue() == LockApplication.mQuickApplication
												.getLaunchSetIdOfApplication()
												.longValue())) {// 其他文件夹的不加进来
										} else {
											installedReference.get().add(
													installedApplication);
										}
									} catch (Exception e) {
										// TODO: handle exception
									}
								} else {
									installedReference.get().add(
											installedApplication);
								}

							}

						}

					}

				}
				Collections.sort(installedReference.get(),
						mComparatorReference.get());
				mHandlerReference.get().sendEmptyMessage(0);
				// tempStringList.clear();
			} catch (Exception e) {
				Util.printException(e);
			}
			// }
		}
	}

	/**
	 * 根据ResolveInfo判断是否需要过滤
	 * 
	 * @param context
	 * @param resolveInfo
	 * @return
	 */
	private static boolean isFilterApp(Context context, ResolveInfo resolveInfo) {
		if (resolveInfo == null) {
			return true;
		} else {
			// if (resolveInfo.filter != null
			// && resolveInfo.filter.hasCategory(Intent.CATEGORY_HOME)) {
			// return true;
			// }
			if (mHomeList.contains(resolveInfo.activityInfo.packageName)) {
				return true;
			}
			if (resolveInfo.activityInfo.packageName.equals(context
					.getPackageName()))
				return true;
			if (resolveInfo.activityInfo.packageName
					.startsWith(Constants.THEME_PACKAGENAME_PREFIX)) {
				return true;
			}
			if (resolveInfo.activityInfo.packageName
					.startsWith(Constants.THEME_CLAUNCHER_PACKAGENAME_PREFIX)) {
				return true;
			}
		}
		return false;
	}

	private static List<String> mHomeList = new ArrayList<String>();

	static class DataLoadHandler extends Handler {
		private WeakReference<InstalledAppAdapter4QuickLaunch> mAdapterReference;
		private WeakReference<View> mLoadingLayoutReference;

		private DataLoadHandler(InstalledAppAdapter4QuickLaunch adapter,
				View loadingLayout) {
			mAdapterReference = new WeakReference<InstalledAppAdapter4QuickLaunch>(
					adapter);
			mLoadingLayoutReference = new WeakReference<View>(loadingLayout);
		}

		@Override
		public void handleMessage(Message msg) {
			if (mAdapterReference != null && mAdapterReference.get() != null) {
				mAdapterReference.get().notifyDataSetChanged();
			}
			if (mLoadingLayoutReference != null
					&& mLoadingLayoutReference.get() != null) {
				mLoadingLayoutReference.get().setVisibility(View.GONE);
			}
		}
	}

	public void refreshData() {
		try {
			Collections.sort(installList, mAppLockComparator);
			mAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			Util.printException(e);
		}
	}

	public boolean isFolder;

	public void setIsFolder(boolean isFloder) {
		this.isFolder = isFloder;
		mAdapter.setIsFolder(isFloder);
	}
}
