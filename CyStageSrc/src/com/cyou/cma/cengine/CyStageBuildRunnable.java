package com.cyou.cma.cengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;

import com.cyou.cma.cengine.CyStage.BuildCallback;
import com.cyou.cma.cengine.xml.CyStageXmlHelper;

public class CyStageBuildRunnable implements Runnable{
	protected static List<CyStage> queue = new ArrayList<CyStage>();
	protected static boolean isRun = false;
	private Context ctx = null;
	protected CyStageBuildRunnable(Context c){
		ctx = c;
	}
	public static void build(Context ct, CyStage st){
		if(ct!=null&&st!=null){
			queue.add(st);
			if(!isRun){
				Thread t = new Thread(new CyStageBuildRunnable(ct));
				t.start();
			}
		}
	}
	@Override
	public void run() {
		CyTool.setThreadPriority();
		isRun = true;
		synchronized(queue){
			while(queue.size()>0){
				CyStage s = queue.get(0);
				if(s!=null){
					CyTool.log("init:"+s.config);
					BuildCallback cb = s.getCb();
					if(cb!=null){
						cb.onStart(s);
					}
					InputStream is = null;
					try {
						AssetManager am = null;
						boolean isAsset = CyStageConfig.isInAsset(s.config);
						boolean readXml = false;
						if(s.picUrl==null){
							if(isAsset){
								am = ctx.getAssets();
								is = am.open(s.config);
							}else{
								File f = new File(s.config);
								is = new FileInputStream(f);
							}
							if(is!=null){
								readXml = CyStageXmlHelper.getInstance().parse(is, s, ctx);
								s.setActorIndex(0);
							}else{
								CyTool.log("LeStage.init: is == null");
							}
						}else{
							readXml = true;
						}
						if(readXml){
							s.resizeStage(null, s.vW, s.vH, s.vL, s.vT, s.vLeft, s.vTop);
							synchronized(s.aryMp){
								if(s.aryMp[0]!=null){
									s.aryMp[0].stop();
									s.aryMp[0].release();
									s.aryMp[0] = null;
								}
							}
							if(s.mediaUrl!=null){
								boolean b = false;
								if(isAsset){
									b = true;
									if(s.mediaUrl.indexOf("/")==-1){
										s.mediaUrl = CyTool.getPath(s.config) + s.mediaUrl;
									}
								}else{
									if(s.mediaUrl.indexOf("/")==-1){
										s.mediaUrl = CyTool.getPath(s.config) + s.mediaUrl;
									}
									File f = new File(s.mediaUrl);
									if(f.exists()){
										b = true;
									}
								}
								synchronized(s.aryMp){
									if(b){
										s.aryMp[0] = new MediaPlayer();
										s.aryMp[0].reset();
										if(isAsset){
											if(am==null){
												am = ctx.getAssets();
											}
											AssetFileDescriptor fd = am.openFd(s.mediaUrl);
											s.aryMp[0].setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
										}else{
											s.aryMp[0].setDataSource(s.mediaUrl);
										}
										s.aryMp[0].prepare();
									}
								}
							}
							synchronized(s.aryBmp){
								if(s.parentId==null){
									if(s.aryBmp[0]!=null){
										CyTool.log(s.config + ": s.aryBmp[0].recycle()");
										s.aryBmp[0].recycle();
										s.aryBmp[0] = null;
									}
								}
								if(s.parentId==null||CyStageConfig.isDebug){
									if(isAsset){
										if(s.picUrl.indexOf("/")==-1){
											s.picUrl = CyTool.getPath(s.config) + s.picUrl;
										}
										s.aryBmp[0] = CyTool.getBmpByAssets(ctx, s.picUrl);
										if(s.aryBmp[0]==null){
											CyTool.log(s.config + ": bmp==null");
										}else{
											CyTool.log(s.config + ": bmp!=null");
										}
									}else{
										if(s.picUrl.indexOf("/")==-1){
											s.picUrl = CyTool.getPath(s.config) + s.picUrl;
										}
										s.aryBmp[0] = BitmapFactory.decodeFile(s.picUrl);
										if(s.aryBmp[0]!=null){
											CyTool.log(s.config + ": bmp ready");
										}
									}
								}
							}
							s.isEnd = false;
						}else{
							s.picUrl = null;
							s.isEnd = true;
						}
					} catch (Exception e) {
						String log = e.toString();
						android.util.Log.d("____", "LeStage.init: "+log);
						e.printStackTrace();
						s.isEnd = true;
					} finally{
						if(is!=null){
							try {
								is.close();
							} catch (IOException e) {
								e.printStackTrace();
							}finally{
								is = null;
							}
						}
						if(cb!=null){
							cb.onEnd(s);
						}
					}
				}
				queue.remove(s);
			}
		}
		isRun = false;
		ctx = null;
	}

}
