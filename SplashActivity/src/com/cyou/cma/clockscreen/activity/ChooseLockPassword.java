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

import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.password.widget.PasswordEntryKeyboardHelper;
import com.cyou.cma.clockscreen.password.widget.PasswordEntryKeyboardView;
import com.cyou.cma.clockscreen.service.MailBoxService;
import com.cyou.cma.clockscreen.service.PasswordBackupService;
import com.cyou.cma.clockscreen.util.HttpUtil;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.LockPatternUtils;
import com.cyou.cma.clockscreen.util.MailBoxHelper;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.ToastMaster;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.CustomAlertDialog;
import com.cyou.cma.clockscreen.widget.FontedEditText;
import com.cyou.cma.clockscreen.widget.material.LImageButton;

public class ChooseLockPassword extends BaseActivity implements TextWatcher {
	private static final String KEY_FIRST_PIN = "first_pin";
	private static final String KEY_UI_STAGE = "ui_stage";
	private EditText mPasswordEntry;
	private int mPasswordMinLength = 4;
	private int mPasswordMaxLength = 4;
	private LockPatternUtils mLockPatternUtils;
	private int mRequestedQuality = DevicePolicyManager.PASSWORD_QUALITY_NUMERIC;
	private ChooseLockPassword.Stage mUiStage = Stage.Introduction;
	private TextView mHeaderText;
	private String mFirstPin;
	private KeyboardView mKeyboardView;
	private PasswordEntryKeyboardHelper mKeyboardHelper;
	private static Handler mHandler = new Handler();
	static final int RESULT_FINISHED = RESULT_FIRST_USER;
	private static final long ERROR_MESSAGE_TIMEOUT = 3000;

	private int type;
	private TextView mTitleTextView;
	private LImageButton mBackButton;

	/**
	 * Keep track internally of where the user is in choosing a pattern.
	 */
	protected enum Stage {

		Introduction(R.string.lockpassword_choose_your_pin_header,
				R.string.lockpassword_continue_label),

		NeedToConfirm(R.string.lockpassword_choose_your_password_again,
				R.string.lockpassword_ok_label),

		ConfirmWrong(R.string.lockpassword_confirm_passwords_dont_match,
				R.string.lockpassword_continue_label);

		/**
		 * @param headerMessage
		 *            The message displayed at the top.
		 */
		Stage(int hintInNumeric, int nextButtonText) {
			this.numericHint = hintInNumeric;
			this.buttonText = nextButtonText;
		}

		public final int numericHint;
		public final int buttonText;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SystemUIStatusUtil.onCreate(this, this.getWindow());
		mLockPatternUtils = new LockPatternUtils(this);
		type = getIntent().getIntExtra("type", 0);
		initViews();
		if (savedInstanceState == null) {
			updateStage(Stage.Introduction);
		}
		if (SystemUIStatusUtil.isStatusBarTransparency(mContext)) {
			findViewById(R.id.root).setPadding(0,
					ImageUtil.getStatusBarHeight(mContext), 0, 0);
		}
	}

	private void initViews() {
		setContentView(R.layout.choose_lock_password);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		mBackButton = (LImageButton) findViewById(R.id.btn_left);
		mTitleTextView = (TextView) findViewById(R.id.tv_title);
		// edit by jiangbin
		// mTitleTextView.setText(R.string.shuzimima);
		mTitleTextView.setText(R.string.mimasuo);
		// end
		mBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mKeyboardView = (PasswordEntryKeyboardView) findViewById(R.id.keyboard);
		mPasswordEntry = (EditText) findViewById(R.id.password_entry);
		mPasswordEntry.addTextChangedListener(this);

		mKeyboardHelper = new PasswordEntryKeyboardHelper(this, mKeyboardView,
				mPasswordEntry);
		mKeyboardHelper
				.setKeyboardMode(PasswordEntryKeyboardHelper.KEYBOARD_MODE_NUMERIC);

		mHeaderText = (TextView) findViewById(R.id.headerText);
		mKeyboardView.requestFocus();
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateStage(mUiStage);
		mKeyboardView.requestFocus();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_UI_STAGE, mUiStage.name());
		outState.putString(KEY_FIRST_PIN, mFirstPin);
		outState.putInt("type", type);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		String state = savedInstanceState.getString(KEY_UI_STAGE);
		mFirstPin = savedInstanceState.getString(KEY_FIRST_PIN);
		type = savedInstanceState.getInt("type");
		if (state != null) {
			mUiStage = Stage.valueOf(state);
			updateStage(mUiStage);
		}
	}

	protected void updateStage(Stage stage) {
		mUiStage = stage;
		updateUi();
	}

	/**
	 * Validates PIN and returns a message to display if PIN fails test.
	 * 
	 * @param password
	 *            the raw password the user typed in
	 * @return error message to show to user or null if password is OK
	 */
	private String validatePassword(String password) {
		if (password.length() < mPasswordMinLength) {
			// return getString(mIsAlphaMode ?
			// R.string.lockpassword_password_too_short
			// : R.string.lockpassword_pin_too_short, mPasswordMinLength);
			return getString(R.string.lockpassword_choose_your_pin_header);
		}
		if (password.length() > mPasswordMaxLength) {
			return getString(R.string.lockpassword_pin_too_long,
					mPasswordMaxLength);
		}
		boolean hasAlpha = false;
		boolean hasDigit = false;
		boolean hasSymbol = false;
		for (int i = 0; i < password.length(); i++) {
			char c = password.charAt(i);
			// allow non white space Latin-1 characters only
			if (c <= 32 || c > 127) {
				return getString(R.string.lockpassword_illegal_character);
			}
			if (c >= '0' && c <= '9') {
				hasDigit = true;
			} else if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
				hasAlpha = true;
			} else {
				hasSymbol = true;
			}
		}
		if (DevicePolicyManager.PASSWORD_QUALITY_NUMERIC == mRequestedQuality
				&& (hasAlpha | hasSymbol)) {
			// This shouldn't be possible unless user finds some way to bring up
			// soft keyboard
			return getString(R.string.lockpassword_pin_contains_non_digits);
		} else {
			final boolean alphabetic = DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC == mRequestedQuality;
			final boolean alphanumeric = DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC == mRequestedQuality;
			if ((alphabetic || alphanumeric) && !hasAlpha) {
				return getString(R.string.lockpassword_password_requires_alpha);
			}
			if (alphanumeric && !hasDigit) {
				return getString(R.string.lockpassword_password_requires_digit);
			}

		}
		return null;
	}

	private void showPasswordBackup() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		final FontedEditText fontedEditText = (FontedEditText) layoutInflater
				.inflate(R.layout.edittext, null);
		CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(this);
		builder.setTitle(R.string.settings_password_backup);
		builder.setMessage(R.string.tip_password_backup);
		builder.setPositiveButton(getString(R.string.send_text),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (HttpUtil.isNetworkAvailable(mContext)) {
							String mail = fontedEditText.getText().toString();
							String password = mFirstPin;
							Intent intent = new Intent(mContext,
									PasswordBackupService.class);
							intent.putExtra("p", password);
							intent.putExtra("mail", mail);
							startService(intent);
							Intent intent2 = new Intent(mContext, MailBoxService.class);
							intent2.putExtra("mail", mail);
							// startService(intent)
							mContext.startService(intent2);
							dialog.dismiss();
						} else {
							ToastMaster.makeText(mContext,
									R.string.network_unavailable,
									Toast.LENGTH_SHORT);
						}
					}
				});

		builder.setNegativeButton(getString(R.string.send_not_text),
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setView(fontedEditText);
		final CustomAlertDialog dg = builder.create();
		dg.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				ToastMaster.makeText(getApplicationContext(),
						R.string.password_set_successful, Toast.LENGTH_SHORT);
				finish();

			}
		});

		fontedEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

				boolean isEmail = Util.checkEmail(s.toString());
				if (isEmail) {
					dg.setPositiveButtonEnable(true);
				} else {
					dg.setPositiveButtonEnable(false);
				}
			}
		});
		

		dg.show();
		dg.setPositiveButtonEnable(false);
		fontedEditText.setText(MailBoxHelper.getSavedMailBox(this));

	}

	private void handleNext() {
		final String pin = mPasswordEntry.getText().toString();
		if (TextUtils.isEmpty(pin)) {
			return;
		}
		String errorMsg = null;
		if (mUiStage == Stage.Introduction) {
			errorMsg = validatePassword(pin);
			if (errorMsg == null) {
				mFirstPin = pin;
				updateStage(Stage.NeedToConfirm);
				mPasswordEntry.setText("");
			}
		} else if (mUiStage == Stage.NeedToConfirm) {
			if (mFirstPin.equals(pin)) {
				mLockPatternUtils.clearLock(type);
				if (type == LockPatternUtils.LOCKSCREEN_TYPE) {
					Util.putPreferenceString(mContext, Constants.AAAAAA,
							mFirstPin);
				} else {
					Util.putPreferenceString(mContext, Constants.CCCCCC,
							mFirstPin);
				}
				mLockPatternUtils.saveLockPassword(pin, type);
				if (type == LockPatternUtils.LOCKSCREEN_TYPE) {
					showPasswordBackup();
				} else {
					finish();
				}
				// finish();
			} else {
				updateStage(Stage.ConfirmWrong);
				errorMsg = getString(R.string.lockpassword_confirm_passwords_dont_match);
				// CharSequence tmp = mPasswordEntry.getText();
				// if (tmp != null) {
				// Selection.setSelection((Spannable) tmp, 0, tmp.length());
				// }
				mPasswordEntry.setText("");
			}
		}
		if (errorMsg != null) {
			showError(errorMsg, mUiStage);
		}
	}

	private void showError(String msg, final Stage next) {
		mHeaderText.setText(msg);
		mHandler.postDelayed(new Runnable() {
			public void run() {
				updateStage(next);
			}
		}, ERROR_MESSAGE_TIMEOUT);
	}

	/**
	 * Update the hint based on current Stage and length of password entry
	 */
	private void updateUi() {
		String password = mPasswordEntry.getText().toString();
		final int length = password.length();
		if (mUiStage == Stage.Introduction && length > 0) {
			if (length < mPasswordMinLength) {
			} else {
				String error = validatePassword(password);
				if (error != null) {
					mHeaderText.setText(error);

				} else {
					handleNext();

				}
			}
		} else {
			mHeaderText.setText(mUiStage.numericHint);
		}
	}

	public void afterTextChanged(Editable s) {
		// Changing the text while error displayed resets to NeedToConfirm state
		if (mUiStage == Stage.ConfirmWrong) {
			mUiStage = Stage.NeedToConfirm;
		}
		updateUi();
		if (s.length() == 4 && mUiStage == Stage.NeedToConfirm) {
			handleNext();
		}
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}
}
