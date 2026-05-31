package com.countdownapp.util

import com.nlf.calendar.Lunar
import java.util.Calendar
import java.util.Date

fun lunarToSolarMillis(year: Int, month: Int, day: Int): Long? {
    return runCatching {
        val solar = Lunar.fromYmd(year, month, day).solar
        Calendar.getInstance().apply {
            set(Calendar.YEAR, solar.year)
            set(Calendar.MONTH, solar.month - 1)
            set(Calendar.DAY_OF_MONTH, solar.day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }.getOrNull()
}

fun lunarFromMillis(millis: Long): Lunar {
    return Lunar.fromDate(Date(millis))
}

fun formatLunarDate(year: Int, month: Int, day: Int, includeYear: Boolean = true): String {
    return runCatching {
        val lunar = Lunar.fromYmd(year, month, day)
        val yearText = if (includeYear) "${lunar.yearInChinese}年" else ""
        "农历$yearText${lunar.monthInChinese}月${lunar.dayInChinese}"
    }.getOrElse {
        val yearText = if (includeYear) "${year}年" else ""
        "农历$yearText${formatLunarMonthName(month)}${formatLunarDayName(day)}"
    }
}

fun formatLunarMonthName(month: Int): String {
    return listOf("正月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "冬月", "腊月")
        .getOrElse(month - 1) { "${month}月" }
}

fun formatLunarDayName(day: Int): String {
    return listOf(
        "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
        "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
        "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"
    ).getOrElse(day - 1) { day.toString() }
}
