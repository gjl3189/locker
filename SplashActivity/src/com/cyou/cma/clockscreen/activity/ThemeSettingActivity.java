package com.cyou.cma.clockscreen.activity;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.sqlite.ProviderHelper;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.SettingsHelper;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.material.LImageButton;
import com.cyou.cma.clockscreen.widget.material.PreferenceCheckBox;
import com.cyou.cma.clockscreen.widget.material.PreferenceCheckBox.OnLPreferenceSwitchListener;
import com.cyou.cma.clockscreen.widget.material.PreferenceNormal;
import com.umeng.analytics.MobclickAgent;

/**
 * 单个主题设置界面
 * 
 * @author jiangbin
 */
public class ThemeSettingActivity extends BaseActivity implements
        OnClickListener, OnLPreferenceSwitchListener {

    private LImageButton mImageButtomLeft;// 返回按钮
    private LImageButton mImageButtonRight;// 设置按钮
    private TextView mTextViewTitle;

    private PreferenceNormal mWallpaperPreference;
    private PreferenceCheckBox mSoundCheckBox;
    private PreferenceCheckBox mVibrateCheckBox;

    private String currentPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        SystemUIStatusUtil.onCreate(this, this.getWindow());
        setContentView(R.layout.activity_theme_settings);
        currentPackageName = SettingsHelper.getCurrentTheme(this);
        if (currentPackageName.equals(Constants.SKY_LOCKER_DEFAULT_THEME)) {
            mSupportSound = getResources().getBoolean(R.bool.support_sound);
            mSupportWallpaper = getResources().getBoolean(R.bool.support_wallpaper);
        } else {
            try {
                Context mThemeContext = createPackageContext(currentPackageName, Context.CONTEXT_IGNORE_SECURITY
                        | Context.CONTEXT_INCLUDE_CODE);
                Resources res = mThemeContext.getResources();
                mSupportSound = res.getBoolean(res.getIdentifier("support_sound",
                        "bool", currentPackageName));
                mSupportWallpaper = res.getBoolean(res.getIdentifier("support_wallpaper",
                        "bool", currentPackageName));
            } catch (Exception e) {
                Util.printException(e);
            }
        }
        initView();
        if (SystemUIStatusUtil.isStatusBarTransparency(mContext)) {
            findViewById(R.id.root).setPadding(0, ImageUtil.getStatusBarHeight(mContext), 0, 0);
        }
    }

    private void initView() {
        mImageButtomLeft = (LImageButton) findViewById(R.id.btn_left);
        mImageButtonRight = (LImageButton) findViewById(R.id.btn_right);
        mTextViewTitle = (TextView) findViewById(R.id.tv_title);

        mWallpaperPreference = (PreferenceNormal) findViewById(R.id.themesettings_wallpaper);
        mWallpaperPreference.setOnClickListener(this);
        mSoundCheckBox = (PreferenceCheckBox) findViewById(R.id.themesettings_sound_switch);
        mSoundCheckBox.setOnCheckedChangeListener(this);
        mVibrateCheckBox = (PreferenceCheckBox) findViewById(R.id.themesettings_vibrate_switch);
        mVibrateCheckBox.setOnCheckedChangeListener(this);
        if (isEnableSetWallpaper()) {
            mWallpaperPreference.setEnabled(true);
            mWallpaperPreference
                    .setSummary(R.string.settings_lockscreen_wallpaper_summary);
        } else {
            mWallpaperPreference.setEnabled(false);
            mWallpaperPreference
                    .setSummary(R.string.settings_lockscreen_wallpaper_notsuport);
        }

        mImageButtomLeft.setOnClickListener(this);
        mImageButtonRight.setVisibility(View.INVISIBLE);
        mTextViewTitle.setText(R.string.app_name);
    }

    private boolean mSupportSound = false;
    private boolean mSupportWallpaper = false;

    private boolean isEnableSound() {
        return mSupportSound;
    }

    private boolean isEnableSetWallpaper() {
        return mSupportWallpaper;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isEnableSound()) {
            boolean soundEnable = ProviderHelper.getSoundEnable(mContext, currentPackageName);
            mSoundCheckBox.setChecked(soundEnable);
            mSoundCheckBox.setSummary("");
        } else {
            mSoundCheckBox.setEnabled(false);
            mSoundCheckBox.setSummary(R.string.settings_lockscreen_tools_only_theme_ocean);
        }
        mVibrateCheckBox.setChecked(ProviderHelper.getVibrateEnable(mContext, currentPackageName));
    }

    @Override
    public void onLCheckedChanged(View v, boolean isChecked) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("status", String.valueOf(isChecked));
        switch (v.getId()) {
            case R.id.themesettings_sound_switch:
                MobclickAgent.onEvent(mContext, Util.Statistics.KEY_UNLOCK_SOUND, map);
                ProviderHelper.updateSound(mContext, currentPackageName, isChecked ? 1 : 0);
                break;
            case R.id.themesettings_vibrate_switch:
                MobclickAgent.onEvent(mContext, Util.Statistics.KEY_UNLOCK_VIBRATE, map);
                ProviderHelper.updateVibrate(mContext, currentPackageName, isChecked ? 1 : 0);
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                finish();
                break;
            case R.id.themesettings_wallpaper:
                Intent intent = new Intent(this, WallpaperActivity.class);
                intent.putExtra(Constants.C_EXTRAS_PACKAGE, currentPackageName);
                startActivity(new Intent(intent));
                break;
        }
    }

}
