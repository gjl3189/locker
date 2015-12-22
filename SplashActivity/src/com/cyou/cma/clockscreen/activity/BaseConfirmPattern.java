/*
 * Copyright (C) 2008 The Android Open Source Project
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

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.adjust.sdk.Adjust;
import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.password.PasswordView;
import com.cyou.cma.clockscreen.password.SecureAccess;
import com.cyou.cma.clockscreen.password.widget.PatternView;
import com.cyou.cma.clockscreen.password.widget.PatternView.Cell;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.LockPatternUtils;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.widget.LinearLayoutWithDefaultTouchRecepient;
import com.cyou.cma.clockscreen.widget.material.LImageButton;

/**
 * Launch this when you want the user to confirm their lock pattern.
 * Sets an activity result of {@link Activity#RESULT_OK} when the user
 * successfully confirmed their pattern.
 */
public abstract class BaseConfirmPattern extends Activity implements PasswordView<List<PatternView.Cell>>,
        SecureAccess {

    private static final int WRONG_PATTERN_CLEAR_TIMEOUT_MS = 2000;

    private static final String KEY_NUM_WRONG_ATTEMPTS = "num_wrong_attempts";

    private PatternView mLockPatternView;
    protected LockPatternUtils mLockPatternUtils;
    private int mNumWrongConfirmAttempts;

    private TextView mHeaderTextView;

    // caller-supplied text for various prompts
    protected CharSequence mHeaderText;
    protected CharSequence mHeaderWrongText;
//    private CharSequence mFooterWrongText;
    private LImageButton mBackButton;
    protected TextView mTitleTextView;

    private enum Stage {
        NeedToUnlock,
        NeedToUnlockWrong,
        LockedOut
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SystemUIStatusUtil.onCreate(this, this.getWindow());
        mLockPatternUtils = new LockPatternUtils(this.getApplicationContext());

        setContentView(R.layout.confirm_lock_pattern);

        mHeaderTextView = (TextView) findViewById(R.id.headerText);
        mLockPatternView = (PatternView) findViewById(R.id.lockPattern);

        mBackButton = (LImageButton) findViewById(R.id.btn_left);
        mBackButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleTextView = (TextView) findViewById(R.id.tv_title);
        mTitleTextView.setText(R.string.mimasuo);
        final LinearLayoutWithDefaultTouchRecepient topLayout = (LinearLayoutWithDefaultTouchRecepient) findViewById(
                R.id.topLayout);
        topLayout.setDefaultTouchRecepient(mLockPatternView);

// mLockPatternView.setTactileFeedbackEnabled(mLockPatternUtils.isTactileFeedbackEnabled());
        mLockPatternView.setOnPatternListener(mConfirmExistingLockPatternListener);
        updateStage(Stage.NeedToUnlock);

        if (savedInstanceState != null) {
            mNumWrongConfirmAttempts = savedInstanceState.getInt(KEY_NUM_WRONG_ATTEMPTS);
        }

        if (SystemUIStatusUtil.isStatusBarTransparency(this)) {
            findViewById(R.id.topLayout).setPadding(0, ImageUtil.getStatusBarHeight(this), 0, 0);
        }
        setSecureAccess(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // deliberately not calling super since we are managing this in full
        outState.putInt(KEY_NUM_WRONG_ATTEMPTS, mNumWrongConfirmAttempts);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Adjust.onPause();

// if (mCountdownTimer != null) {
// mCountdownTimer.cancel();
// }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Adjust.onResume(this);
        // if the user is currently locked out, enforce it.
        long deadline = mLockPatternUtils.getLockoutAttemptDeadline();
        if (deadline != 0) {
// handleAttemptLockout(deadline);
        }
    }

    private void updateStage(Stage stage) {

        switch (stage) {
            case NeedToUnlock:
                if (mHeaderText != null) {
                    mHeaderTextView.setText(mHeaderText);
                } else {
                    mHeaderTextView.setText(R.string.lockpattern_need_to_unlock);
                }

                mLockPatternView.setEnabled(true);
                mLockPatternView.enableInput();
                break;
            case NeedToUnlockWrong:
                if (mHeaderWrongText != null) {
                    mHeaderTextView.setText(mHeaderWrongText);
                } else {
                    mHeaderTextView.setText(R.string.lockpattern_need_to_unlock_wrong);
                }

                mLockPatternView.setDisplayMode(PatternView.DisplayMode.Wrong);
                mLockPatternView.setEnabled(true);
                mLockPatternView.enableInput();
                break;
            case LockedOut:
                mLockPatternView.clearPattern();
                // enabled = false means: disable input, and have the
                // appearance of being disabled.
                mLockPatternView.setEnabled(false); // appearance of being disabled
                break;
        }
    }

    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };

    // clear the wrong pattern unless they have started a new one
    // already
    private void postClearPatternRunnable() {
        mLockPatternView.removeCallbacks(mClearPatternRunnable);
        mLockPatternView.postDelayed(mClearPatternRunnable, WRONG_PATTERN_CLEAR_TIMEOUT_MS);
    }

    /**
     * The pattern listener that responds according to a user confirming
     * an existing lock pattern.
     */
    private PatternView.OnPatternListener mConfirmExistingLockPatternListener = new PatternView.OnPatternListener() {

        public void onPatternStart() {
            mLockPatternView.removeCallbacks(mClearPatternRunnable);
        }

        public void onPatternCleared() {
            mLockPatternView.removeCallbacks(mClearPatternRunnable);
        }

        public void onPatternCellAdded(List<Cell> pattern) {

        }

        public void onPatternDetected(List<PatternView.Cell> pattern) {
            boolean pass = checkPassword(pattern);
            if (pass)
            {
                mSecureAccess.onSecureSuccess();
            }
            // if (mLockPatternUtils.checkPattern(pattern, LockPatternUtils.LOCKSCREEN_TYPE)) {
// startActivity(new Intent(BaseConfirmPattern.this, PwdSettingActivity.class));
// finish();
// }
            else {
                if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL &&
                        ++mNumWrongConfirmAttempts >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
// long deadline = mLockPatternUtils.setLockoutAttemptDeadline();
// handleAttemptLockout(deadline);
                } else {
                    updateStage(Stage.NeedToUnlockWrong);
                    postClearPatternRunnable();
                }
            }
        }
    };
    private SecureAccess mSecureAccess;

    public void setSecureAccess(SecureAccess secureAccess) {
        this.mSecureAccess = secureAccess;
    };
}
