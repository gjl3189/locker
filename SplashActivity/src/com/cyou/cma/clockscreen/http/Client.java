package com.cyou.cma.clockscreen.http;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

import org.apache.http.NameValuePair;

import java.util.List;

public interface Client<T,L> {
    public  T makeHttpPost(String url, List<NameValuePair> nameValuePairs, Listener<L> listener,
            ErrorListener errorListener);

    public  T makeHttpGet(String url, List<NameValuePair> nameValuePairs, Listener<L> listener,
            ErrorListener errorListener);

    public void executeRequest(T request);

}
