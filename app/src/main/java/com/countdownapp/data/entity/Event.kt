package com.countdownapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val targetDate: Long,
    val backgroundImagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    // For recurring events (no year): month (0-11) and day (1-31)
    val recurringMonth: Int = -1,
    val recurringDay: Int = -1,
    // Pin support
    val isPinned: Boolean = false,
    val pinnedTime: Long = 0L,
    // Calendar type: "solar" or "lunar"
    val calendarType: String = "solar",
    // Lunar calendar fields
    val lunarYear: Int = 0,
    val lunarMonth: Int = 0,
    val lunarDay: Int = 0,
    val isLeapMonth: Boolean = false,
    // Repeat yearly for lunar birthdays
    val isRepeatYearly: Boolean = false
)