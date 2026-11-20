package com.smartwallet.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {

    /**
     * ✅ اللغة الموحّدة للتطبيق: عربي (مصر).
     * تُستخدم في كل تنسيقات الأرقام والتواريخ لضمان الاتساق.
     * ملاحظة: ar-EG يعرض الأرقام العربية-الهندية (٠١٢٣...)
     * إن أردت الأرقام اللاتينية (0123...) بدّلها إلى new Locale("ar")
     */
    public static final Locale AR_EG = new Locale("ar", "EG");

    // ✅ العملة الموحّدة: ج.م
    public static final String CURRENCY_SUFFIX = " ج.م";

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd/MM/yyyy", AR_EG);
    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("hh:mm a", AR_EG);
    private static final SimpleDateFormat DATE_TIME_FORMAT =
            new SimpleDateFormat("dd/MM/yyyy hh:mm a", AR_EG);
    private static final SimpleDateFormat MONTH_YEAR_FORMAT =
            new SimpleDateFormat("MMMM yyyy", AR_EG);
    private static final SimpleDateFormat DAY_NAME_FORMAT =
            new SimpleDateFormat("EEEE", AR_EG);

    // --- Currency ---

    public static String formatCurrency(double amount) {
        return formatNumber(amount) + CURRENCY_SUFFIX;
    }

    public static String formatCurrencyWithoutSuffix(double amount) {
        return formatNumber(amount);
    }

    /** تنسيق رقم عام بأرقام عربية متسقة */
    public static String formatNumber(double amount) {
        NumberFormat nf = NumberFormat.getNumberInstance(AR_EG);
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(2);
        return nf.format(amount);
    }

    // --- Dates ---

    public static String formatDate(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }

    public static String formatTime(long timestamp) {
        return TIME_FORMAT.format(new Date(timestamp));
    }

    public static String formatDateTime(long timestamp) {
        return DATE_TIME_FORMAT.format(new Date(timestamp));
    }

    public static String formatMonthYear(long timestamp) {
        return MONTH_YEAR_FORMAT.format(new Date(timestamp));
    }

    public static String formatDayName(long timestamp) {
        return DAY_NAME_FORMAT.format(new Date(timestamp));
    }

    public static String formatRelativeDate(long timestamp) {
        Calendar today = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timestamp);

        if (isSameDay(today, date)) return "اليوم";

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        if (isSameDay(yesterday, date)) return "أمس";

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        if (isSameDay(tomorrow, date)) return "غداً";

        return DATE_FORMAT.format(new Date(timestamp));
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static String formatPercentage(double value) {
        return String.format(AR_EG, "%.1f%%", value);
    }

    // --- Time boundaries ---

    public static long getStartOfDay(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static long getEndOfDay(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTimeInMillis();
    }

    public static long getStartOfWeek() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return getStartOfDay(c.getTimeInMillis());
    }

    public static long getEndOfWeek() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        c.add(Calendar.WEEK_OF_YEAR, 1);
        c.add(Calendar.MILLISECOND, -1);
        return c.getTimeInMillis();
    }

    public static long getStartOfMonth() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        return getStartOfDay(c.getTimeInMillis());
    }

    public static long getEndOfMonth() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return getEndOfDay(c.getTimeInMillis());
    }

    public static long getStartOfYear() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, 1);
        return getStartOfDay(c.getTimeInMillis());
    }

    public static long getEndOfYear() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DAY_OF_MONTH, 31);
        return getEndOfDay(c.getTimeInMillis());
    }

    public static int getDaysUntil(long timestamp) {
        return (int) ((timestamp - System.currentTimeMillis()) / (1000 * 60 * 60 * 24));
    }
}