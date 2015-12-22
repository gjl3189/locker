/*
 * TabFragmentActivity.java
 * Tab风格的活动，内容用Fragment显示，通过左右滑动切换Fragment，同时Tab与之联动
 * @since  2014-2-13下午3:01:06
 * @author xuchunlei
 */

package com.cyou.cma.clockscreen.activity;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.adapter.TabPagerAdapter;
import com.cyou.cma.clockscreen.adapter.ZoomOutPageTransformer;
import com.cyou.cma.clockscreen.fragment.OnTabChangeListener;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.widget.TabPageIndicator;
import com.cyou.cma.clockscreen.widget.material.LImageButton;

/**
 * @author xuchunlei
 */
public abstract class TabFragmentActivity extends BaseFragmentActivity
		implements OnClickListener, OnPageChangeListener {

	public static final String TAG = "TabFragmentActivity";

	// 默认的tab页位置
	private final int DEFAULT_TAB_POSITION = 0;

	// private RadioGroup mContainer;
	protected ViewPager mViewPager;
	private TabPagerAdapter mAdapter;

	// 标题控件
	protected TextView mTitleTxv;

	protected TabPageIndicator mTabPageIndicator;

	// tab项集合，方便用于各类关于tab项的操作
	// private List<RadioButton> mTabs;
	// 当前Tab页位置
	private int mPosition = DEFAULT_TAB_POSITION;
	// 标题栏左侧按钮
	protected LImageButton mLeft;
	// 标题栏右侧按钮
	protected LImageButton mRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SystemUIStatusUtil.onCreate(this, this.getWindow());
		setContentView(R.layout.tab_activity_main);
		mTitleTxv = (TextView) findViewById(R.id.tv_title);
		setTitleText(mTitleTxv);
		mLeft = (LImageButton) findViewById(R.id.btn_left);
		mRight = (LImageButton) findViewById(R.id.btn_right);
		mTabPageIndicator = (TabPageIndicator) findViewById(R.id.tab_indicator);
		// 初始化各Tab页标题
		initTabs(mTabPageIndicator,
				getResources().getStringArray(getTabsNameResource()));

		// mContainer = (RadioGroup) findViewById(R.id.tab_indicator_container);
		// mTabs = new ArrayList<RadioButton>();
		// initTabs(mContainer,
		// getResources().getStringArray(getTabsNameResource()));

		// 初始化各Tab页内容
		mViewPager = (ViewPager) findViewById(R.id.tab_pager);
//		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
		// mViewPager.setOnPageChangeListener(this);
		mAdapter = new TabPagerAdapter(getSupportFragmentManager(),
				getTabsFragments());
		mViewPager.setAdapter(mAdapter);
		mTabPageIndicator.setViewPager(mViewPager);
		mTabPageIndicator.setOnPageChangeListener(this);

		if (SystemUIStatusUtil.isStatusBarTransparency(this)) {
			findViewById(R.id.root).setPadding(0,
					ImageUtil.getStatusBarHeight(this), 0, 0);
		}

	}

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
	protected abstract void setTitleText(TextView title);

	/**
	 * 获得标签页的名称资源
	 * 
	 * @return
	 */
	protected abstract int getTabsNameResource();

	/**
	 * 获得标签页Fragment集合
	 * 
	 * @return
	 */
	protected abstract List<Fragment> getTabsFragments();

}
