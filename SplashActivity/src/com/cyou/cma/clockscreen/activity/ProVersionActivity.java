package com.cyou.cma.clockscreen.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.util.CyKeyHelper;
import com.cyou.cma.clockscreen.util.CyKeyResult;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.ToastMaster;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.material.LButton;
import com.umeng.analytics.MobclickAgent;

public class ProVersionActivity extends BaseActivity implements OnClickListener {
	private View mUpdateView;
	private View mDoneView;
	private EditText mTokenEditText;
	private LButton mTokenButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SystemUIStatusUtil.onCreate(this, this.getWindow());
		setContentView(R.layout.activity_pro_version);
		initView();
		if (SystemUIStatusUtil.isStatusBarTransparency(mContext)) {
			findViewById(R.id.root).setPadding(0,
					ImageUtil.getStatusBarHeight(mContext), 0, 0);
		}
	}

	private void initView() {
		findViewById(R.id.pro_done_ok).setOnClickListener(this);
		findViewById(R.id.btn_left).setOnClickListener(this);
		mUpdateView = findViewById(R.id.pro_update_layout);
		mDoneView = findViewById(R.id.pro_done_layout);
		mTokenButton = (LButton) findViewById(R.id.pro_update_token);
		mTokenButton.setOnClickListener(this);
		mTokenButton.setEnabled(false);
		findViewById(R.id.pro_update_gp).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_title))
				.setText(R.string.settings_pro_title);
		mTokenEditText = (EditText) findViewById(R.id.pro_token);
		mTokenEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence content, int arg1, int arg2,
					int arg3) {
				mTokenButton.setEnabled(!TextUtils.isEmpty(content));
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

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
		boolean isProVersion = Util.isProVersion(mContext);
		mUpdateView.setVisibility(isProVersion ? View.GONE : View.VISIBLE);
		mDoneView.setVisibility(isProVersion ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pro_done_ok:
			finish();
			break;
		case R.id.btn_left:
			finish();
			break;
		case R.id.pro_update_token:
			CyKeyResult result = CyKeyHelper.judgeKey(mTokenEditText.getText()
					.toString());
			if (result.isActive) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mTokenEditText.getWindowToken(), 0);
				Util.putPreferenceBoolean(mContext,
						Util.SAVE_KEY_IS_PRO_VERSION, true);
				onResume();
			} else {
				ToastMaster.makeText(mContext, R.string.pro_token_invalid,
						Toast.LENGTH_SHORT);
			}
			break;
		case R.id.pro_update_gp:
			Uri gpUri = Uri
					.parse("https://play.google.com/store/apps/details?id="
							+ Constants.LOCKER_PRO_PACKAGENAME);
			Intent intentGP = new Intent(Intent.ACTION_VIEW, gpUri);
			intentGP.setClassName("com.android.vending",
					"com.android.vending.AssetBrowserActivity");
			intentGP.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				startActivity(intentGP);
			} catch (Exception e) {
				Intent intentWeb = new Intent(Intent.ACTION_VIEW);
				intentWeb.setData(gpUri);
				intentWeb = Intent.createChooser(intentWeb, null);
				try {
					startActivity(intentWeb);
				} catch (Exception e1) {
					Toast.makeText(mContext, R.string.lock_open_app_faild,
							Toast.LENGTH_SHORT).show();
				}
			}
			break;

		default:
			break;
		}
	}
}
