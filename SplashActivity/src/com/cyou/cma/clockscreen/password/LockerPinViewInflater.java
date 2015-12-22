package com.cyou.cma.clockscreen.password;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.cynad.cma.locker.R;

/**
 * 锁屏界面的pin码解锁界面的渲染器
 * @author jiangbin
 */
public class LockerPinViewInflater implements PasswordViewInflater<String> {

    @SuppressWarnings("unchecked")
    @Override
    public PasswordView<String> getPasswordView(LayoutInflater layoutInflater, ViewGroup parent) {
        return (PasswordView<String>) layoutInflater.inflate(R.layout.password_pin_lockscreen, parent);
    }

}
