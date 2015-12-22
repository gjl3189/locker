package com.cyou.cma.clockscreen.bean.jsonparser;

import org.json.JSONObject;

import com.cyou.cma.clockscreen.bean.Group;
import com.cyou.cma.clockscreen.bean.Preview;
import com.cyou.cma.clockscreen.bean.Theme4Play;

public class Theme4PlayParser extends AbstractParser<Theme4Play> {

    @Override
    Theme4Play parserInner(JSONObject jsonObject) throws Exception {

        Theme4Play theme4Play = new Theme4Play();
        theme4Play.id = jsonObject.getString("id");
        theme4Play.lockscreenThemeId = jsonObject.getLong("lockscreenThemeId");
        theme4Play.title = jsonObject.getString("title");
        theme4Play.description = jsonObject.getString("description");
        theme4Play.author = jsonObject.getString("author");
        theme4Play.thumbnail = jsonObject.getString("thumbnail");
        theme4Play.googlePlayUrl = jsonObject.getString("googlePlayUrl");
       try {
           theme4Play.youtubeUrl = jsonObject.getString("youtubeUrl");
    } catch (Exception e) {
    }
        theme4Play.size = jsonObject.getLong("size");
        theme4Play.sizeText = jsonObject.getString("sizeText");
        theme4Play.auditTime = jsonObject.getLong("auditTime");
        theme4Play.packageName = jsonObject.getString("packageName").trim();
        theme4Play.weight = jsonObject.getInt("weight");

        Group<Preview> previews = new GroupParser<Preview>(new PreviewParser(),
                "previews").parser(jsonObject);
        theme4Play.previews = previews;
        return theme4Play;

    }

}
