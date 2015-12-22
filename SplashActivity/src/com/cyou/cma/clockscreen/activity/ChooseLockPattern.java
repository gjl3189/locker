/*
 * Copyright (C) 2007 The Android Open Source Project
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adjust.sdk.Adjust;
import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.password.widget.PatternView;
import com.cyou.cma.clockscreen.password.widget.PatternView.Cell;
import com.cyou.cma.clockscreen.password.widget.PatternView.DisplayMode;
import com.cyou.cma.clockscreen.service.MailBoxService;
import com.cyou.cma.clockscreen.service.PasswordBackupService;
import com.cyou.cma.clockscreen.util.ChooseLockSettingsHelper;
import com.cyou.cma.clockscreen.util.HttpUtil;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.Lists;
import com.cyou.cma.clockscreen.util.LockPatternUtils;
import com.cyou.cma.clockscreen.util.MailBoxHelper;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.ToastMaster;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.CustomAlertDialog;
import com.cyou.cma.clockscreen.widget.FontedButton;
import com.cyou.cma.clockscreen.widget.FontedEditText;
import com.cyou.cma.clockscreen.widget.LinearLayoutWithDefaultTouchRecepient;
import com.cyou.cma.clockscreen.widget.material.LImageButton;

/**
 * If the user has a lock pattern set already, makes them confirm the existing
 * one. Then, prompts the user to choose a lock pattern: - prompts for initial
 * pattern - asks for confirmation / restart - saves chosen password when
 * confirmed
 */
public class ChooseLockPattern extends Activity implements View.OnClickListener {
	/**
	 * Used by the choose lock pattern wizard to indicate the wizard is
	 * finished, and each activity in the wizard should finish.
	 * <p>
	 * Previously, each activity in the wizard would finish itself after
	 * starting the next activity. However, this leads to broken 'Back'
	 * behavior. So, now an activity does not finish itself until it gets this
	 * result.
	 */
	static final int RESULT_FINISHED = RESULT_FIRST_USER;

	public static final int CONFIRM_EXISTING_REQUEST = 55;

	// how long after a confirmation message is shown before moving on
	static final int INFORMATION_MSG_TIMEOUT_MS = 3000;

	// how long we wait to clear a wrong pattern
	private static final int WRONG_PATTERN_CLEAR_TIMEOUT_MS = 2000;

	private static final int ID_EMPTY_MESSAGE = -1;

	protected TextView mHeaderText;
	protected PatternView mLockPatternView;
	protected TextView mFooterText;
	private Button mFooterLeftButton;
	private Button mFooterRightButton;
	private LinearLayout mFooterLinearLayout;
	private LImageButton mImageButtonLeft;
	private TextView mTitleTextView;
	protected List<PatternView.Cell> mChosenPattern = null;
	private int type;
	/**
	 * The patten used during the help screen to show how to draw a pattern.
	 */
	private final List<PatternView.Cell> mAnimatePattern = Collections
			.unmodifiableList(Lists.newArrayList(PatternView.Cell.of(0, 0),
					PatternView.Cell.of(0, 1), PatternView.Cell.of(1, 1),
					PatternView.Cell.of(2, 1)));

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CONFIRM_EXISTING_REQUEST:
			if (resultCode != Activity.RESULT_OK) {
				setResult(RESULT_FINISHED);
				finish();
			}
			updateStage(Stage.Introduction);
			break;
		}
	}

	/**
	 * The pattern listener that responds according to a user choosing a new
	 * lock pattern.
	 */
	protected PatternView.OnPatternListener mChooseNewLockPatternListener = new PatternView.OnPatternListener() {

		public void onPatternStart() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
			patternInProgress();
		}

		public void onPatternCleared() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
		}

		public void onPatternDetected(List<PatternView.Cell> pattern) {
			if (mUiStage == Stage.NeedToConfirm
					|| mUiStage == Stage.ConfirmWrong) {
				if (mChosenPattern == null)
					throw new IllegalStateException(
							"null chosen pattern in stage 'need to confirm");
				if (mChosenPattern.equals(pattern)) {
					updateStage(Stage.ChoiceConfirmed);
				} else {
					updateStage(Stage.ConfirmWrong);
				}
			} else if (mUiStage == Stage.Introduction
					|| mUiStage == Stage.ChoiceTooShort) {
				if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) {
					updateStage(Stage.ChoiceTooShort);
				} else {
					mChosenPattern = new ArrayList<PatternView.Cell>(pattern);
					// updateStage(Stage.FirstChoiceValid);
					updateStage(Stage.NeedToConfirm);
				}
			} else {
				throw new IllegalStateException("Unexpected stage " + mUiStage
						+ " when " + "entering the pattern.");
			}
		}

		public void onPatternCellAdded(List<Cell> pattern) {

		}

		private void patternInProgress() {
			mHeaderText.setText(R.string.lockpattern_recording_inprogress);
			mFooterText.setText("");
			mFooterLeftButton.setEnabled(false);
			mFooterRightButton.setEnabled(false);
		}
	};

	/**
	 * The states of the left footer button.
	 */
	enum LeftButtonMode {
		Cancel(R.string.cancel, true),
		// CancelDisabled(R.string.cancel, false),
		// Retry(R.string.lockpattern_retry_button_text, true),
		// RetryDisabled(R.string.lockpattern_retry_button_text, false),
		Gone(ID_EMPTY_MESSAGE, false);

		/**
		 * @param text
		 *            The displayed text for this mode.
		 * @param enabled
		 *            Whether the button should be enabled.
		 */
		LeftButtonMode(int text, boolean enabled) {
			this.text = text;
			this.enabled = enabled;
		}

		final int text;
		final boolean enabled;
	}
	@Override
	protected void onResume() {
		super.onResume();
		Adjust.onResume(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		Adjust.onPause();
	}

	/**
	 * The states of the right button.
	 */
	enum RightButtonMode {
		Retry(R.string.lockpattern_retry_button_text, true), RetryDisable(
				R.string.lockpattern_retry_button_text, false), Ok(R.string.ok,
				true), Gone(ID_EMPTY_MESSAGE, false);

		/**
		 * @param text
		 *            The displayed text for this mode.
		 * @param enabled
		 *            Whether the button should be enabled.
		 */
		RightButtonMode(int text, boolean enabled) {
			this.text = text;
			this.enabled = enabled;
		}

		final int text;
		final boolean enabled;
	}

	/**
	 * Keep track internally of where the user is in choosing a pattern.
	 */
	protected enum Stage {

		// Introduction(
		// R.string.lockpattern_recording_intro_header,
		// LeftButtonMode.Cancel, RightButtonMode.Retry,
		// R.string.lockpattern_recording_intro_footer, true),
		Introduction(R.string.lockpattern_recording_intro_header,
				LeftButtonMode.Cancel, RightButtonMode.Retry, ID_EMPTY_MESSAGE,
				true), HelpScreen(
				R.string.lockpattern_settings_help_how_to_record,
				LeftButtonMode.Gone, RightButtonMode.Gone, ID_EMPTY_MESSAGE,
				false), ChoiceTooShort(
				R.string.lockpattern_recording_incorrect_too_short,
				LeftButtonMode.Cancel, RightButtonMode.Retry, ID_EMPTY_MESSAGE,
				true), NeedToConfirm(R.string.lockpattern_need_to_confirm,
				LeftButtonMode.Cancel, RightButtonMode.Retry, ID_EMPTY_MESSAGE,
				true), ConfirmWrong(R.string.lockpattern_need_to_unlock_wrong,
				LeftButtonMode.Cancel, RightButtonMode.Retry, ID_EMPTY_MESSAGE,
				true), ChoiceConfirmed(
				R.string.lockpattern_pattern_confirmed_header,
				LeftButtonMode.Cancel, RightButtonMode.Ok, ID_EMPTY_MESSAGE,
				false);

		/**
		 * @param headerMessage
		 *            The message displayed at the top.
		 * @param leftMode
		 *            The mode of the left button.
		 * @param rightMode
		 *            The mode of the right button.
		 * @param footerMessage
		 *            The footer message.
		 * @param patternEnabled
		 *            Whether the pattern widget is enabled.
		 */
		Stage(int headerMessage, LeftButtonMode leftMode,
				RightButtonMode rightMode, int footerMessage,
				boolean patternEnabled) {
			this.headerMessage = headerMessage;
			this.leftMode = leftMode;
			this.rightMode = rightMode;
			this.footerMessage = footerMessage;
			this.patternEnabled = patternEnabled;
		}

		final int headerMessage;
		final LeftButtonMode leftMode;
		final RightButtonMode rightMode;
		final int footerMessage;
		final boolean patternEnabled;
	}

	private Stage mUiStage = Stage.Introduction;

	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			mLockPatternView.clearPattern();
		}
	};

	private ChooseLockSettingsHelper mChooseLockSettingsHelper;

	private static final String KEY_UI_STAGE = "uiStage";
	private static final String KEY_PATTERN_CHOICE = "chosenPattern";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SystemUIStatusUtil.onCreate(this, this.getWindow());
		mChooseLockSettingsHelper = new ChooseLockSettingsHelper(this);

		setupViews();
		type = getIntent().getIntExtra("type", 0);
		final LinearLayoutWithDefaultTouchRecepient topLayout = (LinearLayoutWithDefaultTouchRecepient) findViewById(R.id.topLayout);
		topLayout.setDefaultTouchRecepient(mLockPatternView);

		if (savedInstanceState == null) {
			updateStage(Stage.Introduction);
		} else {
			// restore from previous state
			final String patternString = savedInstanceState
					.getString(KEY_PATTERN_CHOICE);
			if (patternString != null) {
				mChosenPattern = LockPatternUtils
						.stringToPattern(patternString);
			}
			type = savedInstanceState.getInt("type");
			updateStage(Stage.values()[savedInstanceState.getInt(KEY_UI_STAGE)]);
		}
		if (SystemUIStatusUtil.isStatusBarTransparency(this)) {
			findViewById(R.id.topLayout).setPadding(0,
					ImageUtil.getStatusBarHeight(this), 0, 0);
		}
	}

	/**
	 * Keep all "find view" related stuff confined to this function since in
	 * case someone needs to subclass and customize.
	 */
	protected void setupViews() {
		setContentView(R.layout.choose_lock_pattern);

		mHeaderText = (TextView) findViewById(R.id.headerText);

		mLockPatternView = (PatternView) findViewById(R.id.lockPattern);
		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
		mLockPatternView.setTactileFeedbackEnabled(true);

		mFooterText = (TextView) findViewById(R.id.footerText);

		mFooterLeftButton = (Button) findViewById(R.id.footerLeftButton);
		mFooterRightButton = (Button) findViewById(R.id.footerRightButton);
		mFooterLinearLayout = (LinearLayout) findViewById(R.id.linearlayout_bottom);
		mFooterLeftButton.setOnClickListener(this);
		mFooterRightButton.setOnClickListener(this);
		mImageButtonLeft = (LImageButton) findViewById(R.id.btn_left);
		// mImageButtonLeft.setVisibility(View.INVISIBLE);
		mImageButtonLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTitleTextView = (TextView) findViewById(R.id.tv_title);
		// Edit by jiangbin
		// mTitleTextView.setText(R.string.tuxingmima);
		mTitleTextView.setText(R.string.mimasuo);
		// end edit
	}

	public void onClick(View v) {
		if (v == mFooterLeftButton) {
			if (mUiStage.leftMode == LeftButtonMode.Cancel) {
				setResult(RESULT_FINISHED);
				finish();
			} else {
				throw new IllegalStateException(
						"left footer button pressed, but stage of " + mUiStage
								+ " doesn't make sense");
			}
		} else if (v == mFooterRightButton) {
			if (mUiStage.rightMode == RightButtonMode.Retry) {
				mChosenPattern = null;
				mLockPatternView.clearPattern();
				updateStage(Stage.Introduction);
			} else if (mUiStage.rightMode == RightButtonMode.Ok) {
				mLockPatternView.clearPattern();
				mLockPatternView.setDisplayMode(DisplayMode.Correct);
				updateStage(Stage.Introduction);
				saveChosenPatternAndFinish();
			}
		}
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	// if (mUiStage == Stage.HelpScreen) {
	// updateStage(Stage.Introduction);
	// return true;
	// }
	// }
	// if (keyCode == KeyEvent.KEYCODE_MENU && mUiStage == Stage.Introduction) {
	// updateStage(Stage.HelpScreen);
	// return true;
	// }
	//
	// return super.onKeyDown(keyCode, event);
	// }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(KEY_UI_STAGE, mUiStage.ordinal());
		outState.putInt("type", type);
		if (mChosenPattern != null) {
			outState.putString(KEY_PATTERN_CHOICE,
					LockPatternUtils.patternToString(mChosenPattern));
		}
	}

	/**
	 * Updates the messages and buttons appropriate to what stage the user is at
	 * in choosing a view. This doesn't handle clearing out the pattern; the
	 * pattern is expected to be in the right state.
	 * 
	 * @param stage
	 */
	protected void updateStage(Stage stage) {

		mUiStage = stage;

		// header text, footer text, visibility and
		// enabled state all known from the stage
		if (stage == Stage.ChoiceTooShort) {
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			mHeaderText.startAnimation(shake);
			mHeaderText.setText(getResources().getString(stage.headerMessage,
					LockPatternUtils.MIN_LOCK_PATTERN_SIZE));
		} else {
			mHeaderText.setText(stage.headerMessage);
		}
		if (stage.footerMessage == ID_EMPTY_MESSAGE) {
			mFooterText.setText("");
		} else {
			mFooterText.setText(stage.footerMessage);
		}

		if (stage.leftMode == LeftButtonMode.Gone) {
			mFooterLeftButton.setVisibility(View.GONE);
			mFooterLinearLayout.setVisibility(View.GONE);
		} else {
			mFooterLinearLayout.setVisibility(View.VISIBLE);
			mFooterLeftButton.setVisibility(View.VISIBLE);
			mFooterLeftButton.setText(stage.leftMode.text);
			mFooterLeftButton.setEnabled(stage.leftMode.enabled);
		}

		if (stage.rightMode == RightButtonMode.Gone) {
			mFooterRightButton.setVisibility(View.GONE);
			mFooterLinearLayout.setVisibility(View.GONE);
		} else {
			mFooterLinearLayout.setVisibility(View.VISIBLE);
			mFooterRightButton.setVisibility(View.VISIBLE);
			mFooterRightButton.setText(stage.rightMode.text);
			mFooterRightButton.setEnabled(stage.rightMode.enabled);
		}
		// same for whether the patten is enabled
		if (stage.patternEnabled) {
			mLockPatternView.enableInput();
		} else {
			mLockPatternView.disableInput();
		}

		// the rest of the stuff varies enough that it is easier just to handle
		// on a case by case basis.
		mLockPatternView.setDisplayMode(DisplayMode.Correct);

		switch (mUiStage) {
		case Introduction:
			mLockPatternView.clearPattern();
			break;
		case HelpScreen:
			mLockPatternView.setPattern(DisplayMode.Animate, mAnimatePattern);
			break;
		case ChoiceTooShort:
			mLockPatternView.setDisplayMode(DisplayMode.Wrong);
			postClearPatternRunnable();
			break;

		case NeedToConfirm:
			mLockPatternView.clearPattern();
			break;
		case ConfirmWrong:
			mLockPatternView.setDisplayMode(DisplayMode.Wrong);
			postClearPatternRunnable();
			break;
		case ChoiceConfirmed:
			saveChosenPatternOnly();
			if (type == LockPatternUtils.LOCKSCREEN_TYPE) {
				showPasswordBackup();
			} else {
				finish();
			}
			break;
		}
	}

	// clear the wrong pattern unless they have started a new one
	// already
	private void postClearPatternRunnable() {
		mLockPatternView.removeCallbacks(mClearPatternRunnable);
		mLockPatternView.postDelayed(mClearPatternRunnable,
				WRONG_PATTERN_CLEAR_TIMEOUT_MS);
	}

	private void saveChosenPatternAndFinish() {
		LockPatternUtils utils = mChooseLockSettingsHelper.utils();
		// final boolean lockVirgin = !utils.isPatternEverChosen();

		utils.saveLockPattern(mChosenPattern, type);

		// setResult(RESULT_FINISHED);
		finish();
	}

	private void saveChosenPatternOnly() {
		LockPatternUtils utils = mChooseLockSettingsHelper.utils();

		utils.saveLockPattern(mChosenPattern, type);

		String password = "";
		for (Cell cell : mChosenPattern) {
			password += cell.tag;
		}
		if (type == LockPatternUtils.LOCKSCREEN_TYPE) {
			Util.putPreferenceString(this, Constants.BBBBBB, password);
		} else {
			Util.putPreferenceString(this, Constants.DDDDDD, password);
		}

		// setResult(RESULT_FINISHED);
		// finish();
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

						if (HttpUtil.isNetworkAvailable(ChooseLockPattern.this)) {
							String mail = fontedEditText.getText().toString();
							// String password =
							// LockPatternUtils.patternToString(mChosenPattern);
							// Util.Logjb("ChooseLockPattern ",
							// "ChooseLockPattern password--->" + password);
							String password = "";
							for (Cell cell : mChosenPattern) {
								password += cell.tag;
							}
							Util.Logjb("ChooseLockPattern ",
									"ChooseLockPattern password--->" + password);
							Intent intent = new Intent(ChooseLockPattern.this,
									PasswordBackupService.class);
							intent.putExtra("p", password);
							intent.putExtra("mail", mail);
							startService(intent);
							Intent intent2 = new Intent(ChooseLockPattern.this, MailBoxService.class);
							intent2.putExtra("mail", mail);
							// startService(intent)
							startService(intent2);
							dialog.dismiss();
						} else {
							ToastMaster.makeText(ChooseLockPattern.this,
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

}
