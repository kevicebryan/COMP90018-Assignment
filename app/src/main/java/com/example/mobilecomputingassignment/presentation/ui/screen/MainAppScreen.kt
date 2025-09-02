package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mobilecomputingassignment.data.documents.LegalDocuments
import com.example.mobilecomputingassignment.presentation.ui.component.WatchMatesBottomNavigation
import com.example.mobilecomputingassignment.presentation.viewmodel.AuthViewModel
import com.example.mobilecomputingassignment.presentation.viewmodel.ProfileUiState
import com.example.mobilecomputingassignment.presentation.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
        onLogout: () -> Unit,
        viewModel: AuthViewModel = hiltViewModel()
) {
        var selectedTab by remember { mutableIntStateOf(0) }   // 0=Explore, 1=Events, 2=Check-in, 3=Profile
        var showQRCode by remember { mutableStateOf(false) }
        var showPrivacyPolicy by remember { mutableStateOf(false) }
        var showTermsConditions by remember { mutableStateOf(false) }
        var showTeamSelection by remember { mutableStateOf(false) }

        val signupData by viewModel.signupData.collectAsState()
        val profileViewModel: ProfileViewModel = hiltViewModel()
        val uiState: ProfileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        if (showQRCode) {
                // Pass the SAME user/isLoading used by your Profile UI
                QRCodeScreen(
                        user = uiState.user,
                        isLoading = uiState.isLoading,
                        onBackClick = { showQRCode = false }
                )
        } else if (showPrivacyPolicy) {
                DocumentViewerScreen(
                        title = "Privacy Policy",
                        content = LegalDocuments.PRIVACY_POLICY,
                        onBackClick = { showPrivacyPolicy = false }
                )
        } else if (showTermsConditions) {
                DocumentViewerScreen(
                        title = "Terms & Conditions",
                        content = LegalDocuments.TERMS_CONDITIONS,
                        onBackClick = { showTermsConditions = false }
                )
        } else if (showTeamSelection) {
                // Loading AFL Teams using viewModel
                LaunchedEffect(Unit) {
                        viewModel.loadAflTeams()
                }

                val uiState by viewModel.uiState.collectAsState()
                val signupData by viewModel.signupData.collectAsState()

                TeamSelectionScreen(
                    availableTeams = uiState.availableTeams,
                    initiallySelectedTeamIds = signupData.teams.toSet(),
                    isLoading = uiState.isLoadingTeams,
                    onSaveClick = { updatedSelectedTeamIds ->
                            viewModel.updateUserTeams(updatedSelectedTeamIds.toList())
                            showTeamSelection = false
                    },
                    onBackClick = { showTeamSelection = false}
                )
        } else {
                Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        bottomBar = {
                                WatchMatesBottomNavigation(
                                        selectedTab = selectedTab,
                                        onTabSelected = { selectedTab = it }
                                )
                        }
                ) { innerPadding ->
                        Box(
                                modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                contentAlignment = Alignment.Center
                        ) {
                                when (selectedTab) {
                                        0 -> ProfileScreen(
                                                onLogout = onLogout,
                                                onShowQR = {
                                                        val username = uiState.user?.username.orEmpty()
                                                        val id = uiState.user?.id.orEmpty()
                                                        if (uiState.isLoading) {
                                                                scope.launch { snackbarHostState.showSnackbar("Profile is still loading…") }
                                                        } else if (username.isBlank()) {
                                                                scope.launch { snackbarHostState.showSnackbar("Please set a username in your profile first.") }
                                                        } else if (id.isBlank()) {
                                                                scope.launch { snackbarHostState.showSnackbar("Missing user ID for QR.") }
                                                        } else {
                                                                showQRCode = true
                                                        }
                                                },
                                                onShowPrivacyPolicy = { showPrivacyPolicy = true },
                                                onShowTermsConditions = { showTermsConditions = true },
                                                onShowTeamSelection = { showTeamSelection = true }
                                        )
                                }
                        ) { innerPadding ->
                                Box(
                                        modifier = Modifier
                                                .fillMaxSize()
                                                .padding(innerPadding),
                                        contentAlignment = Alignment.Center
                                ) {
                                        // MUST match the order in WatchMatesBottomNavigation:
                                        // Explore, Events, Check-in, Profile
                                        when (selectedTab) {
                                                0 -> ExploreScreen()
                                                1 -> EventsScreen()
                                                2 -> QRScannerScreen( // Open the scanner for "Check-in"
                                                        onBackClick = { selectedTab = 0 }, // or keep current tab if you prefer
                                                        onResult = { scannedText ->
                                                                // Handle the scanned result (snackbar example)
                                                                scope.launch { snackbarHostState.showSnackbar("Scanned: $scannedText") }
                                                                // Optionally navigate or change tabs here
                                                        }
                                                )
                                                3 -> ProfileScreen(
                                                        onLogout = onLogout,
                                                        onShowQR = {
                                                                val username = uiState.user?.username.orEmpty()
                                                                val id = uiState.user?.id.orEmpty()
                                                                when {
                                                                        uiState.isLoading -> scope.launch {
                                                                                snackbarHostState.showSnackbar("Profile is still loading…")
                                                                        }
                                                                        username.isBlank() -> scope.launch {
                                                                                snackbarHostState.showSnackbar("Please set a username in your profile first.")
                                                                        }
                                                                        id.isBlank() -> scope.launch {
                                                                                snackbarHostState.showSnackbar("Missing user ID for QR.")
                                                                        }
                                                                        else -> showQRCode = true
                                                                }
                                                        },
                                                        onShowPrivacyPolicy = { showPrivacyPolicy = true },
                                                        onShowTermsConditions = { showTermsConditions = true },
                                                        onShowTeamSelection = { showTeamSelection = true }
                                                )
                                        }
                                }
                        }
                }
        }
}