package com.cyou.cma.clockscreen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.adapter.PwdCategoryAdapter;
import com.cyou.cma.clockscreen.adapter.PwdCategoryAdapter.ItemClickListener;
import com.cyou.cma.clockscreen.password.PasswordHelper;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.LockPatternUtils;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.widget.NoScrollListView;
import com.cyou.cma.clockscreen.widget.material.LImageButton;
import com.cyou.cma.clockscreen.widget.material.PreferenceCheckBox.OnLPreferenceSwitchListener;
import com.umeng.analytics.MobclickAgent;

public class PwdSettingActivity extends BaseActivity implements
		OnLPreferenceSwitchListener, OnClickListener {
	private LImageButton leftBtn;
	private LImageButton rightBtn;
	private TextView titleText;

	private NoScrollListView mCategoryListView;

	private PwdCategoryAdapter mCategoryAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		SystemUIStatusUtil.onCreate(this, this.getWindow());
		setContentView(R.layout.activity_pwd_settings);
		initView();
		// prefs = PreferenceManager.getDefaultSharedPreferences(this);
		// prefs.registerOnSharedPreferenceChangeListener(this); // 注册
		// prefs.edit().putInt(Util.UNLOCK_TYPE, Util.SLIDE_TYPE);
		// prefs.edit().commit();
		if (SystemUIStatusUtil.isStatusBarTransparency(mContext)) {
			findViewById(R.id.root).setPadding(0,
					ImageUtil.getStatusBarHeight(mContext), 0, 0);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// prefs.unregisterOnSharedPreferenceChangeListener(this
		// );
	}

	private void initView() {
		mCategoryAdapter = new PwdCategoryAdapter(mContext,
				new ItemClickListener() {

					@Override
					public void onItemClicked(int position) {
						if (position == 0) {
							PasswordHelper.setUnlockType(
									mCategoryAdapter.getType(position),
									PwdSettingActivity.this);
							mCategoryAdapter.notifyDataSetChanged();
						} else if (position == 1) {
							Intent intent = new Intent(PwdSettingActivity.this,
									ChooseLockPassword.class);
							intent.putExtra("type",
									LockPatternUtils.LOCKSCREEN_TYPE);
							startActivity(intent);
						} else if (position == 2) {
							Intent intent = new Intent(PwdSettingActivity.this,
									ChooseLockPattern.class);
							intent.putExtra("type",
									LockPatternUtils.LOCKSCREEN_TYPE);
							startActivity(intent);

						}
					}
				});
		leftBtn = (LImageButton) findViewById(R.id.btn_left);
		rightBtn = (LImageButton) findViewById(R.id.btn_right);
		titleText = (TextView) findViewById(R.id.tv_title);
		rightBtn.setVisibility(View.INVISIBLE);
		titleText.setText(R.string.mimasuo);
		leftBtn.setOnClickListener(this);

		mCategoryListView = (NoScrollListView) findViewById(R.id.pwdsettings_category);
		mCategoryListView.setAdapter(mCategoryAdapter);
		// mCategoryListView.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view, int
		// position, long id) {
		// if (position == 0) {
		// PasswordHelper.setUnlockType(mCategoryAdapter.getType(position),
		// PwdSettingActivity.this);
		// mCategoryAdapter.notifyDataSetChanged();
		// } else if (position == 1) {
		// Intent intent = new Intent(PwdSettingActivity.this,
		// ChooseLockPassword.class);
		// intent.putExtra("type", LockPatternUtils.LOCKSCREEN_TYPE);
		// startActivity(intent);
		// } else if (position == 2) {
		// Intent intent = new Intent(PwdSettingActivity.this,
		// ChooseLockPattern.class);
		// intent.putExtra("type", LockPatternUtils.LOCKSCREEN_TYPE);
		// startActivity(intent);
		//
		// }
		// }
		// });
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		mCategoryAdapter.notifyDataSetChanged();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onLCheckedChanged(View v, boolean isChecked) {
	}

}
