package com.example.mobilecomputingassignment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingassignment.data.repository.UserRepository
import com.example.mobilecomputingassignment.domain.models.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
        val user: User? = null,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
)

@HiltViewModel
class ProfileViewModel
@Inject
constructor(private val userRepository: UserRepository, private val auth: FirebaseAuth) :
        ViewModel() {

  private val _uiState = MutableStateFlow(ProfileUiState())
  val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

  init {
    loadUserProfile()
  }

  fun loadUserProfile() {
    val currentUser = auth.currentUser
    if (currentUser == null) {
      android.util.Log.d("ProfileViewModel", "No authenticated user found")
      _uiState.value =
              _uiState.value.copy(isLoading = false, errorMessage = "User not authenticated")
      return
    }

    android.util.Log.d("ProfileViewModel", "Loading profile for user: ${currentUser.uid}")
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

      try {
        val user = userRepository.getUserById(currentUser.uid)
        android.util.Log.d("ProfileViewModel", "Loaded user profile: ${user?.username}")
        _uiState.value =
                _uiState.value.copy(
                        user = user,
                        isLoading = false,
                        errorMessage = if (user == null) "User profile not found" else null
                )
      } catch (e: Exception) {
        android.util.Log.e("ProfileViewModel", "Failed to load profile", e)
        _uiState.value =
                _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load profile"
                )
      }
    }
  }

  fun refreshProfile() {
    loadUserProfile()
  }

  // Clear cached user data when logout occurs
  fun clearUserData() {
    android.util.Log.d("ProfileViewModel", "Clearing user data")
    _uiState.value = ProfileUiState()
  }
}
