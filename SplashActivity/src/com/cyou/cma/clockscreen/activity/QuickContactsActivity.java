/*
 * Copyright (C) 2009 The Android Open Source Project
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
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.ListView;

import com.adjust.sdk.Adjust;
import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.LockApplication;
import com.cyou.cma.clockscreen.adapter.InstalledAppAdapter4QuickLaunch;
import com.cyou.cma.clockscreen.fragment.QuickContactsFragment;
import com.cyou.cma.clockscreen.util.Util;

public class QuickContactsActivity extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_quick_contacts);
		Util.putPreferenceBoolean(this, Util.HASHASHAS, true);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		QuickContactsFragment.mHasSelectedApp.clear();
		QuickContactsFragment.mFolderApp.clear();
		LockApplication.mQuickContact = null;
	}
}
