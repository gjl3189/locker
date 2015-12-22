package com.cyou.cma.clockscreen.util;


import android.util.Log;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AppThreadPool {
    private static final String TAG = "DxOptThreadPool";

    private static class SingletonHolder {
        public static final AppThreadPool INSTANCE = new AppThreadPool();
    }

    private static class PoolThreadFactory implements ThreadFactory {
        private boolean mUiTask;
        private AtomicInteger mCount = new AtomicInteger(1);

        public PoolThreadFactory(boolean uiTask) {
            mUiTask = uiTask;
        }

        @Override
        public Thread newThread(Runnable r) {
            if (mUiTask) {
                return new Thread(r, "DxOptUiThreadPool#" + mCount.getAndIncrement());
            } else {
                return new Thread(r, "DxOptBkgThreadPool#" + mCount.getAndIncrement());
            }
        }
    }

    private ThreadPoolExecutor mUiThreadPoolExecutor;
    private ThreadPoolExecutor mBkgThreadPoolExecutor;

    public static AppThreadPool getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private AppThreadPool() {
        BlockingQueue<Runnable> poolQueue = new PriorityBlockingQueue<Runnable>();

        int uiThreadsCount = getUiInitialThreadPoolSize();
        int bkgThreadsCount = 1;

        mUiThreadPoolExecutor = new ThreadPoolExecutor(uiThreadsCount, uiThreadsCount, 1,
                TimeUnit.SECONDS, poolQueue, new PoolThreadFactory(true));
        mBkgThreadPoolExecutor = new ThreadPoolExecutor(bkgThreadsCount, bkgThreadsCount, 1,
                TimeUnit.SECONDS, poolQueue, new PoolThreadFactory(false));
    }

    private int getUiInitialThreadPoolSize() {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        return Math.max(2, cpuCores / 2); // at least 2, or (cores/2)
    }

    /**
     * After the app started, we can make more threads to execute background jobs.
     */
    public void enlargePoolSize() {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        int uiPoolSize = Math.max(4, cpuCores / 2); // at least 4, or (cores/2)
        mUiThreadPoolExecutor.setCorePoolSize(uiPoolSize);
        mUiThreadPoolExecutor.setMaximumPoolSize(uiPoolSize);

        int bkgPoolSize = Math.max(4, cpuCores / 2); // at least 4, or (cores/2)
        mBkgThreadPoolExecutor.setCorePoolSize(bkgPoolSize);
        mBkgThreadPoolExecutor.setMaximumPoolSize(bkgPoolSize);
    }

    /**
     * Add a new UI-related task with the priority {@link ThreadPoolTask#PRIORITY_NORMAL}.
     * @param job The task to be execute
     */
    public void addUiTask(Runnable job) {
        ThreadPoolTask task = new ThreadPoolTask(job);
        task.updateQueuedTime(System.currentTimeMillis());
        mUiThreadPoolExecutor.execute(task);
    }

    /**
     * Add a new UI-related task with specified priority.
     * @param job The task to be execute
     * @param priority One of the priorities {@link ThreadPoolTask#PRIORITY_NORMAL},
     *                 {@link ThreadPoolTask#PRIORITY_LOW} or {@link ThreadPoolTask#PRIORITY_HIGH}
     */
    public void addUiTask(Runnable job, int priority) {
        ThreadPoolTask task = new ThreadPoolTask(job);
        task.updateQueuedTime(System.currentTimeMillis());
        mUiThreadPoolExecutor.execute(task);
    }

    /**
     * Add a new non-UI-related task with the priority {@link ThreadPoolTask#PRIORITY_NORMAL}.
     * @param job The task to be execute
     */
    public void addBkgTask(Runnable job) {
        ThreadPoolTask task = new ThreadPoolTask(job);
        task.updateQueuedTime(System.currentTimeMillis());
        mBkgThreadPoolExecutor.execute(task);
    }

    /**
     * Add a new non-UI-related task with specified priority.
     * @param job The task to be execute
     * @param priority One of the priorities {@link ThreadPoolTask#PRIORITY_NORMAL},
     *                 {@link ThreadPoolTask#PRIORITY_LOW} or {@link ThreadPoolTask#PRIORITY_HIGH}
     */
    public void addBkgTask(Runnable job, int priority) {
        ThreadPoolTask task = new ThreadPoolTask(job, priority);
        task.updateQueuedTime(System.currentTimeMillis());
        mBkgThreadPoolExecutor.execute(task);
    }

    public void dumpState(String logPrefix) {
        Log.d(TAG, logPrefix + "-UiTaskPool, PoolCoreSize: " + mBkgThreadPoolExecutor.getCorePoolSize()
                + ", ActiveThreadCount: " + mBkgThreadPoolExecutor.getActiveCount()
                + ", CompletedTaskCount: " + mBkgThreadPoolExecutor.getCompletedTaskCount()
                + ", CurPoolSize:" + mBkgThreadPoolExecutor.getPoolSize()
                + ", ScheduledTaskCount: " + mBkgThreadPoolExecutor.getTaskCount()
                + ", QueueSize: " + mBkgThreadPoolExecutor.getQueue().size());
        Log.d(TAG, logPrefix + "-BkgTaskPool, PoolCoreSize: " + mBkgThreadPoolExecutor.getCorePoolSize()
                + ", ActiveThreadCount: " + mBkgThreadPoolExecutor.getActiveCount()
                + ", CompletedTaskCount: " + mBkgThreadPoolExecutor.getCompletedTaskCount()
                + ", CurPoolSize:" + mBkgThreadPoolExecutor.getPoolSize()
                + ", ScheduledTaskCount: " + mBkgThreadPoolExecutor.getTaskCount()
                + ", QueueSize: " + mBkgThreadPoolExecutor.getQueue().size());
    }
}
