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

package com.cyou.cma.clockscreen.password.widget;

import java.util.List;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.password.PasswordView;
import com.cyou.cma.clockscreen.password.SecureAccess;
import com.cyou.cma.clockscreen.password.widget.PatternView.Cell;
import com.cyou.cma.clockscreen.util.LockPatternUtils;
import com.cyou.cma.clockscreen.widget.LinearLayoutWithDefaultTouchRecepient;

/**
 * This is the screen that shows the 9 circle unlock widget and instructs
 * the user how to unlock their device, or make an emergency call.
 */
public abstract class BasePatternScreen extends LinearLayoutWithDefaultTouchRecepient implements
        PasswordView<List<PatternView.Cell>>
{

    private static final boolean DEBUG = false;
    private static final String TAG = "UnlockScreen";
    private SecureAccess mSecureAccess;
    private int resId;

    // how long before we clear the wrong pattern
    private static final int PATTERN_CLEAR_TIMEOUT_MS = 2000;

    // how long we stay awake after each key beyond MIN_PATTERN_BEFORE_POKE_WAKELOCK
    private static final int UNLOCK_PATTERN_WAKE_INTERVAL_MS = 7000;

    private static final int MIN_PATTERN_BEFORE_POKE_WAKELOCK = 2;

    protected final LockPatternUtils mLockPatternUtils;

    /**
     * whether there is a fallback option available when the pattern is forgotten.
     */
    private boolean mEnableFallback;

    private PatternView mLockPatternView;
    protected TextView mHeardTextView;
    protected String mHeardText;

    /**
     * Keeps track of the last time we poked the wake lock during dispatching
     * of the touch event, initalized to something gauranteed to make us
     * poke it when the user starts drawing the pattern.
     * 
     * @see #dispatchTouchEvent(android.view.MotionEvent)
     */
    private long mLastPokeTime = -UNLOCK_PATTERN_WAKE_INTERVAL_MS;

    /**
     * Useful for clearing out the wrong pattern after a delay
     */
    private Runnable mCancelPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };

    public abstract int getResId();

// public void startAnimator() {
// mLockPatternView.startAnimator();
// }

    @Override
    public void setSecureAccess(SecureAccess secureAccess) {
        this.mSecureAccess = secureAccess;
    }

    /**
     * @param context The context.
     * @param configuration
     * @param lockPatternUtils Used to lookup lock pattern settings.
     * @param updateMonitor Used to lookup state affecting keyguard.
     * @param callback Used to notify the manager when we're done, etc.
     * @param totalFailedAttempts The current number of failed attempts.
     * @param enableFallback True if a backup unlock option is available when the user has forgotten
     *            their pattern (e.g they have a google account so we can show them the account based
     *            backup option).
     */
    public BasePatternScreen(Context context,
            AttributeSet attrs) {
        super(context, attrs);
        mLockPatternUtils = new LockPatternUtils(context);

        LayoutInflater inflater = LayoutInflater.from(context);
//        inflater.inflate(R.layout.keyguard_screen_unlock_portrait, this, true);
        inflater.inflate(getResId(), this, true);
        mLockPatternView = (PatternView) findViewById(R.id.lockPattern);
        mHeardTextView = (TextView) findViewById(R.id.enter_password);

        // make it so unhandled touch events within the unlock screen go to the
        // lock pattern view.
        setDefaultTouchRecepient(mLockPatternView);

        mLockPatternView.setSaveEnabled(false);
        mLockPatternView.setFocusable(false);
        mLockPatternView.setOnPatternListener(new UnlockPatternListener());

        // stealth mode will be the same for the life of this screen
        mLockPatternView.setInStealthMode(false);

        // vibrate mode will be the same for the life of this screen
        mLockPatternView.setTactileFeedbackEnabled(true);

        // assume normal footer mode for now

        setFocusableInTouchMode(true);

        // Required to get Marquee to work.
    }

    public void setEnableFallback(boolean state) {
        if (DEBUG) Log.d(TAG, "setEnableFallback(" + state + ")");
        mEnableFallback = state;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // as long as the user is entering a pattern (i.e sending a touch
        // event that was handled by this screen), keep poking the
        // wake lock so that the screen will stay on.
        final boolean result = super.dispatchTouchEvent(ev);
        if (result &&
                ((SystemClock.elapsedRealtime() - mLastPokeTime)
                > (UNLOCK_PATTERN_WAKE_INTERVAL_MS - 100))) {
            mLastPokeTime = SystemClock.elapsedRealtime();
        }
        return result;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /** {@inheritDoc} */
    public void onKeyboardChange(boolean isKeyboardOpen) {
    }

    /** {@inheritDoc} */
    public boolean needsInput() {
        return false;
    }

    /** {@inheritDoc} */
    public void onPause() {
// if (mCountdownTimer != null) {
// mCountdownTimer.cancel();
// mCountdownTimer = null;
// }
    }

    /** {@inheritDoc} */
    public void onResume() {
        // reset header
// resetStatusInfo();

        // reset lock pattern
        mLockPatternView.enableInput();
        mLockPatternView.setEnabled(true);
        mLockPatternView.clearPattern();

        // show "forgot pattern?" button if we have an alternate authentication method
// mForgotPatternButton.setVisibility(mCallback.doesFallbackUnlockScreenExist()
// ? View.VISIBLE : View.INVISIBLE);

        // if the user is currently locked out, enforce it.
        long deadline = mLockPatternUtils.getLockoutAttemptDeadline();
        if (deadline != 0) {
            handleAttemptLockout(deadline);
        }

    }

    /** {@inheritDoc} */
    public void cleanUp() {
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            // when timeout dialog closes we want to update our state
            onResume();
        }
    }

    private class UnlockPatternListener
            implements PatternView.OnPatternListener {

        public void onPatternStart() {
            if (mHeardText != null) {
                mHeardTextView.setText(mHeardText);
            } else {
                mHeardTextView.setText(R.string.draw_pattern_unlock);
            }
            mLockPatternView.removeCallbacks(mCancelPatternRunnable);
        }

        public void onPatternCleared() {
        }

        public void onPatternCellAdded(List<Cell> pattern) {
            if (mHeardText != null) {
                mHeardTextView.setText(mHeardText);
            } else {
                mHeardTextView.setText(R.string.draw_pattern_unlock);
            }
            // To guard against accidental poking of the wakelock, look for
            // the user actually trying to draw a pattern of some minimal length.
            if (pattern.size() > MIN_PATTERN_BEFORE_POKE_WAKELOCK) {
// mCallback.pokeWakelock(UNLOCK_PATTERN_WAKE_INTERVAL_MS);
            } else {
                // Give just a little extra time if they hit one of the first few dots
// mCallback.pokeWakelock(UNLOCK_PATTERN_WAKE_INTERVAL_FIRST_DOTS_MS);
            }
        }

        public void onPatternDetected(List<PatternView.Cell> pattern) {
            boolean pass = checkPassword(pattern);
            if (pass) {
                mLockPatternView
                        .setDisplayMode(PatternView.DisplayMode.Correct);
                if (mSecureAccess != null) {
                    mSecureAccess.onSecureSuccess();
                }
// updateStatusLines();
// mCallback.keyguardDone(true);
// mCallback.reportSuccessfulUnlockAttempt();
            } else {
                if (pattern.size() > MIN_PATTERN_BEFORE_POKE_WAKELOCK) {
// mCallback.pokeWakelock(UNLOCK_PATTERN_WAKE_INTERVAL_MS);
                }
                mLockPatternView.setDisplayMode(PatternView.DisplayMode.Wrong);
                mHeardTextView.setText(R.string.wrong_pattern_unlock);
                postClearPatternRunnable();
// if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
// mTotalFailedPatternAttempts++;
// mFailedPatternAttemptsSinceLastTimeout++;
// mCallback.reportFailedUnlockAttempt();
// }
// if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
// long deadline = mLockPatternUtils.setLockoutAttemptDeadline();
// handleAttemptLockout(deadline);
// } else {
                // TODO mUnlockIcon.setVisibility(View.VISIBLE);
// mInstructions = getContext().getString(R.string.lockscreen_pattern_wrong);
// updateStatusLines();
// Toast.makeText(getContext(), "wrong", Toast.LENGTH_SHORT).show();
// mLockPatternView.postDelayed(
// mCancelPatternRunnable,
// PATTERN_CLEAR_TIMEOUT_MS);
// }
            }
        }
    }

    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView
                    .setDisplayMode(PatternView.DisplayMode.Correct);
            mLockPatternView.clearPattern();
            if (mHeardText != null) {
                mHeardTextView.setText(mHeardText);
            } else {
                mHeardTextView.setText(R.string.draw_pattern_unlock);
            }
        }
    };

    private void postClearPatternRunnable() {

        mLockPatternView.removeCallbacks(mClearPatternRunnable);
        mLockPatternView.postDelayed(mClearPatternRunnable, 2000);
    }

    private void handleAttemptLockout(long elapsedRealtimeDeadline) {
        mLockPatternView.clearPattern();
    }

    public void onPhoneStateChanged(String newState) {
    }

    @Override
    public void onShow() {
        mLockPatternView.startAnimator();
    }

    @Override
    public void onHide() {

    }

}
