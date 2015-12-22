package com.cyou.cma.clockscreen.password.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.password.widget.PatternView.Cell;
import com.cyou.cma.clockscreen.util.LockPatternUtils;

public class ApplockPatternScreen extends BasePatternScreen {

    public ApplockPatternScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHeardText = context.getString(R.string.draw_pattern_app_lock);
        mHeardTextView.setText(mHeardText);
    }

    @Override
    public boolean checkPassword(List<Cell> password) {
        return mLockPatternUtils.checkPattern(password, LockPatternUtils.APPLOCK_TYPE);
    }

    @Override
    public int getResId() {
        return R.layout.applock_screen_unlock_portrait;
    }

}
