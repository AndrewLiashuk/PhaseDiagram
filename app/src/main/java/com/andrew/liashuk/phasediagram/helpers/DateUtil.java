package com.andrew.liashuk.phasediagram.helpers;

import android.content.Context;

import com.andrew.liashuk.phasediagram.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

    private static Locale locale = Locale.US;
    private static Calendar cal = Calendar.getInstance();

    static int SECOND_MILLISECONDS = 1000;
    static long MINUTE_SECONDS = 60;
    static long HOUR_SECONDS = MINUTE_SECONDS * 60;
    static long DAY_SECONDS = HOUR_SECONDS * 24;

    static SimpleDateFormat dateFormatDayMonthTime = new SimpleDateFormat("d MMM HH:mm", locale);
    static SimpleDateFormat dateFormatShortDayFirst = new SimpleDateFormat("d MMM yyyy", locale);
    public static SimpleDateFormat dateFormatRelativeDay = new SimpleDateFormat("HH:mm", locale);


    public static String relativeDate(Context context, long milliseconds) {
        if (context == null) {
            return "";
        }
        String suffixMinute = context.getString(R.string.date_minute);
        Calendar calNow = Calendar.getInstance();
        cal.setTimeInMillis(milliseconds);
        Date then = cal.getTime();
        long diff = (calNow.getTimeInMillis() - then.getTime()) / 1000;
        if (diff < HOUR_SECONDS) {
            //within last hour
            int mins = (int) (diff / 60);
            return mins < 2 ? suffixMinute : context.getString(R.string.date_minutes, mins);
        } else if (diff < DAY_SECONDS * 3) {
            long daysDiff = daysBetweenDates(calNow.getTimeInMillis(), cal.getTimeInMillis());
            if (daysDiff == 0) {
                //same day
                return dateFormatRelativeDay.format(then);
            }
            if (daysDiff == 1) {
                //yesterday
                return context.getString(R.string.date_yesterday, dateFormatRelativeDay.format(then));
            }
        }
        return dateFormatShortDayFirst.format(then);
    }

    public static String dayMonthTime(long milliseconds) {
        return dateFormatDayMonthTime.format(new Date(milliseconds));
    }

    public static String shortDateDayFirst(long milliseconds) {
        return dateFormatShortDayFirst.format(new Date(milliseconds));
    }

    private static long daysBetweenDates(long now, long then) {
        Calendar calNow = Calendar.getInstance();
        calNow.setTimeInMillis(now);
        calNow.set(Calendar.HOUR_OF_DAY, 0);
        calNow.set(Calendar.MINUTE, 0);
        calNow.set(Calendar.SECOND, 0);
        calNow.set(Calendar.MILLISECOND, 0);

        Calendar calThen = Calendar.getInstance();
        calThen.setTimeInMillis(then);
        calThen.set(Calendar.HOUR_OF_DAY, 0);
        calThen.set(Calendar.MINUTE, 0);
        calThen.set(Calendar.SECOND, 0);
        calThen.set(Calendar.MILLISECOND, 0);

        return (calNow.getTimeInMillis() - calThen.getTimeInMillis()) / (DAY_SECONDS * 1000);
    }

    private static long daysBetweenDatesCeil(long now, long then) {
        return (long) Math.ceil((now - then) / ((double) 1000 * DAY_SECONDS));
    }

    public static String dayRangeCeil(Context context, long now, long then) {
        long days = daysBetweenDatesCeil(now, then);
        if (context == null || days <= 0) {
            return "";
        }
        if (days == 1) {
            return context.getString(R.string.date_day);
        }
        return context.getString(R.string.date_days, days);
    }

    public static boolean isWithinCurrentMonth(long milliseconds) {
        Calendar calendarNow = Calendar.getInstance();
        Calendar calendarThen = Calendar.getInstance();
        calendarNow.setTime(new Date());
        Date dateThen = new Date();
        dateThen.setTime(milliseconds);
        calendarThen.setTime(dateThen);
        return calendarNow.get(Calendar.YEAR) == calendarThen.get(Calendar.YEAR) &&
                calendarNow.get(Calendar.MONTH) == calendarThen.get(Calendar.MONTH);
    }

    public static int getOffsetSecondsFromGmt(TimeZone timeZone, long localDateTimeMillis) {
        if (timeZone == null  || localDateTimeMillis <= 0) {
            return 0;
        }
        int offsetFromGmt = timeZone.getOffset(localDateTimeMillis);
        return offsetFromGmt / SECOND_MILLISECONDS;
    }
}
