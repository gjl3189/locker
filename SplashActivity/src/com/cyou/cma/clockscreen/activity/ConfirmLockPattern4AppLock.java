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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.password.widget.PatternView;
import com.cyou.cma.clockscreen.password.widget.PatternView.Cell;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.LockPatternUtils;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.LinearLayoutWithDefaultTouchRecepient;
import com.cyou.cma.clockscreen.widget.material.LImageButton;

/**
 * Launch this when you want the user to confirm their lock pattern.
 * Sets an activity result of {@link Activity#RESULT_OK} when the user
 * successfully confirmed their pattern.
 */
public class ConfirmLockPattern4AppLock extends BaseConfirmPattern {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mHeaderText = getString(R.string.lockpattern_need_to_app_lock);
        super.onCreate(savedInstanceState);

        mTitleTextView.setText(R.string.confirm_app_lock_pattern);

    }

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }

    private List<Cell> password;

    @Override
    public boolean checkPassword(List<Cell> password) {
        this.password = password;
        return mLockPatternUtils.checkPattern(password, LockPatternUtils.LOCKSCREEN_TYPE);
    }

    @Override
    public void onSecureSuccess() {
        mLockPatternUtils.saveLockPattern(password, LockPatternUtils.APPLOCK_TYPE);
        startActivity(new Intent(ConfirmLockPattern4AppLock.this, AppLockSettingActivity.class));
        finish();

    }

}