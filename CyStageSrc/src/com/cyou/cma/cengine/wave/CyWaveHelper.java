package com.cyou.cma.cengine.wave;

import com.cyou.cma.cengine.CyTool;
import com.cyou.cma.cengine.wave.util.CyWaveMask;
import com.cyou.cma.cengine.wave.util.CyWaveRender;
import com.cyou.cma.cengine.wave.util.CyWaveView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class CyWaveHelper {
	//进程单例
	private static View[] aryView = new View[]{null, null};

	private static Handler mHandler = new Handler();
	
	public static View[] buildAry(Context ct){
		CyTool.log("buildAry");
		synchronized(aryView){
//			if(aryView[0]==null){
				aryView[0] = new CyWaveView(ct);
				aryView[1] = new CyWaveMask(ct);
			}
//		}
		return aryView;
	}
	public static CyWaveView getWaveView(){
		View v = aryView[0];
		if(v!=null){
			return (CyWaveView)v;	
		}else{
			return null;
		}
	}
	//OpenGL线程调用
	public static void hideMaskView(){
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				CyTool.log("hideMaskView");
				if(aryView[1]!=null){
					CyWaveMask v = (CyWaveMask)aryView[1];
					v.setVisibility(View.GONE);
					v.setImageBitmap(null);
				}
			}
		});
	}
	private static Bitmap bitmap = null;//为低端Sdk缓存bitmap对象
	public static void setBmp(Bitmap bmp, boolean recyle){
		if(bmp!=null){
			if(!bmp.isRecycled()){
				synchronized(aryView){
					CyWaveRender rd = null;
					if(aryView[0]!=null){
						CyWaveView v = (CyWaveView)aryView[0];
						rd = v.getRd();
					}
					if(aryView[1]!=null){
						CyWaveMask v = (CyWaveMask)aryView[1];
						if(aryView[0]==null){
							v.setImageBitmap(bmp);
							if(bitmap!=null){
								if(recyle){
									if(!bitmap.isRecycled()){
										bitmap.recycle();
									}
								}
								bitmap = bmp;
							}
						}else{
							if(rd!=null){
								if(!rd.hasDraw()){
									v.setImageBitmap(bmp);	
								}else{
									v.setImageBitmap(null);	
								}
							}
						}
					}
					if(rd!=null){
						rd.setBmp(bmp, recyle);
		    			if(aryView[0]!=null){
		    				CyWaveView wv = (CyWaveView)aryView[0];
		    				if(wv!=null){
		    					wv.requestRender();
		    				}
		    			}
					}
				}
			}
		}
	}
	public static void setBmp(Bitmap bmp){
		setBmp(bmp, true);
	}
	public static void judgeTouchEvent(MotionEvent event){
		synchronized(aryView){
			if(aryView[1]!=null){
				if(aryView[1].getVisibility()==View.VISIBLE){
					return;
				}
			}
			if(aryView[0]!=null){
				CyWaveView v = (CyWaveView)aryView[0];
				CyWaveRender rd = v.getRd();
				if(rd!=null){
					if(rd.hasDraw()){
						v.judgeTouchEvent(event);		
					}
				}
			}
		}
    }
	public static void onResume(){
		synchronized(aryView){
			if(aryView[0]!=null){
				CyWaveView v = (CyWaveView)aryView[0];
				v.onResume();
			}
		}
	}
	public static void onPause(){
		synchronized(aryView){
			if(aryView[0]!=null){
				CyWaveView v = (CyWaveView)aryView[0];
				v.onPause();
			}
		}
	}
	public static void onDestroy(){
		synchronized(aryView){
			if(aryView[0]!=null){
				CyWaveView v = (CyWaveView)aryView[0];
				v.onDestroy();
				aryView[0] = null;
			}
			if(aryView[1]!=null){
				CyWaveMask v = (CyWaveMask)aryView[1];
				v.setImageBitmap(null);
				aryView[1] = null;
			}
			cb = null;
		}
	}
	
	private static CyWaveHpCallback cb = null;
	public static void setCb(CyWaveHpCallback callback){
		cb = callback;
		CyTool.log("setCb");
	}
	public interface CyWaveHpCallback{
		public void removeWave();
	}
	public static void renderFailed(){
		CyTool.log("CyWaveHelper.renderFailed");
		if(cb!=null){
			CyTool.log("CyWaveHelper.renderFailed: cb!=null");
			cb.removeWave();
		}else{
			CyTool.log("CyWaveHelper.renderFailed: cb==null");
		}
	}
}
