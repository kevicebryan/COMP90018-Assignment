package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobilecomputingassignment.presentation.viewmodel.AuthViewModel
import com.example.mobilecomputingassignment.presentation.viewmodel.OnboardingStep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onNavigateToMain: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.currentStep) {
        if (uiState.currentStep == OnboardingStep.COMPLETE) {
            onNavigateToMain()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
                    errorMessage = uiState.errorMessage
                )
            }
            OnboardingStep.SIGNUP_PASSWORD -> {
                SignupPasswordStep(
                    onNextClick = { password, confirmPassword -> viewModel.validatePassword(password, confirmPassword) },
                    onBackClick = { viewModel.navigateToStep(OnboardingStep.SIGNUP_EMAIL) },
                    errorMessage = uiState.errorMessage
                )
            }
            OnboardingStep.SIGNUP_USERNAME -> {
                SignupUsernameStep(
                    onNextClick = { username -> viewModel.checkUsernameAvailability(username) },
                    onBackClick = { viewModel.navigateToStep(OnboardingStep.SIGNUP_PASSWORD) },
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage
                )
            }
            OnboardingStep.SIGNUP_AGE -> {
                SignupAgeStep(
                    onNextClick = { birthdate -> viewModel.validateAge(birthdate) },
                    onBackClick = { viewModel.navigateToStep(OnboardingStep.SIGNUP_USERNAME) },
                    errorMessage = uiState.errorMessage
                )
            }
            OnboardingStep.SIGNUP_LEAGUE -> {
                SignupLeagueStep(
                    onNextClick = { leagues -> viewModel.selectLeagues(leagues) },
                    onSkipClick = { viewModel.skipOptionalSteps() },
                    onBackClick = { viewModel.navigateToStep(OnboardingStep.SIGNUP_AGE) }
                )
            }
            OnboardingStep.SIGNUP_TEAM -> {
                SignupTeamStep(
                    onNextClick = { teams -> viewModel.selectTeams(teams) },
                    onSkipClick = { viewModel.skipOptionalSteps() },
                    onBackClick = { viewModel.navigateToStep(OnboardingStep.SIGNUP_LEAGUE) }
                )
            }
            OnboardingStep.COMPLETE -> {
                // This will trigger the LaunchedEffect above
            }
        }

        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = uiState.errorMessage!!,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}

@Composable
fun WelcomeStep(
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Welcome to WatchMates",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Connect with fellow sports fans and share your passion",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onSignupClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get Started")
        }

        OutlinedButton(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("I already have an account")
        }
    }
}