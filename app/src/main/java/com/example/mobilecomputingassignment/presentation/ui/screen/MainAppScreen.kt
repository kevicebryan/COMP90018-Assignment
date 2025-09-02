package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
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

        val profileViewModel: ProfileViewModel = hiltViewModel()
        val uiState: ProfileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        when {
                showQRCode -> {
                        QRCodeScreen(
                                user = uiState.user,
                                isLoading = uiState.isLoading,
                                onBackClick = { showQRCode = false }
                        )
                }
                showPrivacyPolicy -> {
                        DocumentViewerScreen(
                                title = "Privacy Policy",
                                content = LegalDocuments.PRIVACY_POLICY,
                                onBackClick = { showPrivacyPolicy = false }
                        )
                }
                showTermsConditions -> {
                        DocumentViewerScreen(
                                title = "Terms & Conditions",
                                content = LegalDocuments.TERMS_CONDITIONS,
                                onBackClick = { showTermsConditions = false }
                        )
                }
                else -> {
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