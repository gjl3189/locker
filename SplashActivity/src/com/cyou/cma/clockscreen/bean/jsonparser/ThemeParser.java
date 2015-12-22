package com.cyou.cma.clockscreen.bean.jsonparser;

import com.cyou.cma.clockscreen.bean.Group;
import com.cyou.cma.clockscreen.bean.Preview;
import com.cyou.cma.clockscreen.bean.Theme;

import org.json.JSONObject;

public class ThemeParser extends AbstractParser<Theme> {

    @Override
    Theme parserInner(JSONObject jsonObject) throws Exception {
        Theme theme = new Theme();

        String _id = jsonObject.getString("_id");
        int id = jsonObject.getInt("id");
        String title = jsonObject.getString("title");
        String description = jsonObject.getString("description");
        String author = jsonObject.getString("author");
        String category = jsonObject.getString("category");
        String tag = jsonObject.getString("tag");
        int downloads = jsonObject.getInt("downloads");
        long recommendTime = jsonObject.getLong("recommendTime");
        long auditTime = jsonObject.getLong("auditTime");
        String updateTime = jsonObject.getString("updateTime");
        String thumbnail = jsonObject.getString("thumbnail");
        double price = jsonObject.getDouble("price");
        String url = jsonObject.getString("url");
        String sizeStr = jsonObject.getString("sizeStr");
        int size = jsonObject.getInt("size");
        String packageName = jsonObject.getString("packageName");
        String versionName = jsonObject.getString("versionName");
        int versionCode = jsonObject.getInt("versionCode");
        Group<Preview> previews = new GroupParser<Preview>(new PreviewParser(),
                "previews").parser(jsonObject);
        theme.set_id(_id);
        theme.setId(id);
        theme.setTitle(title);
        theme.setDescription(description);
        theme.setAuthor(author);
        theme.setCategory(category);
        theme.setTag(tag);
        theme.setDownloads(downloads);
        theme.setRecommendTime(recommendTime);
        theme.setAuditTime(auditTime);
        theme.setUpdateTime(updateTime);
        theme.setThumbnail(thumbnail);
        theme.setPrice(price);
        theme.setUrl(url);
        theme.setSizeStr(sizeStr);
        theme.setSize(size);
        theme.setPackageName(packageName);
        theme.setVersionName(versionName);
        theme.setVersionCode(versionCode);
        theme.setPreviews(previews);
        return theme;
    }

}
