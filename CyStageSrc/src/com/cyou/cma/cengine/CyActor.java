package com.cyou.cma.cengine;

import java.util.ArrayList;
import java.util.List;
import com.cyou.cma.cengine.anim.CyRect;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class CyActor {
	public String name = "default";
	//need 'rp' to decide animation process 主动渲染
	public boolean isInitiative = false;
	//骨骼序列
	public List<CyBone> lst = new ArrayList<CyBone>();
	//动画时长
	public long dr = CyStage.DURATION;
	//是否重复
	public boolean repeat = false;
	//父子模式***
	public CyBone parent = null;
	public void setParent(CyBone bn){
		parent = bn;
		if(bn==null){
			firstRectP = null;
		}
	}
	public CyRect firstRectP = null;
	public CyRect currentRectP = new CyRect();
	//***
	public void addBone(CyBone bn){
		lst.add(bn);
	}
	public CyBone getLastBone(){
		return lst.get(lst.size() - 1);
	}
	protected void draw(float rp, Canvas can, Bitmap bmp){
		if(al!=null){
			al.onRun(rp);
		}
		for(int i=0;i<lst.size();i++){
			lst.get(i).draw(this, rp, can, bmp);
		}
	}
//	public void resetSize(){
//		for(int i=0;i<lst.size();i++){
//			lst.get(i).resetSize();
//		}
//	}
//	protected void reset(){
//		for(int i=0;i<lst.size();i++){
//			lst.get(i).reset();
//		}
//	}
	private CyActorListener al = null;
	public CyActorListener getAl(){
		return al;
	}
	public void setAl(CyActorListener al){
		this.al = al;
	}
	public interface CyActorListener{
		public void onStart();
		public void onDrawRc(Canvas can, Bitmap bmp, Rect rc, Paint pt);
		public void onRun(float run);
		public void onZero();
		public float onEnd(CyStageView sv, CyStage st);//当此Actor动画结束时，stage结束到哪一帧
	}
	public CyActor clone(){
		CyActor ac = new CyActor();
		ac.isInitiative = isInitiative;
		ac.dr = dr;
		ac.repeat = repeat;
		ac.parent = parent;
		for(int i=0;i<lst.size();i++){
			ac.lst.add(lst.get(i));
		}
		return ac;
	}
}