package zzhao.code.http;

import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import zzhao.code.http.extend.HttpClientConnectionManager;
import zzhao.code.http.extend.HttpClientPoolConnectionManager;

/**
 *
 * @author zzhao
 * @version 2015年12月2日
 */
public class HttpClientUtils {

    private static final int CONNECT_TIMEOUT = 20000;
    private static final int SO_TIMEOUT = 20000;
    private static final int MAX_TOTAL_CONNECTION = 20;
    private static final int MAX_PER_ROUTE = 5;

    public static HttpClient getHttpClient() {
        return getHttpClient(CONNECT_TIMEOUT, SO_TIMEOUT, MAX_TOTAL_CONNECTION, MAX_PER_ROUTE, null);
    }

    public static HttpClient getClientWithHosts(Map<String, String> hosts) {
        return getHttpClient(CONNECT_TIMEOUT, SO_TIMEOUT, MAX_TOTAL_CONNECTION, MAX_PER_ROUTE, hosts);
    }

    public static HttpClient getHttpClient(int connectTimeOut, int soTimeOut, int maxTotal, int maxPerRoute) {
        return getHttpClient(connectTimeOut, soTimeOut, maxTotal, maxPerRoute, null);
    }

    public static HttpClient getHttpClient(int connectTimeOut, int soTimeOut, int maxTotal, int maxPerRoute,
                    Map<String, String> hosts) {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, connectTimeOut);
        HttpConnectionParams.setSoTimeout(httpParams, soTimeOut);
        PoolingClientConnectionManager connManager;
        if (hosts == null) {
            connManager = new HttpClientPoolConnectionManager();
        } else {
            connManager = new HttpClientPoolConnectionManager(SchemeRegistryFactory.createDefault(),
                            new SpecialDNSResolver(hosts));
        }
        connManager.setMaxTotal(maxTotal);
        connManager.setDefaultMaxPerRoute(maxPerRoute);
        HttpClient httpClient = new DefaultHttpClient(connManager, httpParams);
        return httpClient;
    }

    public static HttpClient getSingleHttpClient() {
        return getSingleHttpClient(CONNECT_TIMEOUT, SO_TIMEOUT);
    }

    public static HttpClient getSingleHttpClient(int connectTimeOut, int soTimeOut) {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, connectTimeOut);
        HttpConnectionParams.setSoTimeout(httpParams, soTimeOut);
        BasicClientConnectionManager httpConnectionManager = new HttpClientConnectionManager();
        HttpClient httpClient = new DefaultHttpClient(httpConnectionManager);
        return httpClient;
    }
}
