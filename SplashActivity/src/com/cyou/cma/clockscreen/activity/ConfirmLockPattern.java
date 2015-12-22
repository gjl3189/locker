package com.cyou.cma.clockscreen.activity;

import java.util.List;

import android.content.Intent;

import com.cyou.cma.clockscreen.password.widget.PatternView.Cell;
import com.cyou.cma.clockscreen.util.LockPatternUtils;

public class ConfirmLockPattern extends BaseConfirmPattern {

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }

    @Override
    public boolean checkPassword(List<Cell> password) {
        return mLockPatternUtils.checkPattern(password, LockPatternUtils.LOCKSCREEN_TYPE);
    }

    @Override
    public void onSecureSuccess() {
        startActivity(new Intent(ConfirmLockPattern.this, PwdSettingActivity.class));
        finish();

    }
}
