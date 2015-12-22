package com.cyou.cma.clockscreen.defaulttheme;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.cyou.cma.clockscreen.util.Util;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

public class TipAnimView extends ImageView {
	private AnimatorSet set = new AnimatorSet();

	public TipAnimView(Context context, AttributeSet attrs) {
		super(context, attrs);
		ViewHelper.setAlpha(this, 0.1f);
	}

	public void startAnim() {
		int offsetX = -200 * Util.getScreenHeight(getContext()) / 1280;
		AnimatorSet transAnim = new AnimatorSet();
		transAnim.playTogether(
				ObjectAnimator.ofFloat(this, "translationY", offsetX)
						.setDuration(500),
				ObjectAnimator.ofFloat(this, "alpha", 0.8f).setDuration(500));
		ObjectAnimator animAlpha = ObjectAnimator.ofFloat(this, "alpha", 1f)
				.setDuration(800);
		set.setInterpolator(new DecelerateInterpolator(0.8f));
		set.setStartDelay(1000);
		set.play(transAnim).before(animAlpha);
		set.addListener(new AnimatorListener() {
			private boolean mCanceled;

			@Override
			public void onAnimationStart(Animator arg0) {
				mCanceled = false;
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (!mCanceled) {
					animation.start();
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				mCanceled = true;

			}
		});
		set.start();

	}

	// public void doScaleAnim() {
	// // set.setDuration(ANIMTIME);
	// final AnimatorSet transAnim = new AnimatorSet();
	// transAnim.playTogether(
	// ObjectAnimator.ofFloat(this, "translationY", -100).setDuration(
	// 1000), ObjectAnimator.ofFloat(this, "alpha", 1f)
	// .setDuration(2000));
	// // ObjectAnimator animAlpha = ObjectAnimator.ofFloat(this, "alpha", 1f)
	// // .setDuration(3000);
	// // set.play(transAnim).before(animAlpha);
	// // set.play(transAnim);
	// transAnim.addListener(new AnimatorListener() {
	// private boolean mCanceled;
	//
	// @Override
	// public void onAnimationStart(Animator arg0) {
	// mCanceled = false;
	// }
	//
	// @Override
	// public void onAnimationRepeat(Animator arg0) {
	//
	// }
	//
	// @Override
	// public void onAnimationEnd(Animator animation) {
	// if (!mCanceled) {
	// transAnim.start();
	// }
	// }
	//
	// @Override
	// public void onAnimationCancel(Animator animation) {
	// mCanceled = true;
	//
	// }
	// });
	// transAnim.start();
	// // set.start();
	//
	// }

	public void cleanUp() {
		set.cancel();
	}
}
