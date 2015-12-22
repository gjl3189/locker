package com.cyou.cma.clockscreen.bean.jsonparser;

import com.cyou.cma.clockscreen.bean.EmptyEntity;

import org.json.JSONObject;

public class EmptyEntityParser extends AbstractParser<EmptyEntity> {

    @Override
    EmptyEntity parserInner(JSONObject jsonObject) throws Exception {
        return new EmptyEntity();
    }

}
