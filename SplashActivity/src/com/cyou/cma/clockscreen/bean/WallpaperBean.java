package com.cyou.cma.clockscreen.bean;

import android.content.ContentValues;

import com.cyou.cma.clockscreen.sqlite.WallPaperProvider;

public class WallpaperBean {
    private int id;
    private String thumbPath;
    private String wallpaperPath;
    private long time;
    /**
     * 是否是默认提供的
     */
    private int isDefault;
    /**
     * 是否已选中(删除)
     */
    private boolean isSelected;
    /**
     * 是否内置的壁纸 1内置0不是内置
     */
    private int isProvide;

    /**
     * @return
     */
    public int getIsProvide() {
        return isProvide;
    }

    public void setIsProvide(int isProvide) {
        this.isProvide = isProvide;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getWallpaperPath() {
        return wallpaperPath;
    }

    public void setWallpaperPath(String wallpaperPath) {
        this.wallpaperPath = wallpaperPath;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public ContentValues getContentValues() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(WallPaperProvider.KEY_THUMBNAIL, getThumbPath());
        contentValues.put(WallPaperProvider.KEY_WALLPAPER, getWallpaperPath());
        contentValues.put(WallPaperProvider.KEY_DEFAULT, getIsDefault());
        contentValues.put(WallPaperProvider.KEY_TIME, getTime());
        contentValues.put(WallPaperProvider.KEY_PROVIDE, getIsProvide());
        return contentValues;
    }

}
