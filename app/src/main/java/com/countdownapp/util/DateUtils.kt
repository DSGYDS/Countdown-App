package com.countdownapp.util

import androidx.compose.ui.graphics.Color
import com.countdownapp.data.entity.Event
import java.util.*

fun generateEventColor(eventName: String): Color {
    val hash = eventName.hashCode()
    val hue = (hash and 0xFF) * 360f / 255f
    return Color.hsv(hue, 0.75f, 0.9f)
}

fun getDaysDescription(days: Long): String {
    return when {
        days > 0 -> "距离还有 $days 天"
        days < 0 -> "已过去 ${-days} 天"
        else -> "就是今天！"
    }
}

fun calculateDays(targetDate: Long, recurringMonth: Int = -1, recurringDay: Int = -1): Long {
    val today = getTodayStart()

    // Mode A: recurring event (no year) - calculate days to NEXT occurrence
    if (recurringMonth >= 0 && recurringDay >= 0) {
        val candidate = Calendar.getInstance().apply {
            set(Calendar.YEAR, today.get(Calendar.YEAR))
            set(Calendar.MONTH, recurringMonth)
            set(Calendar.DAY_OF_MONTH, recurringDay)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // If this year's date already passed, use next year
        if (candidate.timeInMillis < today.timeInMillis) {
            candidate.add(Calendar.YEAR, 1)
        }
        val diff = candidate.timeInMillis - today.timeInMillis
        return diff / (1000 * 60 * 60 * 24)
    }

    // Mode B: specific year event - calculate days from target to today
    // Can be negative (past) or positive (future)
    val diff = targetDate - today.timeInMillis
    return diff / (1000 * 60 * 60 * 24)
}

fun calculateEventDays(event: Event): Long {
    if (event.calendarType == "lunar" && event.isRepeatYearly && event.lunarMonth > 0 && event.lunarDay > 0) {
        return calculateLunarRecurringDays(event.lunarMonth, event.lunarDay)
    }
    return calculateDays(event.targetDate, event.recurringMonth, event.recurringDay)
}

private fun calculateLunarRecurringDays(lunarMonth: Int, lunarDay: Int): Long {
    val today = getTodayStart()
    val currentYear = today.get(Calendar.YEAR)

    for (year in currentYear..currentYear + 5) {
        val occurrence = lunarToSolarMillis(year, lunarMonth, lunarDay) ?: continue
        if (occurrence >= today.timeInMillis) {
            return (occurrence - today.timeInMillis) / (1000 * 60 * 60 * 24)
        }
    }

    val diff = (lunarToSolarMillis(currentYear, lunarMonth, lunarDay) ?: today.timeInMillis) - today.timeInMillis
    return diff / (1000 * 60 * 60 * 24)
}

private fun getTodayStart(): Calendar {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return today
}

fun isEventPassed(targetDate: Long): Boolean {
    return targetDate < System.currentTimeMillis()
}
