package com.cyou.cma.clockscreen.bean;

public class Preview implements EntityType {
    private static final long serialVersionUID = 762430441976223224L;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static Group<Preview> convertString2Group(String previews) {
        Group<Preview> group = new Group<Preview>();
        if (previews != null && (!"".equals(previews))) {
            String[] preivewArray = previews.split(";");
            group = new Group<Preview>();
            if (preivewArray != null) {
                int length = preivewArray.length;
                for (int i = 0; i < length; i++) {
                    Preview preview = new Preview();
                    preview.setUrl(preivewArray[i]);
                    group.add(preview);
                }
            }
        }
        return group;
    }

    public static String convertGroup2String(Group<Preview> previews) {
        if (previews != null) {
            String previewStr = "";

            for (int index = 0; index < previews.size(); index++) {
                if (index == previews.size() - 1) {
                    previewStr += previews.get(index).getUrl();
                } else {
                    previewStr += previews.get(index).getUrl() + ";";
                }
            }
            return previewStr;
        } else {
            return "";
        }
    }
}
