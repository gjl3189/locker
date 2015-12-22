package com.cyou.cma.clockscreen.bean.jsonparser;

import com.cyou.cma.clockscreen.bean.Preview;

import org.json.JSONObject;

public class PreviewParser extends AbstractParser<Preview> {

    @Override
    Preview parserInner(JSONObject jsonObject) throws Exception {
        Preview preview = new Preview();
        String url = jsonObject.getString("url");
        preview.setUrl(url);
        return preview;
    }
}
