package com.cyou.cma.clockscreen.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog.Calls;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewStub.OnInflateListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cynad.cma.locker.R;
import com.cyou.cma.clocker.apf.Keyguard;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.LockApplication;
import com.cyou.cma.clockscreen.adapter.NotificationAdapter4Other;
import com.cyou.cma.clockscreen.bean.AppBaseInfo;
import com.cyou.cma.clockscreen.bean.AppNotification;
import com.cyou.cma.clockscreen.bean.Group;
import com.cyou.cma.clockscreen.core.CheckObserverAsyncTask;
import com.cyou.cma.clockscreen.core.CheckObserverAsyncTask.MissedCall;
import com.cyou.cma.clockscreen.core.CheckObserverAsyncTask.MissedMessage;
import com.cyou.cma.clockscreen.core.ClassInitException;
import com.cyou.cma.clockscreen.core.KeyguardCallbackImpl;
import com.cyou.cma.clockscreen.core.KeyguardFactory;
import com.cyou.cma.clockscreen.core.KeyguardImpl4Play;
import com.cyou.cma.clockscreen.core.KeyguardView;
import com.cyou.cma.clockscreen.defaulttheme.CLockScreen;
import com.cyou.cma.clockscreen.event.BlurEvent;
import com.cyou.cma.clockscreen.event.NotificationEvent;
import com.cyou.cma.clockscreen.event.RemoveNotificationEvent;
import com.cyou.cma.clockscreen.notification.NotificationUtil;
import com.cyou.cma.clockscreen.password.PasswordView;
import com.cyou.cma.clockscreen.password.PasswordViewInflater;
import com.cyou.cma.clockscreen.password.SecureAccess;
import com.cyou.cma.clockscreen.quicklaunch.DatabaseUtil;
import com.cyou.cma.clockscreen.quicklaunch.LaunchSet;
import com.cyou.cma.clockscreen.quicklaunch.QuickApplication;
import com.cyou.cma.clockscreen.quicklaunch.QuickContact;
import com.cyou.cma.clockscreen.quicklaunch.QuickFolder;
import com.cyou.cma.clockscreen.receiver.BatteryChangeReceiver;
import com.cyou.cma.clockscreen.receiver.DateTimeObserver;
import com.cyou.cma.clockscreen.receiver.KeyguardObserverCallback;
import com.cyou.cma.clockscreen.receiver.MissedCallObserver;
import com.cyou.cma.clockscreen.receiver.TimeChangeReceiver;
import com.cyou.cma.clockscreen.receiver.UnreadMsgObserver;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.LauchSetType;
import com.cyou.cma.clockscreen.util.LockUtil;
import com.cyou.cma.clockscreen.util.NotificationDescendComparator;
import com.cyou.cma.clockscreen.util.SettingsHelper;
import com.cyou.cma.clockscreen.util.SystemUIStatusUtil;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.CircleMenu;
import com.cyou.cma.clockscreen.widget.MyAdvertLayout;
import com.cyou.cma.clockscreen.widget.CircleMenu.CircleMenuListener;
import com.cyou.cma.clockscreen.widget.LongClickLayout;
import com.cyou.cma.clockscreen.widget.LongClickLayout.LockLongClickListener;
import com.cyou.cma.clockscreen.widget.SwipeDeleteListView;
import com.cyou.cma.clockscreen.widget.SwipeDeleteListView.OnDismissCallback;
import com.cyou.cma.clockscreen.widget.folder.CyFolderHelper;
import com.cyou.cma.clockscreen.widget.folder.util.CyFolder;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

public class LockScreenDialog extends Dialog implements
		KeyguardObserverCallback, SecureAccess {
	private final String TAG = "LockScreenDialog";
	private Context mContext;
	private String mCurrentThemePackageName;
	private LongClickLayout mLockScreen;
	// private FrameLayout mPatternLockScreen;
	private ViewStub mMenuStub;
	private CircleMenu mCircleMenu;
	// private FrameLayout mPasswordLockScreen;

	private LinearLayout mPasswordScreen;
	private LinearLayout mNotificationLayout;
	private FrameLayout mPasswordScreenParant;
	private FrameLayout mFrameLayoutExcludeLockscreen;
	private SwipeDeleteListView mSwipeDeleteListView;
	private NotificationAdapter4Other mNotificationAdapter4Other;
	private ImageButton mNotificationClosed;
	private Group<AppNotification> mNotifications = new Group<AppNotification>();

	// 新的监听在 onStart 和 onStop 中调用
	private BatteryChangeReceiver mBatteryChangeReceiver;
	private DateTimeObserver mDateTimeObserver;
	private MissedCallObserver mMissedCallObserver;
	private TimeChangeReceiver mTimeChangeReceiver;
	private UnreadMsgObserver mUnreadMsgObserver;
	private ContentResolver mContentResolver;

	private KeyguardView mKeyguardView;
	private Keyguard mKeyguardAccess;// TODO 查看为什么会为 空
	private Handler mHandler = new Handler();
	private MessageOrCallHandler mMessageOrCallHandler;
	private KeyguardCallbackImpl mkeyCallbackImpl;

	private boolean mRecevierRegisted = false;// 是否已经注册监听
	private CyFolderHelper helper;
	private PackageManager mPackageManager;
	private NotificationDescendComparator mDescendComparator;

	private MyAdvertLayout mAdvertLayout;

	public LockScreenDialog(Context context) {
		this(context, 0);
	}

	public LockScreenDialog(Context context, int theme) {
		super(context, theme);
		// wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
		// "hehe");
		// wakeLock.acquire();
		// add by Jack
		// getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		mPackageManager = context.getPackageManager();
		mkeyCallbackImpl = new KeyguardCallbackImpl(this);
		getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		mDescendComparator = new NotificationDescendComparator();
		// add by Jack: 降低Cpu占用率
		if (Build.VERSION.SDK_INT >= 11) {
			getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		}
		// end
		// end
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		getWindow().setAttributes(lp);
		mContext = context;
		mCurrentThemePackageName = SettingsHelper.getCurrentTheme(mContext);
		SystemUIStatusUtil.onCreate(context, this.getWindow());

		setContentView(R.layout.activity_lockscreen);
		mContentResolver = mContext.getContentResolver();
		initViews();

		EventBus.getDefault().register(this);

	}

	public void onEventMainThread(BlurEvent event) {
		if (!Util
				.getPreferenceBoolean(mContext, Util.HAS_SHOW_SWIPE_TIP, false)) {
			AppNotification appNotification2 = new AppNotification();
			appNotification2.mPackageName = "ttt";
			appNotification2.type = 1;
			showNotification(appNotification2);
			//
		}
	}

	// Runnable first = new Runnable() {
	//
	// @Override
	// public void run() {
	// AppNotification appNotification2 = new AppNotification();
	// appNotification2.mPackageName = "ttt";
	// appNotification2.type = 1;
	// showNotification(appNotification2);
	//
	// }
	// };
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// if (wakeLock != null) {
		// wakeLock.acquire();
		// }
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			// if (mPasswordLockScreen.getVisibility() == View.VISIBLE) {
			// mPasswordLockScreen.setVisibility(View.GONE);
			// mLockScreen.setVisibility(View.VISIBLE);
			// passwordView.clearArray();
			// passwordView.clearCheckBox();
			// mKeyguardAccess.reset();
			// }
			// if (mPatternLockScreen.getVisibility() == View.VISIBLE) {
			// mPatternLockScreen.setVisibility(View.GONE);
			// mLockScreen.setVisibility(View.VISIBLE);
			// mKeyguardAccess.reset();
			// }
			if (mPasswordScreenParant.getVisibility() == View.VISIBLE) {
				mPasswordScreenParant.setVisibility(View.GONE);
				mLockScreen.setVisibility(View.VISIBLE);
				mKeyguardAccess.reset();
				PasswordView passwordView = (PasswordView) mPasswordScreen
						.getChildAt(0);
				passwordView.onHide();
			}
			if (helper != null && helper.isShow()) {
				helper.hide();
			} else if (mCircleMenu != null && mCircleMenu.isExpanded()) {
				mCircleMenu.switchState(true);
			}
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// if (wakeLock != null)
		// wakeLock.acquire();
		return super.onTouchEvent(event);
	}

	@Override
	protected void onStart() {
		super.onStart();
		MobclickAgent.onPageStart("LockScreen");
		initKeyguardObserver();
		initKeyguardObserverCallback();
		registerObserver();

		onResume();

	}

	@Override
	protected void onStop() {
		// Log.e(TAG, "onStop");
		MobclickAgent.onPageEnd("LockScreen");
		super.onStop();
		// onPause();

		// if (wakeLock != null) {
		// wakeLock.release();
		// }
	}

	public void onResume() {
		new CheckObserverAsyncTask(mContext, CheckObserverAsyncTask.CHECK_CALL,
				mMessageOrCallHandler).execute();
		new CheckObserverAsyncTask(mContext, CheckObserverAsyncTask.CHECK_SMS,
				mMessageOrCallHandler).execute();
		if (mKeyguardAccess != null) {
			if (Util.isScreenOn(mContext)) {
				mKeyguardAccess.onResume();
				registerObserver();
			} else {
				onPause();
				// mKeyguardAccess.onPause();
				// unregisterObserver();
			}
			mKeyguardAccess.onTimeChanged();
		}

		// Adjust.onResume(getContext());
	}

	public void onPause() {
		// Adjust.onPause();
		// Log.e(TAG, "onPause");
		reset();
		unregisterObserver();
		if (mKeyguardAccess != null) {
			mKeyguardAccess.onPause();
		}
	}

	@Override
	public void dismiss() {
		// Log.e(TAG, "dismiss");
		super.dismiss();
		cleanUp();
	}

	public void unregisterObserver() {
		if (mRecevierRegisted) {
			// Log.e(TAG, "jiangbin7 unregisterObserver");
			mRecevierRegisted = false;
			mContext.unregisterReceiver(mTimeChangeReceiver);
			mContext.unregisterReceiver(mBatteryChangeReceiver);
			mContentResolver.unregisterContentObserver(mMissedCallObserver);
			mContentResolver.unregisterContentObserver(mUnreadMsgObserver);
			mContentResolver.unregisterContentObserver(mDateTimeObserver);
		}
	}

	public void registerObserver() {
		if (!mRecevierRegisted) {
			// Log.e(TAG, "jiangbin7 registerObserver");
			mRecevierRegisted = true;
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_TIME_TICK);
			filter.addAction(Intent.ACTION_TIME_CHANGED);
			filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
			mContext.registerReceiver(mTimeChangeReceiver, filter);

			filter = new IntentFilter();
			filter.addAction(Intent.ACTION_BATTERY_CHANGED);
			mContext.registerReceiver(mBatteryChangeReceiver, filter);

			mContentResolver.registerContentObserver(Calls.CONTENT_URI, false,
					mMissedCallObserver);
			mContentResolver.registerContentObserver(
					Uri.parse("content://mms-sms/"), true, mUnreadMsgObserver);
			Uri uri = Settings.System.getUriFor(Settings.System.DATE_FORMAT);
			mContentResolver.registerContentObserver(uri, false,
					mDateTimeObserver);
			uri = Settings.System.getUriFor(Settings.System.TIME_12_24);
			mContentResolver.registerContentObserver(uri, false,
					mDateTimeObserver);
		}
	}

	/**
	 * 初始化监听
	 */
	private void initKeyguardObserver() {
		if (mBatteryChangeReceiver == null) {
			mBatteryChangeReceiver = new BatteryChangeReceiver();
		}
		if (mDateTimeObserver == null) {
			mDateTimeObserver = new DateTimeObserver(mHandler);
		}
		if (mMissedCallObserver == null) {
			mMissedCallObserver = new MissedCallObserver(mHandler);
		}
		if (mTimeChangeReceiver == null) {
			mTimeChangeReceiver = new TimeChangeReceiver();
		}
		if (mUnreadMsgObserver == null) {
			mUnreadMsgObserver = new UnreadMsgObserver(mHandler);
		}
	}

	/**
	 * 初始化监听回调
	 */
	private void initKeyguardObserverCallback() {
		mBatteryChangeReceiver.setOnBatteryChangeReceiver(this);
		mDateTimeObserver.setOnDateTimeObserver(this);
		mMissedCallObserver.setOnMissedCallObserver(this);
		mTimeChangeReceiver.setOnTimeChangeReceiver(this);
		mUnreadMsgObserver.setOnUnreadMsgObserver(this);
	}

	// private Bitmap mBlurBitmap;

	@SuppressLint("InlinedApi")
	public void initViews() {
		mFrameLayoutExcludeLockscreen = (FrameLayout) findViewById(R.id.framelayout_exclude_lockscreen);
		mMenuStub = (ViewStub) mFrameLayoutExcludeLockscreen
				.findViewById(R.id.lockscreen_menu_stub);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("lockTheme", mCurrentThemePackageName);
		MobclickAgent.onEvent(mContext, Util.Statistics.KEY_THEME_USING, map);

		View view = null;
		mKeyguardView = KeyguardFactory.createKeyguardView();
		try {

			mKeyguardView.initKeyguardView(mContext);
			view = mKeyguardView.getKeyguardView();

		} catch (Exception e) {
			dismiss();
			return;
		}
		mLockScreen = (LongClickLayout) findViewById(R.id.lockscreen);
		mLockScreen.setLockLongClickListener(new LockLongClickListener() {

			@Override
			public void onLongClick() {
				MobclickAgent.onEvent(mContext,
						Util.Statistics.KEY_OPEN_QUICKLAUNCH);
				DatabaseUtil.deleteInvalidContact(mContext);
				final List<LaunchSet> launchSets = LockApplication.launchSetDao
						.loadAll();
				for (int i = launchSets.size() - 1; i >= 0; i--) {
					if (!LauchSetType.isVaildType(launchSets.get(i).getType())) {
						launchSets.remove(i);
					}
				}
				if (mMenuStub == null)
					return;
				if (mCircleMenu == null) {
					mMenuStub.setOnInflateListener(new OnInflateListener() {
						@Override
						public void onInflate(final ViewStub stub, View inflated) {
							// if (SystemUIStatusUtil
							// .isStatusBarTransparency(mContext)) {
							// inflated.setPadding(0,
							// ImageUtil.getStatusBarHeight(mContext),
							// 0, 0);
							// }
							// Bitmap bitmap = mKeyguardAccess.getBlurBitmap();
							// if (bitmap != null) {
							// inflated.setBackgroundDrawable(new
							// BitmapDrawable(
							// bitmap));
							// } else {
							// inflated.setBackgroundColor(Color.argb(
							// (int) (255 * 0.85), 0, 0, 0));
							// }
							mAdvertLayout = (MyAdvertLayout) mFrameLayoutExcludeLockscreen
									.findViewById(R.id.myadvertlayout);
							mCircleMenu = (CircleMenu) mFrameLayoutExcludeLockscreen
									.findViewById(R.id.lockscreen_menu);
							mCircleMenu
									.setCircleMenuListener(new CircleMenuListener() {

										@Override
										public void onAnimEnd(boolean isOpen) {
											if (!isOpen) {
												if (stub != null) {
													stub.setVisibility(View.GONE);
												}
											}
										}

										@Override
										public void onItemClick(int x, int y,
												LaunchSet obj) {
											switch (obj.getType()) {
											case LauchSetType.APP_TYPE:
												QuickApplication quickApplication = DatabaseUtil
														.getQuickApplicationOnLaunchSet(obj
																.getId());
												mUnlockToPackageName = quickApplication
														.getPackageName();
												mUnlockToMainClassName = quickApplication
														.getMainActivityClassName();
												mkeyCallbackImpl
														.unlock2Application();
												break;
											case LauchSetType.CONTACT_TYPE:
												QuickContact quickContact = DatabaseUtil
														.getQuickContactOnLaunchSet(obj
																.getId());
												mUnlockToPhoneNumber = quickContact
														.getContactNumber();
												mkeyCallbackImpl
														.unlock2CallContact();
												break;
											case LauchSetType.FOLDER_TYPE:
												QuickFolder quickFolder = DatabaseUtil
														.getQuickFolderOnLaunchSet(obj
																.getId());
												List<QuickApplication> appList = DatabaseUtil
														.getQuickApplicationOnFolder(quickFolder
																.getId());
												List<QuickContact> contactList = DatabaseUtil
														.getQuickContactOnFolder(quickFolder
																.getId());
												if (appList == null
														|| contactList == null
														|| (appList.size() == 0 && contactList
																.size() == 0)) {
													Toast.makeText(
															mContext,
															R.string.quicklaunch_lockscreen_folder_nodata,
															Toast.LENGTH_SHORT)
															.show();
												}

												List<CyFolder> folderList = new ArrayList<CyFolder>();
												CyFolder folder = null;
												int count = 0;
												AppBaseInfo baseInfo = null;
												for (final QuickApplication application : appList) {
													if (count >= 8) {
														break;
													}
													folder = new CyFolder();
													baseInfo = getPackageBitmap(
															application
																	.getPackageName(),
															application
																	.getMainActivityClassName());
													if (baseInfo == null)
														continue;
													folder.setAppBaseInfo(baseInfo);
													folder.setCl(new View.OnClickListener() {

														@Override
														public void onClick(
																View arg0) {
															mUnlockToPackageName = application
																	.getPackageName();
															mUnlockToMainClassName = application
																	.getMainActivityClassName();
															mkeyCallbackImpl
																	.unlock2Application();
														}
													});
													folderList.add(folder);
													count++;
												}
												for (final QuickContact contact : contactList) {
													if (count >= 8) {
														break;
													}
													folder = new CyFolder();
													if (TextUtils.isEmpty(contact
															.getPhotoUri())) {
														folder.setBmp(ImageUtil
																.readBitmapWithDensity(
																		getContext(),
																		R.drawable.icon_quicklaunch_contact_folder));
													} else {
														Bitmap bitmap = null;
														try {
															bitmap = MediaStore.Images.Media
																	.getBitmap(
																			getContext()
																					.getContentResolver(),
																			Uri.parse(contact
																					.getPhotoUri()));
														} catch (Exception e) {
														}
														if (bitmap == null) {
															folder.setBmp(ImageUtil
																	.readBitmapWithDensity(
																			getContext(),
																			R.drawable.icon_quicklaunch_contact_folder));
														} else {
															folder.setBmp(LockApplication
																	.getInstance()
																	.getQuickLaunchWithMaskBitmap(
																			bitmap,
																			true));
														}
													}
													folder.setCl(new View.OnClickListener() {

														@Override
														public void onClick(
																View arg0) {
															mUnlockToPhoneNumber = contact
																	.getContactNumber();
															mkeyCallbackImpl
																	.unlock2CallContact();
														}
													});
													folder.setName(contact
															.getContactName());
													folderList.add(folder);
													count++;
												}
												if (helper == null) {
													helper = CyFolderHelper
															.getInstance();
												}
												helper.show(x, y, folderList);
												break;

											default:
												break;
											}
										}

										@Override
										public void onLockerClick() {
											mkeyCallbackImpl
													.unlock2LockerSetting();
										}
									});
							mCircleMenu.setAdapterList(launchSets);
							inflated.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									if (mCircleMenu != null) {
										mCircleMenu.switchState(true);
									}
									// if (stub != null) {
									// stub.setVisibility(View.GONE);
									// }

								}
							});
							if (mCircleMenu != null) {
								mCircleMenu.postDelayed(new Runnable() {

									@Override
									public void run() {
										mCircleMenu.switchState(true);
									}
								}, 50);
							}
						}
					});
					try {
						mMenuStub.inflate();
					} catch (Exception e) {
						Util.printException(e);
					}
				} else {
					if (mMenuStub.getVisibility() == View.VISIBLE)
						return;
					if (mCircleMenu != null && mCircleMenu.isExpanded()) {
						mCircleMenu.switchState(false);
					}
					mMenuStub.setVisibility(View.VISIBLE);
					if (mCircleMenu != null) {
						mCircleMenu.postDelayed(new Runnable() {

							@Override
							public void run() {
								mCircleMenu.switchState(true);
							}
						}, 50);
					}
				}
			}
		});

		mPasswordScreen = (LinearLayout) mFrameLayoutExcludeLockscreen
				.findViewById(R.id.password_screen);
		mNotificationLayout = (LinearLayout) mFrameLayoutExcludeLockscreen
				.findViewById(R.id.notification_layout);
		mSwipeDeleteListView = (SwipeDeleteListView) mNotificationLayout
				.findViewById(R.id.swipe_delete);
		mNotificationClosed = (ImageButton) mFrameLayoutExcludeLockscreen
				.findViewById(R.id.notification_other_close);
		mNotificationClosed.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mNotifications.clear();
				mNotificationAdapter4Other.notifyDataSetChanged();
				mNotificationClosed.setVisibility(View.GONE);
				mHandler.postDelayed(r, 300);
				// hideNotificationLayout();
			}
		});
		mSwipeDeleteListView.setOnDismissCallback(new OnDismissCallback() {

			@Override
			public void onOpen(int position) {
				try {
					AppNotification appNotification = mNotificationAdapter4Other
							.getItem(position);
					if (appNotification.type == 1) {
						mNotifications.remove(appNotification);
						mNotificationAdapter4Other.notifyDataSetChanged();
						if (mNotifications.size() == 0) {
							hideNotificationLayout();
						}
						mkeyCallbackImpl.unlock2NotificationSetting();
					} else {
						mkeyCallbackImpl
								.unlockByNotification(appNotification.mPendingIntent);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onDismiss(int dismissPosition) {
				try {
					boolean need = false;
					if (mNotifications.get(dismissPosition).type == 1) {
						need = true;
					}
					mNotifications.remove(dismissPosition);
					mNotificationAdapter4Other.notifyDataSetChanged();
					if (mNotifications.size() == 0) {
						hideNotificationLayout();
					}
					if (need) {
						mkeyCallbackImpl.unlock2NotificationSetting();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onRemoveAllItem() {
				try {
					mNotifications.clear();
					mNotificationAdapter4Other.notifyDataSetChanged();
					hideNotificationLayout();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
		mNotificationAdapter4Other = new NotificationAdapter4Other(mContext);
		mSwipeDeleteListView.setAdapter(mNotificationAdapter4Other);
		mPasswordScreenParant = (FrameLayout) mFrameLayoutExcludeLockscreen
				.findViewById(R.id.password_screen_parent);
		// FrameLayout mPasswordView = (FrameLayout)
		// findViewById(R.id.password_view);
		// TODO jiangbin 这里强制改成google play 的
		mKeyguardAccess = new KeyguardImpl4Play(mKeyguardView);
		mKeyguardAccess.setKeyguardCallback(mkeyCallbackImpl);
		// if (PasswordHelper.getUnlockType(mContext) != Util.SLIDE_TYPE) {
		// mBlurBitmap = mKeyguardAccess.getBlurBitmap();
		// }
		mMessageOrCallHandler = new MessageOrCallHandler(mKeyguardAccess);
		mLockScreen.addView(view, 0);
	}

	private Runnable r = new Runnable() {

		@Override
		public void run() {
			hideNotificationLayout();
		}
	};
	PackageManager pm;

	private AppBaseInfo getPackageBitmap(String packageName,
			String mainClassName) {
		if (pm == null) {
			pm = getContext().getPackageManager();
		}
		AppBaseInfo baseInfo = new AppBaseInfo(packageName);
		try {
			if (!TextUtils.isEmpty(mainClassName)) {
				ActivityInfo activityInfo = pm.getActivityInfo(
						new ComponentName(packageName, mainClassName), 0);
				baseInfo.setIcon(LockApplication.getInstance()
						.getQuickLaunchWithMaskBitmap(
								activityInfo.loadIcon(pm), true));
				baseInfo.setName(activityInfo.loadLabel(pm).toString());
			} else {
				ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
				baseInfo.setIcon(LockApplication.getInstance()
						.getQuickLaunchWithMaskBitmap(info.loadIcon(pm), true));
				baseInfo.setName(info.loadLabel(pm).toString());
			}
		} catch (NameNotFoundException e) {
			Util.printException(e);

			return null;
		}
		return baseInfo;
	}

	@Override
	public void onRefreshBatteryInfo(boolean plugged, int level) {
		if (mKeyguardAccess != null) {
			mKeyguardAccess.onRefreshBatteryInfo(plugged, level);
		}
	}

	@Override
	public void onDateTimeObserver() {
		if (mKeyguardAccess != null) {
			mKeyguardAccess.onTimeChanged();
		}
	}

	@Override
	public void onMissedCallObserver() {
		new CheckObserverAsyncTask(mContext, CheckObserverAsyncTask.CHECK_CALL,
				mMessageOrCallHandler).execute();

	}

	@Override
	public void onTimeChangeReceiver() {
		if (mKeyguardAccess != null) {
			mKeyguardAccess.onTimeChanged();
		}
	}

	@Override
	public void onUnreadMsgObserver() {
		new CheckObserverAsyncTask(mContext, CheckObserverAsyncTask.CHECK_SMS,
				mMessageOrCallHandler).execute();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return true;
	}

	static class MessageOrCallHandler extends Handler {
		private WeakReference<Keyguard> mWeakReferenceKeyguard;

		private MessageOrCallHandler(Keyguard keyguard) {
			mWeakReferenceKeyguard = new WeakReference<Keyguard>(keyguard);
		}

		@Override
		public void handleMessage(Message msg) {
			if (mWeakReferenceKeyguard.get() != null) {
				Object object = msg.obj;
				if (object != null) {
					if (object instanceof CheckObserverAsyncTask.MissedCall) {
						MissedCall missedCall = (MissedCall) object;
						mWeakReferenceKeyguard.get().onMissedCallChanged(
								missedCall.missedCount, missedCall.missedTime,
								missedCall.callNumber);
					} else if (object instanceof CheckObserverAsyncTask.MissedMessage) {
						MissedMessage missedMessage = (MissedMessage) object;
						mWeakReferenceKeyguard.get().onUnreadSMSChanged(
								missedMessage.missedCount,
								missedMessage.missedTime,
								missedMessage.messageContent);
					}
				}

			}
		}
	}

	View view;

	@SuppressWarnings("deprecation")
	public void showPasswordScreen(String clzzName) {
		if (mPasswordScreen.getChildCount() == 0) {
			// TODO addview
			PasswordViewInflater passwordViewInflater = null;

			try {
				passwordViewInflater = (PasswordViewInflater) Class.forName(
						clzzName).newInstance();
			} catch (InstantiationException e) {
				Util.printException(e);
			} catch (IllegalAccessException e) {
				Util.printException(e);
			} catch (ClassNotFoundException e) {
				Util.printException(e);
			}
			if (passwordViewInflater == null) {
				return;
			}
			PasswordView passwordView = passwordViewInflater.getPasswordView(
					getLayoutInflater(), null);
			passwordView.setSecureAccess(this);
			// passwordView.setType(LockPatternUtils.LOCKSCREEN_TYPE);
			view = (View) passwordView;
			Bitmap bitmap = mKeyguardAccess.getBlurBitmap();
			if (bitmap != null) {
				view.setBackgroundDrawable(new BitmapDrawable(bitmap));
			} else {
				view.setBackgroundColor(Color.argb((int) (255 * 0.85), 0, 0, 0));
			}
			// view.setf
			mPasswordScreen.addView((View) passwordView,
					new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT));

		}

		mPasswordScreenParant.setVisibility(View.VISIBLE);
		mLockScreen.setVisibility(View.GONE);
		mNotificationLayout.setVisibility(View.GONE);
		((PasswordView) (mPasswordScreen.getChildAt(0))).onShow();
	}

	// public Stack<AppNotification> stack = new Stack<AppNotification>();
	public AppNotification mPhoneNotification;

	public void showNotificationLayout(AppNotification appNotification) {

		mLockScreen.setVisibility(View.GONE);
		mNotificationLayout.setVisibility(View.VISIBLE);
		mNotificationClosed.setVisibility(View.VISIBLE);
		mNotificationLayout.setBackgroundDrawable(new BitmapDrawable(
				mKeyguardAccess.getBlurBitmap()));

		try {
			appNotification
					.setLogo(mPackageManager
							.getApplicationIcon(appNotification.mPackageName),
							mContext);
		} catch (NameNotFoundException e) {
		}

		if (!Util.NOTIFICATION_DEBUG) {
			mNotifications.remove(appNotification);
		}
		mNotifications.add(appNotification);
		Collections.sort(mNotifications, mDescendComparator);
		mNotificationAdapter4Other.setGroup(mNotifications);
		mNotificationAdapter4Other.notifyDataSetChanged();
	}

	public void hideNotificationLayout() {
		mNotificationClosed.setVisibility(View.GONE);
		mLockScreen.setVisibility(View.VISIBLE);
		mNotificationLayout.setVisibility(View.GONE);
	}

	private int unlockType = 0;
	private int mMissedCallCount = -1;
	private String mUnlockToPackageName;
	private String mUnlockToMainClassName;
	private String mUnlockToPhoneNumber;
	private PendingIntent mPendingIntent;

	public void setUnlockType(int unlockType, int arg) {
		this.unlockType = unlockType;
		this.mMissedCallCount = arg;
	}

	public void setUnlockType(int unlockType, PendingIntent pendingIntent) {
		this.unlockType = unlockType;
		this.mPendingIntent = pendingIntent;
	}

	@Override
	public void onSecureSuccess() {
		switch (unlockType) {
		case KeyguardCallbackImpl.UNLOCK_NORMAL:
			break;
		case KeyguardCallbackImpl.UNLOCK_MESSAGE:
			LockUtil.openSms(getContext());
			break;
		case KeyguardCallbackImpl.UNLOCK_PHONE:
			LockUtil.openCall(getContext(), mMissedCallCount);
			break;
		case KeyguardCallbackImpl.UNLOCK_CAMERA:
			LockUtil.openCamera(getContext());
			break;
		case KeyguardCallbackImpl.UNLOCK_LOCKER_SETTING:
			LockUtil.openLockerSetting(getContext());
			break;
		case KeyguardCallbackImpl.UNLOCK_APPLICATION:
			Util.startAppByPackageName(mUnlockToPackageName,
					mUnlockToMainClassName, getContext());
			break;
		case KeyguardCallbackImpl.UNLOCK_CALL_CONTACT:
			LockUtil.doCallWithNumber(mContext, mUnlockToPhoneNumber);
			break;
		case KeyguardCallbackImpl.UNLOCK_BY_NOTIFICATION:
			try {
				this.mPendingIntent.send();
			} catch (Exception e) {
			}
			break;
		case KeyguardCallbackImpl.UNLOCK_NOTIFICATION:
			Intent intent = new Intent("com.cynad.cma.locker.notification");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
			break;
		}
		if (view != null)
			view.setBackgroundDrawable(null);
		this.dismiss();
	}

	public void reset() {
		// Log.e(TAG, "reset");
		if (mKeyguardAccess != null) {
			mKeyguardAccess.reset();
		}
		mLockScreen.setVisibility(View.VISIBLE);
		// mPasswordLockScreen.setVisibility(View.GONE);
		// mPatternLockScreen.setVisibility(View.GONE);
		mPasswordScreenParant.setVisibility(View.GONE);
		if (helper != null && helper.isShow()) {
			helper.hide();
		}
		if (mCircleMenu != null && mCircleMenu.isExpanded()) {
			mCircleMenu.switchState(false);
		}
	}

	public void cleanUp() {
		// Log.e(TAG, "cleanUp");
		if (mAdvertLayout != null) {
			mAdvertLayout.destory();
		}
		EventBus.getDefault().unregister(this);
		unregisterObserver();
		// mHandler.removeCallbacks(first);
		// if (mKeyguardReceiver != null) {
		// try {
		// mContext.unregisterReceiver(mKeyguardReceiver);
		// } catch (Exception e) {
		//
		// }
		// }
		CyFolderHelper.getInstance().onDestroy();
		if (mKeyguardAccess != null) {
			mKeyguardAccess.cleanUp();
		}
		{
			System.gc();
		}
		System.gc();
	}

	public void onEventMainThread(NotificationEvent e) {

		AppNotification appNotification = NotificationUtil
				.getNotificationContent(e.notification, mContext);
		if (appNotification == null) {
			return;
		} else {
		}
		appNotification.mIsPhone = e.isPhone;
		showNotification(appNotification);
		try {
			MobclickAgent.onEvent(mContext, "Notification_Push",
					"Notification_Push");
		} catch (Exception e2) {
		}

	}

	public void onEventMainThread(RemoveNotificationEvent e) {

		if (mCurrentThemePackageName.equals(Constants.SKY_LOCKER_DEFAULT_THEME)) {
			try {
				View view = mKeyguardView.getKeyguardView();
				if (view instanceof CLockScreen) {
					CLockScreen cLockScreen = (CLockScreen) view;
					AppNotification appNotification = new AppNotification();
					appNotification.mPackageName = "com.android.phone";
					cLockScreen.removePhone(appNotification);
				}
			} catch (ClassInitException e1) {
				e1.printStackTrace();
			}
		} else {
			try {

				AppNotification appNotification = new AppNotification();
				appNotification.mPackageName = "com.android.phone";
				mNotifications.remove(appNotification);
				mNotificationAdapter4Other.notifyDataSetChanged();
				if (mNotifications.size() == 0) {
					hideNotificationLayout();
				}
			} catch (Exception e2) {
			}
		}

	}

	private void showNotification(AppNotification appNotification) {
		if (mCurrentThemePackageName.equals(Constants.SKY_LOCKER_DEFAULT_THEME)) {
			try {
				View view = mKeyguardView.getKeyguardView();
				if (view instanceof CLockScreen) {
					CLockScreen cLockScreen = (CLockScreen) view;
					if (appNotification != null) {
						if (mPasswordScreenParant == null
								|| !(mPasswordScreenParant.getVisibility() == View.VISIBLE)) {
							cLockScreen.showSwipeListView(appNotification);
						}
					}
				}
			} catch (ClassInitException e1) {
				e1.printStackTrace();
			}
		} else {
			if (appNotification != null) {
				if (mPasswordScreenParant == null
						|| !(mPasswordScreenParant.getVisibility() == View.VISIBLE)) {
					showNotificationLayout(appNotification);
				}
			}
		}
	}

	public void needAddPhoneNotification() {
		// String text = Util.getMissedCall(mContext);
		// String name[] = text.split(";");
		// boolean contains = false;
		// if (mPhoneNotification == null) {
		// Log.d("jiangbintest", "mPhoneNotification == null");
		// return;
		// } else {
		// Log.d("jiangbintest", "mPhoneNotification != null");
		//
		// }
		// String content = mPhoneNotification.mContent;
		// String title = mPhoneNotification.mTitle;
		// if (name.length == 2) {
		// if (content.contains(name[0]) || content.contains(name[1])
		// || title.contains(name[0]) || title.contains(name[1])) {
		// contains = true;
		// }
		// } else if (name.length == 1) {
		// if (content.contains(name[0]) || title.contains(name[0])) {
		// contains = true;
		// }
		// }
		// if (contains) {
		// Log.d("jiangbintest", "contains show");
		// showNotification(mPhoneNotification);
		// } else {
		// Log.d("jiangbintest", "contains not show");
		// }
	}

}
