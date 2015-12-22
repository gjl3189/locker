package com.cyou.cma.clockscreen.fragment;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.AppClient;
import com.cyou.cma.clockscreen.LockApplication;
import com.cyou.cma.clockscreen.NoDataException;
import com.cyou.cma.clockscreen.Urls;
import com.cyou.cma.clockscreen.adapter.OnlineThemeGridViewAdapter4Play;
import com.cyou.cma.clockscreen.bean.Group;
import com.cyou.cma.clockscreen.bean.InstallLocker;
import com.cyou.cma.clockscreen.bean.Theme4Play;
import com.cyou.cma.clockscreen.bean.jsonparser.GroupParser;
import com.cyou.cma.clockscreen.bean.jsonparser.Parser;
import com.cyou.cma.clockscreen.bean.jsonparser.Theme4PlayParser;
import com.cyou.cma.clockscreen.util.HttpUtil;
import com.cyou.cma.clockscreen.util.ToastMaster;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.WidgetHttpLoadView;
import com.cyou.cma.clockscreen.widget.WidgetHttpLoadView.HttpLoadListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
//import com.cyou.cma.clockscreen.bean.Theme;
//import com.cyou.cma.clockscreen.bean.jsonparser.ThemeParser;

@SuppressLint("NewApi")
public class OnlineThemeFragmentGp extends Fragment implements OnTabChangeListener
{

    private PullToRefreshListView mPullToRefreshGridView;
    private OnlineThemeGridViewAdapter4Play mOnlineThemeGridViewAdapter;

    private OnlineThemeLoadTask mOnlineThemeLoadTask;

    private Context mContext;

    private Group<Theme4Play> mOnlineThemes = new Group<Theme4Play>();

    private Theme4Play mLastTheme = null;
    private AppClient mAppClient;
    private WidgetHttpLoadView mHttpView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mOnlineThemeGridViewAdapter = new OnlineThemeGridViewAdapter4Play(mContext);
        mOnlineThemeGridViewAdapter.setGroup(mOnlineThemes);
        mAppClient = new AppClient();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(mNetworkBroadcastReceiver, intentFilter);
        IntentFilter installFilter = new IntentFilter();
        installFilter.addAction(Intent.ACTION_INSTALL_PACKAGE);
        installFilter.addAction(Intent.ACTION_UNINSTALL_PACKAGE);
        installFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        installFilter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        installFilter.addDataScheme("package");
        getActivity().registerReceiver(themeInstallReciever, installFilter);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private BroadcastReceiver themeInstallReciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_UNINSTALL_PACKAGE.equals(action)
                    || Intent.ACTION_PACKAGE_FULLY_REMOVED.equals(action)) {
                String packageName = intent.getDataString().substring(8);
                if (InstallLocker.isOurPakcageName(packageName)) {
                    themeUninstallListener(packageName);
                }
            } else if (Intent.ACTION_INSTALL_PACKAGE.equals(action)
                    || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                String packageName = intent.getDataString().substring(8);
                if (InstallLocker.isOurLocker(context.getPackageManager(), packageName)) {
                    themeInstallListener(packageName);
                }
            }
        }
    };

    public void themeInstallListener(String packageName) {
        if (mOnlineThemes != null) {
            for (Theme4Play theme4Play : mOnlineThemes) {
                if (theme4Play.packageName.equals(packageName)) {
                    theme4Play.hasDownloaded = true;
                    mOnlineThemeGridViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public void themeUninstallListener(String packageName) {

        if (mOnlineThemes != null) {
            for (Theme4Play theme4Play : mOnlineThemes) {
                if (theme4Play.packageName.equals(packageName)) {
                    theme4Play.hasDownloaded = false;
                    mOnlineThemeGridViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }

    }

    BroadcastReceiver mNetworkBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (HttpUtil.isNetworkAvailable(mContext)) {

                    if (mLastTheme == null && !mPullToRefreshGridView.isRefreshing()
                            && mHttpView.getStatus() != WidgetHttpLoadView.HTTPVIEW_LOADING) {
                        if (HttpUtil.isNetworkAvailable(mContext)) {
                            mPullToRefreshGridView.setMode(Mode.PULL_FROM_END);
                            mHttpView.setStatus(WidgetHttpLoadView.HTTPVIEW_LOADING);
                            getThemesFromServer();
                        } else {
                            showNetworkNotavailable();
                        }
                    }

                }

            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_theme, null);
        mHttpView = (WidgetHttpLoadView) view.findViewById(R.id.httpview);

        mPullToRefreshGridView = (PullToRefreshListView) view
                .findViewById(R.id.online_theme_listview);
        // add by Jack
        ViewGroup vg = mPullToRefreshGridView.getRefreshableView();
        if (vg != null) {
            vg.setClipChildren(false);
        }
        // end
        mHttpView.init(mPullToRefreshGridView, new HttpLoadListener() {

            @Override
            public void reLoad() {
                Util.Logcs("OnlineTheme", "reLoad");
                getThemesFromServer();
            }
        });
        mPullToRefreshGridView.setAdapter(mOnlineThemeGridViewAdapter);
        mPullToRefreshGridView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (!HttpUtil.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext, getString(R.string.setnetwork), Toast.LENGTH_LONG)
                            .show();
                    mPullToRefreshGridView.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            mPullToRefreshGridView.onRefreshComplete();
                        }
                    }, 300);

                } else {
                    getThemesFromServer();
                }

            }
        });

        if (mLastTheme == null) {
            if (HttpUtil.isNetworkAvailable(mContext)) {
                mHttpView.setStatus(WidgetHttpLoadView.HTTPVIEW_LOADING);
                mPullToRefreshGridView.setRefreshing();
                getThemesFromServer();
            } else {
                showNetworkNotavailable();
            }
        }
        return view;
    }

    private void showNetworkNotavailable() {
        mHttpView.setStatus(WidgetHttpLoadView.HTTPVIEW_NONETWORK);
    }

    private void getThemesFromServer() {
        if (!HttpUtil.isNetworkAvailable(mContext)) {
            ToastMaster.makeText(mContext, R.string.http_no_network_error, Toast.LENGTH_SHORT);
            mHttpView.setStatus(WidgetHttpLoadView.HTTPVIEW_NONETWORK);
            return;
        }
        mOnlineThemeLoadTask = new OnlineThemeLoadTask(this, mAppClient, mLastTheme);
        if (Build.VERSION.SDK_INT >= 11) {
            mOnlineThemeLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mOnlineThemeLoadTask.execute();
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void onLoadingException(Exception exception) {
        if (mOnlineThemes.size() == 0) {
            mHttpView.setStatus(WidgetHttpLoadView.HTTPVIEW_NONETWORK);
        }
        int resId;
        if (exception instanceof UnknownHostException) {
            resId = R.string.cannot_connect_server;
        } else if (exception instanceof ConnectTimeoutException) {
            resId = R.string.timeout_request;

        } else if (exception instanceof TimeoutException) {
            resId = R.string.timeout_request;
        } else if (exception instanceof IOException) {
            resId = R.string.network_exception;
        } else if (exception instanceof NoDataException) {
            resId = R.string.no_more_data;
            mPullToRefreshGridView.onRefreshComplete();
            mPullToRefreshGridView.setMode(Mode.DISABLED);
        } else {
            resId = R.string.request_exception;
        }
        ToastMaster.makeText(mContext, resId, Toast.LENGTH_SHORT);
        mPullToRefreshGridView.onRefreshComplete();
    }

    public void onLoadingCompelted(Group<Theme4Play> result) {
        mHttpView.setStatus(WidgetHttpLoadView.HTTPVIEW_DONE);
        if (result != null && result.size() != 0) {
            mLastTheme = result.get(result.size() - 1);
// for(T)
            // TODO 解决重复的问题
            for (Theme4Play theme4Play : result) {
                if (!mOnlineThemes.contains(theme4Play)) {
                    mOnlineThemes.add(theme4Play);
                }
            }

            mOnlineThemeGridViewAdapter.notifyDataSetChanged();

        } else {
            ToastMaster.makeText(mContext, R.string.no_more_data, Toast.LENGTH_SHORT);
            mPullToRefreshGridView.onRefreshComplete();
            mPullToRefreshGridView.setMode(Mode.DISABLED);
        }
        mPullToRefreshGridView.onRefreshComplete();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOnlineThemeLoadTask != null && !mOnlineThemeLoadTask.isCancelled()) {
            mOnlineThemeLoadTask.cancel(true);
        }
        getActivity().unregisterReceiver(mNetworkBroadcastReceiver);
        getActivity().unregisterReceiver(themeInstallReciever);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onUnselected() {

    }

    @Override
    public void onSelected() {
        if (mLastTheme == null && !mPullToRefreshGridView.isRefreshing()) {
            if (HttpUtil.isNetworkAvailable(mContext)) {
                mPullToRefreshGridView.setMode(Mode.PULL_FROM_END);
                mHttpView.setStatus(WidgetHttpLoadView.HTTPVIEW_LOADING);
                getThemesFromServer();
            } else {
                showNetworkNotavailable();
            }
        }
    }

    static class OnlineThemeLoadTask extends LoadingTask<Group<Theme4Play>> {
        private Theme4Play mLastTheme;
        private WeakReference<OnlineThemeFragmentGp> onlineThemeFragment;

        private OnlineThemeLoadTask(AppClient appClient) {
            super(appClient);
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        public OnlineThemeLoadTask(OnlineThemeFragmentGp onlineThemeFragment, AppClient appClient,
                Theme4Play lastTheme) {
            this(appClient);
            this.onlineThemeFragment = new WeakReference<OnlineThemeFragmentGp>(onlineThemeFragment);
            this.mLastTheme = lastTheme;
        }

        @Override
        protected Group<Theme4Play> doInBackground(Void... params) {
            Group<Theme4Play> result = super.doInBackground(params);

            if (result != null && result.size() != 0) {
                mLastTheme = result.get(result.size() - 1);
                for (Theme4Play theme : result) {// TODO 这步操作放到线程中
                    theme.hasDownloaded = InstallLocker.isOurLocker(LockApplication.getInstance()
                            .getPackageManager(), theme.packageName);
// Util.appInstalled(theme.packageName);
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Group<Theme4Play> result) {
            if (onlineThemeFragment != null && onlineThemeFragment.get() != null) {
                // onlineThemeFragment.get().mPullToRefreshGridView.onRefreshComplete();
                if (exception != null) {
                    onlineThemeFragment.get().onLoadingException(exception);
                    return;
                }
                onlineThemeFragment.get().onLoadingCompelted(result);
            }

        }

        @Override
        public HttpRequestBase createHttpRequestBase() {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            if (mLastTheme != null) {
                nameValuePairs.add(new BasicNameValuePair(Urls.PARAM_THEMEID, ""
                        + mLastTheme.lockscreenThemeId));
                nameValuePairs.add(new BasicNameValuePair(Urls.PARAM_AUDITTIME, ""
                        + mLastTheme.auditTime));
            }
            nameValuePairs.add(new BasicNameValuePair(Urls.PARAM_LIMIT, Urls.DEFAULT_PAGE_SIZE
                    + ""));
            nameValuePairs.add(new BasicNameValuePair(Urls.PARAM_LANGUAGE, Urls.RequestUtil
                    .getLaunguage()));
            nameValuePairs.add(new BasicNameValuePair(Urls.PARAM_COUNTRY, Urls.RequestUtil
                    .getLocal()));
            nameValuePairs.add(new BasicNameValuePair(Urls.PARAM_VERSION, Urls.RequestUtil
                    .getVersionCode(onlineThemeFragment.get().getActivity())));

            HttpGet httpGet = mAppClient.makeHttpGet(Urls.getNewThemesUrlGp(), nameValuePairs);
            return httpGet;
        }

        @Override
        public Parser<Group<Theme4Play>> createParser() {
            return new GroupParser<Theme4Play>(new Theme4PlayParser(), "themes");
        }

    }

}
