package com.cyou.cma.cengine;

import java.util.ArrayList;
import java.util.List;

public class CyStageConfig {
	public static boolean isDebug = false;

	public static final String CONFIG_NAME = "config.xml";
	
	public static List<String> aryAssetRes = new ArrayList<String>();
	public static List<String> aryAssetWelcomeRes = new ArrayList<String>();
	
	public static synchronized void addAryAssetRes(String url){
		if(url!=null){
			if(!aryAssetRes.contains(url)){
				aryAssetRes.add(url);
			}
		}
	}
	public static synchronized void addAryAssetWelcomeRes(String url){
		if(url!=null){
			if(!aryAssetWelcomeRes.contains(url)){
				aryAssetWelcomeRes.add(url);
			}
		}
	}
	public static synchronized boolean isInAsset(String url){
		boolean b = false;
		if(url!=null){
			if(aryAssetRes.contains(url)){
				b = true;
			}
			if(!b){
				if(aryAssetWelcomeRes.contains(url)){
					b = true;
				}
			}
		}
		return b;
	}
}
