package com.example.mobilecomputingassignment.presentation.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight

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
    AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            iconContentColor =
                    if (isDestructive) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary,
            icon = {
                Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint =
                                if (isDestructive) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                        onClick = onConfirm,
                        colors =
                                if (isDestructive) {
                                    ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = MaterialTheme.colorScheme.onError
                                    )
                                } else {
                                    ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                ) { Text(confirmText) }
            },
            dismissButton = {
                TextButton(
                        onClick = onDismiss,
                        colors =
                                ButtonDefaults.textButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                )
                ) { Text(dismissText) }
            }
    )
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
