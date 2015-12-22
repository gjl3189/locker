package com.cyou.cma.clockscreen.widget;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.widget.CyCanvasHelper.OnDrawListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CyFrameLayout extends FrameLayout {
	private boolean showAnim = true;
	public void showAnim(boolean b){
		showAnim = b;
	}
	private CyCanvasHelper ch = null;
	
	public CyFrameLayout(Context context) {
		super(context);
	}
	public CyFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public CyFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void onLayout(boolean changed, int left, int top, int right, int bottom){
		super.onLayout(changed, left, top, right, bottom);
		if(changed){
			if(ch==null){
				initCh();
			}
			if(ch!=null){
				int w = right - left;
				int h = bottom - top;
				ch.setPoint(right, top);
				ch.setRadius(0, (int)Math.sqrt(w*w+h*h));	
			}
		}
	}
	private void initCh(){
		ch = new CyCanvasHelper(Color.WHITE);
		OnDrawListener dl = new OnDrawListener() {
			@Override
			public void onStart() {
			}
			@SuppressLint("NewApi") @Override
			public void onEnd() {
				ch = null;
				Activity at = (Activity)(CyFrameLayout.this.getContext());
				at.getWindow().getDecorView().setBackgroundColor(Color.WHITE);
				setBackgroundColor(Color.WHITE);
				
		    	LinearLayout cn = (LinearLayout)findViewById(R.id.cn);
		    	cn.setVisibility(View.VISIBLE);
			}
			@Override
			public void onMiddle(float run) {
				
			}
		};
		if(showAnim){
			ch.setOnDrawListener(dl);	
		}else{
			dl.onEnd();
		}
	}
	
	@Override
	public void onDraw(Canvas can){
		if(ch!=null){
			ch.drawArc(this, can);
		}
		super.onDraw(can);
	}
//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//    	super.dispatchDraw(canvas);
//    }
//	@Override
//	protected boolean drawChild(Canvas can, View child, long drawingTime) {
//		boolean b = false;
//		if(ch==null){
//			b = super.drawChild(can, child, drawingTime);	
//		}
//		return b;
//	}
}
