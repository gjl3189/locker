package com.cyou.cma.clockscreen.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.bean.HttpResponseBean;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.List;

public class HttpUtil {
    private final static int CONNECT_TIMEOUT = 10000;
    private final static int SO_TIMEOUT = 10000;
    private final static int RESPONSE_OK = 200;

    // errorCode
    private final static int ServiceError = 1;
    private final static int NetWorkError = 2;
    private final static int DataError = 3;

    public final static String NULL = "null";

    // 反馈接口
    public static final String URL_FEEDBACK = "http://receive.client.c-launcher.com/mobo/client/typefeedback/add.do";

    public static HttpResponseBean doPost(Context context, String url, List<NameValuePair> params) {
        HttpResponseBean responseBean = new HttpResponseBean();
        if (!isNetworkAvailable(context)) {
            responseBean.setSuccess(false);
            responseBean.setErrorCode(NetWorkError);
            responseBean.setErrorMsg(context.getString(R.string.http_no_network_error));
            return responseBean;
        }
        HttpPost httpRequest = new HttpPost(url);
        // List<NameValuePair> params = new ArrayList<NameValuePair>();
        try {
            HttpClient client = new DefaultHttpClient();
            // 请求超时
            client.getParams().setParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECT_TIMEOUT);
            // 读取超时
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                    SO_TIMEOUT);

            /* 添加请求参数到请求对象 */
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            /* 发送请求并等待响应 */
            HttpResponse httpResponse = client.execute(httpRequest);
            /* 若状态码为200 ok */
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                BufferedReader bufferedReader2 = new BufferedReader(
                        new InputStreamReader(httpResponse.getEntity().getContent()));
                StringBuilder builder = new StringBuilder();
                for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2
                        .readLine()) {
                    builder.append(s);
                }
                responseBean.setSuccess(true);
                responseBean.setResult(builder.toString());
            } else {
                responseBean.setSuccess(false);
                responseBean.setErrorCode(ServiceError);
                responseBean.setErrorMsg(context.getString(R.string.http_service_error));
            }

        } catch (UnknownHostException e) {
            Util.printException(e);
            responseBean.setSuccess(false);
            responseBean.setErrorCode(NetWorkError);
            responseBean.setErrorMsg(context.getString(R.string.http_network_error));
        } catch (IOException e) {
            Util.printException(e);
            responseBean.setSuccess(false);
            responseBean.setErrorCode(DataError);
            responseBean.setErrorMsg(context.getString(R.string.http_data_error));
        } catch (Exception e) {
            Util.printException(e);
            responseBean.setSuccess(false);
            responseBean.setErrorCode(DataError);
            responseBean.setErrorMsg("unknow error");
        }
        return responseBean;
    }

    public static HttpResponseBean httpGet(Context context, String url) {
        Util.Logcs("HttpUtil", "httpGet-->" + url);
        HttpResponseBean responseBean = new HttpResponseBean();
        if (!isNetworkAvailable(context)) {
            responseBean.setSuccess(false);
            responseBean.setErrorCode(NetWorkError);
            responseBean.setErrorMsg(context.getString(R.string.http_no_network_error));
            return responseBean;
        }
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams()
                .setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                        CONNECT_TIMEOUT);
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                SO_TIMEOUT);

        try {
            HttpResponse response = httpclient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == RESPONSE_OK) {
                responseBean.setSuccess(true);
                responseBean.setResult(EntityUtils.toString(response.getEntity()));
            } else {
                responseBean.setSuccess(false);
                responseBean.setErrorCode(ServiceError);
                responseBean.setErrorMsg(context.getString(R.string.http_service_error));
            }
        } catch (UnknownHostException e) {
            Util.printException(e);
            responseBean.setSuccess(false);
            responseBean.setErrorCode(NetWorkError);
            responseBean.setErrorMsg(context.getString(R.string.http_network_error));
        } catch (IOException e) {
            Util.printException(e);
            responseBean.setSuccess(false);
            responseBean.setErrorCode(DataError);
            responseBean.setErrorMsg(context.getString(R.string.http_data_error));
// responseBean.setErrorCode(NetWorkError);
// responseBean.setErrorMsg(context.getString(R.string.http_network_error));
        } catch (Exception e) {
            Util.printException(e);
            responseBean.setSuccess(false);
            responseBean.setErrorCode(DataError);
            responseBean.setErrorMsg("unknow error");
        }
        return responseBean;
    }

    /**
     * Judge network available
     * 
     * @param context
     * @return
     * @author yinang
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
