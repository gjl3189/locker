package com.cyou.cma.clockscreen.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.LockApplication;
import com.cyou.cma.clockscreen.adapter.InstalledAppAdapter4QuickLaunch;
import com.cyou.cma.clockscreen.adapter.TabPagerAdapter;
import com.cyou.cma.clockscreen.adapter.ZoomOutPageTransformer;
import com.cyou.cma.clockscreen.event.SelectEvent;
import com.cyou.cma.clockscreen.event.SendEvent;
import com.cyou.cma.clockscreen.fragment.OnTabChangeListener;
import com.cyou.cma.clockscreen.fragment.QuickAppChooseFragment;
import com.cyou.cma.clockscreen.fragment.QuickContactsFragment;
import com.cyou.cma.clockscreen.fragment.QuickLaunchFragment;
import com.cyou.cma.clockscreen.quicklaunch.DatabaseUtil;
import com.cyou.cma.clockscreen.quicklaunch.QuickApplication;
import com.cyou.cma.clockscreen.quicklaunch.QuickContact;
import com.cyou.cma.clockscreen.quicklaunch.QuickFolder;
import com.cyou.cma.clockscreen.util.EditTextMaxLengthWatcher;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.StringUtils;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.FontedEditText;
import com.cyou.cma.clockscreen.widget.TabPageIndicator;
import com.cyou.cma.clockscreen.widget.material.LButton;
import com.cyou.cma.clockscreen.widget.material.LImageButton;

import de.greenrobot.event.EventBus;

public class SelectFolderActivity extends BaseFragmentActivity implements
		OnClickListener, OnPageChangeListener {

	public static final String TAG = "TabFragmentActivity";

	// 默认的tab页位置
	private final int DEFAULT_TAB_POSITION = 0;

	// private RadioGroup mContainer;
	protected ViewPager mViewPager;
	private TabPagerAdapter mAdapter;

	// 标题控件
	protected TextView mTitleTxv;

	// protected TabPageIndicator mTabPageIndicator;
	public LButton mAppsTextView;
	public LButton mContactTextView;

	// tab项集合，方便用于各类关于tab项的操作
	// private List<RadioButton> mTabs;
	// 当前Tab页位置
	private int mPosition = DEFAULT_TAB_POSITION;
	// 标题栏左侧按钮
	protected LImageButton mLeft;
	// 标题栏右侧按钮
	protected LImageButton mRight;
	private FontedEditText mFolderNameEditText;
	private TextView mTipView;

	// public static int app_number;
	// public static int contact_number;
	public static int allCount;
	private View rootView;
	public EditTextMaxLengthWatcher mEditTextMaxLengthWatcher;
	public static boolean sEditTextChanged;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SystemUIStatusUtil.onCreate(this, this.getWindow());
		setContentView(R.layout.tab_activity_folder);
		rootView = findViewById(R.id.root);
		rootView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isOpen) {
					InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					isOpen = false;
				}
			}
		});
		mTitleTxv = (TextView) findViewById(R.id.tv_title);
		setTitleText(mTitleTxv);
		mLeft = (LImageButton) findViewById(R.id.btn_left);
		mRight = (LImageButton) findViewById(R.id.btn_right);
		// mTabPageIndicator = (TabPageIndicator)
		// findViewById(R.id.tab_indicator);

		mAppsTextView = (LButton) findViewById(R.id.apps_textview);
		mAppsTextView.setSelected(true);
		mContactTextView = (LButton) findViewById(R.id.contacts_textview);
		mAppsTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAppsTextView.setSelected(true);
				mContactTextView.setSelected(false);
				mViewPager.setCurrentItem(0);
			}
		});
		mContactTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAppsTextView.setSelected(false);
				mContactTextView.setSelected(true);
				mViewPager.setCurrentItem(1);
			}
		});
		mFolderNameEditText = (FontedEditText) findViewById(R.id.folderedit);

		mFolderNameEditText.performClick();
		mFolderNameEditText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isOpen = true;
			}
		});
		mEditTextMaxLengthWatcher = new EditTextMaxLengthWatcher(64,
				mFolderNameEditText);
		mFolderNameEditText.addTextChangedListener(mEditTextMaxLengthWatcher);
		mTipView = (TextView) findViewById(R.id.folderhint2);

		if (LockApplication.mQuickFolder != null) {
			mFolderNameEditText.setText(LockApplication.mQuickFolder
					.getFolderName());
			mFolderNameEditText.setHint("");
			mTipView.setText(getString(R.string.folderhint2,
					LockApplication.mQuickFolder.getSubCount()));
		} else {
			mTipView.setText(getString(R.string.folderhint2, 0));
			mFolderNameEditText.setHint(getString(R.string.folderhint,
					Util.getFolderNo(this)));
			mFolderNameEditText.setCursorVisible(false);
		}
		// mFolderNameEditText.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// mFolderNameEditText.setHint("");
		//
		// }
		// });
		// mFolderNameEditText
		// .setOnFocusChangeListener(new OnFocusChangeListener() {
		//
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// if (hasFocus) {
		// // mFolderNameEditText.setHint("");
		// }else{
		// if(LockApplication.mQuickFolder != null){
		// mFolderNameEditText.setHint(getString(R.string.folderhint,
		// Util.getFolderNo(SelectFolderActivity.this)));
		// }
		// }
		// }
		// });

		mLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		// 初始化各Tab页标题
		// initTabs(mTabPageIndicator,
		// getResources().getStringArray(getTabsNameResource()));

		// mContainer = (RadioGroup) findViewById(R.id.tab_indicator_container);
		// mTabs = new ArrayList<RadioButton>();
		// initTabs(mContainer,
		// getResources().getStringArray(getTabsNameResource()));

		// 初始化各Tab页内容
		mViewPager = (ViewPager) findViewById(R.id.tab_pager);
		// mViewPager.setOffscreenPageLimit(3);
		mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
		// mViewPager.setOnPageChangeListener(this);
		mAdapter = new TabPagerAdapter(getSupportFragmentManager(),
				getTabsFragments());
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(this);
		// mTabPageIndicator.setViewPager(mViewPager);
		// mTabPageIndicator.setOnPageChangeListener(this);

		if (SystemUIStatusUtil.isStatusBarTransparency(this)) {
			findViewById(R.id.root).setPadding(0,
					ImageUtil.getStatusBarHeight(this), 0, 0);
		}
		EventBus.getDefault().register(this);
		Util.putPreferenceBoolean(this, Util.HASHASHAS, true);

		// Handler handler = new Handler();
		// handler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// InputMethodManager m = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		// isOpen = true;
		//
		// }
		// }, 300);
	}

	boolean isOpen;

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		setCurrentTab(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

	}

	public void onEvent(SelectEvent event) {

		// if (event.type == 1) {
		// app_number = event.appNum;
		// } else {
		// contact_number = event.contactNum;
		// }
		// allCount = app_number + contact_number;
		allCount = InstalledAppAdapter4QuickLaunch.sQuickHashMap.size()
				+ QuickContactsFragment.mFolderApp.size();
		this.mTipView.setText(getString(R.string.folderhint2, allCount));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (allCount != 0) {
			QuickFolder quickFolder = null;
			if (LockApplication.mQuickFolder != null) {
				quickFolder = LockApplication.mQuickFolder;
				// DatabaseUtil.deleteApplicationOnLaunchset(id)
				DatabaseUtil.deleteThingsOnfolder(quickFolder.getId());
			} else {
				quickFolder = new QuickFolder();
			}
			quickFolder.setLaunchSetIdOfFolder(QuickLaunchFragment.ID);
			String name = mFolderNameEditText.getText().toString();
			if (StringUtils.isEmpty(name)) {
				name = getString(R.string.folderhint, Util.getFolderNo(this));
			}
			quickFolder.setFolderName(name);
			quickFolder.setSubCount(allCount);
			long id = LockApplication.mQuickFolderDao
					.insertOrReplace(quickFolder);

			Iterator<Map.Entry<String, QuickApplication>> iter = InstalledAppAdapter4QuickLaunch.sQuickHashMap
					.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, QuickApplication> entry = (Map.Entry<String, QuickApplication>) iter
						.next();
				String packageName = entry.getKey();
				QuickApplication val = entry.getValue();
				val.setFolderIdOfApplication(id);
				LockApplication.mQuickApplicationDao.insertOrReplace(val);
			}
			InstalledAppAdapter4QuickLaunch.sQuickHashMap.clear();

			Iterator<Map.Entry<String, QuickContact>> iter2 = QuickContactsFragment.mFolderApp
					.entrySet().iterator();
			while (iter2.hasNext()) {
				Map.Entry<String, QuickContact> entry = (Map.Entry<String, QuickContact>) iter2
						.next();
				// String postion = entry.getKey();
				QuickContact val = entry.getValue();
				val.setFolderIdOfContact(id);
				LockApplication.mQuickContactDao.insertOrReplace(val);
			}
			QuickContactsFragment.mHasSelectedApp.clear();
			SendEvent event = new SendEvent();
			event.eventType = SendEvent.FOLDER_TYPE;
			event.extra1 = name;
			// Util.saveFolderNo(this);
			EventBus.getDefault().post(event);
		} else {
			if (LockApplication.mQuickFolder != null) {
				if (sEditTextChanged) {
					QuickFolder quickFolder = LockApplication.mQuickFolder;
					String folderName = mFolderNameEditText.getText()
							.toString();
					if (StringUtils.isEmpty(folderName)) {
					} else {
						quickFolder.setFolderName(folderName);
						quickFolder
								.setLaunchSetIdOfFolder(QuickLaunchFragment.ID);
						LockApplication.mQuickFolderDao
								.insertOrReplace(quickFolder);
						SendEvent event = new SendEvent();
						event.eventType = SendEvent.FOLDER_TYPE;
						event.extra1 = folderName;
						// Util.saveFolderNo(this);
						EventBus.getDefault().post(event);
					}
				}
			}
		}
		allCount = 0;
		// app_number = 0;
		// contact_number = 0;
		// InstalledAppAdapter4QuickLaunch.sQuickHashMap
		EventBus.getDefault().unregister(this);
		QuickContactsFragment.mHasSelectedApp.clear();
		QuickContactsFragment.mFolderApp.clear();
		InstalledAppAdapter4QuickLaunch.sQuickHashMap.clear();
		LockApplication.mQuickFolder = null;
		sEditTextChanged = false;

	}

	@Override
	public void onPageSelected(int position) {
		// 触发先前选中的Tab页的未选监听事件
		Fragment oldItem = mAdapter.getItem(mPosition);
		if (oldItem instanceof OnTabChangeListener) {
			((OnTabChangeListener) oldItem).onUnselected();
		}
		// mTabs.get(position).setChecked(true);

		Fragment newItem = mAdapter.getItem(position);
		// 触发当前选中Tab页的标题栏变更事件
		// if (newItem instanceof OnTitleBarListener) {
		// ((OnTitleBarListener) newItem).onLeftChanged(mLeft);
		// ((OnTitleBarListener) newItem).onRightChanged(mRight);
		// }
		// 触发当前选中的Tab页的选中监听事件
		if (newItem instanceof OnTabChangeListener) {
			((OnTabChangeListener) newItem).onSelected();
		}
		mPosition = position;
		if (position == 0) {
			mAppsTextView.setSelected(true);
			mContactTextView.setSelected(false);
		} else {
			mAppsTextView.setSelected(false);
			mContactTextView.setSelected(true);
		}
	}

	/**
	 * 获得标题栏
	 * 
	 * @return
	 */
	protected View getTitleBar() {
		return mTitleTxv;
	}

	/**
	 * 获得当前的Fragment
	 * 
	 * @return
	 */
	protected Fragment getCurrentFragment() {
		return mAdapter.getItem(mViewPager.getCurrentItem());
	}

	// 初始化tab标题栏
	private void initTabs(TabPageIndicator tabPageIndicator, String[] names) {

		for (int i = 0, length = names.length; i < length; i++) {
			// RadioButton indicator = (RadioButton) inflater.inflate(
			// R.layout.tab_activity_indicator, container, false);
			// indicator.setText(names[i]);
			// // 保存tab项的索引
			// indicator.setTag(i);
			// indicator.setOnClickListener(this);
			// container.addView(indicator);
			// // 将tab页实例添加到集合
			// mTabs.add(indicator);
			tabPageIndicator.addTitle(names[i]);
		}
		// mTabs.get(DEFAULT_TAB_POSITION).setChecked(true);
	}

	/**
	 * 设置当前显示的tab页面
	 * 
	 * @param position
	 */
	protected void setCurrentTab(int position) {
		// mTabs.get(position).setChecked(true);
		mViewPager.setCurrentItem(position);
	}

	/**
	 * 设置页面的主标题
	 * 
	 * @param title
	 */
	protected void setTitleText(TextView title) {
		title.setText(R.string.quick_launcher);
	}

	/**
	 * 获得标签页的名称资源
	 * 
	 * @return
	 */
	protected int getTabsNameResource() {
		return R.array.quick_folder;
	}

	/**
	 * 获得标签页Fragment集合
	 * 
	 * @return
	 */
	protected List<Fragment> getTabsFragments() {
		List<Fragment> fragements = new ArrayList<Fragment>();
		QuickAppChooseFragment quickAppChooseFragment = new QuickAppChooseFragment();
		fragements.add(quickAppChooseFragment);
		quickAppChooseFragment.setIsFolder(true);
		QuickContactsFragment quickContactsFragment = new QuickContactsFragment();
		fragements.add(quickContactsFragment);
		quickContactsFragment.setHeadViewHidden(true);
		quickContactsFragment.setIsFolder(true);
		return fragements;
	}

}
