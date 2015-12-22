package com.cyou.cma.clockscreen.widget.folder.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.widget.folder.CyFolderHelper;

public class CyFolderItem extends LinearLayout {
	public CyFolderItem(Context context) {
		super(context);
	}

	public CyFolderItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private Rect rc = new Rect();
	public Rect getRc(){
		return rc;
	}
	
//	private int mX = 0;
//	private int mY = 0;
//	private int rt = 0;
//	private float sc = 1f;
//	public void resize(int x, int y, float f, int r){
//		mX = x;
//		mY = y;
//		sc = f;
//		rt = r;
//	}
	
	private int index = 0;
	public void setIndex(int n){
		index = n;
	}
	public int getIndex(){
		return index;
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		rc.left = l;
		rc.top = t;
		rc.right = r;
		rc.bottom = b;
//		CyTool.log("CyFolderItem:"+rc.toShortString());
	}
//	@SuppressLint("NewApi") @Override
//	protected boolean drawChild(Canvas can, View v, long dt) {
//		boolean b = false;
//		CyFolderHelper hp = CyFolderHelper.getInstance();
//		if(hp!=null){
//			if(hp.isOpening()||hp.isClosing()){
//				hp.fresh();
//				invalidate();
//				float run = hp.getRun(index);
////				CyTool.log("CyFolderItem["+index+"] onDraw_"+run+": mX="+mX+";   mY="+mY);
//				hp.resize(this, run);
//				int cs = can.save();
//				int cx = mX + rc.width()/2;
//				int cy = mY + rc.height()/2;
////				can.rotate(rt, cx, cy);
////				can.scale(run, run, cx, cy);
//				can.translate(mX, mY);
////				Rect rc = can.getClipBounds();
////				rc.left = 0;
////				rc.top = 0;
////				rc.right = 720;
////				rc.bottom = 1280;
//				CyTool.log(rc.toShortString());
//				if(Build.VERSION.SDK_INT>10){
////					v.setAlpha(run);
//				}
//				b = super.drawChild(can, v, dt);
//				CyTool.log("CyFolderContainer onDraw");
//				can.restoreToCount(cs);
//			}else{
//				b = super.drawChild(can, v, dt);
////				CyTool.log("CyFolderContainer onDraw");
//			}
//		}else{
//			b = super.drawChild(can, v, dt);
////			CyTool.log("CyFolderContainer onDraw");
//		}
//		return b;
//	}
	@Override
    protected void onFinishInflate() {
		super.onFinishInflate();
		tv = (TextView)findViewById(R.id.folder_item_tv);
		iv = (ImageView)findViewById(R.id.folder_item_iv);
	}
	//view
	private TextView tv = null;
	public TextView getTv(){
		return tv;
	}
	private ImageView iv = null;
	public ImageView getIv(){
		return iv;
	}
	public void clean(){
		if(iv!=null){
			iv.setImageResource(R.drawable.icon_quicklaunch_mask_folder);
		}
		if(tv!=null){
			tv.setText(null);
		}
	}
	public void onDestroy(){
		clean();
		iv = null;
		tv = null;
	}
}
