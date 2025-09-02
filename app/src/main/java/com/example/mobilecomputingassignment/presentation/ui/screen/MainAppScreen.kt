package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
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
        // Bottom bar order in your BottomNavigation:
        // 0=Explore, 1=Events, 2=Check-in, 3=Profile
        var selectedTab by remember { mutableIntStateOf(0) }
        var showQRCode by remember { mutableStateOf(false) }
        var showPrivacyPolicy by remember { mutableStateOf(false) }
        var showTermsConditions by remember { mutableStateOf(false) }
        var showTeamSelection by remember { mutableStateOf(false) }

        // Auth VM data you already use elsewhere
        val signupData by viewModel.signupData.collectAsState()

        // Profile VM state (used by ProfileScreen & QRCodeScreen)
        val profileViewModel: ProfileViewModel = hiltViewModel()
        val uiState: ProfileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        // Full-screen branches first
        when {
                showQRCode -> {
                        QRCodeScreen(
                                user = uiState.user,
                                isLoading = uiState.isLoading,
                                onBackClick = { showQRCode = false }
                        )
                        return
                }
                showPrivacyPolicy -> {
                        DocumentViewerScreen(
                                title = "Privacy Policy",
                                content = LegalDocuments.PRIVACY_POLICY,
                                onBackClick = { showPrivacyPolicy = false }
                        )
                        return
                }
                showTermsConditions -> {
                        DocumentViewerScreen(
                                title = "Terms & Conditions",
                                content = LegalDocuments.TERMS_CONDITIONS,
                                onBackClick = { showTermsConditions = false }
                        )
                        return
                }
                showTeamSelection -> {
                        // Load teams when this screen becomes visible
                        LaunchedEffect(Unit) { viewModel.loadAflTeams() }

                        // Collect ONLY here to avoid shadowing the profile uiState
                        val authUiState by viewModel.uiState.collectAsState()

                        TeamSelectionScreen(
                                availableTeams = authUiState.availableTeams,
                                initiallySelectedTeamIds = signupData.teams.toSet(),
                                isLoading = authUiState.isLoadingTeams,
                                onSaveClick = { updatedSelectedTeamIds ->
                                        viewModel.updateUserTeams(updatedSelectedTeamIds.toList())
                                        showTeamSelection = false
                                },
                                onBackClick = { showTeamSelection = false }
                        )
                        return
                }
        }

        // Normal “tabbed” shell
        Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                        WatchMatesBottomNavigation(
                                selectedTab = selectedTab,
                                onTabSelected = { index -> selectedTab = index }
                        )
                }
        ) { innerPadding ->
                Box(
                        modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                        contentAlignment = Alignment.Center
                ) {
                        // MUST match the order defined in WatchMatesBottomNavigation:
                        // Explore, Events, Check-in, Profile
                        when (selectedTab) {
                                0 -> ExploreScreen()
                                1 -> EventsScreen()
                                2 -> QRScannerScreen(
                                        onBackClick = { selectedTab = 0 }, // or keep 2 to remain on scanner
                                        onResult = { scannedText ->
                                                scope.launch { snackbarHostState.showSnackbar("Scanned: $scannedText") }
                                                // TODO: handle check-in logic/navigation with scannedText
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