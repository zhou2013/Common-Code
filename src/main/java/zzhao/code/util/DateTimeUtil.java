package zzhao.code.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
*
* @author zzhao
* @version 2015年12月2日
*/
public class DateTimeUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtil.class);

    public final static long MS_PER_DAY = 86400000L;
    public final static long MS_PER_HOUR = 3600000L;
    public final static long MS_PER_SECOND = 1000L;
    public final static long MS_PER_MINUTE = 60000L;

    public final static long SECOND_PER_DAY = 86400L;
    public final static long SECOND_PER_HOUR = 3600L;
    public final static long SECOND_PER_MINUTE = 60L;

    /**
     * 返回long类型yyyy-MM-dd HH:mm:ss格式时间
     * @param strDate yyyy-MM-dd HH:mm:ss格式时间
     * @return
     */
    public static long getTimeFromStrDate(String strDate) {
        long time = 0L;
        if (StringUtils.isEmpty(strDate)) {
            return time;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            time = simpleDateFormat.parse(strDate).getTime();
        } catch (ParseException e) {
            logger.error("input=" + strDate, e);
        }
        return time;
    }

    /**
     * 获取自定义格式字符串时间
     * @param strDate
     * @param format
     * @return
     */
    public static long getTimeFromStrDate(String strDate, String format) {
        long time = 0L;
        if (StringUtils.isEmpty(strDate)) {
            return time;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            time = simpleDateFormat.parse(strDate).getTime();
        } catch (ParseException e) {
            logger.error("input=" + strDate, e);
        }
        return time;
    }

    /**
     * 返回字符串时间
     * @param time 
     * @param dateFormat
     * @return
     */
    public static String getTimeFromLongDate(long time, String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(time);
    }

    /**
     * 返回字符串yyyy-MM-dd HH:mm:ss格式时间
     * @param time
     * @return
     */
    public static String getTimeFromLongDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(time);
    }

    /**
     * 对应这种格式的时间：yyyymmddhhmmss
     * @param time
     * @return
     */
    public static String getStrTimeFromLongDate(long time) {
        return getTimeFromLongDate(time, "yyyyMMddHHmmss");
    }

    /**
     * 把yyyymmddhhmmss这种格式的时间转化成long型时间
     * @param time
     * @return
     */
    public static long getLongDateFromStrTime(String time) {
        return DateTimeUtil.getTimeFromStrDate(time, "yyyyMMddHHmmss");
    }

    /**
     * 获取一天的开始时间 例如：2013-07-03 00:00:00
     * @return
     */
    public static long getTodayBeginTime() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MILLISECOND, 0);
        return currentDate.getTimeInMillis();
    }

    public static long now() {
        return System.currentTimeMillis();
    }

    public static long elapse(long start) {
        return DateTimeUtil.now() - start;
    }

    public static String formatDate(long time, String format) {
        if (time <= 0) {
            return "N/A";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            String dateStr = sdf.format(new Date(time));
            return dateStr;
        }
    }

    public static String format(long time) {
        return formatDate(time, "yyyy-MM-dd HH:mm:ss");
    }

    public static int getMonth(Date start, Date end) {
        if (start.after(end)) {
            Date t = start;
            start = end;
            end = t;
        }
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(start);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(end);
        Calendar temp = Calendar.getInstance();
        temp.setTime(end);
        temp.add(Calendar.DATE, 1);

        int year = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int month = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

        if ((startCalendar.get(Calendar.DATE) == 1) && (temp.get(Calendar.DATE) == 1)) {
            return year * 12 + month + 1;
        } else if ((startCalendar.get(Calendar.DATE) != 1) && (temp.get(Calendar.DATE) == 1)) {
            return year * 12 + month;
        } else if ((startCalendar.get(Calendar.DATE) == 1) && (temp.get(Calendar.DATE) != 1)) {
            return year * 12 + month;
        } else {
            return (year * 12 + month - 1) < 0 ? 0 : (year * 12 + month);
        }
    }
}
