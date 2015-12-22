package com.cyou.cma.clockscreen.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.cynad.cma.locker.R;

/**
 * 自定义的dialog，仿照AlertDialog的源代码进行编写。 这里去除了一些用的很少的属性，比如设置cursor列表等。
 * 不同于AlertDialog这里我们可以设置自己的dialog样式。 并且有必要时还可以多种样式进行切换。
 * 所有弹出框都可以使用CustomDialog，用法与AlertDialog相似。
 * 
 * @author yunnan
 */
public class CustomAlertDialog extends Dialog implements DialogInterface {

	public interface VisibleCallback {
		public void onclick();
	}

	private DialogController control;
	private static final int default_theme = R.style.customDialogStyle;

	protected CustomAlertDialog(Context context) {
		this(context, default_theme);
	}

	protected CustomAlertDialog(Context context, int theme) {
		super(context, theme);
		control = new DialogController(context, this, getWindow());
	}

	protected CustomAlertDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, default_theme);
		setCancelable(cancelable);
		setOnCancelListener(cancelListener);
		control = new DialogController(context, this, getWindow());
	}

	public void setPositiveButtonEnable(boolean enable) {
		control.setPositiveButtonEnable(enable);
	}

	public DialogController getController() {
		return control;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		control.installContent();
	}

	public static class Builder {
		private final DialogController.DialogParams P;

		public Builder(Context context) {
			P = new DialogController.DialogParams(context);
		}

		/**
		 * 根据resource id来设置title
		 * 
		 * @param titleId
		 * @return
		 */
		public Builder setTitle(int titleId) {
			P.mTitle = P.mContext.getText(titleId);
			return this;
		}

		/**
		 * 给定字符串来设置dialog的title
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(CharSequence title) {
			P.mTitle = title;
			return this;
		}

		/**
		 * 根据resource id来设置message
		 * 
		 * @param messageId
		 * @return
		 */
		public Builder setMessage(int messageId) {
			P.mMessage = P.mContext.getText(messageId);
			return this;
		}

		/**
		 * 给定字符串来设置dialog的message
		 * 
		 * @param message
		 * @return
		 */
		public Builder setMessage(CharSequence message) {
			P.mMessage = message;
			return this;
		}

		public Builder setVisible(VisibleCallback visibleCallback) {
			P.visibleCallback = visibleCallback;
			return this;
		}

		/**
		 * 根据resource id来设置title左边图标
		 * 
		 * @param iconId
		 * @return
		 */
		public Builder setIcon(int iconId) {
			P.mIconId = iconId;
			return this;
		}

		/**
		 * 给定drawable来设置dialog的title左边图标
		 * 
		 * @param icon
		 * @return
		 */
		public Builder setIcon(Drawable icon) {
			P.mIcon = icon;
			return this;
		}

		/**
		 * 设置message信息是否居中，默认居左
		 * 
		 * @param value
		 * @return
		 */
		public Builder setCenterMsg(boolean value) {
			P.centerMsg = value;
			return this;
		}

		/**
		 * 设置dialog弹出的是否为警告信息， 是则改变title文字的颜色为红色。默认为蓝色
		 * 
		 * @param value
		 * @return
		 */
		// public Builder setWarnTitle(boolean value){
		// P.warnTitle = value;
		// return this;
		// }

		/**
		 * 为左边的按钮设置一个事件监听，当按钮被按下时可以执行某些动作
		 * 
		 * @param textId
		 *            按钮中显示的文字的resource id
		 * @param listener
		 *            事件监听器
		 * @return
		 */
		public Builder setPositiveButton(int textId,
				final OnClickListener listener) {
			P.mPositiveButtonText = P.mContext.getText(textId);
			P.mPositiveButtonListener = listener;
			return this;
		}

		/**
		 * 为确定的按钮设置一个事件监听，当按钮被按下时可以执行某些动作
		 * 
		 * @param text
		 *            按钮中显示的文字字符串
		 * @param listener
		 *            事件监听器
		 * @return
		 */
		public Builder setPositiveButton(CharSequence text,
				final OnClickListener listener) {
			P.mPositiveButtonText = text;
			P.mPositiveButtonListener = listener;
			return this;
		}

		/**
		 * @param value
		 *            false点击确定不dismiss，默认为true
		 * @return
		 */
		public Builder setClickPositiveBtnDismiss(boolean value) {
			P.clickPositiveBtnDismiss = value;
			return this;
		}

		/**
		 * 为右边的按钮设置一个事件监听，当按钮被按下时可以执行某些动作
		 * 
		 * @param textId
		 *            按钮中显示的文字的resource id
		 * @param listener
		 *            事件监听器
		 * @return
		 */
		public Builder setNegativeButton(int textId,
				final OnClickListener listener) {
			P.mNegativeButtonText = P.mContext.getText(textId);
			P.mNegativeButtonListener = listener;
			return this;
		}

		/**
		 * 为右边的按钮设置一个事件监听，当按钮被按下时可以执行某些动作
		 * 
		 * @param text
		 *            按钮中显示的文字字符串
		 * @param listener
		 *            事件监听器
		 * @return
		 */
		public Builder setNegativeButton(CharSequence text,
				final OnClickListener listener) {
			P.mNegativeButtonText = text;
			P.mNegativeButtonListener = listener;
			return this;
		}

		/**
		 * 为中间的按钮设置一个事件监听，当按钮被按下时可以执行某些动作
		 * 
		 * @param textId
		 *            按钮中显示的文字的resource id
		 * @param listener
		 *            事件监听器
		 * @return
		 */
		public Builder setNeutralButton(int textId,
				final OnClickListener listener) {
			P.mNeutralButtonText = P.mContext.getText(textId);
			P.mNeutralButtonListener = listener;
			return this;
		}

		/**
		 * 为中间的按钮设置一个事件监听，当按钮被按下时可以执行某些动作
		 * 
		 * @param text
		 *            按钮中显示的文字字符串
		 * @param listener
		 *            事件监听器
		 * @return
		 */
		public Builder setNeutralButton(CharSequence text,
				final OnClickListener listener) {
			P.mNeutralButtonText = text;
			P.mNeutralButtonListener = listener;
			return this;
		}

		public Builder setCancelable(boolean cancelable) {
			P.mCancelable = cancelable;
			return this;
		}

		public Builder setOnCancelListener(OnCancelListener onCancelListener) {
			P.mOnCancelListener = onCancelListener;
			return this;
		}

		/**
		 * dialog将根据提供的数组resource id，显示为一个包含listView的列表。 并为其提供点击监听事件
		 * 
		 * @param itemsId
		 * @param listener
		 * @return
		 */
		public Builder setItems(int itemsId, final OnClickListener listener) {
			P.mItems = P.mContext.getResources().getTextArray(itemsId);
			P.mOnClickListener = listener;
			return this;
		}

		/**
		 * dialog将根据提供的字符串数组，显示为一个包含listView的列表。 并为其提供点击监听事件
		 * 
		 * @param itemsId
		 * @param listener
		 * @return
		 */
		public Builder setItems(CharSequence[] items,
				final OnClickListener listener) {
			P.mItems = items;
			P.mOnClickListener = listener;
			return this;
		}

		/**
		 * 以图片+文字列表的形式显示
		 * 
		 * @param items
		 * @param drawables
		 * @param listener
		 * @return
		 */
		public Builder setImgItems(CharSequence[] items, Drawable[] drawables,
				final OnClickListener listener) {
			P.mItems = items;
			P.mDrawables = drawables;
			P.mOnClickListener = listener;
			return this;
		}

		/**
		 * 提供一个ListAdapter，作为ListView的显示数据适配器
		 * 
		 * @param adapter
		 * @param listener
		 * @return
		 */
		public Builder setAdapter(final ListAdapter adapter,
				final OnClickListener listener) {
			P.mAdapter = adapter;
			P.mOnClickListener = listener;
			return this;
		}

		/**
		 * 设置多选列表
		 * 
		 * @param items
		 * @param checkedItems
		 *            默认选中的项
		 * @param listener
		 * @return
		 */
		public Builder setMultiChoiceItems(int itemsId, boolean[] checkedItems,
				final OnMultiChoiceClickListener listener) {
			P.mItems = P.mContext.getResources().getTextArray(itemsId);
			P.mOnCheckboxClickListener = listener;
			P.mCheckedItems = checkedItems;
			P.mIsMultiChoice = true;
			return this;
		}

		/**
		 * 设置多选列表
		 * 
		 * @param items
		 * @param checkedItems
		 *            默认选中的项
		 * @param listener
		 * @return
		 */
		public Builder setMultiChoiceItems(CharSequence[] items,
				boolean[] checkedItems,
				final OnMultiChoiceClickListener listener) {
			P.mItems = items;
			P.mOnCheckboxClickListener = listener;
			P.mCheckedItems = checkedItems;
			P.mIsMultiChoice = true;
			return this;
		}

		/**
		 * 设置单选框列表
		 * 
		 * @param itemsId
		 * @param checkedItem
		 *            选中的项
		 * @param listener
		 * @return
		 */
		public Builder setSingleChoiceItems(int itemsId, int checkedItem,
				final OnClickListener listener) {
			P.mItems = P.mContext.getResources().getTextArray(itemsId);
			P.mOnClickListener = listener;
			P.mCheckedItem = checkedItem;
			P.mIsSingleChoice = true;
			return this;
		}

		/**
		 * 设置单选框列表
		 * 
		 * @param itemsId
		 * @param checkedItem
		 *            选中的项
		 * @param listener
		 * @return
		 */
		public Builder setSingleChoiceItems(CharSequence[] items,
				int checkedItem, final OnClickListener listener) {
			P.mItems = items;
			P.mOnClickListener = listener;
			P.mCheckedItem = checkedItem;
			P.mIsSingleChoice = true;
			return this;
		}

		/**
		 * 为ListView的每一项设置监听
		 * 
		 * @param listener
		 * @return
		 */
		public Builder setOnItemSelectedListener(
				final AdapterView.OnItemSelectedListener listener) {
			P.mOnItemSelectedListener = listener;
			return this;
		}

		/**
		 * 为内容区域设置自定义的视图
		 * 
		 * @param view
		 * @return
		 */
		public Builder setView(View view) {
			P.mView = view;
			return this;
		}

		/**
		 * 创建dialog
		 * 
		 * @return
		 */
		public CustomAlertDialog create() {
			final CustomAlertDialog dialog = new CustomAlertDialog(P.mContext);
			P.apply(dialog.control);
			dialog.setCancelable(P.mCancelable);
			if (P.mCancelable) {
				dialog.setCanceledOnTouchOutside(true);
			}
			dialog.setOnCancelListener(P.mOnCancelListener);
			return dialog;
		}

		/**
		 * 创建并显示dialog
		 * 
		 * @return
		 */
		public CustomAlertDialog show() {
			CustomAlertDialog dialog = create();
			dialog.show();
			return dialog;
		}
	}

}
