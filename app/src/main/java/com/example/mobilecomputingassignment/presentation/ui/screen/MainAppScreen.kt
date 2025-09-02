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
import com.example.mobilecomputingassignment.presentation.viewmodel.CheckInViewModel
import com.example.mobilecomputingassignment.presentation.viewmodel.PointsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
        onLogout: () -> Unit,
        viewModel: AuthViewModel = hiltViewModel()
) {
        // BottomNav order: 0=Explore, 1=Events, 2=Check-in, 3=Profile
        var selectedTab by remember { mutableIntStateOf(0) }
        var showQRCode by remember { mutableStateOf(false) }
        var showScanner by remember { mutableStateOf(false) }
        var showPrivacyPolicy by remember { mutableStateOf(false) }
        var showTermsConditions by remember { mutableStateOf(false) }
        var showTeamSelection by remember { mutableStateOf(false) }

        // check-in flow state
        var scannedHostId by remember { mutableStateOf<String?>(null) }
        var showHostEvents by remember { mutableStateOf(false) }
        var selectedEventId by remember { mutableStateOf<String?>(null) }
        var showCheckInComplete by remember { mutableStateOf(false) }

        // points flow state
        var showPointsEarned by remember { mutableStateOf(false) }
        var earnedPoints by remember { mutableStateOf<Int?>(null) }
        var alreadyCheckedDialog by remember { mutableStateOf(false) }

        val signupData by viewModel.signupData.collectAsState()
        val profileViewModel: ProfileViewModel = hiltViewModel()
        val uiState: ProfileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()

        val checkInViewModel: CheckInViewModel = hiltViewModel()
        val pointsViewModel: PointsViewModel = hiltViewModel()

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        // ----- Full-screen branches FIRST -----
        when {
                // Scanner
                showScanner -> {
                        QRScannerScreen(
                                onBackClick = { showScanner = false },
                                onResult = { qrText ->
                                        val hostId = qrText.trim()
                                        if (hostId.isBlank()) {
                                                scope.launch { snackbarHostState.showSnackbar("Invalid QR code") }
                                                showScanner = false
                                        } else {
                                                scannedHostId = hostId
                                                showScanner = false
                                                showHostEvents = true
                                        }
                                }
                        )
                        return
                }

                // Select event (hostId from QR)
                showHostEvents && scannedHostId != null -> {
                        HostEventsScreen(
                                hostId = scannedHostId!!,
                                onBackClick = { showHostEvents = false },
                                onSelectEvent = { eventId ->
                                        scope.launch {
                                                // 1) PRE-CHECK
                                                val already = checkInViewModel.hasAlreadyCheckedIn(eventId).getOrElse {
                                                        // If the check failed, treat as not already checked to avoid blocking
                                                        false
                                                }
                                                if (already) {
                                                        alreadyCheckedDialog = true
                                                        return@launch
                                                }

                                                // 2) PROCEED WITH CHECK-IN
                                                val res = checkInViewModel.checkInToEvent(eventId)
                                                if (res.isSuccess) {
                                                        selectedEventId = eventId
                                                        showHostEvents = false
                                                        showCheckInComplete = true  // will later allow “Reveal points”
                                                } else {
                                                        snackbarHostState.showSnackbar(res.exceptionOrNull()?.message ?: "Check-in failed")
                                                }
                                        }
                                }
                        )
                        if (alreadyCheckedDialog) {
                                AlertDialog(
                                        onDismissRequest = { alreadyCheckedDialog = false },
                                        confirmButton = {
                                                TextButton(onClick = { alreadyCheckedDialog = false }) {
                                                        Text("OK")
                                                }
                                        },
                                        title = { Text("Already checked in") },
                                        text  = { Text("You've already checked in to this event. You won't receive points for checking in twice.") }
                                )
                        }
                        return
                }

                // Check-in complete → reveal points
                showCheckInComplete && selectedEventId != null -> {
                        CheckInCompleteScreen(
                                onBackClick = { showCheckInComplete = false },
                                eventId = selectedEventId!!,
                                onRevealPointsClick = {
                                        val earned = (10..50).random()
                                        scope.launch {
                                                val res = pointsViewModel.awardPoints(earned, currentPointsHint = uiState.user?.points)
                                                if (res.isSuccess) {
                                                        earnedPoints = earned
                                                        // refresh profile so the UI picks up new total elsewhere
                                                        profileViewModel.refreshProfile()
                                                        showCheckInComplete = false
                                                        showPointsEarned = true
                                                } else {
                                                        snackbarHostState.showSnackbar(res.exceptionOrNull()?.message ?: "Failed to update points")
                                                }
                                        }
                                }
                        )
                        return
                }

                // Points earned screen
                showPointsEarned && earnedPoints != null -> {
                        PointsEarnedScreen(
                                points = earnedPoints!!,
                                onBackClick = {
                                        showPointsEarned = false
                                },
                                // ✅ ADDED: let the screen navigate straight to Profile if you added a "See Points" button there
                                onSeePoints = {
                                        showPointsEarned = false
                                        selectedTab = 3 // Profile tab
                                }
                        )
                        return
                }

                // Existing branches
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
                        LaunchedEffect(Unit) { viewModel.loadAflTeams() }
                        val authUiState by viewModel.uiState.collectAsState()
                        TeamSelectionScreen(
                                availableTeams = authUiState.availableTeams,
                                initiallySelectedTeamIds = signupData.teams.toSet(),
                                isLoading = authUiState.isLoadingTeams,
                                onSaveClick = { ids ->
                                        viewModel.updateUserTeams(ids.toList())
                                        showTeamSelection = false
                                },
                                onBackClick = { showTeamSelection = false }
                        )
                        return
                }
        }

        // ----- Normal tab shell -----
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
                        when (selectedTab) {
                                0 -> ExploreScreen()
                                1 -> EventsScreen()
                                2 -> CheckInLanding(
                                        onBackClick = null,
                                        onTapScan = { showScanner = true }
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
