package com.example.mobilecomputingassignment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilecomputingassignment.domain.usecases.auth.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isUsernameValid: Boolean = true,
    val isAgeValid: Boolean = true,
    val currentStep: OnboardingStep = OnboardingStep.WELCOME
)

enum class OnboardingStep {
    WELCOME,
    LOGIN,
    SIGNUP_EMAIL,
    SIGNUP_PASSWORD,
    SIGNUP_USERNAME,
    SIGNUP_AGE,
    SIGNUP_LEAGUE,
    SIGNUP_TEAM,
    COMPLETE
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val checkEmailExistsUseCase: CheckEmailExistsUseCase,
    private val checkUsernameExistsUseCase: CheckUsernameExistsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _signupData = MutableStateFlow(SignupData())
    val signupData: StateFlow<SignupData> = _signupData.asStateFlow()

    fun login(email: String, password: String) {
        if (!isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid email", isEmailValid = false)
            return
        }

        if (password.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password cannot be empty", isPasswordValid = false)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            loginUseCase.execute(email, password)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentStep = OnboardingStep.COMPLETE
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Login failed"
                    )
                }
        }
    }

    fun googleSignIn(idToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            googleSignInUseCase.execute(idToken)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentStep = OnboardingStep.COMPLETE
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Google sign in failed"
                    )
                }
        }
    }

    fun checkEmailAvailability(email: String) {
        if (!isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid email", isEmailValid = false)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val emailExists = checkEmailExistsUseCase.execute(email)
            if (emailExists) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Email already exists. Please use a different email or login.",
                    isEmailValid = false
                )
            } else {
                _signupData.value = _signupData.value.copy(email = email)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentStep = OnboardingStep.SIGNUP_PASSWORD,
                    isEmailValid = true
                )
            }
        }
    }

    fun validatePassword(password: String, confirmPassword: String) {
        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password must be at least 6 characters", isPasswordValid = false)
            return
        }

        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(errorMessage = "Passwords do not match", isPasswordValid = false)
            return
        }

        _signupData.value = _signupData.value.copy(password = password)
        _uiState.value = _uiState.value.copy(
            currentStep = OnboardingStep.SIGNUP_USERNAME,
            isPasswordValid = true,
            errorMessage = null
        )
    }

    fun checkUsernameAvailability(username: String) {
        if (username.length < 3) {
            _uiState.value = _uiState.value.copy(errorMessage = "Username must be at least 3 characters", isUsernameValid = false)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val usernameExists = checkUsernameExistsUseCase.execute(username)
            if (usernameExists) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Username already taken. Please choose a different one.",
                    isUsernameValid = false
                )
            } else {
                _signupData.value = _signupData.value.copy(username = username)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentStep = OnboardingStep.SIGNUP_AGE,
                    isUsernameValid = true
                )
            }
        }
    }

    fun validateAge(birthdateString: String) {
        if (birthdateString.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your birthdate", isAgeValid = false)
            return
        }

        try {
            val parts = birthdateString.split("/")
            if (parts.size != 3) {
                _uiState.value = _uiState.value.copy(errorMessage = "Please use DD/MM/YYYY format", isAgeValid = false)
                return
            }

            val day = parts[0].toInt()
            val month = parts[1].toInt()
            val year = parts[2].toInt()

            if (day !in 1..31 || month !in 1..12 || year < 1900 || year > 2023) {
                _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid date", isAgeValid = false)
                return
            }

            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            val age = currentYear - year

            if (age < 18) {
                _uiState.value = _uiState.value.copy(errorMessage = "You must be 18 or older to use this app", isAgeValid = false)
                return
            }

            _signupData.value = _signupData.value.copy(birthdate = birthdateString)
            _uiState.value = _uiState.value.copy(
                currentStep = OnboardingStep.SIGNUP_LEAGUE,
                isAgeValid = true,
                errorMessage = null
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid date", isAgeValid = false)
        }
    }

    fun selectLeagues(leagues: List<String>) {
        _signupData.value = _signupData.value.copy(leagues = leagues)
        _uiState.value = _uiState.value.copy(currentStep = OnboardingStep.SIGNUP_TEAM)
    }

    fun selectTeams(teams: List<String>) {
        _signupData.value = _signupData.value.copy(teams = teams)
        completeSignup()
    }

    fun skipOptionalSteps() {
        completeSignup()
    }

    private fun completeSignup() {
        val data = _signupData.value
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            registerUseCase.execute(
                email = data.email,
                password = data.password,
                username = data.username,
                birthdateString = data.birthdate,
                leagues = data.leagues,
                teams = data.teams
            )
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentStep = OnboardingStep.COMPLETE
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Registration failed"
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