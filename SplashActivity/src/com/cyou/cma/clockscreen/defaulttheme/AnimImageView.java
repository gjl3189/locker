
package com.cyou.cma.clockscreen.defaulttheme;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

public class AnimImageView extends ImageView {
    private String TAG = "AnimImageView";
    private final long ANIMTIME = 500;

    public static final int MODE_NORMAL = 0;
    public static final int MODE_GONE = 1;
    public static final int MODE_SELECTED = 2;
    public static final int MODE_HIGHLIGHT = 3;

    public AnimImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        doScaleAnim(MODE_NORMAL);
    }
    
    public void reset(){
    	this.clearAnimation();
        ViewHelper.setAlpha(this, 0.75f);
        ViewHelper.setScaleX(this, 1f);
        ViewHelper.setScaleY(this, 1f);
        
    }

    public void doScaleAnim(int mode) {
        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIMTIME);
        switch (mode) {
            case MODE_NORMAL:
                set.playTogether(
                        ObjectAnimator.ofFloat(this, "scaleX", 1f),
                        ObjectAnimator.ofFloat(this, "scaleY", 1f),
                        ObjectAnimator.ofFloat(this, "alpha", 0.75f));
                break;
            case MODE_GONE:
                set.playTogether(
                        ObjectAnimator.ofFloat(this, "scaleX", 0f),
                        ObjectAnimator.ofFloat(this, "scaleY", 0f),
                        ObjectAnimator.ofFloat(this, "alpha", 0f));
                break;
            case MODE_SELECTED:
                set.playTogether(
                        ObjectAnimator.ofFloat(this, "scaleX", 1.3f),
                        ObjectAnimator.ofFloat(this, "scaleY", 1.3f),
                        ObjectAnimator.ofFloat(this, "alpha", 1f));
                break;
            case MODE_HIGHLIGHT:
                AnimatorSet animSetBig = new AnimatorSet();
                animSetBig.playTogether(
                        ObjectAnimator.ofFloat(this, "scaleX", 1.2f),
                        ObjectAnimator.ofFloat(this, "scaleY", 1.2f),
                        ObjectAnimator.ofFloat(this, "alpha", 1.0f));
                AnimatorSet animSetNormal = new AnimatorSet();
                animSetNormal.playTogether(
                        ObjectAnimator.ofFloat(this, "scaleX", 1.0f),
                        ObjectAnimator.ofFloat(this, "scaleY", 1.0f),
                        ObjectAnimator.ofFloat(this, "alpha", 0.75f));
                set.play(animSetBig).before(animSetNormal);
                set.setDuration(200);
                break;
            default:
                break;
        }
        set.start();
    }

}
