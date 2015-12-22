//package com.cyou.cma.clockscreen.widget;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.widget.Button;
//
//import com.cynad.cma.locker.R;
//import com.cyou.cma.clockscreen.util.SettingsHelper;
//import com.cyou.cma.clockscreen.util.Util;
//
//public class StateButton extends LButton {
//
//    public static final int DOWNLOADING = 1000;
//    public static final int APPLYING = 1001;
//    // public static final int BUYING = 1002;
//    public static final int WAITING = 1002;
//    public static final int USING = 1003;
//    public static final int RESUME = 1004;
//
//    private static final int[] STATE_DOWNLOADING = {
//        R.attr.state_downloading
//    };
//    private static final int[] STATE_APPLYING = {
//        R.attr.state_applying
//    };
//    private static final int[] STATE_USING = {
//        R.attr.state_using
//    };
//    private static final int[] STATE_WAITING = {
//        R.attr.state_waiting
//    };
//    private int mState = DOWNLOADING;
//
//    private String TAG = "StateButton";
//
//    public void setState(int state) {
//        mState = state;
//        Util.Logjb("binjiangtest", "the state is " + state);
//        switch (state) {
//            case DOWNLOADING:
//                setEnabled(true);
//                setText(R.string.state_button_download);
//                break;
//            case APPLYING:
//                setEnabled(true);
//                setText(R.string.state_button_apply);
//                break;
//            case USING:
//                setEnabled(!SettingsHelper.getLockServiceEnable(getContext()));
//                setText(R.string.state_button_use);
//                break;
//            case WAITING:
//                setEnabled(false);
//                setText(R.string.state_button_wait);
//                break;
//            case RESUME:
//                setEnabled(true);
//                setText(R.string.state_button_resume);
//                break;
//
//        }
//
//        refreshDrawableState();
//
//    }
//
//    public int getState() {
//        return this.mState;
//    }
//
//    public StateButton(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    @Override
//    protected int[] onCreateDrawableState(int extraSpace) {
//        Util.Logjb(TAG, "onCreateDrawableState------>");
//        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
//        switch (mState) {
//            case APPLYING:
//                mergeDrawableStates(drawableState, STATE_APPLYING);
//                break;
//            case USING:
//                mergeDrawableStates(drawableState, STATE_USING);
//                break;
//            case WAITING:
//                mergeDrawableStates(drawableState, STATE_WAITING);
//                break;
//            case DOWNLOADING:
//
//            default:
//                mergeDrawableStates(drawableState, STATE_DOWNLOADING);
//                break;
//        }
//
//        return drawableState;
//    }
//
//}
