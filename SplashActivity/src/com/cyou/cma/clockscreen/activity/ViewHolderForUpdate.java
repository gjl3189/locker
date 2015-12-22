//package com.cyou.cma.clockscreen.activity;
//
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.cyou.cma.clockscreen.widget.LImageButton;
//import com.cyou.cma.clockscreen.widget.StateButton;
//
///**
// * 更新功能的ViewHolder
// * 
// * @author jiangbin
// */
//public class ViewHolderForUpdate implements ViewHolder {
//
//    public ViewHolderForUpdate() {
//    }
//
//    // 升级 和 下载等状态按钮的布局
//
//    public LinearLayout mUpdateLinearLayout;
//    /**
//     * 状态按钮
//     */
//    public StateButton mStateButton;
//    /**
//     * 升级按钮
//     */
//
//    public LImageButton mImageButtonCrash;
//    public LImageButton mImageButtonSetting;
//    public LImageButton mImageButtonBack;
//
//    public TextView mTextViewLockName;
//
//    /**
//     * 隐藏更新按钮
//     */
//    private void showStateButtonOnly(int state) {
//        mUpdateLinearLayout.setVisibility(View.VISIBLE);
//        mStateButton.setVisibility(View.VISIBLE);
//        if (state != -1) {
//            mStateButton.setState(state);
//        }
//    }
//
//    /**
//     * 显示删除按钮
//     */
//    public void showCrashView() {
//        mImageButtonCrash.setVisibility(View.VISIBLE);
//    }
//
//    /**
//     * 隐藏删除按钮
//     */
//    public void hideCrashView() {
//        mImageButtonCrash.setVisibility(View.GONE);
//    }
//
//    /**
//     * 显示设置按钮
//     */
//    public void showSettingButton() {
//        mImageButtonSetting.setVisibility(View.VISIBLE);
//    }
//
//    /**
//     * 隐藏设置按钮
//     */
//    public void hideSettingButton() {
//        mImageButtonSetting.setVisibility(View.GONE);
//    }
//
//    /**
//     * 显示状态按钮并根据当前主题是否需要 并且隐藏掉进度条
//     */
//    public void showStateButtonCascade(boolean updatable, int state) {
//
//        showStateButtonOnly(state);
//    }
//
//}
