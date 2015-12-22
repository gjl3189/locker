/**
 * 
 */

package com.cyou.cma.clockscreen.receiver;

import android.database.ContentObserver;
import android.os.Handler;

/**
 * @author Peter.Jiang
 */
public class MissedCallObserver extends ContentObserver {
    private OnMissedCallObserver mOnMissedCallObserver;

    public MissedCallObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {

        if (mOnMissedCallObserver != null) {
            mOnMissedCallObserver.onMissedCallObserver();
        }
    }

    public void setOnMissedCallObserver(OnMissedCallObserver onMissedCallObserver) {
        this.mOnMissedCallObserver = onMissedCallObserver;
    }

    public interface OnMissedCallObserver {
        /**
         * 有未读短信
         */
        public void onMissedCallObserver();
    }
}
