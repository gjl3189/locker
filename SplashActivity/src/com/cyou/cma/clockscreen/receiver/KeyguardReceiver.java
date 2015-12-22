
package com.cyou.cma.clockscreen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.cyou.cma.clockscreen.activity.DismissActivity;
import com.cyou.cma.clockscreen.core.Intents;
import com.cyou.cma.clockscreen.util.Util;

/**
 * This broadcast receiver starts keyguard activity when receives
 * {@code Intent.ACTION_SCREEN_OFF} and {@code Intent.ACTION_BOOT_COMPLETE}
 * broadcasts.
 * 
 * @author wind.zhang
 */
public class KeyguardReceiver extends BroadcastReceiver {
    public static final String ACTION_DEVICE_POLICY_MANAGER_STATE_CHANGED = "android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED";

    private static final String TAG = "mopolocker";
    private boolean duringCall = false;

    private ScreenListener mScreenListener;

    public KeyguardReceiver(ScreenListener listener) {
        mScreenListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Util.Logjb(TAG, "KeyguardReceiver received broadcast:" + intent);
        }
        String action = intent.getAction();
        if (TextUtils.isEmpty(action))
            return;
        if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            Util.Logjb(TAG, "KeyguardReceiver ACTION_SCREEN_OFF duringCall=" + duringCall);
            if (!duringCall) {
                if (mScreenListener != null) {
                    // if (Util.getPreferenceInt(context, Util.SAVE_KEY_OS_TYPE,
                    // 0) >= 0) {
                    Intent activityDismiss = new Intent(context, DismissActivity.class);
                    activityDismiss.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activityDismiss.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(activityDismiss);
                    // }
                    mScreenListener.onScreenOff();
                }
            }
        } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
            if (mScreenListener != null) {
                mScreenListener.onScreenOn();
            }
        } else if (Intent.ACTION_USER_PRESENT.equals(action)
                || ACTION_DEVICE_POLICY_MANAGER_STATE_CHANGED.equals(intent.getAction())) {
        } else if (action.equals(Intents.ACTION_RECREATE_DIALOG)) {
            if (mScreenListener != null) {
                mScreenListener.RecreatLocker();
            }
        } else if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            duringCall = true;
        } else {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    PhoneStateListener listener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
//                    Log.e(TAG, "CALL_STATE_IDLE");
                    duringCall = false;
                    if (mScreenListener != null) {
                        mScreenListener.onCallEnd();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
//                    Log.e(TAG, "CALL_STATE_OFFHOOK");
                    duringCall = true;
                    if (mScreenListener != null) {
                        mScreenListener.onCallIn();
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
//                    Log.e(TAG, "CALL_STATE_RINGING");
                    duringCall = true;
                    if (mScreenListener != null) {
                        mScreenListener.onCallIn();
                    }
                    break;
            }
        }
    };

    public interface ScreenListener {
        public void onScreenOff();

        public void onScreenOn();

        public void RecreatLocker();

        public void onCallIn();

        public void onCallEnd();
    }
}
