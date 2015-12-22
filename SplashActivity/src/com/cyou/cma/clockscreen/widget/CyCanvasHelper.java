package com.cyou.cma.clockscreen.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class CyCanvasHelper {
	//迭代器
	private AccelerateInterpolator ai = new AccelerateInterpolator();
	//画笔
	private Paint pt = null;
	//动画状态监听
	private OnDrawListener dl = null;
	public void setOnDrawListener(OnDrawListener dl){
		this.dl = dl;
	}
	public interface OnDrawListener{
		public void onStart();
		public void onMiddle(float run);
		public void onEnd();
	}
	//绘制时间
	private long duration = 300;
	private long startTime = NONE;
	private static final long NONE = 0;
	//圆&扇
	private int screenX = 0;
	private int screenY = 0;
	public void setPoint(int x, int y){
		screenX = x;
		screenY = y;
	}
	private int minRadius = 0;
	private int radiusRange = 100;
	public void setRadius(int min, int max){
		minRadius = min;
		radiusRange = max - min;
	}
	private RectF rc = new RectF();
	private int fps = 0;
	public CyCanvasHelper(int color){
		pt = new Paint();
		pt.setAntiAlias(true);
		pt.setColor(color);
	}
	
	public void drawCircle(View v, Canvas can){
		if(can!=null&&v!=null){
			long dt = System.currentTimeMillis();
			if(startTime==NONE){
				startTime = dt;
				if(dl!=null){
					dl.onStart();
				}
			}
			float run = 1f*(dt - startTime)/duration;
			if(run<0){
				end(can);
			}else if(run<=1){
				int x = screenX - (int)v.getX();
				int y = screenY - (int)v.getY();
				int radius = minRadius + (int)(radiusRange*run);
				can.drawCircle(x, y, radius, pt);
			}else{
				end(can);
			}
		}
	}
	public void drawArc(View v, Canvas can){
		if(can!=null&&v!=null){
			long dt = System.currentTimeMillis();
			if(startTime==NONE){
				fps = 0;
				startTime = dt;
				if(dl!=null){
					dl.onStart();
				}
			}
			float run = 1f*(dt - startTime)/duration;
			if(run<0){
				end(can);
			}else if(run<=1){
				run = ai.getInterpolation(run);
//				if(dl!=null){
//					dl.onMiddle(run);
//				}
				fps++;
				int radius = minRadius + (int)(radiusRange*run);
//				CyTool.log(run+"%: radius="+radius+" in ("+minRadius+"~"+maxRadius+")");
				rc.set(screenX-radius, screenY-radius, screenX+radius, screenY+radius);
		        can.drawArc(rc, 90, 180, true, pt);
//				android.util.Log.d("____", "radius="+radius);
		        v.invalidate();
			}else{
				end(can);
			}
		}
	}

	private void end(Canvas can){
		startTime = NONE;
		if(can!=null&&pt!=null){
			can.drawColor(pt.getColor());
		}
		if(dl!=null){
			dl.onEnd();
			dl = null;
		}
//		android.util.Log.d("____", "Fps="+fps);
	}
}
