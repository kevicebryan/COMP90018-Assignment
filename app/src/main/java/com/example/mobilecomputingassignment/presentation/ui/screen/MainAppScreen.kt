package com.example.mobilecomputingassignment.presentation.ui.screen

// import androidx.compose.runtime.getValue // Redundant if other specific imports are used
// import androidx.compose.runtime.mutableIntStateOf // Redundant if other specific imports are used
// import androidx.compose.runtime.setValue // Redundant if other specific imports are used
// Import your ViewModels and SignupLeagueStep
// Make sure this path is correct for your SignupLeagueStep composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mobilecomputingassignment.data.documents.LegalDocuments
import com.example.mobilecomputingassignment.presentation.ui.component.WatchMatesBottomNavigation
import com.example.mobilecomputingassignment.presentation.viewmodel.AuthViewModel
import com.example.mobilecomputingassignment.presentation.viewmodel.CheckInViewModel
import com.example.mobilecomputingassignment.presentation.viewmodel.PointsViewModel
import com.example.mobilecomputingassignment.presentation.viewmodel.ProfileUiState
import com.example.mobilecomputingassignment.presentation.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
        onLogout: () -> Unit,
        viewModel: AuthViewModel = hiltViewModel() // This is your AuthViewModel
) {
        // BottomNav order: 0=Explore, 1=Events, 2=Check-in, 3=Profile
        var selectedTab by remember { mutableIntStateOf(0) }
        var showQRCode by remember { mutableStateOf(false) }
        var showScanner by remember { mutableStateOf(false) }
        var showPrivacyPolicy by remember { mutableStateOf(false) }
        var showTermsConditions by remember { mutableStateOf(false) }
        var showTeamSelection by remember { mutableStateOf(false) }
        var showLeagueSelection by remember { mutableStateOf(false) } // <-- NEW STATE VARIABLE

        // check-in flow state
        var scannedHostId by remember { mutableStateOf<String?>(null) }
        var showHostEvents by remember { mutableStateOf(false) }
        var selectedEventId by remember { mutableStateOf<String?>(null) }
        var showCheckInComplete by remember { mutableStateOf(false) }

        // points flow state
        var showPointsEarned by remember { mutableStateOf(false) }
        var earnedPoints by remember { mutableStateOf<Int?>(null) }
        var alreadyCheckedDialog by remember { mutableStateOf(false) }

        val signupData by viewModel.signupData.collectAsState() // From AuthViewModel
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
                                                scope.launch {
                                                        snackbarHostState.showSnackbar(
                                                                "Invalid QR code"
                                                        )
                                                }
                                                showScanner = false
                                        } else {
                                                scannedHostId = hostId
                                                showScanner = false
                                                showHostEvents = true
                                        }
                                }
                        )
                        return // Use 'return' for full-screen overlays to stop further composition
                        // of this branch
                }

                // Select event (hostId from QR)
                showHostEvents && scannedHostId != null -> {
                        HostEventsScreen(
                                hostId = scannedHostId!!,
                                onBackClick = { showHostEvents = false },
                                onSelectEvent = { eventId ->
                                        scope.launch {
                                                val already =
                                                        checkInViewModel.hasAlreadyCheckedIn(
                                                                        eventId
                                                                )
                                                                .getOrElse { false }
                                                if (already) {
                                                        alreadyCheckedDialog = true
                                                        return@launch
                                                }
                                                val res = checkInViewModel.checkInToEvent(eventId)
                                                if (res.isSuccess) {
                                                        selectedEventId = eventId
                                                        showHostEvents = false
                                                        showCheckInComplete = true
                                                } else {
                                                        snackbarHostState.showSnackbar(
                                                                res.exceptionOrNull()?.message
                                                                        ?: "Check-in failed"
                                                        )
                                                }
                                        }
                                }
                        )
                        if (alreadyCheckedDialog) {
                                AlertDialog(
                                        onDismissRequest = { alreadyCheckedDialog = false },
                                        confirmButton = {
                                                TextButton(
                                                        onClick = { alreadyCheckedDialog = false }
                                                ) { Text("OK") }
                                        },
                                        title = { Text("Already checked in") },
                                        text = {
                                                Text(
                                                        "You've already checked in to this event. You won't receive points for checking in twice."
                                                )
                                        }
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
                                                val res =
                                                        pointsViewModel.awardPoints(
                                                                earned,
                                                                currentPointsHint =
                                                                        uiState.user?.points
                                                        ) // Assuming points is Long, cast if needed
                                                if (res.isSuccess) {
                                                        earnedPoints = earned
                                                        profileViewModel.refreshProfile()
                                                        showCheckInComplete = false
                                                        showPointsEarned = true
                                                } else {
                                                        snackbarHostState.showSnackbar(
                                                                res.exceptionOrNull()?.message
                                                                        ?: "Failed to update points"
                                                        )
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
                                onBackClick = { showPointsEarned = false },
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
                        LaunchedEffect(Unit) { viewModel.loadAflTeams() } // AuthViewModel
                        val authUiState by viewModel.uiState.collectAsState()
                        TeamSelectionScreen(
                                availableTeams = authUiState.availableTeams,
                                // Assuming signupData.teams from AuthViewModel holds current user's
                                // teams
                                // If not, you might need to fetch from ProfileViewModel's
                                // uiState.user?.teams
                                initiallySelectedTeamNames = (uiState.user?.teams?.toSet()
                                                ?: signupData.teams.toSet()),
                                isLoading = authUiState.isLoadingTeams,
                                onSaveClick = { teamNames ->
                                        // Use ProfileViewModel to update teams
                                        profileViewModel.updateUserTeams(teamNames.toList())
                                        showTeamSelection = false
                                },
                                onBackClick = { showTeamSelection = false }
                        )
                        return
                }

                // --- VVVVV NEW BRANCH FOR LEAGUE SELECTION VVVVV ---
                showLeagueSelection -> {
                        // Get the current user's leagues from ProfileViewModel's uiState
                        val currentUserLeagues = uiState.user?.leagues?.toSet() ?: emptySet()

                        SignupLeagueStep(
                                initialSelectedLeagues = currentUserLeagues,
                                isEditingMode = true,
                                onSaveLeagues = { updatedLeagues ->
                                        profileViewModel.updateUserLeagues(
                                                updatedLeagues
                                        ) // Call ProfileViewModel to save
                                        showLeagueSelection = false // Close the screen
                                },
                                // These are for the original signature of SignupLeagueStep, adapt
                                // behavior:
                                onNextClick = { /* No-op in editing mode, handled by onSaveLeagues */
                                },
                                onSkipClick = { showLeagueSelection = false }, // Acts as "Cancel"
                                onBackClick = {
                                        showLeagueSelection = false
                                } // Acts as "Back" or "Cancel"
                        )
                        return@MainAppScreen // Or just 'return' if not inside a specific lambda
                        // scope that needs qualification
                }
        // --- ^^^^^ END OF NEW LEAGUE SELECTION BRANCH ^^^^^ ---
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
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        contentAlignment = Alignment.Center
                ) {
                        when (selectedTab) {
                                0 -> ExploreScreen()
                                1 -> EventsScreen()
                                2 ->
                                        CheckInLanding(
                                                onBackClick = null, // Or some default action
                                                onTapScan = { showScanner = true }
                                        )
                                3 ->
                                        ProfileScreen(
                                                onLogout = onLogout,
                                                onShowQR = {
                                                        val username =
                                                                uiState.user?.username.orEmpty()
                                                        val id = uiState.user?.id.orEmpty()
                                                        when {
                                                                uiState.isLoading ->
                                                                        scope.launch {
                                                                                snackbarHostState
                                                                                        .showSnackbar(
                                                                                                "Profile is still loading…"
                                                                                        )
                                                                        }
                                                                username.isBlank() ->
                                                                        scope.launch {
                                                                                snackbarHostState
                                                                                        .showSnackbar(
                                                                                                "Please set a username in your profile first."
                                                                                        )
                                                                        }
                                                                id.isBlank() ->
                                                                        scope.launch {
                                                                                snackbarHostState
                                                                                        .showSnackbar(
                                                                                                "Missing user ID for QR."
                                                                                        )
                                                                        }
                                                                else -> showQRCode = true
                                                        }
                                                },
                                                onShowPrivacyPolicy = { showPrivacyPolicy = true },
                                                onShowTermsConditions = {
                                                        showTermsConditions = true
                                                },
                                                onShowTeamSelection = { showTeamSelection = true },
                                                onShowLeagueSelection = {
                                                        showLeagueSelection = true
                                                } // <-- THIS TRIGGERS THE NEW BRANCH
                                        )
                        }
                }
        }
}
