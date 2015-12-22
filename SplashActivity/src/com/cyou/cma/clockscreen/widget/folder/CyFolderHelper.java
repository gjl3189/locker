package com.cyou.cma.clockscreen.widget.folder;

import java.util.ArrayList;
import java.util.List;

import com.cyou.cma.cengine.CyTool;
import com.cyou.cma.clockscreen.widget.folder.ui.CyFolderContainer;
import com.cyou.cma.clockscreen.widget.folder.ui.CyFolderItem;
import com.cyou.cma.clockscreen.widget.folder.util.CyFolder;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class CyFolderHelper {
	private static CyFolderHelper hp = null;
	public synchronized static CyFolderHelper getInstance() {
		if(hp==null){
			hp = new CyFolderHelper();
		}
        return hp;
    }
	
	private List<CyFolderItem> lstView = new ArrayList<CyFolderItem>();
	public List<CyFolderItem> getLstView(){
		return lstView;
	}
	
	private View cn = null;
	public void setCn(View v){
		cn = v;
		if(cn!=null){
			cn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(isShow()){
						hide();
					}
				}
			});
		}
	}
	
	private int total = 0;
	
	private static final int DRATION = 1000;
	private int dr = DRATION;
	private void setDr(int n){//0~7
		dr = DRATION/2 + n*DRATION/14;
	}
	private static final int ROTATE = 360*3;
	private int space = 0;
//	private long startTime = 0;
	private int animTime = 0;
	
	private int fromX = 0;
	private int fromY = 0;
	public enum Status{
		Opening, Show, Closing, Hide
	}
	private Status st = Status.Hide;
	public boolean isOpening(){
		return st==Status.Opening;
	}
	public boolean isClosing(){
		return st==Status.Closing;
	}
	public boolean isShow(){
		return st==Status.Show;
	}
	public boolean isHide(){
		return st==Status.Hide;
	}
	private List<CyFolder> lstData = new ArrayList<CyFolder>();
	public void show(int x, int y, List<CyFolder> lst){
//		CyTool.log("show at("+x+", "+y+")");
		if(st==Status.Hide){
			if(cn!=null&&lst!=null){
				total = lst.size();
				if(total>0&&total<=CyFolderContainer.aryId.length){
					setDr(total - 1);
					fromX = x;
					fromY = y;
//					startTime = 0;
					st = Status.Opening;
					int n = total - 1;
					if(n==0){
						space = 0;
					}else{
						space = dr/3/n;
					}
					animTime = dr - n*space;
					lstData.clear();
					for(int i=0;i<lstView.size();i++){
						if(i<total){
							CyFolder cf = lst.get(i);
							lstData.add(cf);
							CyFolderItem v = lstView.get(i);
							if(v!=null&&cf!=null){
								TextView tv = v.getTv();
								if(tv!=null){
									tv.setText(cf.getName());
								}
								ImageView iv = v.getIv();
								if(iv!=null){
									iv.setImageBitmap(cf.getBmp());
								}
								v.setVisibility(View.VISIBLE);
								v.setOnClickListener(cf.getCl());
								v.setClickable(false);
//								v.invalidate();
								//开启动画
								Rect rc = v.getRc();
								if(rc!=null){
//									if(i==0){
//										CyTool.log("rc.width()="+rc.width()+";   rc.height()="+rc.height()+";   ("+rc.centerX()+", "+rc.centerY()+")");
//									}
									Animation anim = getAnim(true, v, i, rc);
									if(anim!=null){
										v.startAnimation(anim);	
									}else{
										if(i==total-1){
											setShow();
										}	
									}
								}else{
									if(i==total-1){
										setShow();
									}
								}
							}
						}else{
							CyFolderItem v = lstView.get(i);
							if(v!=null){
								v.clean();
								v.clearAnimation();
								v.setVisibility(View.INVISIBLE);
								v.setOnClickListener(null);
								v.setClickable(false);
							}
						}
					}
					cn.setVisibility(View.VISIBLE);
					cn.setBackgroundColor(Color.argb(150, 0, 0, 0));
				}
			}
		}
	}
	public void hide(){
		if(st==Status.Show){
			st = st.Closing;
//			startTime = 0;
			for(int i=0;i<lstView.size();i++){
				CyFolderItem v = lstView.get(i);
				if(v!=null){
					if(v.getVisibility()==View.VISIBLE){
						Rect rc = v.getRc();
						if(rc!=null){
//							if(i==0){
//								CyTool.log("rc.width()="+rc.width()+";   rc.height()="+rc.height()+";   ("+rc.centerX()+", "+rc.centerY()+")");
//							}
							Animation anim = getAnim(false, v, i, rc);
							if(anim!=null){
								v.startAnimation(anim);	
							}else{
								if(i==0){
									setHide();
								}	
							}
						}else{
							if(i==0){
								setHide();
							}
						}
					}
				}
			}
		}
	}
	private void setShow(){
		st = Status.Show;
		for(int i=0;i<lstView.size();i++){
			if(i<total){
				View v = lstView.get(i);
				if(v!=null){
					v.clearAnimation();
					v.setClickable(true);
				}
			}else{
				View v = lstView.get(i);
				if(v!=null){
					v.clearAnimation();
					v.setClickable(false);
				}
			}
		}
//		CyTool.log("setShow");
	}
	private void setHide(){
		for(int i=0;i<lstView.size();i++){
			View v = lstView.get(i);
			if(v!=null){
				v.clearAnimation();
				v.setClickable(false);
			}
		}
		cn.setVisibility(View.INVISIBLE);
		cn.setBackgroundColor(Color.argb(0, 0, 0, 0));
		clean();
		st = Status.Hide;
	}
	//获取动画
	private static final DecelerateInterpolator DI = new DecelerateInterpolator();
	private static final String[] aryDevice = new String[]{"GT-I93", "GT-S7562C", "SCl22"};
	private int rcLeft = 0; 
	private Animation getAnim(boolean isOpening, final View v, final int index, Rect rc){
		if(index==0&&rc!=null){
			rcLeft = rc.left;
		}
		if(Build.VERSION.SDK_INT<14){
			return null;
		}else{
			if(index==0){
//				CyTool.log("isOpening="+isOpening+";   fromX="+fromX+";   fromY="+fromY);
				if(cn!=null){
					boolean showAnim = true;
					String str = Build.MODEL;
					if(str!=null){
						for(int i=0;i<aryDevice.length;i++){
							if(str.startsWith(aryDevice[i])){
								showAnim = false;
								break;
							}	
						}
					}
					if(showAnim){
						if(isOpening){
							AlphaAnimation al = new AlphaAnimation(0, 1);
							al.setDuration(dr);
							al.setInterpolator(DI);
							cn.startAnimation(al);
						}else{
							AlphaAnimation al = new AlphaAnimation(1, 0);
							al.setDuration(dr);
							al.setInterpolator(DI);
							cn.startAnimation(al);
						}	
					}
				}
			}
			if(cn!=null){
				cn.invalidate();
			}
			AnimationSet as = new AnimationSet(true);
			as.setDuration(animTime);
			if(isOpening){
				//旋转
				RotateAnimation rt = new RotateAnimation(0, ROTATE, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
				as.addAnimation(rt);
				//位移
				int startX = fromX - rc.centerX() + rcLeft;
				int startY = fromY - rc.centerY();
				if(index<4){
					startY -= line_0_Y;
				}else{
					startY -= line_1_Y;
				}
				TranslateAnimation tl = new TranslateAnimation(startX, 0, startY, 0);
				as.setStartOffset(index*space);
				as.setInterpolator(DI);
				as.addAnimation(tl);
				//透明
				AlphaAnimation al = new AlphaAnimation(0, 1);
				as.addAnimation(al);
				//缩放
				ScaleAnimation sc = new ScaleAnimation(0, 1, 0, 1, startX, startY);
				as.addAnimation(sc);
				//监听
				if(index==total-1){
					AnimationListener ls = new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {}
						@Override
						public void onAnimationRepeat(Animation animation) {}
						@Override
						public void onAnimationEnd(Animation animation) {
							setShow();
						}
					};
					as.setAnimationListener(ls);
				}
			}else{
				//旋转
				RotateAnimation rt = new RotateAnimation(0, ROTATE, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
				as.addAnimation(rt);
				//位移
				int startX = fromX - rc.centerX() + rcLeft;
				int startY = fromY - rc.centerY();
				if(index<4){
					startY -= line_0_Y;
				}else{
					startY -= line_1_Y;
				}
				TranslateAnimation tl = new TranslateAnimation(0, startX, 0, startY);
				as.setStartOffset(index*space);
				as.setInterpolator(DI);
				as.addAnimation(tl);
				//透明
				AlphaAnimation al = new AlphaAnimation(1, 0);
				as.addAnimation(al);
				//缩放
				ScaleAnimation sc = new ScaleAnimation(1, 0, 1, 0, startX, startY);
				as.addAnimation(sc);
				//监听
				AnimationListener ls = new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {}
					@Override
					public void onAnimationRepeat(Animation animation) {}
					@Override
					public void onAnimationEnd(Animation animation) {
						if(index==total-1){
							setHide();
						}else{
							v.setVisibility(View.INVISIBLE);
						}
					}
				};
				as.setAnimationListener(ls);
			}
			return as;
		}
	}
//	//获取绘制状态
//	public float getRun(int index){
//		float run = 0;
//		if(startTime>0){
//			long dt = System.currentTimeMillis();
//			long st = index*space;
//			long rt = dt - startTime - st;
//			run = 1f*rt/animTime;
//			if(run<0){
//				run = 0;
//			}else if(run>1){
//				run = 1;
//			}
//			run = 1 - (1 - run)*(1 - run);
//		}else{
//			startTime = System.currentTimeMillis();
//			run = 0;
//		}
//		if(isClosing()){
//			if((index==(total - 1))&&(run==1)){
//				cn.setVisibility(View.INVISIBLE);
//				cn.setBackgroundColor(Color.argb(0, 0, 0, 0));
//				clean();
//				st = Status.Hide;
//			}
//			run = 1 - run;
//		}else{
//			if((index==(total - 1))&&(run==1)){
//				setShow();
//			}
//		}
//		return run;
//	}
//	public void resize(CyFolderItem item, float run){
//		if(item!=null){
//			Rect rc = item.getRc();
//			int cX = rc.centerX();
//			int cY = rc.centerY();
//			int fY = fromY;
//			if(item.getIndex()<4){
//				fY -= line_0_Y;
//			}else{
//				fY -= line_1_Y;
//			}
//			int x = (int)((fromX - cX)*(1f - run));
//			int y = (int)((fY - cY)*(1f - run));
//			int r = (int)(ROTATE*run);
//			item.resize(x, y, run, r);
//		}
//	}
	//行位置
	public int line_0_Y = 0;
	public int line_1_Y = 0;
	//销毁
	public void onDestroy(){
		cn = null;
		for(int i=0;i<lstView.size();i++){
			CyFolderItem v = lstView.get(i);
			if(v!=null){
				v.onDestroy();
			}
		}
		lstView.clear();
		clean();
		hp = null;
	}
	private void clean(){
		st = Status.Hide;
		for(int i=0;i<lstView.size();i++){
			CyFolderItem v = lstView.get(i);
			if(v!=null){
				v.clean();
			}
		}
		for(int i=0;i<lstData.size();i++){
			CyFolder f = lstData.get(i);
			if(f!=null){
				Bitmap bmp = f.getBmp();
				if(bmp!=null&&!bmp.isRecycled()){
					bmp.recycle();
					bmp = null;
					f.setBmp(null);
				}
			}
		}
		lstData.clear();
	}
}
