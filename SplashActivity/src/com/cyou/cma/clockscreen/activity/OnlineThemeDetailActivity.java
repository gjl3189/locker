package com.cyou.cma.clockscreen.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adjust.sdk.Adjust;
import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.adapter.OnlineThemeDetailPagerAdapter;
import com.cyou.cma.clockscreen.adapter.ZoomOutPageTransformer2;
import com.cyou.cma.clockscreen.bean.InstallLocker;
import com.cyou.cma.clockscreen.bean.Theme4Play;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.CyStageClickButton;
import com.cyou.cma.clockscreen.widget.LinePageIndicator;
import com.cyou.cma.clockscreen.widget.ViewPagerOnPageChangeListener;
import com.cyou.cma.clockscreen.widget.material.LButton;
import com.cyou.cma.clockscreen.widget.material.LImageButton;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("InlinedApi")
public class OnlineThemeDetailActivity extends Activity implements OnClickListener {

    private RelativeLayout viewPagerContainer;
    private ViewPager mViewPager;
    private LinePageIndicator mLinePageIndicator;
    // add by Jack: add animation
    public CyStageClickButton mImageButtonCrash;
    // end
    public LImageButton mImageButtonSetting;
    public LImageButton mImageButtonBack;

    private OnlineThemeDetailPagerAdapter mPagerAdapter;
// private InstallLocker mInstallLocker;
    private Theme4Play mTheme4Play;

    private TextView mTextViewLockName;
    private LButton mStateButton;

    private BroadcastReceiver themeInstallReciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_INSTALL_PACKAGE.equals(action)
                    || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                String packageName = intent.getDataString().substring(8);
                boolean isOurLocker = InstallLocker.isOurLocker(context.getPackageManager(), packageName);
                if (isOurLocker) {
                    themeInstallListener(packageName);
                }
            }
        }
    };

    public void themeInstallListener(String packageName) {
        Intent intent = new Intent(this, LocalThemeDetailActivity.class);
        intent.putExtra("packageName", packageName);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SystemUIStatusUtil.onCreate(this, this.getWindow());
        
        setContentView(R.layout.activity_lock_detail);
        MobclickAgent.setDebugMode(Util.DEBUG);
        MobclickAgent.onError(this);
        initInstallLocker();
        findViews();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_INSTALL_PACKAGE);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addDataScheme("package");
        registerReceiver(themeInstallReciever, intentFilter);
        if (SystemUIStatusUtil.isStatusBarTransparency(this)) {
            findViewById(R.id.root).setPadding(0, ImageUtil.getStatusBarHeight(this), 0, 0);
        }
    }

    private void initInstallLocker() {
        mTheme4Play = (Theme4Play) getIntent().getSerializableExtra("theme4play");
// String pakcageName = getIntent().getStringExtra("packageName");
// String currentTheme = SettingsHelper.getCurrentTheme(this
// );

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        Adjust.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        Adjust.onResume(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Util.Logjb("task id ", "ThemeDetailActivity onNewIntent " + getTaskId());
    }

    private void findViews() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mLinePageIndicator = (LinePageIndicator) findViewById(R.id.linePageIndicator);
        viewPagerContainer = (RelativeLayout) findViewById(R.id.pager_layout);

        // 初始化 ViewHolder
        mTextViewLockName = (TextView) findViewById(R.id.textview_lockname);
        mStateButton = (LButton) findViewById(R.id.state_button);
        // add by Jack
        mImageButtonCrash = (CyStageClickButton) findViewById(R.id.imageview_crash);
        mImageButtonCrash.setVisibility(View.GONE);
        // end
        mImageButtonSetting = (LImageButton) findViewById(R.id.imageview_setting);
        mImageButtonSetting.setVisibility(View.GONE);
        mImageButtonBack = (LImageButton) findViewById(R.id.imageview_back);
        mStateButton.setOnClickListener(this);
        mImageButtonBack.setOnClickListener(this);
        mTextViewLockName.setText(mTheme4Play.title);
        mStateButton.setText(R.string.state_button_download);
        mStateButton.setBackgroundResource(R.drawable.statebutton_download);
        initViewPager();
    }

    private void initViewPager() {
        mPagerAdapter = new OnlineThemeDetailPagerAdapter(getApplicationContext(), mTheme4Play);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        if (Build.VERSION.SDK_INT >= 11) {
            mViewPager.setPageMargin(1);
            mViewPager.setPageTransformer(true, new ZoomOutPageTransformer2());
        } else {
            mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.page_margin));
        }
        mLinePageIndicator.setViewPager(mViewPager, 1);
        ViewPagerOnPageChangeListener mOnPageChangeListener = new ViewPagerOnPageChangeListener(
                viewPagerContainer);
        mLinePageIndicator.setOnPageChangeListener(mOnPageChangeListener);

        viewPagerContainer.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    return mViewPager.dispatchTouchEvent(event);
                } catch (Exception e) {
                    return false;
                }

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.removeAllViews();
        mPagerAdapter = null;
        unregisterReceiver(themeInstallReciever);
        System.gc();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mViewPager.getLayoutParams().width = (int) (mViewPager.getHeight() * 500 / 888f);

    }

    @Override
    public void onClick(View v) {
        if (v.getTag().toString().equals(mImageButtonBack.getTag().toString())) {
            finish();
        } else if (v.getTag().toString().equals(mStateButton.getTag().toString())) {
            Util.Logjb("OnlineThemeDetailActivity", "OnlineThemeDetailActivity google url--->"
                    + mTheme4Play.googlePlayUrl);
            moveToGooglePlay(mTheme4Play.googlePlayUrl);
        }
    }

    private static final String GOOGLE_PLAY_URL_PREFIX = "https://play.google.com/store/";
    private static final String GOOGLE_PLAY_DEFAULT_URL = "https://play.google.com/store/apps/details?id=null";

    public void moveToGooglePlay(String googlePlayUrl) {
        if (googlePlayUrl == null) return;
        if (!googlePlayUrl.startsWith(GOOGLE_PLAY_URL_PREFIX)) {
            googlePlayUrl = GOOGLE_PLAY_DEFAULT_URL;
        }

        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayUrl));
            browserIntent.setClassName("com.android.vending", "com.android.vending.AssetBrowserActivity");
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(browserIntent);
        } catch (Exception e) {
            Util.printException(e);

            try {
                Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayUrl));
                startActivity(browserIntent2);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
