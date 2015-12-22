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
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.adapter.InstalledAppAdapter4Notification;
import com.cyou.cma.clockscreen.bean.InstalledAppBean;
import com.cyou.cma.clockscreen.event.LoadingEvent;
import com.cyou.cma.clockscreen.notification.NotificationUtil;
import com.cyou.cma.clockscreen.notification.NotificationUtil.PM;
import com.cyou.cma.clockscreen.util.AppLockComparator;
import com.cyou.cma.clockscreen.util.AppLockComparator4Notification;
import com.cyou.cma.clockscreen.util.ResolveInfoUtil;
import com.cyou.cma.clockscreen.util.StringUtils;
import com.cyou.cma.clockscreen.util.Util;

import de.greenrobot.event.EventBus;

public class InstalledLayout4Notification extends LinearLayout {

	private String TAG = "InstalledLayout";
	private Context mContext;
	private GridView mGridView;
	private View mLoadingLayout;

	public static final String NOTIFICATION_EXTRA = "notification_extra";

	private InstalledAppAdapter4Notification mAdapter;
	private List<InstalledAppBean> installList = new ArrayList<InstalledAppBean>();
	private int mColumn = 4;
	private AppLockComparator4Notification mAppLockComparator;
	private DataLoadHandler mDataHandler;

	public InstalledLayout4Notification(Context context, AttributeSet attrs) {
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
		mAdapter = new InstalledAppAdapter4Notification(mContext, installList);
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
//				if ("com.android.contacts.activities.DialtactsActivity"
//						.equals(resolveInfo.activityInfo.packageName)) {
//					mHomeList.add("com.android.phone");
//				} else {
					mHomeList.add(resolveInfo.activityInfo.packageName);
//				}
			}
		}

		mAppLockComparator = new AppLockComparator4Notification(mContext);

		mLoadingLayout.setVisibility(View.VISIBLE);
		new DataLoadThread(installList, mContext, mDataHandler,
				mAppLockComparator).start();
	}

	static class DataLoadThread extends Thread {
		private WeakReference<List<InstalledAppBean>> installedReference;
		private WeakReference<Context> mContextReference;
		private WeakReference<DataLoadHandler> mHandlerReference;
		private WeakReference<AppLockComparator4Notification> mComparatorReference;

		public DataLoadThread(List<InstalledAppBean> installedList,
				Context context, DataLoadHandler dataHandler,
				AppLockComparator4Notification comparator) {
			installedReference = new WeakReference<List<InstalledAppBean>>(
					installedList);
			mContextReference = new WeakReference<Context>(context);
			mHandlerReference = new WeakReference<DataLoadHandler>(dataHandler);
			mComparatorReference = new WeakReference<AppLockComparator4Notification>(
					comparator);
		}

		@Override
		public void run() {
			try {
				 
				final List<ResolveInfo> tempList = Util
						.queryInstalledApps(mContextReference.get());
				InstalledAppBean installApp = null;
				PackageManager pm = mContextReference.get().getPackageManager();
				ArrayList<PM> list = NotificationUtil
						.getNotificationPackages(mContextReference.get());
				
				for (ResolveInfo resolveInfo : tempList) {
				 
					if (isFilterApp(mContextReference.get(), resolveInfo)) {
						continue;
					}
					installApp = new InstalledAppBean();
					installApp.setResolveInfo(resolveInfo);
					installApp.setLogo(resolveInfo.loadIcon(pm));
					installApp.mainActivityClassName = Util
							.getMainActivityClassByPackageName(resolveInfo);
					installApp.pakcageName = ResolveInfoUtil
							.getPakcageName(resolveInfo);
					boolean selected = false;

					for (PM pm2 : list) {
						if (pm2.packageName.equals(installApp.pakcageName)
								&& pm2.mainActivityClass
										.equals(installApp.mainActivityClassName)) {
							selected = true;
							break;
						}
						// }
					}
					installApp.setSelected(selected);
					installedReference.get().add(installApp);
				
				}
				Collections.sort(installedReference.get(),
						mComparatorReference.get());
				Message message = new Message();
				if (list.size() == installedReference.get().size()) {
					message.arg1 = 1;// 全选
				} else {
					message.arg1 = 2;// 全选
				}
				message.arg2 = list.size();
				message.what = installedReference.get().size();
				mHandlerReference.get().sendMessage(message);
				// tempStringList.clear();
			} catch (Exception e) {
				Util.printException(e);
			}
			// }
		}
	}

	public void selectALl() {
		for (InstalledAppBean installedAppBean : installList) {
			installedAppBean.setSelected(true);
			mAdapter.notifyDataSetChanged();
		}
	}

	public void deselectALl() {
		for (InstalledAppBean installedAppBean : installList) {
			installedAppBean.setSelected(false);
			mAdapter.notifyDataSetChanged();
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
//			 if("com.android.contacts".equals(resolveInfo.activityInfo.packageName)){
//				 return true;
//			 }
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
		private WeakReference<InstalledAppAdapter4Notification> mAdapterReference;
		private WeakReference<View> mLoadingLayoutReference;

		private DataLoadHandler(InstalledAppAdapter4Notification adapter,
				View loadingLayout) {
			mAdapterReference = new WeakReference<InstalledAppAdapter4Notification>(
					adapter);
			mLoadingLayoutReference = new WeakReference<View>(loadingLayout);
		}

		@Override
		public void handleMessage(Message msg) {
			if (mAdapterReference != null && mAdapterReference.get() != null) {
				mAdapterReference.get().notifyDataSetChanged();
				LoadingEvent event = new LoadingEvent();
				event.type = LoadingEvent.TYPE1;
				event.allSelected = (msg.arg1 == 1);
				event.count = msg.arg2;
				event.allCount = msg.what;
				EventBus.getDefault().post(event);
			}
			if (mLoadingLayoutReference != null
					&& mLoadingLayoutReference.get() != null) {
				mLoadingLayoutReference.get().setVisibility(View.GONE);
			}
		}
	}

	public void setIsGuide(boolean isGuide, int hintHeight) {
		if (mAdapter != null) {
			mAdapter.setIsGuide(isGuide, hintHeight);
		}
	}

	public void saveData() {
		if (mLoadingLayout.getVisibility() == View.VISIBLE)
			return;
		StringBuffer sb = new StringBuffer();
		for (InstalledAppBean bean : installList) {
			if (bean.isSelected()) {
				sb.append(bean.pakcageName + "," + bean.mainActivityClassName
						+ ";");
			}
		}
		String s = sb.toString();
		Util.putPreferenceString(mContext, NOTIFICATION_EXTRA, s);
	}

}
