package com.cyou.cma.clockscreen.password.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.password.PasswordView;
import com.cyou.cma.clockscreen.password.SecureAccess;
import com.cyou.cma.clockscreen.util.ChooseLockSettingsHelper;
import com.cyou.cma.clockscreen.util.LockPatternUtils;

public abstract class BasePinPasswordView extends LinearLayout implements OnClickListener, AnimationListener,
        PasswordView<String> {
    private Button mNumpad1;
    private Button mNumpad2;
    private Button mNumpad3;
    private Button mNumpad4;
    private Button mNumpad5;
    private Button mNumpad6;
    private Button mNumpad7;
    private Button mNumpad8;
    private Button mNumpad9;
    private Button mNumpad0;
    private RelativeLayout keypad_num_back;
    private RelativeLayout keypad_num_reset;
    private LinearLayout password_pin_box;
    private TextView pin_input_title;
    private SecureAccess mSecureAccess;
    private Stage mUiStage = Stage.Introduction;
    private String mFirstPassword;
    private int[] digital = {
            -1, -1, -1, -1
    };
    protected ChooseLockSettingsHelper mChooseLockSettingsHelper;

    protected enum Stage {
        Introduction(R.string.lockpassword_choose_your_password_header, false),
        ConfirmWrong(R.string.lockpassword_choose_your_password_header, true);
        public final int resid;
        public final boolean shake;

        Stage(int resid, boolean shake) {
            this.resid = resid;
            this.shake = shake;
        }

    }

    private Handler mHandler = new Handler();
    public static final String KEY_FIRST_PASSWORD = "first_password";
    public static final String KEY_UI_STAGE = "ui_stage";
    public static final String KEY_FIRST_DIGITAL = "first_digital";
    public static final String KEY_SECOND_DIGITAL = "second_digital";
    public static final String KEY_THIRD_DIGITAL = "third_digital";
    public static final String KEY_FOURTH_DIGITAL = "fourth_digital";

    public BasePinPasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mChooseLockSettingsHelper = new ChooseLockSettingsHelper(context);
    }

     

    @Override
    public void setSecureAccess(SecureAccess secureAccess) {
        this.mSecureAccess = secureAccess;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mNumpad1 = (Button) findViewById(R.id.keypad_num_1);
        mNumpad2 = (Button) findViewById(R.id.keypad_num_2);
        mNumpad3 = (Button) findViewById(R.id.keypad_num_3);
        mNumpad4 = (Button) findViewById(R.id.keypad_num_4);
        mNumpad5 = (Button) findViewById(R.id.keypad_num_5);
        mNumpad6 = (Button) findViewById(R.id.keypad_num_6);
        mNumpad7 = (Button) findViewById(R.id.keypad_num_7);
        mNumpad8 = (Button) findViewById(R.id.keypad_num_8);
        mNumpad9 = (Button) findViewById(R.id.keypad_num_9);
        mNumpad0 = (Button) findViewById(R.id.keypad_num_0);
        keypad_num_back = (RelativeLayout) findViewById(R.id.keypad_num_back);
        keypad_num_reset = (RelativeLayout) findViewById(R.id.keypad_num_reset);
        password_pin_box = (LinearLayout) findViewById(R.id.password_pin_box);
        pin_input_title = (TextView) findViewById(R.id.pin_input_title);
        mNumpad1.setOnClickListener(this);
        mNumpad2.setOnClickListener(this);
        mNumpad3.setOnClickListener(this);
        mNumpad4.setOnClickListener(this);
        mNumpad5.setOnClickListener(this);
        mNumpad6.setOnClickListener(this);
        mNumpad7.setOnClickListener(this);
        mNumpad8.setOnClickListener(this);
        mNumpad9.setOnClickListener(this);
        mNumpad0.setOnClickListener(this);
        keypad_num_back.setOnClickListener(this);
        keypad_num_reset.setOnClickListener(this);
        keypad_num_reset.setVisibility(View.INVISIBLE);
        updateStage(Stage.Introduction, false);
    }

    @Override
    public void onClick(View v) {

        int tag = Integer.parseInt(v.getTag().toString());
        switch (tag) {
            case -1:// keypad_num_reset
                break;
            case -2:// keypad_num_back
                for (int i = digital.length - 1; i >= 0; i--) {
                    if (digital[i] != -1) {
                        digital[i] = -1;
                        updateStage(mUiStage, false);
                        break;
                    }
                }
                break;
            default:
                for (int i = 0; i < digital.length; i++) {
                    if (digital[i] == -1) {
                        digital[i] = tag;
                        updateStage(mUiStage, false);
                        break;
                    }
                }
                if (digital[3] != -1) {// 已经填充满了
                    updateStage(mUiStage, false);
                    mFirstPassword = array2String();
                    LockPatternUtils utils = mChooseLockSettingsHelper.utils();
                    boolean pass = checkPassword(mFirstPassword);
                    if (pass) {

                        postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                if (mSecureAccess != null) {
                                    mSecureAccess.onSecureSuccess();
                                }

                            }
                        }, 100);

                    } else {
                        updateStage(Stage.ConfirmWrong, true);
                        clearArray();
                    }
                }
                break;
        }

    }

    public String array2String() {
        String password = "" + digital[0] + digital[1] + digital[2] + digital[3];
        return password;
    }

    Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

            clearCheckBox();

        }
    };

    public void clearCheckBox() {
        int checkBoxCount = password_pin_box.getChildCount();
        for (int i = 0; i < checkBoxCount; i++) {
            ((CheckBox) password_pin_box.getChildAt(i)).setChecked(false);
        }
    }

    public void clearArray() {
        for (int i = 0; i < digital.length; i++) {
            digital[i] = -1;
        }

    }

    public void updateStage(Stage stage, boolean full) {
        this.mUiStage = stage;
        updateUi(full);
    }

    public void updateUi(boolean full) {
        boolean shake = this.mUiStage.shake;
        if (shake && full) {
            Animation shakeAnimation = AnimationUtils.loadAnimation(this.getContext(), R.anim.shake);
            shakeAnimation.setAnimationListener(this);
            this.password_pin_box.startAnimation(shakeAnimation);
        }
        pin_input_title.setText(this.mUiStage.resid);

        if (!full) {
            for (int i = 0; i < digital.length; i++) {
                boolean checked = (digital[i] != -1);
                ((CheckBox) password_pin_box.getChildAt(i)).setChecked(checked);
            }
        } else {
            if (mUiStage == Stage.ConfirmWrong) {
                pin_input_title.setText(R.string.lockpassword_confirm_passwords_dont_incorrect);
                mHandler.postDelayed(mRunnable, 500);
            } else {
                clearCheckBox();
            }
        }

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superSaveState = super.onSaveInstanceState();
        return new SavedState(superSaveState, mUiStage.name(), mFirstPassword, digital[0], digital[1],
                digital[2], digital[3]);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mUiStage = Stage.valueOf(ss.stageName);
        mFirstPassword = ss.firstPassword;
        digital[0] = ss.first;
        digital[1] = ss.second;
        digital[2] = ss.third;
        digital[3] = ss.fourth;
    }

    private static class SavedState extends BaseSavedState {
        String stageName;
        String firstPassword;
        int first;
        int second;
        int third;
        int fourth;

        SavedState(Parcelable superState, String stageName, String firstPassword, int first, int second,
                int third, int fourth) {
            super(superState);
            this.stageName = stageName;
            this.firstPassword = firstPassword;
            this.first = first;
            this.second = second;
            this.third = third;
            this.fourth = fourth;
        }

        public SavedState(Parcel in) {
            super(in);
            this.stageName = in.readString();
            this.firstPassword = in.readString();
            this.first = in.readInt();
            this.second = in.readInt();
            this.third = in.readInt();
            this.fourth = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(stageName);
            dest.writeString(firstPassword);
            dest.writeInt(first);
            dest.writeInt(second);
            dest.writeInt(third);
            dest.writeInt(fourth);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    @Override
    public void onAnimationStart(Animation animation) {
        setButtonEnable(false);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        setButtonEnable(true);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void setButtonEnable(boolean enable) {
        mNumpad1.setEnabled(enable);
        mNumpad2.setEnabled(enable);
        mNumpad3.setEnabled(enable);
        mNumpad4.setEnabled(enable);
        mNumpad5.setEnabled(enable);
        mNumpad6.setEnabled(enable);
        mNumpad7.setEnabled(enable);
        mNumpad8.setEnabled(enable);
        mNumpad9.setEnabled(enable);
        mNumpad0.setEnabled(enable);
        keypad_num_back.setEnabled(enable);
        keypad_num_reset.setEnabled(enable);
    }

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {
        clearArray();
        clearCheckBox();
    }

}
