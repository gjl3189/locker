package com.cyou.cma.cengine;

import java.util.ArrayList;
import java.util.List;

import com.cyou.cma.cengine.CyActor.CyActorListener;
import com.cyou.cma.cengine.anim.CyBoneAnimation;
import com.cyou.cma.cengine.anim.CyRect;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class CyBone {
	//是否把画布交由回调去绘制
	public boolean drawSelf = true;
	private static final Paint ptTmp = new Paint();
	static{
		ptTmp.setARGB(150, 120, 120, 120);
	}
	//父子关系
	private boolean isParent = false;
	private boolean isChild = false;
	public boolean isParent(){
		return isParent;
	}
	public boolean isChild(){
		return isChild;
	}
	public void setParent(boolean b){
		isParent = b;
	}
	public void setChild(boolean b){
		isChild = b;
	}
	
	public Rect rcBmp = null;
	public Rect rcBmpMirror = null;
	
	protected CyRect rc = new CyRect();
	private CyStage st = null;

	protected float rt = 0;
	protected int rx = 0;
	protected int ry = 0;
	protected int alpha = 255;

	protected void reset(){
		rt = 0;
		rx = 0;
		ry = 0;
		alpha = 255;
	}

//	protected void resetSize(){
//
//	}

	public List<CyBoneAnimation> lst = new ArrayList<CyBoneAnimation>();
	public void addAnim(CyBoneAnimation ba){
		lst.add(ba);
	}
	public CyBoneAnimation getLastAnim(){
		if(lst.size()==0){
			return null;
		}else{
			return lst.get(lst.size() - 1);
		}
	}

	public CyBone(Rect picRc, CyStage stg){
		rcBmp = picRc;
		st = stg;
	}
	private void resetRc(CyRect rc, CyBoneAnimation anim){
		if(st!=null){
			if(anim.matchParentX){
				rc.rcDst.left = st.rcStage.left;
				rc.rcDst.right = st.rcStage.right;
			}else{
				rc.rcDst.left = (int)(rc.rcSrc.left * st.scale) + st.l;
				rc.rcDst.right = (int)(rc.rcSrc.right * st.scale) + st.l;
			}
			rc.rcDst.top = (int)(rc.rcSrc.top * st.scale) + st.t;
			rc.rcDst.bottom = (int)(rc.rcSrc.bottom * st.scale) + st.t;
			rc.dstRx = (int)(rc.srcRx * st.scale) + st.l;
			rc.dstRy = (int)(rc.srcRy * st.scale) + st.t;
		}
	}
	private boolean inView(){
		boolean b = true;
		if(st!=null&&st.p!=4){
			if(rc.rcDst.bottom<st.rcStage.top){//up
				b = false;
			}else if(rc.rcDst.top>st.rcStage.bottom){//down
				b = false;
			}else if(rc.rcDst.right<st.rcStage.left){//left
				b = false;
			}else if(rc.rcDst.left>st.rcStage.right){//right
				b = false;
			}	
		}
		return b;
	}
	public CyBoneAnimation getAnimByRp(float rp){
		CyBoneAnimation anim = null;
		if(rcBmp!=null){
			for(int i=0;i<lst.size();i++){
				anim = lst.get(i);
				if(anim.isInAnimation(rp)){
					if(anim.isShow){
						break;
					}
				}
				anim = null;
			}
		}
		return anim;
	}
	protected void draw(CyActor ac ,float rp, Canvas can, Bitmap bmp){
		if(ac!=null){
			CyBoneAnimation anim = getAnimByRp(rp);
			if(anim!=null){
				anim.runAnimation(st, ac, rp, rc, isChild);
				resetRc(rc, anim);
				if(inView()){
					rt = rc.rt;
					rx = rc.dstRx;
					ry = rc.dstRy;
					
					alpha = rc.alpha;
						
					int cs = can.save();

//					if(isChild&&ac.currentRectP!=null){
//						can.rotate(ac.currentRectP.rt, ac.currentRectP.dstRx, ac.currentRectP.dstRy);
//					}
					can.rotate(rt, rx, ry);

					st.pt.setAlpha(alpha);
					if(drawSelf){
						if(anim.isMirror){
							if(rcBmpMirror==null){
								rcBmpMirror = new Rect(rcBmp.right, rcBmp.top, rcBmp.left, rcBmp.bottom);
							}
							can.drawBitmap(bmp, rcBmpMirror, rc.rcDst, st.pt);
						}else{
							can.drawBitmap(bmp, rcBmp, rc.rcDst, st.pt);
						}
					}else{
						if(CyStageConfig.isDebug){
							can.drawRect(rc.rcDst, ptTmp);
						}else{
							CyActorListener al = ac.getAl();
							if(al!=null){
								al.onDrawRc(can, bmp, rc.rcDst, st.pt);
							}
						}
					}
					can.restoreToCount(cs);
				}
			}
		}
	}
	public CyBone clone(){
		Rect rc = new Rect();
		rc.left = rcBmp.left;
		rc.top = rcBmp.top;
		rc.right = rcBmp.right;
		rc.bottom = rcBmp.bottom;
		CyBone bn = new CyBone(rc, st);
		bn.drawSelf = drawSelf;
		bn.isChild = isChild;
		bn.isParent = isParent;
		for(int i=0;i<lst.size();i++){
			bn.lst.add(lst.get(i));
		}
		return bn;
	}
}
