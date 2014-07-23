package zzhao.code.util;

/**
 *
 * @author zzhao
 * @version 2014-7-15
 */
public class MathUtil {

    public static boolean isPowerOfTwo(int val) {
        return (val & -val) == val;
    }

}
