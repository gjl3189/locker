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

import android.content.Intent;
import android.os.Bundle;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.password.SecureAccess;
import com.cyou.cma.clockscreen.util.LockPatternUtils;

/**
 * 重用Lockscreen的pin验证密码界面 
 * 这个界面应该只会调用一次 
 * 
 * @author jiangbin
 */
public class ConfirmPinPasswordLockscreen4AppLock extends BaseConfirmPinPassword implements SecureAccess
{

    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPasswordEntry.setHint(R.string.default_password_lockscreen);
        mTitleTextView.setText(R.string.confirm_app_lock_pattern);
    }

    @Override
    public boolean checkPassword(String password) {
        this.password = password;
        return mLockPatternUtils.checkPassword(password, LockPatternUtils.LOCKSCREEN_TYPE);
    }

    @Override
    public void onSecureSuccess() {
        mLockPatternUtils.saveLockPassword(password, LockPatternUtils.APPLOCK_TYPE);
        startActivity(new Intent(ConfirmPinPasswordLockscreen4AppLock.this, AppLockSettingActivity.class));
        finish();

    }

}
