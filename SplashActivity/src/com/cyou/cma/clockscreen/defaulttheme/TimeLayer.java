
package com.cyou.cma.clockscreen.defaulttheme;

import java.util.Calendar;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.util.ImageUtil;

public class TimeLayer extends LinearLayout {

    private ImageView mFirstTop;
    private ImageView mFirstBottom;
    private ImageView mSecondTop;
    private ImageView mSecondBottom;
    private ImageView mThirdTop;
    private ImageView mThirdBottom;
    private ImageView mFourthTop;
    private ImageView mFourthBottom;
    private ImageView mAmPMTop;

    private TextView mDateWeekTextView;
    private int mNumFirst = -1;
    private int mNumSecond = -1;
    private int mNumThird = -1;
    private int mNumFourth = -1;
    private final int ANIMATION_DURING = 400;
    private final int ANIMATION_BACK = 100;
    private final int ANIMATION_FINAL = 50;
    private final int ANIMATION_BETWEEN = (ANIMATION_DURING + ANIMATION_BACK) / 2;

    private Handler mHandler = new Handler();

    private Context mContext;

    public TimeLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initParms();
    }

    public void initParms() {
        View.inflate(mContext, R.layout.defaulttheme_time_layer, this);
        mDateWeekTextView = (TextView) findViewById(R.id.defaulttheme_week_date);
        mFirstTop = (ImageView) findViewById(R.id.defaulttheme_first_top);
        mFirstBottom = (ImageView) findViewById(R.id.defaulttheme_first_bottom);
        mSecondTop = (ImageView) findViewById(R.id.defaulttheme_second_top);
        mSecondBottom = (ImageView) findViewById(R.id.defaulttheme_second_bottom);
        mThirdTop = (ImageView) findViewById(R.id.defaulttheme_third_top);
        mThirdBottom = (ImageView) findViewById(R.id.defaulttheme_third_bottom);
        mFourthTop = (ImageView) findViewById(R.id.defaulttheme_fourth_top);
        mFourthBottom = (ImageView) findViewById(R.id.defaulttheme_fourth_bottom);
        mAmPMTop = (ImageView) findViewById(R.id.defaulttheme_ampm);
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        updateTime(hour, minute, false, 0);
        updateDateUI();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * 更新日期显示
     */
    private void updateDateUI() {
        Calendar calendar = Calendar.getInstance();
        // int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        String dayInWeek;
        switch (weekday) {
            case Calendar.SUNDAY:
                dayInWeek = mContext.getString(R.string.time_week_sunday);
                break;
            case Calendar.MONDAY:
                dayInWeek = mContext.getString(R.string.time_week_monday);
                break;
            case Calendar.TUESDAY:
                dayInWeek = mContext.getString(R.string.time_week_tuesday);
                break;
            case Calendar.WEDNESDAY:
                dayInWeek = mContext.getString(R.string.time_week_wednesday);
                break;
            case Calendar.THURSDAY:
                dayInWeek = mContext.getString(R.string.time_week_thursday);
                break;
            case Calendar.FRIDAY:
                dayInWeek = mContext.getString(R.string.time_week_friday);
                break;
            case Calendar.SATURDAY:
                dayInWeek = mContext.getString(R.string.time_week_saturday);
                break;
            default:
                dayInWeek = mContext.getString(R.string.time_week_sunday);
                break;
        }
        String monthInYear;
        switch (month) {
            case 1:
                monthInYear = mContext.getString(R.string.time_month_one);
                break;
            case 2:
                monthInYear = mContext.getString(R.string.time_month_two);
                break;
            case 3:
                monthInYear = mContext.getString(R.string.time_month_three);
                break;
            case 4:
                monthInYear = mContext.getString(R.string.time_month_four);
                break;
            case 5:
                monthInYear = mContext.getString(R.string.time_month_five);
                break;
            case 6:
                monthInYear = mContext.getString(R.string.time_month_six);
                break;
            case 7:
                monthInYear = mContext.getString(R.string.time_month_seven);
                break;
            case 8:
                monthInYear = mContext.getString(R.string.time_month_eight);
                break;
            case 9:
                monthInYear = mContext.getString(R.string.time_month_nine);
                break;
            case 10:
                monthInYear = mContext.getString(R.string.time_month_ten);
                break;
            case 11:
                monthInYear = mContext.getString(R.string.time_month_eleven);
                break;
            default:
                monthInYear = mContext.getString(R.string.time_month_twelve);
                break;
        }
        mDateWeekTextView.setText(dayInWeek + "," + mContext.getString(
                R.string.time_date_description, monthInYear, day));
        // mDate.setText(mContext.getResources().getString(
        // R.string.time_date_description, day, month, year));
        // mWeek.setText(dayInWeek);
    }

    private int getImageByNumber(int number) {
        int resourceBitmap;
        switch (number) {
            case 1:
                resourceBitmap = R.drawable.time_num_1;
                break;
            case 2:
                resourceBitmap = R.drawable.time_num_2;
                break;
            case 3:
                resourceBitmap = R.drawable.time_num_3;
                break;
            case 4:
                resourceBitmap = R.drawable.time_num_4;
                break;
            case 5:
                resourceBitmap = R.drawable.time_num_5;
                break;
            case 6:
                resourceBitmap = R.drawable.time_num_6;
                break;
            case 7:
                resourceBitmap = R.drawable.time_num_7;
                break;
            case 8:
                resourceBitmap = R.drawable.time_num_8;
                break;
            case 9:
                resourceBitmap = R.drawable.time_num_9;
                break;
            default:
                resourceBitmap = R.drawable.time_num_0;
                break;
        }
        return resourceBitmap;

    }

    private void doAnimation(ImageView first, ImageView second, int resource,
            boolean animation) {
        final ImageView top;
        final ImageView bottom;
        if (first.getVisibility() == View.VISIBLE) {
            top = first;
            bottom = second;
        } else {
            top = second;
            bottom = first;
        }
        if (!animation) {
            top.setImageBitmap(ImageUtil.readBitmapWithDensity(mContext,
                    resource));
            return;
        }
        bottom.setImageBitmap(ImageUtil.readBitmapWithDensity(mContext,
                resource));
        AnimationSet set = new AnimationSet(true);
        TranslateAnimation one = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f);
        AlphaAnimation scale = new AlphaAnimation(1.0f, 0.0f);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addAnimation(scale);
        set.addAnimation(one);
        set.setFillAfter(false);
        set.setDuration(ANIMATION_DURING);
        set.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

                TranslateAnimation second = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, -1.0f,
                        Animation.RELATIVE_TO_SELF, 0.15f);
                second.setInterpolator(new AccelerateDecelerateInterpolator());
                second.setDuration(ANIMATION_DURING);
                second.setFillAfter(false);
                second.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        bottom.setVisibility(View.VISIBLE);
                        TranslateAnimation tan = new TranslateAnimation(
                                Animation.RELATIVE_TO_SELF, 0.0f,
                                Animation.RELATIVE_TO_SELF, 0.0f,
                                Animation.RELATIVE_TO_SELF, 0.15f,
                                Animation.RELATIVE_TO_SELF, -0.1f);
                        tan.setDuration(ANIMATION_BACK);
                        tan.setFillAfter(false);
                        tan.setInterpolator(new DecelerateInterpolator());
                        tan.setAnimationListener(new AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                TranslateAnimation f = new TranslateAnimation(
                                        Animation.RELATIVE_TO_SELF, 0.0f,
                                        Animation.RELATIVE_TO_SELF, 0.0f,
                                        Animation.RELATIVE_TO_SELF, -0.1f,
                                        Animation.RELATIVE_TO_SELF, 0.0f);
                                f.setDuration(ANIMATION_FINAL);
                                bottom.startAnimation(f);
                            }
                        });
                        bottom.startAnimation(tan);
                    }
                });
                bottom.startAnimation(second);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                top.setVisibility(View.INVISIBLE);
            }
        });
        top.startAnimation(set);

    }

    private void updateTime(int hour, int minute, final boolean animation,
            int between) {
        boolean is24Mode = android.text.format.DateFormat
                .is24HourFormat(getContext());

        mAmPMTop.setVisibility(is24Mode ? View.GONE : View.VISIBLE);
        if (!is24Mode) {
            Calendar mCalendar = Calendar.getInstance();
            mAmPMTop.setImageBitmap(ImageUtil.readBitmapWithDensity(mContext,
                    mCalendar.get(Calendar.AM_PM) == 1 ? R.drawable.time_num_pm
                            : R.drawable.time_num_am));
            if (hour == 0) {
                hour += 12;
            } else {
                hour = hour > 12 ? hour - 12 : hour;
            }
        }
        int first = hour / 10;
        int second = hour % 10;
        int third = minute / 10;
        int fourth = minute % 10;
        int delay = 0;
        if (first != mNumFirst) {
            mNumFirst = first;
            doAnimation(mFirstTop, mFirstBottom, getImageByNumber(mNumFirst),
                    animation);
            delay += between;
        }
        if (second != mNumSecond) {
            mNumSecond = second;
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    doAnimation(mSecondTop, mSecondBottom,
                            getImageByNumber(mNumSecond), animation);
                }
            }, delay);
            delay += between;
        }
        if (third != mNumThird) {
            mNumThird = third;
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    doAnimation(mThirdTop, mThirdBottom,
                            getImageByNumber(mNumThird), animation);

                }
            }, delay);
            delay += between;
        }

        if (fourth != mNumFourth) {
            mNumFourth = fourth;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doAnimation(mFourthTop, mFourthBottom,
                            getImageByNumber(mNumFourth), animation);
                }
            }, delay);
        }
    }

    public void update() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        updateTime(hour, minute, true, ANIMATION_BETWEEN);
        updateDateUI();
    }

}
