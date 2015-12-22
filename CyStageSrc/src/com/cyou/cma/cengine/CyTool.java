package com.cyou.cma.cengine;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.AssetManager.AssetInputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

@SuppressLint("NewApi") public class CyTool {
	private static DisplayMetrics dm = new DisplayMetrics();
	public static final long NONE = -1l;
	public static final float ZERO_FLOAT = 0f;
//	public static final String SD = "mnt/sdcard";
//	public boolean canReadSd(){
//	       if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//	            String filePath=Environment.getExternalStorageDirectory().getPath();
//	       }
//		return true;
//	}
	public static String getPath(String config){
		if(config==null){
			return null;
		}else{
			return config.replace(CyStageConfig.CONFIG_NAME, "");
		}
	}
	public static String getFileName(String path){
		if(path==null){
			return null;
		}else{
			return path.substring(path.lastIndexOf("/")+1);
		}
	}
	public static void showToast(final Context ct, final String str){
		try{
			((Activity)ct).runOnUiThread(new Runnable(){
				@Override
				public void run() {
					Toast.makeText(ct, str, Toast.LENGTH_LONG).show();
				}
			});
		}catch(Exception ex){
			log(ex.toString());
		}
	}
	public static void log(String str){
//		android.util.Log.d("____", str);
	}
	public static void log(String flag, String str){
//		android.util.Log.d(flag, str);
	}
	public static void init(Context ct){
		if(ct==null){
			log("ct==null");
			return;
		}else{
			WindowManager wm = ((Activity)ct).getWindowManager();
			if(wm==null){
				log("wm==null");
				return;
			}
			Display dp = wm.getDefaultDisplay();
			if(dp==null){
				log("dp==null");
				return;
			}
			dp.getMetrics(dm);
			StringBuffer sb = new StringBuffer();
			sb.append("Resource: ");
			sb.append("w="+ct.getResources().getDisplayMetrics().widthPixels);
			sb.append(" h="+ct.getResources().getDisplayMetrics().heightPixels);
			log(sb.toString());
			sb.setLength(0);
		}
	}
	public static int getPxByDp(float dp, Context ct){
		return (int)(dp * dm.density + 0.5f);
	}
	public static boolean equalsFloat(float f1, float f2){
		return Math.abs(f1 - f2)<.01f;
	}
	public static boolean equalsZero(float f){
		return equalsFloat(f, ZERO_FLOAT);
	}
	public static Bitmap getBmpByAssets(Context ctx, String str){
		Bitmap bmp = null;
		if(ctx!=null){
			AssetManager asm = ctx.getAssets();
			if(asm!=null){
				InputStream is = null;
				try{
					is = asm.open(str);
					bmp = BitmapFactory.decodeStream(is);
				}catch(Exception e){
					CyTool.log(str+": "+e.toString());
				}finally{
					try{
						if(is!=null){
							is.close();
						}
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}
		}
		return bmp;
	}
	private static final String SEPARATOR = ",";
	public static String[] getAry(String str){
		if(str==null){
			return null;
		}else{
//			str = str.trim();
			return str.split(SEPARATOR);
		}
	}
	private static final int THREAD_PRIORITY = 3;
	public static void setThreadPriority(){
		Process.setThreadPriority(THREAD_PRIORITY);
	}
	private static final int BUFFER_LENTH = 1024*16;
	@SuppressLint("NewApi")
	public static boolean unZip(Context ct, String zipSrcAssetPath, String destDir){
		boolean b = false;
		if(zipSrcAssetPath == null || destDir == null){
			CyTool.log("illegal param, srcPath:" + zipSrcAssetPath + ",destDir:" + destDir);
		}else{
			BufferedOutputStream dest = null;
			InputStream fis = null;
			FileOutputStream fos = null;
			ZipInputStream zis = null;
			byte buf[] = new byte[BUFFER_LENTH];
			try {
				AssetManager asm = ct.getAssets();
				if(asm!=null){
					fis = asm.open(zipSrcAssetPath);
					zis = new ZipInputStream(new BufferedInputStream(fis));
					ZipEntry entry = null;
					File dir = new File(destDir);
					if(!dir.exists()){
						dir.mkdirs();
					}
					while ((entry = zis.getNextEntry()) != null) {
						if(entry.isDirectory()){
							File newSubDir = new File(destDir + entry.getName());
							newSubDir.mkdir();
						}else{
							String name = entry.getName();
			                File outputFile = new File(destDir + name);
			                String outputPath = outputFile.getCanonicalPath();
			                name = outputPath.substring(outputPath.lastIndexOf("/") + 1);
			                outputPath = outputPath.substring(0, outputPath.lastIndexOf("/"));
			                File outputDir = new File(outputPath);
			                outputDir.mkdirs();
			                outputFile = new File(outputPath, name);
			                outputFile.createNewFile();
			                fos = new FileOutputStream(outputFile);

			                int numread = 0;
			                do {
			                    numread = zis.read(buf);
			                    if (numread <= 0) {
			                        break;
			                    } else {
			                    	fos.write(buf, 0, numread);
			                    }
			                } while (true);
			                fos.close();
							
//							int count = 0;
//							byte data[] = new byte[BUFFER_LENTH];
//							String strEntry = entry.getName();
//							File entryFile = new File(destDir + strEntry);
//							fos = new FileOutputStream(entryFile);
//							dest = new BufferedOutputStream(fos, BUFFER_LENTH);
//							while ((count = zis.read(data, 0, BUFFER_LENTH)) != -1) {
//								dest.write(data, 0, count);
//							}
//							dest.flush();
//							dest.close();
						}
					}
					b = true;
				}
			} catch (Exception e) {
				CyTool.log(e.toString());
			} finally{
				if(dest!=null){
					try {
						dest.close();
						dest = null;
					} catch (IOException e) {
						CyTool.log(e.toString());
					}
				}
				if(fos!=null){
					try {
						fos.close();
						fos = null;
					} catch (IOException e) {
						CyTool.log(e.toString());
					}
				}
				if(zis!=null){
					try {
						zis.close();
						zis = null;
					} catch (IOException e) {
						CyTool.log(e.toString());
					}
				}
				if(fis!=null){
					try {
						fis.close();
						fis = null;
					} catch (IOException e) {
						CyTool.log(e.toString());
					}
				}
			}
		}
//		LeTool.log("unzip file:" + zipSrcAssetPath + ", to :" + destDir + ",result:" + b);
		return b;
	}
	public static Bitmap resizeBitmap(Bitmap bmp, int width, int height) {
		return resizeBitmap(bmp, width, height, true);
	}
    public static Bitmap resizeBitmap(Bitmap bmp, int width, int height, boolean deletSrc) {
    	Bitmap newBmp = null;
        try {
            if(bmp!=null&&!bmp.isRecycled()) {
                if (bmp.getWidth()>width||bmp.getHeight()>height) {
                    Matrix matrix = new Matrix();
                    matrix.postScale(((float) width)/bmp.getWidth(), ((float)height)/bmp.getHeight());
                    newBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                    if(deletSrc){
                    	bmp.recycle();
                    }
                }else{
                	newBmp = bmp;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(newBmp!=null){
            	if(!newBmp.isRecycled()){
            		newBmp.recycle();
            		newBmp = null;
            	}
            }
            newBmp = bmp;
        }
        return newBmp;
    }
    public static Bitmap getBlurBmp(Bitmap bmp, int scale, int radius){
    	return getBlurBmp(null, bmp, scale, radius);
    }
    public static Bitmap getBlurBmp(Context ct, Bitmap bmp, int scale, int radius) {  
    	return getBlurBmp(null, bmp, scale, radius, true);
    }
    public static Bitmap getBlurBmp(Context ct, Bitmap bmp, int scale, int radius, boolean deletSrc) {  
    	if(bmp==null||bmp.isRecycled()){
    		return null;
    	}
    	if(scale<1){
    		return null;
    	}else if(scale==1){
    		if(!bmp.isMutable()){
    			Bitmap src = bmp;
        		bmp = src.copy(src.getConfig(), true);
        		src.recycle();
        		src = null;
    		}
    	}else{
            bmp = resizeBitmap(bmp, bmp.getWidth()/scale, bmp.getHeight()/scale, deletSrc);
    	}
        if (radius < 1||radius>25) {  
            return (null);  
        }
        
        if(Build.VERSION.SDK_INT>=17&&ct!=null){
    		getBlurBmpRs(ct, bmp, radius);
        }else{
        	getBlurBmpAry(bmp, radius);
        }
        return (bmp);  
    }
    public static boolean isHighSdk(){
    	boolean b = false;
    	if(Build.VERSION.SDK_INT>10){
    		b = true;
        }
    	return b;
    }
    private static void getBlurBmpRs(Context ct, Bitmap bmp, int radius){
    	RenderScript rs = RenderScript.create(ct);
	    Allocation input = Allocation.createFromBitmap(rs, bmp);
	    Allocation output = Allocation.createTyped(rs, input.getType());
	    ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
	    script.setRadius(radius);
	    script.setInput(input);
	    script.forEach(output);
	    output.copyTo(bmp);
    }
    private static void getBlurBmpAry(Bitmap bmp, int radius){
    	int w = bmp.getWidth();  
        int h = bmp.getHeight();  
  
        int[] pix = new int[w * h];  
        bmp.getPixels(pix, 0, w, 0, 0, w, h);  
  
        int wm = w - 1;  
        int hm = h - 1;  
        int wh = w * h;  
        int div = radius + radius + 1;  
  
        int r[] = new int[wh];  
        int g[] = new int[wh];  
        int b[] = new int[wh];  
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;  
        int vmin[] = new int[Math.max(w, h)];  
  
        int divsum = (div + 1) >> 1;  
        divsum *= divsum;  
        int dv[] = new int[256 * divsum];  
        for (i = 0; i < 256 * divsum; i++) {  
            dv[i] = (i / divsum);  
        }  
  
        yw = yi = 0;  
  
        int[][] stack = new int[div][3];  
        int stackpointer;  
        int stackstart;  
        int[] sir;  
        int rbs;  
        int r1 = radius + 1;  
        int routsum, goutsum, boutsum;  
        int rinsum, ginsum, binsum;  
  
        for (y = 0; y < h; y++) {  
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;  
            for (i = -radius; i <= radius; i++) {  
                p = pix[yi + Math.min(wm, Math.max(i, 0))];  
                sir = stack[i + radius];  
                sir[0] = (p & 0xff0000) >> 16;  
                sir[1] = (p & 0x00ff00) >> 8;  
                sir[2] = (p & 0x0000ff);  
                rbs = r1 - Math.abs(i);  
                rsum += sir[0] * rbs;  
                gsum += sir[1] * rbs;  
                bsum += sir[2] * rbs;  
                if (i > 0) {  
                    rinsum += sir[0];  
                    ginsum += sir[1];  
                    binsum += sir[2];  
                } else {  
                    routsum += sir[0];  
                    goutsum += sir[1];  
                    boutsum += sir[2];  
                }  
            }  
            stackpointer = radius;  
  
            for (x = 0; x < w; x++) {  
  
                r[yi] = dv[rsum];  
                g[yi] = dv[gsum];  
                b[yi] = dv[bsum];  
  
                rsum -= routsum;  
                gsum -= goutsum;  
                bsum -= boutsum;  
  
                stackstart = stackpointer - radius + div;  
                sir = stack[stackstart % div];  
  
                routsum -= sir[0];  
                goutsum -= sir[1];  
                boutsum -= sir[2];  
  
                if (y == 0) {  
                    vmin[x] = Math.min(x + radius + 1, wm);  
                }  
                p = pix[yw + vmin[x]];  
  
                sir[0] = (p & 0xff0000) >> 16;  
                sir[1] = (p & 0x00ff00) >> 8;  
                sir[2] = (p & 0x0000ff);  
  
                rinsum += sir[0];  
                ginsum += sir[1];  
                binsum += sir[2];  
  
                rsum += rinsum;  
                gsum += ginsum;  
                bsum += binsum;  
  
                stackpointer = (stackpointer + 1) % div;  
                sir = stack[(stackpointer) % div];  
  
                routsum += sir[0];  
                goutsum += sir[1];  
                boutsum += sir[2];  
  
                rinsum -= sir[0];  
                ginsum -= sir[1];  
                binsum -= sir[2];  
  
                yi++;  
            }  
            yw += w;  
        }  
        for (x = 0; x < w; x++) {  
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;  
            yp = -radius * w;  
            for (i = -radius; i <= radius; i++) {  
                yi = Math.max(0, yp) + x;  
  
                sir = stack[i + radius];  
  
                sir[0] = r[yi];  
                sir[1] = g[yi];  
                sir[2] = b[yi];  
  
                rbs = r1 - Math.abs(i);  
  
                rsum += r[yi] * rbs;  
                gsum += g[yi] * rbs;  
                bsum += b[yi] * rbs;  
  
                if (i > 0) {  
                    rinsum += sir[0];  
                    ginsum += sir[1];  
                    binsum += sir[2];  
                } else {  
                    routsum += sir[0];  
                    goutsum += sir[1];  
                    boutsum += sir[2];  
                }  
  
                if (i < hm) {  
                    yp += w;  
                }  
            }  
            yi = x;  
            stackpointer = radius;  
            for (y = 0; y < h; y++) {  
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )  
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];  
  
                rsum -= routsum;  
                gsum -= goutsum;  
                bsum -= boutsum;  
  
                stackstart = stackpointer - radius + div;  
                sir = stack[stackstart % div];  
  
                routsum -= sir[0];  
                goutsum -= sir[1];  
                boutsum -= sir[2];  
  
                if (x == 0) {  
                    vmin[y] = Math.min(y + r1, hm) * w;  
                }  
                p = x + vmin[y];  
  
                sir[0] = r[p];  
                sir[1] = g[p];  
                sir[2] = b[p];  
  
                rinsum += sir[0];  
                ginsum += sir[1];  
                binsum += sir[2];  
  
                rsum += rinsum;  
                gsum += ginsum;  
                bsum += binsum;  
  
                stackpointer = (stackpointer + 1) % div;  
                sir = stack[stackpointer];  
  
                routsum += sir[0];  
                goutsum += sir[1];  
                boutsum += sir[2];  
  
                rinsum -= sir[0];  
                ginsum -= sir[1];  
                binsum -= sir[2];  
  
                yi += w;  
            }  
        }  
        bmp.setPixels(pix, 0, w, 0, 0, w, h); 
    }
}
