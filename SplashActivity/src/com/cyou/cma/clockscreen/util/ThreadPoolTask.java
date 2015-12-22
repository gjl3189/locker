package com.cyou.cma.clockscreen.util;

import android.os.Process;

public class ThreadPoolTask implements Runnable, Comparable<ThreadPoolTask> {
    public static final int PRIORITY_HIGH = 4;
    public static final int PRIORITY_NORMAL = 5;
    public static final int PRIORITY_LOW = 6;

    private Runnable mTarget;
    private int mPriority = PRIORITY_NORMAL;
    private long mQueuedTime;

    /**
     * Same to {@link #ThreadPoolTask(target, ThreadPoolTask.PRIORITY_NORMAL)}
     * @param target
     */
    public ThreadPoolTask(Runnable target) {
        mTarget = target;
    }

    /**
     * Construct a ThreadPoolTask object with specified target and priority.
     * @param target
     * @param priority One of the priorities {@link #PRIORITY_NORMAL}, {@link #PRIORITY_LOW}
     *                 or {@link #PRIORITY_HIGH}
     */
    public ThreadPoolTask(Runnable target, int priority) {
        mTarget = target;
        mPriority = priority;
    }

    Runnable getTarget() {
        return mTarget;
    }

    int getPriority() {
        return mPriority;
    }

    void updateQueuedTime(long queuedTime) {
        mQueuedTime = queuedTime;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        mTarget.run();
    }

    @Override
    public int compareTo(ThreadPoolTask another) {
        if (mPriority < another.mPriority) {
            return -1;
        } else if (mPriority > another.mPriority) {
            return 1;
        } else {
            if (mQueuedTime < another.mQueuedTime) {
                return -1;
            } else if (mQueuedTime > another.mQueuedTime) {
                return 1;
            }
            return 0;
        }
    }
}
