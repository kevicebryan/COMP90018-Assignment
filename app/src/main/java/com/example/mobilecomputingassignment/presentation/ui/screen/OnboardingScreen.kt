package com.example.mobilecomputingassignment.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobilecomputingassignment.presentation.viewmodel.AuthViewModel
import com.example.mobilecomputingassignment.presentation.viewmodel.OnboardingStep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onNavigateToMain: () -> Unit, viewModel: AuthViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val signupData by viewModel.signupData.collectAsState()

    LaunchedEffect(uiState.currentStep) {
        Log.d("OnboardingScreen", "Current step: ${uiState.currentStep}")
        if (uiState.currentStep == OnboardingStep.COMPLETE) {
            Log.d("OnboardingScreen", "Navigating to main app")
            onNavigateToMain()
        }
    }

    // Different layouts for different steps
    when (uiState.currentStep) {
        OnboardingStep.WELCOME -> {
            WelcomeStep(
                    onLoginClick = { viewModel.navigateToStep(OnboardingStep.LOGIN) },
                    onSignupClick = { viewModel.navigateToStep(OnboardingStep.SIGNUP_EMAIL) }
            )
        }
        OnboardingStep.LOGIN -> {
            LoginStep(
                    onLoginClick = { email, password -> viewModel.login(email, password) },
                    onGoogleSignInClick = { idToken -> viewModel.googleSignIn(idToken) },
                    onForgotPasswordClick = { email -> viewModel.sendPasswordResetEmail(email) },
                    onBackClick = { viewModel.navigateToStep(OnboardingStep.WELCOME) },
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage
            )
        }
        OnboardingStep.SIGNUP_EMAIL -> {
            SignupEmailStep(
                    onNextClick = { email -> viewModel.checkEmailAvailability(email) },
                    onBackClick = { viewModel.navigateToStep(OnboardingStep.WELCOME) },
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                    initialEmail = signupData.email,
                    onEmailChange = { viewModel.clearEmailError() }
            )
        }
        OnboardingStep.SIGNUP_PASSWORD -> {
            SignupPasswordStep(
                    onNextClick = { password, confirmPassword ->
                        viewModel.validatePassword(password, confirmPassword)
                    },
                    onBackClick = { viewModel.navigateToStep(OnboardingStep.SIGNUP_EMAIL) },
                    errorMessage = uiState.errorMessage,
                    initialPassword = signupData.password,
                    initialConfirmPassword = signupData.password
            )
        }
        OnboardingStep.SIGNUP_USERNAME_AGE -> {
            SignupUsernameAndAgeStep(
                    onNextClick = { username, birthdate, ageConfirmed ->
                        viewModel.validateUsernameAndAge(username, birthdate, ageConfirmed)
                    },
                    onBackClick = { viewModel.navigateToStep(OnboardingStep.SIGNUP_PASSWORD) },
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                    initialUsername = signupData.username,
                    initialBirthdate = signupData.birthdate,
                    initialAgeConfirmed = signupData.birthdate.isNotEmpty(),
                    onUsernameChange = { viewModel.clearUsernameError() },
                    uiState = uiState
            )
        }
        OnboardingStep.SIGNUP_LEAGUE -> {
            SignupLeagueStep(
                    onNextClick = { leagues -> viewModel.selectLeagues(leagues) },
                    onSkipClick = { viewModel.skipOptionalSteps() },
                    onBackClick = { viewModel.navigateToStep(OnboardingStep.SIGNUP_USERNAME_AGE) }
            )
        }
        OnboardingStep.SIGNUP_TEAM -> {
            SignupTeamStep(
                    onNextClick = { teams -> viewModel.selectTeams(teams) },
                    onSkipClick = { viewModel.skipOptionalSteps() },
                    onBackClick = { viewModel.navigateToStep(OnboardingStep.SIGNUP_LEAGUE) },
                    availableTeams = uiState.availableTeams,
                    isLoadingTeams = uiState.isLoadingTeams,
                    onLoadTeams = { viewModel.loadAflTeams() }
            )
        }
        OnboardingStep.COMPLETE -> {
            // This will trigger the LaunchedEffect above
        }
    }
}

@Composable
fun WelcomeStep(onLoginClick: () -> Unit, onSignupClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        // Main content
        Column(
                modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
            // Onboarding Image
            Image(
                    painter =
                            painterResource(
                                    id = com.example.mobilecomputingassignment.R.drawable.onboarding
                            ),
                    contentDescription = "WatchMates Onboarding",
                    modifier = Modifier.size(280.dp).padding(bottom = 48.dp)
            )

            // App Title with Racing Sans One font from typography theme
            Text(
                    text = "WatchMates",
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
            )

            // Description
            Text(
                    text = "Find sports watch alongs near you!\nConnect with the community",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 48.dp)
            )
        }

        // Bottom section with buttons
        Column(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Primary CTA
            Button(
                    onClick = onSignupClick,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                        text = "Sign up",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 16.sp
                )
            }

            // Secondary CTA
            OutlinedButton(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "Log in", style = MaterialTheme.typography.labelLarge, fontSize = 16.sp)
            }

            // Terms and Privacy
            Text(
                    text = "By continuing, you agree to our Terms of Service and Privacy Policy",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 8.dp)
            )
        }
    }
}
