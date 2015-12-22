package com.cyou.cma.clockscreen.http;

import com.android.volley.Response.Listener;
import com.cyou.cma.clockscreen.bean.EntityType;
import com.cyou.cma.clockscreen.bean.jsonparser.AbstractParser;
import com.cyou.cma.clockscreen.bean.jsonparser.Parser;

public class ResponseListener<T extends EntityType> implements Listener<String> {

    private Parser<T> mParser;
    private ResultListener<T> resultListener;

    public ResponseListener(Parser<T> parser, ResultListener<T> resultListener) {
        this.mParser = parser;
        this.resultListener = resultListener;
    }

    @Override
    public void onResponse(String response) {
        if (response != null) {
            try {
                T result = mParser.parser(AbstractParser
                        .createJsonObject(response));
                resultListener.onPostExecute(result, null);
            } catch (Exception e) {
                this.resultListener.onPostExecute(null, new Exception());
            }
        } else {
            this.resultListener.onPostExecute(null, new Exception());
        }
    }

    public interface ResultListener<T extends EntityType> {
        public void onPostExecute(T result, Exception exception);
    }

}
