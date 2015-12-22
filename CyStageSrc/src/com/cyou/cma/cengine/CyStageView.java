package com.cyou.cma.cengine;

import java.util.ArrayList;
import java.util.List;

import com.cyou.cma.cengine.CyActor.CyActorListener;
import com.cyou.cma.cengine.CyStage.BuildCallback;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class CyStageView extends View {
	//debug
	public static Paint ptDebug = new Paint();
	public List<int[]> lstPoint = new ArrayList<int[]>();
	public List<Rect> lstRect = new ArrayList<Rect>();
	public void showFps(boolean b){
		if(stg!=null){
			stg.showFps(b);
		}else{
			CyTool.log("stg==null, cant showFps");
		}
	}
	//恢复
	private boolean toZero = false;
	private static final AccelerateInterpolator ai = new AccelerateInterpolator();
	public void toZero(){
		if(!toZero){
			if(rp!=0){
				toZero = true;
				toZeroSt = System.currentTimeMillis();
				toZeroRp = rp;
				toZeroDr = (long)(rp*toZeroDrAll);
				if(toZeroDr<=0){
					toZero = false;
				}
			}
		}
		invalidate();
	}
	public long toZeroDrAll = 500;
	public long toZeroDr = 0;
	private float toZeroRp = 0;
	public long toZeroSt = 0;
	public long toZeroDt = 0;
	//优化被动渲染
	private boolean waitBmp = true;
	public Point p = null;
	private boolean add = true;
	public void setAdd(boolean b){
		add = b;
	}
	private boolean needToFront = false;
	public void setNeedToFront(boolean b){
		needToFront = b;
	}
	private float rp = 0;
	public float getRp(){
		return rp;
	}
	public void updateRp(float runPercent){
		updateRpSelf(runPercent);
		toZero = false;
	}
	private void updateRpSelf(float runPercent){
		int vb = getVisibility();
		if(vb!=View.VISIBLE){
			CyTool.log("updateRpSelf: getVisibility()!=View.VISIBLE");
		}
		setVisibility(View.VISIBLE);
		invalidate();
		rp = runPercent;
	}
//	private boolean canReceiveBroadcast = true;
//	public boolean canReceiveBroadcast(){
//		return canReceiveBroadcast;
//	}
//	public void setCanReceiveBroadcast(boolean b){
//		canReceiveBroadcast = b;
//	}
	protected CyStage stg = null;
	public void setStg(CyStage stg){
		this.stg = stg;
		resize();
	}
	public CyStage getStage(){
		return stg;
	}
//	public boolean drawEdge = false;
	public Paint ptLine = null;
	public void resize(){
		if(w!=0&&h!=0&&stg!=null){
			stg.resizeStage(ci, w, h, l, t, l - getPaddingLeft(), t - getPaddingTop());
		}
	}
	public boolean isRun(){
		return stg!=null;
	}
	private int w = 0;
	public int layoutW = 0;
	private int h = 0;
	public int layoutH = 0;
	private int l = 0;
//	public int layoutL = 0;
	private int t = 0;
//	public int layoutT = 0;

//	private String to = null;
//	public void setTo(String to){
//		this.to = to;
//		CTool.log("LeSurpriseView.setTo:" + (to==null?"to==null":to));
//	}
//	public String getTo(){
//		return to;
//	}

	public CyStageView(Context context) {
		super(context);
		init();
	}

	public CyStageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CyStageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		ptDebug.setARGB(150, 0, 255, 255);
//		CStageFactory.getInstance().pushView(this);
		setVisibility(View.INVISIBLE);
	}
	@Override
	public void setVisibility (int visibility) {
		super.setVisibility(visibility);
		//String str = stg==null||stg.id==null?"null":"sv["+stg.id+"]";
		if(visibility==4){
			//LeTool.log(str + ".h");
//			setBackgroundColor(BC);
		}else if(visibility==0){
			postInvalidate();
			//LeTool.log(str + ".show");
		}
	}
	public boolean start(String id){
		return start(id, (BuildCallback)null);
	}
	public boolean start(String id, BuildCallback cb){//, int c
		if(stg==null){
			CyStageFactory sf = CyStageFactory.getInstance();
			stg = sf.getStageById(id, add);
			if(stg!=null){
				stg.setCb(cb);
				stg.init(getContext());
				stg.id = id;
				if(w!=0&&h!=0){
					stg.resizeStage(ci, w, h, l, t, l - getPaddingLeft(), t - getPaddingTop());
				}
				if(needToFront){
					bringToFront();
				}
				setVisibility(View.VISIBLE);
				invalidate();
				return true;
			}else{
				CyTool.log("LeSurpriseView.start: sf.getStageById("+id+", add)==null");
				return false;
			}
		}else{
			CyTool.log("LeSurpriseView.start: Surprise is showing");
			return false;
		}
	}
	private boolean changeByPadding = false;
	@Override
	public void setPadding(int l, int t, int r, int b){
		super.setPadding(l, t, r, b);
//		CyTool.log("setPadding");
		changeByPadding = true;
	}
	private CyCutInfo ci = null;
	public void setCutInfo(CyCutInfo ci){
		this.ci = ci;
		resize();
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int r, int b) {
		if(changed||changeByPadding){
			changeByPadding = false;
			layoutW = r - left;
			layoutH = b - top;
			l = left + getPaddingLeft();
			t = top + getPaddingTop();
			w = r - l - getPaddingRight();
			h = b - t - getPaddingBottom();
			if(stg!=null){
				stg.resizeStage(ci, w, h, l, t, left, top);
			}
		}
		invalidate();
	}

	public void stop(){
		if(stg!=null){
			CyTool.log("CyStageView.stop()");
			stg.stop();
			setVisibility(View.INVISIBLE);
			waitBmp = true;
			waitParent = true;
			CyStageFactory.getMapStage().remove(stg.id);
			stg = null;
		}
	}
	//是否截断触摸事件
	private boolean eatTouchEvent = false;
	public void setEatTouchEvent(boolean b){
		eatTouchEvent = b;
	}
	@Override
	public boolean onTouchEvent(MotionEvent me){
		if(eatTouchEvent){
			return true;
		}else{
			return false;
		}
	}
	//child.aryBmp = parent.aryBmp
	private boolean waitParent = true;
	private void setChildBmp(){
		if(stg!=null){
			String parentId = stg.parentId;
			if(parentId!=null&&waitParent){
				CyStage parentStage = CyStageFactory.getInstance().getParentStage(parentId);
				if(parentStage!=null){
					synchronized(parentStage.aryBmp){
						if(parentStage.aryBmp[0]!=null){
							stg.aryBmp = parentStage.aryBmp;
							waitParent = false;
//							stg.parentId = null;
						}else{
							CyTool.log("CStageView.onDraw: "+parentId);
						}
					}
				}
				if(waitParent){
					invalidate();
				}
			}
		}
	}
	@Override
	protected void onDraw(Canvas can){
		super.onDraw(can);
		setChildBmp();
		if(stg==null){
			invalidate();
			return;
		}else{
			if(stg.isInitiative&&!CyStageConfig.isDebug){
				toZero = false;
				long dt = System.currentTimeMillis();
				stg.draw(can, dt, this);
				if(!stg.isEnd){
					invalidate();
				}else{
					stg = null;
					setVisibility(View.INVISIBLE);
				}
			}else{
				if(waitBmp){
					synchronized(stg.aryBmp){
						if(stg.aryBmp[0]!=null&&!stg.aryBmp[0].isRecycled()){
							waitBmp = false;
						}else{
							invalidate();
						}
					}
				}
				if(CyStageConfig.isDebug||stg.parentId!=null){
					invalidate();
				}
				if(toZero){
					invalidate();
					toZeroDt  = System.currentTimeMillis();
					float run = 1f*(toZeroDt - toZeroSt)/toZeroDr;
					if(run>1){
						run = 1;
					}
					run = ai.getInterpolation(run);
					rp = (1-run)*toZeroRp;
					if(rp<=0){
						rp = 0;
						toZero = false;
						CyActor ac = stg.getActor();
						if(ac!=null){
							CyActorListener al = ac.getAl();
							if(al!=null){
								al.onZero();
							}
						}
					}
				}
				stg.drawRp(can, rp, this);
			}
		}
		if(ptLine!=null){
			can.drawLine(stg.l, stg.t, stg.l + stg.w, stg.t, ptLine);
			can.drawLine(stg.l + stg.w, stg.t, stg.l + stg.w, stg.t + stg.h, ptLine);
			can.drawLine(stg.l + stg.w, stg.t + stg.h, stg.l, stg.t + stg.h, ptLine);
			can.drawLine(stg.l, stg.t + stg.h, stg.l, stg.t, ptLine);
			
			ptLine.setStrokeWidth(3);
			can.drawLine(stg.vL, stg.vT, stg.vL + stg.vW, stg.vT, ptLine);
			can.drawLine(stg.vL + stg.vW, stg.vT, stg.vL + stg.vW, stg.vT + stg.vH, ptLine);
			can.drawLine(stg.vL + stg.vW, stg.vT + stg.vH, stg.vL, stg.vT + stg.vH, ptLine);
			can.drawLine(stg.vL, stg.vT + stg.vH, stg.vL, stg.vT, ptLine);
			ptLine.setStrokeWidth(1);
			
			if(p!=null){
				can.drawLine(p.x, 0, p.x, layoutH, ptLine);
				can.drawLine(0, p.y, layoutW, p.y, ptLine);
			}
		}
		if(lstPoint.size()>0&&ptDebug!=null){
			ptDebug.setAlpha(255);
			for(int i=0;i<lstPoint.size();i++){
				int[] ary = lstPoint.get(i);
				if(ary!=null){
					can.drawCircle(ary[0], ary[1], 10, ptDebug);
				}
			}
		}
		if(lstRect.size()>0&&ptDebug!=null){
			ptDebug.setAlpha(100);
			for(int i=0;i<lstRect.size();i++){
				Rect rc = lstRect.get(i);
				if(rc!=null){
					can.drawRect(rc, ptDebug);
				}
			}
		}
	}
}
