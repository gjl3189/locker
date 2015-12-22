package com.cyou.cma.clockscreen.http;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.cyou.cma.clockscreen.bean.EntityType;
import com.cyou.cma.clockscreen.bean.jsonparser.Parser;

/**
 * 所有用volley的界面都需要实现此接口
 * 
 * @author jiangbin
 */
public interface Requestable<T, U, V extends EntityType> {
    public Request<T> createRequest(Listener<U> listener, ErrorListener errorListener);

    public Parser<V> createParser();
}
