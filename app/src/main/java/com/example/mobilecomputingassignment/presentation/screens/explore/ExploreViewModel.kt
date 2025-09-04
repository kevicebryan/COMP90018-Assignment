package com.example.mobilecomputingassignment.presentation.screens.explore

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.domain.repository.IEventRepository
import com.example.mobilecomputingassignment.domain.repository.LocationRepository
import com.google.android.gms.maps.model.LatLng
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
        private val locationRepository: LocationRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(ExploreUiState())
  val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

  init {
    checkLocationPermission()
    loadEvents()
    observeLocationUpdates()
  }

  private fun observeLocationUpdates() {
    viewModelScope.launch {
      locationRepository.getLocationUpdates()
        .collect { location ->
          _uiState.update { state ->
            state.copy(
              userLocation = LatLng(location.latitude, location.longitude),
              nearbyEvents = filterNearbyEvents(state.allEvents, location)
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
      calculateDistance(location.latitude, location.longitude, event.location.latitude, event.location.longitude) <=
              3000 // 3km in meters
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
      eventRepository.getAllActiveEvents().onSuccess { events ->
        Log.d("ExploreViewModel", "Loaded ${events.size} events for explore map")
        events.forEach { event ->
          Log.d("ExploreViewModel", "Event: ${event.matchDetails?.homeTeam ?: "No match"} vs ${event.matchDetails?.awayTeam ?: "No match"} at ${event.location.name} (${event.location.latitude}, ${event.location.longitude})")
        }
        _uiState.update { state ->
          state.copy(
                  allEvents = events,
                  visibleEvents = filterEventsInViewport(events, state.currentViewport)
          )
        }
        updateNearbyEvents()
      }.onFailure { error ->
        Log.e("ExploreViewModel", "Failed to load events", error)
      }
    }
  }

  fun onMapClick(latLng: LatLng) {
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
  }

  fun addEventToInterested(event: Event) {
    viewModelScope.launch { 
      // TODO: Get current user ID from authentication service
      val currentUserId = "current_user_id" // Replace with actual user ID
      eventRepository.addUserInterest(event.id, currentUserId) 
    }
  }

  fun openGoogleMapsDirections(event: Event) {
    val uri = Uri.parse("google.navigation:q=${event.location.latitude},${event.location.longitude}")
    val mapIntent =
            Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.google.android.apps.maps") }
    // Handle intent launch in the UI
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
        val error: String? = null
)

data class MapViewport(val southWest: LatLng, val northEast: LatLng)