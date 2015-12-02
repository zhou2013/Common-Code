package zzhao.code.http;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

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
    
    public static HttpClient getHttpClient(){
        return getHttpClient(CONNECT_TIMEOUT, SO_TIMEOUT, MAX_TOTAL_CONNECTION, MAX_PER_ROUTE);
    }

    public static HttpClient getHttpClient(int connectTimeOut, int soTimeOut, int maxTotal, int maxPerRoute) {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, connectTimeOut);
        HttpConnectionParams.setSoTimeout(httpParams, soTimeOut);
        PoolingClientConnectionManager connManager = new PoolingClientConnectionManager();
        connManager.setMaxTotal(maxTotal);
        connManager.setDefaultMaxPerRoute(maxPerRoute);
        HttpClient httpClient = new DefaultHttpClient(connManager, httpParams);
        return httpClient;
    }
}
