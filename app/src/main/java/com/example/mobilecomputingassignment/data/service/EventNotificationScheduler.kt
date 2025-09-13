package com.example.mobilecomputingassignment.data.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.mobilecomputingassignment.domain.models.Event
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class EventNotificationScheduler
@Inject
constructor(
        @ApplicationContext private val context: Context,
        private val notificationService: EventNotificationService
) {
  companion object {
    private const val TAG = "EventNotificationScheduler"

    // Action strings for broadcast receiver
    const val ACTION_EVENT_DAY_NOTIFICATION =
            "com.example.mobilecomputingassignment.EVENT_DAY_NOTIFICATION"
    const val ACTION_EVENT_30MIN_NOTIFICATION =
            "com.example.mobilecomputingassignment.EVENT_30MIN_NOTIFICATION"

    // Intent extras
    const val EXTRA_EVENT_ID = "event_id"
    const val EXTRA_EVENT_TITLE = "event_title"
    const val EXTRA_EVENT_LOCATION = "event_location"
    const val EXTRA_EVENT_TIME = "event_time"
    const val EXTRA_HOME_TEAM = "home_team"
    const val EXTRA_AWAY_TEAM = "away_team"
    const val EXTRA_CONTACT_NUMBER = "contact_number"
  }

  private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
  private val _scheduledEvents = MutableStateFlow<Set<String>>(emptySet())
  val scheduledEvents: Flow<Set<String>> = _scheduledEvents.asStateFlow()

  fun scheduleEventNotifications(event: Event) {
    val eventId = event.id
    Log.d(TAG, "Scheduling notifications for event: $eventId")

    // Schedule event day notification (at 9 AM on the event day)
    scheduleEventDayNotification(event)

    // Schedule 30-minute prior notification
    scheduleEvent30MinNotification(event)

    // Track scheduled event
    _scheduledEvents.value = _scheduledEvents.value + eventId
  }

  fun cancelEventNotifications(eventId: String) {
    Log.d(TAG, "Canceling notifications for event: $eventId")

    // Cancel event day notification
    val eventDayIntent =
            Intent(context, EventNotificationReceiver::class.java).apply {
              action = ACTION_EVENT_DAY_NOTIFICATION
              putExtra(EXTRA_EVENT_ID, eventId)
            }
    val eventDayPendingIntent =
            PendingIntent.getBroadcast(
                    context,
                    getEventDayRequestCode(eventId),
                    eventDayIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
    alarmManager.cancel(eventDayPendingIntent)

    // Cancel 30-minute notification
    val event30MinIntent =
            Intent(context, EventNotificationReceiver::class.java).apply {
              action = ACTION_EVENT_30MIN_NOTIFICATION
              putExtra(EXTRA_EVENT_ID, eventId)
            }
    val event30MinPendingIntent =
            PendingIntent.getBroadcast(
                    context,
                    getEvent30MinRequestCode(eventId),
                    event30MinIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
    alarmManager.cancel(event30MinPendingIntent)

    // Remove from tracked events
    _scheduledEvents.value = _scheduledEvents.value - eventId
  }

  private fun scheduleEventDayNotification(event: Event) {
    val eventDate =
            Calendar.getInstance().apply {
              time = event.date
              set(Calendar.HOUR_OF_DAY, 9) // 9 AM
              set(Calendar.MINUTE, 0)
              set(Calendar.SECOND, 0)
              set(Calendar.MILLISECOND, 0)
            }

    // Don't schedule if the time has already passed
    if (eventDate.timeInMillis <= System.currentTimeMillis()) {
      Log.d(TAG, "Event day notification time has passed, skipping for event: ${event.id}")
      return
    }

    val intent =
            Intent(context, EventNotificationReceiver::class.java).apply {
              action = ACTION_EVENT_DAY_NOTIFICATION
              putExtra(EXTRA_EVENT_ID, event.id)
              putExtra(
                      EXTRA_EVENT_TITLE,
                      "${event.matchDetails?.homeTeam ?: "Team A"} vs ${event.matchDetails?.awayTeam ?: "Team B"}"
              )
              putExtra(EXTRA_EVENT_LOCATION, event.location.name)
              putExtra(EXTRA_EVENT_TIME, event.checkInTime.time)
              putExtra(EXTRA_HOME_TEAM, event.matchDetails?.homeTeam ?: "")
              putExtra(EXTRA_AWAY_TEAM, event.matchDetails?.awayTeam ?: "")
              putExtra(EXTRA_CONTACT_NUMBER, event.contactNumber)
            }

    val pendingIntent =
            PendingIntent.getBroadcast(
                    context,
                    getEventDayRequestCode(event.id),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

    alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            eventDate.timeInMillis,
            pendingIntent
    )

    Log.d(TAG, "Scheduled event day notification for ${event.id} at ${eventDate.time}")
  }

  private fun scheduleEvent30MinNotification(event: Event) {
    val eventTime =
            Calendar.getInstance().apply {
              time = event.checkInTime
              add(Calendar.MINUTE, -30) // 30 minutes before
            }

    // Don't schedule if the time has already passed
    if (eventTime.timeInMillis <= System.currentTimeMillis()) {
      Log.d(TAG, "30-minute notification time has passed, skipping for event: ${event.id}")
      return
    }

    val intent =
            Intent(context, EventNotificationReceiver::class.java).apply {
              action = ACTION_EVENT_30MIN_NOTIFICATION
              putExtra(EXTRA_EVENT_ID, event.id)
              putExtra(
                      EXTRA_EVENT_TITLE,
                      "${event.matchDetails?.homeTeam ?: "Team A"} vs ${event.matchDetails?.awayTeam ?: "Team B"}"
              )
              putExtra(EXTRA_EVENT_LOCATION, event.location.name)
              putExtra(EXTRA_EVENT_TIME, event.checkInTime.time)
              putExtra(EXTRA_HOME_TEAM, event.matchDetails?.homeTeam ?: "")
              putExtra(EXTRA_AWAY_TEAM, event.matchDetails?.awayTeam ?: "")
              putExtra(EXTRA_CONTACT_NUMBER, event.contactNumber)
            }

    val pendingIntent =
            PendingIntent.getBroadcast(
                    context,
                    getEvent30MinRequestCode(event.id),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

    alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            eventTime.timeInMillis,
            pendingIntent
    )

    Log.d(TAG, "Scheduled 30-minute notification for ${event.id} at ${eventTime.time}")
  }

  private fun getEventDayRequestCode(eventId: String): Int {
    return 1000 + eventId.hashCode()
  }

  private fun getEvent30MinRequestCode(eventId: String): Int {
    return 2000 + eventId.hashCode()
  }

  fun isEventScheduled(eventId: String): Boolean {
    return _scheduledEvents.value.contains(eventId)
  }
}
