package com.cyou.cma.clockscreen.defaulttheme;

import android.os.Build;

public class CyWallpaperHelper {
	private static final String[] aryDevice = new String[]{"GT-S7562"};
	public static boolean useWave(){
		boolean b = true;
		String device = Build.MODEL;
		if(device==null){
			b = false;
		}else{
			for(int i=0;i<aryDevice.length;i++){
				if(aryDevice[i].equals(device)){
					b = false;
					break;
				}
			}
		}
		return b;
	}
	
	public static final int NONE = 0;
	
	public static int width = NONE;//屏幕宽度
	public static int height = NONE;//屏幕高度
	
	public static boolean setSize(int w, int h){
		boolean change = false;
		if(width!=w){
			width = w;
			change = true;
		}
		if(height!=h){
			height = h;
			change = true;
		}
		return change;
	}
	
	public static boolean hasSize(){
		return (width!=NONE)&&(height!=NONE);
	}
}
