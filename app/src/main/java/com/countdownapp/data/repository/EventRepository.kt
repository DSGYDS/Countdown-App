package com.countdownapp.data.repository

import com.countdownapp.data.db.EventDao
import com.countdownapp.data.entity.Event
import kotlinx.coroutines.flow.Flow

class EventRepository(private val eventDao: EventDao) {
    fun getAllEvents(): Flow<List<Event>> = eventDao.getAllEvents()

    suspend fun insertEvent(event: Event) = eventDao.insertEvent(event)

    suspend fun deleteEvent(event: Event) = eventDao.deleteEvent(event)

    suspend fun deleteEventsByIds(ids: List<Long>) = eventDao.deleteEventsByIds(ids)

    suspend fun updateBackground(eventId: Long, path: String?) =
        eventDao.updateBackground(eventId, path)

    suspend fun updateName(eventId: Long, name: String) =
        eventDao.updateName(eventId, name)

    suspend fun updateTargetDate(
        eventId: Long,
        newDate: Long,
        month: Int = -1,
        day: Int = -1,
        calendarType: String = "solar",
        lunarYear: Int = 0,
        lunarMonth: Int = 0,
        lunarDay: Int = 0,
        isLeapMonth: Boolean = false,
        isRepeatYearly: Boolean = false
    ) = eventDao.updateTargetDateAndRecurring(
        eventId,
        newDate,
        month,
        day,
        calendarType,
        lunarYear,
        lunarMonth,
        lunarDay,
        isLeapMonth,
        isRepeatYearly
    )

    suspend fun updatePinStatus(eventId: Long, isPinned: Boolean, pinnedTime: Long) =
        eventDao.updatePinStatus(eventId, isPinned, pinnedTime)

    suspend fun unpinOtherEvents(eventId: Long) = eventDao.unpinOtherEvents(eventId)

    suspend fun getPinnedCount(): Int = eventDao.getPinnedCount()

    suspend fun getPinnedEventIds(): List<Long> = eventDao.getPinnedEventIds()
}
