// In: app/.../presentation/ui/screen/MainAppScreen.kt

package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mobilecomputingassignment.data.documents.LegalDocuments
import com.example.mobilecomputingassignment.presentation.ui.component.WatchMatesBottomNavigation
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(onLogout: () -> Unit) {
        var selectedTab by remember { mutableIntStateOf(0) }
        var showQRCode by remember { mutableStateOf(false) }
        var showPrivacyPolicy by remember { mutableStateOf(false) }
        var showTermsConditions by remember { mutableStateOf(false) }

        // 1. ADD THIS STATE VARIABLE
        var showTeamSelection by remember { mutableStateOf(false) }

        val currentUser = FirebaseAuth.getInstance().currentUser

        // 2. ADD THIS "if" BLOCK AT THE TOP
        if (showTeamSelection) {
                TeamSelectionScreen(
                        onNavigateBack = { showTeamSelection = false }
                )
        } else if (showQRCode) {
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
        } else {
                // Main screen with bottom navigation
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
                                        0 -> // Profile Screen
                                                ProfileScreen(
                                                        onLogout = onLogout,
                                                        onShowQR = { showQRCode = true },
                                                        onShowPrivacyPolicy = { showPrivacyPolicy = true },
                                                        onShowTermsConditions = { showTermsConditions = true },
                                                        // 3. PASS THE LAMBDA TO ProfileScreen
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