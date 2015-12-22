/**
 * 
 */

package com.cyou.cma.clockscreen.receiver;

import android.database.ContentObserver;
import android.os.Handler;

/**
 * @author Peter.Jiang
 */
public class UnreadMsgObserver extends ContentObserver {
    private OnUnreadMsgObserver mOnUnreadMsgObserver;

    public UnreadMsgObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        if (mOnUnreadMsgObserver != null) {
            mOnUnreadMsgObserver.onUnreadMsgObserver();
        }
    }

    public void setOnUnreadMsgObserver(OnUnreadMsgObserver onUnreadMsgObserver) {
        this.mOnUnreadMsgObserver = onUnreadMsgObserver;
    }

    public interface OnUnreadMsgObserver {
        /**
         * 有未读短信
         */
        public void onUnreadMsgObserver();
    }
}
