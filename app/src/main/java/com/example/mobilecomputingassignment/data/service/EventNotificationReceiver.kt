package com.example.mobilecomputingassignment.data.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.domain.models.EventLocation
import com.example.mobilecomputingassignment.domain.models.EventAmenities
import com.example.mobilecomputingassignment.domain.models.EventAccessibility
import com.example.mobilecomputingassignment.domain.models.MatchDetails
class EventNotificationReceiver : BroadcastReceiver() {

  companion object {
    private const val TAG = "EventNotificationReceiver"
  }

  override fun onReceive(context: Context, intent: Intent) {
    Log.d(TAG, "Received notification intent: ${intent.action}")

    when (intent.action) {
      EventNotificationScheduler.ACTION_EVENT_DAY_NOTIFICATION -> {
        handleEventDayNotification(context, intent)
      }
      EventNotificationScheduler.ACTION_EVENT_30MIN_NOTIFICATION -> {
        handleEvent30MinNotification(context, intent)
      }
    }
  }

    private fun handleEventDayNotification(context: Context, intent: Intent) {
        val event = createEventFromIntent(intent)
        if (event != null) {
            Log.d(TAG, "Showing event day notification for: ${event.id}")
            val notificationService = EventNotificationService(context)
            notificationService.showEventDayNotification(event)
        } else {
            Log.e(TAG, "Failed to create event from intent for day notification")
        }
    }
    
    private fun handleEvent30MinNotification(context: Context, intent: Intent) {
        val event = createEventFromIntent(intent)
        if (event != null) {
            Log.d(TAG, "Showing 30-minute notification for: ${event.id}")
            val notificationService = EventNotificationService(context)
            notificationService.showEvent30MinNotification(event)
        } else {
            Log.e(TAG, "Failed to create event from intent for 30-minute notification")
        }
    }

  private fun createEventFromIntent(intent: Intent): Event? {
    return try {
      val eventId = intent.getStringExtra(EventNotificationScheduler.EXTRA_EVENT_ID)
      val eventTitle = intent.getStringExtra(EventNotificationScheduler.EXTRA_EVENT_TITLE)
      val eventLocation = intent.getStringExtra(EventNotificationScheduler.EXTRA_EVENT_LOCATION)
      val eventTime = intent.getLongExtra(EventNotificationScheduler.EXTRA_EVENT_TIME, 0L)
      val homeTeam = intent.getStringExtra(EventNotificationScheduler.EXTRA_HOME_TEAM)
      val awayTeam = intent.getStringExtra(EventNotificationScheduler.EXTRA_AWAY_TEAM)
      val contactNumber = intent.getStringExtra(EventNotificationScheduler.EXTRA_CONTACT_NUMBER)

      if (eventId == null || eventTitle == null || eventLocation == null || eventTime == 0L) {
        Log.e(TAG, "Missing required event data in intent")
        return null
      }

      Event(
              id = eventId,
              hostUserId = "", // Not available in intent
              hostUsername = "", // Not available in intent
              date = java.util.Date(eventTime),
              checkInTime = java.util.Date(eventTime),
              matchId = "", // Not available in intent
              matchDetails =
                      MatchDetails(
                              id = "", // Not available in intent
                              homeTeam = homeTeam ?: "Team A",
                              awayTeam = awayTeam ?: "Team B",
                              competition = "", // Not available in intent
                              venue = eventLocation,
                              matchTime = java.util.Date(eventTime),
                              round = "", // Not available in intent
                              season = 2025 // Default season
                      ),
              location =
                      EventLocation(
                              name = eventLocation,
                              address = "", // Not available in intent
                              latitude = 0.0, // Not available in intent
                              longitude = 0.0 // Not available in intent
                      ),
              capacity = 0, // Not available in intent
              contactNumber = contactNumber ?: "",
              amenities = EventAmenities(), // Default amenities
              accessibility = EventAccessibility(), // Default accessibility
              attendees = "", // Not available in intent
              attendeesCount = 0L, // Not available in intent
              interestedUsers = emptyList(),
              createdAt = java.util.Date(), // Current time
              updatedAt = java.util.Date(), // Current time
              isActive = true,
              volume = 1 // Default volume
      )
    } catch (e: Exception) {
      Log.e(TAG, "Error creating event from intent", e)
      null
    }
  }
}
