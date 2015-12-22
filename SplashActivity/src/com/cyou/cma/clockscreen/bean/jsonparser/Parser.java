package com.cyou.cma.clockscreen.bean.jsonparser;

import com.cyou.cma.clockscreen.bean.EntityType;

import org.json.JSONObject;

public interface Parser<T extends EntityType> {
    T parser(JSONObject jsonObject) throws Exception;
}
