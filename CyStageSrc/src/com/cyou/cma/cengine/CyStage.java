package com.cyou.cma.cengine;

import java.util.ArrayList;
import java.util.List;

import com.cyou.cma.cengine.CyActor.CyActorListener;
import com.cyou.cma.cengine.anim.CyBoneAnimation;
import com.cyou.cma.cengine.anim.CyRect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.media.MediaPlayer;

public class CyStage {
	//加载回调函数
	private BuildCallback cb = null;
	public void setCb(BuildCallback cb){
		this.cb = cb;
	}
	public BuildCallback getCb(){
		return cb;
	}
	public static interface BuildCallback {
		public void onStart(final CyStage st);
		public void onEnd(final CyStage st);
	}
	//need 'rp' to decide animation process 主动渲染
	public boolean isInitiative = false;
	//Stage中当前Actor的index
	private int[] aryActorIndex = new int[]{0};
	public int getActorIndex(){
		return aryActorIndex[0];
	}
	//指定Stage中的Actor进行播放
	public void setActorIndex(int n){
		synchronized(aryActorIndex){
			CyActor ac = getActor(n);
			if(ac!=null){
				aryActorIndex[0] = n;
				loadActor(ac);
			}else{
				CyTool.log("CyStage.setActorIndex: n="+n+" Error");
			}
		}
	}
	public void resetActorIndex(){
		synchronized(aryActorIndex){
			aryActorIndex[0] = 0;
		}
	}
	public void delActorByIndex(int n){
		synchronized(aryActorIndex){
			lst.remove(n);
			if(n<aryActorIndex[0]){
				aryActorIndex[0]--; 
			}else if(n==aryActorIndex[0]){
				setActorIndex(0);
			}
		}
	}
	//装配新的Actor
	public void loadActor(CyActor ac){
		if(ac!=null){
			isInitiative = ac.isInitiative;
			dr = ac.dr;
			st = CyTool.NONE;
		}
	}
	//显示帧率
	private static final int MAX = 100;
	private static final int SECOND = 1000;
	private boolean showFps = false;
	public void showFps(boolean b){
		showFps = b;
		lstFps.clear();
	}
	private final List<Integer> lstFps = new ArrayList<Integer>();
	public void showFps(long d){
		if(lstFps.size()>=MAX){
			float sum = 0;
			for(int i=0;i<lstFps.size();i++){
				sum += lstFps.get(i);
			}
			CyTool.log(id+"Fps: "+sum/lstFps.size());
			lstFps.clear();
		}else{
			int n = (int)d;
			lstFps.add(SECOND/n);
		}
	}
	//***
	public String config = null;
	public CyStage(String str){
		config = str;
	}
	public Bitmap[] aryBmp = new Bitmap[1];
	private long st = CyTool.NONE;//start time
	private long ldt = CyTool.NONE;
	public static final int DURATION = 9000;
	public long dr = DURATION;
	//stage位置和大小
	public float scale = 0f;

	public int sW = 990;
	public int sH = 1280;
	public int vW = 0;
	public int vH = 0;
	public int vL = 0;
	public int vT = 0;
	public int vLeft = 0;//不计算padding的vL
	public int vTop = 0;//不计算padding的vT
	public Rect rcStage = new Rect();
	//end
	public int l = 0;
	public int t = 0;
	public int w = 0;
	public int h = 0;

	public String id = null;
	public String name = null;
	public String picUrl = null;
	public String mediaUrl = null;
	public MediaPlayer[] aryMp = new MediaPlayer[1];
	public String parentId = null;//use other stage's bitmap
	//播放状态
	public boolean isPaused = false;
	public boolean isEnd = false;
	//increase draw quality
	protected static final Paint pt = new Paint();
	private static final PaintFlagsDrawFilter pd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);

	public void init(Context ct){
		isEnd = false;
		pt.setAntiAlias(true);
		CyStageBuildRunnable.build(ct, this);
	}
	public byte p = 0;//0 - center, 1 - top, 2 - bottom, 3 - center + 1/3top, 4 - Y方向顶屏幕
	public float scaleType = 0;
	private void resize(CyCutInfo ci){
		if(ci==null){
			if(p==4){
				scale = 1f*vH/sH;
			}else{
				float sc = 1f*vW/sW;
				if(sc*sH>vH){
					sc = 1f*vH/sH;
				}else{
					float tmp = sc;
					sc = 1f*vH/sH;
					if(sc*sW>vW){
						sc = tmp;
					}else{
						sc = Math.max(sc, tmp);
					}
				}
				if(scaleType==1){
					sc = Math.min(sc, 1);
				}else if(scaleType<1&&scaleType>0) {
					sc = sc*scaleType;
					sc = Math.min(sc, 2);
				}
				scale = sc;
			}
			w = (int)(scale*sW);
			h = (int)(scale*sH);
			l = (vW - w)/2 + vL;
			if(p==1){//top
				t = 0 + vT;
			}else if(p==2){//bottom
				t = vH - h + vT;
			}else if(p==3){//bottom
				t = (vH - h)/3 + vT;
			}else{
				t = (vH - h)/2 + vT;
			}
		}else{
			scale = ci.scale;
			w = vW;
			h = vH;
			l = (int)(-ci.left*scale);
			t = (int)(-ci.top*scale);
		}
//		left = l;
//		top = t;
	}
	public static int[] getPoint(
			int x, int y, int p,
			int screenWidth, int screenHeight, 
			int stageWidth, int stageHeight, 
			int screenLeft, int screenTop,
			int type){
		Rect rc = new Rect();
		rc.left = x;
		rc.top = y;
		buildRect(
			rc, p,
			screenWidth, screenHeight,
			stageWidth, stageHeight,
			screenLeft, screenTop,
			type
		);
		int[] ary = new int[2];
		ary[0] = rc.left;
		ary[1] = rc.top;
		return ary;
	}
	//为匹配锁屏背景新增的函数
	public static void buildRect(
			Rect rcSrc, int p,
			int screenWidth, int screenHeight, 
			int stageWidth, int stageHeight, 
			int screenLeft, int screenTop,
			int type){
		float sc = 0;
		if(p==4){
			sc = 1f*screenHeight/stageHeight;
		}else{
			sc = 1f*screenWidth/stageWidth;
			if(sc*stageHeight>screenHeight){
				sc = 1f*screenHeight/stageHeight;
			}else{
				float tmp = sc;
				sc = 1f*screenHeight/stageHeight;
				if(sc*stageWidth>screenWidth){
					sc = tmp;
				}else{
					sc = Math.max(sc, tmp);
				}
			}
			if(type==1){
				sc = Math.min(sc, 1);
			}else if(type<1&&type>0) {
				sc = sc*type;
				sc = Math.min(sc, 2);
			}
		}
		float scale = sc;
		int w = (int)(scale*stageWidth);
		int h = (int)(scale*stageHeight);
		int l = (screenWidth - w)/2 + screenLeft;
		int t = 0;
		if(type==1){//top
			t = 0 + screenTop;
		}else if(type==2){//bottom
			t = screenHeight - h + screenTop;
		}else if(type==3){//bottom
			t = (screenHeight - h)/3 + screenTop;
		}else{
			t = (screenHeight - h)/2 + screenTop;
		}
		rcSrc.left = (int)(rcSrc.left * scale) + l;
		rcSrc.top = (int)(rcSrc.top * scale) + t;
		rcSrc.right = (int)(rcSrc.right * scale) + l;
		rcSrc.bottom = (int)(rcSrc.bottom * scale) + t;
	}
	public void resizeStage(CyCutInfo ci, int vW, int vH, int vL, int vT, int left, int top){
		//CyTool.log("vW="+vW+";   vH="+vH+";   vL="+vL+";   vT="+vT);
		this.vW = vW;
		this.vH = vH;
		this.vL = vL;
		this.vT = vT;
		
		vLeft = left;
		vTop = top;
		
		rcStage.top = vT;
		rcStage.bottom = vT + vH;
		rcStage.left = vL;
		rcStage.right = vL + vW;

		if(sW==0||sH==0||vW==0||vH==0){
			return;
		}
		resize(ci);
	}

	public List<CyActor> lst = new ArrayList<CyActor>();
	public void addActor(CyActor at){
		lst.add(at);
	}
	public CyActor getLastActor(){
		if(lst.size()==0){
			return null;
		}else{
			return lst.get(lst.size() - 1);
		}
	}
	public CyActor getActor(int n){
		CyActor ac = null;
		try{
			ac = lst.get(n);
		}catch(Exception ex){
			CyTool.log(ex.toString());
		}
		return ac;
	}
	public CyActor getActor(){
		return getActor(aryActorIndex[0]);
	}
	protected void drawRt(Canvas can, long rt, CyStageView sv){
		if(dr!=0){
			drawRp(can, rt*1f/dr, sv);
		}else{
			CyTool.log("CStage.drawRt():dr==0 - "+config);
		}
	}
	public void runParent(float rp, CyActor ac){
		if(ac!=null){
			if(ac.parent!=null){
				CyBoneAnimation anim = ac.parent.getAnimByRp(rp);
				if(anim==null){
					CyTool.log("runParent: parent!=null");
				}else{
					anim.runAnimation(this, ac, rp, ac.currentRectP, false);
					if(ac.firstRectP==null){
						ac.firstRectP = new CyRect();
						anim.runAnimation(this, ac, 0, ac.firstRectP, false);
					}
				}
			}
		}
	}
	private void drawActor(CyActor ac, Canvas can, float rp){
		synchronized(aryBmp){
			if(aryBmp[0]==null||isEnd){
				return;
			}else{
				int cs = can.save();
				can.translate(-vLeft, -vTop);
				can.setDrawFilter(pd);
				runParent(rp, ac);
				ac.draw(rp, can, aryBmp[0]);
				can.restoreToCount(cs);
			}
		}
	}
	protected void drawRp(Canvas can, float rp, CyStageView sv){
		synchronized(aryActorIndex){
			CyActor ac = getActor(aryActorIndex[0]);
			if(ac!=null){
				if(rp<0||rp>1){
					if(ac.repeat){
						rp = 0;
						st = ldt;
					}else{
						CyActorListener al = ac.getAl();
						if(al!=null){
							rp = al.onEnd(sv, this);
							ac = getActor(aryActorIndex[0]);
						}
					}
					if(rp>1f){
						rp = 1f;
						st = ldt;
					}else if(rp<0){
						rp = 0f;
						st = ldt;
					}
				}
				drawActor(ac, can, rp);
			}
		}
	}
	protected boolean isDrawing(){
		synchronized(aryBmp){
			if(CyTool.equalsZero(scale)||aryBmp[0]==null){
				return false;
			}
		}
		if(isEnd){
			return false;
		}
		return true;
	}
	public void draw(Canvas can, long dt, CyStageView sv){
		if(isEnd||isPaused){
			return;
		}
		synchronized(aryBmp){
			if(CyTool.equalsZero(scale)||aryBmp[0]==null){
				return;
			}
		}
		synchronized(aryActorIndex){
			if(st==CyTool.NONE){
				CyActor ac = getActor();
				if(ac!=null){
					CyActorListener al = ac.getAl();
					if(al!=null){
						al.onStart();
					}
				}
				st = dt;
				synchronized(aryMp){
					if(aryMp[0]!=null){
						aryMp[0].start();
					}
				}
			}
		}
		if(ldt!=CyTool.NONE){
			if(showFps){
				showFps(dt - ldt);
			}
		}
		ldt = dt;
		long rt = dt - st;
		drawRt(can, rt, sv);
	}
	public void stop(){
		isEnd = true;
//			for(int i=0;i<lst.size();i++){
//				lst.get(i).reset();
//			}
		synchronized(aryMp){
			if(aryMp[0]!=null){
				aryMp[0].stop();
				aryMp[0].release();
				aryMp[0] = null;
			}
		}
		if(parentId==null){
			synchronized(aryBmp){
				if(aryBmp[0]!=null){
					aryBmp[0].recycle();
					aryBmp[0] = null;
				}
			}
		}
		st = CyTool.NONE;
	}
	protected CyStage clone(String config){
		return new CyStage(config);
	}
}
