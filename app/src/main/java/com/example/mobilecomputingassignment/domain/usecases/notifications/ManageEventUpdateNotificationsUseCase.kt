package com.example.mobilecomputingassignment.domain.usecases.notifications

import com.example.mobilecomputingassignment.data.service.EventUpdateMonitorService
import com.example.mobilecomputingassignment.domain.models.Event
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ManageEventUpdateNotificationsUseCase @Inject constructor(
    private val eventUpdateMonitorService: EventUpdateMonitorService
) {
    
    suspend fun notifyEventUpdated(event: Event): Result<Unit> {
        return eventUpdateMonitorService.onEventUpdated(event)
    }
    
    suspend fun initializeWithCurrentEvents() {
        eventUpdateMonitorService.initializeWithCurrentEvents()
    }
    
    fun clearStoredEvents() {
        eventUpdateMonitorService.clearStoredEvents()
    }
    
    fun getLastKnownEvent(eventId: String): Event? {
        return eventUpdateMonitorService.getLastKnownEvent(eventId)
    }
    
    val lastKnownEvents: StateFlow<Map<String, Event>> = eventUpdateMonitorService.lastKnownEvents
}
