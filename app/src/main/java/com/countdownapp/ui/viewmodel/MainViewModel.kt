package com.countdownapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.countdownapp.data.db.AppDatabase
import com.countdownapp.data.entity.Event
import com.countdownapp.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EventRepository

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    // Edit mode state
    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private val _selectedEventIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedEventIds: StateFlow<Set<Long>> = _selectedEventIds.asStateFlow()

    init {
        val dao = AppDatabase.getDatabase(application).eventDao()
        repository = EventRepository(dao)
        viewModelScope.launch {
            repository.getAllEvents().collect { eventList ->
                _events.value = eventList
            }
        }
    }

    fun addEvent(
        name: String,
        targetDate: Long,
        recurringMonth: Int = -1,
        recurringDay: Int = -1,
        calendarType: String = "solar",
        lunarYear: Int = 0,
        lunarMonth: Int = 0,
        lunarDay: Int = 0,
        isLeapMonth: Boolean = false,
        isRepeatYearly: Boolean = false
    ) {
        viewModelScope.launch {
            repository.insertEvent(
                Event(
                    name = name,
                    targetDate = targetDate,
                    recurringMonth = recurringMonth,
                    recurringDay = recurringDay,
                    calendarType = calendarType,
                    lunarYear = lunarYear,
                    lunarMonth = lunarMonth,
                    lunarDay = lunarDay,
                    isLeapMonth = isLeapMonth,
                    isRepeatYearly = isRepeatYearly
                )
            )
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }

    fun updateBackground(eventId: Long, path: String?) {
        viewModelScope.launch {
            repository.updateBackground(eventId, path)
        }
    }

    fun updateName(eventId: Long, name: String) {
        viewModelScope.launch {
            repository.updateName(eventId, name)
        }
    }

    fun updateTargetDate(
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
    ) {
        viewModelScope.launch {
            repository.updateTargetDate(
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
        }
    }

    fun enterEditMode() {
        _isEditMode.value = true
        _selectedEventIds.value = emptySet()
    }

    fun exitEditMode() {
        _isEditMode.value = false
        _selectedEventIds.value = emptySet()
    }

    fun toggleSelection(eventId: Long) {
        val current = _selectedEventIds.value.toMutableSet()
        if (current.contains(eventId)) {
            current.remove(eventId)
        } else {
            current.add(eventId)
        }
        _selectedEventIds.value = current
    }

    fun selectAll() {
        _selectedEventIds.value = _events.value.map { it.id }.toSet()
    }

    fun deselectAll() {
        _selectedEventIds.value = emptySet()
    }

    fun deleteSelectedEvents(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteEventsByIds(_selectedEventIds.value.toList())
            exitEditMode()
            onComplete()
        }
    }

    fun togglePin(event: Event) {
        viewModelScope.launch {
            val newPinned = !event.isPinned
            val newPinnedTime = if (newPinned) System.currentTimeMillis() else 0L
            if (newPinned) {
                repository.unpinOtherEvents(event.id)
            }
            repository.updatePinStatus(event.id, newPinned, newPinnedTime)
        }
    }
}
