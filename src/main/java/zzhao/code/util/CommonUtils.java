package zzhao.code.util;

import org.apache.log4j.Logger;

/**
 * 一些通用的辅助函数
 * @author zzhao
 * @version 2015-9-9
 */
public class CommonUtils {
    private static final Logger logger = Logger.getLogger(CommonUtils.class);

    public static void sleepQuitely(long millis) {
        try {
            if (millis > 0) {
                Thread.sleep(millis);
            }
        } catch (Exception e) {
            logger.warn("sleep exception：" + e.getMessage(), e);
        }
    }

}
