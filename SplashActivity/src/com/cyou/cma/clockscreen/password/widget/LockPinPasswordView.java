package com.cyou.cma.clockscreen.password.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.cyou.cma.clockscreen.util.LockPatternUtils;

/**
 * 锁屏的数字密码界面
 * @author jiangbin
 */
public class LockPinPasswordView extends BasePinPasswordView {

    public LockPinPasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean checkPassword(String password) {
        LockPatternUtils utils = mChooseLockSettingsHelper.utils();
        return utils.checkPassword(password, LockPatternUtils.LOCKSCREEN_TYPE);
    }

}
