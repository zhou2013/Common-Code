package zzhao.code.util;

import java.text.DecimalFormat;

/**
 *
 * @author zzhao
 * @version 2014-7-15
 */
public class ByteUtil {
    private static final char[] HEXDUMP_TABLE = new char[256 * 4];

    private static ThreadLocal<DecimalFormat> decimalFormate = new ThreadLocal<DecimalFormat>() {
        @Override
        protected synchronized DecimalFormat initialValue() {
            return new DecimalFormat("0.##");
        }
    };

    static {
        final char[] DIGITS = "0123456789abcdef".toCharArray();
        for (int i = 0; i < 256; i ++) {
            HEXDUMP_TABLE[ i << 1     ] = DIGITS[i >>> 4 & 0x0F];
            HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i       & 0x0F];
        }
    }

    public static String adjustBytes(long bytes) {
        int level = 0;
        double tmp = bytes;
        while (tmp > (10 * 1024)) {
            level++;
            tmp = tmp / 1024;
        }
        String units = "";
        switch (level) {
            case 0:
                units = "";
                break;
            case 1:
                units = "K";
                break;
            case 2:
                units = "M";
                break;
            case 3:
                units = "G";
                break;
            case 4:
                units = "T";
                break;
            default:
                break;
        }
        return decimalFormate.get().format(tmp) + units;
    }

    public static String adjustNumber(long number) {
        int level = 0;
        double tmp = number;
        while (tmp > (10 * 1000)) {
            level++;
            tmp = tmp / 1000;
        }
        String units = "";
        switch (level) {
            case 0:
                units = "";
                break;
            case 1:
                units = "K";
                break;
            case 2:
                units = "M";
                break;
            case 3:
                units = "B";
                break;
            default:
                break;
        }
        return decimalFormate.get().format(tmp) + units;
    }
}
