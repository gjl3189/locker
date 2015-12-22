package com.cyou.cma.clockscreen.adapter;

import java.util.List;

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
import com.cyou.cma.clockscreen.bean.InstalledAppBean;
import com.cyou.cma.clockscreen.sqlite.SqlListenerAppLockSort;
import com.cyou.cma.clockscreen.util.ToastMaster;
import com.cyou.cma.clockscreen.util.Util;
import com.nineoldandroids.view.ViewHelper;

public class InstalledAppAdapter extends BaseAdapter {
	private Context mContext;
	private List<InstalledAppBean> mInstalledList;
	private int selectedCount;

	private PackageManager pm;
	private int iconHeight;
	private int column;
	private int firstTopPadding;
	private boolean isProVersion = false;

	private boolean isGuide = false;
	private int mHintHeight;
	private int totalRow = 0;

	public InstalledAppAdapter(Context context, List<InstalledAppBean> list) {
		this.mContext = context;
		this.mInstalledList = list;
		pm = context.getPackageManager();
		int empty = mContext.getResources().getDimensionPixelSize(
				R.dimen.commonapp_empty_hoizontal);
		column = Util.getPreferenceInt(mContext,
				Util.SAVA_KEY_COMMONAPP_COLUMN, 4);
		iconHeight = (Util.getScreenWidth(mContext) - empty) / column;
		firstTopPadding = context.getResources().getDimensionPixelSize(
				R.dimen.install_app_layout_title_height);
		isProVersion = Util.isProVersion(mContext);
	}

	@Override
	public int getCount() {
		return mInstalledList.size();
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
		totalRow = getTotalRowNumber();
		selectedCount = 0;
		for (InstalledAppBean installedAppBean : mInstalledList) {
			if (installedAppBean.isSelected()) {
				selectedCount++;
			}
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		final InstalledAppBean info = mInstalledList.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = View.inflate(mContext,
					R.layout.adapter_add_commonappitem, null);
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
			if (isGuide) {
				// int lastBeginIndex = list.size() % column == 0 ? (list.size()
				// - column)
				// : (list.size() - list.size() % column);
				if (getCurrentRowNumber(position) == totalRow) {
					convertView.setPadding(0, 0, 0, mHintHeight);
				}
			}
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
			// viewHolder.imageView.setImageDrawable(info.getLogo());
			viewHolder.imageView.setImageBitmap(info.getLogo());
			// viewHolder.imageView.setImageDrawable(info.getResolveInfo().loadIcon(pm));
		}
		viewHolder.mTouchView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean targetSelected = !mInstalledList.get(position).isSelected();
				if (!isProVersion
						&& targetSelected
						&& selectedCount >= SqlListenerAppLockSort.COMMON_LIMIT_COUNT) {
					ToastMaster.makeText(mContext,
							R.string.applock_cannot_add_more,
							Toast.LENGTH_SHORT);
					return;
				}
				if (targetSelected) {
					selectedCount++;
				} else {
					selectedCount--;
				}
				mInstalledList.get(position).setSelected(targetSelected);
				for (InstalledAppBean installedAppBean : mInstalledList) {
					if (installedAppBean.getResolveInfo().activityInfo.packageName
							.equals(mInstalledList.get(position).getResolveInfo().activityInfo.packageName)) {
						installedAppBean.setSelected(targetSelected);
					}
				}
				notifyDataSetChanged();
			}
		});
		return convertView;
	}

	public int getTotalRowNumber() {
		int count = mInstalledList.size();
		return count / column + (count % column == 0 ? 0 : 1);
	}

	private int getCurrentRowNumber(int position) {
		return position / column + (position % column == 0 ? 0 : 1);
	}

	private class ViewHolder {
		View mTouchView;
		ImageView checkbox;
		ImageView imageView;
		TextView name;
	}

	public void setIsGuide(boolean isGuide, int hintHeight) {
		mHintHeight = hintHeight > 0 ? hintHeight : 250;
		this.isGuide = isGuide;
	}

}
