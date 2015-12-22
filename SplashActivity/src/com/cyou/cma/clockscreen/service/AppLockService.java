package com.cyou.cma.clockscreen.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
//import android.util.Log;

import com.cyou.cma.clockscreen.applock.AppLockHelper;
import com.cyou.cma.clockscreen.applock.StartAppLockBehavior;
import com.cyou.cma.clockscreen.sqlite.SqlListenerLockApp;
import com.cyou.cma.clockscreen.util.Util;

/**
 * app locker的后台服务
 * 
 * @author jiangbin
 */
public class AppLockService extends Service {
    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;
// private boolean mRedelivery;
    private ActivityManager mActivityManager;
    private ArrayList<String> mLauncherPackageNames;
    private Stack<String> mStack = new Stack<String>();
    private Object mLockObject = new Object();
    public static long mLastLockTime;
    private long mCurrentTime;
    private SqlListenerLockApp sqlListenerLockApp;
    private boolean on = true;
    public static  Hashtable<String, Boolean> mHashtable = new Hashtable<String, Boolean>();

// private ArrayList<String> mProtectedPackages = new ArrayList<String>();

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (on) {
                qureyTaskTop();
                Message message = obtainMessage();
                mServiceHandler.sendMessageDelayed(message, 300);
            }
        }
    }

    private Handler mStartAcitivityHandler = new Handler() {
        @Override
		public void handleMessage(Message msg) {
            // 根据具体业务启动相应的界面
            mCurrentTime = SystemClock.elapsedRealtime();
            if (mCurrentTime - mLastLockTime >= 1000) {
                Intent intent = new Intent();
                String clazzName = AppLockHelper.mAppLockerClazzs.get(AppLockHelper
                        .getAppLockType(getApplicationContext()));
                // TODO 判断是否为空
                if (clazzName == null)
                    return;
                intent.putExtra("packageName", msg.obj.toString());
                intent.putExtra("openApp", msg.what);
// Log.d("applocker", "send packagename" + msg.obj.toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    Class clazz = getClassLoader().loadClass(clazzName);

                    StartAppLockBehavior startAppLockBehavior = (StartAppLockBehavior) clazz
                            .newInstance();
                    startAppLockBehavior.startAppLockActivity(getApplicationContext(), intent);
                } catch (InstantiationException e) {
                    Util.printException(e);
                } catch (IllegalAccessException e) {
                    Util.printException(e);
                } catch (ClassNotFoundException e) {
                    Util.printException(e);
                }
            }
// mLastLockTime = mCurrentTime;
        }
    };

// public void setIntentRedelivery(boolean enabled) {
// mRedelivery = enabled;
// }

    @Override
    public void onCreate() {

        super.onCreate();
        sqlListenerLockApp = new SqlListenerLockApp(getApplicationContext());
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ResolveInfo> resolveInfos = Util.getHomeList(AppLockService.this);
        mLauncherPackageNames = new ArrayList<String>();
        for (ResolveInfo resolveInfo : resolveInfos) {
            mLauncherPackageNames.add(resolveInfo.activityInfo.packageName);
        }
        HandlerThread thread = new HandlerThread("AppLockService");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        if (Build.VERSION.SDK_INT < 18) {
            Notification localNotification = new Notification();
            // localNotification.flags = localNotification.flags | 0x20;
            startForeground(11146, localNotification);
        }

        IntentFilter filter = new IntentFilter();
// filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                on = false;
                clear();
                mHashtable.clear();
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                on = true;
                Message msg = mServiceHandler.obtainMessage();
                mServiceHandler.sendMessage(msg);
            }
        }
    };

    @Override
    public void onStart(Intent intent, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);
    }

    /**
     * You should not override this method for your IntentService. Instead,
     * override {@link #onHandleIntent}, which the system calls when the IntentService
     * receives a start request.
     * 
     * @see android.app.Service#onStartCommand
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
// return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mServiceLooper.quit();
        stopForeground(true);
        unregisterReceiver(receiver);
        sqlListenerLockApp.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 检查栈顶的activity的包名，并做相应的处理
     */
    protected void qureyTaskTop() {

        String packageName = getTopActivityPackage();
// String packageName = compontName.split(":")[0];
        if (packageName.equals(getPackageName())) {
            return;
        }
        if (mLauncherPackageNames.contains(packageName) || "android".equals(packageName)) {
            // 如果当前界面是 桌面 则重置所有的状态
// Log.d("applocker", "the top activity clear--->" + packageName);
// clear();
            return;

        }
// if (packageName.equals(getPackageName())) {
// return;
// }
// Log.d("applocker", "the top activity is " + packageName);
        if (contains(packageName)&&(mHashtable.get(packageName)!=null&&mHashtable.get(packageName))) {

        } else {
            List<String> protectPackages = sqlListenerLockApp.getLockedApp();
            // TODO jiangbin 开启密码保护界面
// || "com.android.packageinstaller:com.android.packageinstaller.UninstallerActivity"
// .equals(compontName)
            // TODO 这里有同包名的应用
// if(Build.DEVICE.equals(object))
            if (protectPackages.contains(packageName)) {
                push(packageName);
                Message msg = mStartAcitivityHandler.obtainMessage();
                msg.obj = packageName;

                mStartAcitivityHandler.sendMessage(msg);

            }
        }

    }

    public String getTopActivityPackage() {
        ComponentName cn = this.mActivityManager.getRunningTasks(1).get(0).topActivity;
// return cn.getPackageName() + ":" + cn.getClassName();
        return cn.getPackageName();
    }

    public void push(String packageName) {
        synchronized (mLockObject) {
            mStack.push(packageName);
        }
    }

    public boolean contains(String packageName) {
        synchronized (mLockObject) {
            return mStack.contains(packageName);
        }
    }

    public void clear() {
        synchronized (mLockObject) {
            mStack.clear();
// mLastLockTime = SystemClock.elapsedRealtime();
        }
    }
}
