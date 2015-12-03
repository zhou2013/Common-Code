package zzhao.code.http;

import java.io.IOException;
import java.io.InputStream;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

/**
 *
 * @author zzhao
 * @version 2015年12月2日
 */
public class CharacterUtils {

    public static String detectEncode(InputStream inputStream) throws IOException {
        final EncodeResult result = new EncodeResult();
        nsDetector det = new nsDetector(nsDetector.ALL);
        nsICharsetDetectionObserver cdo = new nsICharsetDetectionObserver() {
            public void Notify(String charset) {
                result.found = true;
                result.encode = charset;
            }
        };
        det.Init(cdo);
        byte[] buf = new byte[1024];
        int len;
        boolean done = false;
        boolean isAscii = true;
        long total = 0;
        while ((len = inputStream.read(buf, 0, buf.length)) != -1) {
            // Check if the stream is only ascii.
            total = total + len;
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
