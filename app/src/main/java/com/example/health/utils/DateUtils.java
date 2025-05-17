// DateUtils.java
package com.example.health.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy", Locale.getDefault());
    private static final SimpleDateFormat WEEK_FORMAT = new SimpleDateFormat("yyyy-MM-dd EEEE", Locale.getDefault());

    // 获取时间戳对应的小时数
    public static float getHourFromTimestamp(String timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(timestamp));
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    // 周粒度：从Date获取周内天数 (周日=0)
    public static float getDayOfWeekFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) - 1; // 转换为0-6
    }

    // 月粒度：从日期字符串(yyyy-MM-dd)获取天数
    public static float getDayOfMonthFromDateString(Date dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(String.valueOf(dateStr));
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            return 0f;
        }
    }

    // 年粒度：从月份字符串(yyyy-MM)获取月份数字
    public static float getMonthNumberFromString(String monthStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            Date date = sdf.parse(monthStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.MONTH) + 1; // 转换为1-12
        } catch (ParseException e) {
            return 0f;
        }
    }

    // 日期范围格式化
    public static String formatDayRange(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("M月d日 EEEE", Locale.CHINA);
        return sdf.format(date);
    }

    public static String formatWeekRange(Date date) {
        SimpleDateFormat startFormat = new SimpleDateFormat("M月d日", Locale.CHINA);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(date);
        endCal.add(Calendar.DATE, 6);
        return startFormat.format(date) + " - " + startFormat.format(endCal.getTime());
    }

    public static String formatMonthRange(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月", Locale.CHINA);
        return sdf.format(date);
    }

    public static String formatYearRange(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年", Locale.CHINA);
        return sdf.format(date);
    }

    public static String formatDate(Date date) {
        return date != null ? DATE_FORMAT.format(date) : "";
    }

    public static String formatDateTime(Date date) {
        return date != null ? DATE_TIME_FORMAT.format(date) : "";
    }

    public static String formatMonth(Date date) {
        return date != null ? MONTH_FORMAT.format(date) : "";
    }

    public static String formatYear(Date date) {
        return date != null ? YEAR_FORMAT.format(date) : "";
    }

    public static String formatWeek(Date date) {
        return date != null ? WEEK_FORMAT.format(date) : "";
    }

    public static String formatDuration(Integer minutes) {
        if (minutes == null) return "0分钟";
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format(Locale.getDefault(), "%d小时%d分钟", hours, mins);
    }

    public static String getWeekday(Date date) {
        String[] weekdays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek < 0) dayOfWeek = 0;
        return weekdays[dayOfWeek];
    }

    public static int getChineseWeekday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SUNDAY ? 7 : dayOfWeek - 1;
    }

    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date getStartOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return getStartOfDay(calendar.getTime());
    }

    public static Date getEndOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + 6);
        return getEndOfDay(calendar.getTime());
    }

    public static Date getStartOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return getStartOfDay(calendar.getTime());
    }

    public static Date getEndOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return getEndOfDay(calendar.getTime());
    }

    public static Date getStartOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return getStartOfDay(calendar.getTime());
    }

    public static Date getEndOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        return getEndOfDay(calendar.getTime());
    }

    public static List<Date> generateDateRange(Date startDate, Date endDate) {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (!calendar.getTime().after(endDate)) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }

        return dates;
    }

    public static Date parseDateString(String dateStr) {
        try {
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static Date parseMonthString(String monthStr) {
        try {
            return MONTH_FORMAT.parse(monthStr);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static int daysBetween(Date start, Date end) {
        long diff = end.getTime() - start.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    public static int monthsBetween(Date start, Date end) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);

        int yearDiff = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR);
        int monthDiff = endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH);
        return yearDiff * 12 + monthDiff;
    }

    public static String formatDate(Date date, String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(date);
    }
}