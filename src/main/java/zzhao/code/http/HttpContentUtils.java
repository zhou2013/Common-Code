package zzhao.code.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;

/**
 * http 内容扒取工具
 * 1. 包含contenttype 检查
 * 2. 包含非法类型自动断开链接
 * 3. 包含编码自动检查
 * @author zzhao
 * @version 2015年12月2日
 */
public class HttpContentUtils {
    private static final Logger logger = Logger.getLogger(HttpContentUtils.class);

    public static void main(String[] args) {
        String content = getHtmlContent("http://163.com");
        System.out.println(content);
    }

    public static String getHtmlContent(String url) {
        HttpClient httpClient = HttpClientUtils.getSingleHttpClient();
        HttpGet method = new HttpGet(url);
        String result = null;
        method.setHeader("Accept-Encoding", "gzip");
        method.setHeader("Connection", "close");

        try {
            HttpResponse response = httpClient.execute(method);
            if (response.getStatusLine().getStatusCode() != 200) {
                result = String.format("调用失败!status=%s", response.getStatusLine());
            } else {
                String contentType = response.getFirstHeader("Content-Type").getValue().toLowerCase();
                if (StringUtils.isNotBlank(contentType) && contentType.contains("text/html")) {
                    Charset cs = null;
                    if (contentType.contains("charset")) {
                        String[] strs = contentType.split(";");
                        for(String tmp : strs){
                            if(tmp.contains("charset")){
                                String[] strs2 = tmp.split("=");
                                String cset = strs2[1].trim();
                                cs = Charset.forName(cset);
                            }
                        }
                    }
                    result = getResponseBodyAsString(response, cs);
                } else {
                    result = "not valid html";
                }
            }
        } catch (Exception e) {
            logger.error("抓取出粗!", e);
        } finally {
            try {
                httpClient.getConnectionManager().shutdown();
            } catch (Exception e) {
                logger.error("关闭连接出错!", e);
            }
        }
        return result;
    }

    private static String getResponseBodyAsString(HttpResponse response, Charset cs) throws IOException {
        InputStream inputStream = response.getEntity().getContent();
        int i = (int) response.getEntity().getContentLength();
        if (i < 0) {
            i = inputStream.available();
        }
        Header header = response.getFirstHeader("Content-Encoding"); 
        if (header != null){
            String encoding = header.getValue().toLowerCase();
            if(encoding.contains("gzip")){
                inputStream = new GZIPInputStream(inputStream);
            }
        }

        if (cs == null) {
            int limit = 1024 * 1024 * 10; // 10M限制
            inputStream = new BufferedInputStream(inputStream, limit);
            inputStream.mark(limit);
            String chartset = CharacterUtils.detectEncode(inputStream);
            inputStream.reset();
            cs = Charset.forName(chartset);
        }

        Reader reader = new InputStreamReader(inputStream, cs);
        StringBuffer buffer = new StringBuffer();
        char[] tmp = new char[4096];
        int l;
        while ((l = reader.read(tmp)) != -1) {
            buffer.append(tmp, 0, l);
        }
        return buffer.toString();
    }
}
