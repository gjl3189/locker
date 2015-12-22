package com.cyou.cma.clockscreen.core;

import java.lang.ref.WeakReference;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.text.TextUtils;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.core.CheckObserverAsyncTask.Missed;
import com.cyou.cma.clockscreen.util.Util;

public class CheckObserverAsyncTask extends AsyncTask<Integer, String, Missed> {
    private WeakReference<Context> mWeakReferenceContext;
    private WeakReference<Handler> mWeakReferenceHandler;
    private int mCheckType = -1;
    public static final int CHECK_CALL = 1;
    public static final int CHECK_SMS = 2;

    public CheckObserverAsyncTask(Context context, int type, Handler handler) {

        mCheckType = type;
        mWeakReferenceContext = new WeakReference<Context>(context);
        mWeakReferenceHandler = new WeakReference<Handler>(handler);
    }

    @Override
    protected Missed doInBackground(Integer... params) {
        switch (mCheckType) {
            case CHECK_CALL:

                if (mWeakReferenceContext.get() != null) {

                    return getMissedCall(mWeakReferenceContext.get());
                }
            case CHECK_SMS:
                if (mWeakReferenceContext.get() != null) {

                    return getMissedMsg(mWeakReferenceContext.get());
                }
            default:
                return null;
        }

    }

    @Override
    protected void onPostExecute(Missed result) {
        super.onPostExecute(result);
        if (mWeakReferenceHandler.get() != null) {
            Message msg = mWeakReferenceHandler.get().obtainMessage();
            msg.obj = result;
            mWeakReferenceHandler.get().sendMessage(msg);
        }
    }

    private MissedCall getMissedCall(Context context) {
        ContentResolver cr = context.getContentResolver();
        int callCount = 0;
        String[] proj = new String[] {
                CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.DATE
        };
        Cursor cursor = null;
        Uri uri = CallLog.Calls.CONTENT_URI;
        try {
            cursor = cr.query(uri, proj, "type=3 and new<>0", null,
                    CallLog.Calls.DEFAULT_SORT_ORDER);
            if (cursor != null)
                callCount = cursor.getCount();
        } catch (Exception e) {
            Util.printException(e);
            cursor = null;
        }
        long missedCallTime = 0;
        StringBuilder sb = new StringBuilder();
        if (cursor != null && callCount > 0) {
            try {
                int count = 0;
                int dIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
                int nmIndex = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
                int nIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                while (cursor.moveToNext()) {
                    try {
                        count++;
                        long callTime = cursor.getLong(dIndex);
                        if (callTime > missedCallTime) {
                            missedCallTime = callTime;
                        }
                        String name = cursor.getString(nmIndex);
                        String number;
                        if (!TextUtils.isEmpty(name)) {
                            number = name;
                        } else {
                            number = cursor.getString(nIndex);
                        }
                        if ("-1".equals(number) || TextUtils.isEmpty(number)) {
                            number = context.getString(R.string.state_unknown);
                        }
                        sb.append(number);
                        if (count < callCount) {
                            sb.append(", ");
                        }
                    } catch (Exception e) {
                        Util.printException(e);
                    }
                }
            } catch (Exception e) {
                Util.printException(e);
            }
        }
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                Util.printException(e);
            } finally {
                cursor = null;
            }
        }
        MissedCall missedCall = new MissedCall();
        missedCall.missedCount = callCount;
        missedCall.missedTime = missedCallTime;
        missedCall.callNumber = sb.toString();
        return missedCall;
    }

    public class MissedCall extends Missed {
        public int missedCount;
        public long missedTime;
        public String callNumber;
    }

    private MissedMessage getMissedMsg(Context context) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse("content://sms/inbox");// Telephony.Sms.Inbox.CONTENT_URI;
        String[] proj = new String[2];
        proj[0] = "body";
        proj[1] = "date";
        String[] projmms = new String[1];
        projmms[0] = "date";
        String sortOrder = "date DESC";// Conversations.DEFAULT_SORT_ORDER;
        Cursor cursor = null;
        int smsCount = 0;
        try {
            cursor = cr.query(uri, proj, "read=0", null, sortOrder);
            if (cursor != null) {
                smsCount = cursor.getCount();
            }
        } catch (Exception e) {
            Util.printException(e);
        }
        long missedSmsTime = 0;
        String lastMsg = "";
        if (cursor != null && smsCount > 0) {
            try {
                int dIndex = cursor.getColumnIndex("date");
                int bIndex = cursor.getColumnIndex("body");
                while (cursor.moveToNext()) {
                    try {
                        long smsTime = cursor.getLong(dIndex);
                        if (smsTime > missedSmsTime) {
                            missedSmsTime = smsTime;
                            lastMsg = cursor.getString(bIndex);
                        }
                    } catch (Exception e) {
                        Util.printException(e);
                    }
                }
            } catch (Exception e) {
                Util.printException(e);
            }
        }
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            } finally {
                cursor = null;
            }
        }
        uri = Uri.parse("content://mms/inbox");// Telephony.Mms.Inbox.CONTENT_URI;
        cursor = null;
        int mmsCount = 0;
        try {
            cursor = cr.query(uri, projmms, /* Mms.READ */"read" + " = 0 and " + /*
                                                                                   * Mms
                                                                                   * .
                                                                                   * MESSAGE_TYPE
                                                                                   */"m_type"
                    + " <> " + 0x86 + " and " + /* Mms.MESSAGE_TYPE */"m_type" + " <> " + 0x88,
                    null, sortOrder);
            if (cursor != null) {
                mmsCount = cursor.getCount();
            }
        } catch (Exception e) {
            Util.printException(e);
        }
        if (cursor != null && mmsCount > 0) {
            try {
                int dIndex = cursor.getColumnIndex("date");
                while (cursor.moveToNext()) {
                    try {
                        long mmsTime = cursor.getLong(dIndex);
                        if (mmsTime > missedSmsTime) {
                            missedSmsTime = mmsTime;
                            lastMsg = context.getString(R.string.mms);
                        }
                    } catch (Exception e) {
                        Util.printException(e);
                    }
                }
            } catch (Exception e) {
                Util.printException(e);
            }
        }
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            } finally {
                cursor = null;
            }
        }

        MissedMessage missedMessage = new MissedMessage();
        missedMessage.missedCount = smsCount + mmsCount;
        missedMessage.missedTime = missedSmsTime;
        missedMessage.messageContent = lastMsg;
        return missedMessage;

    }

    public class MissedMessage extends Missed {
        public int missedCount;
        public long missedTime;
        public String messageContent;
    }

    public class Missed {
    }

}
