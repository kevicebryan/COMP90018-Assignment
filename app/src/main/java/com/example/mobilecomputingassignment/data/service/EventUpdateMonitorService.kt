package com.example.mobilecomputingassignment.data.service

import android.util.Log
import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.domain.repository.IEventRepository
import com.example.mobilecomputingassignment.domain.repository.IUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventUpdateMonitorService @Inject constructor(
    private val eventRepository: IEventRepository,
    private val userRepository: IUserRepository,
    private val eventUpdateNotificationService: EventUpdateNotificationService
) {
    companion object {
        private const val TAG = "EventUpdateMonitorService"
    }
    
    // Store the last known state of events to detect changes
    private val _lastKnownEvents = MutableStateFlow<Map<String, Event>>(emptyMap())
    val lastKnownEvents: StateFlow<Map<String, Event>> = _lastKnownEvents.asStateFlow()
    
    /**
     * Called when an event is updated - notifies all interested users
     */
    suspend fun onEventUpdated(updatedEvent: Event): Result<Unit> {
        return try {
            Log.d(TAG, "Event updated: ${updatedEvent.id}")
            
            // Get the previous state of the event to detect what changed
            val previousEvent = _lastKnownEvents.value[updatedEvent.id]
            
            if (previousEvent != null) {
                val changedFields = detectChangedFields(previousEvent, updatedEvent)
                
                if (changedFields.isNotEmpty()) {
                    Log.d(TAG, "Event ${updatedEvent.id} changed fields: $changedFields")
                    
                    // Notify all interested users (excluding the host)
                    updatedEvent.interestedUsers.forEach { userId ->
                        if (userId != updatedEvent.hostUserId) {
                            Log.d(TAG, "Notifying interested user: $userId about event update: ${updatedEvent.id}")
                            eventUpdateNotificationService.showEventUpdateNotification(updatedEvent, changedFields)
                        }
                    }
                } else {
                    Log.d(TAG, "No significant changes detected for event ${updatedEvent.id}")
                }
            } else {
                Log.d(TAG, "No previous state found for event ${updatedEvent.id}, skipping update notification")
            }
            
            // Update the stored event state
            _lastKnownEvents.value = _lastKnownEvents.value + (updatedEvent.id to updatedEvent)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error handling event update notification", e)
            Result.failure(e)
        }
    }
    
    /**
     * Initialize the service with current events (called on app start)
     */
    suspend fun initializeWithCurrentEvents() {
        try {
            val eventsResult = eventRepository.getAllActiveEvents()
            if (eventsResult.isSuccess) {
                val events = eventsResult.getOrThrow()
                val eventsMap = events.associateBy { it.id }
                _lastKnownEvents.value = eventsMap
                Log.d(TAG, "Initialized with ${events.size} events")
            } else {
                Log.e(TAG, "Failed to load events for initialization", eventsResult.exceptionOrNull())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing event update monitor", e)
        }
    }
    
    /**
     * Detect what fields have changed between two event versions
     */
    private fun detectChangedFields(oldEvent: Event, newEvent: Event): List<String> {
        val changedFields = mutableListOf<String>()
        
        // Check date changes
        if (oldEvent.date != newEvent.date) {
            changedFields.add("Date")
        }
        
        // Check check-in time changes
        if (oldEvent.checkInTime != newEvent.checkInTime) {
            changedFields.add("Check-in Time")
        }
        
        // Check location changes
        if (oldEvent.location.name != newEvent.location.name || 
            oldEvent.location.address != newEvent.location.address ||
            oldEvent.location.latitude != newEvent.location.latitude ||
            oldEvent.location.longitude != newEvent.location.longitude) {
            changedFields.add("Location")
        }
        
        // Check contact number changes
        if (oldEvent.contactNumber != newEvent.contactNumber) {
            changedFields.add("Contact Number")
        }
        
        // Check capacity changes
        if (oldEvent.capacity != newEvent.capacity) {
            changedFields.add("Capacity")
        }
        
        // Check amenities changes
        if (oldEvent.amenities != newEvent.amenities) {
            changedFields.add("Amenities")
        }
        
        // Check accessibility changes
        if (oldEvent.accessibility != newEvent.accessibility) {
            changedFields.add("Accessibility")
        }
        
        // Check match details changes
        if (oldEvent.matchDetails != newEvent.matchDetails) {
            changedFields.add("Match Details")
        }
        
        return changedFields
    }
    
    /**
     * Clear stored event state (useful for testing or when user logs out)
     */
    fun clearStoredEvents() {
        _lastKnownEvents.value = emptyMap()
        Log.d(TAG, "Cleared stored event states")
    }
    
    /**
     * Get the last known state of a specific event
     */
    fun getLastKnownEvent(eventId: String): Event? {
        return _lastKnownEvents.value[eventId]
    }
}
