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
import com.cyou.cma.clockscreen.applock.AppLockHelper;
import com.cyou.cma.clockscreen.password.SecureAccess;
import com.cyou.cma.clockscreen.util.LockPatternUtils;

/**
 * 0000
 * 
 * @author jiangbin
 */
public class ConfirmPinPasswordAppLockDefault extends BaseConfirmPinPassword
		implements SecureAccess {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!AppLockHelper.hasPasswordEverSet(this)) {
			mPasswordEntry.setHint(R.string.default_password);
		}
		mTitleTextView.setText(R.string.confirm_app_lock_pattern);
	}

	@Override
	public void onSecureSuccess() {

//		AppLockHelper.setPasswordEverSet(true, mContext);
		AppLockHelper.setAppLockType(AppLockHelper.PIN_APP_LOCKER,
				mContext);

		mLockPatternUtils.saveLockPassword("0000",
				LockPatternUtils.APPLOCK_TYPE);
		startActivity(new Intent(ConfirmPinPasswordAppLockDefault.this,
				AppLockSettingActivity.class));
		finish();
	}

	@Override
	public boolean checkPassword(String password) {
		return password.equals("0000");
	}

}
