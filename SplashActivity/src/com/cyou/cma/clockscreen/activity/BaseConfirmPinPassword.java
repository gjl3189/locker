/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyou.cma.clockscreen.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.password.PasswordView;
import com.cyou.cma.clockscreen.password.SecureAccess;
import com.cyou.cma.clockscreen.password.widget.PasswordEntryKeyboardHelper;
import com.cyou.cma.clockscreen.password.widget.PasswordEntryKeyboardView;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.LockPatternUtils;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.widget.material.LImageButton;

public abstract class BaseConfirmPinPassword extends BaseActivity implements
		TextWatcher, PasswordView<String>, SecureAccess {
	private static final long ERROR_MESSAGE_TIMEOUT = 3000;
	protected EditText mPasswordEntry;
	protected LockPatternUtils mLockPatternUtils;
	private TextView mHeaderText;
	private Handler mHandler = new Handler();
	private PasswordEntryKeyboardHelper mKeyboardHelper;
	private PasswordEntryKeyboardView mKeyboardView;
	protected TextView mTitleTextView;
	private LImageButton mBackButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SystemUIStatusUtil.onCreate(this, this.getWindow());
		mLockPatternUtils = new LockPatternUtils(this);
		initViews();
		if (SystemUIStatusUtil.isStatusBarTransparency(mContext)) {
			findViewById(R.id.root).setPadding(0,
					ImageUtil.getStatusBarHeight(mContext), 0, 0);
		}
		setSecureAccess(this);
	}

	private void initViews() {
		setContentView(R.layout.confirm_lock_password);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

		mTitleTextView = (TextView) findViewById(R.id.tv_title);
		mBackButton = (LImageButton) findViewById(R.id.btn_left);
		// Edit by jiangbin
		// mTitleTextView.setText(R.string.yanzhengmima);
		mTitleTextView.setText(R.string.mimasuo);
		// end by jiangbin
		mBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mPasswordEntry = (EditText) findViewById(R.id.password_entry);
		mPasswordEntry.addTextChangedListener(this);
		mKeyboardView = (PasswordEntryKeyboardView) findViewById(R.id.keyboard);
		mHeaderText = (TextView) findViewById(R.id.headerText);
		final boolean isAlpha = false;
		mHeaderText
				.setText(isAlpha ? R.string.lockpassword_confirm_your_password_header
						: R.string.lockpassword_confirm_your_pin_header);
		mKeyboardHelper = new PasswordEntryKeyboardHelper(this, mKeyboardView,
				mPasswordEntry);
		mKeyboardHelper
				.setKeyboardMode(isAlpha ? PasswordEntryKeyboardHelper.KEYBOARD_MODE_ALPHA
						: PasswordEntryKeyboardHelper.KEYBOARD_MODE_NUMERIC);
		mKeyboardView.requestFocus();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mKeyboardView.requestFocus();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mKeyboardView.requestFocus();
	}

	private void handleNext() {
		final String pin = mPasswordEntry.getText().toString();
		// if(mKeyboardView.checkPassword(pin)){
		//
		// }
		if (checkPassword(pin)) {
			if (this.mSecureAccess != null) {
				mSecureAccess.onSecureSuccess();
			}
		} else {
			showError(R.string.lockpassword_confirm_passwords_dont_incorrect);
		}
		// if (mLockPatternUtils.checkPassword(pin,
		// LockPatternUtils.LOCKSCREEN_TYPE)) {
		// startActivity(new Intent(BaseConfirmPinPassword.this,
		// PwdSettingActivity.class));
		// finish();
		// } else {
		// showError(R.string.lockpassword_confirm_passwords_dont_incorrect);
		// }
	}

	private void showError(int msg) {
		mHeaderText.setText(msg);
		mPasswordEntry.setText(null);
		mHandler.postDelayed(new Runnable() {
			public void run() {
				mHeaderText
						.setText(R.string.lockpassword_confirm_your_password_header);
			}
		}, ERROR_MESSAGE_TIMEOUT);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		if (s.length() == 4) {
			handleNext();
		}
	}

	@Override
	public void onShow() {

	}

	@Override
	public void onHide() {

	}

	protected SecureAccess mSecureAccess;

	@Override
	public void setSecureAccess(SecureAccess secureAccess) {
		this.mSecureAccess = secureAccess;
	}

}
