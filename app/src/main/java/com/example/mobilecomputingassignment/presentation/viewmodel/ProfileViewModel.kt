package com.example.mobilecomputingassignment.presentation.viewmodel

// correct
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingassignment.data.repository.UserRepository // Assuming this is
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
constructor(
        // Ensure UserRepository is the concrete class, not the interface if IUserRepository isn't
        // used here
        private val userRepository: UserRepository,
        private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val currentAuthUser = auth.currentUser // Renamed for clarity within this function
        if (currentAuthUser == null) {
            android.util.Log.d(
                    "ProfileViewModel",
                    "No authenticated user found for loading profile"
            )
            _uiState.value =
                    _uiState.value.copy(
                            user = null, // Ensure user is null if not authenticated
                            isLoading = false,
                            errorMessage = "User not authenticated"
                    )
            return
        }

        android.util.Log.d("ProfileViewModel", "Loading profile for user: ${currentAuthUser.uid}")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val user = userRepository.getUserById(currentAuthUser.uid)
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

    // --- NEW FUNCTION TO UPDATE USER LEAGUES (Option 2) ---
    fun updateUserLeagues(selectedLeagues: List<String>) {
        val currentAuthUser = auth.currentUser
        if (currentAuthUser == null) {
            _uiState.value =
                    _uiState.value.copy(
                            isLoading = false, // Not loading if user isn't authenticated
                            errorMessage = "User not authenticated to update leagues"
                    )
            android.util.Log.w(
                    "ProfileViewModel",
                    "Attempted to update leagues for unauthenticated user."
            )
            return
        }

        // Get the current user state from our uiState
        val currentUserData = _uiState.value.user
        if (currentUserData == null) {
            _uiState.value =
                    _uiState.value.copy(
                            isLoading = false, // Not loading if current user data is missing
                            errorMessage = "Current user data not loaded. Cannot update leagues."
                    )
            android.util.Log.w(
                    "ProfileViewModel",
                    "Current user data in uiState is null. Cannot proceed with league update."
            )
            return
        }

        // Ensure the ID of the user data matches the authenticated user's ID
        if (currentUserData.id != currentAuthUser.uid) {
            _uiState.value =
                    _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "User data mismatch. Please try again."
                    )
            android.util.Log.e(
                    "ProfileViewModel",
                    "Mismatch between authenticated user ID (${currentAuthUser.uid}) and loaded user data ID (${currentUserData.id})."
            )
            // Optionally, trigger a profile reload here if this state is unexpected
            // loadUserProfile()
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Create a new User object with the updated leagues list
            val updatedUserData = currentUserData.copy(leagues = selectedLeagues)

            val result =
                    userRepository.updateUser(updatedUserData) // Use the general updateUser method

            if (result.isSuccess) {
                android.util.Log.d(
                        "ProfileViewModel",
                        "Successfully updated user with new leagues."
                )
                // Update local state directly with the modified user object to reflect changes
                // immediately
                // then reload from source to ensure consistency and get any server-side updates
                // (like updatedAt timestamp)
                _uiState.value = _uiState.value.copy(user = updatedUserData) // Optimistic update
                loadUserProfile() // Reload to ensure data is fresh and synced, also handles
                // isLoading = false
            } else {
                android.util.Log.e(
                        "ProfileViewModel",
                        "Failed to update user with new leagues",
                        result.exceptionOrNull()
                )
                _uiState.value =
                        _uiState.value.copy(
                                isLoading = false,
                                errorMessage = result.exceptionOrNull()?.message
                                                ?: "Failed to update leagues"
                        )
            }
        }
    }
    // --- END OF NEW FUNCTION ---

    fun clearUserData() {
        android.util.Log.d("ProfileViewModel", "Clearing user data")
        _uiState.value = ProfileUiState() // Reset to initial empty state, ensures user is null
    }

    // Function to update user teams
    fun updateUserTeams(selectedTeams: List<String>) {
        val currentAuthUser = auth.currentUser
        if (currentAuthUser == null) {
            _uiState.value =
                    _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "User not authenticated to update teams"
                    )
            android.util.Log.w(
                    "ProfileViewModel",
                    "Attempted to update teams for unauthenticated user."
            )
            return
        }

        // Get the current user state from our uiState
        val currentUserData = _uiState.value.user
        if (currentUserData == null) {
            _uiState.value =
                    _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Current user data not loaded. Cannot update teams."
                    )
            android.util.Log.w(
                    "ProfileViewModel",
                    "Current user data in uiState is null. Cannot proceed with team update."
            )
            return
        }

        // Ensure the ID of the user data matches the authenticated user's ID
        if (currentUserData.id != currentAuthUser.uid) {
            _uiState.value =
                    _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "User data mismatch. Please try again."
                    )
            android.util.Log.e(
                    "ProfileViewModel",
                    "Mismatch between authenticated user ID (${currentAuthUser.uid}) and loaded user data ID (${currentUserData.id})."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // Create a new User object with the updated teams list
            val updatedUserData = currentUserData.copy(teams = selectedTeams)

            val result = userRepository.updateUser(updatedUserData)

            if (result.isSuccess) {
                android.util.Log.d("ProfileViewModel", "Successfully updated user with new teams.")
                // Update local state directly with the modified user object to reflect changes
                // immediately
                _uiState.value = _uiState.value.copy(user = updatedUserData) // Optimistic update
                loadUserProfile() // Reload to ensure data is fresh and synced
            } else {
                android.util.Log.e(
                        "ProfileViewModel",
                        "Failed to update user with new teams",
                        result.exceptionOrNull()
                )
                _uiState.value =
                        _uiState.value.copy(
                                isLoading = false,
                                errorMessage = result.exceptionOrNull()?.message
                                                ?: "Failed to update teams"
                        )
            }
        }
    }
}
