package zzhao.code.util;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author zzhao
 * @version 2015年12月2日
 */
public class IpUtils {

    /**
     * 将ip转换成long
     * @param ip
     * @return
     */
    public static long ipToLong(String ip) {
        long ipLongValue = 0;

        String[] ipBlockStr = ip.split("\\.");

        long[] ipBlocks = new long[4];
        for (int i = 0; i < 4; i++) {
            try {
                ipBlocks[i] = Integer.parseInt(ipBlockStr[i]);
            } catch (Exception e) {
                ipBlocks[i] = 0;
            }
        }

        ipLongValue += ipBlocks[0] << 24;
        ipLongValue += ipBlocks[1] << 16;
        ipLongValue += ipBlocks[2] << 8;
        ipLongValue += ipBlocks[3];

        return ipLongValue;
    }

    /**
     * long转化成ip
     * @param ip
     * @return
     */
    public static String longToIp(long ip) {
        StringBuilder sb = new StringBuilder();
        sb.append((ip >> 24) & 0xff).append(".");
        sb.append((ip >> 16) & 0xff).append(".");
        sb.append((ip >> 8) & 0xff).append(".");
        sb.append(ip & 0xff);
        return sb.toString();
    }

    /**
     * 判断是否是内网ip
     * @param ip
     * @return
     */
    public static boolean isInternalIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            // 空白认为是内网ip
            return true;
        }

        if (ip.startsWith("10.") || ip.startsWith("192.168.")) {
            return true;
        }

        long iplong = ipToLong(ip);
        // 172.16.0.0 ~ 172.31.255.255
        if (iplong >= 2886729728L && iplong <= 2887778303L) {
            return true;
        }

        return false;
    }

    /**
     * 将ip格式标准话
     * @param ip
     * @return
     */
    public static String formateIp(String ip) {
        long value = ipToLong(ip);
        return longToIp(value);
    }

    /**
     * 判断是否是合法的ip或者ip段
     * @param value
     * @return
     */
    public static boolean isValidIpOrIpsegment(String value) {
        if (value.indexOf('/') > 0) {
            return isValidIpSegmane(value);
        } else {
            return isValidIpAddress(value);
        }
    }

    /**
     * 判断是否是合法的ip段
     * @param value
     * @return
     */
    public static boolean isValidIpSegmane(String value) {
        int index = value.indexOf('/');
        String ip = value.substring(0, index);
        String segment = value.substring(index + 1);
        try {
            int seg = Integer.parseInt(segment);
            if (seg < 0 || seg > 32) {
                return false;
            }
            if (!isValidIpAddress(ip)) {
                return false;
            }
            long ipValue = ipToLong(ip);
            long tmp = (0xffffffffL >> seg) & ipValue;
            if (tmp == 0) {
                return true;
            }
        } catch (Exception e) {

        }

        return false;
    }

    /**
     * 判断是否为合法IP
     * @param ip
     * @return
     */
    public static boolean isValidIpAddress(String ip) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }

        String[] tmps = StringUtils.split(ip, '.');
        if (tmps.length != 4) {
            return false;
        }

        try {
            for (String tmp : tmps) {
                if (tmp.length() > 3) {
                    return false;
                }
                int value = Integer.parseInt(tmp);
                if (value < 0 || value > 255) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
