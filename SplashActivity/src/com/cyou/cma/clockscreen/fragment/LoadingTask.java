
package com.cyou.cma.clockscreen.fragment;

import android.os.AsyncTask;

import com.cyou.cma.clockscreen.AppClient;
import com.cyou.cma.clockscreen.bean.EntityType;
import com.cyou.cma.clockscreen.bean.jsonparser.Parser;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * 跟网络相关的请求类
 * 
 * @author jiangbin
 * @param <T>
 */
public abstract class LoadingTask<T extends EntityType> extends AsyncTask<Void, Void, T> {
    protected AppClient mAppClient;
    protected Exception exception;

    public LoadingTask(AppClient appClient) {
        mAppClient = appClient;
    }

    @Override
    protected T doInBackground(Void... params) {

       
        try {
            return mAppClient.executeHttp(createHttpRequestBase(), createParser());
        } catch (Exception e) {
            exception = e;
            return null;
        }

    }

    public abstract HttpRequestBase createHttpRequestBase();

    public abstract Parser<T> createParser();
}
