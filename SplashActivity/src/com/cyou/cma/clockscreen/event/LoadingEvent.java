package com.cyou.cma.clockscreen.event;

public class LoadingEvent {
	public static final int TYPE1= 1;//第一次
	public static final int TYPE2= 2;// 用户自己选择了 
	
	public int type ;
	public int count;
	public boolean allSelected;
	public int allCount;
//	public boolean first;
}
