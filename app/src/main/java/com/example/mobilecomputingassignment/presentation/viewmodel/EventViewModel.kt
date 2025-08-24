package com.example.mobilecomputingassignment.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingassignment.domain.models.*
import com.example.mobilecomputingassignment.domain.usecases.events.*
import com.example.mobilecomputingassignment.data.repository.MatchRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EventUiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val hostedEvents: List<Event> = emptyList(),
        val interestedEvents: List<Event> = emptyList(),
        val allEvents: List<Event> = emptyList(),
        val selectedTab: Int = 0, // 0 = Interested, 1 = Hosted
        val isCreatingEvent: Boolean = false,
        val isEditingEvent: Boolean = false,
        val editingEvent: Event? = null,
        val availableMatches: List<MatchDetails> = emptyList(),
        val isLoadingMatches: Boolean = false
)

data class EventFormData(
        val date: Date = Date(),
        val checkInTime: Date = Date(),
        val matchId: String = "",
        val selectedMatch: MatchDetails? = null,
        val locationName: String = "",
        val locationAddress: String = "",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val capacity: Int = 10,
        val contactNumber: String = "",
        val amenities: EventAmenities = EventAmenities(),
        val accessibility: EventAccessibility = EventAccessibility()
)

@HiltViewModel
class EventViewModel
@Inject
constructor(
        private val createEventUseCase: CreateEventUseCase,
        private val getUserEventsUseCase: GetUserEventsUseCase,
        private val updateEventUseCase: UpdateEventUseCase,
        private val manageEventInterestUseCase: ManageEventInterestUseCase,
        private val matchRepository: MatchRepository
) : ViewModel() {

        private val _uiState = MutableStateFlow(EventUiState())
        val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()

        private val _formData = MutableStateFlow(EventFormData())
        val formData: StateFlow<EventFormData> = _formData.asStateFlow()

        private val currentUser = FirebaseAuth.getInstance().currentUser

        companion object {
                private const val TAG = "EventViewModel"
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
                val username = currentUser?.displayName ?: "Unknown User"
                val form = _formData.value

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
                                                name = form.locationName,
                                                address = form.locationAddress,
                                                latitude = form.latitude,
                                                longitude = form.longitude
                                        ),
                                capacity = form.capacity,
                                contactNumber = form.contactNumber,
                                amenities = form.amenities,
                                accessibility = form.accessibility
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

                val updatedEvent =
                        editingEvent.copy(
                                date = form.date,
                                checkInTime = form.checkInTime,
                                matchId = form.matchId,
                                matchDetails = form.selectedMatch,
                                location =
                                        EventLocation(
                                                name = form.locationName,
                                                address = form.locationAddress,
                                                latitude = form.latitude,
                                                longitude = form.longitude
                                        ),
                                capacity = form.capacity,
                                contactNumber = form.contactNumber,
                                amenities = form.amenities,
                                accessibility = form.accessibility
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
}
