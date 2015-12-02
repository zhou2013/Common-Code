package zzhao.code.util;

/**
 *
 * @author zzhao
 * @version 2015年12月2日
 */
public class ClassUtils {

    /**
     * 检查是否是一些基础Long,Integer等类型
     * @param clz
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static boolean isWrapObject(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }
}
