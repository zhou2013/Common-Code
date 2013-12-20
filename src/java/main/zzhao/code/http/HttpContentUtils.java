package zzhao.code.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

/**
 * 网络爬虫工具， 主要包含以下3个特点:
 * 1. 支持gzip的压缩包
 * 2. 编码自动识别
 * 3. 排除除了html以外的其他内容，并遇到下载时会自动断开链接
 * 
 * @author zzhao
 * @version 2013-12-11
 */
public class HttpContentUtils {
    private static final Logger logger = Logger.getLogger(HttpContentUtils.class);
    
    public static void main(String[] args) {
        String content = getHtmlContent("http://www.sanodor.nl/logs/vip.163Signon.htm");
        System.out.println(content);
    }

    public static String getHtmlContent(String url) {
        SimpleHttpConnectionManager httpConnectionManager = new SimpleHttpConnectionManager();
        httpConnectionManager.getParams().setConnectionTimeout(5000);
        httpConnectionManager.getParams().setSoTimeout(15000);
        HttpClient httpClient = new HttpClient(httpConnectionManager);
        GetMethod method = new GetMethod(url);
        String result = null;
        method.addRequestHeader("Accept-Encoding", "gzip");
        method.setRequestHeader("Connection", "close");
        try {
            int status = httpClient.executeMethod(method);
            if (status != 200) {
                String response = method.getResponseBodyAsString(2048);
                result = String.format("调用失败!status=%s, response=%s", status, response);
            } else {
                String contentType = method.getResponseHeader("Content-Type").getValue().toLowerCase();
                if (StringUtils.isNotBlank(contentType) && contentType.contains("text/html")) {
                    result = getResponseBodyAsString(method);
                } else {
                    result = "not valid html";
                }
            }
        } catch (Exception e) {
            logger.error("抓取出粗!", e);
        } finally {
            try {
                ((SimpleHttpConnectionManager) httpClient.getHttpConnectionManager()).shutdown();
            } catch (Exception e) {
                logger.error("关闭连接出错!", e);
            }
        }
        return result;
    }

    private static String getResponseBodyAsString(GetMethod method) throws IOException {
        InputStream inputStream = getResponseBodyAsStream(method);
        if (inputStream != null) {
            String chartset = detectEncode(inputStream);
            // 前面那个stream已经被读过了,必须重新构造
            InputStreamReader isr = new InputStreamReader(getResponseBodyAsStream(method), chartset);
            java.io.BufferedReader br = new java.io.BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            String tempbf;
            while ((tempbf = br.readLine()) != null) {
                sb.append(tempbf);
                sb.append("\r\n");
            }
            isr.close();
            inputStream.close();
            return sb.toString();
        }
        return null;
    }
    
    private static InputStream getResponseBodyAsStream(HttpMethodBase method) throws IOException {
        byte[] responseBody = method.getResponseBody();
        InputStream inputStream = null;
        if (responseBody != null) {
            inputStream = new ByteArrayInputStream(responseBody);
            if (method.getResponseHeader("Content-Encoding") != null
                            && method.getResponseHeader("Content-Encoding").getValue().toLowerCase().indexOf("gzip") > -1) {
                // For GZip response
                inputStream = new GZIPInputStream(inputStream);
            }
        }
        return inputStream;
    }

    private static String detectEncode(InputStream inputStream) throws IOException {
        final EncodeResult result = new EncodeResult();
        nsDetector det = new nsDetector(nsDetector.ALL);
        nsICharsetDetectionObserver cdo = new nsICharsetDetectionObserver() {
            public void Notify(String charset) {
                result.found = true;
                result.encode = charset;
            }
        };
        det.Init(cdo);
        BufferedInputStream imp = new BufferedInputStream(inputStream);
        byte[] buf = new byte[1024];
        int len;
        boolean done = false;
        boolean isAscii = true;
        while ((len = imp.read(buf, 0, buf.length)) != -1) {
            // Check if the stream is only ascii.
            if (isAscii)
                isAscii = det.isAscii(buf, len);
            // DoIt if non-ascii and not done yet.
            if (!isAscii && !done)
                done = det.DoIt(buf, len, false);
        }
        det.DataEnd();

        if (isAscii) {
            result.encode = "ASCII";
            result.found = true;
        }

        if (!result.found) {
            String prob[] = det.getProbableCharsets();
            if (prob.length > 0) {
                // 在没有发现情况下，则取第一个可能的编码
                result.encode = prob[0];
            } else {
                return null;
            }
        }
        return result.encode;
    }
    
    private static class EncodeResult {
        private boolean found = false;
        private String encode = null;
    }
}
