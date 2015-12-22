package com.cyou.cma.clockscreen.bean;

import android.graphics.Bitmap;

public class CoreBitmapObj {
	private float scale = 1;
	private int x;
	private int y;
	private Bitmap bitmap;

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public CoreBitmapObj() {

	}

	public void init(Bitmap bitmap, float scale, int x, int y) {
		this.bitmap = bitmap;
		this.scale = scale;
		this.x = x;
		this.y = y;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
