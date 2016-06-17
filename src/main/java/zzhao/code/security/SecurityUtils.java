package zzhao.code.security;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author zzhao
 * @version 2016年6月15日
 */
public class SecurityUtils {

    public static String md5Hex(String content) {
        return DigestUtils.md5Hex(content.getBytes());
    }

    public static String base64Encode(String content) {
        return Base64.encodeBase64String(content.getBytes());
    }

    public static String base64Decode(String content) {
        return new String(Base64.decodeBase64(content));
    }

    public static String shaHex(String content) {
        return DigestUtils.shaHex(content);
    }
    
}
