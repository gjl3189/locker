package com.cyou.cma.clockscreen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.core.Intents;

public class ThemeApplyBroadcastReveiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intents.ACTION_THEME_INSTALLED.equals(intent.getAction())) {
            String packageName = intent.getStringExtra(Constants.C_EXTRAS_PACKAGE);
//            new ApplyThemeThread(context, null, packageName).start();
            //TODO jiangbin 
        }
    }
}
