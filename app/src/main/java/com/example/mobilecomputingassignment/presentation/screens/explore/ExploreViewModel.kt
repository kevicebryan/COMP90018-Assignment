package com.example.mobilecomputingassignment.presentation.screens.explore

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.domain.repository.IEventRepository
import com.example.mobilecomputingassignment.domain.repository.LocationRepository
import com.example.mobilecomputingassignment.domain.usecases.network.CheckNetworkConnectivityUseCase
import com.example.mobilecomputingassignment.domain.usecases.notifications.ManageEventNotificationsUseCase
import com.example.mobilecomputingassignment.domain.usecases.notifications.ManageFavoriteTeamNotificationsUseCase
import com.example.mobilecomputingassignment.presentation.utils.EventDateUtils
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class ExploreViewModel
@Inject
constructor(
        private val eventRepository: IEventRepository,
        private val locationRepository: LocationRepository,
        private val auth: FirebaseAuth,
        private val manageEventNotificationsUseCase: ManageEventNotificationsUseCase,
        private val manageFavoriteTeamNotificationsUseCase: ManageFavoriteTeamNotificationsUseCase,
        private val checkNetworkConnectivityUseCase: CheckNetworkConnectivityUseCase
) : ViewModel() {

  private val _uiState = MutableStateFlow(ExploreUiState())
  val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

  init {
    checkLocationPermission()
    loadEvents()
    observeLocationUpdates()
    updateCurrentUserId()
  }

  private fun updateCurrentUserId() {
    val currentUserId = auth.currentUser?.uid
    Log.d("ExploreViewModel", "Updating current user ID: $currentUserId")
    _uiState.update { it.copy(currentUserId = currentUserId) }
  }

  private fun updateSelectedEventWithFreshData(eventId: String) {
    // Find the updated event from the current allEvents list
    val updatedEvent = _uiState.value.allEvents.find { it.id == eventId }
    if (updatedEvent != null && _uiState.value.selectedEvent?.id == eventId) {
      Log.d(
              "ExploreViewModel",
              "Updating selected event with fresh data: ${updatedEvent.interestedUsers.size} interested users"
      )
      _uiState.update { it.copy(selectedEvent = updatedEvent) }
    }
  }

  fun refreshCurrentUserId() {
    updateCurrentUserId()
  }

  private fun observeLocationUpdates() {
    viewModelScope.launch {
      locationRepository.getLocationUpdates().collect { location ->
        _uiState.update { state ->
          state.copy(
                  userLocation = LatLng(location.latitude, location.longitude),
                  nearbyEvents = filterNearbyEvents(state.allEvents, location)
          )
        }

        // Check for favorite team events when location changes
        val currentUserId = _uiState.value.currentUserId
        if (currentUserId != null) {
          manageFavoriteTeamNotificationsUseCase.onLocationChanged(
                  currentUserId,
                  location.latitude,
                  location.longitude
          )
        }
      }
    }
  }

  private fun filterNearbyEvents(
          events: List<Event>,
          location: android.location.Location
  ): List<Event> {
    return events.filter { event ->
      calculateDistance(
              location.latitude,
              location.longitude,
              event.location.latitude,
              event.location.longitude
      ) <= 3000 // 3km in meters
    }
  }

  private fun checkLocationPermission() {
    viewModelScope.launch {
      _uiState.update {
        it.copy(isLocationPermissionGranted = locationRepository.hasLocationPermission())
      }
    }
  }

  private fun loadEvents() {
    viewModelScope.launch {
      Log.d("ExploreViewModel", "Loading events...")

      // Check network connectivity first
      if (!checkNetworkConnectivityUseCase.hasInternetConnectivity()) {
        Log.e("ExploreViewModel", "No internet connectivity")
        _uiState.update {
          it.copy(error = checkNetworkConnectivityUseCase.getConnectivityErrorMessage())
        }
        return@launch
      }

      eventRepository
              .getAllActiveEvents()
              .onSuccess { events ->
                Log.d("ExploreViewModel", "Loaded ${events.size} events for explore map")
                
                // Filter out past events - only show today onwards
                val futureEvents = EventDateUtils.filterFutureEvents(events)
                Log.d("ExploreViewModel", "Filtered to ${futureEvents.size} future events (removed ${events.size - futureEvents.size} past events)")
                
                futureEvents.forEach { event ->
                  Log.d(
                          "ExploreViewModel",
                          "Event: ${event.matchDetails?.homeTeam ?: "No match"} vs ${event.matchDetails?.awayTeam ?: "No match"} at ${event.location.name} (${event.location.latitude}, ${event.location.longitude})"
                  )
                  Log.d(
                          "ExploreViewModel",
                          "Event ${event.id} interested users: ${event.interestedUsers.size} - ${event.interestedUsers}"
                  )
                }
                _uiState.update { state ->
                  state.copy(
                          allEvents = futureEvents,
                          visibleEvents = filterEventsInViewport(futureEvents, state.currentViewport),
                          error = null // Clear any previous errors
                  )
                }
                updateNearbyEvents()

                // Check for favorite team events after loading
                val currentUserId = _uiState.value.currentUserId
                val userLocation = _uiState.value.userLocation
                if (currentUserId != null && userLocation != null) {
                  manageFavoriteTeamNotificationsUseCase.checkFavoriteTeamEvents(
                          currentUserId,
                          userLocation.latitude,
                          userLocation.longitude
                  )
                }
              }
              .onFailure { error ->
                Log.e("ExploreViewModel", "Failed to load events", error)
                val errorMessage =
                        if (checkNetworkConnectivityUseCase.isNetworkAvailable()) {
                          "Unable to connect to servers. Please try again."
                        } else {
                          checkNetworkConnectivityUseCase.getConnectivityErrorMessage()
                        }
                _uiState.update { it.copy(error = errorMessage) }
              }
    }
  }

  fun onMapClick(@Suppress("UNUSED_PARAMETER") latLng: LatLng) {
    _uiState.update { it.copy(selectedEvent = null) }
  }

  fun onMarkerClick(event: Event) {
    _uiState.update { it.copy(selectedEvent = event) }
  }

  fun onEventSelected(event: Event) {
    _uiState.update { it.copy(selectedEvent = event) }
  }

  fun onEventDeselected() {
    _uiState.update { it.copy(selectedEvent = null) }
  }

  fun refreshEvents() {
    loadEvents()
    updateCurrentUserId() // Also refresh the current user ID
  }

  fun onLocationPermissionGranted() {
    _uiState.update { it.copy(isLocationPermissionGranted = true) }
    updateNearbyEvents()
  }

  fun toggleEventInterest(event: Event) {
    val currentUserId = auth.currentUser?.uid ?: return

    viewModelScope.launch {
      // Prevent users from showing interest in their own events
      if (currentUserId == event.hostUserId) {
        Log.d("ExploreViewModel", "User cannot show interest in their own event")
        return@launch
      }

      val isCurrentlyInterested = event.interestedUsers.contains(currentUserId)
      Log.d(
              "ExploreViewModel",
              "Toggling interest for event ${event.id}: currently interested = $isCurrentlyInterested"
      )

      // Optimistic UI update - immediately update the UI
      updateEventInterestOptimistically(event, currentUserId, !isCurrentlyInterested)

      // Set loading state
      _uiState.update { it.copy(isUpdatingInterest = true) }

      val result =
              if (isCurrentlyInterested) {
                eventRepository.removeUserInterest(event.id, currentUserId)
              } else {
                eventRepository.addUserInterest(event.id, currentUserId)
              }

      result
              .onSuccess {
                Log.d("ExploreViewModel", "Interest toggled successfully")

                // Schedule or cancel notifications based on interest
                if (!isCurrentlyInterested) {
                  // User just became interested - schedule notifications
                  manageEventNotificationsUseCase.scheduleNotificationsForEvent(event)
                  Log.d("ExploreViewModel", "Scheduled notifications for event: ${event.id}")
                } else {
                  // User just removed interest - cancel notifications
                  manageEventNotificationsUseCase.cancelNotificationsForEvent(event.id)
                  Log.d("ExploreViewModel", "Cancelled notifications for event: ${event.id}")
                }

                // Refresh events to get updated interest data from server
                loadEvents()
                // Update the selected event with fresh data
                updateSelectedEventWithFreshData(event.id)
                // Clear loading state
                _uiState.update { it.copy(isUpdatingInterest = false) }
              }
              .onFailure { error ->
                Log.e("ExploreViewModel", "Failed to toggle interest", error)
                // Revert optimistic update on error
                updateEventInterestOptimistically(event, currentUserId, isCurrentlyInterested)
                // Clear loading state
                _uiState.update { it.copy(isUpdatingInterest = false) }
              }
    }
  }

  private fun updateEventInterestOptimistically(
          event: Event,
          userId: String,
          shouldBeInterested: Boolean
  ) {
    val updatedEvent =
            if (shouldBeInterested) {
              // Add user to interested list if not already present
              event.copy(interestedUsers = event.interestedUsers + userId)
            } else {
              // Remove user from interested list
              event.copy(interestedUsers = event.interestedUsers.filter { it != userId })
            }

    // Update all events list
    _uiState.update { state ->
      state.copy(
              allEvents = state.allEvents.map { if (it.id == event.id) updatedEvent else it },
              visibleEvents =
                      state.visibleEvents.map { if (it.id == event.id) updatedEvent else it },
              nearbyEvents = state.nearbyEvents.map { if (it.id == event.id) updatedEvent else it },
              selectedEvent =
                      if (state.selectedEvent?.id == event.id) updatedEvent else state.selectedEvent
      )
    }
  }

  fun openGoogleMapsDirections(event: Event): Intent {
    val uri =
            Uri.parse("google.navigation:q=${event.location.latitude},${event.location.longitude}")
    return Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.google.android.apps.maps") }
  }

  fun searchLocation(query: String, context: Context, cameraPositionState: com.google.maps.android.compose.CameraPositionState) {
    viewModelScope.launch {
      try {
        if (Geocoder.isPresent()) {
          val geocoder = Geocoder(context)
          val addresses = geocoder.getFromLocationName(query, 1)
          
          if (addresses?.isNotEmpty() == true) {
            val address = addresses[0]
            val latLng = LatLng(address.latitude, address.longitude)
            
            Log.d("ExploreViewModel", "Found location: ${address.getAddressLine(0)} at $latLng")
            
            // Animate camera to the searched location
            cameraPositionState.animate(
              update = com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(latLng, 15f)
            )
          } else {
            Log.w("ExploreViewModel", "No results found for query: $query")
          }
        } else {
          Log.e("ExploreViewModel", "Geocoder not available")
        }
      } catch (e: Exception) {
        Log.e("ExploreViewModel", "Error searching location: $query", e)
      }
    }
  }

  private fun updateNearbyEvents() {
    viewModelScope.launch {
      val userLocation = locationRepository.getCurrentLocation()
      if (userLocation != null) {
        val nearbyEvents =
                _uiState.value.allEvents.filter { event ->
                  calculateDistance(
                          userLocation.latitude,
                          userLocation.longitude,
                          event.location.latitude,
                          event.location.longitude
                  ) <= 3000 // 3km in meters
                }
        _uiState.update { it.copy(nearbyEvents = nearbyEvents) }
      }
    }
  }

  private fun filterEventsInViewport(events: List<Event>, viewport: MapViewport?): List<Event> {
    if (viewport == null) return events
    return events.filter { event ->
      event.location.latitude in viewport.southWest.latitude..viewport.northEast.latitude &&
              event.location.longitude in viewport.southWest.longitude..viewport.northEast.longitude
    }
  }

  private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371e3 // Earth's radius in meters
    val φ1 = lat1 * PI / 180
    val φ2 = lat2 * PI / 180
    val Δφ = (lat2 - lat1) * PI / 180
    val Δλ = (lon2 - lon1) * PI / 180

    val a = sin(Δφ / 2) * sin(Δφ / 2) + cos(φ1) * cos(φ2) * sin(Δλ / 2) * sin(Δλ / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return r * c
  }
}

data class ExploreUiState(
        val isLocationPermissionGranted: Boolean = false,
        val allEvents: List<Event> = emptyList(),
        val visibleEvents: List<Event> = emptyList(),
        val nearbyEvents: List<Event> = emptyList(),
        val selectedEvent: Event? = null,
        val currentViewport: MapViewport? = null,
        val userLocation: LatLng? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val currentUserId: String? = null,
        val isUpdatingInterest: Boolean = false
)

data class MapViewport(val southWest: LatLng, val northEast: LatLng)
