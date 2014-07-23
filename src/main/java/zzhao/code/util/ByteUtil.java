package zzhao.code.util;

/**
 *
 * @author zzhao
 * @version 2014-7-15
 */
public class ByteUtil {
    private static final char[] HEXDUMP_TABLE = new char[256 * 4];

    static {
        final char[] DIGITS = "0123456789abcdef".toCharArray();
        for (int i = 0; i < 256; i ++) {
            HEXDUMP_TABLE[ i << 1     ] = DIGITS[i >>> 4 & 0x0F];
            HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i       & 0x0F];
        }

        System.out.println(HEXDUMP_TABLE);
    }

    public static void main(String[] args) {
        new ByteUtil();
    }
}
