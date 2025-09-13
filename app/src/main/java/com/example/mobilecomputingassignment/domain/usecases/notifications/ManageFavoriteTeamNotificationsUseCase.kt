package com.example.mobilecomputingassignment.domain.usecases.notifications

import com.example.mobilecomputingassignment.data.service.FavoriteTeamMonitorService
import com.example.mobilecomputingassignment.domain.models.Event
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ManageFavoriteTeamNotificationsUseCase
@Inject
constructor(private val favoriteTeamMonitorService: FavoriteTeamMonitorService) {

  suspend fun checkFavoriteTeamEvents(
          userId: String,
          userLatitude: Double,
          userLongitude: Double
  ): Result<Unit> {
    return favoriteTeamMonitorService.checkFavoriteTeamEvents(userId, userLatitude, userLongitude)
  }

  suspend fun onLocationChanged(userId: String, userLatitude: Double, userLongitude: Double) {
    favoriteTeamMonitorService.onLocationChanged(userId, userLatitude, userLongitude)
  }

  suspend fun onNewEventCreated(event: Event) {
    favoriteTeamMonitorService.onNewEventCreated(event)
  }

  fun clearEventNotifications(eventId: String) {
    favoriteTeamMonitorService.clearEventNotifications(eventId)
  }

  fun resetNotifications() {
    favoriteTeamMonitorService.resetNotifications()
  }

  val notifiedEvents: Flow<Set<String>> = favoriteTeamMonitorService.notifiedEvents
  val proximityNotifiedEvents: Flow<Set<String>> =
          favoriteTeamMonitorService.proximityNotifiedEvents
}
