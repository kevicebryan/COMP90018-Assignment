package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobilecomputingassignment.data.documents.LegalDocuments
import com.example.mobilecomputingassignment.presentation.ui.component.WatchMatesBottomNavigation
import com.example.mobilecomputingassignment.presentation.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(onLogout: () -> Unit, viewModel: AuthViewModel = hiltViewModel()) {
        var selectedTab by remember { mutableIntStateOf(0) }
        var showQRCode by remember { mutableStateOf(false) }
        var showPrivacyPolicy by remember { mutableStateOf(false) }
        var showTermsConditions by remember { mutableStateOf(false) }
        var showTeamSelection by remember { mutableStateOf(false) }

        val signupData by viewModel.signupData.collectAsState() // Not sure if this necessary?
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (showQRCode) {
                QRCodeScreen(
                        username = currentUser?.displayName ?: "user",
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
                        bottomBar = {
                                WatchMatesBottomNavigation(
                                        selectedTab = selectedTab,
                                        onTabSelected = { selectedTab = it }
                                )
                        }
                ) { innerPadding ->
                        Box(
                                modifier = Modifier.fillMaxSize().padding(innerPadding),
                                contentAlignment = Alignment.Center
                        ) {
                                when (selectedTab) {
                                        0 ->
                                                ProfileScreen(
                                                        onLogout = onLogout,
                                                        onShowQR = { showQRCode = true },
                                                        onShowPrivacyPolicy = {
                                                                showPrivacyPolicy = true
                                                        },
                                                        onShowTermsConditions = {
                                                                showTermsConditions = true
                                                        },
                                                        onShowTeamSelection = {
                                                                showTeamSelection = true
                                                        }
                                                )
                                        1 -> ExploreScreen()
                                        2 -> EventsScreen()
                                        3 -> CheckInScreen()
                                }
                        }
                }
        }
}
