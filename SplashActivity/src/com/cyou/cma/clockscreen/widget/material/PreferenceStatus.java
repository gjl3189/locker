package com.cyou.cma.clockscreen.widget.material;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cynad.cma.locker.R;

public class PreferenceStatus extends LFrameLayout {
	private Context mContext;
	private ImageView mStatusImageView;
	private TextView mTitleTextView;
	private TextView mSummaryTextView;
	private View mDividerView;

	public PreferenceStatus(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.PreferenceStyle);
		mTitleTextView.setText(a
				.getString(R.styleable.PreferenceStyle_preference_title));
		String summary = a
				.getString(R.styleable.PreferenceStyle_preference_summary);
		mSummaryTextView.setVisibility(TextUtils.isEmpty(summary) ? View.GONE
				: View.VISIBLE);
		mSummaryTextView.setText(summary);
		mStatusImageView.setSelected(a.getBoolean(
				R.styleable.PreferenceStyle_preference_title, false));
		mDividerView
				.setVisibility(a.getBoolean(
						R.styleable.PreferenceStyle_preference_show_divider,
						true) ? View.VISIBLE : View.GONE);
		a.recycle();
	}

	private void initView() {
		View.inflate(mContext, R.layout.preference_status, this);
		mTitleTextView = (TextView) findViewById(R.id.preference_title);
		mSummaryTextView = (TextView) findViewById(R.id.preference_summary);
		mStatusImageView = (ImageView) findViewById(R.id.preference_checkbox);
		mDividerView = findViewById(R.id.preference_divider);
	}

	public void setChecked(boolean checked) {
		mStatusImageView.setSelected(checked);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mStatusImageView.setEnabled(enabled);
		mTitleTextView.setEnabled(enabled);
		mSummaryTextView.setEnabled(enabled);
	}

	public void setSummary(int resId) {
		setSummary(mContext.getString(resId));
	}

	public void setSummary(String summary) {
		mSummaryTextView.setVisibility(TextUtils.isEmpty(summary) ? View.GONE
				: View.VISIBLE);
		mSummaryTextView.setText(summary);
	}

}
