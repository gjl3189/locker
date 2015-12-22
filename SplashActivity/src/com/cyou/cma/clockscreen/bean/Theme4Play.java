package com.cyou.cma.clockscreen.bean;

public class Theme4Play implements EntityType {

    /**
     * 
     */
    private static final long serialVersionUID = 4543023084983167744L;
    public String id;
    public long lockscreenThemeId;
    public String title;
    public String description;
    public String author;
    public String thumbnail;
    public String googlePlayUrl;
    public String youtubeUrl;
    public long size;
    public String sizeText;
    public long auditTime;
    public String packageName;
    public int weight;
    public Group<Preview> previews;
    public boolean hasDownloaded;
    public boolean needAnim = true;

// public boolean isDownloaded() {// TODO jiangbin
// return true;
// }

    @Override
    public String toString() {
        return "Theme4Play [id=" + id + ", lockscreenThemeId=" + lockscreenThemeId + ", title=" + title
                + ", description=" + description + ", author=" + author + ", thumbnail=" + thumbnail
                + ", googlePlayUrl=" + googlePlayUrl + ", youtubeUrl=" + youtubeUrl + ", size=" + size
                + ", sizeText=" + sizeText + ", auditTime=" + auditTime + ", packageName=" + packageName
                + ", weight=" + weight + ", previews=" + previews + "]";
    }

    @Override
    public boolean equals(Object o) {
// return super.equals(o);
        if (o instanceof Theme4Play) {
            return ((Theme4Play) o).packageName.equals(this.packageName);
        } else {
            return false;
        }
    }

}
