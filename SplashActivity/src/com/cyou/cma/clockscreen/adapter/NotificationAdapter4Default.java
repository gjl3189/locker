package com.cyou.cma.clockscreen.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.bean.AppNotification;
import com.cyou.cma.clockscreen.util.Util;

import java.util.List;

public class NotificationAdapter4Default extends
		BaseGroupAdapater<AppNotification> {

	private Context mContext;
	private int height = 116;

	public NotificationAdapter4Default(Context context) {
		this.mContext = context;
		height = (int) ((116 / 1280f) * Util.getScreenHeight(mContext));
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.notification_default,
					null);
			viewHolder.iconImageView = (ImageView) convertView
					.findViewById(R.id.notification_icon);
			viewHolder.contentTipTextView = (TextView) convertView
					.findViewById(R.id.notification_content_tip);
			viewHolder.titleTextView = (TextView) convertView
					.findViewById(R.id.notification_title);
			viewHolder.contentTextView = (TextView) convertView
					.findViewById(R.id.notification_content);
			viewHolder.timeTextView = (TextView) convertView
					.findViewById(R.id.notification_time);
			// viewHolder.iconImageView.getLayoutParams().height = height;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		AppNotification notification = getItem(position);
		if (notification.type == 1) {
			viewHolder.contentTipTextView.setVisibility(View.VISIBLE);
			viewHolder.contentTextView.setVisibility(View.GONE);
			viewHolder.iconImageView.setVisibility(View.GONE);
			viewHolder.timeTextView.setVisibility(View.GONE);
			viewHolder.titleTextView.setText(R.string.notification_tip);
			viewHolder.contentTipTextView.setText(R.string.notification_tip_content);
			
		} else {
			viewHolder.contentTipTextView.setVisibility(View.GONE);
			viewHolder.contentTextView.setVisibility(View.VISIBLE);
			viewHolder.iconImageView.setVisibility(View.VISIBLE);
			viewHolder.timeTextView.setVisibility(View.VISIBLE);
			viewHolder.iconImageView.setImageBitmap(getItem(position).mLogo);
			viewHolder.titleTextView.setText(notification.mTitle);
			viewHolder.contentTextView.setText(notification.mContent);
			viewHolder.timeTextView.setText(notification.mTime);
		}
		return convertView;
	}

	private class ViewHolder {
		TextView timeTextView;
		TextView contentTextView;
		TextView titleTextView;
		TextView contentTipTextView;
		ImageView iconImageView;
	}

}
