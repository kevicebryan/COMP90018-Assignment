package com.example.mobilecomputingassignment.presentation.viewmodel

// In: app/.../presentation/viewmodel/TeamSelectionViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingassignment.data.repository.TeamRepository
import com.example.mobilecomputingassignment.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamSelectionViewModel @Inject constructor(
    private val teamRepository: TeamRepository,
    private val userRepository: UserRepository // Assuming you have this repository
) : ViewModel() {

    private val _availableTeams = MutableStateFlow<List<String>>(emptyList())
    val availableTeams: StateFlow<List<String>> = _availableTeams

    private val _selectedTeams = MutableStateFlow<Set<String>>(emptySet())
    val selectedTeams: StateFlow<Set<String>> = _selectedTeams

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadAllTeams()
        loadUserSelectedTeams()
    }

    private fun loadAllTeams() {
        viewModelScope.launch {
            _isLoading.value = true
            teamRepository.getAflTeams()
                .onSuccess { teams ->
                    _availableTeams.value = teams.map { it.name }.sorted()
                }
                .onFailure {
                    // Handle error (e.g., set an error state)
                }
            _isLoading.value = false
        }
    }

    private fun loadUserSelectedTeams() {
        viewModelScope.launch {
            // This function needs to exist in your UserRepository
            userRepository.getUserTeams().collect { teams ->
                _selectedTeams.value = teams.toSet()
            }
        }
    }

    fun toggleTeamSelection(teamName: String) {
        val currentSelection = _selectedTeams.value.toMutableSet()
        if (currentSelection.contains(teamName)) {
            currentSelection.remove(teamName)
        } else {
            currentSelection.add(teamName)
        }
        _selectedTeams.value = currentSelection
    }

    suspend fun confirmSelection() {
        // This function needs to exist in your UserRepository
        userRepository.updateUserTeams(_selectedTeams.value.toList())
    }
}