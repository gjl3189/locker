package com.cyou.cma.clockscreen.widget.material;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cynad.cma.locker.R;

public class PreferenceNormal extends LFrameLayout {
	private final String TAG = "PreferenceNormal";
	private Context mContext;
	private TextView mTitleTextView;
	private TextView mSummaryTextView;
	private View mDividerView;
	private ImageView mNewImageView;

	public PreferenceNormal(Context context, AttributeSet attrs) {
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
		mDividerView
				.setVisibility(a.getBoolean(
						R.styleable.PreferenceStyle_preference_show_divider,
						true) ? View.VISIBLE : View.GONE);
		a.recycle();
	}

	private void initView() {
		View.inflate(mContext, R.layout.preference_normal, this);
		mTitleTextView = (TextView) findViewById(R.id.preference_title);
		mSummaryTextView = (TextView) findViewById(R.id.preference_summary);
		mDividerView = findViewById(R.id.preference_divider);
		mNewImageView = (ImageView) findViewById(R.id.preference_new);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
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

	public void setNewStatus(boolean isNew) {
		if (mNewImageView != null) {
			mNewImageView.setVisibility(isNew ? View.VISIBLE : View.GONE);
		}
	}

}
