package com.cyou.cma.clockscreen.password;

import android.view.LayoutInflater;
import android.view.ViewGroup;

public interface PasswordViewInflater<T> {
    public PasswordView<T> getPasswordView(LayoutInflater layoutInflater,ViewGroup parent);
}
