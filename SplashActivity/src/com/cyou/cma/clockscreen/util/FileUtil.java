package com.cyou.cma.clockscreen.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
	// 锁屏文件系统的根路径
	public static final String FILEPATH_ROOT = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ "clauncher.cyou.inc" + File.separator + "CLocker";
	// apk path
	public static final String FILEPATH_UPDATE = FILEPATH_ROOT + File.separator
			+ "apk" + File.separator;
	// wallpaper path
	public static final String FILEPATH_WALLPAPER = FILEPATH_ROOT
			+ File.separator + "wallpaper" + File.separator;
	public static final String FILEPATH_THUMBNAIL_WALLPAPER = FILEPATH_WALLPAPER
			+ "thumbnail" + File.separator;

	/**
	 * 判断SD是否可用
	 * 
	 * @return
	 */
	public static boolean isExternalStorageEnable() {
		String state = android.os.Environment.getExternalStorageState();
		if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 确保文件夹存在(不存在则创建)
	 * 
	 * @param fileDir
	 */
	public static void ensureExists(String fileDir) {
		File folder = new File(fileDir);
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}

	/**
	 * 复制单个文件
	 * 
	 * @param inStream
	 *            InputStream 原文件流
	 * @param newPath
	 *            String 复制后路径
	 * @return boolean
	 */
	public static boolean copyFile(InputStream inStream, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			if (inStream != null) { // 文件存在时
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			} else {
				return false;// 源文件不存在
			}
		} catch (Exception e) {
			Util.printException(e);
			return false;// 读取出错
		}
		return true;
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径
	 * @param newPath
	 *            String 复制后路径
	 * @return boolean
	 */
	/*
	 * public static boolean copyFile(String oldPath, String newPath) { try {
	 * int bytesum = 0; int byteread = 0; File oldfile = new File(oldPath); if
	 * (oldfile.exists()) { // 文件存在时 InputStream inStream = new
	 * FileInputStream(oldPath); // 读入原文件 FileOutputStream fs = new
	 * FileOutputStream(newPath); byte[] buffer = new byte[1444]; while
	 * ((byteread = inStream.read(buffer)) != -1) { bytesum += byteread; // 字节数
	 * 文件大小 System.out.println(bytesum); fs.write(buffer, 0, byteread); }
	 * inStream.close(); fs.close(); } else { return false;// 源文件不存在 } } catch
	 * (Exception e) { Util.printException(e);return false;// 读取出错 } return true;
	 * }
	 */

	/**
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	/*
	 * public void copyFolder(String oldPath, String newPath) { try { (new
	 * File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹 File a = new File(oldPath);
	 * String[] file = a.list(); File temp = null; for (int i = 0; i <
	 * file.length; i++) { if (oldPath.endsWith(File.separator)) { temp = new
	 * File(oldPath + file[i]); } else { temp = new File(oldPath +
	 * File.separator + file[i]); } if (temp.isFile()) { FileInputStream input =
	 * new FileInputStream(temp); FileOutputStream output = new
	 * FileOutputStream(newPath + "/" + (temp.getName()).toString()); byte[] b =
	 * new byte[1024 * 5]; int len; while ((len = input.read(b)) != -1) {
	 * output.write(b, 0, len); } output.flush(); output.close(); input.close();
	 * } if (temp.isDirectory()) {// 如果是子文件夹 copyFolder(oldPath + "/" + file[i],
	 * newPath + "/" + file[i]); } } } catch (Exception e) {
	 * System.out.println("复制整个文件夹内容操作出错"); Util.printException(e); } }
	 */

	/**
	 * 通过压缩图片的尺寸来压缩图片大小
	 * 
	 * @param pathName
	 *            图片的完整路径
	 * @param targetWidth
	 *            缩放的目标宽度
	 * @param targetHeight
	 *            缩放的目标高度
	 * @return 缩放后的图片
	 */
	public static Bitmap compressBySize(String pathName, int targetWidth,
			int targetHeight) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;// 不去真的解析图片，只是获取图片的头部信息，包含宽高等；
		Bitmap bitmap = BitmapFactory.decodeFile(pathName, opts);
		// 得到图片的宽度、高度；
		int imgWidth = opts.outWidth;
		int imgHeight = opts.outHeight;
		// 分别计算图片宽度、高度与目标宽度、高度的比例；取大于等于该比例的最小整数；
		int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
		int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
		if (widthRatio > 1 || widthRatio > 1) {
			if (widthRatio > heightRatio) {
				opts.inSampleSize = widthRatio;
			} else {
				opts.inSampleSize = heightRatio;
			}
		}
		// 设置好缩放比例后，加载图片进内容；
		opts.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(pathName, opts);
		return bitmap;
	}

	public static void saveBitmap(Bitmap bitmap, String savePath)
			throws Exception {
		File f = new File(savePath);
		if (f.exists()) {
			f.delete();
		}
		f.createNewFile();
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (Exception e) {
			Util.printException(e);
			return;
		}
		try {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		} catch (Exception e) {
			Util.printException(e);
		}
		try {
			fOut.flush();
		} catch (IOException e) {
			Util.printException(e);
		}
		try {
			fOut.close();
		} catch (IOException e) {
			Util.printException(e);
		}
	}
}
