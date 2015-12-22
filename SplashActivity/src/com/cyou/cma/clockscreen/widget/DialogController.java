package com.cyou.cma.clockscreen.widget;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.widget.CustomAlertDialog.VisibleCallback;
import com.cyou.cma.clockscreen.widget.material.LButton;
import com.cyou.cma.clockscreen.widget.material.LImageButton;

/**
 * 自定义dialog的逻辑处理类。 根据传进来的参数定制dialog样式。
 * 
 * @author yunnan，google
 */
public class DialogController {
	private final Context mContext;
	private final DialogInterface mDialogInterface;
	private final Window mWindow;

	private ListView mListView;

	private View mView; // 传进来的自定义view

	private LButton mButtonPositive; // 左边按钮
	private CharSequence mButtonPositiveText;
	private Message mButtonPositiveMessage;

	private LButton mButtonNegative; // 右边按钮
	private CharSequence mButtonNegativeText;
	private Message mButtonNegativeMessage;

	private LButton mButtonNeutral; // 中间按钮
	private CharSequence mButtonNeutralText;
	private Message mButtonNeutralMessage;

	private ScrollView mScrollView; // 内容区域的scrollView

	private int mIconId = -1; // 标题提示图标
	private Drawable mIcon;
	private ImageView mIconView;
	public LImageButton mCloseImageView;

	private TextView mTitleView; // 标题title
	private CharSequence mTitle; // 标题信息

	private TextView mMessageView; // 提示信息textView
	private CharSequence mMessage; // 提示信息

	private ListAdapter mAdapter;
	private int mCheckedItem = -1; // 多选列表时，选中的textview

	private int mAlertDialogLayout; // dialog整体布局id
	private int mListLayout;
	private int mMultiChoiceItemLayout;
	private int mSingleChoiceItemLayout;
	private int mListItemLayout;
	private int mListImgItemLayout;

	public boolean clickPositiveBtnDismiss; // 点击确认按钮是否让窗体消失
	public boolean centerMsg; // 是否让显示文字信息居中
	public boolean warnTitle; // 标题是否是警告信息，是则改变其文字颜色为红色，默认为蓝色

	private Handler mHandler;

	public DialogInterface getDialogInterface() {
		return mDialogInterface;
	}

	View.OnClickListener mButtonHandler = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Message m = null;
			if (v == mButtonPositive && mButtonPositiveMessage != null) {
				m = Message.obtain(mButtonPositiveMessage);
			} else if (v == mButtonNegative && mButtonNegativeMessage != null) {
				m = Message.obtain(mButtonNegativeMessage);
			} else if (v == mButtonNeutral && mButtonNeutralMessage != null) {
				m = Message.obtain(mButtonNeutralMessage);
			}
			if (m != null) {
				m.sendToTarget();
			}

			if (!clickPositiveBtnDismiss && v == mButtonPositive) {
				return;
			}
			// Post a message so we dismiss after the above handlers are
			// executed
			mHandler.obtainMessage(ButtonHandler.MSG_DISMISS_DIALOG,
					mDialogInterface).sendToTarget();
		}
	};

	private static final class ButtonHandler extends Handler {
		// Button clicks have Message.what as the BUTTON{1,2,3} constant
		private static final int MSG_DISMISS_DIALOG = 1;

		private WeakReference<DialogInterface> mDialog;

		public ButtonHandler(DialogInterface dialog) {
			mDialog = new WeakReference<DialogInterface>(dialog);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case DialogInterface.BUTTON_POSITIVE:
			case DialogInterface.BUTTON_NEGATIVE:
			case DialogInterface.BUTTON_NEUTRAL:
				((DialogInterface.OnClickListener) msg.obj).onClick(
						mDialog.get(), msg.what);
				break;

			case MSG_DISMISS_DIALOG:
				((DialogInterface) msg.obj).dismiss();
			}
		}
	}

	public DialogController(Context context, DialogInterface di, Window window) {
		mContext = context;
		mDialogInterface = di;
		mWindow = window;
		mHandler = new ButtonHandler(di);

		mAlertDialogLayout = R.layout.custom_alert_dialog;
		mListLayout = R.layout.select_dialog; // ListView
		mMultiChoiceItemLayout = R.layout.select_dialog_multichoice; // ListView的各个Item
		mSingleChoiceItemLayout = R.layout.select_dialog_singlechoice;
		mListItemLayout = R.layout.select_dialog_item;
		mListImgItemLayout = R.layout.select_dialog_img_item;
	}

	public void setPositiveButtonEnable(boolean enable) {
		mButtonPositive.setEnabled(enable);
	}

	/**
	 * 根据所有得到的参数，开始定制dialog
	 */
	public void installContent() {
		/* We use a custom title so never request a window title */
		mWindow.requestFeature(Window.FEATURE_NO_TITLE);
		mWindow.setContentView(mAlertDialogLayout);
		setupView();
	}

	private void setupView() {
		LinearLayout contentPanel = (LinearLayout) mWindow
				.findViewById(R.id.contentPanel);
		setupContent(contentPanel);

		boolean hasButtons = setupButtons(); // bottom button
		View buttonPanel = mWindow.findViewById(R.id.buttonPanel);
		if (!hasButtons) {
			buttonPanel.setVisibility(View.GONE);
		}

		LinearLayout topPanel = (LinearLayout) mWindow
				.findViewById(R.id.topPanel);
		boolean hasTitle = setupTitle(topPanel); // title
		if (hasTitle) {
			View divider = null;
			if (mMessage != null || mView != null || mListView != null) {
				divider = mWindow.findViewById(R.id.titleDivider);
				divider.setVisibility(View.VISIBLE);
			}
		}

		FrameLayout customPanel = null;
		if (mView != null) { // custom view
			customPanel = (FrameLayout) mWindow.findViewById(R.id.customPanel);
			FrameLayout custom = (FrameLayout) mWindow
					.findViewById(R.id.custom);
			custom.addView(mView, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
			if (mListView != null) {
				((LinearLayout.LayoutParams) customPanel.getLayoutParams()).weight = 0;
			}
		} else {
			mWindow.findViewById(R.id.customPanel).setVisibility(View.GONE);
		}

		setBackground();
	}

	private void setBackground() {

		// ListVIew与Adapter绑定
		if ((mListView != null) && (mAdapter != null)) {
			mListView.setAdapter(mAdapter);
			if (mCheckedItem > -1) {
				mListView.setItemChecked(mCheckedItem, true);
				mListView.setSelection(mCheckedItem);
			}
		}
	}

	/**
	 * 判断是否有标题
	 * 
	 * @param topPanel
	 *            ，标题部分容器
	 * @return 有则返回true，否则返回false
	 */
	private boolean setupTitle(LinearLayout topPanel) {
		boolean hasTitle = true;

		final boolean hasTextTitle = !TextUtils.isEmpty(mTitle);
		mIconView = (ImageView) mWindow.findViewById(R.id.icon);
		mCloseImageView = (LImageButton) mWindow.findViewById(R.id.close);
		mCloseImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (visibleCallback != null) {
					visibleCallback.onclick();
				}
			}
		});
		if (hasTextTitle) {
			mTitleView = (TextView) mWindow.findViewById(R.id.alertTitle);
			mTitleView.setText(mTitle);
			if (warnTitle) {
				mTitleView.setTextColor(Color.parseColor("#fe4242"));
				View titleDivider = mWindow.findViewById(R.id.titleDivider);
				titleDivider.setBackgroundColor(Color.parseColor("#fe4242"));
			}
			if (mIconId > 0) { // title小图标
				mIconView.setImageResource(mIconId);
			} else if (mIcon != null) {
				mIconView.setImageDrawable(mIcon);
			} else if (mIconId == 0) {
				mTitleView.setPadding(mIconView.getPaddingLeft(),
						mIconView.getPaddingTop(), mIconView.getPaddingRight(),
						mIconView.getPaddingBottom());
				mIconView.setVisibility(View.GONE);
			}
			if (visibleCallback != null) {
				mCloseImageView.setVisibility(View.VISIBLE);
			} else {
				mCloseImageView.setVisibility(View.INVISIBLE);
			}
		} else {
			// Hide the title template
			View titleTemplate = mWindow.findViewById(R.id.title_template);
			titleTemplate.setVisibility(View.GONE);
			mIconView.setVisibility(View.GONE);
			topPanel.setVisibility(View.GONE);
			hasTitle = false;
		}
		return hasTitle;
	}

	/**
	 * 初始化内容区域
	 * 
	 * @param contentPanel
	 *            内容区域容器
	 */
	private void setupContent(LinearLayout contentPanel) {
		mScrollView = (ScrollView) mWindow.findViewById(R.id.scrollView);
		mScrollView.setFocusable(false);

		mMessageView = (TextView) mWindow.findViewById(R.id.message);
		if (mMessage != null) {
			if (centerMsg) {
				mMessageView.setGravity(Gravity.CENTER);
			}
			mMessageView.setText(mMessage);
		} else {
			mMessageView.setVisibility(View.GONE);
			contentPanel.removeView(mWindow.findViewById(R.id.scrollView));
			if (mListView != null) { // 如果ListView不为空
				int itemCount = mAdapter == null ? 0 : mAdapter.getCount();
				int param = MATCH_PARENT;
				int maxLine = getMaxListLineNum();
				if (itemCount > maxLine) { // 大于maxLine行，则固定高度
					int itemHeight = mContext.getResources()
							.getDimensionPixelSize(
									R.dimen.dialog_list_item_height_spcial);
					param = itemHeight * maxLine;
				}
				contentPanel.addView(mListView, new LinearLayout.LayoutParams(
						MATCH_PARENT, param));
			} else {
				contentPanel.setVisibility(View.GONE);
			}
		}
	}

	public int getMaxListLineNum() {
		return 6;
	}

	/**
	 * 检查是否需要初始化button
	 * 
	 * @return
	 */
	private boolean setupButtons() {
		int BIT_BUTTON_POSITIVE = 1;
		int BIT_BUTTON_NEGATIVE = 2;
		int BIT_BUTTON_NEUTRAL = 4;
		int whichButtons = 0;
		int btnCount = 0;
		// CY9027 ADD Begin
		// 设置按钮背景
		boolean isNegativeVisible = false;
		boolean isNeutralVisible = false;
		boolean isPositiveVisible = false;
		// CY9027 ADD end
		mButtonPositive = (LButton) mWindow.findViewById(R.id.button1);
		mButtonPositive.setOnClickListener(mButtonHandler);

		View v1 = mWindow.findViewById(R.id.view1);
		View v2 = mWindow.findViewById(R.id.view2);

		if (TextUtils.isEmpty(mButtonPositiveText)) {
			mButtonPositive.setVisibility(View.GONE);
			v2.setVisibility(View.GONE);
		} else {
			mButtonPositive.setText(mButtonPositiveText);
			mButtonPositive.setVisibility(View.VISIBLE);
			isPositiveVisible = true;
			whichButtons = whichButtons | BIT_BUTTON_POSITIVE;
			btnCount++;
		}

		mButtonNegative = (LButton) mWindow.findViewById(R.id.button2);
		mButtonNegative.setOnClickListener(mButtonHandler);

		if (TextUtils.isEmpty(mButtonNegativeText)) {
			mButtonNegative.setVisibility(View.GONE);
			v1.setVisibility(View.GONE);
		} else {
			isNegativeVisible = true;
			mButtonNegative.setText(mButtonNegativeText);
			mButtonNegative.setVisibility(View.VISIBLE);
			whichButtons = whichButtons | BIT_BUTTON_NEGATIVE;
			btnCount++;
		}

		mButtonNeutral = (LButton) mWindow.findViewById(R.id.button3);
		mButtonNeutral.setOnClickListener(mButtonHandler);

		if (TextUtils.isEmpty(mButtonNeutralText)) {
			mButtonNeutral.setVisibility(View.GONE);
			v1.setVisibility(View.GONE);
		} else {
			isNeutralVisible = true;
			mButtonNeutral.setText(mButtonNeutralText);
			mButtonNeutral.setVisibility(View.VISIBLE);
			whichButtons = whichButtons | BIT_BUTTON_NEUTRAL;
			btnCount++;
		}

		if (btnCount == 1) {
			v1.setVisibility(View.GONE);
			v2.setVisibility(View.GONE);
		}
		//
		// // 设置button的背景 2*2*2种case
		// if (isNegativeVisible) {
		// // 左边的可见
		// if (isNeutralVisible) {
		// // 中间的可见
		// if (isPositiveVisible) {
		// // 左中右都可见
		// mButtonPositive.setBackgroundResource(R.drawable.all_apps_dialog_button_right);
		// mButtonNegative.setBackgroundResource(R.drawable.all_apps_dialog_button_left);
		// mButtonNeutral.setBackgroundResource(R.drawable.all_apps_dialog_button_middle);
		// } else {
		// // //左中可见
		// mButtonNegative.setBackgroundResource(R.drawable.all_apps_dialog_button_right);
		// mButtonNeutral.setBackgroundResource(R.drawable.all_apps_dialog_button_left);
		// }
		// } else {
		// // 中间的不可见
		// if (isPositiveVisible) {
		// // 左右可见
		// mButtonPositive.setBackgroundResource(R.drawable.all_apps_dialog_button_right);
		// mButtonNegative.setBackgroundResource(R.drawable.all_apps_dialog_button_left);
		// } else {
		// // 左可见
		// mButtonNegative.setBackgroundResource(R.drawable.all_apps_dialog_button_all);
		// }
		// }
		//
		// } else {
		// // 左不可见
		// if (isNeutralVisible) {
		// // 中间的可见
		// if (isPositiveVisible) {
		// // 中右可见
		// mButtonPositive.setBackgroundResource(R.drawable.all_apps_dialog_button_right);
		// mButtonNeutral.setBackgroundResource(R.drawable.all_apps_dialog_button_left);
		// } else {
		// // 中可见
		// mButtonNeutral.setBackgroundResource(R.drawable.all_apps_dialog_button_all);
		// }
		// } else {
		// // 中间的不可见
		// if (isPositiveVisible) {
		// // 右可见
		// mButtonPositive.setBackgroundResource(R.drawable.all_apps_dialog_button_all);
		// } else {
		// // 左中右都不可见
		// }
		// }
		// }
		return whichButtons != 0;

	}

	public void setTitle(CharSequence title) {
		mTitle = title;
		if (mTitleView != null) {
			mTitleView.setText(title);
		}
	}

	public void setClickPositiveBtnDismiss(boolean value) {
		clickPositiveBtnDismiss = value;
	}

	public void setCenterMsg(boolean value) {
		centerMsg = value;
	}

	public void setWarnTitle(boolean value) {
		warnTitle = value;
	}

	public void setMessage(CharSequence message) {
		mMessage = message;
		if (mMessageView != null) {
			mMessageView.setText(message);
		}
	}

	public void setButton(int whichButton, CharSequence text,
			DialogInterface.OnClickListener listener, Message msg) {

		if (msg == null && listener != null) {
			msg = mHandler.obtainMessage(whichButton, listener);
		}

		switch (whichButton) {

		case DialogInterface.BUTTON_POSITIVE:
			mButtonPositiveText = text;
			mButtonPositiveMessage = msg;
			break;

		case DialogInterface.BUTTON_NEGATIVE:
			mButtonNegativeText = text;
			mButtonNegativeMessage = msg;
			break;

		case DialogInterface.BUTTON_NEUTRAL:
			mButtonNeutralText = text;
			mButtonNeutralMessage = msg;
			break;

		default:
			// throw new IllegalArgumentException("Button does not exist");
			break;
		}
	}

	public void setVisibleCallback(VisibleCallback visibleCallback) {
		this.visibleCallback = visibleCallback;
	}

	public void setIcon(int resId) {
		mIconId = resId;
		if (mIconView != null) {
			if (resId > 0) {
				mIconView.setImageResource(mIconId);
			} else if (resId == 0) {
				mIconView.setVisibility(View.GONE);
			}
		}
	}

	public void setIcon(Drawable icon) {
		mIcon = icon;
		if ((mIconView != null) && (mIcon != null)) {
			mIconView.setImageDrawable(icon);
		}
	}

	public void setView(View view) {
		mView = view;
	}

	public VisibleCallback visibleCallback;

	public static class DialogParams {
		public final Context mContext;
		public final LayoutInflater mInflater;

		public VisibleCallback visibleCallback;
		public int mIconId = 0;
		public Drawable mIcon;
		public CharSequence mTitle;
		public CharSequence mMessage;
		public CharSequence mPositiveButtonText;
		public DialogInterface.OnClickListener mPositiveButtonListener;
		public DialogInterface.OnClickListener mCloseListener;
		public CharSequence mNegativeButtonText;
		public DialogInterface.OnClickListener mNegativeButtonListener;
		public CharSequence mNeutralButtonText;
		public DialogInterface.OnClickListener mNeutralButtonListener;
		public boolean mCancelable;
		public boolean clickPositiveBtnDismiss;
		public boolean centerMsg;
		public boolean warnTitle;
		public OnCancelListener mOnCancelListener;

		public CharSequence[] mItems; // 字符串列表
		public Drawable[] mDrawables; // 图片数组，放在列表文字前面
		public ListAdapter mAdapter;
		public DialogInterface.OnClickListener mOnClickListener;

		public View mView;
		public boolean[] mCheckedItems; // 多选时选中和未选中的boolean值
		public boolean mIsMultiChoice;
		public boolean mIsSingleChoice;
		public int mCheckedItem = -1; // 当为单选选时，指定的默认选中位置
		public DialogInterface.OnMultiChoiceClickListener mOnCheckboxClickListener;
		public AdapterView.OnItemSelectedListener mOnItemSelectedListener;

		public DialogParams(Context context) {
			mContext = context;
			mCancelable = true;
			clickPositiveBtnDismiss = true;
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void apply(DialogController control) {
			control.setClickPositiveBtnDismiss(clickPositiveBtnDismiss);
			control.setCenterMsg(centerMsg);
			control.setWarnTitle(warnTitle);
			if (mTitle != null) {
				control.setTitle(mTitle);
			}
			if (mIcon != null) {
				control.setIcon(mIcon);
			}
			control.setVisibleCallback(visibleCallback);

			if (mIconId >= 0) {
				control.setIcon(mIconId);
			}
			if (mMessage != null) {
				control.setMessage(mMessage);
			}

			if (mPositiveButtonText != null) {
				control.setButton(DialogInterface.BUTTON_POSITIVE,
						mPositiveButtonText, mPositiveButtonListener, null);
			}
			if (mNegativeButtonText != null) {
				control.setButton(DialogInterface.BUTTON_NEGATIVE,
						mNegativeButtonText, mNegativeButtonListener, null);
			}
			if (mNeutralButtonText != null) {
				control.setButton(DialogInterface.BUTTON_NEUTRAL,
						mNeutralButtonText, mNeutralButtonListener, null);
			}
			if (mView != null) {
				control.setView(mView);
			}
			if ((mItems != null) || (mAdapter != null)) {
				createListView(control);
			}
		}

		/**
		 * 根据传进的参数，定制ListView的样式
		 * 
		 * @param control
		 */
		private void createListView(final DialogController control) {
			final ListView listView = (ListView) mInflater.inflate(
					control.mListLayout, null);
			ListAdapter adapter;
			if (mIsMultiChoice) {
				adapter = new ArrayAdapter<CharSequence>(mContext,
						control.mMultiChoiceItemLayout, R.id.text1, mItems) {
					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						View view = super
								.getView(position, convertView, parent);
						if (mCheckedItems != null) {
							boolean isItemChecked = mCheckedItems[position];
							if (isItemChecked) {
								listView.setItemChecked(position, true);
							}
						}
						return view;
					}
				};
			} else {
				if (mDrawables == null) {
					int layout = mIsSingleChoice ? control.mSingleChoiceItemLayout
							: control.mListItemLayout;
					adapter = (mAdapter != null) ? mAdapter
							: new ArrayAdapter<CharSequence>(mContext, layout,
									R.id.text1, mItems);
				} else {
					adapter = new ImageTextAdapter(control);
				}
			}

			control.mAdapter = adapter;
			control.mCheckedItem = mCheckedItem;

			if (mOnClickListener != null) {
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {
						mOnClickListener.onClick(control.mDialogInterface,
								position);
						if (!mIsSingleChoice) {
							control.mDialogInterface.dismiss();
						}
					}
				});
			} else if (mOnCheckboxClickListener != null) {
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {
						if (mCheckedItems != null) {
							mCheckedItems[position] = listView
									.isItemChecked(position);
						}
						mOnCheckboxClickListener.onClick(
								control.mDialogInterface, position,
								listView.isItemChecked(position));
					}
				});
			}

			if (mOnItemSelectedListener != null) {
				listView.setOnItemSelectedListener(mOnItemSelectedListener);
			}

			if (mIsSingleChoice) {
				listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
			} else if (mIsMultiChoice) {
				listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
			}

			control.mListView = listView;
		}

		private class ImageTextAdapter extends BaseAdapter {

			DialogController control;

			public ImageTextAdapter(DialogController control) {
				this.control = control;
			}

			@Override
			public int getCount() {
				return mItems.length;
			}

			@Override
			public Object getItem(int position) {
				return position;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = mInflater.inflate(control.mListImgItemLayout,
							null);
				}
				ImageView image1 = (ImageView) convertView
						.findViewById(R.id.image1);
				TextView text1 = (TextView) convertView
						.findViewById(R.id.text1);
				text1.setText(mItems[position]);
				if (position < mDrawables.length) {
					image1.setImageDrawable(mDrawables[position]);
				} else {
					// image1.setImageResource(R.drawable.icon);
				}
				return convertView;
			}

		}

	}

}
