package com.cyou.cma.clockscreen.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.cyou.cma.clockscreen.core.Intents;
import com.cyou.cma.clockscreen.service.AppLockService;
import com.cyou.cma.clockscreen.service.KeyguardService;
import com.cyou.cma.clockscreen.util.SettingsHelper;
import com.cyou.cma.clockscreen.util.Util;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // Util.Logcs("BootReceiver", "onReceive==action-->"+action);
        if (TextUtils.isEmpty(action))
            return;
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (SettingsHelper.getLockServiceEnable(context)) {
                Intent i = new Intent(context, KeyguardService.class);
                context.startService(i);
            }
            if (SettingsHelper.getApplockEnable(context)) {
                Intent i = new Intent(context, AppLockService.class);
                context.startService(i);

            }
        } else if (action.equals(Intent.ACTION_USER_PRESENT)
                || action.equals("android.net.conn.CONNECTIVITY_CHANGE")
                || action.equals(Intent.ACTION_DATE_CHANGED)) {
            if (SettingsHelper.getLockServiceEnable(context)) {
                Intent i = new Intent(context, KeyguardService.class);
                context.startService(i);
            }
            if (SettingsHelper.getApplockEnable(context)) {
                Intent i = new Intent(context, AppLockService.class);
                context.startService(i);

            }
        } else if (action.equals(Intents.ACTION_RESTART_LOCKER_FROM_THEME)) {
            Util.Logcs("BootReceiver", Intents.ACTION_RESTART_LOCKER_FROM_THEME);
            if (SettingsHelper.getLockServiceEnable(context)) {
                Intent i = new Intent(context, KeyguardService.class);
                AlarmManager alarmManager = (AlarmManager) context
                        .getSystemService(Context.ALARM_SERVICE);
                PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + 1000, pi);
            }

            if (SettingsHelper.getApplockEnable(context)) {
                Intent i = new Intent(context, AppLockService.class);
                AlarmManager alarmManager = (AlarmManager) context
                        .getSystemService(Context.ALARM_SERVICE);
                PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + 1000, pi);
            }
            android.os.Process.killProcess(android.os.Process.myTid());
            System.exit(0);
        } else if (action.equals(Intents.ACTION_RESTART_LOCKER)) {
            Util.Logcs("BootReceiver", Intents.ACTION_RESTART_LOCKER);
            if (SettingsHelper.getLockServiceEnable(context)) {
                context.startService(new Intent(context, KeyguardService.class));
            }
            if (SettingsHelper.getApplockEnable(context)) {
                Intent i = new Intent(context, AppLockService.class);
                context.startService(i);

            }
        }
    }

}
