package com.cyou.cma.clockscreen.util;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

public class CyLvItemAnim {
	public static Animation getAnim(boolean isLeft, int itemHeight){
		AnimationSet as = new AnimationSet(true);
//		ScaleAnimation sc = new ScaleAnimation(1.1f, 1, 1.1f, 1, convertView.getWidth()/2, convertView.getHeight()/2);
//		as.addAnimation(sc);
		int h = 0;
		if(isLeft){
			h = +itemHeight*2/3;
		}else{
			h = +itemHeight/3;
		}
		TranslateAnimation tl = new TranslateAnimation(0, 0, h, 0);
		as.addAnimation(tl);
		as.setInterpolator(new AccelerateDecelerateInterpolator());
//		AlphaAnimation al = new AlphaAnimation(.2f, 1);
//		as.addAnimation(al);
		as.setDuration(500);
		return as;
	}
}
