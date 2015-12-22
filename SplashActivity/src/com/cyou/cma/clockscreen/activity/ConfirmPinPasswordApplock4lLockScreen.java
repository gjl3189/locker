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
 * add in 2014/11/24
 * 
 * @author jiangbin
 * 
 */
public class ConfirmPinPasswordApplock4lLockScreen extends
		BaseConfirmPinPassword implements SecureAccess {
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPasswordEntry.setHint(R.string.default_password_applock);
		// mTitleTextView.setText(R.string.);
	}

	@Override
	public boolean checkPassword(String password) {
		this.password = password;
		return mLockPatternUtils.checkPassword(password,
				LockPatternUtils.APPLOCK_TYPE);
		// return false;
	}

	@Override
	public void onSecureSuccess() {
		mLockPatternUtils.saveLockPassword(password,
				LockPatternUtils.LOCKSCREEN_TYPE);
		startActivity(new Intent(ConfirmPinPasswordApplock4lLockScreen.this,
				PwdSettingActivity.class));
		finish();
	}

}
