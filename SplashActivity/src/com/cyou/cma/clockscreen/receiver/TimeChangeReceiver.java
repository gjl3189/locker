/**
 * 
 */

package com.cyou.cma.clockscreen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cyou.cma.clockscreen.util.Util;

/**
 * @author Peter.Jiang
 */
public class TimeChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "mopolocker";
    private OnTimeChangeReceiver mOnTimeChangeReceiver;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null)
            return;

        String action = intent.getAction();
        Util.Logjb(TAG, "onReceive=" + action);
        if (action.equals(Intent.ACTION_TIME_TICK) || action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {

            if (mOnTimeChangeReceiver != null) {
                mOnTimeChangeReceiver.onTimeChangeReceiver();
            }
        }

    }

    public void setOnTimeChangeReceiver(OnTimeChangeReceiver onTimeChangeReceiver) {
        this.mOnTimeChangeReceiver = onTimeChangeReceiver;
    }

    public interface OnTimeChangeReceiver {
        /**
         * 监听到时间变化
         */
        public void onTimeChangeReceiver();
    }
}
