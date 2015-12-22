package com.cyou.cma.cengine.wave.util;

import com.cyou.cma.cengine.CyTool;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;

public class CyWaveMask extends ImageView{
	
	public CyWaveMask(Context context) {
		super(context);
        init();
	}
	public CyWaveMask(Context context, AttributeSet attrs) {
		super(context, attrs);
        init();
	}
	public CyWaveMask(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        init();
	}

	private void init() {
		setScaleType(ScaleType.FIT_XY);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        setAlpha(254);
        try{
        	setLayoutParams(lp);
        }catch(Exception e){
        	CyTool.log(e.toString());
        }
    }
	@Override 
    protected void onDraw(Canvas can) {
		try{
			super.onDraw(can);
		}catch(Exception e){
			CyTool.log(e.toString());
		}
	}
}
