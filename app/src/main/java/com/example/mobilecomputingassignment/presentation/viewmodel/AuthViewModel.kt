package com.example.mobilecomputingassignment.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingassignment.core.utils.ContentFilter
import com.example.mobilecomputingassignment.data.repository.TeamRepository
import com.example.mobilecomputingassignment.domain.models.Team
import com.example.mobilecomputingassignment.domain.usecases.auth.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val isEmailValid: Boolean = true,
        val isPasswordValid: Boolean = true,
        val isUsernameValid: Boolean = true,
        val isAgeValid: Boolean = true,
        val currentStep: OnboardingStep = OnboardingStep.WELCOME,
        val availableTeams: List<Team> = emptyList(),
        val isLoadingTeams: Boolean = false
)

enum class OnboardingStep {
        WELCOME,
        LOGIN,
        SIGNUP_EMAIL,
        SIGNUP_PASSWORD,
        SIGNUP_USERNAME_AGE,
        SIGNUP_LEAGUE,
        SIGNUP_TEAM,
        COMPLETE
}

@HiltViewModel
class AuthViewModel
@Inject
constructor(
        private val loginUseCase: LoginUseCase,
        private val registerUseCase: RegisterUseCase,
        private val googleSignInUseCase: GoogleSignInUseCase,
        private val checkEmailExistsUseCase: CheckEmailExistsUseCase,
        private val checkUsernameExistsUseCase: CheckUsernameExistsUseCase,
        private val teamRepository: TeamRepository
) : ViewModel() {

        private val _uiState = MutableStateFlow(AuthUiState())
        val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

        private val _signupData = MutableStateFlow(SignupData())
        val signupData: StateFlow<SignupData> = _signupData.asStateFlow()

        fun login(email: String, password: String) {
                if (!isValidEmail(email)) {
                        _uiState.value =
                                _uiState.value.copy(
                                        errorMessage = "Please enter a valid email",
                                        isEmailValid = false
                                )
                        return
                }

                if (password.isEmpty()) {
                        _uiState.value =
                                _uiState.value.copy(
                                        errorMessage = "Password cannot be empty",
                                        isPasswordValid = false
                                )
                        return
                }

                viewModelScope.launch {
                        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                        loginUseCase
                                .execute(email, password)
                                .onSuccess {
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoading = false,
                                                        currentStep = OnboardingStep.COMPLETE
                                                )
                                }
                                .onFailure { exception ->
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoading = false,
                                                        errorMessage = exception.message
                                                                        ?: "Login failed"
                                                )
                                }
                }
        }

        fun sendPasswordResetEmail(email: String) {
                if (!isValidEmail(email)) {
                        _uiState.value =
                                _uiState.value.copy(
                                        errorMessage = "Please enter a valid email address",
                                        isEmailValid = false
                                )
                        return
                }

                viewModelScope.launch {
                        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                        try {
                                com.google.firebase.auth.FirebaseAuth.getInstance()
                                        .sendPasswordResetEmail(email)
                                        .addOnCompleteListener { task ->
                                                _uiState.value =
                                                        _uiState.value.copy(isLoading = false)
                                                if (task.isSuccessful) {
                                                        _uiState.value =
                                                                _uiState.value.copy(
                                                                        errorMessage =
                                                                                "Password reset email sent! Check your inbox."
                                                                )
                                                } else {
                                                        _uiState.value =
                                                                _uiState.value.copy(
                                                                        errorMessage =
                                                                                task.exception
                                                                                        ?.message
                                                                                        ?: "Failed to send reset email"
                                                                )
                                                }
                                        }
                        } catch (e: Exception) {
                                _uiState.value =
                                        _uiState.value.copy(
                                                isLoading = false,
                                                errorMessage = e.message
                                                                ?: "Failed to send reset email"
                                        )
                        }
                }
        }

        fun googleSignIn(idToken: String) {
                viewModelScope.launch {
                        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                        googleSignInUseCase
                                .execute(idToken)
                                .onSuccess {
                                        // Get the current Firebase user to check if they need
                                        // onboarding
                                        val firebaseUser =
                                                com.google.firebase.auth.FirebaseAuth.getInstance()
                                                        .currentUser
                                        if (firebaseUser != null) {
                                                // For now, assume Google users need to complete
                                                // onboarding
                                                // In a real app, you'd check their Firestore
                                                // profile
                                                _signupData.value =
                                                        _signupData.value.copy(
                                                                email = firebaseUser.email ?: ""
                                                        )
                                                _uiState.value =
                                                        _uiState.value.copy(
                                                                isLoading = false,
                                                                currentStep =
                                                                        OnboardingStep
                                                                                .SIGNUP_USERNAME_AGE
                                                        )
                                        } else {
                                                _uiState.value =
                                                        _uiState.value.copy(
                                                                isLoading = false,
                                                                currentStep =
                                                                        OnboardingStep.COMPLETE
                                                        )
                                        }
                                }
                                .onFailure { exception ->
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoading = false,
                                                        errorMessage = exception.message
                                                                        ?: "Google sign in failed"
                                                )
                                }
                }
        }

        fun checkEmailAvailability(email: String) {
                Log.d("AuthViewModel", "=== EMAIL AVAILABILITY CHECK START ===")
                Log.d("AuthViewModel", "Input email: '$email'")
                Log.d("AuthViewModel", "Current UI state: ${_uiState.value}")

                if (!isValidEmail(email)) {
                        Log.d("AuthViewModel", "Email format is invalid")
                        _uiState.value =
                                _uiState.value.copy(
                                        errorMessage = "Please enter a valid email",
                                        isEmailValid = false
                                )
                        return
                }

                Log.d("AuthViewModel", "Email format is valid, checking availability...")
                viewModelScope.launch {
                        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                        Log.d("AuthViewModel", "Set loading state to true")

                        val emailExists = checkEmailExistsUseCase.execute(email)

                        Log.d("AuthViewModel", "=== FINAL RESULT ===")
                        Log.d("AuthViewModel", "Email exists check result: $emailExists")

                        if (emailExists) {
                                Log.d("AuthViewModel", "Email already exists, BLOCKING progress")
                                _uiState.value =
                                        _uiState.value.copy(
                                                isLoading = false,
                                                errorMessage =
                                                        "A user with this email already exists. Please try another email or login with this email.",
                                                isEmailValid = false
                                        )
                                Log.d(
                                        "AuthViewModel",
                                        "Updated UI state with error: ${_uiState.value}"
                                )
                        } else {
                                Log.d(
                                        "AuthViewModel",
                                        "Email available, ALLOWING progress to password step"
                                )
                                _signupData.value = _signupData.value.copy(email = email)
                                _uiState.value =
                                        _uiState.value.copy(
                                                isLoading = false,
                                                currentStep = OnboardingStep.SIGNUP_PASSWORD,
                                                isEmailValid = true
                                        )
                                Log.d(
                                        "AuthViewModel",
                                        "Updated UI state for next step: ${_uiState.value}"
                                )
                        }
                        Log.d("AuthViewModel", "=== EMAIL AVAILABILITY CHECK END ===")
                }
        }

        fun validatePassword(password: String, confirmPassword: String) {
                // Clear any previous error
                _uiState.value = _uiState.value.copy(errorMessage = null)

                // Check password strength (7+ chars, 1 capital, 1 number)
                if (password.length < 7) {
                        _uiState.value =
                                _uiState.value.copy(
                                        errorMessage = "Password must be at least 7 characters",
                                        isPasswordValid = false
                                )
                        return
                }

                if (!password.any { it.isUpperCase() }) {
                        _uiState.value =
                                _uiState.value.copy(
                                        errorMessage =
                                                "Password must contain at least one uppercase letter",
                                        isPasswordValid = false
                                )
                        return
                }

                if (!password.any { it.isDigit() }) {
                        _uiState.value =
                                _uiState.value.copy(
                                        errorMessage = "Password must contain at least one number",
                                        isPasswordValid = false
                                )
                        return
                }

                if (password != confirmPassword) {
                        _uiState.value =
                                _uiState.value.copy(
                                        errorMessage = "Passwords do not match",
                                        isPasswordValid = false
                                )
                        return
                }

                _signupData.value = _signupData.value.copy(password = password)
                _uiState.value =
                        _uiState.value.copy(
                                currentStep = OnboardingStep.SIGNUP_USERNAME_AGE,
                                isPasswordValid = true,
                                errorMessage = null
                        )
        }

        fun validateUsernameAndAge(
                username: String,
                birthdateString: String,
                ageConfirmed: Boolean
        ) {
                // Validate username for inappropriate content
                val usernameValidation = ContentFilter.validateContent(username)
                if (!usernameValidation.isValid) {
                        _uiState.value =
                                _uiState.value.copy(
                                        errorMessage =
                                                "Username: ${usernameValidation.errorMessage}",
                                        isUsernameValid = false
                                )
                        return
                }

                // Validate username length
                if (username.length < 3) {
                        _uiState.value =
                                _uiState.value.copy(
                                        errorMessage = "Username must be at least 3 characters",
                                        isUsernameValid = false
                                )
                        return
                }

                // Validate birthdate
                if (birthdateString.isEmpty()) {
                        _uiState.value =
                                _uiState.value.copy(
                                        errorMessage = "Please enter your birthdate",
                                        isAgeValid = false
                                )
                        return
                }

                // Validate age confirmation
                if (!ageConfirmed) {
                        _uiState.value =
                                _uiState.value.copy(
                                        errorMessage = "You must confirm that you are 18 or older",
                                        isAgeValid = false
                                )
                        return
                }

                // Validate birthdate format and age
                try {
                        val parts = birthdateString.split("/")
                        if (parts.size != 3) {
                                _uiState.value =
                                        _uiState.value.copy(
                                                errorMessage = "Please use DD/MM/YYYY format",
                                                isAgeValid = false
                                        )
                                return
                        }

                        val day = parts[0].toInt()
                        val month = parts[1].toInt()
                        val year = parts[2].toInt()

                        if (day !in 1..31 || month !in 1..12 || year < 1900 || year > 2023) {
                                _uiState.value =
                                        _uiState.value.copy(
                                                errorMessage = "Please enter a valid date",
                                                isAgeValid = false
                                        )
                                return
                        }

                        val currentYear =
                                java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                        val age = currentYear - year

                        if (age < 18) {
                                _uiState.value =
                                        _uiState.value.copy(
                                                errorMessage =
                                                        "You must be 18 or older to use this app",
                                                isAgeValid = false
                                        )
                                return
                        }
                } catch (e: Exception) {
                        _uiState.value =
                                _uiState.value.copy(
                                        errorMessage = "Please enter a valid date",
                                        isAgeValid = false
                                )
                        return
                }

                // Check username availability
                viewModelScope.launch {
                        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                        Log.d("AuthViewModel", "Checking username availability: $username")

                        val usernameExists = checkUsernameExistsUseCase.execute(username)

                        Log.d("AuthViewModel", "Username exists check result: $usernameExists")
                        if (usernameExists) {
                                Log.d("AuthViewModel", "Username already exists, blocking progress")
                                _uiState.value =
                                        _uiState.value.copy(
                                                isLoading = false,
                                                errorMessage =
                                                        "A user with this username already exists. Please choose a different username.",
                                                isUsernameValid = false
                                        )
                        } else {
                                Log.d(
                                        "AuthViewModel",
                                        "Username available, proceeding to league step"
                                )
                                _signupData.value =
                                        _signupData.value.copy(
                                                username = username,
                                                birthdate = birthdateString
                                        )
                                _uiState.value =
                                        _uiState.value.copy(
                                                isLoading = false,
                                                currentStep = OnboardingStep.SIGNUP_LEAGUE,
                                                isUsernameValid = true,
                                                isAgeValid = true
                                        )
                        }
                }
        }

        fun selectLeagues(leagues: List<String>) {
                _signupData.value = _signupData.value.copy(leagues = leagues)
                _uiState.value = _uiState.value.copy(currentStep = OnboardingStep.SIGNUP_TEAM)
        }

        fun selectTeams(teams: List<String>) {
                Log.d("AuthViewModel", "selectTeams called with: $teams")
                _signupData.value = _signupData.value.copy(teams = teams)
                completeSignup()
        }

        fun skipOptionalSteps() {
                Log.d("AuthViewModel", "skipOptionalSteps called")
                completeSignup()
        }

        fun loadAflTeams() {
                Log.d("AuthViewModel", "loadAflTeams called")
                viewModelScope.launch {
                        _uiState.value = _uiState.value.copy(isLoadingTeams = true)

                        teamRepository
                                .getAflTeams()
                                .onSuccess { teams ->
                                        Log.d(
                                                "AuthViewModel",
                                                "Teams loaded successfully: ${teams.size} teams"
                                        )
                                        teams.forEach { team ->
                                                Log.d(
                                                        "AuthViewModel",
                                                        "Team: ${team.name}, Logo: ${team.logoUrl}"
                                                )
                                        }
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        availableTeams = teams,
                                                        isLoadingTeams = false
                                                )
                                }
                                .onFailure { exception ->
                                        Log.e("AuthViewModel", "Failed to load teams", exception)
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoadingTeams = false,
                                                        errorMessage =
                                                                "Failed to load teams: ${exception.message}"
                                                )
                                }
                }
        }

        private fun completeSignup() {
                Log.d("AuthViewModel", "completeSignup called")
                val data = _signupData.value
                Log.d("AuthViewModel", "Signup data: $data")
                viewModelScope.launch {
                        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                        registerUseCase
                                .execute(
                                        email = data.email,
                                        password = data.password,
                                        username = ContentFilter.sanitizeInput(data.username),
                                        birthdateString = data.birthdate,
                                        leagues = data.leagues,
                                        teams = data.teams
                                )
                                .onSuccess {
                                        Log.d(
                                                "AuthViewModel",
                                                "Registration successful, setting COMPLETE step"
                                        )
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoading = false,
                                                        currentStep = OnboardingStep.COMPLETE
                                                )
                                }
                                .onFailure { exception ->
                                        Log.e("AuthViewModel", "Registration failed", exception)
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoading = false,
                                                        errorMessage = exception.message
                                                                        ?: "Registration failed"
                                                )
                                }
                }
        }

        fun navigateToStep(step: OnboardingStep) {
                _uiState.value = _uiState.value.copy(currentStep = step, errorMessage = null)
        }

        fun clearError() {
                _uiState.value = _uiState.value.copy(errorMessage = null)
        }

        fun clearEmailError() {
                _uiState.value = _uiState.value.copy(errorMessage = null, isEmailValid = true)
        }

        fun clearUsernameError() {
                _uiState.value = _uiState.value.copy(errorMessage = null, isUsernameValid = true)
        }

        private fun isValidEmail(email: String): Boolean {
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
}

data class SignupData(
        val email: String = "",
        val password: String = "",
        val username: String = "",
        val birthdate: String = "",
        val leagues: List<String> = emptyList(),
        val teams: List<String> = emptyList()
)
