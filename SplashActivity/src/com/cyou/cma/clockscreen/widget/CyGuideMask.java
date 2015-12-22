package com.cyou.cma.clockscreen.widget;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.util.CyGuideHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CyGuideMask extends RelativeLayout{
	public CyGuideMask(Context context) {
		super(context);
		init();
	}
	public CyGuideMask(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public CyGuideMask(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if(getVisibility()==VISIBLE){
			th = (TapWithHandView)findViewById(R.id.guide_hand);
			if(th!=null){
				th.startDraw();
			}
			tv = (TextView)findViewById(R.id.guide_txt);
		}
	}


	private Bitmap[] aryBmp = new Bitmap[]{null};
	
	private Bitmap[] aryBmpBg = new Bitmap[]{null};
	
	private TapWithHandView th = null;
	private TextView tv = null;
	
	private static final Paint pt = new Paint();
	
	private static final Rect rcTop = new Rect();
	private static final Rect rcBottom = new Rect();
	private static final Rect rcLeft = new Rect();
	private static final Rect rcRight = new Rect();
	
	
	private void init(){
		if(CyGuideHelper.needShow(getContext())){
			setVisibility(VISIBLE);
			CyGuideHelper.isShow = true;
			pt.setARGB(153, 0, 0, 0);
			synchronized(aryBmp){
				aryBmp[0] = BitmapFactory.decodeResource(getResources(), R.drawable.main_guide_mask);
			}
			synchronized(aryBmpBg){
				aryBmpBg[0] = BitmapFactory.decodeResource(getResources(), R.drawable.main_guide_mask_bg);
			}
		}else{
			setVisibility(GONE);
		}
	}
    @Override
    public void setVisibility(int visibility) {
    	super.setVisibility(visibility);
    	if(visibility==GONE){
        	synchronized(aryBmp){
    			if(aryBmp[0]!=null){
    				aryBmp[0].recycle();
    				aryBmp[0] = null;
    			}
    		}
        	synchronized(aryBmpBg){
    			if(aryBmpBg[0]!=null){
    				aryBmpBg[0].recycle();
    				aryBmpBg[0] = null;
    			}
    		}
        	if(th!=null){
        		th.cleanUp();
        		th = null;
        	}
        	if(tv!=null){
        		tv = null;
        	}
    	}
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
    	boolean b = false;
    	int kc = event.getKeyCode();
    	if(kc==KeyEvent.KEYCODE_BACK||kc==KeyEvent.KEYCODE_ESCAPE){
    		if(event.getAction()==KeyEvent.ACTION_UP){
    			setVisibility(GONE);
    		}
    		b = true;
    	}else{
    		b = super.dispatchKeyEvent(event);
    	}
    	return b;
    }
	@Override
	protected void onLayout(boolean changed, int left, int top, int r, int b) {
		super.onLayout(changed, left, top, r, b);
		//rcTop
		rcTop.left = 0;
		rcTop.top = 0;
		rcTop.right = r;
		rcTop.bottom = CyGuideHelper.rc.top;
		//rcBottom
		rcBottom.left = 0;
		rcBottom.top = CyGuideHelper.rc.bottom;
		rcBottom.right = r;
		rcBottom.bottom = b;
		//rcLeft
		rcLeft.left = 0;
		rcLeft.top = CyGuideHelper.rc.top;
		rcLeft.right = CyGuideHelper.rc.left;
		rcLeft.bottom = CyGuideHelper.rc.bottom;
		//rcRight
		rcRight.left = CyGuideHelper.rc.right;
		rcRight.top = CyGuideHelper.rc.top;
		rcRight.right = r;
		rcRight.bottom = CyGuideHelper.rc.bottom;
	}
	 @Override
    protected void onDraw(Canvas cv) {
		 if(!CyGuideHelper.isShow){
			 setVisibility(GONE);
		 }
		synchronized(aryBmpBg){
			if(aryBmpBg[0]!=null&&!aryBmpBg[0].isRecycled()){
				//top
				rcTop.bottom = CyGuideHelper.rc.top;
//				cv.drawRect(rcTop, pt);
				cv.drawBitmap(aryBmpBg[0], null, rcTop, null);
				//bottom
				rcBottom.top = CyGuideHelper.rc.bottom;
//				cv.drawRect(rcBottom, pt);
				cv.drawBitmap(aryBmpBg[0], null, rcBottom, null);
				//left
				rcLeft.top = CyGuideHelper.rc.top;
				rcLeft.right = CyGuideHelper.rc.left;
				rcLeft.bottom = CyGuideHelper.rc.bottom;
//				cv.drawRect(rcLeft, pt);
				cv.drawBitmap(aryBmpBg[0], null, rcLeft, null);
				//right
				rcRight.left = CyGuideHelper.rc.right;
				rcRight.top = CyGuideHelper.rc.top;
				rcRight.bottom = CyGuideHelper.rc.bottom;
//				cv.drawRect(rcRight, pt);
				cv.drawBitmap(aryBmpBg[0], null, rcRight, null);
			}
		}
        synchronized(aryBmp){
			if(aryBmp[0]!=null&&!aryBmp[0].isRecycled()){
				/*cv.save();  
		        cv.clipRect(CyGuideHelper.rc, Region.Op.XOR);
		        cv.drawARGB(153, 0, 0, 0);
		        cv.restore();*/
		        cv.drawBitmap(aryBmp[0], null, CyGuideHelper.rc, null);
		        
				if(th!=null){
					int x = (int) (CyGuideHelper.rc.left + CyGuideHelper.rc.width()*.55f);
					int y = (int) (CyGuideHelper.rc.top + CyGuideHelper.rc.height()*.65f);
					
					//移动
					long dt = System.currentTimeMillis();
					float run = getRun(dt);
					int offset = (int)(run*D);
					x += offset;
					y += offset;
					//end
					
					if(Build.VERSION.SDK_INT>10){
						th.setX(x);
						th.setY(y);
					}else{
						RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)th.getLayoutParams();
						if(lp!=null){
							lp.leftMargin = x;
							lp.topMargin = y;
							th.setLayoutParams(lp);
						}
					}
				}
				if(tv!=null){
					int y = CyGuideHelper.rc.bottom + 30;
					if(Build.VERSION.SDK_INT>10){
						tv.setY(y);
					}else{
						RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)tv.getLayoutParams();
						if(lp!=null){
							lp.topMargin = y;
							tv.setLayoutParams(lp);
						}
					}
				}
		        //super.onDraw(cv);
			}else{
		        super.onDraw(cv);
			}
		}
	 }
	 //位移动画
	 private long start = 0;
	 private static final long DR = 800;
	 private static final int D = 20;
	 private boolean add = true;
	 public float getRun(long dt){
		 float run = 0;
		 if(start!=0){
			 run = 1f*(dt - start)/DR;
			 if(run>=1){
				 run = 0;
				 start = dt;
				 add = !add; 
			 }
			 if(!add){
				 run = 1 - run;
			 }
		 }else{
			 start = dt;
		 }
		 return run;
	 }
}
