package com.cyou.cma.clockscreen.fragment;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.activity.LocalThemeDetailActivity;
import com.cyou.cma.clockscreen.adapter.LocalThemeGridViewAdapter;
import com.cyou.cma.clockscreen.bean.Group;
import com.cyou.cma.clockscreen.bean.InstallLocker;
import com.cyou.cma.clockscreen.util.InstallLockerDescendComparator;
import com.cyou.cma.clockscreen.util.SettingsHelper;
import com.cyou.cma.clockscreen.util.StringUtils;
import com.cyou.cma.clockscreen.util.ToastMaster;
import com.cyou.cma.clockscreen.util.Util;

@SuppressLint("NewApi")
public class LocalThemeFragment extends Fragment {
    private static final String TAG = "LocalThemeFragment";
    private GridView mLocalThemeGridView;
    private LocalThemeGridViewAdapter mLocalThemeGridViewAdapter;
    private Group<InstallLocker> mAllLocalThemes = new Group<InstallLocker>();

    private LoadLocalThemeTask mLoadLocalThemeTask;
    private Context mContext;
    private InstallLockerDescendComparator mDescendComparator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.Logjb(TAG, "oncreate");
        mContext = getActivity();
        mLocalThemeGridViewAdapter = new LocalThemeGridViewAdapter(mContext);
        mLocalThemeGridViewAdapter.setGroup(mAllLocalThemes);
        mLoadLocalThemeTask = new LoadLocalThemeTask(this);
        if (Build.VERSION.SDK_INT >= 11) {
            mLoadLocalThemeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mLoadLocalThemeTask.execute();
        }
        mDescendComparator = new InstallLockerDescendComparator();
        mContext.getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Constants.C_THEME_PACKAGE), false, mObserver);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_INSTALL_PACKAGE);
        intentFilter.addAction(Intent.ACTION_UNINSTALL_PACKAGE);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        mContext.registerReceiver(themeInstallReciever, intentFilter);
    }

    private BroadcastReceiver themeInstallReciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Util.Logjb("LocalThemeFragment", "LocalThemeFragment--->" + action);
            if (Intent.ACTION_UNINSTALL_PACKAGE.equals(action)
                    || Intent.ACTION_PACKAGE_FULLY_REMOVED.equals(action)) {
                String packageName = intent.getDataString().substring(8);
                boolean isOurLocker = InstallLocker.isOurPakcageName(packageName);

                if (isOurLocker) {
                    themeUninstallListener(packageName);
                }
                // }
            } else if (Intent.ACTION_INSTALL_PACKAGE.equals(action)
                    || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                String packageName = intent.getDataString().substring(8);
                boolean isOurLocker = InstallLocker.isOurLocker(context.getPackageManager(), packageName);
                if (isOurLocker) {
                    if (!inLocalList(packageName)) {
                        themeInstallListener(packageName);
                    }
                }
            }
        }
    };
    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            String packageName = SettingsHelper.getCurrentTheme(mContext);
            onUpdateListener(packageName);
        }
    };

    public void themeInstallListener(String packageName) {
        try {
            PackageManager packageManager = getActivity().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            InstallLocker installLocker = new InstallLocker();
            installLocker.firstInstallTime = packageInfo.firstInstallTime;
            installLocker.lastUpdateTime = packageInfo.lastUpdateTime;
            installLocker.packageName = packageInfo.packageName;
            installLocker.label = packageManager.getApplicationLabel(packageInfo.applicationInfo)
                    .toString();
            installLocker.versionCode = packageInfo.versionCode;
            installLocker.versionName = packageInfo.versionName;
            installLocker.currentTheme = packageName.equals(SettingsHelper.getCurrentTheme(mContext));
            try {
                installLocker.context = getActivity().createPackageContext(
                        installLocker.packageName, Context.CONTEXT_IGNORE_SECURITY);
            } catch (Exception e) {
                Util.printException(e);
            }
            mAllLocalThemes.add(installLocker);
            Collections.sort(mAllLocalThemes, mDescendComparator);
            mLocalThemeGridViewAdapter.notifyDataSetChanged();
        } catch (NameNotFoundException e) {
            Util.printException(e);
        }
    }

    public boolean inLocalList(String packageName) {
        boolean in = false;
        for (InstallLocker installLocker : mAllLocalThemes) {
            if (installLocker.packageName.equals(packageName)) {
                in = true;
                break;
            }
        }
        return in;
    }

    public void themeUninstallListener(String packageName) {
        for (InstallLocker installLocker : mAllLocalThemes) {
            if (packageName.equals(installLocker.packageName)) {
                mAllLocalThemes.remove(installLocker);
                if (installLocker.packageName.equals(SettingsHelper.getCurrentTheme(getActivity()))) {
                    mAllLocalThemes.get(0).currentTheme = true;
                    SettingsHelper.putCurrentTheme(mContext, Constants.SKY_LOCKER_DEFAULT_THEME);
                }
                mLocalThemeGridViewAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.local_theme, null);
        mLocalThemeGridView = (GridView) view.findViewById(R.id.gridview);
        mLocalThemeGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(mContext, LocalThemeDetailActivity.class);
                InstallLocker lockInfo = mAllLocalThemes.get(position);
                intent.putExtra("packageName", lockInfo.packageName);
                mContext.startActivity(intent);

            }

        });
        mLocalThemeGridView.setAdapter(mLocalThemeGridViewAdapter);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadLocalThemeTask != null && !mLoadLocalThemeTask.isCancelled()) {
            mLoadLocalThemeTask.cancel(true);
//            Log.e("mLoadLocalThemeTask", "ondestory cancle");
        }
        mContext.getContentResolver().unregisterContentObserver(mObserver);
        mContext.unregisterReceiver(themeInstallReciever);
// LockApplication.removeOnListChangedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void onLoadingCompeleted(Group<InstallLocker> themes) {
        mAllLocalThemes.clear();
        mAllLocalThemes.addAll(themes);
        Collections.sort(mAllLocalThemes, mDescendComparator);
        mLocalThemeGridViewAdapter.setGroup(mAllLocalThemes);
        mLocalThemeGridViewAdapter.notifyDataSetChanged();
    }

    static class LoadLocalThemeTask extends AsyncTask<Void, Void, Group<InstallLocker>> {
        private Exception exception;
        private WeakReference<LocalThemeFragment> localThemeFragment;
        private WeakReference<Context> mContext;

        LoadLocalThemeTask(LocalThemeFragment localThemeFragment) {
            this.localThemeFragment = new WeakReference<LocalThemeFragment>(localThemeFragment);
            mContext = new WeakReference<Context>(localThemeFragment.getActivity());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Group<InstallLocker> doInBackground(Void... params) {
            if (mContext != null && mContext.get() != null) {
                String currentTheme = SettingsHelper.getCurrentTheme(mContext.get());
                Group<InstallLocker> installLockers = new Group<InstallLocker>();
                PackageManager packageManager = mContext.get().getPackageManager();
                List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
                InstallLocker innerLocker = new InstallLocker();

                if (currentTheme.equals(Constants.SKY_LOCKER_DEFAULT_THEME)) {
                    innerLocker.currentTheme = true;
                } else {
                    innerLocker.currentTheme = false;
                }
                PackageInfo packageInfo1 = null;
                try {
                    packageInfo1 = mContext.get().getPackageManager()
                            .getPackageInfo(mContext.get().getPackageName(), 0);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                innerLocker.firstInstallTime = packageInfo1.firstInstallTime;
                innerLocker.lastUpdateTime = packageInfo1.lastUpdateTime;
                innerLocker.packageName = Constants.SKY_LOCKER_DEFAULT_THEME;
                innerLocker.label = "Default";
                innerLocker.versionCode = 1;
                innerLocker.versionName = "1.0";
                innerLocker.context = null;
                long length = new File(packageInfo1.applicationInfo.publicSourceDir).length();
                innerLocker.sizeStr = StringUtils.friendly_appsize(length);
                installLockers.add(innerLocker);
                for (PackageInfo packageInfo : packageInfos) {
                    if (InstallLocker.isOurLocker(packageManager, packageInfo.packageName)) {
                        InstallLocker installLocker = new InstallLocker();
                        if (currentTheme.equals(packageInfo.packageName)) {
                            installLocker.currentTheme = true;
                        } else {
                            installLocker.currentTheme = false;
                        }
                        installLocker.firstInstallTime = packageInfo.firstInstallTime;
                        installLocker.lastUpdateTime = packageInfo.lastUpdateTime;
                        installLocker.packageName = packageInfo.packageName;
                        installLocker.label = packageManager.getApplicationLabel(packageInfo.applicationInfo)
                                .toString();
                        installLocker.versionCode = packageInfo.versionCode;
                        installLocker.versionName = packageInfo.versionName;
                        long length1 = new File(packageInfo.applicationInfo.publicSourceDir).length();
                        innerLocker.sizeStr = StringUtils.friendly_appsize(length1);
                        try {
                            installLocker.context = mContext.get().createPackageContext(
                                    installLocker.packageName, Context.CONTEXT_IGNORE_SECURITY);
                        } catch (NameNotFoundException e) {
                            Util.printException(e);
                        }
                        installLockers.add(installLocker);
                    }
                }
                return installLockers;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Group<InstallLocker> themes) {

            super.onPostExecute(themes);
            if (themes != null) {
                if (localThemeFragment != null && localThemeFragment.get() != null
                        && mContext != null && mContext.get() != null)
                    if (exception != null) {
                        ToastMaster.makeText(mContext.get(), R.string.error_divice_not_found,
                                Toast.LENGTH_SHORT);
                    }
                if (localThemeFragment != null && localThemeFragment.get() != null) {
                    localThemeFragment.get().onLoadingCompeleted(themes);
                }

            }
        }

    }

    public void onUpdateListener(String packageName) {
        for (InstallLocker installLocker : mAllLocalThemes) {
            if (installLocker.packageName.equals(packageName)) {
                installLocker.currentTheme = true;
            } else {
                installLocker.currentTheme = false;

            }
        }
        mLocalThemeGridViewAdapter.setGroup(mAllLocalThemes);//
        mLocalThemeGridViewAdapter.notifyDataSetChanged();
    }

}
