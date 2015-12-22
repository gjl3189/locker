package com.cyou.cma.clockscreen.http;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cyou.cma.clockscreen.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolleyClient implements Client<Request<String>, String> {

    private static RequestQueue requestQueue;
    private static VolleyClient sVolleyClient = null;

    private VolleyClient(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
    }

    public synchronized static VolleyClient getInstance(Context context) {
        if (sVolleyClient == null) {
            sVolleyClient = new VolleyClient(context);
        }
        return sVolleyClient;
    }

    @Override
    public void executeRequest(Request<String> request) {
        requestQueue.add(request);
    }

    @Override
    public Request<String> makeHttpPost(String url, final List<NameValuePair> nameValuePairs,
            Listener<String> listener,
            ErrorListener errorListener) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if (nameValuePairs != null && nameValuePairs.size() != 0) {
                    Map<String, String> map = new HashMap<String, String>();
                    for (NameValuePair nameValuePair : nameValuePairs) {
                        map.put(nameValuePair.getName(), nameValuePair.getValue());
                    }
                    return map;
                } else {
                    return super.getParams();
                }

            }
        };
        return stringRequest;
    }

    @Override
    public Request<String> makeHttpGet(String url, List<NameValuePair> nameValuePairs,
            Listener<String> listener, ErrorListener errorListener) {

        String params = null;
        if (nameValuePairs != null && nameValuePairs.size() != 0)
            params = URLEncodedUtils.format(nameValuePairs, HTTP.UTF_8);
        if (params != null)
            url = url + "?" + params;
        Util.Logjb("url", "url -->" + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, errorListener);
        return stringRequest;

    }

    public void canclePendingRequest(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

}
