package com.cyou.cma.clockscreen.util;

import java.util.Calendar;

public class CyKeyHelper {
	private static final int DIGIT = 8;
	private static final int START_YEAR = 1920;
	private static final int ARY_RANGE = 3;
	private static char[] ary0 = {'a','k','u','8'};
	private static char[] ary1 = {'b','l','v','9'};
	private static char[] ary2 = {'c','m','w','0'};
	private static char[] ary3 = {'d','n','x','1'};
	private static char[] ary4 = {'e','o','y','2'};
	private static char[] ary5 = {'f','p','z','3'};
	private static char[] ary6 = {'g','q','4','4'};
	private static char[] ary7 = {'h','r','5','5'};
	private static char[] ary8 = {'i','s','6','6'};
	private static char[] ary9 = {'j','t','7','7'};
	
	public static CyKeyResult judgeKey(String key){
		CyKeyResult rs = new CyKeyResult();
		if(key==null){
			resetResult(rs);
			return rs;
		}else{
			key = key.toLowerCase();
			char[] ary = key.toCharArray();
			//校验位数
			if(ary==null||ary.length!=DIGIT){
				resetResult(rs);
				return rs;
			}
			//校验后4位
			boolean b = inArys(ary[4])&&inArys(ary[5])&&inArys(ary[6])&&inArys(ary[7]);
			if(!b){
				resetResult(rs);
				return rs;
			}
			//校验时间
			int year = 10*getNumByChar(ary[0]) + getNumByChar(ary[1]);
			int month = 10*getNumByChar(ary[2]) + getNumByChar(ary[3]);
			rs.year = START_YEAR + year;
			rs.month = month;
			//取得当前日期
			Calendar cal = Calendar.getInstance();
			int m = cal.get(Calendar.MONTH) + 1;
			rs.m = m;
			int y = cal.get(Calendar.YEAR);
			rs.y = y;
			if(12*y+m<=12*rs.year+rs.month){
				rs.isActive = true;
			}else{
				rs.isActive = false;
			}
		}
		return rs;
	}
	public static String getKey(int year, int month){
		char[] ary = new char[DIGIT];
		int ten = 0;
		ten = (year - START_YEAR)/10;
		ary[0] = getCharByNum(ten);
		ary[1] = getCharByNum((year - START_YEAR) - 10*ten);
		ten = month/10;
		ary[2] = getCharByNum(ten);
		ary[3] = getCharByNum(month - 10*ten);
		for(int i=4;i<ary.length;i++){
			int n = getRandom(9);
			ary[i] = getCharByNum(n);
		}
		return String.valueOf(ary);
	}
	private static void resetResult(CyKeyResult rs){
		rs.isActive = false;
		rs.year = 0;
		rs.month = 0;
	}
	private static int getNumByChar(char c){
		int n = 0;
		if(inAry(ary0, c)){
			n = 0;
		}else if(inAry(ary1, c)){
			n = 1;
		}else if(inAry(ary2, c)){
			n = 2;
		}else if(inAry(ary3, c)){
			n = 3;
		}else if(inAry(ary4, c)){
			n = 4;
		}else if(inAry(ary5, c)){
			n = 5;
		}else if(inAry(ary6, c)){
			n = 6;
		}else if(inAry(ary7, c)){
			n = 7;
		}else if(inAry(ary8, c)){
			n = 8;
		}else if(inAry(ary9, c)){
			n = 9;
		}
		return n;
	}
	private static char getCharByNum(int n){
		char c = ary0[0];
		int index = getRandom(ARY_RANGE);
		if(n==0){
			c = ary0[index];
		}else if(n==1){
			c = ary1[index];
		}else if(n==2){
			c = ary2[index];
		}else if(n==3){
			c = ary3[index];
		}else if(n==4){
			c = ary4[index];
		}else if(n==5){
			c = ary5[index];
		}else if(n==6){
			c = ary6[index];
		}else if(n==7){
			c = ary7[index];
		}else if(n==8){
			c = ary8[index];
		}else if(n==9){
			c = ary9[index];
		}
		return c;
	}
	private static int getRandom(int range){
		return (int)(Math.round(range*Math.random()));
	}
	private static boolean inAry(char[] ary, char c){
		boolean b = false;
		if(ary!=null){
			for(int i=0;i<ary.length;i++){
				if(c==ary[i]){
					b = true;
					break;
				}
			}	
		}
		return b;
	}
	private static boolean inArys(char c){
		boolean b = false;
		if(inAry(ary0, c)){
			b = true;
		}else if(inAry(ary1, c)){
			b = true;
		}else if(inAry(ary2, c)){
			b = true;
		}else if(inAry(ary3, c)){
			b = true;
		}else if(inAry(ary4, c)){
			b = true;
		}else if(inAry(ary5, c)){
			b = true;
		}else if(inAry(ary6, c)){
			b = true;
		}else if(inAry(ary7, c)){
			b = true;
		}else if(inAry(ary8, c)){
			b = true;
		}else if(inAry(ary9, c)){
			b = true;
		}
		return b;
	}
}
