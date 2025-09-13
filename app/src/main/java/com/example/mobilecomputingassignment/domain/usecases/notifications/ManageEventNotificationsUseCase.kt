package com.example.mobilecomputingassignment.domain.usecases.notifications

import com.example.mobilecomputingassignment.data.service.EventNotificationScheduler
import com.example.mobilecomputingassignment.domain.models.Event
import javax.inject.Inject

class ManageEventNotificationsUseCase
@Inject
constructor(private val notificationScheduler: EventNotificationScheduler) {

  fun scheduleNotificationsForEvent(event: Event) {
    notificationScheduler.scheduleEventNotifications(event)
  }

  fun cancelNotificationsForEvent(eventId: String) {
    notificationScheduler.cancelEventNotifications(eventId)
  }

  fun isEventScheduled(eventId: String): Boolean {
    return notificationScheduler.isEventScheduled(eventId)
  }
}
