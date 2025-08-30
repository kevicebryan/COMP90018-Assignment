package com.example.mobilecomputingassignment.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingassignment.core.utils.ContentFilter
import com.example.mobilecomputingassignment.data.repository.MatchRepository
import com.example.mobilecomputingassignment.domain.models.*
import com.example.mobilecomputingassignment.domain.usecases.events.*
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * EventViewModel - UI State Management
 *
 * This ViewModel manages the UI state for the Events screen. It follows the MVVM
 * (Model-View-ViewModel) architecture pattern.
 *
 * Key Responsibilities:
 * - Manages UI state (loading, error, data)
 * - Handles user interactions (create, edit, delete events)
 * - Coordinates between UI and business logic
 * - Manages data flow using StateFlow
 *
 * Architecture Concepts:
 * - ViewModel: Survives configuration changes (rotation, etc.)
 * - StateFlow: Reactive data streams for UI updates
 * - Use Cases: Business logic operations
 * - Dependency Injection: Automatic dependency management
 */

/**
 * UI State Data Class
 *
 * Contains all the data that the UI needs to display. This is the single source of truth for the
 * Events screen.
 */
data class EventUiState(
        // Loading states
        val isLoading: Boolean = false, // General loading indicator
        val isLoadingMatches: Boolean = false, // Loading indicator for match data

        // Error handling
        val errorMessage: String? = null, // Error message to display

        // Event data
        val hostedEvents: List<Event> = emptyList(), // Events created by current user
        val interestedEvents: List<Event> = emptyList(), // Events user is interested in
        val allEvents: List<Event> = emptyList(), // All available events

        // UI state
        val selectedTab: Int = 0, // 0 = Interested tab, 1 = Hosted tab
        val isCreatingEvent: Boolean = false, // Show create event form
        val isEditingEvent: Boolean = false, // Show edit event form
        val editingEvent: Event? = null, // Event being edited

        // Match selection
        val availableMatches: List<MatchDetails> = emptyList(), // Matches for selected date

        // Map picker
        val showMapPicker: Boolean = false // Show map picker dialog
)

/**
 * Form Data Class
 *
 * Contains all the data for the event creation/editing form. This is separate from the UI state to
 * keep form data isolated.
 */
data class EventFormData(
        // Event timing
        val date: Date = Date(), // When the event occurs
        val checkInTime: Date = Date(), // When attendees should arrive

        // Match association
        val matchId: String = "", // Selected match ID
        val selectedMatch: MatchDetails? = null, // Full match details

        // Location
        val locationName: String = "", // Venue name
        val locationAddress: String = "", // Full address
        val latitude: Double = 0.0, // GPS coordinates
        val longitude: Double = 0.0,

        // Event details
        val capacity: Int = 10, // Maximum attendees
        val contactNumber: String = "", // Host contact
        val volume: Int = 1, // Event volume level

        // Venue features
        val amenities: EventAmenities = EventAmenities(), // General facilities
        val accessibility: EventAccessibility = EventAccessibility() // Accessibility features
)

/**
 * Main ViewModel Class
 *
 * Manages all UI state and business logic for the Events screen. Uses dependency injection to get
 * required dependencies.
 */
@HiltViewModel
class EventViewModel
@Inject
constructor(
        // Use Cases: Business logic operations
        private val createEventUseCase: CreateEventUseCase,
        private val getUserEventsUseCase: GetUserEventsUseCase,
        private val updateEventUseCase: UpdateEventUseCase,
        private val manageEventInterestUseCase: ManageEventInterestUseCase,

        // Repository: Data access
        private val matchRepository: MatchRepository
) : ViewModel() {

        // StateFlow: Reactive data streams for UI updates
        // MutableStateFlow: Internal state that can be modified
        // StateFlow: Public read-only version for UI consumption
        private val _uiState = MutableStateFlow(EventUiState())
        val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()

        // Form data state
        private val _formData = MutableStateFlow(EventFormData())
        val formData: StateFlow<EventFormData> = _formData.asStateFlow()

        // Current authenticated user
        private val currentUser = FirebaseAuth.getInstance().currentUser

        companion object {
                private const val TAG = "EventViewModel" // For logging
        }

        init {
                loadUserEvents()
        }

        fun setSelectedTab(tab: Int) {
                _uiState.value = _uiState.value.copy(selectedTab = tab)
        }

        fun loadUserEvents() {
                val userId = currentUser?.uid ?: return

                viewModelScope.launch {
                        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                        try {
                                // Load hosted events
                                val hostedResult = getUserEventsUseCase.getHostedEvents(userId)
                                val hostedEvents =
                                        hostedResult.getOrElse {
                                                Log.e(TAG, "Failed to load hosted events", it)
                                                emptyList()
                                        }

                                // Load interested events
                                val interestedResult =
                                        getUserEventsUseCase.getInterestedEvents(userId)
                                val interestedEvents =
                                        interestedResult.getOrElse {
                                                Log.e(TAG, "Failed to load interested events", it)
                                                emptyList()
                                        }

                                // Load all events for discovery
                                val allEventsResult = getUserEventsUseCase.getAllActiveEvents()
                                val allEvents =
                                        allEventsResult.getOrElse {
                                                Log.e(TAG, "Failed to load all events", it)
                                                emptyList()
                                        }

                                Log.d(
                                        TAG,
                                        "Loaded ${hostedEvents.size} hosted events, ${interestedEvents.size} interested events, ${allEvents.size} total events"
                                )
                                _uiState.value =
                                        _uiState.value.copy(
                                                isLoading = false,
                                                hostedEvents = hostedEvents,
                                                interestedEvents = interestedEvents,
                                                allEvents = allEvents
                                        )
                        } catch (e: Exception) {
                                Log.e(TAG, "Error loading events", e)
                                _uiState.value =
                                        _uiState.value.copy(
                                                isLoading = false,
                                                errorMessage = "Failed to load events: ${e.message}"
                                        )
                        }
                }
        }

        fun startCreatingEvent() {
                _formData.value = EventFormData()
                _uiState.value =
                        _uiState.value.copy(
                                isCreatingEvent = true,
                                isEditingEvent = false,
                                editingEvent = null
                        )
        }

        fun startEditingEvent(event: Event) {
                _formData.value =
                        EventFormData(
                                date = event.date,
                                checkInTime = event.checkInTime,
                                matchId = event.matchId,
                                selectedMatch = event.matchDetails,
                                locationName = event.location.name,
                                locationAddress = event.location.address,
                                latitude = event.location.latitude,
                                longitude = event.location.longitude,
                                capacity = event.capacity,
                                contactNumber = event.contactNumber,
                                amenities = event.amenities,
                                accessibility = event.accessibility
                        )
                _uiState.value =
                        _uiState.value.copy(
                                isCreatingEvent = false,
                                isEditingEvent = true,
                                editingEvent = event
                        )
        }

        fun cancelEventForm() {
                _uiState.value =
                        _uiState.value.copy(
                                isCreatingEvent = false,
                                isEditingEvent = false,
                                editingEvent = null
                        )
                _formData.value = EventFormData()
        }

        fun updateFormData(formData: EventFormData) {
                _formData.value = formData
        }

        fun loadMatchesForDate(date: Date) {
                viewModelScope.launch {
                        _uiState.value = _uiState.value.copy(isLoadingMatches = true)

                        matchRepository
                                .getMatchesForDate(date)
                                .onSuccess { matches ->
                                        Log.d(
                                                TAG,
                                                "Loaded ${matches.size} matches for selected date"
                                        )
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoadingMatches = false,
                                                        availableMatches = matches
                                                )
                                }
                                .onFailure { exception ->
                                        Log.e(TAG, "Failed to load matches", exception)
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoadingMatches = false,
                                                        availableMatches = emptyList(),
                                                        errorMessage =
                                                                "Failed to load matches: ${exception.message}"
                                                )
                                }
                }
        }

        fun selectMatch(match: MatchDetails) {
                val updatedFormData =
                        _formData.value.copy(
                                matchId = match.id,
                                selectedMatch = match,
                                // Set location name to venue if not already set
                                locationName =
                                        if (_formData.value.locationName.isEmpty()) match.venue
                                        else _formData.value.locationName,
                                // Update event date and check-in time based on match time
                                date = match.matchTime,
                                checkInTime =
                                        Date(
                                                match.matchTime.time - (30 * 60 * 1000)
                                        ) // 30 minutes before match
                        )
                _formData.value = updatedFormData
        }

        fun createEvent() {
                val userId = currentUser?.uid ?: return
                // TODO: use the profile to get the username
                val username =
                        currentUser?.displayName
                                ?: currentUser?.email?.split("@")?.first() ?: "Unknown User"

                Log.d(TAG, "Creating event with userId: $userId, username: $username")
                val form = _formData.value

                // Validate venue name and address for inappropriate content
                val venueNameValidation = ContentFilter.validateContent(form.locationName)
                val venueAddressValidation = ContentFilter.validateContent(form.locationAddress)

                if (!venueNameValidation.isValid) {
                        _uiState.value =
                                _uiState.value.copy(
                                        errorMessage =
                                                "Venue name: ${venueNameValidation.errorMessage}"
                                )
                        return
                }

                if (!venueAddressValidation.isValid) {
                        _uiState.value =
                                _uiState.value.copy(
                                        errorMessage =
                                                "Venue address: ${venueAddressValidation.errorMessage}"
                                )
                        return
                }

                val event =
                        Event(
                                hostUserId = userId,
                                hostUsername = username,
                                date = form.date,
                                checkInTime = form.checkInTime,
                                matchId = form.matchId,
                                matchDetails = form.selectedMatch,
                                location =
                                        EventLocation(
                                                name =
                                                        ContentFilter.sanitizeInput(
                                                                form.locationName
                                                        ),
                                                address =
                                                        ContentFilter.sanitizeInput(
                                                                form.locationAddress
                                                        ),
                                                latitude = form.latitude,
                                                longitude = form.longitude
                                        ),
                                capacity = form.capacity,
                                contactNumber = form.contactNumber,
                                amenities = form.amenities,
                                accessibility = form.accessibility,
                                attendees = "",
                                volume = form.volume,
                                createdAt = Date(),
                                updatedAt = Date(),
                                isActive = true
                        )

                viewModelScope.launch {
                        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                        createEventUseCase
                                .execute(event)
                                .onSuccess { eventId ->
                                        Log.d(TAG, "Event created successfully with ID: $eventId")
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoading = false,
                                                        isCreatingEvent = false
                                                )
                                        _formData.value = EventFormData()
                                        loadUserEvents() // Refresh the list
                                }
                                .onFailure { exception ->
                                        Log.e(TAG, "Failed to create event", exception)
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoading = false,
                                                        errorMessage = exception.message
                                                                        ?: "Failed to create event"
                                                )
                                }
                }
        }

        fun updateEvent() {
                val editingEvent = _uiState.value.editingEvent ?: return
                val form = _formData.value

                // Validate venue name and address for inappropriate content
                val venueNameValidation = ContentFilter.validateContent(form.locationName)
                val venueAddressValidation = ContentFilter.validateContent(form.locationAddress)

                if (!venueNameValidation.isValid) {
                        _uiState.value =
                                _uiState.value.copy(
                                        errorMessage =
                                                "Venue name: ${venueNameValidation.errorMessage}"
                                )
                        return
                }

                if (!venueAddressValidation.isValid) {
                        _uiState.value =
                                _uiState.value.copy(
                                        errorMessage =
                                                "Venue address: ${venueAddressValidation.errorMessage}"
                                )
                        return
                }

                val updatedEvent =
                        editingEvent.copy(
                                date = form.date,
                                checkInTime = form.checkInTime,
                                matchId = form.matchId,
                                matchDetails = form.selectedMatch,
                                location =
                                        EventLocation(
                                                name =
                                                        ContentFilter.sanitizeInput(
                                                                form.locationName
                                                        ),
                                                address =
                                                        ContentFilter.sanitizeInput(
                                                                form.locationAddress
                                                        ),
                                                latitude = form.latitude,
                                                longitude = form.longitude
                                        ),
                                capacity = form.capacity,
                                contactNumber = form.contactNumber,
                                amenities = form.amenities,
                                accessibility = form.accessibility,
                                volume = form.volume
                        )

                viewModelScope.launch {
                        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                        updateEventUseCase
                                .execute(updatedEvent)
                                .onSuccess {
                                        Log.d(TAG, "Event updated successfully")
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoading = false,
                                                        isEditingEvent = false,
                                                        editingEvent = null
                                                )
                                        _formData.value = EventFormData()
                                        loadUserEvents() // Refresh the list
                                }
                                .onFailure { exception ->
                                        Log.e(TAG, "Failed to update event", exception)
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoading = false,
                                                        errorMessage = exception.message
                                                                        ?: "Failed to update event"
                                                )
                                }
                }
        }

        fun deleteEvent(eventId: String) {
                viewModelScope.launch {
                        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                        updateEventUseCase
                                .deleteEvent(eventId)
                                .onSuccess {
                                        Log.d(TAG, "Event deleted successfully")
                                        _uiState.value = _uiState.value.copy(isLoading = false)
                                        loadUserEvents() // Refresh the list
                                }
                                .onFailure { exception ->
                                        Log.e(TAG, "Failed to delete event", exception)
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoading = false,
                                                        errorMessage = exception.message
                                                                        ?: "Failed to delete event"
                                                )
                                }
                }
        }

        fun toggleEventInterest(eventId: String, isInterested: Boolean) {
                val userId = currentUser?.uid ?: return

                viewModelScope.launch {
                        val result =
                                if (isInterested) {
                                        manageEventInterestUseCase.removeInterest(eventId, userId)
                                } else {
                                        manageEventInterestUseCase.addInterest(eventId, userId)
                                }

                        result
                                .onSuccess {
                                        Log.d(TAG, "Event interest toggled successfully")
                                        loadUserEvents() // Refresh the list
                                }
                                .onFailure { exception ->
                                        Log.e(TAG, "Failed to toggle event interest", exception)
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        errorMessage = exception.message
                                                                        ?: "Failed to update interest"
                                                )
                                }
                }
        }

        fun checkInToEvent(eventId: String) {
                val userId = currentUser?.uid ?: return

                viewModelScope.launch {
                        manageEventInterestUseCase
                                .checkIn(eventId, userId)
                                .onSuccess {
                                        Log.d(TAG, "Checked in to event successfully")
                                        loadUserEvents() // Refresh the list
                                }
                                .onFailure { exception ->
                                        Log.e(TAG, "Failed to check in to event", exception)
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        errorMessage = exception.message
                                                                        ?: "Failed to check in"
                                                )
                                }
                }
        }

        fun clearError() {
                _uiState.value = _uiState.value.copy(errorMessage = null)
        }

        // Map picker methods
        fun showMapPicker() {
                _uiState.value = _uiState.value.copy(showMapPicker = true)
        }

        fun hideMapPicker() {
                _uiState.value = _uiState.value.copy(showMapPicker = false)
        }

        fun onLocationSelected(latLng: com.google.android.gms.maps.model.LatLng, address: String) {
                val updatedFormData =
                        _formData.value.copy(
                                latitude = latLng.latitude,
                                longitude = latLng.longitude,
                                locationAddress = address
                        )
                _formData.value = updatedFormData
                hideMapPicker()
        }

        // Clear cached event data when logout occurs
        fun clearEventData() {
                android.util.Log.d("EventViewModel", "Clearing event data")
                _uiState.value = EventUiState()
                _formData.value = EventFormData()
        }
}
