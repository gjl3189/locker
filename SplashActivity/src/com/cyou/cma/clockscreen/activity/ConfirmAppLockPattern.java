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

import android.content.Intent;
import android.os.Bundle;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.password.widget.PatternView.Cell;
import com.cyou.cma.clockscreen.util.LockPatternUtils;

public class ConfirmAppLockPattern extends BaseConfirmPattern {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mHeaderText = getString(R.string.lockpattern_need_to_unlock);
        super.onCreate(savedInstanceState);
       
        mTitleTextView.setText(R.string.confirm_app_lock_pattern);
    }

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }

    @Override
    public boolean checkPassword(List<Cell> password) {
        return mLockPatternUtils.checkPattern(password, LockPatternUtils.APPLOCK_TYPE);
    }

    @Override
    public void onSecureSuccess() {
        startActivity(new Intent(ConfirmAppLockPattern.this, AppLockSettingActivity.class));
        finish();
    }
}
