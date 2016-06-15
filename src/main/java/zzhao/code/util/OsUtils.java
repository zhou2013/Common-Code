package zzhao.code.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author zzhao
 * @version 2016年6月15日
 */
public class OsUtils {

    /**
     * 在linux下,获取本机的hostName
     * @return
     */
    public static String getHostNameForLiunx() {
        try {
            return (InetAddress.getLocalHost()).getCanonicalHostName();
        } catch (UnknownHostException uhe) {
            String host = uhe.getMessage();
            if (host != null) {
                int colon = host.indexOf(':');
                if (colon > 0) {
                    return host.substring(0, colon);
                }
            }
            return "UnknownHost";
        }
    }
}
