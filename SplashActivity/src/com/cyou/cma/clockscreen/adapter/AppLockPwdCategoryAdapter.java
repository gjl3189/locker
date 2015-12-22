package com.cyou.cma.clockscreen.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.adapter.PwdCategoryAdapter.ItemClickListener;
import com.cyou.cma.clockscreen.applock.AppLockHelper;

public class AppLockPwdCategoryAdapter extends BaseAdapter {

	private Context mContext;
	private int[] typeList = new int[] { AppLockHelper.PIN_APP_LOCKER,
			AppLockHelper.PATTERN_APP_LOCKER };

	private int mCurrentType = -1;
	private ItemClickListener mListener;

	public AppLockPwdCategoryAdapter(Context context, ItemClickListener listener) {
		this.mContext = context;
		this.mListener = listener;
		mCurrentType = AppLockHelper.getAppLockType(context);
//		if (mCurrentType == AppLockHelper.PIN_APP_LOCKER_DEFAULT) {
//			mCurrentType = AppLockHelper.PIN_APP_LOCKER;
//		}
	}

	@Override
	public int getCount() {
		return typeList.length;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	public int getType(int position) {
		return typeList[position];
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO to jiangbin 获取当前applock密码类型需要改变
		mCurrentType = AppLockHelper.getAppLockType(mContext);
//		if (mCurrentType == AppLockHelper.PIN_APP_LOCKER_DEFAULT) {
//			mCurrentType = AppLockHelper.PIN_APP_LOCKER;
//		}
		// mCurrentType = Util.getPreferenceInt(mContext,
		// Util.SAVE_KEY_CURRENT_PWD_CATEGORY, TYPE_NO);
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		int type = typeList[position];
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.adapter_pwd_category,
					null);
			viewHolder.checkbox = (CheckedTextView) convertView
					.findViewById(R.id.adapter_pwdtype_checkbox);
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.adapter_pwdtype_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.checkbox.setChecked(type == mCurrentType);
		viewHolder.name.setText(getNameByType(mContext, type));
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mListener != null) {
					mListener.onItemClicked(position);
				}
			}
		});
		return convertView;
	}

	private class ViewHolder {
		CheckedTextView checkbox;
		TextView name;
	}

	private String getNameByType(Context context, int type) {
		// TODO to jiangbin 两种密码类型的name是否与原来一样，如果一样，，不变，不一样定义string
		switch (type) {
		case AppLockHelper.PIN_APP_LOCKER:
			return context.getString(R.string.shuzimima);
		case AppLockHelper.PATTERN_APP_LOCKER:
			return context.getString(R.string.tuxingmima);
		}
		return "";
	}

}
