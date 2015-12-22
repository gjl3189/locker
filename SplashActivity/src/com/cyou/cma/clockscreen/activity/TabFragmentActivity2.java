/*
 * TabFragmentActivity.java
 * Tab风格的活动，内容用Fragment显示，通过左右滑动切换Fragment，同时Tab与之联动
 * @since  2014-2-13下午3:01:06
 * @author xuchunlei
 */

package com.cyou.cma.clockscreen.activity;

import java.util.List;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.adapter.TabPagerAdapter;
import com.cyou.cma.clockscreen.adapter.ZoomOutPageTransformer;
import com.cyou.cma.clockscreen.bean.SecurityJudge;
import com.cyou.cma.clockscreen.event.DrawFinishEvent;
import com.cyou.cma.clockscreen.event.MailboxEvent;
import com.cyou.cma.clockscreen.event.SecurityCompleteEvent;
import com.cyou.cma.clockscreen.fragment.OnTabChangeListener;
import com.cyou.cma.clockscreen.util.CyGuideHelper;
import com.cyou.cma.clockscreen.util.HttpUtil;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.ToastMaster;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.CyScrollLayout;
import com.cyou.cma.clockscreen.widget.MyAdvertLayout;
import com.cyou.cma.clockscreen.widget.MyFrameLayout;
import com.cyou.cma.clockscreen.widget.ScrollLayout.OnViewChangeListener;
import com.cyou.cma.clockscreen.widget.ScrollLayout.ScaleListener;
import com.cyou.cma.clockscreen.widget.TabPageIndicator;
import com.cyou.cma.clockscreen.widget.TabPageIndicator.HeHeListener;
import com.cyou.cma.clockscreen.widget.material.LImageButton;

import de.greenrobot.event.EventBus;

/**
 * @author xuchunlei
 */
public abstract class TabFragmentActivity2 extends BaseFragmentActivity
		implements OnClickListener, OnPageChangeListener, OnViewChangeListener,
		HeHeListener, ScaleListener {

	public static final String TAG = "TabFragmentActivity";

	// 默认的tab页位置
	private final int DEFAULT_TAB_POSITION = 0;

	// private RadioGroup mContainer;
	protected ViewPager mViewPager;
	private TabPagerAdapter mAdapter;

	// 标题控件
	protected TextView mTitleTxv;

	protected TabPageIndicator mTabPageIndicatorFake;// 假的指示器
	protected TabPageIndicator mTabPageIndicatorReal;// 真的指示器

	// tab项集合，方便用于各类关于tab项的操作
	// private List<RadioButton> mTabs;
	// 当前Tab页位置
	private int mPosition = DEFAULT_TAB_POSITION;
	// 标题栏左侧按钮
	protected LImageButton mLeft;
	// 标题栏右侧按钮
	protected LImageButton mRight;
	protected CyScrollLayout mScrollLayout;
	private MyAdvertLayout mAdvertLayout;

	@Override
	public void onScale(float scale) {
		// ViewHelper.setScaleY(mFrameLayoutl, scale);
		// if(scale<0.8f){
		// mTabPageIndicator.setVisibility(View.GONE);
		// }
		// int height = (int) (offset * scale)
		// mFrameLayoutl.getLayoutParams().height = ;
	}

	private View mDividerView1;
	private View mDividerViewDown;
	private SharedPreferences sp;

	// BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
	//
	// @Override
	// public void onReceive(Context arg0, Intent arg1) {
	// if (arg1.getAction().equals((Intents.ACTION_MAILBOX))) {
	// SecurityJudge securityJudge = Util.getSecurityLevel(arg0);
	// if (mLastSecurityJudge != securityJudge) {
	// myFrameLayout.updateSecurityLevel(securityJudge);
	// }
	// mLastSecurityJudge = securityJudge;
	// }
	// }
	//
	// };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SystemUIStatusUtil.onCreate(this, this.getWindow());
		setContentView(R.layout.tab_activity_main2);
		mDividerView1 = findViewById(R.id.divider_view1);
		mDividerViewDown = findViewById(R.id.divider_view2);
		mDividerViewDown.setVisibility(View.INVISIBLE);
		mTitleTxv = (TextView) findViewById(R.id.tv_title);
		setTitleText(mTitleTxv);
		// mFrameLayoutl = (FrameLayout) findViewById(R.id.frame_indicator);
		mScrollLayout = (CyScrollLayout) findViewById(R.id.scroll_layout);
		mScrollLayout.setScaleListener(this);
		myFrameLayout = (MyFrameLayout) findViewById(R.id.security_framelayout);
		mScrollLayout.SetOnViewChangeListener(this);
		mLeft = (LImageButton) findViewById(R.id.btn_left);
		mRight = (LImageButton) findViewById(R.id.btn_right);
		mTabPageIndicatorFake = (TabPageIndicator) findViewById(R.id.tab_indicator);
		mTabPageIndicatorReal = (TabPageIndicator) findViewById(R.id.tab_indicator2);
		mTabPageIndicatorReal.setVisibility(View.INVISIBLE);
		mTabPageIndicatorFake.setOnHeheListener(this);
		// 初始化各Tab页标题
		initTabs(mTabPageIndicatorFake,
				getResources().getStringArray(R.array.locker_mode2));
		initTabs(mTabPageIndicatorReal,
				getResources().getStringArray(getTabsNameResource()));
		// mContainer = (RadioGroup) findViewById(R.id.tab_indicator_container);
		// mTabs = new ArrayList<RadioButton>();
		// initTabs(mContainer,
		// getResources().getStringArray(getTabsNameResource()));

		// 初始化各Tab页内容
		mViewPager = (ViewPager) findViewById(R.id.tab_pager);
		mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
		// mViewPager.setOnPageChangeListener(this);
		mViewPager.setOffscreenPageLimit(4);
		mAdapter = new TabPagerAdapter(getSupportFragmentManager(),
				getTabsFragments());
		mViewPager.setAdapter(mAdapter);
		mTabPageIndicatorFake.setViewPager(mViewPager);
		mTabPageIndicatorFake.setOnPageChangeListener(this);
		mTabPageIndicatorReal.setViewPager(mViewPager);
		mTabPageIndicatorReal.setOnPageChangeListener(this);

		if (SystemUIStatusUtil.isStatusBarTransparency(this)) {
			findViewById(R.id.root).setPadding(0,
					ImageUtil.getStatusBarHeight(this), 0, 0);
		}
		mLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Log.d("jiangbinf", "jiangbinf--" +
				// mScrollLayout.getCurScreen()
				// + " child count" + mScrollLayout.getChildCount());
				if (mLeft.mImageResId == R.drawable.icon_header_back_normal) {
					mScrollLayout.snapToScreen(0);
				}
			}
		});

		// IntentFilter installFilter = new IntentFilter();
		// installFilter.addAction(Intents.ACTION_MAILBOX);
		// registerReceiver(broadcastReceiver, installFilter);
		long start = System.currentTimeMillis();
		EventBus.getDefault().register(this);
		long end = System.currentTimeMillis();
		mAdvertLayout = (MyAdvertLayout) findViewById(R.id.myadvertlayout);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// unregisterReceiver(broadcastReceiver);
		EventBus.getDefault().unregister(this);
		mAdvertLayout.destory();

	}

	@Override
	public void onBackPressed() {
		if (mLeft.mImageResId == R.drawable.icon_header_back_normal) {
			mScrollLayout.snapToScreen(0);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onHehe() {
		if (mScrollLayout.getCurScreen() == 0) {
			mScrollLayout.snapToScreen(1);
		}
	}

	private SecurityJudge mLastSecurityJudge;
	MyFrameLayout myFrameLayout;

	@Override
	protected void onResume() {
		super.onResume();
		SecurityJudge securityJudge = Util.getSecurityLevel(this);
		if (mLastSecurityJudge != securityJudge) {
			myFrameLayout.updateSecurityLevel(securityJudge);
		}
		mLastSecurityJudge = securityJudge;
	}

	@Override
	public void OnViewChange(int view) {
		// ViewHelper.setScaleY(mFrameLayoutl, 1);
		if (view == 1) {
			// mViewPager.invalidate();
			mTabPageIndicatorFake.setVisibility(View.INVISIBLE);
			mTabPageIndicatorReal.setVisibility(View.VISIBLE);
			mDividerViewDown.setVisibility(View.VISIBLE);
			// mDividerView2.setVisibility(View.VISIBLE);
			mDividerView1.setVisibility(View.INVISIBLE);
			mLeft.setImageResource(R.drawable.icon_header_back_normal);
			mLeft.setEnabled(true);
		} else {
			mTabPageIndicatorReal.setVisibility(View.INVISIBLE);
			mTabPageIndicatorFake.setVisibility(View.VISIBLE);
			// mDividerView1.setVisibility(View.VISIBLE);
			// mDividerView2.setVisibility(View.INVISIBLE);
			mDividerViewDown.setVisibility(View.INVISIBLE);
			// mDividerView2.setVisibility(View.VISIBLE);
			mDividerView1.setVisibility(View.VISIBLE);
			mLeft.setImageResource(R.drawable.icon_header_logo);
			mLeft.setEnabled(false);
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
	private boolean shouldShow;

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
		if(position==2){
			if (Util.getPreferenceBoolean(this, "shouldshow", true)) {
				shouldShow = true;
				Util.putPreferenceBoolean(this, "shouldshow", false);
			}
			if(shouldShow){
				shouldShow = false;
				ToastMaster.makeText(this, R.string.quick_tip, Toast.LENGTH_LONG);
			}
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

	// add by Jack
	private static final int NONE = -1;
	private int touchX = NONE;
	private int touchY = NONE;
	private static final int MOVE = 7;
	private final Rect rc = new Rect(); 
	private boolean inRange(int x, int y){
		rc.left = touchX - MOVE;
		rc.right = touchX + MOVE;
		rc.top = touchY - MOVE;
		rc.bottom = touchY + MOVE;
		return rc.contains(x, y);
	}
//	private int moveX = NONE;
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		boolean b = false;
		if (CyGuideHelper.isShow) {
			if (event.getPointerCount() > 1) {
				b = true;
			} else {
				int x = (int) event.getX();
				int y = (int) event.getY();
				if (CyGuideHelper.inCircle(x, y)) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {// &&ta==TouchAction.NONE
						b = super.dispatchTouchEvent(event);
					} else if (event.getAction() == MotionEvent.ACTION_UP) {// &&ta==TouchAction.DOWN
						b = super.dispatchTouchEvent(event);
					} else {
						b = true;
					}
					touchX = NONE;
					touchY = NONE;
				} else {
					int action = event.getAction();
					if(action==MotionEvent.ACTION_DOWN){
						touchX = x;
						touchY = y;
					}else if(action==MotionEvent.ACTION_UP){
						if(touchX!=NONE&&touchY!=NONE&&inRange(x, y)){
							touchX = NONE;
							touchY = NONE;
							CyGuideHelper.isShow = false;
							CyGuideHelper.hasShow(this);
						}
					}else if(action==MotionEvent.ACTION_MOVE){
						
					}else{
						touchX = NONE;
						touchY = NONE;
					}
					b = true;
				}
			}
		} else {
			b = super.dispatchTouchEvent(event);
		}
		return b;
	}

	// end
	// add by jiangbin
	/**
	 * 订阅安全设置完成事件
	 */
	public void onEvent(SecurityCompleteEvent event) {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				if (mScrollLayout.getCurScreen() == 0) {
					mScrollLayout.snapToScreen(1);
				}
				mTabPageIndicatorFake.setCurrentItem(2);
				mTabPageIndicatorReal.setCurrentItem(2);
			}
		}, 400);
	}

	public void onEvent(DrawFinishEvent event) {
		mScrollLayout.snapToScreen(1);
		if (!HttpUtil.isNetworkAvailable(this)) {
			// mViewPager.setCurrentItem(1);
			mTabPageIndicatorFake.setViewPager(mViewPager, 1);
			// mTabPageIndicatorFake.invalidateForce(1);
			mTabPageIndicatorReal.setViewPager(mViewPager, 1);
			// mTabPageIndicatorReal.invalidateForce(1);
		}

	}

	public void onEventMainThread(MailboxEvent event) {

		SecurityJudge securityJudge = Util.getSecurityLevel(this);
		if (mLastSecurityJudge != securityJudge) {
			myFrameLayout.updateSecurityLevel(securityJudge);
		}
		mLastSecurityJudge = securityJudge;

	}
	// end
}
