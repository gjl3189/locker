/**
 * 
 */

package com.cyou.cma.clockscreen.receiver;

import android.database.ContentObserver;
import android.os.Handler;

/**
 * 日期变化
 * 
 * @author Peter.Jiang
 */
public class DateTimeObserver extends ContentObserver {
    private OnDateTimeObserver mOnDateTimeObserver;

    public DateTimeObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {

        if (mOnDateTimeObserver != null) {
            mOnDateTimeObserver.onDateTimeObserver();
        }
    }

    public void setOnDateTimeObserver(OnDateTimeObserver onDateTimeObserver) {
        this.mOnDateTimeObserver = onDateTimeObserver;
    }

    public interface OnDateTimeObserver {
        /**
         * 日期变化接口
         */
        public void onDateTimeObserver();
    }
}
