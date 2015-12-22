package com.cyou.cma.clockscreen.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.sqlite.ProviderHelper;
import com.cyou.cma.clockscreen.util.FileUtil;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.SettingsHelper;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.material.LImageButton;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

public class WallpaperActivity extends BaseActivity implements OnClickListener {
    // private final String TAG = "WallpaperActivity";
    private Context mContext;

    private LImageButton mImageButtonBack;

    private TextView mTextViewTitle;

    private ImageView mImageViewChoosePhoto;
    private ImageView mImageViewWallPaper;
    private ImageView mImageRestoreDefault;
    private String mPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUIStatusUtil.onCreate(this, this.getWindow());
        setContentView(R.layout.activity_wallpaper);
        mContext = this;
        mPackageName = getIntent().getStringExtra(Constants.C_EXTRAS_PACKAGE);
        if (mPackageName == null || "".equals(mPackageName)) {
            mPackageName = SettingsHelper.getCurrentTheme(this);
        }
        initView();
        if (FileUtil.isExternalStorageEnable()) {
            FileUtil.ensureExists(FileUtil.FILEPATH_WALLPAPER);
            FileUtil.ensureExists(FileUtil.FILEPATH_THUMBNAIL_WALLPAPER);
            refreshWallpaperType();
        } else {
            Toast.makeText(this, R.string.SdCard_Notexisting,
                    Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }
        
        if (SystemUIStatusUtil.isStatusBarTransparency(mContext)) {
            findViewById(R.id.root).setPadding(0, ImageUtil.getStatusBarHeight(mContext), 0, 0);
        }
    }

    private void initView() {
        mImageButtonBack = (LImageButton) findViewById(R.id.btn_left);
        mTextViewTitle = (TextView) findViewById(R.id.tv_title);
        mImageButtonBack.setOnClickListener(this);
        mImageButtonBack.setVisibility(View.VISIBLE);
        mTextViewTitle.setText(R.string.settings_lockscreen_wallpaper);

        mImageViewChoosePhoto = (ImageView) findViewById(R.id.wallpaper_choose_photo);
        mImageViewChoosePhoto.setOnClickListener(mOnClickListener);

        mImageViewWallPaper = (ImageView) findViewById(R.id.wallpaper_system);
        mImageViewWallPaper.setOnClickListener(mOnClickListener);

        mImageRestoreDefault = (ImageView) findViewById(R.id.wallpaper_restore);
        mImageRestoreDefault.setOnClickListener(mOnClickListener);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.wallpaper_choose_photo:
                    Intent intent = new Intent(mContext,
                            CustomWallpaperActivity.class);
                    intent.putExtra(Constants.C_EXTRAS_PACKAGE, mPackageName);
                    startActivity(intent);
                    break;
                case R.id.wallpaper_system:

                    if (v.isSelected()) {
                        return;
                    }
// if (LockApplication.getDexClassLoader()
// .isSupportBaseThemeSetting(mPackageName)) {

                    ProviderHelper.updateWallpaper(mContext,
                            mPackageName, Constants.C_WALLPAPER_SYSTEM, null);
// } else {
//
// SettingsHelper.setWallpaperPath(mContext, "");
// SettingsHelper.setWallpaperType(mContext,
// Constants.C_WALLPAPER_SYSTEM);
// }

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("theme", "system");
                    MobclickAgent.onEvent(mContext,
                            Util.Statistics.KEY_WALLPAPER_SWITCH, map);

                    refreshWallpaperType();
                    Toast.makeText(mContext,
                            R.string.settings_theme_or_wallpaper_success,
                            Toast.LENGTH_SHORT).show();
                    break;
                case R.id.wallpaper_restore:
                    if (v.isSelected()) {
                        return;
                    }
// if (LockApplication.getDexClassLoader()
// .isSupportBaseThemeSetting(mPackageName)) {
                    ProviderHelper.updateWallpaper(mContext,
                            mPackageName, Constants.C_WALLPAPER_THEME, null);
// } else {
// SettingsHelper.setWallpaperPath(mContext, "");
// SettingsHelper.setWallpaperType(mContext,
// Constants.C_WALLPAPER_THEME);
// }

                    HashMap<String, String> map1 = new HashMap<String, String>();
                    map1.put("theme", "restore");
                    MobclickAgent.onEvent(mContext,
                            Util.Statistics.KEY_WALLPAPER_SWITCH, map1);
                    refreshWallpaperType();
                    Toast.makeText(mContext,
                            R.string.settings_theme_or_wallpaper_success,
                            Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }

        }
    };

    private void refreshWallpaperType() {
        mImageViewChoosePhoto.setSelected(false);
        mImageViewWallPaper.setSelected(false);
        mImageRestoreDefault.setSelected(false);
        int wallpaperType = Constants.C_WALLPAPER_THEME;
        // if (LockApplication.getDexClassLoader().isSupportBaseThemeSetting(
        // mPackageName)) {
        wallpaperType = ProviderHelper.getWallpaperType(mContext,
                mPackageName);
        // }else{
        // wallpaperType = SettingsHelper.getWallpaperType(mContext);
        // }

        switch (wallpaperType) {
            case Constants.C_WALLPAPER_THEME:
                mImageRestoreDefault.setSelected(true);
                break;
            case Constants.C_WALLPAPER_GALLERY:
                mImageViewChoosePhoto.setSelected(true);
                break;
            case Constants.C_WALLPAPER_SYSTEM:
                mImageViewWallPaper.setSelected(true);
                break;
            default:
                mImageRestoreDefault.setSelected(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                this.finish();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        refreshWallpaperType();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
