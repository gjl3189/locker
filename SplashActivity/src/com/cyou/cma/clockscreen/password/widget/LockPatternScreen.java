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
import android.util.AttributeSet;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.password.widget.PatternView.Cell;
import com.cyou.cma.clockscreen.util.LockPatternUtils;

public class LockPatternScreen extends BasePatternScreen
{

    public LockPatternScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean checkPassword(List<Cell> password) {
        boolean resutlt = mLockPatternUtils.checkPattern(password, LockPatternUtils.LOCKSCREEN_TYPE);
        return resutlt;
    }
    @Override
    public int getResId() {
        return R.layout.keyguard_screen_unlock_portrait;
    }
}
