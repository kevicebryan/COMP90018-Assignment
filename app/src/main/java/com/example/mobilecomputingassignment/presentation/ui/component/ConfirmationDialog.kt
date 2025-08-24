package com.example.mobilecomputingassignment.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ConfirmationDialog(
        title: String,
        message: String,
        icon: ImageVector = Icons.Default.Warning,
        confirmText: String = "Confirm",
        dismissText: String = "Cancel",
        onConfirm: () -> Unit,
        onDismiss: () -> Unit,
        isDestructive: Boolean = false
) {
        Dialog(
                onDismissRequest = onDismiss,
                properties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                        usePlatformDefaultWidth = false
                )
        ) {
                // Dark overlay background
                Box(
                        modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                ) {
                        // Dialog card with shadow
                        Card(
                                modifier =
                                        Modifier.fillMaxWidth(0.85f)
                                                .shadow(
                                                        elevation = 24.dp,
                                                        shape = RoundedCornerShape(28.dp),
                                                        ambientColor =
                                                                Color.Black.copy(alpha = 0.3f),
                                                        spotColor = Color.Black.copy(alpha = 0.3f)
                                                ),
                                shape = RoundedCornerShape(28.dp),
                                colors =
                                        CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surface,
                                                contentColor = MaterialTheme.colorScheme.onSurface
                                        ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                                Column(
                                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                        // Icon
                                        Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint =
                                                        if (isDestructive)
                                                                MaterialTheme.colorScheme.error
                                                        else MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(48.dp)
                                        )

                                        // Title
                                        Text(
                                                text = title,
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.Center
                                        )

                                        // Message
                                        Text(
                                                text = message,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(horizontal = 8.dp)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Buttons
                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                                // Cancel button
                                                OutlinedButton(
                                                        onClick = onDismiss,
                                                        modifier = Modifier.weight(1f),
                                                        colors =
                                                                ButtonDefaults.outlinedButtonColors(
                                                                        contentColor =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .primary
                                                                ),
                                                        border =
                                                                androidx.compose.foundation
                                                                        .BorderStroke(
                                                                                1.dp,
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .primary
                                                                        )
                                                ) { Text(dismissText) }

                                                // Confirm button
                                                Button(
                                                        onClick = onConfirm,
                                                        modifier = Modifier.weight(1f),
                                                        colors =
                                                                if (isDestructive) {
                                                                        ButtonDefaults.buttonColors(
                                                                                containerColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .error,
                                                                                contentColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onError
                                                                        )
                                                                } else {
                                                                        ButtonDefaults.buttonColors(
                                                                                containerColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary,
                                                                                contentColor =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onPrimary
                                                                        )
                                                                }
                                                ) { Text(confirmText) }
                                        }
                                }
                        }
                }
        }
}

@Composable
fun LogoutConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
        ConfirmationDialog(
                title = "Logout",
                message =
                        "Are you sure you want to logout? You'll need to sign in again to access your account.",
                icon = Icons.Default.Warning,
                confirmText = "Logout",
                dismissText = "Cancel",
                onConfirm = onConfirm,
                onDismiss = onDismiss,
                isDestructive = true
        )
}
