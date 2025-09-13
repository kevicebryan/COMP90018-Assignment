package com.example.mobilecomputingassignment.presentation.utils

import com.example.mobilecomputingassignment.domain.models.Event
import java.util.Calendar
import java.util.Date

/**
 * Utility functions for handling event dates and filtering
 */
object EventDateUtils {
    
    /**
     * Check if an event is in the past (before today)
     * @param event The event to check
     * @return true if the event date is before today, false otherwise
     */
    fun isEventInPast(event: Event): Boolean {
        val today = getStartOfToday()
        return event.date.before(today)
    }
    
    /**
     * Check if an event is today or in the future
     * @param event The event to check
     * @return true if the event is today or in the future, false if it's in the past
     */
    fun isEventTodayOrFuture(event: Event): Boolean {
        return !isEventInPast(event)
    }
    
    /**
     * Filter events to only include those that are today or in the future
     * @param events List of events to filter
     * @return List of events that are today or in the future
     */
    fun filterFutureEvents(events: List<Event>): List<Event> {
        return events.filter { isEventTodayOrFuture(it) }
    }
    
    /**
     * Get the start of today (00:00:00)
     * @return Date representing the start of today
     */
    private fun getStartOfToday(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
}
