package com.cyou.cma.cengine.anim;

import com.cyou.cma.cengine.CyActor;
import com.cyou.cma.cengine.CyStage;
import com.cyou.cma.cengine.CyTool;
import com.cyou.cma.cengine.xml.CXmlHandler;
import com.cyou.cma.cengine.xml.CyBaHelper;

public class CyBoneAnimation {
	public boolean isMirror = false;
	//自由移动的圆形区域
	private boolean isCircle = false;
	private int radius = 0;
	public void setCircle(int r){
		isCircle = true;
		radius = r;
	}
	//x、y:屏幕坐标系，触摸点到中心点的距离
	//k:stage坐标系/屏幕坐标系 的比例
	public void setTouch(int x, int y, float k){
		int rangeX = (int)(x*k);
		int rangeY = (int)(y*k);
		toLeft = fromLeft + rangeX;
		toTop = fromTop + rangeY;
		toRight = fromRight + rangeX;
		toBottom = fromBottom + rangeY;
	}
	//end
	public float[] ary = new float[2];//start, end

	public CyBaHelper bh = null;
	//速率曲线
	public String rt = CXmlHandler.aryRate[0];

//	public boolean isRectAction = false;
	public int fromLeft = 0;
	public int fromTop = 0;
	public int fromRight = 0;
	public int fromBottom = 0;
	public int toLeft = 0;
	public int toTop = 0;
	public int toRight = 0;
	public int toBottom = 0;
	
	public boolean matchParentX = false;

	public boolean isShow = true;

	public void setRotate(float fr, float tr, int x, int y){
		fromR = fr;
		toR = tr;
		offsetX = x;
		offsetY = y;
//		isRotate = true;
	}
//	public boolean isRotate = true;
	public float fromR = 0;
	public float toR = 0;
	public int offsetX = 0;
	public int offsetY = 0;

//	public boolean isAlpha = true;
	public int fromA = 255;
	public int toA = 255;
	public void setAlpha(int f, int t){
//		isAlpha = true;
		fromA = f;
		toA = t;
	}

	public boolean isInAnimation(float rp){
		if(ary==null){
			return false;
		}else{
			if(rp>=ary[0]&&rp<=ary[1]){
				return true;
			}else{
				return false;
			}
		}
	}
	
	public int[] getParentOffset(CyStage st, CyActor ac, boolean isChild){
		int[] ary = new int[]{0, 0};
		if(st!=null&&isChild&&ac.parent!=null&&ac.currentRectP!=null&&ac.firstRectP!=null){
			ary[0] = ac.currentRectP.rcSrc.left - ac.firstRectP.rcSrc.left;
			ary[1] = ac.currentRectP.rcSrc.top - ac.firstRectP.rcSrc.top;
		}
		return ary;
	}

	private float getRp(float rp){
		if(rt.equals(CXmlHandler.aryRate[1])){
			rp = rp*rp;
		}else if(rt.equals(CXmlHandler.aryRate[2])){
			rp = 1 - (1 - rp)*(1 - rp);
		}
		return rp;
	}
	
	public void runAnimation(CyStage st, CyActor ac, float rp, CyRect rc, boolean isChild){
		rp = (rp - ary[0])/(ary[1] - ary[0]);
		rp = getRp(rp);
//		if(isRectAction){
//			rc.isRectAction = true;
			rc.rcSrc.left = fromLeft + (int)(rp*(toLeft - fromLeft));
			rc.rcSrc.top = fromTop + (int)(rp*(toTop - fromTop));
			rc.rcSrc.right = fromRight + (int)(rp*(toRight - fromRight));
			rc.rcSrc.bottom = fromBottom + (int)(rp*(toBottom - fromBottom));
			

			int[] ary = getParentOffset(st, ac, isChild);
			rc.rcSrc.left += ary[0];
			rc.rcSrc.top += ary[1];
			rc.rcSrc.right += ary[0];
			rc.rcSrc.bottom += ary[1];
//				CyTool.log("left: "+(rc.rcSrc.left-offsetX)+" to "+rc.rcSrc.left+";   x="+offsetX);
//				CyTool.log("top: "+(rc.rcSrc.top-offsetY)+" to "+rc.rcSrc.top+";   y="+offsetY);
			
//			LeTool.log("rp="+rp+";   rcSrc="+rc.rcSrc.toString());
//		}else{
//			rc.isRectAction = false;
//		}
//		if(isRotate){
//			rc.isRotate = true;
			rc.rt = fromR + (rp*(toR - fromR));
			rc.srcRx = rc.rcSrc.left + offsetX;
			rc.srcRy = rc.rcSrc.top + offsetY;
//		}else{
//			rc.isRotate = false;
//		}
//		if(isAlpha){
//			rc.isAlpha = true;
			rc.alpha = fromA + (int)(rp*(toA - fromA));
//		}else{
//			rc.isAlpha = false;
//		}
	}
}
