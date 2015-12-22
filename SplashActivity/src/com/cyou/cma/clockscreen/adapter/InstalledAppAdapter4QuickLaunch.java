package com.cyou.cma.clockscreen.adapter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.LockApplication;
import com.cyou.cma.clockscreen.bean.InstalledAppBean;
import com.cyou.cma.clockscreen.event.SelectEvent;
import com.cyou.cma.clockscreen.event.SendEvent;
import com.cyou.cma.clockscreen.fragment.QuickContactsFragment;
import com.cyou.cma.clockscreen.fragment.QuickLaunchFragment;
import com.cyou.cma.clockscreen.quicklaunch.DatabaseUtil;
import com.cyou.cma.clockscreen.quicklaunch.QuickApplication;
import com.cyou.cma.clockscreen.util.StringUtils;
import com.cyou.cma.clockscreen.util.ToastMaster;
import com.cyou.cma.clockscreen.util.Util;
import com.nineoldandroids.view.ViewHelper;

import de.greenrobot.event.EventBus;

public class InstalledAppAdapter4QuickLaunch extends BaseAdapter {
	private Context mContext;
	private List<InstalledAppBean> mInstalledApps;

	private PackageManager pm;
	private int iconHeight;
	private int column;
	private int firstTopPadding;

	public static ConcurrentHashMap<String, QuickApplication> sQuickHashMap = new ConcurrentHashMap<String, QuickApplication>();

	public InstalledAppAdapter4QuickLaunch(Context context,
			List<InstalledAppBean> list) {
		this.mContext = context;
		this.mInstalledApps = list;
		pm = context.getPackageManager();
		int empty = mContext.getResources().getDimensionPixelSize(
				R.dimen.commonapp_empty_hoizontal);
		column = Util.getPreferenceInt(mContext,
				Util.SAVA_KEY_COMMONAPP_COLUMN, 4);
		iconHeight = (Util.getScreenWidth(mContext) - empty) / column;
		firstTopPadding = context.getResources().getDimensionPixelSize(
				R.dimen.install_app_layout_title_height);
	}

	@Override
	public int getCount() {
		return mInstalledApps.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		for (InstalledAppBean installedAppBean : mInstalledApps) {
			if (installedAppBean.isSelected()) {
			}
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		final InstalledAppBean info = mInstalledApps.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = View.inflate(mContext,
					R.layout.adapter_add_quicklaunch, null);
			viewHolder.mTouchView = convertView
					.findViewById(R.id.adapter_addcommon_touch_layout);
			viewHolder.checkbox = (ImageView) convertView
					.findViewById(R.id.adapter_addcommon_indicate);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.adapter_addcommon_icon);
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.adapter_addcommon_name);
			if (column == 5) {
				viewHolder.imageView.getLayoutParams().width = (int) (iconHeight * 0.8f);
				viewHolder.imageView.getLayoutParams().height = (int) (iconHeight * 0.8f);
			} else {
				viewHolder.imageView.getLayoutParams().height = iconHeight;
			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (position < column) {
			convertView.setPadding(0, firstTopPadding, 0, 0);
		} else {
			convertView.setPadding(0, 0, 0, 0);
		}
		if (info.isSelected()) {
			ViewHelper.setAlpha(viewHolder.imageView, 0.5f);
		} else {
			ViewHelper.setAlpha(viewHolder.imageView, 1f);
		}
		viewHolder.checkbox.setVisibility(info.isSelected() ? View.VISIBLE
				: View.GONE);
		if (info.getResolveInfo() != null) {
			viewHolder.name.setText(info.getResolveInfo().loadLabel(pm));
			viewHolder.imageView.setImageBitmap(info.getLogo());
			// viewHolder.imageView.setImageDrawable(info.getResolveInfo().loadIcon(pm));
		}
		viewHolder.mTouchView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				boolean targetSelected = !mInstalledApps.get(position)
						.isSelected();

				if (targetSelected) {
					if (isFolder
							&& (sQuickHashMap.size()
									+ QuickContactsFragment.mFolderApp.size() == 8)) {
						ToastMaster.makeText(mContext, R.string.most_eight,
								Toast.LENGTH_SHORT);
						return;
					}
				} else {
				}
				mInstalledApps.get(position).setSelected(targetSelected);

				notifyDataSetChanged();
				InstalledAppBean appBean = mInstalledApps.get(position);
				QuickApplication quickApplication = new QuickApplication();

				if (!isFolder) {
					String packageName = appBean.resolveInfo.activityInfo.packageName;
					quickApplication.setPackageName(packageName);
					String mainActivityClassName = appBean.resolveInfo.activityInfo.name;
					quickApplication
							.setMainActivityClassName(mainActivityClassName);
					quickApplication
							.setLaunchSetIdOfApplication(QuickLaunchFragment.ID);
					// String appName = (String)
					// appBean.resolveInfo.loadLabel(pm);
					DatabaseUtil
							.deleteApplicationOnLaunchset(QuickLaunchFragment.ID);
					long id = DatabaseUtil
							.saveApplicationToLaunchset(quickApplication);
					quickApplication.setId(id);
					SendEvent event = new SendEvent();
					event.eventType = SendEvent.APP_TYPE;
					// event.extra1 = appName;
					// event.extra2 =packageName;
					event.resolveInfo = appBean.resolveInfo;
					event.quickApplication = quickApplication;
					EventBus.getDefault().post(event);
					((Activity) mContext).finish();
				} else {
					String packageName = appBean.pakcageName;
					quickApplication.setPackageName(packageName);
					quickApplication
							.setMainActivityClassName(appBean.mainActivityClassName);
					if (targetSelected) {

						sQuickHashMap.put(packageName
								+ appBean.mainActivityClassName,
								quickApplication);
					} else {
						// sQuickHashMap.put(packageName, null);
						sQuickHashMap.remove(packageName
								+ appBean.mainActivityClassName);
					}
					SelectEvent event = new SelectEvent();
					EventBus.getDefault().post(event);
				}

			}
		});
		return convertView;
	}

	public boolean isFolder;

	public void setIsFolder(boolean isFolder) {
		this.isFolder = isFolder;
		if (isFolder) {
			if (LockApplication.mQuickFolder != null) {

				List<QuickApplication> applications = DatabaseUtil
						.getQuickApplicationOnFolder(LockApplication.mQuickFolder
								.getId());

				for (QuickApplication quickApplication : applications) {
					String packageName2 = quickApplication.getPackageName();
					String mainActivityClassName2 = quickApplication
							.getMainActivityClassName();
					if (StringUtils.isEmpty(mainActivityClassName2)) {
						mainActivityClassName2 = "";
					}
					sQuickHashMap.put(packageName2 + mainActivityClassName2,
							quickApplication);
					for (InstalledAppBean installedApplication : mInstalledApps) {
						String packageName1 = installedApplication.pakcageName;
						String mainActivityClassName1 = installedApplication.mainActivityClassName;

						if (packageName1.equals(packageName2)
								&& mainActivityClassName1
										.equals(mainActivityClassName2)) {
							installedApplication.selected = true;
						}
					}
				}
				notifyDataSetChanged();
			}
		}
	}

	private class ViewHolder {
		View mTouchView;
		ImageView checkbox;
		ImageView imageView;
		TextView name;
	}

}
