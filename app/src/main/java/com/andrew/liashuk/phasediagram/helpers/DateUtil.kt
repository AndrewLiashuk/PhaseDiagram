package com.andrew.liashuk.phasediagram.helpers

import com.andrew.liashuk.phasediagram.R
import java.lang.Math.ceil
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtil {

    val dateFormatDayMonthTime = SimpleDateFormat("d MMM HH:mm", Locale.US)
    val dateFormatShortDayFirst = SimpleDateFormat("d MMM yyyy", Locale.US)
    val dateFormatRelativeDay = SimpleDateFormat("HH:mm", Locale.US)

    @JvmStatic
    fun relativeDate(resourceResolver: ResourceResolver, milliseconds: Long): String {
        val calNow = Calendar.getInstance()
        val cal = Calendar.getInstance().apply { timeInMillis = milliseconds  }

        val date = cal.time
        val diffInMillis = calNow.timeInMillis - cal.timeInMillis
        if (TimeUnit.MILLISECONDS.toHours(diffInMillis) < 1) {
            //within last hour
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
            return if (minutes < 2) {
                resourceResolver.getString(R.string.date_minute)
            } else {
                resourceResolver.getString(R.string.date_minutes, minutes)
            }
        } else if (TimeUnit.MILLISECONDS.toDays(diffInMillis) < 3) {
            val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
            if (days == 0L) {
                //same day
                return dateFormatRelativeDay.format(date)
            } else if (days == 1L) {
                //yesterday
                return resourceResolver.getString(R.string.date_yesterday, dateFormatRelativeDay.format(date))
            }
        }
        return dateFormatShortDayFirst.format(date)
    }

    fun dayMonthTime(milliseconds: Long): String {
        return dateFormatDayMonthTime.format(Date(milliseconds))
    }

    fun shortDateDayFirst(milliseconds: Long): String {
        return dateFormatShortDayFirst.format(Date(milliseconds))
    }

    private fun daysBetweenDatesCeil(now: Long, then: Long): Long {
        return ceil((now - then) / (TimeUnit.DAYS.toMillis(1).toDouble())).toLong()
    }

    fun dayRangeCeil(resourceResolver: ResourceResolver, now: Long, then: Long): String {
        val days = daysBetweenDatesCeil(now, then)

        return when {
            days <= 0 -> ""

            days == 1L -> resourceResolver.getString(R.string.date_day)

            else -> resourceResolver.getString(R.string.date_days, days)
        }
    }

    fun isWithinCurrentMonth(milliseconds: Long): Boolean {
        val calendarNow = Calendar.getInstance().apply { time = Date() }
        val calendarThen = Calendar.getInstance().apply {
            time = Date().apply { time = milliseconds }
        }

        return calendarNow[Calendar.YEAR] == calendarThen[Calendar.YEAR] &&
                calendarNow[Calendar.MONTH] == calendarThen[Calendar.MONTH]
    }

    fun getOffsetSecondsFromGmt(timeZone: TimeZone?, localDateTimeMillis: Long): Long {
        if (timeZone == null || localDateTimeMillis <= 0) {
            return 0
        }
        val offsetFromGmt = timeZone.getOffset(localDateTimeMillis)
        return TimeUnit.MILLISECONDS.toSeconds(offsetFromGmt.toLong())
    }
}