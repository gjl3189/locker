package com.cyou.cma.clockscreen.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Rect;

public class CyGuideHelper {
	private static final String KEY_P = "main_guide_show";
	private static final String KEY_V = "show";
	
	public static boolean needShow(Context ct){
		boolean b = false;
		if(ct!=null){
			SharedPreferences sp = ct.getSharedPreferences(KEY_P, Context.MODE_PRIVATE);
			if(sp!=null){
				b = sp.getBoolean(KEY_V, true);
			}
		}
		return b;
	}
	public static void hasShow(Context ct){
		if(ct!=null){
			SharedPreferences sp = ct.getSharedPreferences(KEY_P, Context.MODE_PRIVATE);
			if(sp!=null){
				Editor ed = sp.edit();
				if(ed!=null){
					ed.putBoolean(KEY_V, false);
					ed.commit();
				}
			}
		}
	}
	
	public static Rect rc = new Rect();
	public static int pLeft = 0;
	public static int pTop = 0;
	public static int vLeft = 0;
	public static int vTop = 0;
	public static int cLeft = 0;
	public static int cTop = 0;
	public static int cRight = 0;
	public static int cBottom = 0;
	
	public static void setRc(){
		rc.left = pLeft + vLeft + cLeft;
		rc.top = pTop + vTop + cTop;
		rc.right = pLeft + vLeft + cRight;
		rc.bottom = pTop + vTop + cBottom;
	}
	
	public static boolean isShow = false;
	
	public static boolean inCircle(int x, int y){
		boolean b = false;
		int cX = rc.centerX();
		int cY = rc.centerY();
		int r = rc.width()/2;
		if((x-cX)*(x-cX)+(y-cY)*(y-cY)<=r*r){
			b = true;
		}
		return b;
	}
}
