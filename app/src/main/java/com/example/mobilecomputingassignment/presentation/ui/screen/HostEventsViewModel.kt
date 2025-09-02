package com.example.mobilecomputingassignment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingassignment.domain.models.Event
import com.example.mobilecomputingassignment.domain.repository.IEventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HostEventsUiState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class HostEventsViewModel @Inject constructor(
    private val eventRepository: IEventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HostEventsUiState())
    val uiState: StateFlow<HostEventsUiState> = _uiState

    fun loadEventsByHost(hostUserId: String) {
        _uiState.value = HostEventsUiState(isLoading = true)
        viewModelScope.launch {
            val result = eventRepository.getEventsByHost(hostUserId)
            _uiState.value = result.fold(
                onSuccess = { list -> HostEventsUiState(events = list, isLoading = false) },
                onFailure = { e -> HostEventsUiState(isLoading = false, errorMessage = e.message ?: "Failed to load events") }
            )
        }
    }
}
