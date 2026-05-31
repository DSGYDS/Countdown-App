package com.countdownapp.data.db

import androidx.room.*
import com.countdownapp.data.entity.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY isPinned DESC, pinnedTime DESC, targetDate ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Insert
    suspend fun insertEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("DELETE FROM events WHERE id IN (:ids)")
    suspend fun deleteEventsByIds(ids: List<Long>)

    @Query("UPDATE events SET backgroundImagePath = :path WHERE id = :eventId")
    suspend fun updateBackground(eventId: Long, path: String?)

    @Query("UPDATE events SET name = :name WHERE id = :eventId")
    suspend fun updateName(eventId: Long, name: String)

    @Query(
        """
        UPDATE events
        SET targetDate = :newDate,
            recurringMonth = :month,
            recurringDay = :day,
            calendarType = :calendarType,
            lunarYear = :lunarYear,
            lunarMonth = :lunarMonth,
            lunarDay = :lunarDay,
            isLeapMonth = :isLeapMonth,
            isRepeatYearly = :isRepeatYearly
        WHERE id = :eventId
        """
    )
    suspend fun updateTargetDateAndRecurring(
        eventId: Long,
        newDate: Long,
        month: Int,
        day: Int,
        calendarType: String,
        lunarYear: Int,
        lunarMonth: Int,
        lunarDay: Int,
        isLeapMonth: Boolean,
        isRepeatYearly: Boolean
    )

    @Query("UPDATE events SET isPinned = :isPinned, pinnedTime = :pinnedTime WHERE id = :eventId")
    suspend fun updatePinStatus(eventId: Long, isPinned: Boolean, pinnedTime: Long)

    @Query("UPDATE events SET isPinned = 0, pinnedTime = 0 WHERE id != :eventId")
    suspend fun unpinOtherEvents(eventId: Long)

    @Query("SELECT COUNT(*) FROM events WHERE isPinned = 1")
    suspend fun getPinnedCount(): Int

    @Query("SELECT id FROM events WHERE isPinned = 1 ORDER BY pinnedTime DESC")
    suspend fun getPinnedEventIds(): List<Long>
}
