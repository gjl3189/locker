package com.cyou.cma.clockscreen.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.password.PasswordHelper;

public class PwdCategoryAdapter extends BaseAdapter {

	private Context mContext;
	private int[] typeList = new int[] { PasswordHelper.SLIDE_TYPE,
			PasswordHelper.PASSWORD_TYPE, PasswordHelper.PATTERN_TYPE };
	private int leftPadding = 20;

	private int mCurrentType = PasswordHelper.SLIDE_TYPE;
	private ItemClickListener mListener;

	public PwdCategoryAdapter(Context context, ItemClickListener listener) {
		this.mContext = context;
		this.mListener = listener;
		mCurrentType = PasswordHelper.getUnlockType(context);
		leftPadding = context.getResources().getDimensionPixelSize(
				R.dimen.dialog_list_adapter_left_padding);
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
		mCurrentType = PasswordHelper.getUnlockType(mContext);
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
			// viewHolder.name.setGravity(Gravity.LEFT);
			// viewHolder.name.setTextPadding(leftPadding, 0, 0, 0);
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

	public static String getNameByType(Context context, int type) {
		switch (type) {
		case PasswordHelper.SLIDE_TYPE:
			return context.getString(R.string.wumima);
		case PasswordHelper.PASSWORD_TYPE:
			return context.getString(R.string.shuzimima);
		case PasswordHelper.PATTERN_TYPE:
			return context.getString(R.string.tuxingmima);
		default:
			return context.getString(R.string.wumima);
		}
	}

	public interface ItemClickListener {
		public void onItemClicked(int position);
	}

}
