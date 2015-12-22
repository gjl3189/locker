package com.cyou.cma.clockscreen.password.widget;

import android.content.Context;
import android.util.AttributeSet;

public class AppLockDefaultPinPasswordView extends BasePinPasswordView {

    public AppLockDefaultPinPasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean checkPassword(String password) {
        return password.equals("0000");
// return false;
    }

}
