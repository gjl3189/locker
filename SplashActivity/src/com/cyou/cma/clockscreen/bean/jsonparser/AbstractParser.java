package com.cyou.cma.clockscreen.bean.jsonparser;

import org.json.JSONException;
import org.json.JSONObject;

import com.cyou.cma.clockscreen.NoDataException;
import com.cyou.cma.clockscreen.bean.EntityType;
import com.cyou.cma.clockscreen.util.Util;

//TODO 改成Gson解析
public abstract class AbstractParser<T extends EntityType> implements Parser<T> {

    @Override
    public T parser(JSONObject jsonObject) throws Exception {
        // 首先判断是不是json数据的开始
        JSONObject data = null;
        Exception exception = null;
        try {
            data = jsonObject.getJSONObject("data");
        } catch (JSONException e) {
            // 不是文档的开头
            exception = e;
        }
        if (exception == null) {// 是json数据的开始 解析后台返回的code
            int code = -1;
            try {
                code = data.getInt("code");
            } catch (JSONException e) {
                Util.printException(e);
            }
            if (code == 100) {// 后台返回数据成功，开始解析数据
                return parserInner(data);
            } else if (code == 101) {
// throw new Exception("no data");
                throw new NoDataException();
            } else if (code == 102) {
                throw new Exception("param error");
            } else if (code == 103) {
                throw new Exception("application error");
            } else {
                throw new Exception("unkown error in parser");
            }
        } else {
            return parserInner(jsonObject);
        }

    }

    abstract T parserInner(JSONObject jsonObject) throws Exception;

    public static JSONObject createJsonObject(String json) throws JSONException {
        return new JSONObject(json);
    }

}
