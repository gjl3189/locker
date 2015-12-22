package com.cyou.cma.clockscreen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
 * @author Peter.Jiang
 */
public class BatteryChangeReceiver extends BroadcastReceiver {
    private OnBatteryChangeReceiver mOnBatteryChangeReceiver;

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean plugged = false;
        int rawlevel = intent.getIntExtra("level", -1);
        int scale = intent.getIntExtra("scale", -1);
        int status = intent.getIntExtra("status", -1);
        int health = intent.getIntExtra("health", -1);
        int level = -1; // percentage, or -1 for unknown
        if (rawlevel >= 0 && scale > 0) {
            level = (rawlevel * 100) / scale;
        }
        if (BatteryManager.BATTERY_HEALTH_OVERHEAT == health) {
        } else {
            switch (status) {
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    plugged = false;
                    break;
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    plugged = true;
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    plugged = false;
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    plugged = true;
                    break;
                default:
                    plugged = false;
                    break;
            }
        }

        if (mOnBatteryChangeReceiver != null) {
            mOnBatteryChangeReceiver.onRefreshBatteryInfo(plugged, level);
        }

    }

    public void setOnBatteryChangeReceiver(OnBatteryChangeReceiver onBatteryChangeReceiver) {
        this.mOnBatteryChangeReceiver = onBatteryChangeReceiver;
    }

    public interface OnBatteryChangeReceiver {
        /**
         * 电量变化
         * 
         * @param plugged 插入数据线充电
         * @param level 电量值100表示充满 0表示没电
         */
        public void onRefreshBatteryInfo(boolean plugged, int level);
    }

}
