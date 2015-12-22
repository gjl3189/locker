package com.cyou.cma.cengine.wave.util;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.cyou.cma.cengine.CyTool;
import com.cyou.cma.cengine.wave.CyWaveHelper;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

public class CyWaveRender implements GLSurfaceView.Renderer {
	private static boolean showVersionCode = false;
	
	private CyWaveView[] aryView = new CyWaveView[]{null};
	private Bitmap[] aryBmp = new Bitmap[]{null};
	private int w = 0;
	private int h = 0;
	private boolean changeBmp = false;
	private boolean hasBmp = false;
	private int hasDraw = 0;
	private static int MAX_DRAW = 2;
	public boolean hasDraw(){
		return hasDraw>=MAX_DRAW;
	}
	
	public boolean hasBmp(){
		return hasBmp;
	}
	private boolean showWave = false;
	
	public CyWaveRender(CyWaveView v){
		synchronized(aryView){
			aryView[0] = v;
		}
	}
	private static final int RADIUS = 2;
	private static final int K = 60;//水波纹大小，数字越大，水波越小
	//上层初始化纹理：解决android_SDK<=2.3的bitmap数组在初始化纹理时的适配问题
	private static final boolean initTexture = true;
	private static final int NONE = -1;
	private static final int[] aryTexture = new int[]{NONE};
	private void initTexture(Bitmap bmp, int factor, int radius, int width, int height){
		if(bmp!=null&&!bmp.isRecycled()){
			if(aryTexture[0]==NONE){
				GLES20.glGenTextures(1, aryTexture, 0);
			}
			GLES20.glEnable(GLES20.GL_TEXTURE_2D);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, aryTexture[0]);

	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	        
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	        
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
	        
	        CyWaveLib.init2(bmp.getWidth(), bmp.getHeight(), factor ,radius, width, height);
		}
	}
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {  
		// TODO Auto-generated method stub
		if(!showVersionCode){
			android.util.Log.d("____", "CyWaveEngine's VersionCode: "+CyWaveLib.getVersionCode());
			showVersionCode = true;
		}
	}
	@Override
    public void onDrawFrame(GL10 gl) {
		try{
			if(changeBmp){
				synchronized(aryBmp){
					if(aryBmp[0]!=null){
						if(initTexture){
							initTexture(aryBmp[0], w/K , RADIUS, w, h);
						}else{
							CyWaveLib.init(aryBmp[0], w/K , RADIUS, w, h);
						}
						hasBmp = true;
					}
				}
				changeBmp = false;
			}
			if(hasBmp){
				if(showWave){
					synchronized(aryView){
						if(aryView[0]!=null){
							aryView[0].showWave();
							showWave = false;
						}
					}
				}
			}
			int result = CyWaveLib.onDrawFrame();
	        if(result!=0){
	        	synchronized(aryView){
	    			if(aryView[0]!=null){
	    				aryView[0].requestRender();
	    			}
	    		}
	        }
	        if(hasDraw<MAX_DRAW){
	        	hasDraw++;
	        	if(hasDraw()){
	    			CyWaveHelper.hideMaskView();	
	        	}
	        }
//			CyTool.log("onDrawFrame: Success");
		}catch(Exception ex){
			CyTool.log("onDrawFrame: Failed");
			CyWaveHelper.renderFailed();
		}
    }
	@Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
		w = width;
		h = height;
		try{
			synchronized(aryBmp){
				if(aryBmp[0]!=null){
					if(initTexture){
						initTexture(aryBmp[0], w/K , RADIUS, w, h);
					}else{
						CyWaveLib.init(aryBmp[0], w/K , RADIUS, w, h);
					}
					hasBmp = true;
					changeBmp = false;
				}
			}
			if(hasBmp){
				showWave = true;
			}
			CyTool.log("onSurfaceChanged: Success");
		}catch(Exception ex){
			CyTool.log("onSurfaceChanged: Failed");
			CyWaveHelper.renderFailed();
		}
    }
	public void setBmp(Bitmap bmp){
		setBmp(bmp, true);
    }
	public void setBmp(Bitmap bmp, boolean recyle){
		if(bmp!=null){
			if(!bmp.isRecycled()){
				synchronized(aryBmp){
					if(aryBmp[0]!=null){
						if(recyle){
							if(!aryBmp[0].isRecycled()){
								aryBmp[0].recycle();
							}
						}
					}
					aryBmp[0] = bmp;
					changeBmp = true;
				}
			}
		}
    }
	public void onDestroy(){
		synchronized(aryView){
			aryView[0] = null;
		}
		synchronized(aryBmp){
			if(aryBmp[0]!=null){
				if(!aryBmp[0].isRecycled()){
					aryBmp[0].recycle();
				}
			}
			aryBmp[0] = null;
		}
    }
}
