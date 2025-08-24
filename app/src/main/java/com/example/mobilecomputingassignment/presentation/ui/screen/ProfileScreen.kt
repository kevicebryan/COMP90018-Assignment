package com.example.mobilecomputingassignment.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.presentation.ui.component.LogoutConfirmationDialog
import com.example.mobilecomputingassignment.presentation.ui.component.ProfileCard
import com.example.mobilecomputingassignment.presentation.ui.component.ProfileMenuItem
import com.example.mobilecomputingassignment.presentation.ui.component.ProfileMenuItemWithDrawable
import com.example.mobilecomputingassignment.presentation.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
        onLogout: () -> Unit,
        onShowQR: () -> Unit,
        onShowPrivacyPolicy: () -> Unit,
        onShowTermsConditions: () -> Unit,
        onShowTeamSelection: () -> Unit,
        modifier: Modifier = Modifier,
        profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val profileState by profileViewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LazyColumn(
            modifier = modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
            )
        }

        // Profile Card
        item { ProfileCard(user = profileState.user, isLoading = profileState.isLoading) }

        // Menu Items
        item {
            ProfileMenuItemWithDrawable(
                    iconRes = R.drawable.ic_qr,
                    title = "Show QR",
                    onClick = onShowQR
            )
        }

        item {
            ProfileMenuItemWithDrawable(
                    iconRes = R.drawable.ic_sports,
                    title = "Teams",
                    onClick = onShowTeamSelection
            )
        }

        item {
            ProfileMenuItemWithDrawable(
                    iconRes = R.drawable.ic_privacy,
                    title = "Privacy Policy",
                    onClick = onShowPrivacyPolicy
            )
        }

        item {
            ProfileMenuItemWithDrawable(
                    iconRes = R.drawable.ic_terms,
                    title = "Terms & Conditions",
                    onClick = onShowTermsConditions
            )
        }

        item { Spacer(modifier = Modifier.height(48.dp)) }

        // Logout at bottom
        item {
            ProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = "Log Out",
                    onClick = { showLogoutDialog = true },
                    isDestructive = true
            )
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        LogoutConfirmationDialog(
                onConfirm = {
                    showLogoutDialog = false
                    onLogout()
                },
                onDismiss = { showLogoutDialog = false }
        )
    }
}
