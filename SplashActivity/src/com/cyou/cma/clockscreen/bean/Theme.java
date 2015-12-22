package com.cyou.cma.clockscreen.bean;


public class Theme implements EntityType, Cloneable {
    /**
	 * 
	 */
    private static final long serialVersionUID = -4856530978426809850L;
    public final static String UTF8 = "UTF-8";
    private String _id;
    private int id;
    private String title;
    private String description;
    private String author;
    private String category;
    private String tag;
    private int downloads;
    private long recommendTime;
    private long auditTime;
    private String updateTime;
    private String thumbnail;
    private double price;
    private String url;
    private String sizeStr;
    private long size;
    private String packageName;
    private String versionName;
    private int versionCode;
    private Group<Preview> previews;

// private boolean inner;

    private boolean downloaded;
    private boolean applied;
    private long lastmod;

// private boolean updateable;// 是否有更新 v2.2以后版本增加

// /**
// * 是否可以更新
// *
// * @return
// */
// public boolean isUpdateable() {
// return updateable;
// }

// public void setUpdateable(boolean updateable) {
// this.updateable = updateable;
// }

    public long getLastmod() {
        return lastmod;
    }

    public void setLatmod(long lastmod) {
        this.lastmod = lastmod;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getDownloads() {
        return downloads;
    }

    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }

    public long getRecommendTime() {
        return recommendTime;
    }

    public void setRecommendTime(long recommendTime) {
        this.recommendTime = recommendTime;
    }

    public long getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(long auditTime) {
        this.auditTime = auditTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSizeStr() {
        return sizeStr;
    }

    public void setSizeStr(String sizeStr) {
        this.sizeStr = sizeStr;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public Group<Preview> getPreviews() {
        return previews;
    }

    public void setPreviews(Group<Preview> previews) {
        this.previews = previews;
    }

// public boolean isInner() {
// return inner;
// }

// public void setInner(boolean inner) {
// this.inner = inner;
// }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public boolean isApplied() {
        return applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }

}
