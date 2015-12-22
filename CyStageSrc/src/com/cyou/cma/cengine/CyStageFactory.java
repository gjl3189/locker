package com.cyou.cma.cengine;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;

public class CyStageFactory {

	private static Map<String, CyStage> mapStage = new HashMap<String, CyStage>();
	private static Map<String, String> mapConfig = new HashMap<String, String>();

//	private List<CStageView> lstView = new ArrayList<CStageView>();
//	public void pushView(CStageView v){
//		if(v!=null){
//			synchronized(lstView){
//				lstView.add(v);
//			}
//		}
//	}
//	public void popView(CStageView v){
//		if(v!=null){
//			synchronized(lstView){
//				v.stop();
//				lstView.remove(v);
//			}
//		}
//	}
//	public void popView(Context ct){
//		List<CStageView> lst = getViewLst(ct);
//		if(lst!=null){
//			for(int i=0;i<lst.size();i++){
//				popView(lst.get(i));
//			}
//		}
//	}
//
//	public List<CStageView> getViewLst(Context ct){
//		return getViewLst(ct, NONE);
//	}

	public static final byte TRUE = 1;
	public static final byte FALSE = -1;
	public static final byte NONE = 0;
//	public List<CStageView> getViewLst(Context ct, byte flag){
//		if(ct==null){
//			return null;
//		}else{
//			List<CStageView> lst = new ArrayList<CStageView>();
//			synchronized(lstView){
//				for(int i=0;i<lstView.size();i++){
//					CStageView v = lstView.get(i);
//					if(v!=null){
//						Context c = v.getContext();
//						if(c!=null){
//							if(ct.hashCode()==c.hashCode()){
//								if(flag==NONE){
//									lst.add(v);
//								}else if(flag==TRUE&&v.canReceiveBroadcast()){
//									lst.add(v);
//								}else if(flag==FALSE&&!v.canReceiveBroadcast()){
//									lst.add(v);
//								}
//							}
//						}
//					}
//				}
//			}
//			return lst;
//		}
//	}
//	public void setViewActivityShow(Context ct, boolean b){
//		List<CStageView> lst = getViewLst(ct);
//		if(lst!=null){
//			for(int i=0;i<lst.size();i++){
//				CStageView v = lst.get(i);
//				if(v!=null){
//					v.setActivityShow(b);
//				}
//			}
//		}
//	}
//	public void sendSurprise(Context ct, String id, String to, String tag){
//		if(ct!=null&&id!=null){
//			Intent it = new Intent(CyStageMainReceiver.SEND);
//			it.putExtra(CyStageMainReceiver.ID, id);
//			it.putExtra(CyStageMainReceiver.TO, to);
//			it.putExtra(CyStageMainReceiver.TAG, tag);
//			ct.sendBroadcast(it);
//		}
//	}
//	public void getSurprise(Context ct, String id, String to, byte sq, String tag){
//		boolean b = false;
//		synchronized(lstView){
//			for(int i=0;i<lstView.size();i++){
//				CStageView v = lstView.get(i);
//				if(v!=null){
//					CTool.log("LeStageFactory.getSurprise: v.canReceiveBroadcast="+v.canReceiveBroadcast()+";   v.isActivityShow="+v.isActivityShow());
//					if(v.canReceiveBroadcast()){
//						if(v.isActivityShow()){
//							b = true;
//							if(v.start(id, to)){
//								break;
//							}else{
//								continue;
//							}
//						}
//					}
//				}
//			}
//		}
////		LeSpSelectorTool.sendPlayResult(ct, b, sq, tag, to);
//	}

	public static Map<String, String> getMapConfig(){
		getInstance();
		return mapConfig;
	}
	
	public static Map<String, CyStage> getMapStage(){
		getInstance();
		return mapStage;
	}
	
	public static void addAssetStage(String id, String res){
		getInstance();
		if(id!=null&&res!=null){
//			CyTool.log("id="+id+";   res="+res); 
			mapConfig.put(id, res);
			CyStageConfig.addAryAssetRes(res);
		}
	}

	private static CyStageFactory sf = null;

	public synchronized static CyStageFactory getInstance() {
		if(sf==null){
			sf = new CyStageFactory();
			mapConfig.clear();
			mapStage.clear();
		}
        return sf;
    }
	
//	public static void initMapConfig(String[] aryId, String[] aryRes){
//		if(aryId!=null&&aryRes!=null){
//			mapConfig.clear();
//			for(int i=0;i<aryId.length;i++){
//				String key = aryId[i];
//				if(key!=null){
//					mapConfig.put(aryId[i], aryRes[i]);
//				}
//			}
//		}
//	}

    public boolean hasStage(String id){
    	if(mapConfig.get(id)==null){
    		return false;
    	}else{
    		return true;
    	}
    }
	public CyStage getStageById(String id, boolean add){
		if(hasStage(id)){
			CyStage st = mapStage.get(id);
			String config = mapConfig.get(id);
			if(st==null){
				st = new CyStage(config);
				if(add){
					mapStage.put(id, st);
				}
				st.isEnd = false;
			}else{
				if(!st.isEnd){//if st isEnd, use st; ifnot, use new st
					st = new CyStage(config);
				}
			}
			return st;
		}else{
			android.util.Log.w("____", "getStageById("+id+")==null");
			return null;
		}
	}
	public CyStage getParentStage(String id){
		return mapStage.get(id);
	}
	public CyStage getStageById(String id){
		return getStageById(id, true);
	}
//	public void setTo(String to, Context ct){
//		CTool.log("LeSpLinearLayout.setTo:" + (to==null?"to==null":to));
//		CStageFactory sf = CStageFactory.getInstance();
//		List<CStageView> lst = sf.getViewLst(ct, CStageFactory.TRUE);
//		if(lst!=null){
//			for(int i=0;i<lst.size();i++){
//				CStageView v = lst.get(i);
//				if(v!=null){
//					v.setTo(to);
//				}
//			}
//		}
//	}
}
