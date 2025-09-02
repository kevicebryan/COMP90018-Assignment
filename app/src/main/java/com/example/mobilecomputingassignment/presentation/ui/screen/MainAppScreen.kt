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
        var selectedTab by remember { mutableIntStateOf(0) }
        var showQRCode by remember { mutableStateOf(false) }
        var showPrivacyPolicy by remember { mutableStateOf(false) }
        var showTermsConditions by remember { mutableStateOf(false) }
        var showTeamSelection by remember { mutableStateOf(false) }

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
                                        1 -> ExploreScreen()
                                        2 -> EventsScreen()
                                        3 -> CheckInScreen()
                                }
                        }
                }
        }
}