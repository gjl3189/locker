package com.cyou.cma.clockscreen.defaulttheme;

import static com.cyou.cma.clockscreen.defaulttheme.AnimImageView.MODE_GONE;
import static com.cyou.cma.clockscreen.defaulttheme.AnimImageView.MODE_NORMAL;
import static com.cyou.cma.clockscreen.defaulttheme.AnimImageView.MODE_SELECTED;

import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Build;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.cengine.CyTool;
import com.cyou.cma.cengine.wave.CyWaveHelper;
import com.cyou.cma.cengine.wave.CyWaveHelper.CyWaveHpCallback;
import com.cyou.cma.clocker.apf.Keyguard;
import com.cyou.cma.clocker.apf.KeyguardCallback;
import com.cyou.cma.clockscreen.Constants;
import com.cyou.cma.clockscreen.adapter.NotificationAdapter4Default;
import com.cyou.cma.clockscreen.bean.AppNotification;
import com.cyou.cma.clockscreen.bean.CoreBitmapObj;
import com.cyou.cma.clockscreen.bean.Group;
import com.cyou.cma.clockscreen.core.KeyguardCallbackImpl;
import com.cyou.cma.clockscreen.defaulttheme.BottomView.UnlockListener;
import com.cyou.cma.clockscreen.password.PasswordHelper;
import com.cyou.cma.clockscreen.sqlite.ProviderHelper;
import com.cyou.cma.clockscreen.util.BlurCallable4DefaultTheme;
import com.cyou.cma.clockscreen.util.ImageUtil;
import com.cyou.cma.clockscreen.util.NotificationDescendComparator;
import com.cyou.cma.clockscreen.util.Util;
import com.cyou.cma.clockscreen.widget.SwipeDeleteListView;
import com.cyou.cma.clockscreen.widget.SwipeDeleteListView.OnDismissCallback;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

public class CLockScreen extends FrameLayout implements Keyguard {
	private final String TAG = "DefaultTheme";
	private Context mContext = null;
	private TimeLayer timerLayer;
	private TextView mBatteryTextView;
	private AnimImageView mCallView;
	private AnimImageView mCameraView;
	private AnimImageView mUnlockView;
	private SwipeDeleteListView mSwipeDeleteListView;
	private NotificationAdapter4Default mNotificationAdapter;
	private LinearLayout mTimerMsgLayout;
	private BottomView mBottomView;
	private TextView mHintTextView;

	private boolean isTouching = false;
	private KeyguardCallback mKeyguardCallback;
	private int mMissedCallCount = 0;

	private int index = -1;
	private PackageManager mPackageManager;

	public CLockScreen(Context context) {
		this(context, null);
	}

	public CLockScreen(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mDescendComparator = new NotificationDescendComparator();
		View.inflate(mContext, R.layout.defaulttheme_main, this);
		initViews();
		if (CyWallpaperHelper.hasSize()) {
			setBmp(CyWallpaperHelper.width, CyWallpaperHelper.height);
		}
		// boolean showedTip = Util.getPreferenceBoolean(mContext,
		// Util.SAVE_KEY_IS_SHOWED_THEME_TIP
		// + Constants.SKY_LOCKER_DEFAULT_THEME, false);
		// if (!showedTip) {
		// showViewStubTip();
		// }
		// onResume();
	}

	Group<AppNotification> mNotifications;
	private ImageButton mNotificationClosed;

	private void initViews() {
		mPackageManager = getContext().getPackageManager();
		// mMsgAdapter = new MsgCenterAdapter(mContext, mMsgList,
		// new DataObserver() {
		// @Override
		// public void onNotifyDataSetChanged(int count) {
		// int paddingTop = mTimeLayerPaddingTop - count * 35
		// * getHeight() / 1280;
		// if (paddingTop < 232 * getHeight() / 1280) {
		// paddingTop = 232 * getHeight() / 1280;
		// }
		// timerLayer.setPadding(0, paddingTop, 0, 0);
		// }
		// });

		mNotificationAdapter = new NotificationAdapter4Default(mContext);
		mNotifications = new Group<AppNotification>();
		// for (int i = 0; i < 10; i++) {
		// AppNotification appNotification = new AppNotification();
		// try {
		// appNotification.setLogo(mPackageManager
		// .getApplicationIcon("com.cynad.cma.locker"));
		// } catch (NameNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// notifications.add(appNotification);
		// }
		mNotificationAdapter.setGroup(mNotifications);

		timerLayer = (TimeLayer) findViewById(R.id.defaulttheme_timelayer);
		mBatteryTextView = (TextView) findViewById(R.id.defaulttheme_battery_label);
		mCallView = (AnimImageView) findViewById(R.id.defaulttheme_call);
		mNotificationClosed = (ImageButton) findViewById(R.id.imagebutton_notification_close);
		mNotificationClosed.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// mNotifications.clear();
				// mNotificationAdapter.notifyDataSetChanged();
				// hideNotificationLayout();
				// hideSwipeListView();
				mSwipeDeleteListView.removeAllItem();
			}
		});
		// mCallView.setOnTouchListener(mTouchListener);
		// mCallView.setOnClickListener(mClickListener);
		mUnlockView = (AnimImageView) findViewById(R.id.defaulttheme_unlock);
		this.setOnTouchListener(mTouchListener);
		this.setOnClickListener(mClickListener);
		mTimerMsgLayout = (LinearLayout) findViewById(R.id.defaulttheme_time_msg_layout);
		mCameraView = (AnimImageView) findViewById(R.id.defaulttheme_camera);
		// mCameraView.setOnTouchListener(mTouchListener);
		// mCameraView.setOnClickListener(mClickListener);
		mSwipeDeleteListView = (SwipeDeleteListView) findViewById(R.id.defaulttheme_msgcenter);
		mSwipeDeleteListView.setAdapter(mNotificationAdapter);
		mSwipeDeleteListView.setVisibility(View.GONE);
		mNotificationAdapter.notifyDataSetChanged();
		mSwipeDeleteListView.setOnDismissCallback(new OnDismissCallback() {

			@Override
			public void onOpen(int position) {
				try {
					AppNotification appNotification = mNotificationAdapter
							.getItem(position);

					if (appNotification.type == 1) {
						mNotifications.remove(appNotification);
						mNotificationAdapter.notifyDataSetChanged();
						if (mNotifications.size() == 0) {
							hideSwipeListView();
						}

						if (mKeyguardCallback != null) {
							if (mKeyguardCallback instanceof KeyguardCallbackImpl) {
								((KeyguardCallbackImpl) mKeyguardCallback)
										.unlock2NotificationSetting();
							}
						}

					} else {
						if (mKeyguardCallback != null) {
							if (mKeyguardCallback instanceof KeyguardCallbackImpl) {
								((KeyguardCallbackImpl) mKeyguardCallback)
										.unlockByNotification(appNotification.mPendingIntent);
							}
						}
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
					mNotificationAdapter.notifyDataSetChanged();
					if (mNotifications.size() == 0) {
						hideSwipeListView();
					}
					if (need) {
						if (mKeyguardCallback != null) {
							if (mKeyguardCallback instanceof KeyguardCallbackImpl) {
								((KeyguardCallbackImpl) mKeyguardCallback)
										.unlock2NotificationSetting();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onRemoveAllItem() {
				try {
					mNotifications.clear();
					mNotificationAdapter.notifyDataSetChanged();
					hideSwipeListView();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
		// mMsgListView.setAdapter(mMsgAdapter);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

		// add by Jack
		final View[] aryView = CyWaveHelper.buildAry(mContext);
		if (Build.VERSION.SDK_INT > 11) {
			if (CyWallpaperHelper.useWave()) {
				addView(aryView[0], 0);
				addView(aryView[1], 1);
			} else {
				aryView[0] = null;
				addView(aryView[1], 0);
			}
		} else {
			aryView[0] = null;
			addView(aryView[1], 0);
		}
		CyWaveHelper.setCb(new CyWaveHpCallback() {
			@Override
			public void removeWave() {
				post(new Runnable() {
					@Override
					public void run() {
						CyTool.log("Rw");
						if (aryView[1] != null) {
							int index = indexOfChild(aryView[1]);
							CyTool.log("aryView[1] index=" + index);
							if (index < 0) {
								addView(aryView[1], 0);
								CyTool.log("addView");
							}
						}
						if (aryView[0] != null) {
							int index = indexOfChild(aryView[0]);
							CyTool.log("aryView[0] index=" + index);
							if (index > -1) {
								removeView(aryView[0]);
								CyTool.log("removeView");
							}
						}
					}
				});
			}
		});
		// end

		mBottomView = (BottomView) findViewById(R.id.defaulttheme_bottomview);
		mBottomView.setUnlockListener(new UnlockListener() {

			@Override
			public void onUnlockAnimEnd() {
				switch (mMoveMode) {
				case MOVEMODE_UNKNOW:
					break;
				case MOVEMODE_LEFT:
					mKeyguardCallback.unlock2Camera();
					break;
				case MOVEMODE_RIGHT:
					mKeyguardCallback.unlock2Phone(mMissedCallCount);
					break;
				case MOVEMODE_UP_DOWN:
					mKeyguardCallback.unlock();
					break;
				}
			}

			@Override
			public void onClickAnimEnd() {
				mCallView.setImageResource(R.drawable.icon_defaulttheme_call);
				mCameraView
						.setImageResource(R.drawable.icon_defaulttheme_camera);
			}

		});
		mHintTextView = (TextView) findViewById(R.id.defaulttheme_hint_label);
	}

	@Override
	public void requestLayout() {
		if (isTouching)
			return;
		super.requestLayout();
	}

	private AnimatorSet unlockClickAnim;
	private AnimatorSet callClickAnim;
	private AnimatorSet cameraClickAnim;

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mMoveMode != MOVEMODE_UNKNOW)
				return;
			mHintTextView.setVisibility(View.VISIBLE);
			switch (clickType) {
			case CLICK_TYPE_UNLOCK:
				timerLayer.clearAnimation();
				mCallView.setImageResource(R.drawable.icon_defaulttheme_call);
				mCameraView
						.setImageResource(R.drawable.icon_defaulttheme_camera);
				mHintTextView.setText(R.string.hint_defaulttheme_unlock);
				if (unlockClickAnim == null) {
					unlockClickAnim = new AnimatorSet();
					ValueAnimator firstAnim = ObjectAnimator.ofFloat(
							timerLayer, "y", -getWidth() / 6).setDuration(150);
					firstAnim.setInterpolator(new DecelerateInterpolator(0.9f));
					ValueAnimator firstBackAnim = ObjectAnimator.ofFloat(
							timerLayer, "y", 0).setDuration(200);
					firstBackAnim.setInterpolator(new AccelerateInterpolator(
							0.9f));
					ValueAnimator secondAnim = ObjectAnimator.ofFloat(
							timerLayer, "y", -getWidth() / 32).setDuration(30);
					secondAnim.setInterpolator(new DecelerateInterpolator());
					ValueAnimator secondBackAnim = ObjectAnimator.ofFloat(
							timerLayer, "y", 0).setDuration(30);
					secondBackAnim
							.setInterpolator(new AccelerateInterpolator());
					unlockClickAnim.play(firstAnim).before(firstBackAnim);
					unlockClickAnim.play(firstBackAnim).before(secondAnim);
					unlockClickAnim.play(secondAnim).before(secondBackAnim);
					unlockClickAnim.play(secondBackAnim);
					unlockClickAnim.addListener(new AnimatorListener() {

						@Override
						public void onAnimationStart(Animator arg0) {

						}

						@Override
						public void onAnimationRepeat(Animator arg0) {

						}

						@Override
						public void onAnimationEnd(Animator arg0) {
							reset();
						}

						@Override
						public void onAnimationCancel(Animator arg0) {

						}
					});
				}
				if (!unlockClickAnim.isRunning()) {
					mUnlockView.doScaleAnim(AnimImageView.MODE_HIGHLIGHT);
					unlockClickAnim.start();
				}
				break;
			case CLICK_TYPE_CALL:
				mCallView
						.setImageResource(R.drawable.icon_defaulttheme_call_black);
				mCameraView
						.setImageResource(R.drawable.icon_defaulttheme_camera);
				mHintTextView.setText(R.string.hint_defaulttheme_call);
				mBottomView.doClickAnim(true);
				if (callClickAnim == null) {
					callClickAnim = new AnimatorSet();
					ValueAnimator firstAnim = ObjectAnimator.ofFloat(
							timerLayer, "x", getWidth() / 6).setDuration(150);
					firstAnim.setInterpolator(new DecelerateInterpolator(0.9f));
					ValueAnimator firstBackAnim = ObjectAnimator.ofFloat(
							timerLayer, "x", 0).setDuration(200);
					firstBackAnim.setInterpolator(new AccelerateInterpolator(
							0.9f));
					ValueAnimator secondAnim = ObjectAnimator.ofFloat(
							timerLayer, "x", getWidth() / 32).setDuration(30);
					secondAnim.setInterpolator(new DecelerateInterpolator());
					ValueAnimator secondBackAnim = ObjectAnimator.ofFloat(
							timerLayer, "x", 0).setDuration(30);
					secondBackAnim
							.setInterpolator(new AccelerateInterpolator());
					callClickAnim.play(firstAnim).before(firstBackAnim);
					callClickAnim.play(firstBackAnim).before(secondAnim);
					callClickAnim.play(secondAnim).before(secondBackAnim);
					callClickAnim.play(secondBackAnim);
					callClickAnim.addListener(new AnimatorListener() {

						@Override
						public void onAnimationStart(Animator arg0) {

						}

						@Override
						public void onAnimationRepeat(Animator arg0) {

						}

						@Override
						public void onAnimationEnd(Animator arg0) {
							reset();
						}

						@Override
						public void onAnimationCancel(Animator arg0) {

						}
					});
				}
				if (!callClickAnim.isRunning()) {
					mCallView.doScaleAnim(AnimImageView.MODE_HIGHLIGHT);
					callClickAnim.start();
				}
				break;
			case CLICK_TYPE_CAMERA:
				mCameraView
						.setImageResource(R.drawable.icon_defaulttheme_camera_black);
				mCallView.setImageResource(R.drawable.icon_defaulttheme_call);
				mHintTextView.setText(R.string.hint_defaulttheme_camera);
				mBottomView.doClickAnim(false);
				if (cameraClickAnim == null) {
					cameraClickAnim = new AnimatorSet();
					ValueAnimator firstAnim = ObjectAnimator.ofFloat(
							timerLayer, "x", -getWidth() / 6).setDuration(150);
					firstAnim.setInterpolator(new DecelerateInterpolator(0.9f));
					ValueAnimator firstBackAnim = ObjectAnimator.ofFloat(
							timerLayer, "x", 0).setDuration(200);
					firstBackAnim.setInterpolator(new AccelerateInterpolator(
							0.9f));
					ValueAnimator secondAnim = ObjectAnimator.ofFloat(
							timerLayer, "x", -getWidth() / 32).setDuration(30);
					secondAnim.setInterpolator(new DecelerateInterpolator());
					ValueAnimator secondBackAnim = ObjectAnimator.ofFloat(
							timerLayer, "x", 0).setDuration(50);
					secondBackAnim
							.setInterpolator(new AccelerateInterpolator());
					cameraClickAnim.play(firstAnim).before(firstBackAnim);
					cameraClickAnim.play(firstBackAnim).before(secondAnim);
					cameraClickAnim.play(secondAnim).before(secondBackAnim);
					cameraClickAnim.play(secondBackAnim);
					cameraClickAnim.addListener(new AnimatorListener() {

						@Override
						public void onAnimationStart(Animator arg0) {

						}

						@Override
						public void onAnimationRepeat(Animator arg0) {

						}

						@Override
						public void onAnimationEnd(Animator arg0) {
							reset();
						}

						@Override
						public void onAnimationCancel(Animator arg0) {

						}
					});
				}
				if (!cameraClickAnim.isRunning()) {
					mCameraView.doScaleAnim(AnimImageView.MODE_HIGHLIGHT);
					cameraClickAnim.start();

				}
				break;

			default:
				break;
			}
			mHintTextView.removeCallbacks(hintRunnable);
			mHintTextView.postDelayed(hintRunnable, 3000);
		}
	};

	Runnable hintRunnable = new Runnable() {

		@Override
		public void run() {
			mHintTextView.setVisibility(View.GONE);
		}
	};

	private final int CLICK_TYPE_UNLOCK = 0;
	private final int CLICK_TYPE_CALL = 1;
	private final int CLICK_TYPE_CAMERA = 2;
	private int clickType = CLICK_TYPE_UNLOCK;
	private RectF mCallRect;
	private RectF mCameraRect;
	private float mDownX;
	private float mDownY;
	private int mUnlockOffset;

	private final int MOVEMODE_UNKNOW = 0;
	private final int MOVEMODE_UP_DOWN = 1;
	private final int MOVEMODE_LEFT = 2;
	private final int MOVEMODE_RIGHT = 3;
	private int mMoveMode = MOVEMODE_UNKNOW;
	private boolean canTouch = true;

	private OnTouchListener mTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// add by Jack
			CyWaveHelper.judgeTouchEvent(event);
			// end
			if (event.getPointerCount() != 1) {
				canTouch = false;
				reset();
				return true;
			}
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				reset();
				canTouch = true;
				if (mUnlockOffset == 0) {
					mUnlockOffset = timerLayer.getWidth() / 4;
				}
				if (mCallRect == null) {
					int[] location = new int[2];
					mCallView.getLocationInWindow(location);
					mCallRect = new RectF();
					mCallRect.left = location[0];
					mCallRect.top = location[1];
					mCallRect.right = mCallRect.left + mCallView.getWidth();
					mCallRect.bottom = mCallRect.top + mCallView.getHeight();
				}
				if (mCameraRect == null) {
					int[] location = new int[2];
					mCameraView.getLocationInWindow(location);
					mCameraRect = new RectF();
					mCameraRect.left = location[0];
					mCameraRect.top = location[1];
					mCameraRect.right = mCameraRect.left
							+ mCameraView.getWidth();
					mCameraRect.bottom = mCameraRect.top
							+ mCameraView.getHeight();
				}

				if (mCallRect.contains((int) event.getX(), (int) event.getY())) {
					clickType = CLICK_TYPE_CALL;
				} else if (mCameraRect.contains((int) event.getX(),
						(int) event.getY())) {
					clickType = CLICK_TYPE_CAMERA;
				} else {
					clickType = CLICK_TYPE_UNLOCK;
				}
				mDownX = event.getX();
				mDownY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				if (!canTouch)
					return true;
				float offsetX = event.getX() - mDownX;
				float offsetY = event.getY() - mDownY;
				switch (mMoveMode) {
				case MOVEMODE_UNKNOW:
					float offsetXAbs = Math.abs(event.getX() - mDownX);
					float offsetYAbs = Math.abs(offsetY);
					// if (offsetXAbs > mUnlockOffset / 2
					// || offsetYAbs > mUnlockOffset / 2) {
					// break;
					// }
					if (offsetXAbs > getWidth() / 20
							|| offsetYAbs > getWidth() / 20) {
						if (offsetXAbs < offsetYAbs) {// 上滑
							onSelectedChanged(MOVEMODE_UP_DOWN);
						} else {
							if (event.getX() - mDownX > 0) {// 右滑-电话
								onSelectedChanged(MOVEMODE_RIGHT);
							} else {// 左滑-相机
								onSelectedChanged(MOVEMODE_LEFT);
							}
						}
					}
					break;
				case MOVEMODE_UP_DOWN:
					// if (offsetY > 0) {
					// ViewHelper.setTranslationY(timerLayer, 0);
					// ViewHelper.setAlpha(timerLayer, 1);
					// break;
					// }
					ViewHelper.setTranslationY(mTimerMsgLayout,
							offsetY < 0 ? offsetY : 0);
					float alpha = (timerLayer.getHeight() + offsetY)
							/ (float) timerLayer.getHeight();
					if (alpha > 1)
						alpha = 1;
					if (alpha < 0)
						alpha = 0;
					ViewHelper.setAlpha(mTimerMsgLayout, alpha);
					break;
				case MOVEMODE_RIGHT:
					ViewHelper.setTranslationX(mTimerMsgLayout, offsetX);
					float scaleRight = offsetX / (getWidth() / 2) + 1;
					mBottomView.onMoveAnim(true, scaleRight);
					if (offsetX < 0) {
						onSelectedChanged(MOVEMODE_LEFT);
					}
					break;
				case MOVEMODE_LEFT:
					ViewHelper.setTranslationX(mTimerMsgLayout, offsetX);
					float scaleLeft = -offsetX / (getWidth() / 2) + 1;
					mBottomView.onMoveAnim(false, scaleLeft);
					if (offsetX > 0) {
						onSelectedChanged(MOVEMODE_RIGHT);
					}
					break;

				default:
					break;
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				if (!canTouch)
					return true;
				boolean isUnlock = false;
				switch (mMoveMode) {
				case MOVEMODE_UNKNOW:
					reset();
					return false;
				case MOVEMODE_LEFT:
					if (event.getX() - mDownX <= -mUnlockOffset) {
						isUnlock = true;
						animToUnlock();
					}
					break;
				case MOVEMODE_RIGHT:
					if (event.getX() - mDownX >= mUnlockOffset) {
						isUnlock = true;
						animToUnlock();
					}
					break;
				case MOVEMODE_UP_DOWN:

					if (event.getY() - mDownY <= -mUnlockOffset
							|| ViewHelper.getAlpha(mTimerMsgLayout) <= 0) {
						isUnlock = true;
						animToUnlock();
					}
					break;
				}
				if (!isUnlock) {
					if (mMoveMode != MOVEMODE_UNKNOW)
						animToReset();
				}
				break;

			default:
				break;
			}
			return false;
		}

	};

	private void onSelectedChanged(int mode) {
		mMoveMode = mode;
		mCallView.doScaleAnim(mode == MOVEMODE_RIGHT ? MODE_SELECTED
				: MODE_GONE);
		mUnlockView.doScaleAnim(mode == MOVEMODE_UP_DOWN ? MODE_SELECTED
				: MODE_GONE);
		mCameraView.doScaleAnim(mode == MOVEMODE_LEFT ? MODE_SELECTED
				: MODE_GONE);
	}

	private void animToReset() {
		if (mMoveMode == MOVEMODE_LEFT || mMoveMode == MOVEMODE_RIGHT)
			mBottomView.resetAnim(mMoveMode == MOVEMODE_RIGHT);
		mUnlockView.doScaleAnim(MODE_NORMAL);
		mCameraView.doScaleAnim(MODE_NORMAL);
		mCallView.doScaleAnim(MODE_NORMAL);
		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(mTimerMsgLayout, "x", 0f),
				ObjectAnimator.ofFloat(mTimerMsgLayout, "y", 0f),
				ObjectAnimator.ofFloat(mTimerMsgLayout, "alpha", 1f));
		set.setDuration(300).start();
	}

	private void animToUnlock() {
		if (mMoveMode == MOVEMODE_LEFT || mMoveMode == MOVEMODE_RIGHT)
			mBottomView.unlockAnim(mMoveMode == MOVEMODE_RIGHT);
		if (ProviderHelper.getVibrateEnable(mContext,
				Constants.SKY_LOCKER_DEFAULT_THEME)) {
			try {
				((Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE))
						.vibrate(Util.VIBRATE_TIME);
			} catch (Exception e) {
			}
		}
		ValueAnimator unlockAnim = null;
		switch (mMoveMode) {
		case MOVEMODE_UNKNOW:
			break;
		case MOVEMODE_LEFT:
			unlockAnim = ObjectAnimator.ofFloat(mTimerMsgLayout, "x",
					-timerLayer.getWidth()).setDuration(200);
			unlockAnim.setInterpolator(new DecelerateInterpolator(0.9f));
			unlockAnim.start();
			break;
		case MOVEMODE_RIGHT:
			unlockAnim = ObjectAnimator.ofFloat(mTimerMsgLayout, "x",
					timerLayer.getWidth()).setDuration(200);
			unlockAnim.setInterpolator(new DecelerateInterpolator(0.9f));
			unlockAnim.start();
			break;
		case MOVEMODE_UP_DOWN:
			unlockAnim = ObjectAnimator.ofFloat(mTimerMsgLayout, "y",
					-mTimerMsgLayout.getHeight()).setDuration(200);
			unlockAnim.setInterpolator(new DecelerateInterpolator(0.9f));
			unlockAnim.start();
			break;
		}
		if (unlockAnim == null){
			animToReset();
			return;
		}
		unlockAnim.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {

			}

			@Override
			public void onAnimationRepeat(Animator arg0) {

			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				switch (mMoveMode) {
				case MOVEMODE_UNKNOW:
					break;
				// case MOVEMODE_LEFT:
				// if (!noPassword)
				// mKeyguardCallback.unlock2Camera();
				// break;
				// case MOVEMODE_RIGHT:
				// if (!noPassword)
				// mKeyguardCallback.unlock2Phone(mMissedCallCount);
				// break;
				case MOVEMODE_UP_DOWN:
					mKeyguardCallback.unlock();
					break;
				}
			}

			@Override
			public void onAnimationCancel(Animator arg0) {
				animToReset();
			}
		});
	}

	@Override
	public void onPause() {
		// add by Jack
		CyWaveHelper.onPause();
		// end
	}

	public void onResume() {
		// add by Jack
		CyWaveHelper.onResume();
		// end
	}

	@Override
	public void cleanUp() {
		// add by Jack
		mHintTextView.removeCallbacks(hintRunnable);
		mBottomView.cleanUp();
		// removeView(CyWaveHelper.getWaveView());
		CyWaveHelper.onDestroy();
		index = -1;
		// end
	}

	@Override
	public void reset() {
		mMoveMode = MOVEMODE_UNKNOW;
		mBottomView.resetDirect();
		mUnlockView.reset();
		mCameraView.reset();
		mCameraView.setImageResource(R.drawable.icon_defaulttheme_camera);
		mCallView.reset();
		mCallView.setImageResource(R.drawable.icon_defaulttheme_call);
		mTimerMsgLayout.clearAnimation();
		ViewHelper.setX(mTimerMsgLayout, 0);
		ViewHelper.setY(mTimerMsgLayout, 0);
		ViewHelper.setTranslationX(mTimerMsgLayout, 0);
		ViewHelper.setTranslationY(mTimerMsgLayout, 0);
		ViewHelper.setAlpha(mTimerMsgLayout, 1);
		mSwipeDeleteListView.resetAllItem();
	}

	@Override
	public void onRefreshBatteryInfo(boolean pluggedIn, int batteryLevel) {
		mBatteryTextView.setVisibility(pluggedIn ? View.VISIBLE
				: View.INVISIBLE);
		String chargeText = mContext.getString(R.string.charge_values,
				batteryLevel);
		mBatteryTextView.setText(chargeText);
	}

	@Override
	public void onTimeChanged() {
		timerLayer.update();
	}

	@Override
	public void onMissedCallChanged(int count, long lastTime, String info) {
		mMissedCallCount = count;
		if (lastTime > 0
				&& lastTime == Util.getPreferenceLong(mContext,
						Util.SAVE_KEY_IGNORE_CALL_TIME, 0)) {
			count = 0;
		}
		if (count == 0)
			return;
	}

	@Override
	public void onUnreadSMSChanged(int count, long lastTime, String lastMsg) {
		if (lastTime > 0
				&& lastTime == Util.getPreferenceLong(mContext,
						Util.SAVE_KEY_IGNORE_SMS_TIME, 0)) {
			count = 0;
		}
		if (count == 0)
			return;
	}

	@Override
	public void onUpdateTemperature(int code, String low, String high) {
	}

	@Override
	public void onCityChanged(String cityName, String woeidStr) {
	}

	@Override
	public void onCityInit(String cityname, String lowString,
			String highsString, int code, String woeid) {
	}

	@Override
	public void setKeyguardCallback(KeyguardCallback keyguardCallback) {
		this.mKeyguardCallback = keyguardCallback;

		// ProviderHelper.initProvide(mKeyguardCallback.getPackageName());
	}

	// private static final String[] aryDevice = {"LG-D802","Nexus 4"};
	private CoreBitmapObj getCoreBitmaoObj(int w, int h, int resId) {
		CoreBitmapObj obj = null;
		// boolean useThin = false;
		// String device = Build.MODEL;
		// if(device!=null){
		// for(int i=0;i<aryDevice.length;i++){
		// if(device.equals(aryDevice[i])){
		// useThin = true;
		// break;
		// }
		// }
		// }
		// if(useThin){
		// obj = ImageUtil.getWallpaper(
		// mContext,
		// R.drawable.wallpaper_thin,
		// Constants.SKY_LOCKER_DEFAULT_THEME,
		// w, h,
		// false
		// );
		// float scale = w/768f;
		// obj.setScale(scale);
		// obj.setX(111);
		// obj.setY(0);
		// }else{
		obj = ImageUtil.getWallpaper(mContext, resId,
				Constants.SKY_LOCKER_DEFAULT_THEME, w, h, false, false);
		// }
		return obj;
	}

	private int[] wallpaperIds = new int[] { R.drawable.wallpaper_city,
			R.drawable.wallpaper1, R.drawable.wallpaper2, R.drawable.wallpaper3 };

	private Random mRandom = new Random();
	private Future<Bitmap> mFuture;
	CoreBitmapObj obj;
	Bitmap mBitmap;

	// add by Jack
	private void setBmp(int w, int h) {
		long time = System.currentTimeMillis();
		if (index == -1) {
			index = mRandom.nextInt(wallpaperIds.length);
		}
		obj = getCoreBitmaoObj(w, h, wallpaperIds[index]);

		CyTool.log("Cut bitmap cost:" + (System.currentTimeMillis() - time));

		// add by Jack
		if (obj != null) {
			Bitmap bmp = obj.getBitmap();
			mBitmap = bmp;
			CyWaveHelper.setBmp(bmp, false);
		} else {
			CyWaveHelper.setBmp(null);
		}

		// end
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (w == oldw && h == oldh)
			return;
		mBottomView.setInitPosition(mCallView.getMeasuredWidth(),
				mCallView.getMeasuredHeight());
		if (CyWallpaperHelper.setSize(w, h)) {
			setBmp(w, h);
		}

		if (this.mKeyguardCallback != null && this.mKeyguardCallback.isSecure()
				&& obj != null) {
			mFuture = Executors.newSingleThreadExecutor()
					.submit(new BlurCallable4DefaultTheme(getContext(), obj
							.getBitmap()));
		}
		mTimeLayerPaddingTop = 332 * h / 1280;
		if (mKeyguardCallback != null
				&& mKeyguardCallback.isStatusBarTransparency()) {
			mTimeLayerPaddingTop += ImageUtil.getStatusBarHeight(mContext);
		}
		timerLayer.setPadding(0, mTimeLayerPaddingTop, 0, 0);

	}

	private int mTimeLayerPaddingTop = 332;
	private Comparator<AppNotification> mDescendComparator;

	@Override
	public Bitmap getBlurBitmap() {
		if (mFuture != null) {
			try {
				return mFuture.get();
			} catch (InterruptedException e) {
			} catch (ExecutionException e) {
			}
		}
		return null;
	}

	public void showSwipeListView(AppNotification appNotification) {
		try {
			appNotification
					.setLogo(mPackageManager
							.getApplicationIcon(appNotification.mPackageName),
							mContext);
		} catch (NameNotFoundException e) {
		}
		mNotificationClosed.setVisibility(View.VISIBLE);
		CyWaveHelper.setBmp(getBlurBitmap(), false);
		mSwipeDeleteListView.setVisibility(View.VISIBLE);
		if (!Util.NOTIFICATION_DEBUG) {
			mNotifications.remove(appNotification);
		}
		// if (!Util.getPreferenceBoolean(mContext, Util.HAS_SHOW_SWIPE_TIP,
		// false)) {
		// AppNotification appNotification2 = new AppNotification();
		// appNotification2.type = 1;
		// appNotification2.mPackageName="ttt";
		// mNotifications.add(appNotification2);
		// Util.putPreferenceBoolean(mContext, Util.HAS_SHOW_SWIPE_TIP,
		// true);
		// }
		mNotifications.add(appNotification);
		Collections.sort(mNotifications, mDescendComparator);
		mNotificationAdapter.notifyDataSetChanged();
		mSwipeDeleteListView.resetAllItem();
		layoutTimeLayer();
	}

	public void hideSwipeListView() {
		try {
			// Log.d("jiangbin",
			// "--------------> isRecycle "+mBitmap.isRecycled());
			CyWaveHelper.setBmp(mBitmap, false);
		} catch (Exception e) {
		}
		mNotificationClosed.setVisibility(View.GONE);
		mSwipeDeleteListView.setVisibility(View.GONE);
		layoutTimeLayer();
	}

	private void layoutTimeLayer() {
		int paddingTop = mTimeLayerPaddingTop - mNotifications.size() * 35
				* getHeight() / 1280;
		if (paddingTop < 232 * getHeight() / 1280) {
			paddingTop = 232 * getHeight() / 1280;
		}
		timerLayer.setPadding(0, paddingTop, 0, 0);
	}

	public void needAddPhoneNotification(AppNotification mPhoneNotification) {
		if (!Util.NOTIFICATION_DEBUG) {
			mNotifications.remove(mPhoneNotification);
		}
		mNotifications.add(mPhoneNotification);
		Collections.sort(mNotifications, mDescendComparator);
		mNotificationAdapter.setGroup(mNotifications);
		mNotificationAdapter.notifyDataSetChanged();
	}

	public void removePhone(AppNotification appNotification) {
		try {
			mNotifications.remove(appNotification);
			mNotificationAdapter.notifyDataSetChanged();
			if (mNotifications.size() == 0) {
				hideSwipeListView();
			}
		} catch (Exception e) {
		}
	}

}
