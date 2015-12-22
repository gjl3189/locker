package com.cyou.cma.clockscreen.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.adapter.WallpaperAdapter;
import com.cyou.cma.clockscreen.bean.WallpaperBean;
import com.cyou.cma.clockscreen.imagecrop.Crop;
import com.cyou.cma.clockscreen.sqlite.ProviderHelper;
import com.cyou.cma.clockscreen.sqlite.WallPaperProvider;
import com.cyou.cma.clockscreen.util.FileUtil;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.SettingsHelper;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.CustomAlertDialog;
import com.cyou.cma.clockscreen.widget.material.LImageButton;
import com.umeng.analytics.MobclickAgent;

public class CustomWallpaperActivity extends BaseActivity implements
		OnClickListener {
	private Context mContext;
	private LImageButton leftBtn;
	private LImageButton rightBtn;
	private TextView titleText;

	private GridView mGridView;
	private WallpaperAdapter mWallpaperAdapter;
	private List<WallpaperBean> mWallpaperBeans = new ArrayList<WallpaperBean>();

	private CustomAlertDialog mDeleteDialog;

	private long saveTime = -1;
	private String suffix = ".jpg";

	private boolean isEditMode = false;
	private ContentResolver mContentResolver;
	private String mPackageName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SystemUIStatusUtil.onCreate(this, this.getWindow());
		setContentView(R.layout.activity_custom_wallpaper);
		mContext = this;
		mContentResolver = getContentResolver();

		mPackageName = getIntent().getStringExtra(Constants.C_EXTRAS_PACKAGE);
		if (mPackageName == null || "".equals(mPackageName)) {
			mPackageName = SettingsHelper.getCurrentTheme(this);
		}
		mWallpaperAdapter = new WallpaperAdapter(mContext, mWallpaperBeans,
				mPackageName);
		initView();
		if (FileUtil.isExternalStorageEnable()) {
			FileUtil.ensureExists(FileUtil.FILEPATH_WALLPAPER);
			FileUtil.ensureExists(FileUtil.FILEPATH_THUMBNAIL_WALLPAPER);
			checkProvideWallpaper();
			cleanInvaildWallpaper();
			refreshWallpaper();
		} else {
			Toast.makeText(this, R.string.SdCard_Notexisting,
					Toast.LENGTH_SHORT).show();
			this.finish();
			return;
		}

		if (SystemUIStatusUtil.isStatusBarTransparency(mContext)) {
			findViewById(R.id.root).setPadding(0,
					ImageUtil.getStatusBarHeight(mContext), 0, 0);
		}
	}

	private String PROVIDE_WALLPAPER_NAME = "default_wallpaper";

	/**
	 * 检查内置的壁纸信息和文件是否完整
	 */
	private void checkProvideWallpaper() {
		String defaultPath = FileUtil.FILEPATH_WALLPAPER
				+ PROVIDE_WALLPAPER_NAME + suffix;
		String defaultThumbPath = FileUtil.FILEPATH_THUMBNAIL_WALLPAPER
				+ PROVIDE_WALLPAPER_NAME + suffix;
		File defaultWallpaperFile = new File(defaultPath);
		File defaultThumbFile = new File(defaultThumbPath);
		if (!defaultWallpaperFile.exists()) {
			FileUtil.copyFile(
					getResources().openRawResource(
							R.drawable.default_wallpaper1), defaultPath);
		}
		if (!defaultThumbFile.exists()) {
			int thumbWidth = Util.getPreferenceInt(mContext,
					Util.SAVE_KEY_SCREEN_WIDTH, 720) / 2;
			Bitmap bitmapThumb = Bitmap.createScaledBitmap(ImageUtil
					.readBitmapByStream(this, R.drawable.default_wallpaper1),
					thumbWidth, thumbWidth * 3 / 2, true);
			try {
				FileUtil.saveBitmap(bitmapThumb,
						FileUtil.FILEPATH_THUMBNAIL_WALLPAPER
								+ PROVIDE_WALLPAPER_NAME + suffix);
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		WallpaperBean wallpaperBean = new WallpaperBean();
		wallpaperBean.setThumbPath(FileUtil.FILEPATH_THUMBNAIL_WALLPAPER
				+ PROVIDE_WALLPAPER_NAME + suffix);
		wallpaperBean.setWallpaperPath(FileUtil.FILEPATH_WALLPAPER
				+ PROVIDE_WALLPAPER_NAME + suffix);
		wallpaperBean.setTime(System.currentTimeMillis());
		wallpaperBean.setIsProvide(1);

		mContentResolver.insert(WallPaperProvider.getWallpaperUri(mContext),
				wallpaperBean.getContentValues());
	}

	/**
	 * 清楚无效的壁纸文件
	 */
	private void cleanInvaildWallpaper() {
		List<WallpaperBean> tempList = ProviderHelper.getWallpapers(mContext,
				mPackageName);
		File wallpaperDir = new File(FileUtil.FILEPATH_WALLPAPER);
		try {
			for (File file : wallpaperDir.listFiles()) {
				if (file.getName().equals(
						(new File(FileUtil.FILEPATH_THUMBNAIL_WALLPAPER))
								.getName()))
					continue;
				boolean vaild = false;
				for (WallpaperBean bean : tempList) {
					if (bean.getWallpaperPath().equals(file.getPath())) {
						vaild = true;
						break;
					}
				}
				if (!vaild) {
					file.delete();
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		File thumbsDir = new File(FileUtil.FILEPATH_THUMBNAIL_WALLPAPER);
		try {
			for (File file : thumbsDir.listFiles()) {
				boolean vaild = false;
				for (WallpaperBean bean : tempList) {
					if (bean.getThumbPath().equals(file.getAbsolutePath())) {
						vaild = true;
						break;
					}
				}
				if (!vaild) {
					file.delete();
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}

	}

	private void refreshWallpaper() {
		mWallpaperBeans.clear();

		mWallpaperBeans.addAll(ProviderHelper.getWallpapers(mContext,
				mPackageName));
		mWallpaperAdapter.notifyDataSetChanged();
		boolean canDelete = false;
		for (WallpaperBean b : mWallpaperBeans) {
			if (b.getIsProvide() == 0 && b.getIsDefault() == 0) {
				canDelete = true;
			}
		}
		rightBtn.setVisibility(canDelete ? View.VISIBLE : View.GONE);
		if (isEditMode) {
			if (!canDelete) {
				isEditMode = false;
				mWallpaperAdapter.setEditMode(isEditMode);
				rightBtn.setImageResource(isEditMode ? R.drawable.icon_header_themedetail_delete
						: R.drawable.selector_header_edit);
				rightBtn.setEnabled(isEditMode ? false : true);
			} else {
				rightBtn.setEnabled(false);
				for (WallpaperBean wallpaperBean : mWallpaperBeans) {
					if (wallpaperBean.isSelected()) {
						rightBtn.setEnabled(true);
						break;
					}
				}
			}
		}
	}

	private void initView() {
		leftBtn = (LImageButton) findViewById(R.id.btn_left);
		rightBtn = (LImageButton) findViewById(R.id.btn_right);
		rightBtn.setImageResource(R.drawable.selector_header_edit);
		rightBtn.setOnClickListener(this);
		titleText = (TextView) findViewById(R.id.tv_title);
		leftBtn.setOnClickListener(this);
		leftBtn.setVisibility(View.VISIBLE);
		titleText.setText(R.string.settings_lockscreen_wallpaper);

		mGridView = (GridView) findViewById(R.id.wallpaper_gridview);
		mGridView.setAdapter(mWallpaperAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (isEditMode) {
					WallpaperBean bean = mWallpaperBeans.get(position);
					if (bean.getIsDefault() == 1 || bean.getIsProvide() == 1)
						return;
					bean.setSelected(!mWallpaperBeans.get(position)
							.isSelected());
					rightBtn.setEnabled(false);
					for (WallpaperBean wallpaperBean : mWallpaperBeans) {
						if (wallpaperBean.isSelected()) {
							rightBtn.setEnabled(true);
							break;
						}
					}
					mWallpaperAdapter.notifyDataSetChanged();
				} else {
					if (position == mWallpaperBeans.size()) {
						// choosePotho();
						saveTime = System.currentTimeMillis();
						Crop.pickImage(CustomWallpaperActivity.this);
						return;
					}
					String wallpaperPath = mWallpaperBeans.get(position)
							.getWallpaperPath();

					if (wallpaperPath.equals(ProviderHelper.getWallpaperPath(
							mContext, mPackageName))) {
						return;
					} else {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("theme", "custom");
						MobclickAgent.onEvent(mContext,
								Util.Statistics.KEY_WALLPAPER_SWITCH, map);
						// if (LockApplication.getDexClassLoader()
						// .isSupportBaseThemeSetting(mPackageName)) {
						ProviderHelper.updateWallpaper(mContext, mPackageName,
								Constants.C_WALLPAPER_GALLERY, mWallpaperBeans
										.get(position).getWallpaperPath());
						// } else {
						// SettingsHelper.setWallpaperType(mContext,
						// Constants.C_WALLPAPER_GALLERY);
						// SettingsHelper.setWallpaperPath(mContext,
						// wallpaperPath);
						// }
						refreshWallpaper();
						Toast.makeText(mContext,
								R.string.settings_theme_or_wallpaper_success,
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			if (isEditMode) {
				changedEditMode(!isEditMode);
			} else {
				this.finish();
			}
			break;
		case R.id.btn_right:
			if (isEditMode) {// do delete
				int selectedCount = 0;
				for (WallpaperBean bean : mWallpaperBeans) {
					if (bean.isSelected()) {
						selectedCount++;
					}
				}
				if (selectedCount == 0) {
					// changedEditMode();
					return;
				}
				mDeleteDialog = new CustomAlertDialog.Builder(mContext)
						.setTitle(R.string.dialog_tips)
						.setMessage(
								getString(R.string.dialog_delete_wallpaper_msg,
										selectedCount))
						.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										File tempFile = null;
										for (WallpaperBean bean : mWallpaperBeans) {
											if (bean.isSelected()) {
												tempFile = new File(bean
														.getWallpaperPath());
												if (tempFile.exists())
													tempFile.delete();
												tempFile = new File(bean
														.getThumbPath());
												if (tempFile.exists())
													tempFile.delete();

												mContentResolver.delete(
														WallPaperProvider
																.getWallpaperUri(mContext),
														WallPaperProvider.KEY_TIME
																+ "=?",
														new String[] { ""
																+ bean.getTime() });
												if (bean.getIsDefault() == 1) {
													// Settings.System.putInt(
													// mContext.getContentResolver(),
													// Constants.C_SAVE_KEY_IN_USE_WALLPAPER_TYPE,
													// Constants.C_WALLPAPER_THEME);
													ProviderHelper
															.updateWallpaper(
																	mContext,
																	mPackageName,
																	Constants.C_WALLPAPER_THEME,
																	null);
												}
											}
										}
										changedEditMode(false);
										refreshWallpaper();
									}
								}).setNegativeButton(R.string.cancel, null)
						.create();
				if (!mDeleteDialog.isShowing()) {
					mDeleteDialog.show();
				}
			} else {
				changedEditMode(!isEditMode);
			}
			break;
		default:
			break;
		}
	}

	private void changedEditMode(boolean mode) {
		isEditMode = mode;
		mWallpaperAdapter.setEditMode(isEditMode);
		rightBtn.setImageResource(isEditMode ? R.drawable.icon_header_themedetail_delete
				: R.drawable.selector_header_edit);
		rightBtn.setEnabled(isEditMode ? false : true);
		refreshWallpaper();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
		case Crop.REQUEST_PICK:// 如果是直接从相册获取
			if (resultCode != Activity.RESULT_OK)
				return;
			if (data == null || data.getData() == null)
				return;
			// CorePicture(data.getData());
			CropPicture(data.getData());
			break;
		case Crop.REQUEST_CROP:// 取得裁剪后的图片
			if (data != null) {
				File saveFile = new File(FileUtil.FILEPATH_WALLPAPER + saveTime
						+ suffix);
				// File saveFile = new
				// File(data.getStringExtra("savedJpgPath"));
				if (!saveFile.exists())
					return;
				int thumbWidth = Util.getPreferenceInt(mContext,
						Util.SAVE_KEY_SCREEN_WIDTH, 720) / 2;
				Bitmap bitmapThumb = FileUtil.compressBySize(
						saveFile.getAbsolutePath(), thumbWidth,
						thumbWidth * 3 / 2);
				try {
					FileUtil.saveBitmap(bitmapThumb,
							FileUtil.FILEPATH_THUMBNAIL_WALLPAPER + saveTime
									+ suffix);
				} catch (Exception e) {
					// e.printStackTrace();
				}
				WallpaperBean wallpaperBean = new WallpaperBean();
				wallpaperBean
						.setThumbPath(FileUtil.FILEPATH_THUMBNAIL_WALLPAPER
								+ saveTime + suffix);
				wallpaperBean.setWallpaperPath(FileUtil.FILEPATH_WALLPAPER
						+ saveTime + suffix);
				wallpaperBean.setTime(saveTime);
				wallpaperBean.setIsDefault(1);
				wallpaperBean.setIsProvide(0);
				mContentResolver.insert(
						WallPaperProvider.getWallpaperUri(mContext),
						wallpaperBean.getContentValues());

				// if (LockApplication.getDexClassLoader()
				// .isSupportBaseThemeSetting(mPackageName)) {
				ProviderHelper.updateWallpaper(mContext, mPackageName,
						Constants.C_WALLPAPER_GALLERY,
						wallpaperBean.getWallpaperPath());
				// } else {
				// SettingsHelper.setWallpaperType(mContext,
				// Constants.C_WALLPAPER_GALLERY);
				// SettingsHelper.setWallpaperPath(mContext,
				// wallpaperBean.getWallpaperPath());
				// }

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("theme", "custom");
				MobclickAgent.onEvent(mContext,
						Util.Statistics.KEY_WALLPAPER_SWITCH, map);
				refreshWallpaper();
				Toast.makeText(mContext,
						R.string.settings_theme_or_wallpaper_success,
						Toast.LENGTH_SHORT).show();
				mGridView.smoothScrollToPosition(0);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	private void CropPicture(Uri uri) {
		// Intent mIntent = new Intent(getBaseContext(),
		// CropImageActivity.class);
		// mIntent.putExtra("uri", uri.toString());
		// mIntent.putExtra("outputPath", FileUtil.FILEPATH_WALLPAPER + saveTime
		// + suffix);
		// startActivityForResult(mIntent, REQUEST_ZOOM);
		Uri outputUri = Uri.fromFile(new File(FileUtil.FILEPATH_WALLPAPER
				+ saveTime + suffix));
		new Crop(uri)
				.output(outputUri)
				.withAspect(Util.getScreenWidth(mContext),
						Util.getScreenHeight(mContext)).start(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onBackPressed() {
		if (isEditMode) {
			changedEditMode(!isEditMode);
		} else {
			this.finish();
		}
	}

}
