package com.cyou.cma.clockscreen.bean.jsonparser;

import com.cyou.cma.clockscreen.bean.EntityType;
import com.cyou.cma.clockscreen.bean.Group;

import org.json.JSONArray;
import org.json.JSONObject;

public class GroupParser<T extends EntityType> extends AbstractParser<Group<T>> {
    private Parser<T> mSubParser;
    private String nodeName;

    public GroupParser(Parser<T> parser, String nodeName) {
        this.mSubParser = parser;
        this.nodeName = nodeName;
    }

    @Override
    Group<T> parserInner(JSONObject jsonObject) throws Exception {
        Group<T> group = new Group<T>();
        JSONArray jsonArray = jsonObject.getJSONArray(nodeName);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            try {

                group.add(mSubParser.parser(object));
            } catch (Exception e) {
                continue;
            }
        }
        return group;
    }

}
