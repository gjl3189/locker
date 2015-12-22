package com.cyou.cma.cengine;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;

public class CyStageManager {
//	private static LeSurpriseManager sm = null;
//	public synchronized static LeSurpriseManager getInstance() {
//		if(sm==null){
//			sm = new LeSurpriseManager();
//		}
//        return sm;
//    }

	// send surprise to the other one
	private static List<Integer> lst = new ArrayList<Integer>();
	private static final int MAX = 10;
	private static void push(int id){
		if(lst.size()>MAX){
			CyTool.log("LeSurpriseManager.push("+id+"): lst.size()="+lst.size());
		}else{
			lst.add(id);
		}
	}
	public static void pop(int id){
		lst.remove((Integer)id);
	}
	public static boolean isInLst(int id){
		if(lst.indexOf(id)==-1){
			return false;
		}else{
			return true;
		}
	}

	public static void send(String id, String to, String tag, Context ct){
		CyTool.log("LeSurpriseManager.send Sp: id=" + id + ";  to=" + to);
//		int msg = EngineLoader.getInstance().sendMessage(to, id, LeSurpriseMainReceiver.MSG_TYPE);
//		push(msg);
	}
	//get surprise from the other one
//	public static void get(String id, String to, Context ct){
//		LeTool.log("LeSurpriseManager.get: id="+(id==null?"null":id)+"   to="+(to==null?"null":to)+"   ct="+(ct==null?"null":"not null"));
//		if(ct!=null){
//			Intent it = new Intent(GET);
//			it.putExtra(ID, id);
//			it.putExtra(TO, to);
//			ct.sendBroadcast(it);
//		}
//	}
}
