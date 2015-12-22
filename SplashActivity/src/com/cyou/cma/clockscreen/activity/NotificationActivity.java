package com.cyou.cma.clockscreen.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.event.CheckEvent;
import com.cyou.cma.clockscreen.event.LoadingEvent;
import com.cyou.cma.clockscreen.notification.KeyguardSetting;
import com.cyou.cma.clockscreen.notification.NotificationUtil;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.DontPressedWithParentCheckBox;
import com.cyou.cma.clockscreen.widget.FontedTextView;
import com.cyou.cma.clockscreen.widget.InstalledLayout4Notification;
import com.cyou.cma.clockscreen.widget.material.LImageButton;
import com.cyou.cma.clockscreen.widget.material.LLinearLayout;

import de.greenrobot.event.EventBus;

/**
 * 
 * @author jiangbin
 * 
 */
public class NotificationActivity extends BaseActivityEx {
	private InstalledLayout4Notification mInstalledLayout4Notification;
	private LLinearLayout mLLinearLayout;
	private DontPressedWithParentCheckBox mCheckBoxToggle;
	private CheckBox mCheBoxSelect;
	private TextView mTextViewNotification;
	public static final String NOTIFICATION_CHECK = "NOTIFICATION_CHECK";
	private LImageButton btn_left;
	private FontedTextView mTextView;

	@Override
	public void initView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_notification);

		mLLinearLayout = (LLinearLayout) findViewById(R.id.l_linearlayout);
		mInstalledLayout4Notification = (InstalledLayout4Notification) findViewById(R.id.notification_install_layout);
		mCheckBoxToggle = (DontPressedWithParentCheckBox) findViewById(R.id.toggle);

		mCheckBoxToggle.setEnabled(false);
		btn_left = (LImageButton) findViewById(R.id.btn_left);
		btn_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mTextView = (FontedTextView) findViewById(R.id.tv_title);
		mTextView.setText(R.string.notification_title);
		mCheckBoxToggle
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							Util.putPreferenceBoolean(mContext,
									NOTIFICATION_CHECK, true);
						} else {
							Util.putPreferenceBoolean(mContext,
									NOTIFICATION_CHECK, false);
						}
					}
				});

		mCheBoxSelect = (CheckBox) findViewById(R.id.select_all);
		mCheBoxSelect.setEnabled(false);
		mCheBoxSelect.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// int action
				if (!KeyguardSetting.isAccessibilityEnable(mContext)) {

					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						KeyguardSetting.jumpToAccess(mContext);
						hasBeenSecurity = true;
						break;

					}

					return true;
				}
				return false;
			}
		});
		mCheBoxSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					if (!Util.getPreferenceBoolean(mContext,
							NOTIFICATION_CHECK, false)) {
						Util.putPreferenceBoolean(mContext, NOTIFICATION_CHECK,
								true);
						mCheckBoxToggle.setChecked(true);
					}
					if (!isFromTouch) {

						mInstalledLayout4Notification.selectALl();
						mTextViewNotification.setText("" + allCount);

					}
				} else {
					if (!isFromTouch) {
						mInstalledLayout4Notification.deselectALl();
						mTextViewNotification.setText("" + 0);
					}
				}
				isFromTouch = false;
			}
		});
		mTextViewNotification = (TextView) findViewById(R.id.app_selected);
		mTextViewNotification.setText(""
				+ NotificationUtil.getNotificationPackages(mContext).size());
		mLLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (KeyguardSetting.isAccessibilityEnable(mContext)) {
					mCheckBoxToggle.performClick();
				} else {
					hasBeenSecurity = true;
					KeyguardSetting.jumpToAccess(mContext);

				}
			}
		});
		EventBus.getDefault().register(this);
	}

	Handler mHandler = new Handler();
	public static boolean hasBeenSecurity;
	boolean isFromTouch = false;
	int allCount = 0;

	// private

	public void onEvent(LoadingEvent event) {
		mCheBoxSelect.setEnabled(true);
		if (event.type == LoadingEvent.TYPE2) {
			isFromTouch = true;
		} else {
			isFromTouch = false;
			allCount = event.allCount;
		}
		mTextViewNotification.setText(event.count + "");
		mCheBoxSelect.setChecked(event.allSelected);
		mCheBoxSelect.postDelayed(r, 300);

	}

	public void onEvent(CheckEvent checkEvent) {
		Util.putPreferenceBoolean(mContext, NOTIFICATION_CHECK,
				true);
		mCheckBoxToggle.setChecked(true);
	}

	Runnable r = new Runnable() {

		@Override
		public void run() {
			isFromTouch = false;
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		hasBeenSecurity = false;
		EventBus.getDefault().unregister(this);
	}

	// boolean shouldall = true;

	@Override
	protected void onResume() {
		super.onResume();
		if (hasBeenSecurity) {
			if (KeyguardSetting.isAccessibilityEnable(this)) {
				Util.putPreferenceBoolean(mContext, NOTIFICATION_CHECK, true);
				mCheckBoxToggle.setChecked(true);
			} else {
				mCheckBoxToggle.setChecked(false);
			}
		} else {
			if (KeyguardSetting.isAccessibilityEnable(this)
					&& Util.getPreferenceBoolean(mContext, NOTIFICATION_CHECK,
							false)) {
				// TODO 修改逻辑
				mCheckBoxToggle.setChecked(true);
			} else {
				mCheckBoxToggle.setChecked(false);
			}
		}
	}

	@Override
	public int getRootViewRes() {
		return R.id.root;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mInstalledLayout4Notification.saveData();

	}

	private static List<String> mHomeList = new ArrayList<String>();

}
