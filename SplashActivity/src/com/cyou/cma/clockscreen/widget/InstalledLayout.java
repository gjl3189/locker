package com.cyou.cma.clockscreen.widget;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
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
import com.cyou.cma.clockscreen.adapter.InstalledAppAdapter;
import com.cyou.cma.clockscreen.bean.InstalledAppBean;
import com.cyou.cma.clockscreen.service.AppLockService;
import com.cyou.cma.clockscreen.sqlite.SqlListenerLockApp;
import com.cyou.cma.clockscreen.util.AppLockComparator;
import com.cyou.cma.clockscreen.util.SettingsHelper;
import com.cyou.cma.clockscreen.util.Util;

public class InstalledLayout extends LinearLayout {

	private String TAG = "InstalledLayout";
	private Context mContext;
	private GridView mGridView;
	private View mLoadingLayout;

	private InstalledAppAdapter mAdapter;
	private List<InstalledAppBean> installList = new ArrayList<InstalledAppBean>();
	private List<String> mLockedList = new ArrayList<String>();
	private int mColumn = 4;
	private AppLockComparator mAppLockComparator;
	private DataLoadHandler mDataHandler;

	public InstalledLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initView();
		installList.clear();
		// SqlListenerLockApp sql = new SqlListenerLockApp(mContext);
		// this.mLockedList = sql.getLockedApp();
		// sql.close();
		loadData();
		// mGridView.smoothScrollBy(0, 300);
	}

	private void initView() {
		mAdapter = new InstalledAppAdapter(mContext, installList);
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
		// final List<ResolveInfo> tempList = Util.queryInstalledApps(mContext);
		// InstalledAppBean installApp = null;
		// PackageManager pm = mContext.getPackageManager();
		// for (ResolveInfo resolveInfo : tempList) {
		// installApp = new InstalledAppBean();
		// installApp.setResolveInfo(resolveInfo);
		// installApp.setLogo(resolveInfo.loadIcon(pm));
		// boolean selected = false;
		// if (mLockedList != null) {
		// for (String commonApp : mLockedList) {
		// if (commonApp.equals(resolveInfo.activityInfo.packageName)) {
		// selected = true;
		// break;
		// }
		// }
		// }
		// installApp.setSelected(selected);
		// // installApp.setOldSelected(selected);
		// installList.add(installApp);
		// }
		// Collections.sort(installList, mAppLockComparator);
		// mAdapter.notifyDataSetChanged();
		// mGridView.setVisibility(View.GONE);
		mLoadingLayout.setVisibility(View.VISIBLE);
		new DataLoadThread(installList, mLockedList, mContext, mDataHandler,
				mAppLockComparator).start();
	}

	static class DataLoadThread extends Thread {
		private WeakReference<List<InstalledAppBean>> installedReference;
		private WeakReference<List<String>> mLockedListReference;
		private WeakReference<Context> mContextReference;
		private WeakReference<DataLoadHandler> mHandlerReference;
		private WeakReference<AppLockComparator> mComparatorReference;

		public DataLoadThread(List<InstalledAppBean> installedList,
				List<String> lockedList, Context context,
				DataLoadHandler dataHandler, AppLockComparator comparator) {
			installedReference = new WeakReference<List<InstalledAppBean>>(
					installedList);
			mLockedListReference = new WeakReference<List<String>>(lockedList);
			mContextReference = new WeakReference<Context>(context);
			mHandlerReference = new WeakReference<DataLoadHandler>(dataHandler);
			mComparatorReference = new WeakReference<AppLockComparator>(
					comparator);
		}

		@Override
		public void run() {
			// if (mLockedListReference != null
			// && mLockedListReference.get() != null
			// && mHandlerReference != null
			// && mHandlerReference.get() != null
			// && mContextReference != null
			// && mContextReference.get() != null
			// && installedReference != null
			// && installedReference.get() != null
			// && mComparatorReference != null
			// && mComparatorReference.get() != null) {
			// List<String> tempStringList = new ArrayList<String>();
			try {
				SqlListenerLockApp sql = new SqlListenerLockApp(
						mContextReference.get());
				mLockedListReference.get().addAll(sql.getLockedApp());
				sql.close();
				final List<ResolveInfo> tempList = Util
						.queryInstalledApps(mContextReference.get());
				InstalledAppBean installApp = null;
				PackageManager pm = mContextReference.get().getPackageManager();
				for (ResolveInfo resolveInfo : tempList) {
					// if (tempStringList
					// .contains(resolveInfo.activityInfo.packageName)) {
					// continue;
					// }
					if (isFilterApp(mContextReference.get(), resolveInfo)) {
						continue;
					}
					installApp = new InstalledAppBean();
					installApp.setResolveInfo(resolveInfo);
					installApp.setLogo(resolveInfo.loadIcon(pm));
					boolean selected = false;
					if (mLockedListReference.get() != null) {
						for (String commonApp : mLockedListReference.get()) {
							if (commonApp
									.equals(resolveInfo.activityInfo.packageName)) {
								selected = true;
								break;
							}
						}
					}
					installApp.setSelected(selected);
					installedReference.get().add(installApp);
					// tempStringList.add(resolveInfo.activityInfo.packageName);
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
		private WeakReference<InstalledAppAdapter> mAdapterReference;
		private WeakReference<View> mLoadingLayoutReference;

		private DataLoadHandler(InstalledAppAdapter adapter, View loadingLayout) {
			mAdapterReference = new WeakReference<InstalledAppAdapter>(adapter);
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

	public void saveData() {
		if (mLoadingLayout.getVisibility() == View.VISIBLE)
			return;
		SqlListenerLockApp sql = new SqlListenerLockApp(mContext);
		boolean lockedApp = sql.insertLockedApp(installList);
		sql.close();
		SettingsHelper.setApplockEnable(mContext, lockedApp ? 1 : 0);
		if (lockedApp) {
			mContext.startService(new Intent(mContext, AppLockService.class));
		} else {
			mContext.stopService(new Intent(mContext, AppLockService.class));
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

	public void setIsGuide(boolean isGuide, int hintHeight) {
		if (mAdapter != null) {
			mAdapter.setIsGuide(isGuide, hintHeight);
		}
	}

}
