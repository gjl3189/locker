/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyou.cma.cengine.wave.util;

import com.cyou.cma.cengine.CyTool;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class CyWaveView extends GLSurfaceView {
	
	private CyWaveRender rd = null;
	public CyWaveRender getRd(){
		return rd;
	}

    public CyWaveView(Context context) {
        super(context);
        init();
    }
	public CyWaveView(Context context, AttributeSet attrs) {
		super(context, attrs);
        init();
	}

    private void init() {
        setEGLContextClientVersion(2);
//        setEGLContextFactory(new CyContextFactory());
//    	setEGLConfigChooser(new CyConfigChooser(8, 8, 8, 8, 0, 0));
    	setEGLConfigChooser(8, 8, 8, 8, 16, 0);
    	rd = new CyWaveRender(this);
        setRenderer(rd);
        
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        try{
        	setLayoutParams(lp);
        }catch(Exception e){
        	CyTool.log(e.toString());
        }
    }
    
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	return false;
    }
    
    public void judgeTouchEvent(MotionEvent event){
    	int action = event.getAction();
		if(action==MotionEvent.ACTION_MOVE||action==MotionEvent.ACTION_DOWN){
			showWave((int)event.getX(), (int)event.getY());
		}
    }
    
    public void showWave(){
    	int x = getLeft() + (int)(getWidth()*Math.random());
    	int y = getTop() + (int)(getHeight()*Math.random());
    	showWave(x, y);
    }
    
    private void showWave(int x, int y){
    	CyWaveLib.initiateRippleAtLocation(x, y);
		requestRender();
    }
    
    public void onDestroy(){
//    	setVisibility(View.GONE);
    	if(rd!=null){
    		rd.onDestroy();
    		rd = null;
    	}
    	
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        try{
        	CyWaveLib.onDestroy();
        }catch(Exception e){
        	e.printStackTrace();
        }
    }
}
