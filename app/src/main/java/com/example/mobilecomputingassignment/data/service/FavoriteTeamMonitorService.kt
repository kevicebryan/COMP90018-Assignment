package com.example.mobilecomputingassignment.data.service

import android.util.Log
import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.domain.repository.IEventRepository
import com.example.mobilecomputingassignment.domain.repository.IUserRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class FavoriteTeamMonitorService
@Inject
constructor(
        private val eventRepository: IEventRepository,
        private val userRepository: IUserRepository,
        private val favoriteTeamNotificationService: FavoriteTeamNotificationService
) {
  companion object {
    private const val TAG = "FavoriteTeamMonitorService"
  }

  private val _notifiedEvents = MutableStateFlow<Set<String>>(emptySet())
  val notifiedEvents: Flow<Set<String>> = _notifiedEvents.asStateFlow()

  private val _proximityNotifiedEvents = MutableStateFlow<Set<String>>(emptySet())
  val proximityNotifiedEvents: Flow<Set<String>> = _proximityNotifiedEvents.asStateFlow()

  /** Monitor favorite team events for a user at their current location */
  suspend fun checkFavoriteTeamEvents(
          userId: String,
          userLatitude: Double,
          userLongitude: Double
  ): Result<Unit> {
    return try {
            // Get user's favorite teams
            val user = userRepository.getUserById(userId)
            if (user == null) {
                Log.e(TAG, "Failed to get user data for userId: $userId")
                return Result.failure(Exception("User not found"))
            }
            
            if (user.teams.isEmpty()) {
        Log.d(TAG, "User has no favorite teams configured")
        return Result.success(Unit)
      }

      // Get all active events
      val eventsResult = eventRepository.getAllActiveEvents()
      if (eventsResult.isFailure) {
        Log.e(TAG, "Failed to get events", eventsResult.exceptionOrNull())
        return Result.failure(eventsResult.exceptionOrNull() ?: Exception("Failed to get events"))
      }

      val events = eventsResult.getOrThrow()
      Log.d(TAG, "Checking ${events.size} events for user's favorite teams: ${user.teams}")

      // Check each event for favorite teams and proximity
      events.forEach { event ->
        if (favoriteTeamNotificationService.eventFeaturesFavoriteTeam(event, user.teams)) {
          Log.d(
                  TAG,
                  "Event ${event.id} features favorite team: ${event.matchDetails?.homeTeam} vs ${event.matchDetails?.awayTeam}"
          )

          // Check if event is nearby (5km)
          val (isNearby, distanceKm) =
                  favoriteTeamNotificationService.isEventNearby(event, userLatitude, userLongitude)

          if (isNearby && !_notifiedEvents.value.contains(event.id)) {
            Log.d(TAG, "Showing nearby notification for event ${event.id} (${distanceKm}km away)")
            favoriteTeamNotificationService.showFavoriteTeamNearbyNotification(
                    event,
                    userLatitude,
                    userLongitude,
                    distanceKm
            )
            _notifiedEvents.value = _notifiedEvents.value + event.id
          }

          // Check if event is in proximity (100m)
          val (isInProximity, distanceM) =
                  favoriteTeamNotificationService.isEventInProximity(
                          event,
                          userLatitude,
                          userLongitude
                  )

          if (isInProximity && !_proximityNotifiedEvents.value.contains(event.id)) {
            Log.d(TAG, "Showing proximity notification for event ${event.id} (${distanceM}m away)")
            favoriteTeamNotificationService.showFavoriteTeamProximityNotification(
                    event,
                    userLatitude,
                    userLongitude,
                    distanceM
            )
            _proximityNotifiedEvents.value = _proximityNotifiedEvents.value + event.id
          }
        }
      }

      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Error checking favorite team events", e)
      Result.failure(e)
    }
  }

  /** Check for new events when user's location changes significantly */
  suspend fun onLocationChanged(userId: String, userLatitude: Double, userLongitude: Double) {
    Log.d(TAG, "Location changed for user $userId: $userLatitude, $userLongitude")
    checkFavoriteTeamEvents(userId, userLatitude, userLongitude)
  }

  /** Check for new events when a new event is created */
  suspend fun onNewEventCreated(event: Event) {
    Log.d(TAG, "New event created: ${event.id}")
    // Get all users and check if any have this event's teams as favorites
    // This would be called when a new event is created in the system
    // For now, we'll rely on location-based monitoring
  }

  /** Clear notifications for an event (when user is no longer interested) */
  fun clearEventNotifications(eventId: String) {
    favoriteTeamNotificationService.cancelFavoriteTeamNotifications(eventId)
    _notifiedEvents.value = _notifiedEvents.value - eventId
    _proximityNotifiedEvents.value = _proximityNotifiedEvents.value - eventId
  }

  /** Reset all notifications (useful for testing or when user logs out) */
  fun resetNotifications() {
    _notifiedEvents.value = emptySet()
    _proximityNotifiedEvents.value = emptySet()
  }
}
