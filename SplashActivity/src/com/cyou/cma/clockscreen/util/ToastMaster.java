package com.cyou.cma.clockscreen.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.lang.ref.SoftReference;

public final class ToastMaster {

    /**
     * Show some message for user.<br>
     * we guarantee that It is shown one time in its duration .<br>
     * If the message by msgResId do not exist , we do not show anything .<br>
     * 
     * @param context
     * @param msgResId
     * @param duration
     *            How long to display the message. Either LENGTH_SHORT or
     *            LENGTH_LONG
     */
    public final static void makeText(Context context, int msgResId,
            final int duration) {
        if (context != null) {
            context = context.getApplicationContext();
            try {
                makeText(context, context.getResources().getString(msgResId),
                        duration);
            } catch (Exception e) {
            }
        }
    }

    /**
     * Show some message for user.<br>
     * we guarantee that It is shown one time in its duration .<br>
     * If the message is empty ( null or empty string) , we do not show anything
     * .<br>
     * 
     * @param context
     * @param message
     * @param duration
     *            How long to display the message. Either LENGTH_SHORT or
     *            LENGTH_LONG
     */
    public final static void makeText(Context context, String message,
            final int durationType) {
        if (TextUtils.isEmpty(message)) {
            return;
        }

        context = context.getApplicationContext();

        // make the duration
        long duration;
        if (durationType == Toast.LENGTH_LONG) {
            duration = 3500;// this is a default long time in framework
        } else {
            duration = 2000;// this is a default short time in framework
        }

        // whether or not show
        if (isLastMessage(message)) {
            long now = System.currentTimeMillis();
            long delt = (now - lastTime);
            if (delt > duration) {
                Toast.makeText(context, message, durationType).show();
                lastTime = now;
            }/*
             * else{ }
             */
        } else {
            Toast.makeText(context, message, durationType).show();
            lastTime = System.currentTimeMillis();
        }
    }

    /**
     * Whether or not current message is the same with the last message .<br>
     * If not , we will set the current message as new message , and put into
     * SoftReference
     * 
     * @param lastMessage
     *            It must be not null . we have fliter it .
     * @return
     */
    private final static boolean isLastMessage(String newMessage) {
        boolean isSame = false;
        if (softRef_LastMessage == null || softRef_LastMessage.get() == null) {
            // do nothing. the result has been "false"
        } else {
            isSame = softRef_LastMessage.get().equalsIgnoreCase(newMessage);
        }

        if (!isSame) {
            if (null != softRef_LastMessage) {
                softRef_LastMessage.clear();
            }
            softRef_LastMessage = new SoftReference<String>(newMessage);
        }

        return isSame;
    }

    // the last time for show
    private static long lastTime;

    // Maybe , the softReference is not meanigful
    private static SoftReference<String> softRef_LastMessage;

    // we only need no any instance
    private ToastMaster() {
    }
}
