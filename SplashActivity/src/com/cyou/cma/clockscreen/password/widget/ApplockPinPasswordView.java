package com.cyou.cma.clockscreen.password.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.cyou.cma.clockscreen.util.LockPatternUtils;

/**
 * 应用锁 数字密码控件
 * 
 * @author jiangbin
 * 
 */
public class ApplockPinPasswordView extends BasePinPasswordView {

	public ApplockPinPasswordView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean checkPassword(String password) {
		LockPatternUtils utils = mChooseLockSettingsHelper.utils();
		return utils.checkPassword(password, LockPatternUtils.APPLOCK_TYPE);
	}

}
