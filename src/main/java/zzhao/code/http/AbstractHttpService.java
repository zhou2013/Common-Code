package zzhao.code.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

/**
 *
 * @author zzhao
 * @version 2015年11月25日
 */
public class AbstractHttpService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractHttpService.class);

    protected HttpClient client;

    public AbstractHttpService(HttpClient client) {
        if (client != null) {
            client = HttpClientUtils.getHttpClient();
        }
        this.client = client;
    }

    public AbstractHttpService() {
        this(null);
    }

    protected String delete(String uri) {
        try {
            HttpDelete delete = new HttpDelete(uri);
            return executeMethod(delete);
        } catch (Throwable e) {
            logger.error("failed to delete " + uri, e);
        }
        return null;

    }

    protected String post(String uri, Map<String, String> params) {
        try {
            HttpPost post = new HttpPost(uri);
            HttpEntity entity = getHttpEntity(params);
            post.setEntity(entity);
            return executeMethod(post);
        } catch (Throwable e) {
            logger.error("failed to post " + uri, e);
        }
        return null;
    }

    protected String postJSON(String uri, Gson params) {
        try {
            HttpPost post = new HttpPost(uri);
            StringEntity entity = new StringEntity(params.toString(), "utf-8");// 解决中文乱码问题
            entity.setContentType("application/json");
            post.setEntity(entity);
            return executeMethod(post);
        } catch (Throwable e) {
            logger.error("failed to post " + uri, e);
        }
        return null;
    }

    protected String putJSON(String uri, Gson params) {
        try {
            HttpPut put = new HttpPut(uri);
            StringEntity entity = new StringEntity(params.toString(), "utf-8");// 解决中文乱码问题
            entity.setContentType("application/json");
            put.setEntity(entity);
            return executeMethod(put);
        } catch (Throwable e) {
            logger.error("failed to put " + uri, e);
        }
        return null;
    }

    protected String get(String uri, Map<String, String> paramsMap) {
        try {
            if (paramsMap != null && paramsMap.size() > 0) {
                uri = uri + "?" + buildGetReqParam(paramsMap);
            }
            HttpGet get = new HttpGet(uri);
            get.setHeader(HttpHeaders.CONTENT_ENCODING, "UTF-8");
            return executeMethod(get);
        } catch (Throwable e) {
            logger.error("failed to get " + uri, e);
        }
        return null;
    }

    protected String executeMethod(final HttpUriRequest request) throws Exception {
        prepareRequest(request);
        return client.execute(request, new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    String msg = EntityUtils.toString(response.getEntity());
                    processError(request.getURI().toString(), response.getStatusLine().getStatusCode(), msg);
                    return null;
                }
                return EntityUtils.toString(response.getEntity());
            }
        });
    }

    protected void prepareRequest(final HttpUriRequest request) {

    }

    protected void processError(String uri, int error, String errorMsg) {
        String log = String.format("failed to executer[%s], ret=[%s], msg=[%s]", uri, error, errorMsg);
        logger.info(log);
    }

    private static String buildGetReqParam(Map<String, String> param) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
            sb.append("&");
        }
        if (sb.length() > 0)
            return sb.substring(0, sb.length() - 1);
        return "";
    }

    private static HttpEntity getHttpEntity(Map<String, String> paramsMap) throws UnsupportedEncodingException {
        List<NameValuePair> nvps = Lists.newLinkedList();
        for (Entry<String, String> entry : paramsMap.entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return new UrlEncodedFormEntity(nvps);
    }
}
