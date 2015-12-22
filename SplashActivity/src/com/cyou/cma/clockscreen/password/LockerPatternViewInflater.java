package com.cyou.cma.clockscreen.password;

import java.util.List;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.password.widget.PatternView.Cell;

/**
 * 锁屏界面 手势解锁的渲染器
 * 
 * @author jiangbin
 */
public class LockerPatternViewInflater implements
		PasswordViewInflater<List<Cell>> {

	@SuppressWarnings("unchecked")
	@Override
	public PasswordView<List<Cell>> getPasswordView(
			LayoutInflater layoutInflater, ViewGroup parent) {
		View view = layoutInflater.inflate(
				R.layout.password_pattern_lockscreen, parent);
		TextView textView = (TextView) view.findViewById(R.id.enter_password);
		textView.setTextColor(Color.WHITE);
		return (PasswordView<List<Cell>>) view;
	}
}
