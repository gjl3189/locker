package com.cyou.cma.clockscreen;

import com.cyou.cma.clockscreen.bean.EntityType;
import com.cyou.cma.clockscreen.bean.jsonparser.AbstractParser;
import com.cyou.cma.clockscreen.bean.jsonparser.Parser;
import com.cyou.cma.clockscreen.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;
@Deprecated //不推荐使用改用VolleyClient实现  
public class AppClient {
    public static final String TAG = "AppClient";
    private static final int DEFAULT_SOCKET_TIMEOUT = 20 * 1000;
    private static final int DEFAULT_HOST_CONNECTIONS = 10;
    private static final int DEFAULT_MAX_CONNECTIONS = 20;
    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 1 * 1024;
    private static HttpClient mHttpClient;

    public AppClient() {
        getHttpClient();
    }

    private static synchronized HttpClient getHttpClient() {
        if (mHttpClient == null) {
            final HttpParams httpParams = new BasicHttpParams();

            // timeout: get connections from connection pool
            ConnManagerParams.setTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);
            // timeout: connect to the server
            HttpConnectionParams.setConnectionTimeout(httpParams,
                    DEFAULT_SOCKET_TIMEOUT);
            // timeout: transfer data from server
            HttpConnectionParams.setSoTimeout(httpParams,
                    DEFAULT_SOCKET_TIMEOUT);

            // set max connections per host
            ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
                    new ConnPerRouteBean(DEFAULT_HOST_CONNECTIONS));
            // set max total connections
            ConnManagerParams.setMaxTotalConnections(httpParams,
                    DEFAULT_MAX_CONNECTIONS);

            // use expect-continue handshake
            HttpProtocolParams.setUseExpectContinue(httpParams, true);
            // disable stale check
            HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);

            HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);

            HttpClientParams.setRedirecting(httpParams, false);

            // set user agent
            String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
            HttpProtocolParams.setUserAgent(httpParams, userAgent);

            // disable Nagle algorithm
            HttpConnectionParams.setTcpNoDelay(httpParams, true);

            HttpConnectionParams.setSocketBufferSize(httpParams,
                    DEFAULT_SOCKET_BUFFER_SIZE);

            // scheme: http and https
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            schemeRegistry.register(new Scheme("https", SSLSocketFactory
                    .getSocketFactory(), 443));

            ClientConnectionManager manager = new ThreadSafeClientConnManager(
                    httpParams, schemeRegistry);
            mHttpClient = new DefaultHttpClient(manager, httpParams);
        }

        return mHttpClient;
    }

    public HttpPost makeHttpPost(String url, List<NameValuePair> entityPair) {

        HttpPost httpPost = new HttpPost(url);
        UrlEncodedFormEntity urlEncodedFormEntity = null;
        try {
            urlEncodedFormEntity = new UrlEncodedFormEntity(entityPair);
        } catch (UnsupportedEncodingException e) {
            Util.Logjb("AppClient", "UnsupportedEncodingException " + e);
            return null;
        }
        httpPost.setEntity(urlEncodedFormEntity);
        return httpPost;
    }

    public HttpGet makeHttpGet(String url, List<NameValuePair> nameValuePairs) {
        String params = null;
        if (nameValuePairs != null && nameValuePairs.size() != 0)
            params = URLEncodedUtils.format(nameValuePairs, HTTP.UTF_8);
        if (params != null)
            url = url + "?" + params;
        HttpGet httpGet = new HttpGet(url);
        Util.Logjb(TAG, "makeHttpGet url------>" + httpGet.getURI());
        return httpGet;

    }

    public synchronized <T extends EntityType> T executeHttp(
            HttpRequestBase httpRequestBase, Parser<T> parser) throws Exception {
        try {
            mHttpClient.getConnectionManager().closeExpiredConnections();
            HttpResponse response = mHttpClient.execute(httpRequestBase);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                return parser.parser(AbstractParser
                        .createJsonObject(EntityUtils.toString(response
                                .getEntity())));

            } else {
                throw new Exception("response code is not 200");
            }
        } catch (Exception e) {
            throw e;
        } finally {

        }
    }
}
