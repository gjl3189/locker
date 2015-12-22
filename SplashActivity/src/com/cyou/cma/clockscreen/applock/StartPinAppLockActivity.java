package com.cyou.cma.clockscreen.applock;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class StartPinAppLockActivity implements StartAppLockBehavior {
    @Override
    public void startAppLockActivity(Context context, Intent intent) {
// intent.setComponent(new ComponentName(context.getPackageName(), ""));
        intent.setComponent(new ComponentName(context.getPackageName(),
                "com.cyou.cma.clockscreen.activity.PinAppLockActivity"));
        context.startActivity(intent);
    }
}
