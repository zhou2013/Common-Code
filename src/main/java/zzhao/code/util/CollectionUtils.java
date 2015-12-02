package zzhao.code.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author zzhao
 * @version 2015年12月2日
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection<?> input) {
        if (input == null || input.size() == 0) {
            return true;
        }
        return false;
    }

    public static String join(Collection<?> input, String sep) {
        if (input == null || input.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int index = 0;
        int size = input.size();
        Iterator<?> it = input.iterator();
        while (it.hasNext()) {
            if (index == size - 1) {
                break;
            }
            Object o = it.next();
            sb.append(o).append(sep);
            index++;
        }
        sb.append(it.next());
        return sb.toString();
    }

    /**
     * 将字符串split成list，排除split之后为空的字符串
     * 如果输入为空字符串，那么返回空list
     * 如果input中不包含sep，那么返回input本身
     * @param input
     * @param sep
     * @return
     */
    public static List<String> split(String input, String sep) {
        List<String> list = new LinkedList<String>();
        if (StringUtils.isEmpty(input)) {
            return list;
        } else if (StringUtils.isEmpty(sep)) {
            list.add(input);
            return list;
        }

        String[] splits = input.split(sep);
        for (String s : splits) {
            if (StringUtils.isNotEmpty(s)) {
                list.add(s);
            }
        }
        return list;
    }
}
